package es.academia.campushub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "estudiantes",
    uniqueConstraints = @UniqueConstraint(name = "uk_estudiante_email", columnNames = "email"),
    indexes = @Index(name = "idx_estudiante_grupo", columnList = "grupo_id"))
@Getter
@Setter
@NoArgsConstructor
public class Estudiante {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String nombre;

  @Column(nullable = false, length = 150)
  private String apellidos;

  @Column(nullable = false, length = 254)
  private String email;

  @Column(name = "fecha_nacimiento", nullable = false)
  private LocalDate fechaNacimiento;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "grupo_id", nullable = false)
  private Grupo grupo;
}
