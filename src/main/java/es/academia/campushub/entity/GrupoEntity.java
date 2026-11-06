package es.academia.campushub.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "grupo")
@Data
public class GrupoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_grupo;

    @Column(nullable = false)
    private String nombre_grupo;

    @Column(nullable = false)
    private Integer nivel_grupo;

    @Column
    private DocenteEntity docente;



}
