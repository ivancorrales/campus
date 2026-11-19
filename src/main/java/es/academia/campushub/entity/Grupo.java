package es.academia.campushub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "grupo",
    indexes = @Index(name = "ix_grupo_tutor", columnList = "tutor_docente_id"),
    check = {
      @CheckConstraint(name = "ck_grupo_nombre", constraint = "length(trim(nombre)) > 0"),
      @CheckConstraint(name = "ck_grupo_nivel", constraint = "length(trim(nivel)) > 0")
    })
@Getter
@Setter
public class Grupo {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String nombre;

  @Column(nullable = false, length = 50)
  private String nivel;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "tutor_docente_id", nullable = false)
  private Docente tutor;

  public Grupo() {}
}
