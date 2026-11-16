package es.academia.campushub.repository;

import es.academia.campushub.entity.Inscripcion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

  boolean existsByActividadIdAndEstudianteId(Long actividadId, Long estudianteId);

  long countByActividadId(Long actividadId);

  List<Inscripcion> findByActividadIdOrderById(Long actividadId);

  List<Inscripcion> findByEstudianteIdOrderByFechaInscripcionAsc(Long estudianteId);
}
