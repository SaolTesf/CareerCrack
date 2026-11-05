package com.careercrack.careercrack.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Map;

public class CrossFieldValidator implements ConstraintValidator<ValidCrossFields, CrossFieldValidated> {

    @Override
    public boolean isValid(CrossFieldValidated dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }
        Map<String, String> errors = dto.crossFieldErrors();
        if (errors.isEmpty()) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        errors.forEach((field, message) -> context
                .buildConstraintViolationWithTemplate(literal(message))
                .addPropertyNode(field)
                .addConstraintViolation());
        return false;
    }

    // messages are templates, so escape the characters that would be read as {parameters} or ${expressions}
    private static String literal(String message) {
        return message.replace("\\", "\\\\").replace("{", "\\{").replace("}", "\\}").replace("$", "\\$");
    }
}
