package com.careercrack.careercrack.mappers;

import com.careercrack.careercrack.dtos.ProblemResponse;
import com.careercrack.careercrack.models.Problem;
import com.careercrack.careercrack.models.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProblemMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "problemCategory.name", target = "categoryName")
    ProblemResponse toDto(Problem problem);

    default List<String> mapTags(List<Tag> tags) {
        if (tags == null) {
            return new ArrayList<>();
        }
        return tags.stream().map(Tag::getName).toList();
    }

}
