package es.academia.campushub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "contacto_estudiante",
    check = {
      @CheckConstraint(
          name = "ck_contacto_tutor_legal",
          constraint = "length(trim(tutor_legal)) > 0"),
      @CheckConstraint(name = "ck_contacto_telefono", constraint = "length(trim(telefono)) > 0"),
      @CheckConstraint(name = "ck_contacto_email", constraint = "position('@' in email) > 1")
    })
@Getter
@Setter
public class ContactoEstudiante {
  @Id
  @Column(name = "estudiante_id")
  private Long id;

  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @MapsId
  @JoinColumn(name = "estudiante_id")
  private Estudiante estudiante;

  @Column(name = "tutor_legal", nullable = false, length = 160)
  @NotBlank(message = "El tutor legal no puede estar vacío")
  @Size(max = 160, message = "El tutor legal no puede superar los 160 caracteres")
  private String tutorLegal;

  @Column(nullable = false, length = 20)
  @NotBlank(message = "El teléfono no puede estar vacío")
  @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres")
  private String telefono;

  @Column(nullable = false, length = 254)
  @NotBlank(message = "El email no puede estar vacío")
  @Email(message = "Debe proporcionar un formato de correo electrónico válido")
  @Size(max = 254, message = "El email no puede superar los 254 caracteres")
  private String email;

  @Column(nullable = false)
  private boolean autorizado;

  @Column(name = "fecha_autorizacion")
  private LocalDate fechaAutorizacion;

  public ContactoEstudiante() {}
}
