package com.politechnika_warszawska.wyszukiwarkaprac.controllers;

import com.politechnika_warszawska.wyszukiwarkaprac.dtobjects.OfertaListDTO;
import com.politechnika_warszawska.wyszukiwarkaprac.services.OfertaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/oferty")
public class OfertaController {

    private final OfertaService ofertaService;

    public OfertaController(OfertaService ofertaService) {
        this.ofertaService = ofertaService;
    }

    @GetMapping
    public Page<OfertaListDTO> szukajOfert(
            @RequestParam(required = false) String kodWoj,
            @RequestParam(required = false) String nazwaMiasta,
            @RequestParam(required = false) String nazwaFirmy,
            @RequestParam(required = false) Integer minWidelki,
            @RequestParam(required = false) Integer maxWidelki,
            @PageableDefault(size = 20) Pageable pageable,
            Principal principal // Może być null (niezalogowany)
    ) {
        String email = (principal != null) ? principal.getName() : null;
        return ofertaService.szukajOfert(pageable, kodWoj, nazwaMiasta, nazwaFirmy, minWidelki, maxWidelki, email);
    }

    @GetMapping("/{id}")
    public OfertaListDTO pobierzSzczegoly(@PathVariable Long id, Principal principal) {
        String email = (principal != null) ? principal.getName() : null;
        return ofertaService.pobierzSzczegolyOferty(id, email);
    }
}