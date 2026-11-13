package es.academia.campushub.service;

import es.academia.campushub.controller.dto.*;
import es.academia.campushub.entity.Contacto;
import es.academia.campushub.entity.Estudiante;
import es.academia.campushub.entity.Grupo;
import es.academia.campushub.repository.ContactoRepository;
import es.academia.campushub.repository.EstudianteRepository;
import es.academia.campushub.repository.GrupoRepository;
import es.academia.campushub.util.exception.BusinessConflictException;
import es.academia.campushub.util.exception.InvalidInputException;
import es.academia.campushub.util.exception.PersistenciaPendienteException;
import es.academia.campushub.util.exception.ResourceNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.stereotype.Service;
import es.academia.campushub.entity.Docente;
import es.academia.campushub.repository.DocenteRepository;
import es.academia.campushub.util.Validaciones;

@Service
public class CampusHubServiceImpl implements CampusHubService {
  private final GrupoRepository grupoRepository;
  private final EstudianteRepository estudianteRepository;
  private final ContactoRepository contactoRepository;
  private final DocenteRepository docenteRepository;

  public CampusHubServiceImpl(
          GrupoRepository grupoRepository,
          DocenteRepository docenteRepository,
          EstudianteRepository estudianteRepository,
          ContactoRepository contactoRepository) {
    this.grupoRepository = grupoRepository;
    this.docenteRepository = docenteRepository;
    this.estudianteRepository = estudianteRepository;
    this.contactoRepository = contactoRepository;
  }


  @Override
  public GrupoResponse crearGrupo(CreateGrupoRequest request) {
    if (request == null) {
      throw new InvalidInputException("Faltan datos del grupo");
    }

    String nombre = Validaciones.texto(request.nombre(), "nombre", 100);
    String nivel = Validaciones.texto(request.nivel(), "nivel", 50);
    Long tutorId = Validaciones.id(request.tutorDocenteId(), "tutorDocenteId");

    Docente tutor =
            docenteRepository
                    .findById(tutorId)
                    .orElseThrow(() -> new ResourceNotFoundException("No existe el docente tutor"));

    Grupo grupo = new Grupo();
    grupo.setNombre(nombre);
    grupo.setNivel(nivel);
    grupo.setTutorDocente(tutor);

    Grupo guardado = grupoRepository.save(grupo);

    return new GrupoResponse(
            guardado.getId(),
            guardado.getNombre(),
            guardado.getNivel(),
            tutor.getId());
  }

  @Override
  public DepartamentoResponse crearDepartamento(CreateDepartamentoRequest request) {
    throw new PersistenciaPendienteException("crearDepartamento");
  }

  @Override
  public EstudianteResponse crearEstudiante(CreateEstudianteRequest request) {
    if (request == null
        || request.nombre() == null
        || request.nombre().isBlank()
        || request.apellidos() == null
        || request.apellidos().isBlank()
        || request.email() == null
        || request.email().isBlank()
        || request.fechaNacimiento() == null
        || request.grupoId() == null) {
      throw new InvalidInputException("Faltan datos obligatorios del estudiante");
    }

    LocalDate fechaNacimiento;
    try {
      fechaNacimiento = LocalDate.parse(request.fechaNacimiento());
    } catch (DateTimeParseException ex) {
      throw new InvalidInputException("La fecha debe tener formato YYYY-MM-DD");
    }

    if (!fechaNacimiento.isBefore(LocalDate.now())) {
      throw new InvalidInputException("La fecha de nacimiento debe ser anterior a hoy");
    }

    Grupo grupo =
        grupoRepository
            .findById(request.grupoId())
            .orElseThrow(() -> new ResourceNotFoundException("No existe el grupo indicado"));

    String email = request.email().trim().toLowerCase();

    if (estudianteRepository.existsByEmailIgnoreCase(email)) {
      throw new BusinessConflictException("Ya existe un estudiante con ese correo");
    }

    Estudiante estudiante = new Estudiante();
    estudiante.setNombre(request.nombre().trim());
    estudiante.setApellidos(request.apellidos().trim());
    estudiante.setEmail(email);
    estudiante.setFechaNacimiento(fechaNacimiento);
    estudiante.setGrupo(grupo);

    Estudiante guardado = estudianteRepository.save(estudiante);

    return new EstudianteResponse(
        guardado.getId(),
        guardado.getNombre(),
        guardado.getApellidos(),
        guardado.getEmail(),
        guardado.getFechaNacimiento().toString(),
        grupo.getId());
  }

