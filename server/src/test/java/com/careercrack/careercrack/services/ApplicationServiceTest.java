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
import com.careercrack.careercrack.models.ApplicationStatusHistory;
import com.careercrack.careercrack.models.Company;
import com.careercrack.careercrack.models.Contact;
import com.careercrack.careercrack.models.JobApplication;
import com.careercrack.careercrack.models.User;
import com.careercrack.careercrack.repositories.ApplicationStatusHistoryRepository;
import com.careercrack.careercrack.repositories.ContactRepository;
import com.careercrack.careercrack.repositories.JobApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The mappers are mocked here so these tests check the service's own rules: stages, history, positions, ownership.
 * ApplicationMapperTest checks the mapping itself.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ApplicationServiceTest {

    private static final ApplicationStatus BOOKMARKED = ApplicationStatus.BOOKMARKED;
    private static final ApplicationStatus APPLIED = ApplicationStatus.APPLIED;

    @Mock
    private JobApplicationRepository applicationRepository;
    @Mock
    private ApplicationStatusHistoryRepository statusHistoryRepository;
    @Mock
    private ContactRepository contactRepository;
    @Mock
    private CompanyService companyService;
    @Mock
    private ApplicationMapper applicationMapper;
    @Mock
    private StatusHistoryMapper statusHistoryMapper;
    @Mock
    private ContactMapper contactMapper;
    @Mock
    private CurrentUserService currentUserService;

    private ApplicationService service;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(7L);
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(currentUserService.getCurrentUserId()).thenReturn(7L);

        when(applicationRepository.save(any(JobApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(applicationRepository.saveAndFlush(any(JobApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(applicationRepository.findMaxSortOrder(anyLong(), any(ApplicationStatus.class))).thenReturn(-1);

        when(applicationMapper.toEntity(any(ApplicationRequest.class))).thenAnswer(invocation -> new JobApplication());
        when(applicationMapper.toDetail(any(JobApplication.class))).thenAnswer(invocation -> detail(invocation.getArgument(0)));
        when(applicationMapper.toSummary(any(JobApplication.class))).thenAnswer(invocation -> summary(invocation.getArgument(0)));
        when(statusHistoryMapper.toResponse(any(ApplicationStatusHistory.class))).thenAnswer(invocation -> {
            ApplicationStatusHistory history = invocation.getArgument(0);
            StatusHistoryResponse historyResponse = new StatusHistoryResponse();
            historyResponse.setToStatus(history.getToStatus());
            return historyResponse;
        });
        when(contactMapper.toResponse(any(Contact.class))).thenAnswer(invocation -> {
            Contact contact = invocation.getArgument(0);
            ContactResponse contactResponse = new ContactResponse();
            contactResponse.setId(contact.getId());
            contactResponse.setName(contact.getName());
            return contactResponse;
        });

        service = new ApplicationService(applicationRepository, statusHistoryRepository, contactRepository,
                companyService, applicationMapper, statusHistoryMapper, contactMapper, currentUserService);
    }

    // ---- helpers ----

    private static ApplicationResponse detail(JobApplication application) {
        ApplicationResponse response = new ApplicationResponse();
        response.setId(application.getId());
        response.setStatus(application.getStatus());
        response.setSortOrder(application.getSortOrder());
        response.setAppliedDate(application.getAppliedDate());
        return response;
    }

    private static ApplicationSummaryResponse summary(JobApplication application) {
        ApplicationSummaryResponse response = new ApplicationSummaryResponse();
        response.setId(application.getId());
        response.setStatus(application.getStatus());
        response.setSortOrder(application.getSortOrder());
        return response;
    }

    private static JobApplication application(Long id, ApplicationStatus status, int sortOrder) {
        JobApplication application = new JobApplication();
        application.setId(id);
        application.setStatus(status);
        application.setSortOrder(sortOrder);
        return application;
    }

    private static Company company(Long id, String name) {
        Company company = new Company();
        company.setId(id);
        company.setName(name);
        return company;
    }

    private static Contact contact(Long id, String name) {
        Contact contact = new Contact();
        contact.setId(id);
        contact.setName(name);
        return contact;
    }

    private static ApplicationRequest request(Long companyId, String companyName) {
        ApplicationRequest request = new ApplicationRequest();
        request.setRoleTitle("Backend Engineer");
        request.setCompanyId(companyId);
        request.setCompanyName(companyName);
        return request;
    }

    private static UpdateApplicationStatusRequest drop(ApplicationStatus status, Integer position) {
        UpdateApplicationStatusRequest request = new UpdateApplicationStatusRequest();
        request.setStatus(status);
        request.setSortOrder(position);
        return request;
    }

    private JobApplication createAndCapture(ApplicationRequest request) {
        service.create(request);
        ArgumentCaptor<JobApplication> saved = ArgumentCaptor.forClass(JobApplication.class);
        verify(applicationRepository).save(saved.capture());
        return saved.getValue();
    }

    private void ownsApplication(JobApplication application) {
        when(applicationRepository.findByIdAndUser_Id(application.getId(), 7L)).thenReturn(Optional.of(application));
        when(applicationRepository.findWithDetailsByIdAndUser_Id(application.getId(), 7L)).thenReturn(Optional.of(application));
    }

    private void columnContains(ApplicationStatus status, JobApplication... cards) {
        when(applicationRepository.findAllByUser_IdAndStatusOrderBySortOrderAscIdAsc(7L, status)).thenReturn(List.of(cards));
    }

    // ---- create ----

    @Test
    void createWithACompanyIdUsesThatCompanyAndStartsAtTheBottomOfBookmarked() {
        Company acme = company(3L, "Acme");
        when(companyService.getOwnedEntity(7L, 3L)).thenReturn(acme);
        when(applicationRepository.findMaxSortOrder(7L, BOOKMARKED)).thenReturn(4);

        JobApplication saved = createAndCapture(request(3L, null));

        assertThat(saved.getUser()).isSameAs(user);
        assertThat(saved.getCompany()).isSameAs(acme);
        assertThat(saved.getStatus()).isEqualTo(BOOKMARKED);
        assertThat(saved.getSortOrder()).isEqualTo(5);
        assertThat(saved.getAppliedDate()).isNull();
        assertThat(saved.getStatusHistory()).hasSize(1);
        ApplicationStatusHistory first = saved.getStatusHistory().iterator().next();
        assertThat(first.getFromStatus()).isNull();
        assertThat(first.getToStatus()).isEqualTo(BOOKMARKED);
    }

    @Test
    void createWithACompanyNameFindsOrCreatesTheCompany() {
        Company initech = company(4L, "Initech");
        when(companyService.findOrCreate(user, "Initech")).thenReturn(initech);

        JobApplication saved = createAndCapture(request(null, "Initech"));

        assertThat(saved.getCompany()).isSameAs(initech);
        verify(companyService, never()).getOwnedEntity(anyLong(), anyLong());
    }

    @Test
    void createRefusesACompanyThatIsNotTheirs() {
        when(companyService.getOwnedEntity(7L, 3L)).thenThrow(new ResourceNotFoundException("Company not found"));

        assertThatThrownBy(() -> service.create(request(3L, null))).isInstanceOf(ResourceNotFoundException.class);
        verify(applicationRepository, never()).save(any(JobApplication.class));
    }

    @Test
    void createAsAppliedSetsTodayAsTheAppliedDateAndRecordsThatStage() {
        when(companyService.getOwnedEntity(7L, 3L)).thenReturn(company(3L, "Acme"));
        ApplicationRequest request = request(3L, null);
        request.setStatus(APPLIED);
        LocalDate before = LocalDate.now();

        JobApplication saved = createAndCapture(request);

        assertThat(saved.getAppliedDate()).isBetween(before, LocalDate.now());
        assertThat(saved.getStatusHistory().iterator().next().getToStatus()).isEqualTo(APPLIED);
    }

    @Test
    void createKeepsAnAppliedDateTheUserGave() {
        when(companyService.getOwnedEntity(7L, 3L)).thenReturn(company(3L, "Acme"));
        ApplicationRequest request = request(3L, null);
        request.setStatus(APPLIED);
        JobApplication preset = new JobApplication();
        preset.setAppliedDate(LocalDate.of(2026, 9, 1));
        when(applicationMapper.toEntity(request)).thenReturn(preset);

        JobApplication saved = createAndCapture(request);

        assertThat(saved.getAppliedDate()).isEqualTo(LocalDate.of(2026, 9, 1));
    }

    @Test
    void createAsRejectedLeavesTheAppliedDateEmpty() {
        when(companyService.getOwnedEntity(7L, 3L)).thenReturn(company(3L, "Acme"));
        ApplicationRequest request = request(3L, null);
        request.setStatus(ApplicationStatus.REJECTED);

        assertThat(createAndCapture(request).getAppliedDate()).isNull();
    }

    // ---- update ----

    @Test
    void updateThatChangesTheStageRecordsItAndSetsTheAppliedDate() {
        JobApplication existing = application(5L, BOOKMARKED, 0);
        ownsApplication(existing);
        when(companyService.getOwnedEntity(7L, 3L)).thenReturn(company(3L, "Acme"));
        columnContains(APPLIED);
        ApplicationRequest request = request(3L, null);
        request.setStatus(APPLIED);

        ApplicationResponse response = service.update(5L, request);

        assertThat(existing.getStatus()).isEqualTo(APPLIED);
        assertThat(existing.getAppliedDate()).isNotNull();
        assertThat(existing.getSortOrder()).isEqualTo(0);
        assertThat(existing.getStatusHistory()).hasSize(1);
        ApplicationStatusHistory row = existing.getStatusHistory().iterator().next();
        assertThat(row.getFromStatus()).isEqualTo(BOOKMARKED);
        assertThat(row.getToStatus()).isEqualTo(APPLIED);
        assertThat(response.getStatus()).isEqualTo(APPLIED);
    }

    @Test
    void updateWithTheSameStageAddsNoHistoryAndDoesNotTouchTheColumn() {
        JobApplication existing = application(5L, APPLIED, 2);
        ownsApplication(existing);
        when(companyService.getOwnedEntity(7L, 3L)).thenReturn(company(3L, "Acme"));
        ApplicationRequest request = request(3L, null);
        request.setStatus(APPLIED);

        service.update(5L, request);

        assertThat(existing.getStatusHistory()).isEmpty();
        assertThat(existing.getSortOrder()).isEqualTo(2);
        verify(applicationRepository, never())
                .findAllByUser_IdAndStatusOrderBySortOrderAscIdAsc(anyLong(), any(ApplicationStatus.class));
    }

    @Test
    void updateWithoutAStageLeavesTheStageAlone() {
        JobApplication existing = application(5L, APPLIED, 0);
        ownsApplication(existing);
        when(companyService.getOwnedEntity(7L, 3L)).thenReturn(company(3L, "Acme"));

        service.update(5L, request(3L, null));

        assertThat(existing.getStatus()).isEqualTo(APPLIED);
        assertThat(existing.getStatusHistory()).isEmpty();
    }

    @Test
    void updateRefusesSomeoneElsesApplication() {
        when(applicationRepository.findWithDetailsByIdAndUser_Id(5L, 7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(5L, request(3L, null))).isInstanceOf(ResourceNotFoundException.class);
        verify(applicationRepository, never()).saveAndFlush(any(JobApplication.class));
    }

    // ---- Kanban moves ----

    @Test
    void moveWithinTheSameColumnReorders() {
        JobApplication a = application(1L, APPLIED, 0);
        JobApplication b = application(2L, APPLIED, 1);
        JobApplication c = application(3L, APPLIED, 2);
        ownsApplication(c);
        columnContains(APPLIED, a, b, c);

        BoardColumnResponse column = service.move(3L, drop(APPLIED, 0));

        assertThat(c.getSortOrder()).isEqualTo(0);
        assertThat(a.getSortOrder()).isEqualTo(1);
        assertThat(b.getSortOrder()).isEqualTo(2);
        assertThat(c.getStatusHistory()).isEmpty();
        assertThat(column.getStatus()).isEqualTo(APPLIED);
        assertThat(column.getCount()).isEqualTo(3);
    }

    @Test
    void moveToAnotherColumnRecordsTheStageAndPlacesTheCard() {
        JobApplication moved = application(9L, BOOKMARKED, 0);
        JobApplication a = application(1L, APPLIED, 0);
        JobApplication b = application(2L, APPLIED, 1);
        ownsApplication(moved);
        columnContains(APPLIED, a, b);

        service.move(9L, drop(APPLIED, 1));

        assertThat(moved.getStatus()).isEqualTo(APPLIED);
        assertThat(a.getSortOrder()).isEqualTo(0);
        assertThat(moved.getSortOrder()).isEqualTo(1);
        assertThat(b.getSortOrder()).isEqualTo(2);
        assertThat(moved.getStatusHistory()).hasSize(1);
        assertThat(moved.getStatusHistory().iterator().next().getFromStatus()).isEqualTo(BOOKMARKED);
        assertThat(moved.getAppliedDate()).isNotNull();
    }

    @Test
    void moveWithNoPositionPutsTheCardAtTheBottom() {
        JobApplication moved = application(9L, BOOKMARKED, 0);
        JobApplication a = application(1L, APPLIED, 0);
        JobApplication b = application(2L, APPLIED, 1);
        ownsApplication(moved);
        columnContains(APPLIED, a, b);

        service.move(9L, drop(APPLIED, null));

        assertThat(moved.getSortOrder()).isEqualTo(2);
    }

    @Test
    void moveWithAPositionPastTheEndStopsAtTheBottom() {
        JobApplication moved = application(9L, BOOKMARKED, 0);
        JobApplication a = application(1L, APPLIED, 0);
        ownsApplication(moved);
        columnContains(APPLIED, a);

        service.move(9L, drop(APPLIED, 99));

        assertThat(a.getSortOrder()).isEqualTo(0);
        assertThat(moved.getSortOrder()).isEqualTo(1);
    }

    @Test
    void moveRefusesSomeoneElsesApplication() {
        when(applicationRepository.findByIdAndUser_Id(9L, 7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.move(9L, drop(APPLIED, 0))).isInstanceOf(ResourceNotFoundException.class);
    }

    // ---- board, list, history, delete ----

    @Test
    void boardHasEveryStageInOrderWithItsCards() {
        when(applicationRepository.findAllByUser_Id(eq(7L), any(Sort.class))).thenReturn(List.of(
                application(1L, APPLIED, 0), application(2L, BOOKMARKED, 0), application(3L, APPLIED, 1)));

        List<BoardColumnResponse> board = service.board();

        assertThat(board).extracting(BoardColumnResponse::getStatus).containsExactly(ApplicationStatus.values());
        assertThat(board.stream().filter(column -> column.getStatus() == APPLIED).findFirst().orElseThrow().getCount()).isEqualTo(2);
        assertThat(board.stream().filter(column -> column.getStatus() == BOOKMARKED).findFirst().orElseThrow().getCount()).isEqualTo(1);
        assertThat(board.stream().filter(column -> column.getStatus() == ApplicationStatus.REJECTED).findFirst().orElseThrow().getCount()).isEqualTo(0);
    }

    @Test
    void listReturnsAPageOfSummaries() {
        when(applicationRepository.findAll(ArgumentMatchers.<Specification<JobApplication>>any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(application(1L, APPLIED, 0))));

        PageResponse<ApplicationSummaryResponse> page =
                service.list(null, null, null, null, null, 0, 20, "updatedAt", false);

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    void listRefusesAFieldThatIsNotSortable() {
        assertThatThrownBy(() -> service.list(null, null, null, null, null, 0, 20, "hashedPassword", true))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void listRefusesAnInvertedDateRange() {
        assertThatThrownBy(() -> service.list(null, null, null,
                LocalDate.of(2026, 10, 2), LocalDate.of(2026, 10, 1), 0, 20, "updatedAt", false))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void getRefusesSomeoneElsesApplication() {
        when(applicationRepository.findWithDetailsByIdAndUser_Id(5L, 7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(5L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void historyReturnsTheTimeline() {
        JobApplication existing = application(5L, APPLIED, 0);
        when(applicationRepository.existsByIdAndUser_Id(5L, 7L)).thenReturn(true);
        when(statusHistoryRepository.findTimeline(5L, 7L)).thenReturn(List.of(
                new ApplicationStatusHistory(existing, null, BOOKMARKED),
                new ApplicationStatusHistory(existing, BOOKMARKED, APPLIED)));

        List<StatusHistoryResponse> history = service.history(5L);

        assertThat(history).extracting(StatusHistoryResponse::getToStatus).containsExactly(BOOKMARKED, APPLIED);
    }

    @Test
    void historyRefusesSomeoneElsesApplication() {
        when(applicationRepository.existsByIdAndUser_Id(5L, 7L)).thenReturn(false);

        assertThatThrownBy(() -> service.history(5L)).isInstanceOf(ResourceNotFoundException.class);
        verify(statusHistoryRepository, never()).findTimeline(anyLong(), anyLong());
    }

    @Test
    void deleteRemovesTheApplication() {
        JobApplication existing = application(5L, APPLIED, 0);
        ownsApplication(existing);

        service.delete(5L);

        verify(applicationRepository).delete(existing);
    }

    @Test
    void deleteRefusesSomeoneElsesApplication() {
        when(applicationRepository.findByIdAndUser_Id(5L, 7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(5L)).isInstanceOf(ResourceNotFoundException.class);
        verify(applicationRepository, never()).delete(any(JobApplication.class));
    }

    // ---- contacts ----

    @Test
    void setContactsReplacesTheLinkedContactsAndReturnsThemAlphabetically() {
        Contact old = contact(1L, "Old Contact");
        Contact zed = contact(2L, "zed");
        Contact anna = contact(3L, "Anna");
        JobApplication existing = application(5L, APPLIED, 0);
        existing.getContacts().add(old);
        ownsApplication(existing);
        when(contactRepository.findAllByIdInAndUser_Id(Set.of(2L, 3L), 7L)).thenReturn(List.of(zed, anna));
        ApplicationContactsRequest request = new ApplicationContactsRequest();
        request.setContactIds(Set.of(2L, 3L));

        List<ContactResponse> result = service.setContacts(5L, request);

        assertThat(existing.getContacts()).containsExactlyInAnyOrder(zed, anna);
        assertThat(result).extracting(ContactResponse::getName).containsExactly("Anna", "zed");
    }

    @Test
    void setContactsRefusesAContactThatIsNotTheirsAndChangesNothing() {
        Contact old = contact(1L, "Old Contact");
        JobApplication existing = application(5L, APPLIED, 0);
        existing.getContacts().add(old);
        ownsApplication(existing);
        // two were asked for, only one belongs to this user
        when(contactRepository.findAllByIdInAndUser_Id(Set.of(2L, 3L), 7L)).thenReturn(List.of(contact(2L, "zed")));
        ApplicationContactsRequest request = new ApplicationContactsRequest();
        request.setContactIds(Set.of(2L, 3L));

        assertThatThrownBy(() -> service.setContacts(5L, request)).isInstanceOf(ResourceNotFoundException.class);

        assertThat(existing.getContacts()).containsExactly(old);
        verify(applicationRepository, never()).saveAndFlush(any(JobApplication.class));
    }

    @Test
    void setContactsWithAnEmptyListUnlinksEverythingWithoutAQuery() {
        JobApplication existing = application(5L, APPLIED, 0);
        existing.getContacts().add(contact(1L, "Old Contact"));
        ownsApplication(existing);
        ApplicationContactsRequest request = new ApplicationContactsRequest();
        request.setContactIds(Set.of());

        List<ContactResponse> result = service.setContacts(5L, request);

        assertThat(existing.getContacts()).isEmpty();
        assertThat(result).isEmpty();
        verify(contactRepository, never()).findAllByIdInAndUser_Id(anyCollection(), anyLong());
    }
}