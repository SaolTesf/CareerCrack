package com.careercrack.careercrack.services;

import com.careercrack.careercrack.dtos.ContactRequest;
import com.careercrack.careercrack.dtos.ContactResponse;
import com.careercrack.careercrack.exceptions.ResourceNotFoundException;
import com.careercrack.careercrack.mappers.ContactMapper;
import com.careercrack.careercrack.models.Company;
import com.careercrack.careercrack.models.Contact;
import com.careercrack.careercrack.models.User;
import com.careercrack.careercrack.repositories.ContactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ContactServiceTest {

    @Mock
    private ContactRepository contactRepository;
    @Mock
    private CompanyService companyService;
    @Mock
    private CurrentUserService currentUserService;

    private ContactService service;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(7L);
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(currentUserService.getCurrentUserId()).thenReturn(7L);
        when(contactRepository.save(any(Contact.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(contactRepository.saveAndFlush(any(Contact.class))).thenAnswer(invocation -> invocation.getArgument(0));
        service = new ContactService(contactRepository, Mappers.getMapper(ContactMapper.class),
                companyService, currentUserService);
    }

    private static ContactRequest request(Long companyId, String name) {
        ContactRequest request = new ContactRequest();
        request.setCompanyId(companyId);
        request.setName(name);
        return request;
    }

    private static Company company(Long id, String name) {
        Company company = new Company();
        company.setId(id);
        company.setName(name);
        return company;
    }

    private static Contact contact(Long id, Company company, String name) {
        Contact contact = new Contact();
        contact.setId(id);
        contact.setCompany(company);
        contact.setName(name);
        return contact;
    }

    @Test
    void createLinksTheContactToTheUsersCompany() {
        Company acme = company(3L, "Acme");
        when(companyService.getOwnedEntity(7L, 3L)).thenReturn(acme);
        ContactRequest request = request(3L, "  Dana Lee ");
        request.setEmail("dana@acme.example.com");

        ContactResponse response = service.create(request);

        ArgumentCaptor<Contact> saved = ArgumentCaptor.forClass(Contact.class);
        verify(contactRepository).save(saved.capture());
        assertThat(saved.getValue().getUser()).isSameAs(user);
        assertThat(saved.getValue().getCompany()).isSameAs(acme);
        assertThat(saved.getValue().getName()).isEqualTo("Dana Lee");
        assertThat(response.getCompanyId()).isEqualTo(3L);
        assertThat(response.getCompanyName()).isEqualTo("Acme");
    }

    @Test
    void createRefusesACompanyThatIsNotTheirs() {
        when(companyService.getOwnedEntity(7L, 3L)).thenThrow(new ResourceNotFoundException("Company not found"));

        assertThatThrownBy(() -> service.create(request(3L, "Dana Lee")))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(contactRepository, never()).save(any(Contact.class));
    }

    @Test
    void updateMovesTheContactToAnotherCompanyTheUserOwns() {
        Company acme = company(3L, "Acme");
        Company globex = company(4L, "Globex");
        Contact dana = contact(11L, acme, "Dana Lee");
        when(contactRepository.findByIdAndUser_Id(11L, 7L)).thenReturn(Optional.of(dana));
        when(companyService.getOwnedEntity(7L, 4L)).thenReturn(globex);

        ContactResponse response = service.update(11L, request(4L, "Dana Lee"));

        assertThat(dana.getCompany()).isSameAs(globex);
        assertThat(response.getCompanyName()).isEqualTo("Globex");
    }

    @Test
    void updateDoesNotLookUpTheCompanyWhenItIsUnchanged() {
        Contact dana = contact(11L, company(3L, "Acme"), "Dana Lee");
        when(contactRepository.findByIdAndUser_Id(11L, 7L)).thenReturn(Optional.of(dana));

        service.update(11L, request(3L, "Dana L."));

        assertThat(dana.getName()).isEqualTo("Dana L.");
        verify(companyService, never()).getOwnedEntity(anyLong(), anyLong());
    }

    @Test
    void getReturnsNotFoundForSomeoneElsesContact() {
        when(contactRepository.findByIdAndUser_Id(11L, 7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(11L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void listForCompanyChecksOwnershipBeforeListing() {
        when(companyService.getOwnedEntity(7L, 3L)).thenThrow(new ResourceNotFoundException("Company not found"));

        assertThatThrownBy(() -> service.listForCompany(3L)).isInstanceOf(ResourceNotFoundException.class);
        verify(contactRepository, never()).findAllByUser_IdAndCompany_IdOrderByNameAsc(anyLong(), anyLong());
    }

    @Test
    void deleteRemovesTheContact() {
        Contact dana = contact(11L, company(3L, "Acme"), "Dana Lee");
        when(contactRepository.findByIdAndUser_Id(11L, 7L)).thenReturn(Optional.of(dana));

        service.delete(11L);

        verify(contactRepository).delete(dana);
    }
}
