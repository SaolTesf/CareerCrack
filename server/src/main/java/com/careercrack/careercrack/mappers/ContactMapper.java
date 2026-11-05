package com.careercrack.careercrack.mappers;

import com.careercrack.careercrack.dtos.ContactRequest;
import com.careercrack.careercrack.dtos.ContactResponse;
import com.careercrack.careercrack.models.Contact;
import com.careercrack.careercrack.utils.Text;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface ContactMapper {

    @Mapping(target = "companyId", source = "company.id")
    @Mapping(target = "companyName", source = "company.name")
    ContactResponse toResponse(Contact contact);

    // the owner and the company are set by the service, after checking they belong to the logged-in user
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Contact toEntity(ContactRequest request);

    // an edit replaces every field, so a field sent as null is cleared
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(ContactRequest request, @MappingTarget Contact contact);

    @AfterMapping
    default void tidy(@MappingTarget Contact contact) {
        contact.setName(Text.trimToNull(contact.getName()));
        contact.setContactRole(Text.trimToNull(contact.getContactRole()));
        contact.setEmail(Text.trimToNull(contact.getEmail()));
        contact.setPhone(Text.trimToNull(contact.getPhone()));
        contact.setLinkedinUrl(Text.trimToNull(contact.getLinkedinUrl()));
        contact.setNotes(Text.trimToNull(contact.getNotes()));
    }
}
