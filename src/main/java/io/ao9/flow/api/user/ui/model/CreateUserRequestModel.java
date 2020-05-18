package io.ao9.flow.api.user.ui.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequestModel {

    @NotNull(message = "Please fill in the first name.")
    private String firstName;

    @NotNull(message = "Please fill in the last name.")
    private String lastName;

    @NotNull(message = "Please fill in the email.")
    @Email
    private String email;

    @NotNull(message = "Please fill in the password.")
    @Size(min = 6, max = 16, message = "Password must be at least 6 characters and at most 16 characters.")
    private String password;
    
}