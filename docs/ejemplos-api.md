# Ejemplos para probar la API

Con la aplicación arrancada en `http://localhost:8080`.

La forma más fácil es **Swagger**: <http://localhost:8080/swagger-ui.html>. Se abre un endpoint,
se pulsa *Try it out*, se cambia el JSON y se pulsa *Execute*.

También se puede con `curl` (en Git Bash). Los ids (`1`, `2`...) son los que salen con la base de
datos vacía. Si ya hay datos, hay que usar los que devuelva cada paso.

## Flujo completo

```bash
# 1. Departamento
curl -X POST localhost:8080/api/departamentos -H "Content-Type: application/json" \
  -d '{"nombre":"Informática"}'

# 2. Docente
curl -X POST localhost:8080/api/docentes -H "Content-Type: application/json" \
  -d '{"nombre":"Ana","apellidos":"García","email":"ana@campus.test","departamentoId":1}'

# 3. Grupo (el docente es el tutor)
curl -X POST localhost:8080/api/grupos -H "Content-Type: application/json" \
  -d '{"nombre":"DAM2","nivel":"2 DAM","tutorDocenteId":1}'

# 4. Estudiante
curl -X POST localhost:8080/api/estudiantes -H "Content-Type: application/json" \
  -d '{"nombre":"Laura","apellidos":"Ruiz","email":"laura@campus.test","fechaNacimiento":"2004-04-01","grupoId":1}'

# 5. Actividad con 1 plaza
curl -X POST localhost:8080/api/actividades -H "Content-Type: application/json" \
  -d '{"titulo":"Visita","descripcion":"Visita técnica","tipo":"VISITA","fechaPrevista":"2027-05-01","maxParticipantes":1,"docenteId":1}'

# 6. Inscribir al estudiante
curl -X POST localhost:8080/api/actividades/1/inscripciones -H "Content-Type: application/json" \
  -d '{"estudianteId":1}'

# 7. Seguimiento (se guarda en MongoDB)
curl -X POST localhost:8080/api/actividades/1/seguimientos -H "Content-Type: application/json" \
  -d '{"tipo":"VISITA","datos":{"empresa":"Acme","observaciones":["aprendizaje","visita"]}}'

# 8. Ficha de contacto (con PUT se crea, y si se repite se actualiza)
curl -X PUT localhost:8080/api/estudiantes/1/contacto -H "Content-Type: application/json" \
  -d '{"tutorLegal":"Julia Ruiz","telefono":"600000000","email":"julia@campus.test","autorizado":true,"fechaAutorizacion":"2026-10-01"}'

# 9. Etiqueta y asociarla a la actividad (el PUT se puede repetir)
curl -X POST localhost:8080/api/etiquetas -H "Content-Type: application/json" \
  -d '{"nombre":"tecnologia"}'
curl -X PUT localhost:8080/api/actividades/1/etiquetas/1

# 10. Consultas
curl localhost:8080/api/actividades/1
curl localhost:8080/api/actividades/1/inscripciones
curl localhost:8080/api/actividades/1/detalle
curl localhost:8080/api/estudiantes/1
curl localhost:8080/api/estudiantes/1/actividades
curl localhost:8080/api/estudiantes/1/contacto
curl localhost:8080/api/etiquetas/1/actividades
```

En PowerShell las comillas dan problemas con `curl`. Se puede usar así:

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/departamentos `
  -ContentType "application/json" -Body '{"nombre":"Informática"}'
```

## Qué devuelve el detalle de una actividad

`/api/actividades/1/detalle` junta datos de PostgreSQL (actividad, responsable, inscritos,
etiquetas y plazas) y de MongoDB (seguimientos). Los campos son `actividad`, `responsable`,
`plazasOcupadas`, `plazasDisponibles`, `estudiantes`, `etiquetas` y `seguimientos`. Si no hay
nada, las listas salen vacías (`[]`) y no `null`.

## Errores que hemos probado

| Petición | Resultado |
|---|---|
| Crear estudiante con un JSON roto (`{`) | 400 |
| Crear estudiante con `"email":"sin-arroba"` | 400 |
| Crear estudiante con fecha de nacimiento futura | 400 |
| Crear actividad con `"maxParticipantes":0` | 400 |
| Seguimiento sin `datos` | 400 |
| `GET /api/actividades/99999999` | 404 |
| Crear docente con un `departamentoId` que no existe | 404 |
| `GET /api/estudiantes/1/contacto` sin ficha | 404 |
| Crear un departamento con un nombre repetido | 409 |
| Crear docente o estudiante con un email repetido | 409 |
| Inscribir dos veces al mismo estudiante | 409 |
| Inscribir un segundo estudiante en una actividad de 1 plaza | 409 |

Todos los errores tienen este formato:

```json
{
  "code": "NOT_FOUND",
  "message": "Actividad no encontrada",
  "timestamp": "2026-11-18T12:00:00Z"
}
```

Los códigos que salen son `BAD_JSON`, `INVALID_INPUT`, `NOT_FOUND`, `CONFLICT` y `DATA_INTEGRITY`.