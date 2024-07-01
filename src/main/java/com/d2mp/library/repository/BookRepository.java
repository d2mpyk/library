package com.d2mp.library.repository;

import com.d2mp.library.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByIdiomas(String idiomas);
    @Query(value = "SELECT * FROM books ORDER BY numero_de_descargas DESC LIMIT 10;", nativeQuery = true)
    List<Book> top10BooksDownloads();
}
