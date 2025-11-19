package com.politechnika_warszawska.wyszukiwarkaprac.controllers;

import com.politechnika_warszawska.wyszukiwarkaprac.dtobjects.OfertaListDTO;
import com.politechnika_warszawska.wyszukiwarkaprac.services.OfertaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/ulubione")
public class UlubioneController {

    private final OfertaService ofertaService;

    public UlubioneController(OfertaService ofertaService) {
        this.ofertaService = ofertaService;
    }

    @GetMapping
    public Page<OfertaListDTO> pobierzUlubione(@PageableDefault(size = 20) Pageable pageable, Principal principal) {
        return ofertaService.pobierzUlubione(principal.getName(), pageable);
    }

    @PostMapping("/{id}/toggle")
    public ResponseEntity<Boolean> przelaczUlubione(@PathVariable Long id, Principal principal) {
        boolean isLikedNow = ofertaService.zmienStatusUlubionej(id, principal.getName());
        return ResponseEntity.ok(isLikedNow);
    }
}