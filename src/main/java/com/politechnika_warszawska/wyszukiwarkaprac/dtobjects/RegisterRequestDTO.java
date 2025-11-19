package com.politechnika_warszawska.wyszukiwarkaprac.dtobjects;

import lombok.Data;

@Data
public class RegisterRequestDTO {
    private String email;
    private String password;

    public RegisterRequestDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
