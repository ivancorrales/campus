package es.academia.campushub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;

@Entity
@Table(
    name = "actividad",
    indexes = @Index(name = "idx_actividad_docente", columnList = "docente_id"))
@Check(constraints = "max_participantes > 0")
@Getter
@Setter
@NoArgsConstructor
public class Actividad {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 150)
  private String titulo;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String descripcion;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private TipoActividad tipo;

  @Column(name = "fecha_prevista", nullable = false)
  private LocalDate fechaPrevista;

  @Column(name = "max_participantes", nullable = false)
  private Integer maxParticipantes;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "docente_id", nullable = false)
  private Docente docente;

  @ManyToMany
  @JoinTable(
      name = "actividad_etiqueta",
      joinColumns = @JoinColumn(name = "actividad_id"),
      inverseJoinColumns = @JoinColumn(name = "etiqueta_id"),
      indexes = @Index(name = "idx_actividad_etiqueta_etiqueta", columnList = "etiqueta_id"))
  private Set<Etiqueta> etiquetas = new HashSet<>();
}
