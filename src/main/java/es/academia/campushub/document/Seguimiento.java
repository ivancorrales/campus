package es.academia.campushub.document;

import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "seguimientos")
public class Seguimiento {

  @Id private String id;

  @Field("actividad_id")
  private Long actividadId;

  @Field("tipo")
  private String tipo;

  @Field("datos")
  private Map<String, Object> datos;

  @Field("creado_en")
  private LocalDateTime creadoEn;

  protected Seguimiento() {}

  public Seguimiento(
      Long actividadId, String tipo, Map<String, Object> datos, LocalDateTime creadoEn) {
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

  public LocalDateTime getCreadoEn() {
    return creadoEn;
  }
}
