package es.academia.campushub.entity;

import jakarta.persistence.*;
import lombok.Data;


import java.time.LocalDate;

@Entity
@Table(name="estudiante")
@Data
public class EstudianteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEstudiante;

    @Column(nullable = false)
    private String nombreEstudiante;

    @Column(nullable = false)
    private String apellidoEstudiante;

    @Column(nullable = false, unique = true, length = 150)
    private String correoEstudiante;

    @Column(nullable = false,
            check = @CheckConstraint(name = "ck_estudiante_nacimiento_pasado",
                    constraint = "fecha_nacimiento_estudiante < CURRENT_DATE"))
    private LocalDate fechaNacimientoEstudiante;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grupo_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_estudiante_grupo"))
    private GrupoEntity grupoEstudiante;
}
