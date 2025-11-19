package com.politechnika_warszawska.wyszukiwarkaprac.services;

import com.politechnika_warszawska.wyszukiwarkaprac.encje_db.UzytkownikDB;
import com.politechnika_warszawska.wyszukiwarkaprac.repositories.UzytkownikDBRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;

@Service
public class UzytkownikService implements UserDetailsService {

    private final UzytkownikDBRepository repository;
    private final PasswordEncoder passwordEncoder;


    public UzytkownikService(UzytkownikDBRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;

        this.passwordEncoder = passwordEncoder;
    }


    // po zalogowaniu
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UzytkownikDB uzytkownik  = repository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
        return new User(uzytkownik.getEmail(), uzytkownik.getPassword(), new ArrayList<>());

    }

    //po zarejestrowaniu
    public UzytkownikDB registerUser(String email, String password) {
         if (repository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
         String hashedPassword = passwordEncoder.encode(password);
         UzytkownikDB uzytkownik = new UzytkownikDB();
         uzytkownik.setEmail(email);
         uzytkownik.setPassword(hashedPassword);


         return repository.save(uzytkownik);

    }
}
