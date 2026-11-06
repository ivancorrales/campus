package es.academia.campushub.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "departamento",
        uniqueConstraints = @UniqueConstraint(name = "uk_departamento_nombre", columnNames = "nombre"))
@Data
public class DepartamentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_departamento;

    @Column(nullable = false, length = 100)
    private String nombre_departamento;
}
