package es.academia.campushub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "inscripcion",
    uniqueConstraints =
        @UniqueConstraint(
            name = "uk_inscripcion_actividad_estudiante",
            columnNames = {"actividad_id", "estudiante_id"}),
    indexes = @Index(name = "idx_inscripcion_estudiante", columnList = "estudiante_id"))
@Getter
@Setter
@NoArgsConstructor
public class Inscripcion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "actividad_id", nullable = false)
  private Actividad actividad;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "estudiante_id", nullable = false)
  private Estudiante estudiante;

  @Column(name = "fecha_inscripcion", nullable = false)
  private Instant fechaInscripcion;
}
