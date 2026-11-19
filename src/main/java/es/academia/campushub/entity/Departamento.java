package es.academia.campushub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(
    name = "departamento",
    uniqueConstraints = @UniqueConstraint(name = "uk_departamento_nombre", columnNames = "nombre"),
    check =
        @CheckConstraint(name = "ck_departamento_nombre", constraint = "length(trim(nombre)) > 0"))
public class Departamento {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  @NotBlank(message = "El nombre del departamento no puede estar vacío")
  @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
  private String nombre;

  public Departamento() {}
}
