package es.academia.campushub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "grupo",
        uniqueConstraints = @UniqueConstraint(name = "uk_grupo_nombre", columnNames = "nombre"))
public class Grupo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "nivel", nullable = false, length = 50)
    private String nivel;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "tutor_docente_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_grupo_tutor"))
    private Docente tutor;

    protected Grupo() {}

    public Grupo(String nombre, String nivel, Docente tutor) {
        this.nombre = nombre;
        this.nivel = nivel;
        this.tutor = tutor;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getNivel() {
        return nivel;
    }

    public Docente getTutor() {
        return tutor;
    }
}

