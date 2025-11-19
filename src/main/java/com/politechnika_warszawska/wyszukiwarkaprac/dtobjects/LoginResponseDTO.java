package com.politechnika_warszawska.wyszukiwarkaprac.dtobjects;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private String token;

    public LoginResponseDTO(String token) {
        this.token = token;
    }

}
