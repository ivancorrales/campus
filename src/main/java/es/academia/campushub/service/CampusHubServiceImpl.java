package es.academia.campushub.service;

import es.academia.campushub.config.ConfiguracionRedis;
import es.academia.campushub.controller.dto.*;
import es.academia.campushub.document.Seguimiento;
import es.academia.campushub.entity.*;
import es.academia.campushub.mapper.CampusMapper;
import es.academia.campushub.repository.*;
import es.academia.campushub.util.exception.BusinessConflictException;
import es.academia.campushub.util.exception.InvalidInputException;
import es.academia.campushub.util.exception.ResourceNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación de los casos de uso de CampusHub.
 *
 * <p>Reglas de negocio validadas aquí y, además, protegidas por el almacenamiento: unicidades y
 * claves foráneas en PostgreSQL, índice y validador en MongoDB. El aforo se controla con bloqueo
 * pesimista de la fila de la actividad para evitar inscripciones concurrentes por encima del
 * máximo. El detalle completo se sirve con caché cache-aside en Redis (ver docs/).
 */
@Service
@Transactional(readOnly = true)
public class CampusHubServiceImpl implements CampusHubService {

  private final DocenteRepository docenteRepository;
  private final DepartamentoRepository departamentoRepository;
  private final GrupoRepository grupoRepository;
  private final EstudianteRepository estudianteRepository;
  private final ContactoEstudianteRepository contactoEstudianteRepository;
  private final ActividadRepository actividadRepository;
  private final EtiquetaRepository etiquetaRepository;
  private final InscripcionRepository inscripcionRepository;
  private final SeguimientoRepository seguimientoRepository;
  private final CampusMapper mapper;

  public CampusHubServiceImpl(
      DocenteRepository docenteRepository,
      DepartamentoRepository departamentoRepository,
      GrupoRepository grupoRepository,
      EstudianteRepository estudianteRepository,
      ContactoEstudianteRepository contactoEstudianteRepository,
      ActividadRepository actividadRepository,
      EtiquetaRepository etiquetaRepository,
      InscripcionRepository inscripcionRepository,
      SeguimientoRepository seguimientoRepository,
      CampusMapper mapper) {
    this.docenteRepository = docenteRepository;
    this.departamentoRepository = departamentoRepository;
    this.grupoRepository = grupoRepository;
    this.estudianteRepository = estudianteRepository;
    this.contactoEstudianteRepository = contactoEstudianteRepository;
    this.actividadRepository = actividadRepository;
    this.etiquetaRepository = etiquetaRepository;
    this.inscripcionRepository = inscripcionRepository;
    this.seguimientoRepository = seguimientoRepository;
    this.mapper = mapper;
  }

  // ---------------------------------------------------------------- validaciones

  private String textoObligatorio(String valor, String campo, int max) {
    if (valor == null || valor.isBlank()) {
      throw new InvalidInputException("El campo '" + campo + "' es obligatorio");
    }
    if (valor.length() > max) {
      throw new InvalidInputException(
          "El campo '" + campo + "' supera la longitud máxima de " + max);
    }
    return valor;
  }

  private String textoOpcional(String valor, String campo, int max) {
    if (valor != null && valor.length() > max) {
      throw new InvalidInputException(
          "El campo '" + campo + "' supera la longitud máxima de " + max);
    }
    return valor;
  }

  private String correoValido(String valor, String campo) {
    textoObligatorio(valor, campo, 254);
    if (!valor.contains("@")) {
      throw new InvalidInputException("El campo '" + campo + "' no es un correo válido");
    }
    return valor;
  }

  private LocalDate fechaIso(String valor, String campo) {
    if (valor == null || valor.isBlank()) {
      throw new InvalidInputException("El campo '" + campo + "' es obligatorio");
    }
    try {
      return LocalDate.parse(valor);
    } catch (DateTimeParseException e) {
      throw new InvalidInputException("El campo '" + campo + "' no es una fecha ISO válida");
    }
  }

