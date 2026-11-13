package es.academia.campushub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "docente")
@Getter
@Setter
@NoArgsConstructor
public class Docente {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String nombre;

  @Column(nullable = false, length = 150)
  private String apellidos;

  @Column(nullable = false, unique = true, length = 255)
  private String email;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "departamento_id", nullable = false)
  private Departamento departamento;
}
