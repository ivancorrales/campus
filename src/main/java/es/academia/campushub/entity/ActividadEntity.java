package es.academia.campushub.entity;

import jakarta.persistence.*;
import lombok.Data;


import java.time.LocalDate;

@Entity
@Table(name="actividad")
@Data
public class ActividadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idActividad;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false,columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoActividad tipoActividad;

    @Column(nullable = false)
    private LocalDate fechaPrevista;

    @Column(name = "numero_maximo_participantes",
            nullable = false,
            check = @CheckConstraint(name = "ck_actividad_max_positivo",
                    constraint = "numero_maximo_participantes > 0"))
    private Integer numeroMaximoParticipantes;

    @Column(nullable = false)
    private DocenteEntity docenteResponsable;

}
