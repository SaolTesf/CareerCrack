package com.careercrack.careercrack.mappers;

import com.careercrack.careercrack.dtos.StatusHistoryResponse;
import com.careercrack.careercrack.models.ApplicationStatusHistory;
import org.mapstruct.Mapper;

@Mapper(config = CentralMapperConfig.class)
public interface StatusHistoryMapper {

    StatusHistoryResponse toResponse(ApplicationStatusHistory history);
}
