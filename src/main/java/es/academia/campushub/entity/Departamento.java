package es.academia.campushub.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "departamentos",
    uniqueConstraints = @UniqueConstraint(name = "uk_departamento_nombre", columnNames = "nombre"))
public class Departamento {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "departamento_id")
  private Long id;

  @Column(nullable = false, length = 100)
  private String nombre;

  protected Departamento() {}

  public Departamento(String nombre) {
    this.nombre = nombre;
  }

  public Long getId() {
    return id;
  }

  public String getNombre() {
    return nombre;
  }
}
