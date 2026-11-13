package es.academia.campushub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import org.hibernate.annotations.Check;

/** Ficha de contacto (relación 1:1 con el estudiante, garantizada por UNIQUE en la FK). */
@Entity
@Table(name = "contacto_estudiante")
@Check(constraints = "autorizado = false OR fecha_autorizacion IS NOT NULL")
public class ContactoEstudiante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "estudiante_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_contacto_estudiante"))
    private Estudiante estudiante;

    @Column(name = "tutor_legal", nullable = false, length = 200)
    private String tutorLegal;

    @Column(name = "telefono", length = 30)
    private String telefono;

    @Column(name = "email", length = 254)
    private String email;

    @Column(name = "autorizado", nullable = false)
    private boolean autorizado;

    @Column(name = "fecha_autorizacion")
    private LocalDate fechaAutorizacion;

    protected ContactoEstudiante() {}

    public ContactoEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    /** Sustituye todos los datos editables de la ficha (semántica PUT). */
    public void actualizar(
            String tutorLegal,
            String telefono,
            String email,
            boolean autorizado,
            LocalDate fechaAutorizacion) {
        this.tutorLegal = tutorLegal;
        this.telefono = telefono;
        this.email = email;
        this.autorizado = autorizado;
        this.fechaAutorizacion = fechaAutorizacion;
    }

    public Long getId() {
        return id;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public String getTutorLegal() {
        return tutorLegal;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAutorizado() {
        return autorizado;
    }

    public LocalDate getFechaAutorizacion() {
        return fechaAutorizacion;
    }
}