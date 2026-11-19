package es.academia.campushub.repository;

import es.academia.campushub.entity.Actividad;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ActividadRepository extends JpaRepository<Actividad, Long> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<Actividad> findById(Long id);

  @EntityGraph(attributePaths = {"docente", "docente.departamento"})
  Optional<Actividad> findWithDocenteById(Long id);

  @EntityGraph(attributePaths = {"docente", "docente.departamento", "etiquetas"})
  Optional<Actividad> findDetalleById(Long id);

  @EntityGraph(attributePaths = {"docente"})
  @Query("select a from Actividad a join a.etiquetas e where e.id = :etiquetaId order by a.id")
  List<Actividad> findByEtiquetaId(@Param("etiquetaId") Long etiquetaId);

  @Modifying
  @Query(
      value =
          "INSERT INTO actividad_etiqueta (actividad_id, etiqueta_id) "
              + "VALUES (:actividadId, :etiquetaId) ON CONFLICT DO NOTHING",
      nativeQuery = true)
  int asociarEtiqueta(@Param("actividadId") Long actividadId, @Param("etiquetaId") Long etiquetaId);
}
