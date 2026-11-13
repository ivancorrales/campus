package es.academia.campushub.service;

import es.academia.campushub.controller.dto.*;
import es.academia.campushub.entity.*;
import es.academia.campushub.mapper.ActividadMapper;
import es.academia.campushub.mapper.EtiquetaMapper;
import es.academia.campushub.repository.ActividadRepository;
import es.academia.campushub.repository.DepartamentoRepository;
import es.academia.campushub.repository.DocenteRepository;
import es.academia.campushub.repository.EtiquetaRepository;
import es.academia.campushub.util.Validaciones;
import es.academia.campushub.util.exception.InvalidInputException;
import es.academia.campushub.util.exception.PersistenciaPendienteException;
import es.academia.campushub.util.exception.ResourceNotFoundException;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Punto de entrada de la tarea. Sustituye cada stub por una implementación real. NO hay entidades,
 * repositorios ni documentos entregados.
 */
@Service
public class CampusHubServiceImpl implements CampusHubService {

  private final DepartamentoRepository departamentoRepository;
  private final DocenteRepository docenteRepository;
  private final EtiquetaRepository etiquetaRepository;
  private final ActividadRepository actividadRepository;

  public CampusHubServiceImpl(
          DepartamentoRepository departamentoRepository,
          DocenteRepository docenteRepository,
          EtiquetaRepository etiquetaRepository,
          ActividadRepository actividadRepository) {
    this.departamentoRepository = departamentoRepository;
    this.docenteRepository = docenteRepository;
    this.etiquetaRepository = etiquetaRepository;
    this.actividadRepository = actividadRepository;
  }

  @Override
  public GrupoResponse crearGrupo(CreateGrupoRequest request) {
    throw new PersistenciaPendienteException("crearGrupo");
  }

  @Override
  public DepartamentoResponse crearDepartamento(CreateDepartamentoRequest request) {
    if (request.nombre() == null || request.nombre().isBlank()) {
      throw new InvalidInputException("El nombre del departamento es obligatorio");
    }
    Departamento departamento = new Departamento();
    departamento.setNombre(request.nombre().trim());
    Departamento guardado = departamentoRepository.save(departamento);
    return new DepartamentoResponse(guardado.getId(), guardado.getNombre());
  }

  @Override
  public EstudianteResponse crearEstudiante(CreateEstudianteRequest request) {
    throw new PersistenciaPendienteException("crearEstudiante");
  }

  @Override
  public EstudianteResponse obtenerEstudiante(Long id) {
    throw new PersistenciaPendienteException("obtenerEstudiante");
  }

  @Override
  public DocenteResponse crearDocente(CreateDocenteRequest request) {
    if (request.nombre() == null
            || request.nombre().isBlank()
            || request.apellidos() == null
            || request.apellidos().isBlank()
            || request.email() == null
            || request.email().isBlank()
            || request.departamentoId() == null) {
      throw new InvalidInputException("Nombre, apellidos, email y departamento son obligatorios");
    }
    Departamento departamento =
            departamentoRepository
                    .findById(request.departamentoId())
                    .orElseThrow(() -> new ResourceNotFoundException("El departamento no existe"));

    Docente docente = new Docente();
    docente.setNombre(request.nombre().trim());
    docente.setApellidos(request.apellidos().trim());
    docente.setEmail(request.email().trim());
    docente.setDepartamento(departamento);

    Docente guardado = docenteRepository.save(docente);
    return new DocenteResponse(
            guardado.getId(),
            guardado.getNombre(),
            guardado.getApellidos(),
            guardado.getEmail(),
            guardado.getDepartamento().getId());
  }

  @Override
  public ActividadResponse crearActividad(CreateActividadRequest request) {
    String titulo = Validaciones.texto(request.titulo(), "titulo", 150);
    String descripcion = Validaciones.texto(request.descripcion(), "descripcion", 10000);
    TipoActividad tipo = TipoActividad.desde(request.tipo());
    LocalDate fecha = Validaciones.fecha(request.fechaPrevista(), "fechaPrevista");
    Integer max = request.maxParticipantes();
    if (max == null || max <= 0) {
      throw new InvalidInputException("El máximo de participantes debe ser mayor que cero");
    }
    Long docenteId = Validaciones.id(request.docenteId(), "docenteId");
    Docente docente =
            docenteRepository
                    .findById(docenteId)
                    .orElseThrow(() -> new ResourceNotFoundException("El docente responsable no existe"));

    Actividad actividad = new Actividad();
    actividad.setTitulo(titulo);
    actividad.setDescripcion(descripcion);
    actividad.setTipo(tipo);
    actividad.setFechaPrevista(fecha);
    actividad.setMaxParticipantes(max);
    actividad.setDocente(docente);
    return ActividadMapper.toResponse(actividadRepository.save(actividad));
  }

  @Override
  public ActividadResponse obtenerActividad(Long id) {
    return ActividadMapper.toResponse(buscarActividad(id));
  }

  private Actividad buscarActividad(Long id) {
    return actividadRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("La actividad no existe"));
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
    throw new PersistenciaPendienteException("guardarContacto");
  }

  @Override
  public ContactoResponse contacto(Long estudianteId) {
    throw new PersistenciaPendienteException("contacto");
  }

  @Override
  public EtiquetaResponse crearEtiqueta(CreateEtiquetaRequest request) {
    String nombre = Validaciones.texto(request.nombre(), "nombre", 50);
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
                    .orElseThrow(() -> new ResourceNotFoundException("La etiqueta no existe"));
    actividad.getEtiquetas().add(etiqueta);
  }

  @Override
  public List<ActividadResponse> actividadesPorEtiqueta(Long etiquetaId) {
    etiquetaRepository
            .findById(etiquetaId)
            .orElseThrow(() -> new ResourceNotFoundException("La etiqueta no existe"));
    return actividadRepository.findByEtiquetasIdOrderById(etiquetaId).stream()
            .map(ActividadMapper::toResponse)
            .toList();
  }
}