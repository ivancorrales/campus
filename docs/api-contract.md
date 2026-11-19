# Contrato HTTP — CampusHub

Todos los cuerpos son JSON con `Content-Type: application/json`.
Campos `fechaNacimiento`, `fechaPrevista` y `fechaAutorizacion` son strings ISO `YYYY-MM-DD`.
`creadoEn` y `fechaInscripcion` de las respuestas son strings ISO-8601 de fecha/hora.

| Método | Ruta | DTO de entrada | DTO de salida | Éxito |
|---|---|---|---|---|
| POST | `/api/departamentos` | CreateDepartamentoRequest | DepartamentoResponse | 201 |
| POST | `/api/docentes` | CreateDocenteRequest | DocenteResponse | 201 |
| POST | `/api/grupos` | CreateGrupoRequest | GrupoResponse | 201 |
| POST | `/api/estudiantes` | CreateEstudianteRequest | EstudianteResponse | 201 |
| GET | `/api/estudiantes/{id}` | — | EstudianteResponse | 200 |
| POST | `/api/actividades` | CreateActividadRequest | ActividadResponse | 201 |
| GET | `/api/actividades/{id}` | — | ActividadResponse | 200 |
| POST | `/api/actividades/{id}/inscripciones` | CreateInscripcionRequest | InscripcionResponse | 201 |
| GET | `/api/actividades/{id}/inscripciones` | — | EstudianteResponse[] | 200 |
| POST | `/api/actividades/{id}/seguimientos` | CreateSeguimientoRequest | SeguimientoResponse | 201 |
| GET | `/api/actividades/{id}/detalle` | — | ActividadDetalleResponse | 200 |
| GET | `/api/estudiantes/{id}/actividades` | — | ActividadHistorialResponse[] | 200 |
| PUT | `/api/estudiantes/{id}/contacto` | UpsertContactoRequest | ContactoResponse | 200 |
| GET | `/api/estudiantes/{id}/contacto` | — | ContactoResponse | 200 |
| POST | `/api/etiquetas` | CreateEtiquetaRequest | EtiquetaResponse | 201 |
| PUT | `/api/actividades/{id}/etiquetas/{etiquetaId}` | — | — | 204 |
| GET | `/api/etiquetas/{id}/actividades` | — | ActividadResponse[] | 200 |

## DTO y persistencia

Los DTO fijan el contrato del cliente, **no** el diseño relacional o documental. El alumno
puede elegir entidades, colecciones, tablas intermedias y tipos internos. Los identificadores
visibles en la API son `Long` salvo los registros de seguimiento, cuyo ID es `String`.

La propiedad `datos` de `CreateSeguimientoRequest` es un objeto JSON de claves libres, para
permitir distintos seguimientos. Se debe conservar su estructura y permitir recuperarla.

## Errores

- 400: datos malformados o inválidos que no tienen sentido como conflicto con datos existentes.
- 404: estudiante, docente, actividad o etiqueta requeridos inexistentes.
- 409: inscripción duplicada, falta de plazas o violación de unicidad de recursos existentes.
- 501: stub todavía no implementado (solo durante el desarrollo).

En el detalle: una actividad sin inscripciones, etiquetas o seguimientos debe devolver listas
vacías, no `null`. Las plazas disponibles se calculan como capacidad - ocupadas.

## Decisión deliberada: DTO sin Bean Validation exhaustiva

No se han añadido `@NotNull`, `@NotBlank`, `@Size`, `@Email` ni `@Min` para que el
alumno tenga que implementar tanto reglas de negocio como restricciones de persistencia.
El parser JSON puede rechazar entradas estructuralmente mal formadas, pero eso no sustituye
la protección del almacenamiento frente a inserciones inválidas.

## Notas de la implementación

- Hemos respetado todos los endpoints, DTO y códigos de éxito de la tabla de arriba.
- También devolvemos 404 si el grupo o el departamento que se indica no existe.
- `GET /api/estudiantes/{id}/contacto` devuelve 404 si el estudiante existe pero no tiene ficha.
- `PUT /api/actividades/{id}/etiquetas/{etiquetaId}` se puede repetir y siempre devuelve 204.
- Los inscritos de una actividad salen ordenados por fecha de inscripción, el historial de un
  estudiante por fecha prevista de la actividad, las actividades de una etiqueta por id y los
  seguimientos por fecha de creación.
