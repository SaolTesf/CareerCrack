package com.careercrack.careercrack.mappers;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.MapperConfig;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * Shared settings for the new mappers: use it with @Mapper(config = CentralMapperConfig.class).
 * A target field that no mapping fills (and that is not marked ignore) is a compile error, not a silent null,
 * so adding a field to a DTO or entity without mapping it stops the build. Existing mappers are not affected.
 */
@MapperConfig(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface CentralMapperConfig {
}
