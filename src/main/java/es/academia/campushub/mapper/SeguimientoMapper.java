package es.academia.campushub.mapper;

import es.academia.campushub.controller.dto.SeguimientoResponse;
import es.academia.campushub.document.SeguimientoDocument;

public final class SeguimientoMapper {

  private SeguimientoMapper() {}

  public static SeguimientoResponse toResponse(SeguimientoDocument s) {
    return new SeguimientoResponse(
        s.getId(), s.getActividadId(), s.getTipo(), s.getDatos(), s.getCreadoEn().toString());
  }
}
