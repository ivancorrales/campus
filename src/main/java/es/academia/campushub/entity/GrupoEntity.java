package es.academia.campushub.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "grupo")
@Data
public class GrupoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idGrupo;

    @Column(nullable = false)
    private String nombreGrupo;

    @Column(nullable = false)
    private String nivelGrupo;

    @Column
    private DocenteEntity docente;



}
