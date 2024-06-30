package com.d2mp.library.control;

import com.d2mp.library.service.*;
import com.d2mp.library.model.*;

import java.util.InputMismatchException;
import java.util.Optional;
import java.util.Scanner;

public class Control {
    private final String separador = "|----------------------------------------------------------------------|\n";
    private Scanner teclado = new Scanner(System.in);
    private ConsumeAPI consumeApi = new ConsumeAPI();
    private final String URL_BASE = "https://gutendex.com/books/";
    private ConvierteDatos convierteDatos = new ConvierteDatos();


    public void showMenu(){
        int opcion = -1;
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
            String autor, traductor, idioma, medio, online, imagen;
            Optional<DataPerson> dataPerson = dataAPI.get().datosAutors().stream().findFirst();
            if (dataPerson.isPresent()){
                autor = dataPerson.get().nombre();
            } else autor = "Desconocido";
            Optional<DataPerson> dataTraductor = dataAPI.get().datosTraductors().stream().findFirst();
            if (dataTraductor.isPresent()){
                traductor = dataTraductor.get().nombre();
            } else traductor = "Desconocido";
            idioma = dataAPI.get().datosIdiomas().getFirst();
            medio = dataAPI.get().tipoDeMedio();
            imagen = dataAPI.get().formatos().getOrDefault("image/jpeg", "Desconocido");
            online = dataAPI.get().formatos().getOrDefault("text/html", "Desconocido");
            var descargas = dataAPI.get().numeroDeDescargas();
            var result = separador+
                    "| \uD83D\uDCD6 Titulo:      "+nombre+"\n"+
                    "| \uD83E\uDDD1\u200D\uD83D\uDCBB Autor:       "+autor+"\n"+
                    "| \uD83E\uDDD1\u200D\uD83D\uDCBB Traductor:   "+traductor+"\n"+
                    "| \uD83C\uDF0E Idioma:      "+idioma+"\n"+
                    "| \uD83D\uDCDD Medio:       "+medio+"\n"+
                    "| \uD83D\uDDBC\uFE0F Imagen:      "+imagen+"\n"+
                    "| \uD83D\uDD17 Version Web: "+online+"\n"+
                    "| \uD83E\uDC83  Descargas:   "+descargas+"\n"+
                    separador;
            System.out.println(result);

            // Agregando registro a la base de Datos

        } else {
            System.out.println(STR."\{separador}| *** No se encontró ningún registro con el titulo: \{titulo} ***\n\{separador}");
        }
    }
    private void listarTodosLosLibros(){
        System.out.println(STR."\{separador}|                \uD83D\uDCDA Imprimiendo la lista de los libros \uD83D\uDCDA\n\{separador}");
    }
    private void listarTodosLosAutores(){
        System.out.println(STR."\{separador}|                   \uD83E\uDDD1\u200D\uD83D\uDCBB Listando todos los autores \uD83E\uDDD1\u200D\uD83D\uDCBB \n\{separador}");
    }
    private void listarAutoresPorAnio(){
        System.out.println(STR."\{separador}|                   \uD83D\uDCC6 Listando autores por año \uD83D\uDCC6 \n\{separador}");
    }
    private void buscarAutorPorNombre(){
        System.out.println(STR."\{separador}|                   \uD83D\uDD0D Listando autor por nombre \uD83D\uDD0D \n\{separador}");
    }
    private void listarLibrosPorIdioma(){
        System.out.println(STR."\{separador}|                   \uD83C\uDF0E Listando libros por idioma \uD83C\uDF0E \n\{separador}");
    }
    private void listarTop10LibrosDescargados(){
        System.out.println(STR."\{separador}|                   \uD83D\uDCDA Listando TOP 10 Libros \uD83D\uDCDA \n\{separador}");
    }
    private void mostrarEstadisticas(){
        System.out.println(STR."\{separador}|                   \uD83D\uDEA9 Imprimiendo Estadisticas \uD83D\uDEA9 \n\{separador}");
    }
}
