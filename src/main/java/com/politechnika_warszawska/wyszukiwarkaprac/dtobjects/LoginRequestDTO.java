package com.politechnika_warszawska.wyszukiwarkaprac.dtobjects;

import lombok.Data;

@Data
public class LoginRequestDTO {

    private String email;
    private String password;

    public LoginRequestDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
