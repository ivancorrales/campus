package es.academia.campushub.repository;

import es.academia.campushub.entity.Inscripcion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {
    boolean existsByActividadIdAndEstudianteId(Long actividadId, Long estudianteId);

    @Query("select count(i) from Inscripcion i where i.actividad.id = :actividadId")
    long contarPorActividad(@Param("actividadId") Long actividadId);

    @Query(
            "select i from Inscripcion i join fetch i.estudiante"
                    + " where i.actividad.id = :actividadId order by i.id")
    List<Inscripcion> buscarPorActividad(@Param("actividadId") Long actividadId);

    @Query(
            "select i from Inscripcion i join fetch i.actividad a join fetch a.docente"
                    + " where i.estudiante.id = :estudianteId"
                    + " order by i.fechaInscripcion desc, i.id desc")
    List<Inscripcion> buscarHistorial(@Param("estudianteId") Long estudianteId);
}
