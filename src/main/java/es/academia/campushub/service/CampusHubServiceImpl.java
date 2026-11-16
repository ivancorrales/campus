package es.academia.campushub.service;

import es.academia.campushub.controller.dto.*;
import es.academia.campushub.document.SeguimientoDocument;
import es.academia.campushub.entity.Actividad;
import es.academia.campushub.entity.Contacto;
import es.academia.campushub.entity.Departamento;
import es.academia.campushub.entity.Docente;
import es.academia.campushub.entity.Estudiante;
import es.academia.campushub.entity.Etiqueta;
import es.academia.campushub.entity.Grupo;
import es.academia.campushub.entity.Inscripcion;
import es.academia.campushub.entity.TipoActividad;
import es.academia.campushub.mapper.ActividadMapper;
import es.academia.campushub.mapper.DocenteMapper;
import es.academia.campushub.mapper.EstudianteMapper;
import es.academia.campushub.mapper.EtiquetaMapper;
import es.academia.campushub.mapper.SeguimientoMapper;
import es.academia.campushub.repository.ActividadRepository;
import es.academia.campushub.repository.ContactoRepository;
import es.academia.campushub.repository.DepartamentoRepository;
import es.academia.campushub.repository.DocenteRepository;
import es.academia.campushub.repository.EstudianteRepository;
import es.academia.campushub.repository.EtiquetaRepository;
import es.academia.campushub.repository.GrupoRepository;
import es.academia.campushub.repository.InscripcionRepository;
import es.academia.campushub.repository.SeguimientoRepository;
import es.academia.campushub.util.Validaciones;
import es.academia.campushub.util.exception.BusinessConflictException;
import es.academia.campushub.util.exception.InvalidInputException;
import es.academia.campushub.util.exception.ResourceNotFoundException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Casos de uso de CampusHub. Cada método es una transacción: si algo falla, PostgreSQL deshace todo
 * lo que ese método hubiera cambiado. MongoDB no participa en la transacción.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CampusHubServiceImpl implements CampusHubService {

  private final GrupoRepository grupoRepository;
  private final DocenteRepository docenteRepository;
  private final EstudianteRepository estudianteRepository;
  private final ContactoRepository contactoRepository;
  private final DepartamentoRepository departamentoRepository;
  private final ActividadRepository actividadRepository;
  private final InscripcionRepository inscripcionRepository;
  private final EtiquetaRepository etiquetaRepository;
  private final SeguimientoRepository seguimientoRepository;

  // ---------------------------------------------------------------- RF01 y RF02

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
        guardado.getId(), guardado.getNombre(), guardado.getNivel(), tutor.getId());
  }

  @Override
  public DepartamentoResponse crearDepartamento(CreateDepartamentoRequest request) {
    if (request == null) {
      throw new InvalidInputException("Faltan datos del departamento");
    }

    String nombre = Validaciones.texto(request.nombre(), "nombre", 100);

    if (departamentoRepository.existsByNombre(nombre)) {
      throw new BusinessConflictException("Ya existe un departamento con ese nombre");
    }

    Departamento departamento = new Departamento();
    departamento.setNombre(nombre);

    Departamento guardado = departamentoRepository.save(departamento);
    return new DepartamentoResponse(guardado.getId(), guardado.getNombre());
  }

  @Override
  public DocenteResponse crearDocente(CreateDocenteRequest request) {
    if (request == null) {
      throw new InvalidInputException("Faltan datos del docente");
    }

    String nombre = Validaciones.texto(request.nombre(), "nombre", 100);
    String apellidos = Validaciones.texto(request.apellidos(), "apellidos", 150);
    String email = Validaciones.email(request.email(), "email").toLowerCase();
    Long departamentoId = Validaciones.id(request.departamentoId(), "departamentoId");

    Departamento departamento =
        departamentoRepository
            .findById(departamentoId)
            .orElseThrow(() -> new ResourceNotFoundException("No existe el departamento"));

    if (docenteRepository.existsByEmailIgnoreCase(email)) {
      throw new BusinessConflictException("Ya existe un docente con ese correo");
    }

    Docente docente = new Docente();
    docente.setNombre(nombre);
    docente.setApellidos(apellidos);
    docente.setEmail(email);
    docente.setDepartamento(departamento);

    return DocenteMapper.toResponse(docenteRepository.save(docente));
  }

  @Override
  public EstudianteResponse crearEstudiante(CreateEstudianteRequest request) {
    if (request == null) {
      throw new InvalidInputException("Faltan datos del estudiante");
    }

    String nombre = Validaciones.texto(request.nombre(), "nombre", 100);
    String apellidos = Validaciones.texto(request.apellidos(), "apellidos", 150);
    String email = Validaciones.email(request.email(), "email").toLowerCase();
    LocalDate fechaNacimiento = Validaciones.fecha(request.fechaNacimiento(), "fechaNacimiento");
    Long grupoId = Validaciones.id(request.grupoId(), "grupoId");

    if (!fechaNacimiento.isBefore(LocalDate.now())) {
      throw new InvalidInputException("La fecha de nacimiento debe ser anterior a hoy");
    }

    Grupo grupo =
        grupoRepository
            .findById(grupoId)
            .orElseThrow(() -> new ResourceNotFoundException("No existe el grupo indicado"));

    if (estudianteRepository.existsByEmailIgnoreCase(email)) {
      throw new BusinessConflictException("Ya existe un estudiante con ese correo");
    }

    Estudiante estudiante = new Estudiante();
    estudiante.setNombre(nombre);
    estudiante.setApellidos(apellidos);
    estudiante.setEmail(email);
    estudiante.setFechaNacimiento(fechaNacimiento);
    estudiante.setGrupo(grupo);

    return EstudianteMapper.toResponse(estudianteRepository.save(estudiante));
  }

  @Override
  public EstudianteResponse obtenerEstudiante(Long id) {
    return EstudianteMapper.toResponse(buscarEstudiante(id));
  }

  // ---------------------------------------------------------------- RF03

  @Override
  public ActividadResponse crearActividad(CreateActividadRequest request) {
    if (request == null) {
      throw new InvalidInputException("Faltan datos de la actividad");
    }

    String titulo = Validaciones.texto(request.titulo(), "titulo", 150);
    String descripcion = Validaciones.texto(request.descripcion(), "descripcion", 10000);
    TipoActividad tipo = TipoActividad.desde(request.tipo());
    LocalDate fechaPrevista = Validaciones.fecha(request.fechaPrevista(), "fechaPrevista");
    Integer max = request.maxParticipantes();
    if (max == null || max <= 0) {
      throw new InvalidInputException("El máximo de participantes debe ser mayor que cero");
    }
    Long docenteId = Validaciones.id(request.docenteId(), "docenteId");

    Docente docente =
        docenteRepository
            .findById(docenteId)
            .orElseThrow(() -> new ResourceNotFoundException("No existe el docente responsable"));

    Actividad actividad = new Actividad();
    actividad.setTitulo(titulo);
    actividad.setDescripcion(descripcion);
    actividad.setTipo(tipo);
    actividad.setFechaPrevista(fechaPrevista);
    actividad.setMaxParticipantes(max);
    actividad.setDocente(docente);

    return ActividadMapper.toResponse(actividadRepository.save(actividad));
  }

  @Override
  public ActividadResponse obtenerActividad(Long id) {
    return ActividadMapper.toResponse(buscarActividad(id));
  }

  // ---------------------------------------------------------------- RF04

  @Override
  public InscripcionResponse inscribir(Long actividadId, CreateInscripcionRequest request) {
    if (request == null) {
      throw new InvalidInputException("Faltan datos de la inscripción");
    }
    Long estudianteId = Validaciones.id(request.estudianteId(), "estudianteId");

    // El cerrojo evita que dos peticiones simultáneas se repartan la misma última plaza.
    Actividad actividad =
        actividadRepository
            .buscarConCerrojo(actividadId)
            .orElseThrow(() -> new ResourceNotFoundException("No existe la actividad"));
    Estudiante estudiante = buscarEstudiante(estudianteId);

    if (inscripcionRepository.existsByActividadIdAndEstudianteId(actividadId, estudianteId)) {
      throw new BusinessConflictException("El estudiante ya está inscrito en la actividad");
    }
    if (inscripcionRepository.countByActividadId(actividadId) >= actividad.getMaxParticipantes()) {
      throw new BusinessConflictException("No quedan plazas disponibles");
    }

    Inscripcion inscripcion = new Inscripcion();
    inscripcion.setActividad(actividad);
    inscripcion.setEstudiante(estudiante);
    inscripcion.setFechaInscripcion(Instant.now());
    Inscripcion guardada = inscripcionRepository.save(inscripcion);

    return new InscripcionResponse(
        guardada.getId(), actividadId, estudianteId, guardada.getFechaInscripcion().toString());
  }

  @Override
  public List<EstudianteResponse> participantes(Long actividadId) {
    buscarActividad(actividadId);
    return inscripcionRepository.findByActividadIdOrderById(actividadId).stream()
        .map(i -> EstudianteMapper.toResponse(i.getEstudiante()))
        .toList();
  }

  // ---------------------------------------------------------------- RF05

  @Override
  public SeguimientoResponse registrarSeguimiento(
      Long actividadId, CreateSeguimientoRequest request) {
    if (request == null) {
      throw new InvalidInputException("Faltan datos del seguimiento");
    }
    // PostgreSQL y MongoDB no comparten transacción: comprobamos antes que la actividad existe
    // para no dejar seguimientos huérfanos.
    buscarActividad(actividadId);

    String tipo = Validaciones.texto(request.tipo(), "tipo", 50);
    if (request.datos() == null) {
      throw new InvalidInputException("El campo datos es obligatorio");
    }

    SeguimientoDocument seguimiento = new SeguimientoDocument();
    seguimiento.setActividadId(actividadId);
    seguimiento.setTipo(tipo);
    seguimiento.setDatos(request.datos());
    // MongoDB guarda fechas con precisión de milisegundos.
    seguimiento.setCreadoEn(Instant.now().truncatedTo(ChronoUnit.MILLIS));

    return SeguimientoMapper.toResponse(seguimientoRepository.save(seguimiento));
  }

  // ---------------------------------------------------------------- RF06

  @Override
  public ActividadDetalleResponse detalle(Long id) {
    Actividad actividad = buscarActividad(id);

    List<Inscripcion> inscripciones = inscripcionRepository.findByActividadIdOrderById(id);
    List<EstudianteResponse> estudiantes =
        inscripciones.stream().map(i -> EstudianteMapper.toResponse(i.getEstudiante())).toList();

    List<EtiquetaResponse> etiquetas =
        actividad.getEtiquetas().stream()
            .sorted(Comparator.comparing(Etiqueta::getId))
            .map(EtiquetaMapper::toResponse)
            .toList();

    List<SeguimientoResponse> seguimientos =
        seguimientoRepository.findByActividadIdOrderByCreadoEnAsc(id).stream()
            .map(SeguimientoMapper::toResponse)
            .toList();

    int ocupadas = inscripciones.size();
    return new ActividadDetalleResponse(
        ActividadMapper.toResponse(actividad),
        DocenteMapper.toResponse(actividad.getDocente()),
        ocupadas,
        actividad.getMaxParticipantes() - ocupadas,
        estudiantes,
        etiquetas,
        seguimientos);
  }

  // ---------------------------------------------------------------- RF08

  @Override
  public List<ActividadHistorialResponse> historial(Long estudianteId) {
    buscarEstudiante(estudianteId);
    return inscripcionRepository.findByEstudianteIdOrderByFechaInscripcionAsc(estudianteId).stream()
        .map(
            i -> {
              Actividad a = i.getActividad();
              return new ActividadHistorialResponse(
                  a.getId(),
                  a.getTitulo(),
                  a.getTipo().name(),
                  a.getFechaPrevista().toString(),
                  i.getFechaInscripcion().toString(),
                  DocenteMapper.toResponse(a.getDocente()));
            })
        .toList();
  }

  // ---------------------------------------------------------------- RF09

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

    Estudiante estudiante = buscarEstudiante(estudianteId);

    LocalDate fechaAutorizacion =
        Validaciones.fechaOpcional(request.fechaAutorizacion(), "fechaAutorizacion");

    if (request.autorizado() && fechaAutorizacion == null) {
      throw new InvalidInputException("Indica la fecha de autorización");
    }

    Contacto contacto = contactoRepository.findById(estudianteId).orElseGet(Contacto::new);
    contacto.setEstudiante(estudiante);
    contacto.setTutorLegal(Validaciones.texto(request.tutorLegal(), "tutorLegal", 200));
    contacto.setTelefono(Validaciones.texto(request.telefono(), "telefono", 40));
    contacto.setEmail(Validaciones.email(request.email(), "email").toLowerCase());
    contacto.setAutorizado(request.autorizado());
    contacto.setFechaAutorizacion(fechaAutorizacion);

    return aContactoResponse(estudianteId, contactoRepository.save(contacto));
  }

  @Override
  public ContactoResponse contacto(Long estudianteId) {
    buscarEstudiante(estudianteId);

    Contacto contacto =
        contactoRepository
            .findById(estudianteId)
            .orElseThrow(
                () -> new ResourceNotFoundException("El estudiante no tiene ficha de contacto"));

    return aContactoResponse(estudianteId, contacto);
  }

  // ---------------------------------------------------------------- RF10

  @Override
  public EtiquetaResponse crearEtiqueta(CreateEtiquetaRequest request) {
    if (request == null) {
      throw new InvalidInputException("Faltan datos de la etiqueta");
    }

    String nombre = Validaciones.texto(request.nombre(), "nombre", 50);

    if (etiquetaRepository.existsByNombre(nombre)) {
      throw new BusinessConflictException("Ya existe una etiqueta con ese nombre");
    }

    Etiqueta etiqueta = new Etiqueta();
    etiqueta.setNombre(nombre);
    return EtiquetaMapper.toResponse(etiquetaRepository.save(etiqueta));
  }

  @Override
  public void asociarEtiqueta(Long actividadId, Long etiquetaId) {
    Actividad actividad = buscarActividad(actividadId);
    Etiqueta etiqueta =
        etiquetaRepository
            .findById(etiquetaId)
            .orElseThrow(() -> new ResourceNotFoundException("No existe la etiqueta"));

    // Es un Set: añadir una etiqueta que ya está no duplica la relación. Hibernate guarda el
    // cambio al terminar la transacción.
    actividad.getEtiquetas().add(etiqueta);
  }

  @Override
  public List<ActividadResponse> actividadesPorEtiqueta(Long etiquetaId) {
    etiquetaRepository
        .findById(etiquetaId)
        .orElseThrow(() -> new ResourceNotFoundException("No existe la etiqueta"));

    return actividadRepository.findByEtiquetasIdOrderById(etiquetaId).stream()
        .map(ActividadMapper::toResponse)
        .toList();
  }

  // ---------------------------------------------------------------- ayudas

  private Estudiante buscarEstudiante(Long id) {
    return estudianteRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No existe el estudiante"));
  }

  private Actividad buscarActividad(Long id) {
    return actividadRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("No existe la actividad"));
  }

  private ContactoResponse aContactoResponse(Long estudianteId, Contacto contacto) {
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
}
