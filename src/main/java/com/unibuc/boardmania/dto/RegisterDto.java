package com.unibuc.boardmania.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {

    private String username;

    private String firstName;

    private String lastName;

    private String password;

    // Will be used later when we add email confirmation
    private String email;

}
