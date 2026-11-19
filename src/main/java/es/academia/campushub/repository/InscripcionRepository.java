package es.academia.campushub.repository;

import es.academia.campushub.entity.Estudiante;
import es.academia.campushub.entity.Inscripcion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {
  boolean existsByActividadIdAndEstudianteId(Long actividadId, Long estudianteId);

  long countByActividadId(Long actividadId);

  @Query(
      "select e from Inscripcion i join i.estudiante e join fetch e.grupo "
          + "where i.actividad.id = :actividadId order by i.fechaInscripcion, i.id")
  List<Estudiante> findEstudiantesByActividadId(@Param("actividadId") Long actividadId);

  @Query(
      "select i from Inscripcion i join fetch i.actividad a join fetch a.docente d "
          + "join fetch d.departamento where i.estudiante.id = :estudianteId "
          + "order by a.fechaPrevista, a.id")
  List<Inscripcion> findHistorialByEstudianteId(@Param("estudianteId") Long estudianteId);
}
