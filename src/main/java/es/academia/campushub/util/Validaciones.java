package es.academia.campushub.util;

import es.academia.campushub.util.exception.InvalidInputException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Map;

public class Validaciones {
  private Validaciones() {}

  public static String texto(String valor, String campo, int max) {
    if (valor == null || valor.isBlank()) {
      throw new InvalidInputException(campo + " es obligatorio");
    }
    String limpio = valor.trim();
    if (limpio.length() > max) {
      throw new InvalidInputException(campo + " no puede superar " + max + " caracteres");
    }
    return limpio;
  }

  public static String textoLargo(String valor, String campo) {
    if (valor == null || valor.isBlank()) {
      throw new InvalidInputException(campo + " es obligatorio");
    }
    return valor.trim();
  }

  public static String email(String valor, String campo) {
    String correo = texto(valor, campo, 254).toLowerCase(Locale.ROOT);
    if (!correo.matches("^[^@\\s]+@[^@\\s]+$")) {
      throw new InvalidInputException(campo + " no tiene un formato válido");
    }
    return correo;
  }

  public static LocalDate fecha(String valor, String campo) {
    try {
      return LocalDate.parse(texto(valor, campo, 10));
    } catch (DateTimeParseException e) {
      throw new InvalidInputException(campo + " debe tener formato YYYY-MM-DD");
    }
  }

  public static LocalDate fechaOpcional(String valor, String campo) {
    return valor == null || valor.isBlank() ? null : fecha(valor, campo);
  }

  public static Long id(Long valor, String campo) {
    if (valor == null) {
      throw new InvalidInputException(campo + " es obligatorio");
    }
    return valor;
  }

  public static void clavesDocumento(Object valor, String campo) {
    if (valor instanceof Map<?, ?> mapa) {
      for (Map.Entry<?, ?> entrada : mapa.entrySet()) {
        String clave = String.valueOf(entrada.getKey());
        if (clave.isEmpty() || clave.contains(".") || clave.startsWith("$")) {
          throw new InvalidInputException(
              campo + " contiene una clave no permitida (vacía, con '.' o que empieza por '$')");
        }
        clavesDocumento(entrada.getValue(), campo);
      }
    } else if (valor instanceof Iterable<?> elementos) {
      for (Object elemento : elementos) {
        clavesDocumento(elemento, campo);
      }
    }
  }
}
