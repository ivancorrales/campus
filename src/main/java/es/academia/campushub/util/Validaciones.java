package es.academia.campushub.util;

import es.academia.campushub.util.excepcion.InvalidInputException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public final class Validaciones {

    private Validaciones() {}

    public static String texto(String valor, String campo, int max) {
        if (valor == null || valor.isBlank()) {
            throw new InvalidInputException("El campo " + campo + " es obligatorio");
        }
        String limpio = valor.trim();
        if (limpio.length() > max) {
            throw new InvalidInputException(
                    "El campo " + campo + " no puede superar " + max + " caracteres");
        }
        return limpio;
    }

    public static String textoOpcional(String valor, String campo, int max) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return texto(valor, campo, max);
    }

    public static String email(String valor, String campo) {
        String limpio = texto(valor, campo, 255);
        if (!limpio.contains("@")) {
            throw new InvalidInputException("El campo " + campo + " no es un correo válido");
        }
        return limpio;
    }

    public static String emailOpcional(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return email(valor, campo);
    }

    public static LocalDate fecha(String valor, String campo) {
        String limpio = texto(valor, campo, 10);
        try {
            return LocalDate.parse(limpio);
        } catch (DateTimeParseException e) {
            throw new InvalidInputException("El campo " + campo + " debe tener formato AAAA-MM-DD");
        }
    }

    public static LocalDate fechaOpcional(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return fecha(valor, campo);
    }

    public static Long id(Long valor, String campo) {
        if (valor == null) {
            throw new InvalidInputException("El campo " + campo + " es obligatorio");
        }
        return valor;
    }
}