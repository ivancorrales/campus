package es.academia.campushub.entity;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name="docente")
@Data
public class DocenteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDocente;

    @Column(nullable = false)
    private String nombreDocente;

    @Column(nullable = false)
    private String apellidoDocente;

    @Column(nullable = false, unique = true)
    private String correoDocente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "departamento_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_docente_departamento"))
    private DepartamentoEntity departamento;


}
