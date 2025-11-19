package com.politechnika_warszawska.wyszukiwarkaprac.controllers;

import com.politechnika_warszawska.wyszukiwarkaprac.config.JwtUtil;
import com.politechnika_warszawska.wyszukiwarkaprac.dtobjects.LoginRequestDTO;
import com.politechnika_warszawska.wyszukiwarkaprac.dtobjects.LoginResponseDTO;
import com.politechnika_warszawska.wyszukiwarkaprac.services.UzytkownikService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
//TODO
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UzytkownikService  uzytkownikService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(UzytkownikService uzytkownikService, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.uzytkownikService = uzytkownikService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/rejestracja")
    public LoginResponseDTO register(@RequestBody LoginRequestDTO loginRequestDTO) {
        uzytkownikService.registerUser(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());

        final UserDetails ud = uzytkownikService.loadUserByUsername(loginRequestDTO.getEmail());
        final String token = jwtUtil.generateToken(ud);
        return new LoginResponseDTO(token);
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO loginRequestDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword())
        );
        final UserDetails ud = uzytkownikService.loadUserByUsername(loginRequestDTO.getEmail());
        final String token = jwtUtil.generateToken(ud);
        return new LoginResponseDTO(token);
    }
}
