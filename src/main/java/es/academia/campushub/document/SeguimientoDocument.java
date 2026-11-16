package es.academia.campushub.document;

import java.time.Instant;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Registro de seguimiento de una actividad. Cada tipo de seguimiento guarda sus propios datos. */
@Document(collection = "seguimientos")
@Getter
@Setter
@NoArgsConstructor
public class SeguimientoDocument {

  @Id private String id;

  /** Identificador de la actividad en PostgreSQL (no hay clave foránea entre motores). */
  private Long actividadId;

  private String tipo;

  /** Estructura libre: se guarda exactamente como llega. */
  private Map<String, Object> datos;

  private Instant creadoEn;
}
