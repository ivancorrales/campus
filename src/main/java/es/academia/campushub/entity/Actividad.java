package es.academia.campushub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "actividad",
    indexes = @Index(name = "ix_actividad_docente", columnList = "docente_id"),
    check = {
      @CheckConstraint(name = "ck_actividad_max", constraint = "max_participantes > 0"),
      @CheckConstraint(name = "ck_actividad_tipo", constraint = "length(trim(tipo)) > 0"),
      @CheckConstraint(name = "ck_actividad_titulo", constraint = "length(trim(titulo)) > 0"),
      @CheckConstraint(
          name = "ck_actividad_descripcion",
          constraint = "length(trim(descripcion)) > 0")
    })
@Getter
@Setter
public class Actividad {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 150)
  @NotBlank(message = "El título no puede estar vacío")
  @Size(max = 150, message = "El título no puede superar los 150 caracteres")
  private String titulo;

  @Column(nullable = false, columnDefinition = "TEXT")
  @NotBlank(message = "La descripción no puede estar vacía")
  private String descripcion;

  @Column(nullable = false, length = 30)
  @NotBlank(message = "El tipo no puede estar vacío")
  @Size(max = 30, message = "El tipo no puede superar los 30 caracteres")
  private String tipo;

  @Column(name = "fecha_prevista", nullable = false)
  @NotNull(message = "La fecha prevista es obligatoria")
  private LocalDate fechaPrevista;

  @Column(name = "max_participantes", nullable = false)
  @NotNull(message = "El número máximo de participantes es obligatorio")
  @Min(value = 1, message = "El número máximo de participantes debe ser mayor a 0")
  private Integer maxParticipantes;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "docente_id", nullable = false)
  private Docente docente;

  @ManyToMany
  @JoinTable(
      name = "actividad_etiqueta",
      joinColumns = @JoinColumn(name = "actividad_id"),
      inverseJoinColumns = @JoinColumn(name = "etiqueta_id"))
  private Set<Etiqueta> etiquetas = new HashSet<>();

  public Actividad() {}
}
