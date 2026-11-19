# Decisiones técnicas

## 1. Qué guardamos en cada base de datos

**PostgreSQL:** departamentos, docentes, grupos, estudiantes, actividades, inscripciones, fichas de
contacto, etiquetas y la relación entre actividades y etiquetas.

**MongoDB:** los seguimientos de las actividades.

## 2. Por qué

**PostgreSQL para lo estructurado.** Estos datos tienen campos fijos y están muy relacionados entre
sí. Necesitamos claves foráneas, campos únicos (correos, nombres de departamento y etiqueta),
`CHECK` (aforo mayor que cero, fecha de nacimiento anterior a hoy) y transacciones.

El aforo y la inscripción única son justo el tipo de reglas que una base de datos relacional
garantiza. El método `inscribir` es transaccional y bloquea la fila de la actividad
(`PESSIMISTIC_WRITE`), así que dos peticiones a la vez no pueden coger la última plaza. Además,
existe un índice único `(actividad_id, estudiante_id)` como última barrera contra duplicados.

**MongoDB para el seguimiento.** Cada tipo de actividad genera información distinta (un taller
guarda lenguaje y ejercicios, una visita guarda empresa e incidencias, una charla guarda ponente y
enlaces) y pueden aparecer tipos nuevos. Cada registro se lee entero y solo se relaciona con su
actividad, así que encaja bien como documento. Se guarda tal como llega, sin migraciones ni columnas
nuevas, y el campo `schemaVersion` permite seguir leyendo registros antiguos si el formato cambia.

**Cómo se combinan.** El endpoint `/api/actividades/{id}/detalle` se resuelve en la capa de
servicio: la actividad, el docente, los inscritos y las etiquetas se leen de PostgreSQL, y los
seguimientos de MongoDB. Si no hay elementos, las listas salen vacías y no `null`.

## 3. Alternativas que hemos considerado

| Alternativa | Por qué no la elegimos |
|---|---|
| **Todo en PostgreSQL con `jsonb`** | Es una opción válida y más sencilla en consistencia, porque todo estaría en una sola transacción. Preferimos guardar la parte de estructura variable en un motor pensado para documentos, y el enunciado pide usar los dos motores de forma real. |
| **Todo en MongoDB** | Perderíamos claves foráneas, `UNIQUE` y `CHECK` a nivel de base de datos. Garantizar el aforo y las inscripciones únicas sería más difícil. |
| **Seguimientos dentro del documento de la actividad** | La actividad vive en PostgreSQL. Además, una actividad puede acumular muchos registros y un documento que crece sin control es un problema. |
| **`tipo` de actividad como `enum`** | Preferimos un texto de longitud limitada para poder añadir tipos sin cambiar el código ni el esquema. |

## 4. Riesgos de usar dos motores

Entre PostgreSQL y MongoDB no hay claves foráneas ni transacciones compartidas. Puede aparecer una
**inconsistencia** de estas formas:

1. Alguien borra una actividad en PostgreSQL (con `psql`, o con un borrado que se añada en el
   futuro) y sus seguimientos siguen en MongoDB con un `actividadId` que ya no existe: documentos
   huérfanos.
2. Se comprueba que la actividad existe y justo después se borra, antes de guardar el seguimiento.
3. Una operación que escribiera en los dos motores podría completarse en uno y fallar en el otro.

**Cómo reducimos el riesgo hoy:**

- Antes de guardar un seguimiento comprobamos en PostgreSQL que la actividad existe (404 si no).
- Cada operación escribe en un solo motor, así que ninguna puede quedar a medias entre los dos.
- Los seguimientos solo se añaden. La API no permite borrar actividades, y las claves foráneas de
  PostgreSQL impiden borrar una actividad que tenga inscripciones.
- Un seguimiento huérfano no rompe nada: el detalle solo busca los de una actividad que existe.

**Cómo lo reduciríamos más:**

- Usar borrado lógico (un campo «activa») en lugar de borrar actividades.
- Una tarea periódica que compruebe que cada `actividadId` de MongoDB existe en PostgreSQL.
- Si una operación tuviera que escribir en los dos motores, usar el patrón *outbox* o una saga.

## 5. Limitaciones de nuestra solución

- **No hay transacción distribuida:** lo anterior reduce el riesgo pero no lo elimina.
- **MongoDB sin validación de esquema ni índice propio:** la integridad de los seguimientos
  (`tipo` y `datos` obligatorios, actividad existente) se controla solo desde el servicio, y la
  consulta por `actividadId` recorre la colección. Con muchos seguimientos habría que crear un
  índice sobre `(actividadId, creadoEn)` y un validador `$jsonSchema` mínimo.
- **`actividad_etiqueta` sin índice específico sobre `etiqueta_id`:** la consulta de actividades
  por etiqueta se apoya solo en la clave primaria `(actividad_id, etiqueta_id)`.
- **`ddl-auto=update`** solo vale para desarrollo; en producción habría que versionar el esquema
  con Flyway o Liquibase, y `update` no añade los `CHECK` a tablas que ya existían.
- No hay autenticación ni paginación en los listados.
- No hemos usado Redis (era opcional).

## 6. Decisiones que cambiaron respecto al diseño inicial
- Pasamos de comprobar las reglas solo en el servicio a reforzarlas también con `CHECK`, `UNIQUE`
  y claves foráneas en la base de datos.
- Configuramos `maven-failsafe-plugin` con `reuseForks=false` para que cada clase de test de
  integración arranque sus propios contenedores de Testcontainers.