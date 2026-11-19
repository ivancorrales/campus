package es.academia.campushub.repository;

import es.academia.campushub.entity.Actividad;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ActividadRepository extends JpaRepository<Actividad, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT a FROM Actividad a WHERE a.id = :id")
  Optional<Actividad> bloquearPorId(@Param("id") Long id);

  @Query(
      "SELECT DISTINCT a FROM Actividad a JOIN FETCH a.docente d JOIN FETCH d.departamento "
          + "LEFT JOIN FETCH a.etiquetas WHERE a.id = :id")
  Optional<Actividad> buscarDetallePorId(@Param("id") Long id);

  @Query(
      "SELECT a FROM Actividad a JOIN a.etiquetas e JOIN FETCH a.docente "
          + "WHERE e.id = :etiquetaId ORDER BY a.id")
  List<Actividad> buscarPorEtiqueta(@Param("etiquetaId") Long etiquetaId);
}
