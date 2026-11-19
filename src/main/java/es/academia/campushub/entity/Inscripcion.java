package es.academia.campushub.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "inscripcion",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_inscripcion_actividad_estudiante",
            columnNames = {"actividad_id", "estudiante_id"}),
    indexes = @Index(name = "ix_inscripcion_estudiante", columnList = "estudiante_id"))
@Getter
@Setter
public class Inscripcion {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "actividad_id", nullable = false)
  private Actividad actividad;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "estudiante_id", nullable = false)
  private Estudiante estudiante;

  @Column(name = "fecha_inscripcion", nullable = false)
  private LocalDateTime fechaInscripcion;

  public Inscripcion() {}
}
