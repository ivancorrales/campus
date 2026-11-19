package es.academia.campushub.repository;

import es.academia.campushub.entity.Inscripcion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

  @Query(
      "SELECT i FROM Inscripcion i JOIN FETCH i.estudiante e JOIN FETCH e.grupo "
          + "WHERE i.actividad.id = :actividadId ORDER BY i.id")
  List<Inscripcion> buscarPorActividad(@Param("actividadId") Long actividadId);

  @Query(
      "SELECT i FROM Inscripcion i JOIN FETCH i.actividad a JOIN FETCH a.docente d "
          + "JOIN FETCH d.departamento WHERE i.estudiante.id = :estudianteId ORDER BY i.fecha, i.id")
  List<Inscripcion> buscarPorEstudiante(@Param("estudianteId") Long estudianteId);

  @Query("SELECT COUNT(i) FROM Inscripcion i WHERE i.actividad.id = :actividadId")
  long contarPorActividad(@Param("actividadId") Long actividadId);
}
