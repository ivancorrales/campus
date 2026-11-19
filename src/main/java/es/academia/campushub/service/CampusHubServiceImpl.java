package es.academia.campushub.service;

import es.academia.campushub.controller.dto.*;
import es.academia.campushub.document.Seguimiento;
import es.academia.campushub.entity.*;
import es.academia.campushub.mapper.Mapper;
import es.academia.campushub.repository.*;
import es.academia.campushub.util.Validaciones;
import es.academia.campushub.util.exception.BusinessConflictException;
import es.academia.campushub.util.exception.InvalidInputException;
import es.academia.campushub.util.exception.ResourceNotFoundException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CampusHubServiceImpl implements CampusHubService {

  private final DepartamentoRepository departamentos;
  private final DocenteRepository docentes;
  private final GrupoRepository grupos;
  private final EstudianteRepository estudiantes;
  private final ActividadRepository actividades;
  private final ContactoEstudianteRepository contactos;
  private final InscripcionRepository inscripciones;
  private final EtiquetaRepository etiquetas;
  private final SeguimientoRepository seguimientos;

  public CampusHubServiceImpl(
      DepartamentoRepository departamentos,
      DocenteRepository docentes,
      GrupoRepository grupos,
      EstudianteRepository estudiantes,
      ActividadRepository actividades,
      ContactoEstudianteRepository contactos,
      InscripcionRepository inscripciones,
      EtiquetaRepository etiquetas,
      SeguimientoRepository seguimientos) {
    this.departamentos = departamentos;
    this.docentes = docentes;
    this.grupos = grupos;
    this.estudiantes = estudiantes;
    this.actividades = actividades;
    this.contactos = contactos;
    this.inscripciones = inscripciones;
    this.etiquetas = etiquetas;
    this.seguimientos = seguimientos;
  }

  @Override
  public GrupoResponse crearGrupo(CreateGrupoRequest request) {
    if (request == null) {
      throw new InvalidInputException("Cuerpo obligatorio");
    }
    String nombre = Validaciones.texto(request.nombre(), "nombre", 100);
    String nivel = Validaciones.texto(request.nivel(), "nivel", 50);
    Docente tutor =
        docentes
            .findById(Validaciones.id(request.tutorDocenteId(), "tutorDocenteId"))
            .orElseThrow(() -> new ResourceNotFoundException("Docente tutor no encontrado"));
    Grupo g = new Grupo();
    g.setNombre(nombre);
    g.setNivel(nivel);
    g.setTutor(tutor);
    return Mapper.toResponse(grupos.saveAndFlush(g));
  }

  @Override
  public DepartamentoResponse crearDepartamento(CreateDepartamentoRequest request) {
    String nombre = Validaciones.texto(request == null ? null : request.nombre(), "nombre", 100);
    if (departamentos.existsByNombre(nombre)) {
      throw new BusinessConflictException("Ya existe un departamento con ese nombre");
    }
    Departamento d = new Departamento();
    d.setNombre(nombre);
    return Mapper.toResponse(departamentos.saveAndFlush(d));
  }

  @Override
  public EstudianteResponse crearEstudiante(CreateEstudianteRequest request) {
    if (request == null) {
      throw new InvalidInputException("Cuerpo obligatorio");
    }
    String nombre = Validaciones.texto(request.nombre(), "nombre", 80);
    String apellidos = Validaciones.texto(request.apellidos(), "apellidos", 120);
    String email = Validaciones.email(request.email(), "email");
    LocalDate nacimiento = Validaciones.fecha(request.fechaNacimiento(), "fechaNacimiento");
    if (!nacimiento.isBefore(LocalDate.now())) {
      throw new InvalidInputException("fechaNacimiento debe ser anterior a hoy");
    }
    Grupo grupo =
        grupos
            .findById(Validaciones.id(request.grupoId(), "grupoId"))
            .orElseThrow(() -> new ResourceNotFoundException("Grupo no encontrado"));
    if (estudiantes.existsByEmail(email)) {
      throw new BusinessConflictException("Ya existe un estudiante con ese correo");
    }
    Estudiante e = new Estudiante();
    e.setNombre(nombre);
    e.setApellidos(apellidos);
    e.setEmail(email);
    e.setFechaNacimiento(nacimiento);
    e.setGrupo(grupo);
    return Mapper.toResponse(estudiantes.saveAndFlush(e));
  }

  @Override
  public EstudianteResponse obtenerEstudiante(Long id) {
    return Mapper.toResponse(estudiante(id));
  }

  @Override
  public DocenteResponse crearDocente(CreateDocenteRequest request) {
    if (request == null) {
      throw new InvalidInputException("Cuerpo obligatorio");
    }
    String nombre = Validaciones.texto(request.nombre(), "nombre", 80);
    String apellidos = Validaciones.texto(request.apellidos(), "apellidos", 120);
    String email = Validaciones.email(request.email(), "email");
    Departamento departamento =
        departamentos
            .findById(Validaciones.id(request.departamentoId(), "departamentoId"))
            .orElseThrow(() -> new ResourceNotFoundException("Departamento no encontrado"));
    if (docentes.existsByEmail(email)) {
      throw new BusinessConflictException("Ya existe un docente con ese correo");
    }
    Docente d = new Docente();
    d.setNombre(nombre);
    d.setApellidos(apellidos);
    d.setEmail(email);
    d.setDepartamento(departamento);
    return Mapper.toResponse(docentes.saveAndFlush(d));
  }

  @Override
  public ActividadResponse crearActividad(CreateActividadRequest request) {
    if (request == null) {
      throw new InvalidInputException("Cuerpo obligatorio");
    }
    String titulo = Validaciones.texto(request.titulo(), "titulo", 150);
    String descripcion = Validaciones.textoLargo(request.descripcion(), "descripcion");
    String tipo = Validaciones.texto(request.tipo(), "tipo", 30);
    LocalDate fecha = Validaciones.fecha(request.fechaPrevista(), "fechaPrevista");
    if (request.maxParticipantes() == null || request.maxParticipantes() <= 0) {
      throw new InvalidInputException("maxParticipantes debe ser mayor que cero");
    }
    Docente docente =
        docentes
            .findById(Validaciones.id(request.docenteId(), "docenteId"))
            .orElseThrow(() -> new ResourceNotFoundException("Docente no encontrado"));
    Actividad a = new Actividad();
    a.setTitulo(titulo);
    a.setDescripcion(descripcion);
    a.setTipo(tipo);
    a.setFechaPrevista(fecha);
    a.setMaxParticipantes(request.maxParticipantes());
    a.setDocente(docente);
    return Mapper.toResponse(actividades.saveAndFlush(a));
  }

  @Override
  public ActividadResponse obtenerActividad(Long id) {
    Actividad a =
        actividades
            .findWithDocenteById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));
    return Mapper.toResponse(a);
  }

  @Override
  @Transactional
  public InscripcionResponse inscribir(Long actividadId, CreateInscripcionRequest request) {
    if (request == null) {
      throw new InvalidInputException("Cuerpo obligatorio");
    }
    Long estudianteId = Validaciones.id(request.estudianteId(), "estudianteId");
    Actividad actividad =
        actividades
            .findById(Validaciones.id(actividadId, "actividadId"))
            .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));
    Estudiante estudiante =
        estudiantes
            .findById(estudianteId)
            .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));
    if (inscripciones.existsByActividadIdAndEstudianteId(actividadId, estudianteId)) {
      throw new BusinessConflictException("El estudiante ya está inscrito en la actividad");
    }
    if (inscripciones.countByActividadId(actividadId) >= actividad.getMaxParticipantes()) {
      throw new BusinessConflictException("La actividad no tiene plazas disponibles");
    }
    Inscripcion i = new Inscripcion();
    i.setActividad(actividad);
    i.setEstudiante(estudiante);
    i.setFechaInscripcion(LocalDateTime.now());
    return Mapper.toResponse(inscripciones.saveAndFlush(i));
  }

  @Override
  @Transactional(readOnly = true)
  public List<EstudianteResponse> participantes(Long actividadId) {
    if (!actividades.existsById(Validaciones.id(actividadId, "actividadId"))) {
      throw new ResourceNotFoundException("Actividad no encontrada");
    }
    return inscripciones.findEstudiantesByActividadId(actividadId).stream()
        .map(Mapper::toResponse)
        .toList();
  }

  @Override
  public SeguimientoResponse registrarSeguimiento(
      Long actividadId, CreateSeguimientoRequest request) {
    if (request == null) {
      throw new InvalidInputException("Cuerpo obligatorio");
    }
    String tipo = Validaciones.texto(request.tipo(), "tipo", 50);
    if (request.datos() == null) {
      throw new InvalidInputException("datos es obligatorio");
    }
    Validaciones.clavesDocumento(request.datos(), "datos");
    if (!actividades.existsById(Validaciones.id(actividadId, "actividadId"))) {
      throw new ResourceNotFoundException("Actividad no encontrada");
    }
    Seguimiento s = new Seguimiento();
    s.setActividadId(actividadId);
    s.setTipo(tipo);
    s.setDatos(request.datos());
    s.setCreadoEn(Instant.now());
    return Mapper.toResponse(seguimientos.save(s));
  }

  @Override
  @Transactional(readOnly = true)
  public ActividadDetalleResponse detalle(Long id) {
    // 1) PostgreSQL: actividad, docente, etiquetas e inscritos
    Actividad a =
        actividades
            .findDetalleById(Validaciones.id(id, "id"))
            .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada"));
    List<EstudianteResponse> inscritos =
        inscripciones.findEstudiantesByActividadId(id).stream().map(Mapper::toResponse).toList();
    List<EtiquetaResponse> tags =
        a.getEtiquetas().stream()
            .sorted(Comparator.comparing(Etiqueta::getId))
            .map(Mapper::toResponse)
            .toList();
    // 2) MongoDB: seguimientos
    List<SeguimientoResponse> segs =
        seguimientos.findByActividadIdOrderByCreadoEnAscIdAsc(id).stream()
            .map(Mapper::toResponse)
            .toList();
    int ocupadas = inscritos.size();
    return new ActividadDetalleResponse(
        Mapper.toResponse(a),
        Mapper.toResponse(a.getDocente()),
        ocupadas,
        Math.max(0, a.getMaxParticipantes() - ocupadas),
        inscritos,
        tags,
        segs);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ActividadHistorialResponse> historial(Long estudianteId) {
    if (!estudiantes.existsById(Validaciones.id(estudianteId, "estudianteId"))) {
      throw new ResourceNotFoundException("Estudiante no encontrado");
    }
    return inscripciones.findHistorialByEstudianteId(estudianteId).stream()
        .map(Mapper::toHistorial)
        .toList();
  }

  @Override
  public ContactoResponse guardarContacto(Long estudianteId, UpsertContactoRequest request) {
    if (request == null) {
      throw new InvalidInputException("Cuerpo obligatorio");
    }
    String tutor = Validaciones.texto(request.tutorLegal(), "tutorLegal", 160);
    String telefono = Validaciones.texto(request.telefono(), "telefono", 20);
    String email = Validaciones.email(request.email(), "email");
    if (request.autorizado() == null) {
      throw new InvalidInputException("La autorizacion es obligatoria");
    }
    LocalDate fecha = Validaciones.fechaOpcional(request.fechaAutorizacion(), "fechaAutorizacion");
    Estudiante estudiante = estudiante(estudianteId);
    ContactoEstudiante c = contactos.findById(estudianteId).orElseGet(ContactoEstudiante::new);
    c.setEstudiante(estudiante);
    c.setTutorLegal(tutor);
    c.setTelefono(telefono);
    c.setEmail(email);
    c.setAutorizado(request.autorizado());
    c.setFechaAutorizacion(fecha);
    return Mapper.toResponse(contactos.saveAndFlush(c));
  }

  private Estudiante estudiante(Long id) {
    return estudiantes
        .findWithGrupoById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));
  }

  @Override
  public ContactoResponse contacto(Long estudianteId) {
    estudiante(estudianteId);
    return contactos
        .findById(estudianteId)
        .map(Mapper::toResponse)
        .orElseThrow(() -> new ResourceNotFoundException("El estudiante no tiene ficha"));
  }

  @Override
  public EtiquetaResponse crearEtiqueta(CreateEtiquetaRequest request) {
    String nombre = Validaciones.texto(request == null ? null : request.nombre(), "nombre", 100);
    if (etiquetas.existsByNombre(nombre)) {
      throw new BusinessConflictException("Ya existe una etiqueta con ese nombre");
    }
    Etiqueta e = new Etiqueta();
    e.setNombre(nombre);
    return Mapper.toResponse(etiquetas.saveAndFlush(e));
  }

  @Override
  @Transactional
  public void asociarEtiqueta(Long actividadId, Long etiquetaId) {
    if (!actividades.existsById(Validaciones.id(actividadId, "actividadId"))) {
      throw new ResourceNotFoundException("Actividad no encontrada");
    }
    if (!etiquetas.existsById(Validaciones.id(etiquetaId, "etiquetaId"))) {
      throw new ResourceNotFoundException("Etiqueta no encontrada");
    }
    actividades.asociarEtiqueta(actividadId, etiquetaId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ActividadResponse> actividadesPorEtiqueta(Long etiquetaId) {
    if (!etiquetas.existsById(Validaciones.id(etiquetaId, "etiquetaId"))) {
      throw new ResourceNotFoundException("Etiqueta no encontrada");
    }
    return actividades.findByEtiquetaId(etiquetaId).stream().map(Mapper::toResponse).toList();
  }
}
