package es.academia.campushub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "contactos")
@Getter
@Setter
@NoArgsConstructor
public class Contacto {

  @Id
  @Column(name = "estudiante_id")
  private Long estudianteId;

  @MapsId
  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "estudiante_id", nullable = false)
  private Estudiante estudiante;

  @Column(name = "tutor_legal", nullable = false, length = 200)
  private String tutorLegal;

  @Column(nullable = false, length = 40)
  private String telefono;

  @Column(nullable = false, length = 254)
  private String email;

  @Column(nullable = false)
  private Boolean autorizado;

  @Column(name = "fecha_autorizacion")
  private LocalDate fechaAutorizacion;
}
