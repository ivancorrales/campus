package es.academia.campushub.document;

import java.time.Instant;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Registro de seguimiento de una actividad. Se guarda en MongoDB porque el contenido de {@code
 * datos} es libre y distinto según el tipo de seguimiento. {@code actividadId} referencia a la
 * actividad en PostgreSQL (no existe FK entre bases de datos: la existencia se comprueba en el
 * servicio).
 */
@Document(collection = "seguimientos")
@CompoundIndex(name = "idx_actividad_creado", def = "{'actividadId': 1, 'creadoEn': 1}")
public class Seguimiento {
    @Id private String id;
    private Long actividadId;
    private String tipo;
    private Map<String, Object> datos;
    private Instant creadoEn;

    protected Seguimiento() {}

    public Seguimiento(Long actividadId, String tipo, Map<String, Object> datos, Instant creadoEn) {
        this.actividadId = actividadId;
        this.tipo = tipo;
        this.datos = datos;
        this.creadoEn = creadoEn;
    }

    public String getId() {
        return id;
    }

    public Long getActividadId() {
        return actividadId;
    }

    public String getTipo() {
        return tipo;
    }

    public Map<String, Object> getDatos() {
        return datos;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }
}