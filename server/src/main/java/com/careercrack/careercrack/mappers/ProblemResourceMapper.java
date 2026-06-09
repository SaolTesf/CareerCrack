package com.careercrack.careercrack.mappers;

import com.careercrack.careercrack.dtos.ProblemResourceResponse;
import com.careercrack.careercrack.models.ProblemResource;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProblemResourceMapper {

    ProblemResourceResponse toDto(ProblemResource problemResource);
}
