package com.Authentication.demo.io;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileRequest {

    @NotBlank(message = "Name should not be empty")
    private String name;

    @NotBlank(message = "Email should not be empty")  // ✅ Changed from @NotNull
    @Email(message = "Enter valid email address")
    private String email;

    @NotBlank(message = "Password should not be empty")  // ✅ Added
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}