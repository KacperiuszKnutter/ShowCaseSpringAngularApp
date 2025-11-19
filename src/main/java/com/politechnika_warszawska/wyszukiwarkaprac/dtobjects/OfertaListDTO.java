package com.politechnika_warszawska.wyszukiwarkaprac.dtobjects;

import lombok.Data;

import java.util.List;

@Data
public class OfertaListDTO {

    private Long id;
    private String nazwaStanowiska;
    private Integer widelkiMax;
    private Integer widelkiMin;
    private String nazwaFirmy;
    private String nazwaMiasta;

    private String krotkiOpis;
    private List<String> technologie;
    private boolean isLiked;

}
