package com.careercrack.careercrack.mappers;

import com.careercrack.careercrack.dtos.CompanyRequest;
import com.careercrack.careercrack.dtos.CompanyResponse;
import com.careercrack.careercrack.models.Company;
import com.careercrack.careercrack.utils.Text;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface CompanyMapper {

    CompanyResponse toResponse(Company company);

    // the owner is set by the service from the login, never from the request
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "contacts", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Company toEntity(CompanyRequest request);

    // an edit replaces every field, so a field sent as null is cleared
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "contacts", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(CompanyRequest request, @MappingTarget Company company);

    // runs after toEntity and updateEntity: trim the text, and store "" as no value
    @AfterMapping
    default void tidy(@MappingTarget Company company) {
        company.setName(Text.trimToNull(company.getName()));
        company.setWebsite(Text.trimToNull(company.getWebsite()));
        company.setIndustry(Text.trimToNull(company.getIndustry()));
        company.setLocation(Text.trimToNull(company.getLocation()));
        company.setNotes(Text.trimToNull(company.getNotes()));
    }
}
