package com.careercrack.careercrack.services;

import com.careercrack.careercrack.dtos.ApplicationContactsRequest;
import com.careercrack.careercrack.dtos.ApplicationRequest;
import com.careercrack.careercrack.dtos.ApplicationResponse;
import com.careercrack.careercrack.dtos.ApplicationSummaryResponse;
import com.careercrack.careercrack.dtos.BoardColumnResponse;
import com.careercrack.careercrack.dtos.ContactResponse;
import com.careercrack.careercrack.dtos.PageResponse;
import com.careercrack.careercrack.dtos.StatusHistoryResponse;
import com.careercrack.careercrack.dtos.UpdateApplicationStatusRequest;
import com.careercrack.careercrack.enums.ApplicationStatus;
import com.careercrack.careercrack.exceptions.BadRequestException;
import com.careercrack.careercrack.exceptions.ResourceNotFoundException;
import com.careercrack.careercrack.mappers.ApplicationMapper;
import com.careercrack.careercrack.mappers.ContactMapper;
import com.careercrack.careercrack.mappers.StatusHistoryMapper;
import com.careercrack.careercrack.models.Company;
import com.careercrack.careercrack.models.Contact;
import com.careercrack.careercrack.models.JobApplication;
import com.careercrack.careercrack.models.User;
import com.careercrack.careercrack.repositories.ApplicationStatusHistoryRepository;
import com.careercrack.careercrack.repositories.ContactRepository;
import com.careercrack.careercrack.repositories.JobApplicationRepository;
import com.careercrack.careercrack.repositories.JobApplicationSpecifications;
import com.careercrack.careercrack.utils.PageRequests;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ApplicationService {

    // the only fields a client may sort by (entity property names)
    private static final Set<String> SORT_FIELDS = Set.of("updatedAt", "createdAt", "roleTitle", "status", "appliedDate",
            "postingDate", "salaryMin", "salaryMax", "location", "sortOrder", "company.name");

    // stages that mean the application has been sent
    private static final Set<ApplicationStatus> SENT_STAGES =
            EnumSet.of(ApplicationStatus.APPLIED, ApplicationStatus.INTERVIEWING, ApplicationStatus.OFFER_RECEIVED);

    private final JobApplicationRepository applicationRepository;
    private final ApplicationStatusHistoryRepository statusHistoryRepository;
    private final ContactRepository contactRepository;
    private final CompanyService companyService;
    private final ApplicationMapper applicationMapper;
    private final StatusHistoryMapper statusHistoryMapper;
    private final ContactMapper contactMapper;
    private final CurrentUserService currentUserService;

    public ApplicationService(JobApplicationRepository applicationRepository,
                              ApplicationStatusHistoryRepository statusHistoryRepository,
                              ContactRepository contactRepository,
                              CompanyService companyService,
                              ApplicationMapper applicationMapper,
                              StatusHistoryMapper statusHistoryMapper,
                              ContactMapper contactMapper,
                              CurrentUserService currentUserService) {
        this.applicationRepository = applicationRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.contactRepository = contactRepository;
        this.companyService = companyService;
        this.applicationMapper = applicationMapper;
        this.statusHistoryMapper = statusHistoryMapper;
        this.contactMapper = contactMapper;
        this.currentUserService = currentUserService;
    }

    // ---- read ----

    /** Filtered, sorted, paged list. Filters left null are ignored. */
    @Transactional(readOnly = true)
    public PageResponse<ApplicationSummaryResponse> list(ApplicationStatus status, String search, Long companyId,
                                                         LocalDate appliedFrom, LocalDate appliedTo,
                                                         int page, int size, String sortBy, boolean ascending) {
        if (appliedFrom != null && appliedTo != null && appliedFrom.isAfter(appliedTo)) {
            throw new BadRequestException("appliedFrom cannot be after appliedTo");
        }
        Long userId = currentUserService.getCurrentUserId();
        Pageable pageable = PageRequests.of(page, size, sortBy, ascending, SORT_FIELDS);
        Specification<JobApplication> filter =
                JobApplicationSpecifications.filter(userId, status, search, companyId, appliedFrom, appliedTo);
        return PageResponse.from(applicationRepository.findAll(filter, pageable).map(applicationMapper::toSummary));
    }

    @Transactional(readOnly = true)
    public ApplicationResponse get(Long id) {
        return applicationMapper.toDetail(getOwnedWithDetails(currentUserService.getCurrentUserId(), id));
    }

    /** The Kanban board: every stage in pipeline order (empty ones too), each card in its saved position. */
    @Transactional(readOnly = true)
    public List<BoardColumnResponse> board() {
        Long userId = currentUserService.getCurrentUserId();
        List<JobApplication> all = applicationRepository.findAllByUser_Id(userId, Sort.by("sortOrder", "id"));

        Map<ApplicationStatus, List<ApplicationSummaryResponse>> cardsByStage = new EnumMap<>(ApplicationStatus.class);
        for (ApplicationStatus stage : ApplicationStatus.values()) {
            cardsByStage.put(stage, new ArrayList<>());
        }
        for (JobApplication application : all) {
            cardsByStage.get(application.getStatus()).add(applicationMapper.toSummary(application));
        }
        return Arrays.stream(ApplicationStatus.values())
                .map(stage -> new BoardColumnResponse(stage, cardsByStage.get(stage)))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StatusHistoryResponse> history(Long id) {
        Long userId = currentUserService.getCurrentUserId();
        if (!applicationRepository.existsByIdAndUser_Id(id, userId)) {
            throw new ResourceNotFoundException("Application not found");
        }
        return statusHistoryRepository.findTimeline(id, userId).stream().map(statusHistoryMapper::toResponse).toList();
    }

    // ---- write ----

    /**
     * Creates an application at the bottom of its stage (BOOKMARKED unless the request says otherwise).
     * A new application that is already sent gets today's date as its applied date if none was given.
     */
    @Transactional
    public ApplicationResponse create(ApplicationRequest request) {
        User user = currentUserService.getCurrentUser();
        JobApplication application = applicationMapper.toEntity(request);
        application.setUser(user);
        application.setCompany(resolveCompany(user, request));

        ApplicationStatus status = request.getStatus() != null ? request.getStatus() : ApplicationStatus.BOOKMARKED;
        application.setStatus(status);
        if (application.getAppliedDate() == null && SENT_STAGES.contains(status)) {
            application.setAppliedDate(LocalDate.now());
        }
        application.setSortOrder(applicationRepository.findMaxSortOrder(user.getId(), status) + 1);
        application.recordInitialStatus();

        return applicationMapper.toDetail(applicationRepository.save(application));
    }

    /** Replaces every editable field. A stage that differs from the current one is a move: it is recorded in the history. */
    @Transactional
    public ApplicationResponse update(Long id, ApplicationRequest request) {
        User user = currentUserService.getCurrentUser();
        JobApplication application = getOwnedWithDetails(user.getId(), id);

        applicationMapper.updateEntity(request, application);
        application.setCompany(resolveCompany(user, request));
        if (request.getStatus() != null && request.getStatus() != application.getStatus()) {
            changeStage(application, user.getId(), request.getStatus(), null);
        }
        return applicationMapper.toDetail(applicationRepository.saveAndFlush(application));
    }

    /**
     * A Kanban drop: puts the application in a stage at a position, and renumbers that column.
     * Returns the target column, so the board can refresh it.
     */
    @Transactional
    public BoardColumnResponse move(Long id, UpdateApplicationStatusRequest request) {
        Long userId = currentUserService.getCurrentUserId();
        JobApplication application = getOwned(userId, id);

        changeStage(application, userId, request.getStatus(), request.getSortOrder());
        applicationRepository.flush();
        return column(userId, request.getStatus());
    }

    /** Also deletes its history, events and notes, and its links to contacts and documents. */
    @Transactional
    public void delete(Long id) {
        applicationRepository.delete(getOwned(currentUserService.getCurrentUserId(), id));
    }

    /** Replaces the contacts linked to an application with exactly the ones listed. */
    @Transactional
    public List<ContactResponse> setContacts(Long id, ApplicationContactsRequest request) {
        Long userId = currentUserService.getCurrentUserId();
        JobApplication application = getOwnedWithDetails(userId, id);

        Set<Long> ids = request.getContactIds();
        List<Contact> contacts = ids.isEmpty() ? List.of() : contactRepository.findAllByIdInAndUser_Id(ids, userId);
        // a smaller result means at least one id does not exist or is not theirs
        if (contacts.size() != ids.size()) {
            throw new ResourceNotFoundException("Contact not found");
        }
        application.getContacts().clear();
        application.getContacts().addAll(contacts);
        applicationRepository.saveAndFlush(application);

        return contacts.stream()
                .sorted(Comparator.comparing(Contact::getName, String.CASE_INSENSITIVE_ORDER))
                .map(contactMapper::toResponse)
                .toList();
    }

    // ---- helpers ----

    private JobApplication getOwned(Long userId, Long id) {
        return applicationRepository.findByIdAndUser_Id(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
    }

    private JobApplication getOwnedWithDetails(Long userId, Long id) {
        return applicationRepository.findWithDetailsByIdAndUser_Id(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
    }

    // companyId picks one of the user's companies; otherwise companyName finds or creates one
    private Company resolveCompany(User user, ApplicationRequest request) {
        if (request.getCompanyId() != null) {
            return companyService.getOwnedEntity(user.getId(), request.getCompanyId());
        }
        return companyService.findOrCreate(user, request.getCompanyName());
    }

    /**
     * Moves an application to a stage and a position (null = bottom). A different stage is recorded in the history,
     * and the first time the application counts as sent it gets today's date as its applied date.
     */
    private void changeStage(JobApplication application, Long userId, ApplicationStatus newStatus, Integer position) {
        if (newStatus != application.getStatus()) {
            application.changeStatus(newStatus);
            if (application.getAppliedDate() == null && SENT_STAGES.contains(newStatus)) {
                application.setAppliedDate(LocalDate.now());
            }
        }
        placeInColumn(application, userId, newStatus, position);
    }

    // Puts the card at the position and renumbers the column 0, 1, 2... Only cards whose number changes are touched.
    private void placeInColumn(JobApplication moved, Long userId, ApplicationStatus status, Integer position) {
        List<JobApplication> column = new ArrayList<>(
                applicationRepository.findAllByUser_IdAndStatusOrderBySortOrderAscIdAsc(userId, status));
        column.removeIf(card -> card.getId().equals(moved.getId()));
        int index = position == null ? column.size() : Math.min(position, column.size());
        column.add(index, moved);
        for (int i = 0; i < column.size(); i++) {
            if (!Integer.valueOf(i).equals(column.get(i).getSortOrder())) {
                column.get(i).setSortOrder(i);
            }
        }
    }

    private BoardColumnResponse column(Long userId, ApplicationStatus status) {
        List<ApplicationSummaryResponse> cards = applicationRepository
                .findAllByUser_IdAndStatusOrderBySortOrderAscIdAsc(userId, status)
                .stream().map(applicationMapper::toSummary).toList();
        return new BoardColumnResponse(status, cards);
    }
}