  @Override
  public EstudianteResponse obtenerEstudiante(Long id) {
    Estudiante estudiante =
        estudianteRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No existe el estudiante"));

    return new EstudianteResponse(
        estudiante.getId(),
        estudiante.getNombre(),
        estudiante.getApellidos(),
        estudiante.getEmail(),
        estudiante.getFechaNacimiento().toString(),
        estudiante.getGrupo().getId());
  }

  @Override
  public DocenteResponse crearDocente(CreateDocenteRequest request) {
    throw new PersistenciaPendienteException("crearDocente");
  }

  @Override
  public ActividadResponse crearActividad(CreateActividadRequest request) {
    throw new PersistenciaPendienteException("crearActividad");
  }

  @Override
  public ActividadResponse obtenerActividad(Long id) {
    throw new PersistenciaPendienteException("obtenerActividad");
  }

  @Override
  public InscripcionResponse inscribir(Long actividadId, CreateInscripcionRequest request) {
    throw new PersistenciaPendienteException("inscribir");
  }

  @Override
  public List<EstudianteResponse> participantes(Long actividadId) {
    throw new PersistenciaPendienteException("participantes");
  }

  @Override
  public SeguimientoResponse registrarSeguimiento(
      Long actividadId, CreateSeguimientoRequest request) {
    throw new PersistenciaPendienteException("registrarSeguimiento");
  }

  @Override
  public ActividadDetalleResponse detalle(Long id) {
    throw new PersistenciaPendienteException("detalle");
  }

  @Override
  public List<ActividadHistorialResponse> historial(Long estudianteId) {
    throw new PersistenciaPendienteException("historial");
  }

  @Override
  public ContactoResponse guardarContacto(Long estudianteId, UpsertContactoRequest request) {
    if (request == null
        || request.tutorLegal() == null
        || request.tutorLegal().isBlank()
        || request.telefono() == null
        || request.telefono().isBlank()
        || request.email() == null
        || request.email().isBlank()
        || request.autorizado() == null) {
      throw new InvalidInputException("Faltan datos obligatorios del contacto");
    }

    Estudiante estudiante =
        estudianteRepository
            .findById(estudianteId)
            .orElseThrow(() -> new ResourceNotFoundException("No existe el estudiante"));

    LocalDate fechaAutorizacion = null;
    if (request.fechaAutorizacion() != null && !request.fechaAutorizacion().isBlank()) {
      try {
        fechaAutorizacion = LocalDate.parse(request.fechaAutorizacion());
      } catch (DateTimeParseException ex) {
        throw new InvalidInputException("La fecha debe tener formato YYYY-MM-DD");
      }
    }

    if (request.autorizado() && fechaAutorizacion == null) {
      throw new InvalidInputException("Indica la fecha de autorización");
    }

    Contacto contacto = contactoRepository.findById(estudianteId).orElseGet(Contacto::new);

    contacto.setEstudiante(estudiante);
    contacto.setTutorLegal(request.tutorLegal().trim());
    contacto.setTelefono(request.telefono().trim());
    contacto.setEmail(request.email().trim().toLowerCase());
    contacto.setAutorizado(request.autorizado());
    contacto.setFechaAutorizacion(fechaAutorizacion);

    Contacto guardado = contactoRepository.save(contacto);

    return new ContactoResponse(
        estudiante.getId(),
        guardado.getTutorLegal(),
        guardado.getTelefono(),
        guardado.getEmail(),
        guardado.getAutorizado(),
        guardado.getFechaAutorizacion() == null
            ? null
            : guardado.getFechaAutorizacion().toString());
  }

  @Override
  public ContactoResponse contacto(Long estudianteId) {
    estudianteRepository
        .findById(estudianteId)
        .orElseThrow(() -> new ResourceNotFoundException("No existe el estudiante"));

    Contacto contacto =
        contactoRepository
            .findById(estudianteId)
            .orElseThrow(
                () -> new ResourceNotFoundException("El estudiante no tiene ficha de contacto"));

    return new ContactoResponse(
        estudianteId,
        contacto.getTutorLegal(),
        contacto.getTelefono(),
        contacto.getEmail(),
        contacto.getAutorizado(),
        contacto.getFechaAutorizacion() == null
            ? null
            : contacto.getFechaAutorizacion().toString());
  }

  @Override
  public EtiquetaResponse crearEtiqueta(CreateEtiquetaRequest request) {
    throw new PersistenciaPendienteException("crearEtiqueta");
  }

  @Override
  public void asociarEtiqueta(Long actividadId, Long etiquetaId) {
    throw new PersistenciaPendienteException("asociarEtiqueta");
  }

  @Override
  public List<ActividadResponse> actividadesPorEtiqueta(Long etiquetaId) {
    throw new PersistenciaPendienteException("actividadesPorEtiqueta");
  }
}
