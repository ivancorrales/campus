package es.academia.campushub.mapper;

import es.academia.campushub.controller.dto.EtiquetaResponse;
import es.academia.campushub.entity.Etiqueta;

public final class EtiquetaMapper {

  private EtiquetaMapper() {}

  public static EtiquetaResponse toResponse(Etiqueta etiqueta) {
    return new EtiquetaResponse(etiqueta.getId(), etiqueta.getNombre());
  }
}
