package com.d2mp.library.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DataPerson(
        @JsonAlias("name") String nombre,
        @JsonAlias("birth_year") String anoDeNacimiento,
        @JsonAlias("death_year") String anoDeFallecimiento
) {
}
