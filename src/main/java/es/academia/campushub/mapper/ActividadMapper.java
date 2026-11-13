package es.academia.campushub.mapper;

import es.academia.campushub.controller.dto.ActividadResponse;
import es.academia.campushub.entity.Actividad;

public final class ActividadMapper {

  private ActividadMapper() {}

  public static ActividadResponse toResponse(Actividad a) {
    return new ActividadResponse(
        a.getId(),
        a.getTitulo(),
        a.getDescripcion(),
        a.getTipo().name(),
        a.getFechaPrevista().toString(),
        a.getMaxParticipantes(),
        a.getDocente().getId());
  }
}
