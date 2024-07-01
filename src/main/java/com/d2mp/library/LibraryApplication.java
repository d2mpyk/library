package com.d2mp.library;

import com.d2mp.library.control.Control;
import com.d2mp.library.repository.BookRepository;
import com.d2mp.library.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibraryApplication implements CommandLineRunner {

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private PersonRepository personRepository;

	public static void main(String[] args) {
		SpringApplication.run(LibraryApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Control control = new Control(bookRepository, personRepository);
		control.showMenu();
	}
}
