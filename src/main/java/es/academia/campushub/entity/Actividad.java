package es.academia.campushub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "actividad")
@Check(constraints = "max_participantes > 0")
public class Actividad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "descripcion", length = 2000)
    private String descripcion;

    @Column(name = "tipo", nullable = false, length = 50)
    private String tipo;

    @Column(name = "fecha_prevista", nullable = false)
    private LocalDate fechaPrevista;

    @Column(name = "max_participantes", nullable = false)
    private int maxParticipantes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "docente_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_actividad_docente"))
    private Docente docente;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "actividad_etiqueta",
            joinColumns =
            @JoinColumn(
                    name = "actividad_id",
                    foreignKey = @ForeignKey(name = "fk_actividad_etiqueta_actividad")),
            inverseJoinColumns =
            @JoinColumn(
                    name = "etiqueta_id",
                    foreignKey = @ForeignKey(name = "fk_actividad_etiqueta_etiqueta")))
    private Set<Etiqueta> etiquetas = new LinkedHashSet<>();

    protected Actividad() {}

    public Actividad(
            String titulo,
            String descripcion,
            String tipo,
            LocalDate fechaPrevista,
            int maxParticipantes,
            Docente docente) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.fechaPrevista = fechaPrevista;
        this.maxParticipantes = maxParticipantes;
        this.docente = docente;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public LocalDate getFechaPrevista() {
        return fechaPrevista;
    }

    public int getMaxParticipantes() {
        return maxParticipantes;
    }

    public Docente getDocente() {
        return docente;
    }

    public Set<Etiqueta> getEtiquetas() {
        return etiquetas;
    }

    /** Asocia la etiqueta; al ser un {@link Set}, repetir la operación no duplica la fila. */
    public boolean anadirEtiqueta(Etiqueta etiqueta) {
        return etiquetas.add(etiqueta);
    }
}