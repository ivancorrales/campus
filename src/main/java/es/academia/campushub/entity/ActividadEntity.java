package es.academia.campushub.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Check;

import java.time.LocalDate;

@Entity
@Table(name="Actividad")
@Data
public class ActividadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_actividad;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private TipoActividad tipoActividad;

    @Column(nullable = false)
    private LocalDate fechaPrevista;

    @Column(name = "numero_maximo_participantes",
            nullable = false,
            check = @CheckConstraint(name = "ck_actividad_max_positivo",
                    constraint = "numero_maximo_participantes > 0"))
    private Integer numeroMaximoParticipantes;

    @Column(nullable = false)
    private DocenteEntity docente_responsable;

}
