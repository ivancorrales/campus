package es.academia.campushub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

/** Matrícula de un estudiante en una actividad: un estudiante solo puede inscribirse una vez. */
@Entity
@Table(
        name = "inscripcion",
        uniqueConstraints =
        @UniqueConstraint(
                name = "uk_inscripcion_actividad_estudiante",
                columnNames = {"actividad_id", "estudiante_id"}),
        indexes = @Index(name = "idx_inscripcion_estudiante", columnList = "estudiante_id"))
public class Inscripcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "actividad_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_inscripcion_actividad"))
    private Actividad actividad;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "estudiante_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_inscripcion_estudiante"))
    private Estudiante estudiante;

    @Column(name = "fecha_inscripcion", nullable = false)
    private Instant fechaInscripcion;

    protected Inscripcion() {}

    public Inscripcion(Actividad actividad, Estudiante estudiante, Instant fechaInscripcion) {
        this.actividad = actividad;
        this.estudiante = estudiante;
        this.fechaInscripcion = fechaInscripcion;
    }

    public Long getId() {
        return id;
    }

    public Actividad getActividad() {
        return actividad;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public Instant getFechaInscripcion() {
        return fechaInscripcion;
    }
}
