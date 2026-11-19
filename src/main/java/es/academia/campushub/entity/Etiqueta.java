package es.academia.campushub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "etiqueta",
    uniqueConstraints = @UniqueConstraint(name = "uk_etiqueta_nombre", columnNames = "nombre"),
    check = @CheckConstraint(name = "ck_etiqueta_nombre", constraint = "length(trim(nombre)) > 0"))
@Getter
@Setter
public class Etiqueta {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String nombre;

  public Etiqueta() {}
}
