package es.academia.campushub.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(
    name = "docentes",
    uniqueConstraints = @UniqueConstraint(name = "uk_docente_correo", columnNames = "correo"),
    indexes = @Index(name = "idx_docente_departamento", columnList = "departamento_id"))
public class Docente {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "docente_id")
  private Long id;

  @Column(nullable = false, length = 100)
  private String nombre;

  @Column(nullable = false, length = 150)
  private String apellido;

  @Column(name = "correo", nullable = false, length = 254)
  private String correo;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "departamento_id", nullable = false)
  @OnDelete(action = OnDeleteAction.RESTRICT)
  private Departamento departamento;

  protected Docente() {}

  public Docente(String correo, String nombre, String apellido, Departamento departamento) {
    this.correo = correo;
    this.nombre = nombre;
    this.apellido = apellido;
    this.departamento = departamento;
  }

  public Long getId() {
    return id;
  }

  public String getNombre() {
    return nombre;
  }

  public String getApellido() {
    return apellido;
  }

  public String getApellidos() {
    return apellido;
  }

  public String getCorreo() {
    return correo;
  }

  public String getEmail() {
    return correo;
  }

  public Departamento getDepartamento() {
    return departamento;
  }
}
