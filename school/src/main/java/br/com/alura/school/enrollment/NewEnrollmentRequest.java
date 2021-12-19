package br.com.alura.school.enrollment;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.constraints.NotBlank;

public class NewEnrollmentRequest {

    @NotBlank
    private final String username;

    @JsonCreator
    public NewEnrollmentRequest(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

}
