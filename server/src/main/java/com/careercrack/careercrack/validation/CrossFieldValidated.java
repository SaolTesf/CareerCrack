package com.careercrack.careercrack.validation;

import java.util.Map;

/** Implemented by request DTOs that have rules involving more than one field. See @ValidCrossFields. */
public interface CrossFieldValidated {

    /** Field name to message, one entry per broken rule. Return an empty map when the request is valid. */
    Map<String, String> crossFieldErrors();
}
