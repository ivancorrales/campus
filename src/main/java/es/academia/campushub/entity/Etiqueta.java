package es.academia.campushub.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "etiquetas",
    uniqueConstraints = @UniqueConstraint(name = "uk_etiqueta_nombre", columnNames = "tipo"))
public class Etiqueta {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "etiquetas_id")
  private Long id;

  @Column(name = "tipo", nullable = false, length = 50)
  private String tipo;

  protected Etiqueta() {}

  public Etiqueta(String tipo) {
    this.tipo = tipo;
  }

  public Long getId() {
    return id;
  }

  public String getTipo() {
    return tipo;
  }

  public String getNombre() {
    return tipo;
  }
}
