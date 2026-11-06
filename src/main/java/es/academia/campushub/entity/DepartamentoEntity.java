package es.academia.campushub.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "departamento",
        uniqueConstraints = @UniqueConstraint(name = "uk_departamento_nombre", columnNames = "nombre_departamento"))
@Data
public class DepartamentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDepartamento;

    @Column(nullable = false, length = 100)
    private String nombreDepartamento;
}
