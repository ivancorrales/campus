package es.academia.campushub.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Inscripción de un estudiante en una actividad. La pareja actividad-estudiante es única a nivel de
 * base de datos y la fecha se conserva como fecha-hora (ISO-8601 en la API).
 */
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
  @Column(name = "inscripcion_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "actividad_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Actividad actividad;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "estudiante_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Estudiante estudiante;

  @Column(name = "fecha", nullable = false)
  private LocalDateTime fecha;

  protected Inscripcion() {}

  public Inscripcion(Actividad actividad, Estudiante estudiante, LocalDateTime fecha) {
    this.actividad = actividad;
    this.estudiante = estudiante;
    this.fecha = fecha;
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

  public LocalDateTime getFecha() {
    return fecha;
  }

  public LocalDate getFechaInscripcion() {
    return fecha.toLocalDate();
  }
}
