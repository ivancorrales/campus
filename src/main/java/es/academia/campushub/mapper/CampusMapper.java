package es.academia.campushub.mapper;

import es.academia.campushub.controller.dto.ActividadHistorialResponse;
import es.academia.campushub.controller.dto.ActividadResponse;
import es.academia.campushub.controller.dto.ContactoResponse;
import es.academia.campushub.controller.dto.DepartamentoResponse;
import es.academia.campushub.controller.dto.DocenteResponse;
import es.academia.campushub.controller.dto.EstudianteResponse;
import es.academia.campushub.controller.dto.EtiquetaResponse;
import es.academia.campushub.controller.dto.GrupoResponse;
import es.academia.campushub.controller.dto.InscripcionResponse;
import es.academia.campushub.controller.dto.SeguimientoResponse;
import es.academia.campushub.document.Seguimiento;
import es.academia.campushub.entity.Actividad;
import es.academia.campushub.entity.ContactoEstudiante;
import es.academia.campushub.entity.Departamento;
import es.academia.campushub.entity.Docente;
import es.academia.campushub.entity.Estudiante;
import es.academia.campushub.entity.Etiqueta;
import es.academia.campushub.entity.Grupo;
import es.academia.campushub.entity.Inscripcion;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.stereotype.Component;

/**
 * Conversión manual entre entidades/documentos y DTO. Los DTO no se modifican: el contrato REST
 * queda intacto. Las colecciones se transforman con streams y nunca se devuelven {@code null}.
 */
@Component
public class CampusMapper {

    public DepartamentoResponse aDepartamento(Departamento d) {
        return new DepartamentoResponse(d.getId(), d.getNombre());
    }

    public DocenteResponse aDocente(Docente d) {
        return new DocenteResponse(
                d.getId(), d.getNombre(), d.getApellidos(), d.getEmail(), d.getDepartamento().getId());
    }

    public GrupoResponse aGrupo(Grupo g) {
        return new GrupoResponse(g.getId(), g.getNombre(), g.getNivel(), g.getTutor().getId());
    }

    public EstudianteResponse aEstudiante(Estudiante e) {
        return new EstudianteResponse(
                e.getId(),
                e.getNombre(),
                e.getApellidos(),
                e.getEmail(),
                e.getFechaNacimiento().toString(),
                e.getGrupo().getId());
    }

    public ActividadResponse aActividad(Actividad a) {
        return new ActividadResponse(
                a.getId(),
                a.getTitulo(),
                a.getDescripcion(),
                a.getTipo(),
                a.getFechaPrevista().toString(),
                a.getMaxParticipantes(),
                a.getDocente().getId());
    }

    public EtiquetaResponse aEtiqueta(Etiqueta e) {
        return new EtiquetaResponse(e.getId(), e.getNombre());
    }

    public InscripcionResponse aInscripcion(Inscripcion i) {
        return new InscripcionResponse(
                i.getId(),
                i.getActividad().getId(),
                i.getEstudiante().getId(),
                i.getFechaInscripcion().toString());
    }

    public ContactoResponse aContacto(ContactoEstudiante c) {
        return new ContactoResponse(
                c.getEstudiante().getId(),
                c.getTutorLegal(),
                c.getTelefono(),
                c.getEmail(),
                c.isAutorizado(),
                Optional.ofNullable(c.getFechaAutorizacion()).map(LocalDate::toString).orElse(null));
    }

    public SeguimientoResponse aSeguimiento(Seguimiento s) {
        return new SeguimientoResponse(
                s.getId(), s.getActividadId(), s.getTipo(), s.getDatos(), s.getCreadoEn().toString());
    }

    /** Entrada del historial de un estudiante a partir de su inscripción. */
    public ActividadHistorialResponse aHistorial(Inscripcion i) {
        Actividad a = i.getActividad();
        return new ActividadHistorialResponse(
                a.getId(),
                a.getTitulo(),
                a.getTipo(),
                a.getFechaPrevista().toString(),
                i.getFechaInscripcion().toString(),
                aDocente(a.getDocente()));
    }

    /** Convierte una colección con streams; el resultado es una lista inmutable y nunca nula. */
    public <T, R> List<R> aLista(Collection<T> origen, Function<T, R> conversor) {
        return Optional.ofNullable(origen).orElseGet(List::of).stream().map(conversor).toList();
    }
}
