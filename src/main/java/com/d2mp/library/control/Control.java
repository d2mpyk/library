package com.d2mp.library.control;

import com.d2mp.library.repository.BookRepository;
import com.d2mp.library.repository.PersonRepository;
import com.d2mp.library.service.*;
import com.d2mp.library.model.*;

import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Control {
    private final String separador = "|----------------------------------------------------------------------|\n";
    private int opcion = -1;
    private Scanner teclado = new Scanner(System.in);
    private ConsumeAPI consumeApi = new ConsumeAPI();
    private final String URL_BASE = "https://gutendex.com/books/";
    private ConvierteDatos convierteDatos = new ConvierteDatos();
    private BookRepository bookRepository;
    private PersonRepository personRepository;

    public Control(BookRepository bookRepository, PersonRepository personRepository) {
        this.bookRepository = bookRepository;
        this.personRepository = personRepository;
    }

    public void showMenu(){
        var menu = separador+"|                       Bienvenidos a LiterAlura                       |\n"+separador+
                "| 1.- \uD83D\uDD0D Buscar libro por titulo \n"+
                "| 2.- \uD83D\uDCDA Listar todos los libros \n"+
                "| 3.- \uD83E\uDDD1\u200D\uD83D\uDCBB Listar todos los autores\n"+
                "| 4.- \uD83D\uDCC6 Listar autores vivos por año \n"+
                "| 5.- \uD83D\uDD0D Buscar autor por nombre \n"+
                "| 6.- \uD83C\uDF0E Listar libros por idioma\n"+
                "| 7.- \uD83D\uDCDA Listar TOP 10 libros mas descargados\n"+
                "| 8.- \uD83D\uDEA9 Estadisticas \n|\n"+
                "| 9.- \uD83D\uDEAA Salir \n"+separador+
                "| Escoge una opción: ";

        while (opcion != 9){
            System.out.print(menu);
            try{
                opcion = Integer.parseInt(System.console().readLine());
            } catch (NumberFormatException | InputMismatchException e){
                opcion = -1;
            }

            switch (opcion) {
                case 1:
                    buscarlibroPorTitulo();
                    break;
                case 2:
                    listarTodosLosLibros();
                    break;
                case 3:
                    listarTodosLosAutores();
                    break;
                case 4:
                    listarAutoresPorAnio();
                    break;
                case 5:
                    buscarAutorPorNombre();
                    break;
                case 6:
                    listarLibrosPorIdioma();
                    break;
                case 7:
                    listarTop10LibrosDescargados();
                    break;
                case 8:
                    mostrarEstadisticas();
                    break;
                case 9:
                    System.out.println(STR."\{separador}| *** Cerrando la aplicación... ***");
                    System.out.println(STR."| *** ¡Gracias por su visita, hasta luego! *** \n\{separador}");
                    teclado.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println(STR."\{separador}| *** Opción incorrecta, Por favor intente de nuevo ***\n\{separador}");
                    break;
            }
        }
    }

    private void buscarlibroPorTitulo(){
        System.out.print(STR."\{separador}|                  \uD83D\uDD0D Buscando libro por titulo \uD83D\uDD0D \n\{separador}");
        System.out.print("| Escribe el nombre del libro que deseas buscar: ");
        var titulo = teclado.nextLine();
        var json = consumeApi.getData(STR."\{URL_BASE}?search=\{titulo.replace(" ", "+")}");
        Optional<DataBook> dataAPI = convierteDatos.getData(json, DataAPI.class).resultados()
                .stream().filter(book->book.titulo().toUpperCase().contains(titulo.toUpperCase()))
                .findFirst();

        if (dataAPI.isPresent()){
            var nombre = dataAPI.get().titulo();
            String autor, traductor, tema, idioma, medio, online, imagen;
            boolean copyright;
            Optional<DataPerson> dataPerson = dataAPI.get().datosAutors().stream().findFirst();
            if (dataPerson.isPresent()){
                autor = dataPerson.get().nombre();
            } else autor = "Desconocido";
            Optional<DataPerson> dataTraductor = dataAPI.get().datosTraductors().stream().findFirst();
            if (dataTraductor.isPresent()){
                traductor = dataTraductor.get().nombre();
            } else traductor = "Desconocido";
            tema = dataAPI.get().temas().getFirst();
            if (tema.isEmpty()) tema = "Desconocido";
            idioma = dataAPI.get().datosIdiomas().getFirst();
            if (idioma.isEmpty()) idioma = "Desconocido";
            copyright = dataAPI.get().derechosDeAutor();
            medio = dataAPI.get().tipoDeMedio();
            if (medio.isEmpty()) medio = "Desconocido";
            imagen = dataAPI.get().formatos().getOrDefault("image/jpeg", "Desconocido");
            online = dataAPI.get().formatos().getOrDefault("text/html", "Desconocido");
            var descargas = dataAPI.get().numeroDeDescargas();
            var result = separador+
                    "| \uD83D\uDCD6 Titulo:      "+nombre+"\n"+
                    "| \uD83E\uDDD1\u200D\uD83D\uDCBB Autor:       "+autor+"\n"+
                    "| \uD83E\uDDD1\u200D\uD83D\uDCBB Traductor:   "+traductor+"\n"+
                    "| \u2705 Tema:        "+tema+"\n"+
                    "| \uD83C\uDF0E Idioma:      "+idioma+"\n"+
                    "| \u00A9\uFE0F Copyright:   "+copyright+"\n"+
                    "| \uD83D\uDCDD Medio:       "+medio+"\n"+
                    "| \uD83D\uDDBC\uFE0F Imagen:      "+imagen+"\n"+
                    "| \uD83D\uDD17 Version Web: "+online+"\n"+
                    "| \uD83E\uDC83  Descargas:   "+descargas+"\n"+
                    separador;
            System.out.println(result);

            // Agregando registro a la base de Datos
            // Primero Verificamos si existe, sino lo agregamos
            String name = String.valueOf(dataAPI.get().datosAutors().getFirst().nombre());
            if (personRepository.searchNombre(name)==null){
                Person person = new Person(dataAPI.get().datosAutors().getFirst());
                personRepository.save(person);
            }
            Long idBook = dataAPI.get().id();
            bookRepository.findById(idBook);
        } else {
            System.out.println(STR."\{separador}| *** No se encontró ningún registro con el titulo: \{titulo} ***\n\{separador}");
        }
    }
    private void listarTodosLosLibros(){
        System.out.println(STR."\{separador}|                \uD83D\uDCDA Imprimiendo la lista de los libros \uD83D\uDCDA\n\{separador}");
        List<Book> books = bookRepository.findAll();
        books.stream().sorted(Comparator.comparing(Book::getNumeroDeDescargas).reversed()).forEach(System.out::println);
    }
    private void listarTodosLosAutores(){
        System.out.println(STR."\{separador}|                   \uD83E\uDDD1\u200D\uD83D\uDCBB Listando todos los autores \uD83E\uDDD1\u200D\uD83D\uDCBB \n\{separador}");
        List<Person> autores = personRepository.findAll();
        autores.stream().filter(distinctByKey(Person::getNombre))
                .sorted(Comparator.comparing(Person::getNombre))
                .forEach(a ->System.out.println(separador+"| "+a+"\n"+separador));
    }
    private void listarAutoresPorAnio(){
        int anioBuscado = 0;
        final int anioActual = Calendar.getInstance().get(Calendar.YEAR);
        while(anioBuscado <= anioActual){
            System.out.print(STR."\{separador}|                \uD83D\uDCC6 Listando autores vivos por año \uD83D\uDCC6 \n\{separador}");
            System.out.print("| Indique el año que desea revisar: ");
            try{
                anioBuscado = Integer.parseInt(System.console().readLine());
                List<Person> autores = personRepository.findByNacimientoLessThanEqualAndFallecimientoGreaterThanEqual(anioBuscado, anioBuscado);
                if(!autores.isEmpty()) autores.stream().filter(distinctByKey(Person::getNombre)).forEach(a ->System.out.println(separador+"| "+a+"\n"+separador));
                else System.out.println(STR."\{separador}| *** No se encontraron autores vivos en ese año ***\n\{separador}");
                break;
            } catch (NumberFormatException | InputMismatchException e){
                System.out.println(STR."\{separador}| *** Opción incorrecta, Por favor intente de nuevo ***\n\{separador}");
                break;
            }
        }
    }
    private void buscarAutorPorNombre(){
        System.out.print(STR."\{separador}|                   \uD83D\uDD0D Listando autor por nombre \uD83D\uDD0D \n\{separador}");
        System.out.print("| Escribe el nombre del autor que deseas buscar: ");
        var nombre = teclado.nextLine();
        List<Person> autores = personRepository.findByNombreContainingIgnoreCase(nombre);
        if(!autores.isEmpty()) autores.stream().filter(distinctByKey(Person::getNombre)).forEach(a ->System.out.println(separador+"| "+a+"\n"+separador));
        else System.out.println(STR."\{separador}| *** No se encontraron autores con el nombre: \{nombre} ***\n\{separador}");
    }
    private void listarLibrosPorIdioma(){
        System.out.println(STR."\{separador}|                   \uD83C\uDF0E Listando libros por idioma \uD83C\uDF0E \n\{separador}");
        int opcionIdioma = -1;
        String codigoIdioma = "";
        String menu = "| 1.-  \uD83C\uDDE6\uD83C\uDDEA Árabe \n"+
                "| 2.-  \uD83C\uDDE9\uD83C\uDDEA Alemán \n"+
                "| 3.-  \uD83C\uDDE9\uD83C\uDDF8 Español \n"+
                "| 4.-  \uD83C\uDDFA\uD83C\uDDF8 Inglés \n"+
                "| 5.-  \uD83C\uDDEB\uD83C\uDDEE Finlandés \n"+
                "| 6.-  \uD83C\uDDEE\uD83C\uDDEA Irlandés \n"+
                "| 7.-  \uD83C\uDDEE\uD83C\uDDF1 Hebreo \n"+
                "| 8.-  \uD83C\uDDEE\uD83C\uDDF3 Hindú \n"+
                "| 9.-  \uD83C\uDDED\uD83C\uDDFA Húngaro \n"+
                "| 10.- \uD83C\uDDEE\uD83C\uDDF9 Italiano \n"+
                "| 11.- \uD83C\uDDEF\uD83C\uDDF5 Japonés \n"+
                "| 12.- \uD83C\uDDF0\uD83C\uDDF7 Coreano \n"+
                "| 13.- \uD83C\uDDF3\uD83C\uDDF4 Noruego \n"+
                "| 14.- \uD83C\uDDF5\uD83C\uDDF1 Polaco \n"+
                "| 15.- \uD83C\uDDF5\uD83C\uDDF9 Portugués \n"+
                "| 16.- \uD83C\uDDF7\uD83C\uDDF4 Rumano \n"+
                "| 17.- \uD83C\uDDF7\uD83C\uDDFA Ruso \n"+
                "| 18.- \uD83C\uDDF8\uD83C\uDDF0 Eslovaco \n"+
                "| 19.- \uD83C\uDDF8\uD83C\uDDEE Esloveno \n"+
                "| 20.- \uD83C\uDDF8\uD83C\uDDEA Sueco \n"+
                "| 21.- \uD83C\uDDFA\uD83C\uDDE6 Ucraniano \n"+
                "| 22.- \uD83C\uDDFB\uD83C\uDDF3 Vietnamita \n"+
                "| 23.- \uD83C\uDDE8\uD83C\uDDF3 Chino \n|\n"+
                "| 99.- \uD83D\uDEAA Salir\n"+
                separador+
                "| Indique el idioma que desea buscar: ";

        while (opcionIdioma != 99) {
            System.out.print(menu);
            try {
                opcionIdioma = Integer.parseInt(System.console().readLine());
            } catch (NumberFormatException | InputMismatchException e) {
                opcionIdioma = -1;
            }
            switch (opcionIdioma){
                case 1:
                    codigoIdioma = "ae";
                    break;
                case 2:
                    codigoIdioma = "de";
                    break;
                case 3:
                    codigoIdioma = "es";
                    break;
                case 4:
                    codigoIdioma = "en";
                    break;
                case 5:
                    codigoIdioma = "fi";
                    break;
                case 6:
                    codigoIdioma = "ie";
                    break;
                case 7:
                    codigoIdioma = "ir";
                    break;
                case 8:
                    codigoIdioma = "in";
                    break;
                case 9:
                    codigoIdioma = "hu";
                    break;
                case 10:
                    codigoIdioma = "it";
                    break;
                case 11:
                    codigoIdioma = "jp";
                    break;
                case 12:
                    codigoIdioma = "kr";
                    break;
                case 13:
                    codigoIdioma = "no";
                    break;
                case 14:
                    codigoIdioma = "pl";
                    break;
                case 15:
                    codigoIdioma = "pt";
                    break;
                case 16:
                    codigoIdioma = "ro";
                    break;
                case 17:
                    codigoIdioma = "ru";
                    break;
                case 18:
                    codigoIdioma = "sk";
                    break;
                case 19:
                    codigoIdioma = "si";
                    break;
                case 20:
                    codigoIdioma = "se";
                    break;
                case 21:
                    codigoIdioma = "ua";
                    break;
                case 22:
                    codigoIdioma = "vn";
                    break;
                case 23:
                    codigoIdioma = "cn";
                    break;
                case 99:
                    System.out.println(STR."\{separador}| *** Saliendo al menú principal... ***\n\{separador}");
                    teclado.close();
                    break;
            }

            List<Book> libros = bookRepository.findByIdiomas(codigoIdioma);
            if(!libros.isEmpty()) libros.forEach(System.out::println);
            else System.out.println(STR."\{separador}| *** No se encontraron libros con el idioma: \{codigoIdioma} ***\n\{separador}");
            break;
        }
    }
    private void listarTop10LibrosDescargados(){
        System.out.println(STR."\{separador}|                   \uD83D\uDCDA Listando TOP 10 Libros \uD83D\uDCDA \n\{separador}");
        List<Book> top10Books = bookRepository.top10BooksDownloads();
        if (!top10Books.isEmpty()) top10Books.forEach(System.out::println);
        else System.out.println(STR."\{separador}| *** No se encontraron libros con ese ranking ***\n\{separador}");
    }
    private void mostrarEstadisticas(){
        System.out.print(STR."\{separador}|                   \uD83D\uDEA9 Imprimiendo Estadisticas \uD83D\uDEA9 \n\{separador}");
        List<Book> libros = bookRepository.findAll();
        DoubleSummaryStatistics est = libros.stream()
                .collect(Collectors.summarizingDouble(Book::getNumeroDeDescargas));
        System.out.printf("| Libros Totales:    %s\n",est.getCount());
        System.out.printf("| Descargas Totales: %5.10s\n",est.getSum());
        System.out.printf("| Menos Descargado:  %5.10s\n",est.getMin());
        System.out.printf("| Mas Descargado:    %5.10s\n",est.getMax());
        System.out.println(separador+"\n");

    }

    public static <T> Predicate<T> distinctByKey(
            Function<? super T, ?> keyExtractor) {

        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

}
