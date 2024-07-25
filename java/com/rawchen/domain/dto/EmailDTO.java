package com.rawchen.domain.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class EmailDTO {

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    private String email;

    // Getter and Setter
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

