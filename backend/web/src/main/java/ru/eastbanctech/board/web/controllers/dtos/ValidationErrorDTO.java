package ru.eastbanctech.board.web.controllers.dtos;

import java.util.Map;

/**
 * @author y.bulkin
 */
public class ValidationErrorDTO {

    private Map<String, String> errors;

    public ValidationErrorDTO(Map<String, String> errors) {

        this.errors = errors;
    }
}
