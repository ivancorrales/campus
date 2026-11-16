package es.academia.campushub.mapper;

import es.academia.campushub.controller.dto.EstudianteResponse;
import es.academia.campushub.entity.Estudiante;

public final class EstudianteMapper {

  private EstudianteMapper() {}

  public static EstudianteResponse toResponse(Estudiante e) {
    return new EstudianteResponse(
        e.getId(),
        e.getNombre(),
        e.getApellidos(),
        e.getEmail(),
        e.getFechaNacimiento().toString(),
        e.getGrupo().getId());
  }
}
