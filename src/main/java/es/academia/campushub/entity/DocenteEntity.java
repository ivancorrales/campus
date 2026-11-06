package es.academia.campushub.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Generated;

@Entity
@Table(name="Docente")
@Data
public class DocenteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_docente;

    @Column(nullable = false)
    private String nombre_docente;

    @Column(nullable = false)
    private String apellido_docente;

    @Column(nullable = false, unique = true)
    private String correo_docente;

    @Column(nullable = false)
    private String departamento;


}
