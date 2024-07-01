package com.d2mp.library.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "persons")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String nombre;
    private Long nacimiento;
    private Long fallecimiento;
    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Book> books;

    public Person() {
    }

    public Person(String nombre, Long nacimiento, Long fallecimiento) {
        this.nombre = nombre;
        this.nacimiento = nacimiento;
        this.fallecimiento = fallecimiento;
    }

    public Person(DataPerson dataPerson){
        this.nombre = dataPerson.nombre();
        this.nacimiento = Long.valueOf(dataPerson.nacimiento());
        this.fallecimiento = Long.valueOf(dataPerson.fallecimiento());
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getNacimiento() {
        return nacimiento;
    }

    public void setNacimiento(Long nacimiento) {
        this.nacimiento = nacimiento;
    }

    public Long getFallecimiento() {
        return fallecimiento;
    }

    public void setFallecimiento(Long fallecimiento) {
        this.fallecimiento = fallecimiento;
    }

    @Override
    public String toString() {
        return  nombre + " (" + nacimiento + " - " + fallecimiento + " )";

    }
}
