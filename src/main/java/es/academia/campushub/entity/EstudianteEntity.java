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

    @Column(nullable = false, unique = true,check = @CheckConstraint(name = "ck_estudiante_nacimiento_pasado", constraint = "fecha_nacimiento_estudiante < CURRENT_DATE"))
    private String correoEstudiante;

    @Column(nullable = false)
    private LocalDate fechaNacimientoEstudiante;

    @Column(nullable = false)
    private GrupoEntity grupoEstudiante;
}
