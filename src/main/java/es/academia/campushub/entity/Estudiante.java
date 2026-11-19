package es.academia.campushub.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Check(constraints = "fecha_nacimiento < CURRENT_DATE")
@Table(
    name = "estudiantes",
    uniqueConstraints = @UniqueConstraint(name = "uk_estudiante_correo", columnNames = "correo"),
    indexes = @Index(name = "idx_estudiante_grupo", columnList = "grupo_id"))
public class Estudiante {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "estudiante_id")
  private Long id;

  @Column(nullable = false, length = 100)
  private String nombre;

  @Column(nullable = false, length = 150)
  private String apellidos;

  @Column(name = "correo", nullable = false, length = 254)
  private String correo;

  @Column(name = "fecha_nacimiento", nullable = false)
  private LocalDate fechaNacimiento;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "grupo_id", nullable = false)
  @OnDelete(action = OnDeleteAction.RESTRICT)
  private Grupo grupo;

  protected Estudiante() {}

  public Estudiante(
      String nombre, String apellidos, String correo, LocalDate fechaNacimiento, Grupo grupo) {
    this.nombre = nombre;
    this.apellidos = apellidos;
    this.correo = correo;
    this.fechaNacimiento = fechaNacimiento;
    this.grupo = grupo;
  }

  public Long getId() {
    return id;
  }

  public String getNombre() {
    return nombre;
  }

  public String getApellidos() {
    return apellidos;
  }

  public String getCorreo() {
    return correo;
  }

  public String getEmail() {
    return correo;
  }

  public LocalDate getFechaNacimiento() {
    return fechaNacimiento;
  }

  public Grupo getGrupo() {
    return grupo;
  }
}