  private LocalDate fechaIsoOpcional(String valor, String campo) {
    if (valor == null || valor.isBlank()) {
      return null;
    }
    try {
      return LocalDate.parse(valor);
    } catch (DateTimeParseException e) {
      throw new InvalidInputException("El campo '" + campo + "' no es una fecha ISO válida");
    }
  }

  // ---------------------------------------------------------------- RF01

  @Override
  @Transactional
  public GrupoResponse crearGrupo(CreateGrupoRequest request) {
    if (request.tutorDocenteId() == null) {
      throw new InvalidInputException("El campo 'tutorDocenteId' es obligatorio");
    }
    Docente tutor =
        docenteRepository
            .findById(request.tutorDocenteId())
            .orElseThrow(() -> new ResourceNotFoundException("Docente no encontrado"));
    Grupo grupo =
        new Grupo(
            textoObligatorio(request.nombre(), "nombre", 100),
            textoOpcional(request.nivel(), "nivel", 50),
            tutor);
    try {
      return mapper.aGrupo(grupoRepository.save(grupo));
    } catch (DataIntegrityViolationException e) {
      throw new BusinessConflictException("Conflicto al crear el grupo");
    }
  }

  @Override
  @Transactional
  public EstudianteResponse crearEstudiante(CreateEstudianteRequest request) {
    if (request.grupoId() == null) {
      throw new InvalidInputException("El campo 'grupoId' es obligatorio");
    }
    LocalDate nacimiento = fechaIso(request.fechaNacimiento(), "fechaNacimiento");
    if (!nacimiento.isBefore(LocalDate.now())) {
      throw new InvalidInputException("La fecha de nacimiento debe ser anterior a la fecha actual");
    }
    Grupo grupo =
        grupoRepository
            .findById(request.grupoId())
            .orElseThrow(() -> new ResourceNotFoundException("Grupo no encontrado"));
    Estudiante estudiante =
        new Estudiante(
            textoObligatorio(request.nombre(), "nombre", 100),
            textoObligatorio(request.apellidos(), "apellidos", 150),
            correoValido(request.email(), "email"),
            nacimiento,
            grupo);
    try {
      return mapper.aEstudiante(estudianteRepository.save(estudiante));
    } catch (DataIntegrityViolationException e) {
      throw new BusinessConflictException("El correo del estudiante ya está registrado");
    }
  }

