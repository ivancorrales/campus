package es.academia.campushub.entity;

import es.academia.campushub.util.exception.InvalidInputException;

public enum TipoActividad {
  TALLER,
  CHARLA,
  VISITA,
  JORNADA;

  public static TipoActividad desde(String texto) {
    if (texto == null || texto.isBlank()) {
      throw new InvalidInputException("El tipo de actividad es obligatorio");
    }
    try {
      return TipoActividad.valueOf(texto.trim().toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new InvalidInputException(
          "Tipo de actividad no válido. Valores: TALLER, CHARLA, VISITA, JORNADA");
    }
  }
}
