package es.academia.campushub.service;

import es.academia.campushub.controller.dto.*;
import es.academia.campushub.entity.Departamento;
import es.academia.campushub.entity.Docente;
import es.academia.campushub.repository.DepartamentoRepository;
import es.academia.campushub.repository.DocenteRepository;
import es.academia.campushub.util.exception.InvalidInputException;
import es.academia.campushub.util.exception.PersistenciaPendienteException;
import java.util.List;

import es.academia.campushub.util.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Punto de entrada de la tarea. Sustituye cada stub por una implementación real. NO hay entidades,
 * repositorios ni documentos entregados.
 */
@Service
public class CampusHubServiceImpl implements CampusHubService {

  private final DepartamentoRepository departamentoRepository;
  private final DocenteRepository docenteRepository;

  public CampusHubServiceImpl(
          DepartamentoRepository departamentoRepository, DocenteRepository docenteRepository) {
    this.departamentoRepository = departamentoRepository;
    this.docenteRepository = docenteRepository;
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
    if (request.nombre() == null || request.nombre().isBlank()
            || request.apellidos() == null || request.apellidos().isBlank()
            || request.email() == null || request.email().isBlank()
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
    throw new PersistenciaPendienteException("guardarContacto");
  }

  @Override
  public ContactoResponse contacto(Long estudianteId) {
    throw new PersistenciaPendienteException("contacto");
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
