package com.careercrack.careercrack.mappers;

import com.careercrack.careercrack.dtos.ProblemResourceResponse;
import com.careercrack.careercrack.models.ProblemResource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProblemResourceMapper {

    @Mapping(source = "problem.id", target = "problemId")
    ProblemResourceResponse toDto(ProblemResource problemResource);
}
