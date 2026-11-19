package com.careercrack.careercrack.mappers;

import com.careercrack.careercrack.dtos.EventRequest;
import com.careercrack.careercrack.dtos.EventResponse;
import com.careercrack.careercrack.models.ApplicationEvent;
import com.careercrack.careercrack.utils.Text;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface EventMapper {

    // the application's role title and company name come along, so a calendar can show where each event belongs
    @Mapping(target = "applicationId", source = "application.id")
    @Mapping(target = "roleTitle", source = "application.roleTitle")
    @Mapping(target = "companyName", source = "application.company.name")
    EventResponse toResponse(ApplicationEvent event);

    // The application is set by the service after checking it belongs to the user.
    // reminderSentAt is only ever set by the reminder job (and cleared by the service when an event is rescheduled).
    // "completed" is not copied when the request leaves it out: false for a new event, unchanged on an edit.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "application", ignore = true)
    @Mapping(target = "reminderSentAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ApplicationEvent toEntity(EventRequest request);

    // an edit replaces every other field, so a field sent as null is cleared
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "application", ignore = true)
    @Mapping(target = "reminderSentAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(EventRequest request, @MappingTarget ApplicationEvent event);

    @AfterMapping
    default void tidy(@MappingTarget ApplicationEvent event) {
        event.setTitle(Text.trimToNull(event.getTitle()));
        event.setLocation(Text.trimToNull(event.getLocation()));
        event.setNotes(Text.trimToNull(event.getNotes()));
    }
}
