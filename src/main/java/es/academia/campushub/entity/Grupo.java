package es.academia.campushub.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "grupos", indexes = @Index(name = "idx_grupo_tutor", columnList = "docente_id"))
public class Grupo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "grupo_id")
  private Long id;

  @Column(nullable = false, length = 100)
  private String nombre;

  @Column(length = 50)
  private String nivel;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "docente_id", nullable = false)
  @OnDelete(action = OnDeleteAction.RESTRICT)
  private Docente tutor;

  protected Grupo() {}

  public Grupo(String nombre, String nivel, Docente tutor) {
    this.nombre = nombre;
    this.nivel = nivel;
    this.tutor = tutor;
  }

  public Long getId() {
    return id;
  }

  public String getNombre() {
    return nombre;
  }

  public String getNivel() {
    return nivel;
  }

  public Docente getTutor() {
    return tutor;
  }
}