  @Override
  public EstudianteResponse obtenerEstudiante(Long id) {
    Estudiante estudiante =
        estudianteRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));
    return mapper.aEstudiante(estudiante);
  }

  // ---------------------------------------------------------------- RF02

  @Override
  @Transactional
  public DepartamentoResponse crearDepartamento(CreateDepartamentoRequest request) {
    Departamento departamento = new Departamento(textoObligatorio(request.nombre(), "nombre", 100));
    try {
      return mapper.aDepartamento(departamentoRepository.save(departamento));
    } catch (DataIntegrityViolationException e) {
      throw new BusinessConflictException("El departamento ya existe");
    }
  }

  @Override
  @Transactional
  public DocenteResponse crearDocente(CreateDocenteRequest request) {
    if (request.departamentoId() == null) {
      throw new InvalidInputException("El campo 'departamentoId' es obligatorio");
    }
    Departamento departamento =
        departamentoRepository
            .findById(request.departamentoId())
            .orElseThrow(() -> new ResourceNotFoundException("Departamento no encontrado"));
    Docente docente =
        new Docente(
            correoValido(request.email(), "email"),
            textoObligatorio(request.nombre(), "nombre", 100),
            textoObligatorio(request.apellidos(), "apellidos", 150),
            departamento);
    try {
      return mapper.aDocente(docenteRepository.save(docente));
    } catch (DataIntegrityViolationException e) {
      throw new BusinessConflictException("El correo del docente ya está registrado");
    }
  }

  // ---------------------------------------------------------------- RF03

  @Override
  @Transactional
  public ActividadResponse crearActividad(CreateActividadRequest request) {
    if (request.docenteId() == null) {
      throw new InvalidInputException("El campo 'docenteId' es obligatorio");
    }
    if (request.maxParticipantes() == null || request.maxParticipantes() <= 0) {
      throw new InvalidInputException("maxParticipantes debe ser mayor que cero");
    }
    Docente docente =
        docenteRepository
            .findById(request.docenteId())
            .orElseThrow(() -> new ResourceNotFoundException("Docente no encontrado"));
    Actividad actividad =
        new Actividad(
            docente,
            textoObligatorio(request.titulo(), "titulo", 150),
            textoOpcional(request.descripcion(), "descripcion", 100_000),
            textoObligatorio(request.tipo(), "tipo", 50),
            fechaIso(request.fechaPrevista(), "fechaPrevista"),
            request.maxParticipantes());
    return mapper.aActividad(actividadRepository.save(actividad));
  }

  @Override
  public ActividadResponse obtenerActividad(Long id) {
    Actividad actividad =
        actividadRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));
    return mapper.aActividad(actividad);
  }

  // ---------------------------------------------------------------- RF04

  @Override
  @Transactional
  @CacheEvict(value = ConfiguracionRedis.CACHE_DETALLE, key = "#actividadId")
  public InscripcionResponse inscribir(Long actividadId, CreateInscripcionRequest request) {
    if (request.estudianteId() == null) {
      throw new InvalidInputException("El campo 'estudianteId' es obligatorio");
    }
    // Bloqueo pesimista: serializa inscripciones concurrentes y protege el aforo.
    Actividad actividad =
        actividadRepository
            .bloquearPorId(actividadId)
            .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));
    Estudiante estudiante =
        estudianteRepository
            .findById(request.estudianteId())
            .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));

    long ocupadas = inscripcionRepository.contarPorActividad(actividadId);
    if (ocupadas >= actividad.getMaxParticipantes()) {
      throw new BusinessConflictException("La actividad ha alcanzado el aforo máximo");
    }

    try {
      Inscripcion inscripcion = new Inscripcion(actividad, estudiante, LocalDateTime.now());
      return mapper.aInscripcion(inscripcionRepository.save(inscripcion));
    } catch (DataIntegrityViolationException e) {
      throw new BusinessConflictException("El estudiante ya está inscrito en esta actividad");
    }
  }

  @Override
  public List<EstudianteResponse> participantes(Long actividadId) {
    if (!actividadRepository.existsById(actividadId)) {
      throw new ResourceNotFoundException("Actividad no encontrada");
    }
    List<Inscripcion> inscripciones = inscripcionRepository.buscarPorActividad(actividadId);
    return mapper.aLista(inscripciones, i -> mapper.aEstudiante(i.getEstudiante()));
  }

  // ---------------------------------------------------------------- RF05

  @Override
  @Transactional
  @CacheEvict(value = ConfiguracionRedis.CACHE_DETALLE, key = "#actividadId")
  public SeguimientoResponse registrarSeguimiento(
      Long actividadId, CreateSeguimientoRequest request) {
    if (!actividadRepository.existsById(actividadId)) {
      throw new ResourceNotFoundException("Actividad no encontrada");
    }
    Seguimiento seguimiento =
        new Seguimiento(
            actividadId,
            textoObligatorio(request.tipo(), "tipo", 50),
            request.datos(),
            LocalDateTime.now());
    Seguimiento saved = seguimientoRepository.save(seguimiento);
    return mapper.aSeguimiento(saved);
  }

  // ---------------------------------------------------------------- RF06

  @Override
  @Cacheable(value = ConfiguracionRedis.CACHE_DETALLE, key = "#id")
  public ActividadDetalleResponse detalle(Long id) {
    Actividad actividad =
        actividadRepository
            .buscarDetallePorId(id)
            .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));
    List<Inscripcion> inscripciones = inscripcionRepository.buscarPorActividad(id);
    List<Seguimiento> seguimientos = seguimientoRepository.findByActividadIdOrderByCreadoEnDesc(id);

    int ocupadas = inscripciones.size();
    int disponibles = actividad.getMaxParticipantes() - ocupadas;

    return new ActividadDetalleResponse(
        mapper.aActividad(actividad),
        mapper.aDocente(actividad.getDocente()),
        ocupadas,
        disponibles,
        mapper.aLista(inscripciones, i -> mapper.aEstudiante(i.getEstudiante())),
        mapper.aLista(actividad.getEtiquetas(), mapper::aEtiqueta),
        mapper.aLista(seguimientos, mapper::aSeguimiento));
  }

  // ---------------------------------------------------------------- RF08

  @Override
  public List<ActividadHistorialResponse> historial(Long estudianteId) {
    if (!estudianteRepository.existsById(estudianteId)) {
      throw new ResourceNotFoundException("Estudiante no encontrado");
    }
    List<Inscripcion> inscripciones = inscripcionRepository.buscarPorEstudiante(estudianteId);
    return mapper.aLista(inscripciones, mapper::aHistorial);
  }

  // ---------------------------------------------------------------- RF09

  @Override
  @Transactional
  public ContactoResponse guardarContacto(Long estudianteId, UpsertContactoRequest request) {
    Estudiante estudiante =
        estudianteRepository
            .findById(estudianteId)
            .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));

    ContactoEstudiante contacto =
        contactoEstudianteRepository
            .buscarPorEstudiante(estudianteId)
            .orElse(new ContactoEstudiante(estudiante));

    contacto.actualizar(
        textoObligatorio(request.tutorLegal(), "tutorLegal", 200),
        textoOpcional(request.telefono(), "telefono", 30),
        textoOpcional(request.email(), "email", 254),
        Boolean.TRUE.equals(request.autorizado()),
        fechaIsoOpcional(request.fechaAutorizacion(), "fechaAutorizacion"));

    return mapper.aContacto(contactoEstudianteRepository.save(contacto));
  }

  @Override
  public ContactoResponse contacto(Long estudianteId) {
    if (!estudianteRepository.existsById(estudianteId)) {
      throw new ResourceNotFoundException("Estudiante no encontrado");
    }
    ContactoEstudiante contacto =
        contactoEstudianteRepository
            .buscarPorEstudiante(estudianteId)
            .orElseThrow(() -> new ResourceNotFoundException("Ficha de contacto no encontrada"));
    return mapper.aContacto(contacto);
  }

  // ---------------------------------------------------------------- RF10

  @Override
  @Transactional
  public EtiquetaResponse crearEtiqueta(CreateEtiquetaRequest request) {
    Etiqueta etiqueta = new Etiqueta(textoObligatorio(request.nombre(), "nombre", 50));
    try {
      return mapper.aEtiqueta(etiquetaRepository.save(etiqueta));
    } catch (DataIntegrityViolationException e) {
      throw new BusinessConflictException("La etiqueta ya existe");
    }
  }

  @Override
  @Transactional
  @CacheEvict(value = ConfiguracionRedis.CACHE_DETALLE, key = "#actividadId")
  public void asociarEtiqueta(Long actividadId, Long etiquetaId) {
    Actividad actividad =
        actividadRepository
            .findById(actividadId)
            .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));
    Etiqueta etiqueta =
        etiquetaRepository
            .findById(etiquetaId)
            .orElseThrow(() -> new ResourceNotFoundException("Etiqueta no encontrada"));

    // Idempotente: la relación actividad-etiqueta es un Set y la join table es única.
    actividad.getEtiquetas().add(etiqueta);
    actividadRepository.save(actividad); // Corregido: persistir la relación explícitamente
  }

  @Override
  public List<ActividadResponse> actividadesPorEtiqueta(Long etiquetaId) {
    Etiqueta etiqueta =
        etiquetaRepository
            .findById(etiquetaId)
            .orElseThrow(() -> new ResourceNotFoundException("Etiqueta no encontrada"));
    List<Actividad> actividades = actividadRepository.buscarPorEtiqueta(etiqueta.getId());
    return mapper.aLista(actividades, mapper::aActividad);
  }
}
