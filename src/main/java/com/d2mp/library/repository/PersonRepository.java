package com.d2mp.library.repository;

import com.d2mp.library.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Long> {
    List<Person> findByNacimientoLessThanEqualAndFallecimientoGreaterThanEqual(int nacimiento, int fallecimiento);
    @Query(value = "SELECT * FROM persons WHERE nombre=:nombre LIMIT 1", nativeQuery = true)
    String searchNombre(String nombre);
    List<Person> findByNombreContainingIgnoreCase(String nombre);
}
