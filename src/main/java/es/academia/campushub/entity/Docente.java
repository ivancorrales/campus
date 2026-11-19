package es.academia.campushub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(
    name = "docente",
    uniqueConstraints = @UniqueConstraint(name = "uk_docente_email", columnNames = "email"),
    indexes = @Index(name = "ix_docente_departamento", columnList = "departamento_id"),
    check = {
      @CheckConstraint(name = "ck_docente_nombre", constraint = "length(trim(nombre)) > 0"),
      @CheckConstraint(name = "ck_docente_apellidos", constraint = "length(trim(apellidos)) > 0"),
      @CheckConstraint(name = "ck_docente_email", constraint = "position('@' in email) > 1")
    })
public class Docente {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 80)
  @NotBlank(message = "El nombre no puede estar vacío")
  @Size(max = 80, message = "El nombre no puede superar los 80 caracteres")
  private String nombre;

  @Column(nullable = false, length = 120)
  @NotBlank(message = "Los apellidos no pueden estar vacíos")
  @Size(max = 120, message = "Los apellidos no pueden superar los 120 caracteres")
  private String apellidos;

  @Column(nullable = false, length = 254)
  @NotBlank(message = "El email no puede estar vacío")
  @Email(message = "Debe proporcionar un formato de correo electrónico válido")
  @Size(max = 254, message = "El email no puede superar los 254 caracteres")
  private String email;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "departamento_id", nullable = false)
  private Departamento departamento;

  public Docente() {}
}
