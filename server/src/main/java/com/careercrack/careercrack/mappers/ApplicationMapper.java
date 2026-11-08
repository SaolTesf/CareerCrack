package com.careercrack.careercrack.mappers;

import com.careercrack.careercrack.dtos.ApplicationRequest;
import com.careercrack.careercrack.dtos.ApplicationResponse;
import com.careercrack.careercrack.dtos.ApplicationSummaryResponse;
import com.careercrack.careercrack.dtos.ContactResponse;
import com.careercrack.careercrack.enums.SalaryPeriod;
import com.careercrack.careercrack.models.JobApplication;
import com.careercrack.careercrack.utils.Text;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Comparator;
import java.util.Locale;

@Mapper(config = CentralMapperConfig.class, uses = {CompanyMapper.class, ContactMapper.class, StatusHistoryMapper.class})
public interface ApplicationMapper {

    // list rows and Kanban cards
    @Mapping(target = "companyId", source = "company.id")
    @Mapping(target = "companyName", source = "company.name")
    ApplicationSummaryResponse toSummary(JobApplication application);

    // the detail page
    @Mapping(target = "companyId", source = "company.id")
    @Mapping(target = "companyName", source = "company.name")
    @Mapping(target = "importJobId", source = "importJob.id")
    // events and documents are filled in by later phases (events, then documents); until then they stay empty
    @Mapping(target = "events", ignore = true)
    @Mapping(target = "documents", ignore = true)
    ApplicationResponse toDetail(JobApplication application);

    // The owner, company, stage and position are set by the service, never taken from the request.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "importJob", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "sortOrder", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "statusHistory", ignore = true)
    @Mapping(target = "events", ignore = true)
    @Mapping(target = "activities", ignore = true)
    @Mapping(target = "contacts", ignore = true)
    @Mapping(target = "documents", ignore = true)
    JobApplication toEntity(ApplicationRequest request);

    // an edit replaces every field here, so a field sent as null is cleared
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "importJob", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "sortOrder", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "statusHistory", ignore = true)
    @Mapping(target = "events", ignore = true)
    @Mapping(target = "activities", ignore = true)
    @Mapping(target = "contacts", ignore = true)
    @Mapping(target = "documents", ignore = true)
    void updateEntity(ApplicationRequest request, @MappingTarget JobApplication application);

    // runs after toEntity and updateEntity: trim the text, store "" as no value, and fill the salary defaults
    @AfterMapping
    default void tidy(@MappingTarget JobApplication application) {
        application.setRoleTitle(Text.trimToNull(application.getRoleTitle()));
        application.setLocation(Text.trimToNull(application.getLocation()));
        application.setJobUrl(Text.trimToNull(application.getJobUrl()));
        application.setJobDescription(Text.trimToNull(application.getJobDescription()));
        application.setSource(Text.trimToNull(application.getSource()));
        application.setNotes(Text.trimToNull(application.getNotes()));

        String currency = Text.trimToNull(application.getSalaryCurrency());
        application.setSalaryCurrency(currency == null ? "USD" : currency.toUpperCase(Locale.ROOT));
        if (application.getSalaryPeriod() == null) {
            application.setSalaryPeriod(SalaryPeriod.YEARLY);
        }
    }

    // a set has no order, so show the contacts alphabetically
    @AfterMapping
    default void sortContacts(@MappingTarget ApplicationResponse response) {
        if (response.getContacts() != null) {
            response.getContacts().sort(Comparator.comparing(ContactResponse::getName, String.CASE_INSENSITIVE_ORDER));
        }
    }
}
