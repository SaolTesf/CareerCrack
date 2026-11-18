package com.careercrack.careercrack.services;

import com.careercrack.careercrack.dtos.CompanyRequest;
import com.careercrack.careercrack.dtos.CompanyResponse;
import com.careercrack.careercrack.exceptions.BadRequestException;
import com.careercrack.careercrack.exceptions.ConflictException;
import com.careercrack.careercrack.exceptions.DuplicateResourceException;
import com.careercrack.careercrack.exceptions.ResourceNotFoundException;
import com.careercrack.careercrack.mappers.CompanyMapper;
import com.careercrack.careercrack.models.Company;
import com.careercrack.careercrack.models.User;
import com.careercrack.careercrack.repositories.CompanyRepository;
import com.careercrack.careercrack.repositories.JobApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private JobApplicationRepository jobApplicationRepository;
    @Mock
    private CurrentUserService currentUserService;

    private CompanyService service;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(7L);
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(currentUserService.getCurrentUserId()).thenReturn(7L);
        when(companyRepository.save(any(Company.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(companyRepository.saveAndFlush(any(Company.class))).thenAnswer(invocation -> invocation.getArgument(0));
        service = new CompanyService(companyRepository, jobApplicationRepository,
                Mappers.getMapper(CompanyMapper.class), currentUserService);
    }

    private static CompanyRequest request(String name) {
        CompanyRequest request = new CompanyRequest();
        request.setName(name);
        return request;
    }

    private static Company company(Long id, String name) {
        Company company = new Company();
        company.setId(id);
        company.setName(name);
        return company;
    }

    // ---- create ----

    @Test
    void createSavesATidiedCompanyForTheCurrentUser() {
        when(companyRepository.findForUserByName(7L, "Acme Corp")).thenReturn(Optional.empty());
        CompanyRequest request = request("  Acme Corp  ");
        request.setWebsite("   ");

        CompanyResponse response = service.create(request);

        ArgumentCaptor<Company> saved = ArgumentCaptor.forClass(Company.class);
        verify(companyRepository).save(saved.capture());
        assertThat(saved.getValue().getName()).isEqualTo("Acme Corp");
        assertThat(saved.getValue().getWebsite()).isNull();
        assertThat(saved.getValue().getUser()).isSameAs(user);
        assertThat(response.getName()).isEqualTo("Acme Corp");
    }

    @Test
    void createRefusesANameThatIsTakenIgnoringCase() {
        when(companyRepository.findForUserByName(7L, "acme corp")).thenReturn(Optional.of(company(1L, "Acme Corp")));

        assertThatThrownBy(() -> service.create(request("acme corp")))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Acme Corp");
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void createRefusesABlankName() {
        assertThatThrownBy(() -> service.create(request("   ")))
                .isInstanceOf(BadRequestException.class);
    }

    // ---- update ----

    @Test
    void updateAllowsAnEditThatKeepsTheCompanysOwnName() {
        Company existing = company(5L, "Acme");
        when(companyRepository.findByIdAndUser_Id(5L, 7L)).thenReturn(Optional.of(existing));
        when(companyRepository.findForUserByName(7L, "Acme")).thenReturn(Optional.of(existing));
        CompanyRequest request = request("Acme");
        request.setIndustry("Software");

        CompanyResponse response = service.update(5L, request);

        assertThat(response.getIndustry()).isEqualTo("Software");
        verify(companyRepository).saveAndFlush(existing);
    }

    @Test
    void updateRefusesTheNameOfADifferentCompany() {
        when(companyRepository.findByIdAndUser_Id(5L, 7L)).thenReturn(Optional.of(company(5L, "Acme")));
        when(companyRepository.findForUserByName(7L, "Globex")).thenReturn(Optional.of(company(9L, "Globex")));

        assertThatThrownBy(() -> service.update(5L, request("Globex")))
                .isInstanceOf(DuplicateResourceException.class);
        verify(companyRepository, never()).saveAndFlush(any(Company.class));
    }

    @Test
    void updateClearsFieldsThatAreSentAsNull() {
        Company existing = company(5L, "Acme");
        existing.setWebsite("https://acme.example.com");
        existing.setIndustry("Software");
        when(companyRepository.findByIdAndUser_Id(5L, 7L)).thenReturn(Optional.of(existing));
        when(companyRepository.findForUserByName(7L, "Acme")).thenReturn(Optional.of(existing));

        CompanyResponse response = service.update(5L, request("Acme"));

        assertThat(response.getWebsite()).isNull();
        assertThat(response.getIndustry()).isNull();
    }

    // ---- read ----

    @Test
    void getReturnsNotFoundForACompanyThatIsNotTheirs() {
        when(companyRepository.findByIdAndUser_Id(5L, 7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(5L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchTrimsWhatWasTyped() {
        when(companyRepository.findTop10ByUser_IdAndNameContainingIgnoreCaseOrderByNameAsc(7L, "acm"))
                .thenReturn(List.of(company(1L, "Acme")));

        List<CompanyResponse> results = service.search("  acm ");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Acme");
    }

    @Test
    void listRefusesAFieldThatIsNotSortable() {
        assertThatThrownBy(() -> service.list(0, 20, "hashedPassword", true))
                .isInstanceOf(BadRequestException.class);
    }

    // ---- delete ----

    @Test
    void deleteRefusesWhileTheCompanyStillHasApplications() {
        Company existing = company(5L, "Acme");
        when(companyRepository.findByIdAndUser_Id(5L, 7L)).thenReturn(Optional.of(existing));
        when(jobApplicationRepository.existsByCompany_Id(5L)).thenReturn(true);

        assertThatThrownBy(() -> service.delete(5L)).isInstanceOf(ConflictException.class);
        verify(companyRepository, never()).delete(any(Company.class));
    }

    @Test
    void deleteRemovesACompanyThatHasNoApplications() {
        Company existing = company(5L, "Acme");
        when(companyRepository.findByIdAndUser_Id(5L, 7L)).thenReturn(Optional.of(existing));
        when(jobApplicationRepository.existsByCompany_Id(5L)).thenReturn(false);

        service.delete(5L);

        verify(companyRepository).delete(existing);
    }

    // ---- used by other services ----

    @Test
    void findOrCreateReturnsTheCompanyTheUserAlreadyHas() {
        Company existing = company(5L, "Acme");
        when(companyRepository.findForUserByName(7L, "Acme")).thenReturn(Optional.of(existing));

        assertThat(service.findOrCreate(user, "  Acme ")).isSameAs(existing);
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void findOrCreateCreatesTheCompanyWhenItIsMissing() {
        when(companyRepository.findForUserByName(7L, "Initech")).thenReturn(Optional.empty());

        Company created = service.findOrCreate(user, " Initech ");

        assertThat(created.getName()).isEqualTo("Initech");
        assertThat(created.getUser()).isSameAs(user);
        verify(companyRepository).save(any(Company.class));
    }
}
