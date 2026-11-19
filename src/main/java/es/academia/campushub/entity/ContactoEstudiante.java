package es.academia.campushub.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/** Ficha de contacto del estudiante: relación 1:1, no puede existir sin el estudiante. */
@Entity
@Table(
    name = "contacto_estudiante",
    uniqueConstraints =
        @UniqueConstraint(name = "uk_contacto_estudiante", columnNames = "estudiante_id"))
public class ContactoEstudiante {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "estudiante_id", nullable = false, unique = true)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Estudiante estudiante;

  @Column(name = "nombre_tutor_legal", nullable = false, length = 200)
  private String tutorLegal;

  @Column(length = 30)
  private String telefono;

  @Column(name = "correo", length = 254)
  private String email;

  @Column(nullable = false)
  private boolean autorizado;

  @Column(name = "fecha_autorizacion")
  private LocalDate fechaAutorizacion;

  protected ContactoEstudiante() {}

  public ContactoEstudiante(Estudiante estudiante) {
    this.estudiante = estudiante;
  }

  public void actualizar(
      String tutorLegal,
      String telefono,
      String email,
      boolean autorizado,
      LocalDate fechaAutorizacion) {
    this.tutorLegal = tutorLegal;
    this.telefono = telefono;
    this.email = email;
    this.autorizado = autorizado;
    this.fechaAutorizacion = fechaAutorizacion;
  }

  public Long getId() {
    return id;
  }

  public Estudiante getEstudiante() {
    return estudiante;
  }

  public String getTutorLegal() {
    return tutorLegal;
  }

  public String getTelefono() {
    return telefono;
  }

  public String getEmail() {
    return email;
  }

  public boolean isAutorizado() {
    return autorizado;
  }

  public LocalDate getFechaAutorizacion() {
    return fechaAutorizacion;
  }
}
