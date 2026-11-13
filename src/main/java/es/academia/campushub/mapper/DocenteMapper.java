package es.academia.campushub.mapper;

import es.academia.campushub.controller.dto.DocenteResponse;
import es.academia.campushub.entity.Docente;

public final class DocenteMapper {

  private DocenteMapper() {}

  public static DocenteResponse toResponse(Docente docente) {
    return new DocenteResponse(
        docente.getId(),
        docente.getNombre(),
        docente.getApellidos(),
        docente.getEmail(),
        docente.getDepartamento().getId());
  }
}
