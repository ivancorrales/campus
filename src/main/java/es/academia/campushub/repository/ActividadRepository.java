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
    /**
     * Carga la actividad bloqueando su fila ({@code SELECT ... FOR UPDATE}). Serializa las
     * inscripciones concurrentes de una misma actividad para que el aforo no se pueda superar.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Actividad a where a.id = :id")
    Optional<Actividad> buscarParaActualizar(@Param("id") Long id);

    @Query("select a from Actividad a join a.etiquetas e where e.id = :etiquetaId order by a.id")
    List<Actividad> buscarPorEtiqueta(@Param("etiquetaId") Long etiquetaId);
}
