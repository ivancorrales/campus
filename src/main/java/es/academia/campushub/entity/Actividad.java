package es.academia.campushub.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Check(constraints = "max_participantes > 0")
@Table(
    name = "actividad",
    indexes = @Index(name = "idx_actividad_docente", columnList = "docente_id"))
public class Actividad {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "actividad_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "docente_id", nullable = false)
  @OnDelete(action = OnDeleteAction.RESTRICT)
  private Docente docente;

  @Column(nullable = false, length = 150)
  private String titulo;

  @Column(columnDefinition = "TEXT")
  private String descripcion;

  @Column(nullable = false, length = 50)
  private String tipo;

  @Column(name = "fecha", nullable = false)
  private LocalDate fecha;

  @Column(name = "max_participantes", nullable = false)
  private int maxParticipantes;

  @ManyToMany
  @JoinTable(
      name = "actividad_etiquetas",
      joinColumns = @JoinColumn(name = "actividad_id"),
      inverseJoinColumns = @JoinColumn(name = "etiquetas_id"),
      foreignKey = @ForeignKey(name = "fk_actividad_etiquetas_actividad"),
      indexes = @Index(name = "idx_actividad_etiquetas_etiqueta", columnList = "etiquetas_id"))
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Set<Etiqueta> etiquetas = new HashSet<>();

  protected Actividad() {}

  public Actividad(
      Docente docente,
      String titulo,
      String descripcion,
      String tipo,
      LocalDate fecha,
      int maxParticipantes) {
    this.docente = docente;
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.tipo = tipo;
    this.fecha = fecha;
    this.maxParticipantes = maxParticipantes;
  }

  public Long getId() {
    return id;
  }

  public Docente getDocente() {
    return docente;
  }

  public String getTitulo() {
    return titulo;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public String getTipo() {
    return tipo;
  }

  public LocalDate getFecha() {
    return fecha;
  }

  public LocalDate getFechaPrevista() {
    return fecha;
  }

  public int getMaxParticipantes() {
    return maxParticipantes;
  }

  public Set<Etiqueta> getEtiquetas() {
    return etiquetas;
  }
}
