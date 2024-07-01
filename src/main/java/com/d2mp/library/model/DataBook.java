package com.d2mp.library.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DataBook(
        @JsonAlias("id") Long id,
        @JsonAlias("title") String titulo,
        @JsonAlias("authors") ArrayList<DataPerson> datosAutors,
        @JsonAlias("translators") ArrayList<DataPerson> datosTraductors,
        @JsonAlias("subjects") List<String> temas,
        @JsonAlias("languages") List<String> datosIdiomas,
        @JsonAlias("copyright") Boolean derechosDeAutor,
        @JsonAlias("media_type") String tipoDeMedio,
        @JsonAlias("formats") Map<String, String> formatos,
        @JsonAlias("download_count") Double numeroDeDescargas
) {
}
