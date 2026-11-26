package com.careercrack.careercrack.mappers;

import com.careercrack.careercrack.dtos.ActivityRequest;
import com.careercrack.careercrack.dtos.ActivityResponse;
import com.careercrack.careercrack.models.ApplicationActivity;
import com.careercrack.careercrack.utils.Text;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface ActivityMapper {

    @Mapping(target = "applicationId", source = "application.id")
    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "eventTitle", source = "event.title")
    ActivityResponse toResponse(ApplicationActivity activity);

    // The application and the linked event are set by the service, after checking they belong together.
    // activityType and occurredAt can arrive as null: the service fills the defaults on create and keeps the old values on edit.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "application", ignore = true)
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ApplicationActivity toEntity(ActivityRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "application", ignore = true)
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(ActivityRequest request, @MappingTarget ApplicationActivity activity);

    @AfterMapping
    default void tidy(@MappingTarget ApplicationActivity activity) {
        activity.setTitle(Text.trimToNull(activity.getTitle()));
        activity.setQuestionsAsked(Text.trimToNull(activity.getQuestionsAsked()));
        activity.setNotes(Text.trimToNull(activity.getNotes()));
    }
}
