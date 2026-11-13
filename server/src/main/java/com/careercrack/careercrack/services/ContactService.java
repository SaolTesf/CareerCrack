package com.careercrack.careercrack.services;

import com.careercrack.careercrack.dtos.ContactRequest;
import com.careercrack.careercrack.dtos.ContactResponse;
import com.careercrack.careercrack.dtos.PageResponse;
import com.careercrack.careercrack.exceptions.ResourceNotFoundException;
import com.careercrack.careercrack.mappers.ContactMapper;
import com.careercrack.careercrack.models.Company;
import com.careercrack.careercrack.models.Contact;
import com.careercrack.careercrack.models.User;
import com.careercrack.careercrack.repositories.ContactRepository;
import com.careercrack.careercrack.utils.PageRequests;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class ContactService {

    private static final Set<String> SORT_FIELDS = Set.of("name", "contactRole", "email", "company.name", "createdAt", "updatedAt");

    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;
    private final CompanyService companyService;
    private final CurrentUserService currentUserService;

    public ContactService(ContactRepository contactRepository,
                          ContactMapper contactMapper,
                          CompanyService companyService,
                          CurrentUserService currentUserService) {
        this.contactRepository = contactRepository;
        this.contactMapper = contactMapper;
        this.companyService = companyService;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public PageResponse<ContactResponse> list(int page, int size, String sortBy, boolean ascending) {
        Long userId = currentUserService.getCurrentUserId();
        Pageable pageable = PageRequests.of(page, size, sortBy, ascending, SORT_FIELDS);
        return PageResponse.from(contactRepository.findAllByUser_Id(userId, pageable).map(contactMapper::toResponse));
    }

    @Transactional(readOnly = true)
    public List<ContactResponse> listForCompany(Long companyId) {
        Long userId = currentUserService.getCurrentUserId();
        // a 404 for someone else's company, not an empty list
        companyService.getOwnedEntity(userId, companyId);
        return contactRepository.findAllByUser_IdAndCompany_IdOrderByNameAsc(userId, companyId)
                .stream().map(contactMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ContactResponse get(Long id) {
        return contactMapper.toResponse(getOwned(currentUserService.getCurrentUserId(), id));
    }

    @Transactional
    public ContactResponse create(ContactRequest request) {
        User user = currentUserService.getCurrentUser();
        Company company = companyService.getOwnedEntity(user.getId(), request.getCompanyId());
        Contact contact = contactMapper.toEntity(request);
        contact.setUser(user);
        contact.setCompany(company);
        return contactMapper.toResponse(contactRepository.save(contact));
    }

    @Transactional
    public ContactResponse update(Long id, ContactRequest request) {
        Long userId = currentUserService.getCurrentUserId();
        Contact contact = getOwned(userId, id);
        // moving a contact to another company is allowed, but only to one the user owns
        if (!contact.getCompany().getId().equals(request.getCompanyId())) {
            contact.setCompany(companyService.getOwnedEntity(userId, request.getCompanyId()));
        }
        contactMapper.updateEntity(request, contact);
        return contactMapper.toResponse(contactRepository.saveAndFlush(contact));
    }

    /** Also removes the contact from any application it was linked to (the database does this). */
    @Transactional
    public void delete(Long id) {
        contactRepository.delete(getOwned(currentUserService.getCurrentUserId(), id));
    }

    private Contact getOwned(Long userId, Long contactId) {
        return contactRepository.findByIdAndUser_Id(contactId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found"));
    }
}
