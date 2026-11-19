package es.academia.campushub.mapper;

import es.academia.campushub.controller.dto.*;
import es.academia.campushub.document.Seguimiento;
import es.academia.campushub.entity.*;

public final class Mapper {
  private Mapper() {}

  public static DepartamentoResponse toResponse(Departamento d) {
    return new DepartamentoResponse(d.getId(), d.getNombre());
  }

  public static DocenteResponse toResponse(Docente d) {
    return new DocenteResponse(
        d.getId(), d.getNombre(), d.getApellidos(), d.getEmail(), d.getDepartamento().getId());
  }

  public static GrupoResponse toResponse(Grupo g) {
    return new GrupoResponse(g.getId(), g.getNombre(), g.getNivel(), g.getTutor().getId());
  }

  public static EstudianteResponse toResponse(Estudiante e) {
    return new EstudianteResponse(
        e.getId(),
        e.getNombre(),
        e.getApellidos(),
        e.getEmail(),
        e.getFechaNacimiento().toString(),
        e.getGrupo().getId());
  }

  public static ActividadResponse toResponse(Actividad a) {
    return new ActividadResponse(
        a.getId(),
        a.getTitulo(),
        a.getDescripcion(),
        a.getTipo(),
        a.getFechaPrevista().toString(),
        a.getMaxParticipantes(),
        a.getDocente().getId());
  }

  public static ContactoResponse toResponse(ContactoEstudiante c) {
    return new ContactoResponse(
        c.getId(),
        c.getTutorLegal(),
        c.getTelefono(),
        c.getEmail(),
        c.isAutorizado(),
        c.getFechaAutorizacion() == null ? null : c.getFechaAutorizacion().toString());
  }

  public static EtiquetaResponse toResponse(Etiqueta e) {
    return new EtiquetaResponse(e.getId(), e.getNombre());
  }

  public static InscripcionResponse toResponse(Inscripcion i) {
    return new InscripcionResponse(
        i.getId(),
        i.getActividad().getId(),
        i.getEstudiante().getId(),
        i.getFechaInscripcion().toString());
  }

  public static ActividadHistorialResponse toHistorial(Inscripcion i) {
    Actividad a = i.getActividad();
    return new ActividadHistorialResponse(
        a.getId(),
        a.getTitulo(),
        a.getTipo(),
        a.getFechaPrevista().toString(),
        i.getFechaInscripcion().toString(),
        toResponse(a.getDocente()));
  }

  public static SeguimientoResponse toResponse(Seguimiento s) {
    return new SeguimientoResponse(
        s.getId(), s.getActividadId(), s.getTipo(), s.getDatos(), s.getCreadoEn().toString());
  }
}
