package es.academia.campushub.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;

import java.time.LocalDate;

@Entity
@Table(name="estudiante")
@Data
public class EstudianteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_estudiante;

    @Column(nullable = false)
    private String nombre_estudiante;

    @Column(nullable = false)
    private String apellido_estudiante;

    @Column(nullable = false, unique = true)
    private String correo_estudiante;


    private LocalDate fecha_nacimiento_estudiante;

    @Column(nullable = false)
    private GrupoEntity grupo_estudiante;
}
