package com.careercrack.careercrack.services;

import com.careercrack.careercrack.dtos.CompanyRequest;
import com.careercrack.careercrack.dtos.CompanyResponse;
import com.careercrack.careercrack.dtos.PageResponse;
import com.careercrack.careercrack.exceptions.BadRequestException;
import com.careercrack.careercrack.exceptions.ConflictException;
import com.careercrack.careercrack.exceptions.DuplicateResourceException;
import com.careercrack.careercrack.exceptions.ResourceNotFoundException;
import com.careercrack.careercrack.mappers.CompanyMapper;
import com.careercrack.careercrack.models.Company;
import com.careercrack.careercrack.models.User;
import com.careercrack.careercrack.repositories.CompanyRepository;
import com.careercrack.careercrack.repositories.JobApplicationRepository;
import com.careercrack.careercrack.utils.PageRequests;
import com.careercrack.careercrack.utils.Text;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class CompanyService {

    private static final Set<String> SORT_FIELDS = Set.of("name", "industry", "location", "createdAt", "updatedAt");

    private final CompanyRepository companyRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final CompanyMapper companyMapper;
    private final CurrentUserService currentUserService;

    public CompanyService(
            CompanyRepository companyRepository,
            JobApplicationRepository jobApplicationRepository,
            CompanyMapper companyMapper,
            CurrentUserService currentUserService) {
        this.companyRepository = companyRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.companyMapper = companyMapper;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public PageResponse<CompanyResponse> list(int page, int size, String sortBy, boolean ascending) {
        Long userId = currentUserService.getCurrentUserId();
        Pageable pageable = PageRequests.of(page, size, sortBy, ascending, SORT_FIELDS);
        return PageResponse.from(companyRepository.findAllByUser_Id(userId, pageable).map(companyMapper::toResponse));
    }

    /** Autocomplete for the company field: up to ten matches, or the first ten alphabetically when nothing is typed. */
    @Transactional(readOnly = true)
    public List<CompanyResponse> search(String query) {
        Long userId = currentUserService.getCurrentUserId();
        String fragment = Text.trimToNull(query);
        List<Company> matches = fragment == null
                ? companyRepository.findAllByUser_Id(userId, PageRequest.of(0, 10, Sort.by("name"))).getContent()
                : companyRepository.findTop10ByUser_IdAndNameContainingIgnoreCaseOrderByNameAsc(userId, fragment);
        return matches.stream().map(companyMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CompanyResponse get(Long id) {
        return companyMapper.toResponse(getOwnedEntity(currentUserService.getCurrentUserId(), id));
    }

    @Transactional
    public CompanyResponse create(CompanyRequest request) {
        User user = currentUserService.getCurrentUser();
        ensureNameIsFree(user.getId(), requireName(request.getName()), null);
        Company company = companyMapper.toEntity(request);
        company.setUser(user);
        return companyMapper.toResponse(companyRepository.save(company));
    }

    @Transactional
    public CompanyResponse update(Long id, CompanyRequest request) {
        Long userId = currentUserService.getCurrentUserId();
        Company company = getOwnedEntity(userId, id);
        ensureNameIsFree(userId, requireName(request.getName()), id);
        companyMapper.updateEntity(request, company);
        // flush now so the response carries the new updatedAt
        return companyMapper.toResponse(companyRepository.saveAndFlush(company));
    }

    @Transactional
    public void delete(Long id) {
        Company company = getOwnedEntity(currentUserService.getCurrentUserId(), id);
        if (jobApplicationRepository.existsByCompany_Id(id)) {
            throw new ConflictException("This company still has applications. Delete or move them first.");
        }
        // its contacts are deleted with it
        companyRepository.delete(company);
    }

    // ---- used by other services ----

    /** The user's company with this id, or a 404 (also when it belongs to someone else). */
    @Transactional(readOnly = true)
    public Company getOwnedEntity(Long userId, Long companyId) {
        return companyRepository.findByIdAndUser_Id(companyId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
    }

    /**
     * The user's company with this name (ignoring case), created if it does not exist yet.
     * Used when an application or an import names a company instead of picking one.
     */
    @Transactional
    public Company findOrCreate(User user, String rawName) {
        String name = requireName(rawName);
        return companyRepository.findForUserByName(user.getId(), name).orElseGet(() -> {
            Company created = new Company();
            created.setUser(user);
            created.setName(name);
            return companyRepository.save(created);
        });
    }

    // ---- helpers ----

    private static String requireName(String rawName) {
        String name = Text.trimToNull(rawName);
        if (name == null) {
            throw new BadRequestException("Company name is required");
        }
        return name;
    }

    // excludeId is the company being edited, so keeping its own name is not a duplicate
    private void ensureNameIsFree(Long userId, String name, Long excludeId) {
        companyRepository.findForUserByName(userId, name)
                .filter(existing -> !existing.getId().equals(excludeId))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("You already have a company named '" + existing.getName() + "'");
                });
    }
}
