package com.d2mp.library.model;

import jakarta.persistence.*;

import java.util.NoSuchElementException;

@Entity
@Table(name = "books")
public class Book {
    @Id
    private Long Id;
    private String titulo;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "autor_id")
    private Person autor;
    private String traductor;
    private String temas;
    private String idiomas;
    private Boolean derechosDeAutor;
    private String tipoDeMedio;
    private String imagen;
    private String online;
    private Double numeroDeDescargas;


    /**
     * Book Default Constructor
     */
    public Book() { }

    public Book(DataBook dataBook) {
        this.Id = dataBook.id();
        this.titulo = dataBook.titulo();

        try{
            Person dataAutor = new Person(dataBook.datosAutors().getFirst());
            this.autor = dataAutor;
        } catch(NoSuchElementException e){
            this.autor = new Person("Desconocido", 0L, 0L);
        }

        try{
            Person dataTraductor = new Person(dataBook.datosTraductors().getFirst());
            this.traductor = dataTraductor.toString();
        } catch(NoSuchElementException e){
            this.traductor = "Desconocido (0000 - 0000)";
        }

        if (!dataBook.temas().getFirst().isEmpty()) this.temas = dataBook.temas().getFirst();
        else this.temas = "Desconocido";

        if (!dataBook.datosIdiomas().getFirst().isEmpty()) this.idiomas = dataBook.datosIdiomas().getFirst();
        else this.idiomas = "Desconocido";

        this.derechosDeAutor = dataBook.derechosDeAutor();
        this.tipoDeMedio = dataBook.tipoDeMedio();
        this.imagen = dataBook.formatos().getOrDefault("image/jpeg", "Desconocido");
        this.online = dataBook.formatos().getOrDefault("text/html", "Desconocido");
        this.numeroDeDescargas = dataBook.numeroDeDescargas();
    }

    public Book(Long id, String titulo, Person autor, String traductor, String temas, String datosIdiomas,
                Boolean derechosDeAutor, String tipoDeMedio, String imagen, String online, Double numeroDeDescargas) {
        Id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.traductor = traductor;
        this.temas = temas;
        this.idiomas = datosIdiomas;
        this.derechosDeAutor = derechosDeAutor;
        this.tipoDeMedio = tipoDeMedio;
        this.imagen = imagen;
        this.online = online;
        this.numeroDeDescargas = numeroDeDescargas;
    }

    @Override
    public String toString() {
        String separador = "|----------------------------------------------------------------------|\n";
        return separador+
               "| \uD83D\uDCD6 Titulo:      "+getTitulo()+"\n"+
               "| \uD83E\uDDD1\u200D\uD83D\uDCBB Autor:       "+getAutor().getNombre()+"\n"+
               "| \uD83E\uDDD1\u200D\uD83D\uDCBB Traductor:   "+getTraductor()+"\n"+
               "| \u2705 Tema:        "+getTemas()+"\n"+
               "| \uD83C\uDF0E Idioma:      "+ getIdiomas()+"\n"+
               "| \u00A9\uFE0F Copyright:   "+getDerechosDeAutor()+"\n"+
               "| \uD83D\uDCDD Medio:       "+getTipoDeMedio()+"\n"+
               "| \uD83D\uDDBC\uFE0F Imagen:      "+getImagen()+"\n"+
               "| \uD83D\uDD17 Version Web: "+getOnline()+"\n"+
               "| \uD83E\uDC83  Descargas:   "+getNumeroDeDescargas()+"\n"+
               separador;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Person getAutor() {
        return autor;
    }

    public void setAutor(Person autor) {
        this.autor = autor;
    }

    public String getTraductor() {
        return traductor;
    }

    public void setTraductor(String traductor) {
        this.traductor = traductor;
    }

    public String getTemas() {
        return temas;
    }

    public void setTemas(String temas) {
        this.temas = temas;
    }

    public String getIdiomas() {
        return idiomas;
    }

    public void setIdiomas(String idiomas) {
        this.idiomas = idiomas;
    }

    public Boolean getDerechosDeAutor() {
        return derechosDeAutor;
    }

    public void setDerechosDeAutor(Boolean derechosDeAutor) {
        this.derechosDeAutor = derechosDeAutor;
    }

    public String getTipoDeMedio() {
        return tipoDeMedio;
    }

    public void setTipoDeMedio(String tipoDeMedio) {
        this.tipoDeMedio = tipoDeMedio;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public Double getNumeroDeDescargas() {
        return numeroDeDescargas;
    }

    public void setNumeroDeDescargas(Double numeroDeDescargas) {
        this.numeroDeDescargas = numeroDeDescargas;
    }
}
