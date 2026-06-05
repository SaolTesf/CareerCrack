package com.careercrack.careercrack.mappers;

import com.careercrack.careercrack.dtos.TagResponse;
import com.careercrack.careercrack.models.Tag;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TagMapper {
    List<TagResponse> toDto(List<Tag> tags);
}
