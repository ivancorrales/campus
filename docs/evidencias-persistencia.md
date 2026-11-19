# Evidencias de persistencia

Cómo comprobar que los datos están realmente guardados en las bases de datos y no en memoria.

Primero hay que tener la aplicación arrancada y algunos datos creados (por ejemplo, con el flujo
de [`ejemplos-api.md`](ejemplos-api.md)).

## 1. PostgreSQL

Entrar en la base de datos:

```bash
docker compose exec postgres psql -U campushub -d campushub
```

Ver los datos:

```sql
\dt                                -- lista las tablas
\d estudiante                      -- estructura, restricciones e índices de una tabla
\d inscripcion

SELECT * FROM departamento;
SELECT * FROM docente;
SELECT * FROM estudiante;
SELECT * FROM actividad;
SELECT * FROM inscripcion;
SELECT * FROM actividad_etiqueta;

-- Estudiantes inscritos en la actividad 1, con su fecha de inscripción
SELECT e.nombre, e.apellidos, i.fecha_inscripcion
FROM inscripcion i JOIN estudiante e ON e.id = i.estudiante_id
WHERE i.actividad_id = 1;
```

### Comprobar que rechaza datos inválidos

Estas órdenes se ejecutan directamente en `psql`, sin pasar por la API. Cada una debe fallar. Hay
que ajustar los ids a datos que existan en la base de datos.

```sql
-- Aforo cero: viola ck_actividad_max
INSERT INTO actividad (titulo, descripcion, tipo, fecha_prevista, max_participantes, docente_id)
VALUES ('X', 'Y', 'TALLER', '2027-01-01', 0, 1);

-- Fecha de nacimiento futura: viola ck_estudiante_nacimiento
INSERT INTO estudiante (nombre, apellidos, email, fecha_nacimiento, grupo_id)
VALUES ('A', 'B', 'a@b.test', '2999-01-01', 1);

-- Inscripción repetida: viola uk_inscripcion_actividad_estudiante
INSERT INTO inscripcion (actividad_id, estudiante_id, fecha_inscripcion)
VALUES (1, 1, now()), (1, 1, now());

-- Actividad inexistente: viola la clave foránea
INSERT INTO inscripcion (actividad_id, estudiante_id, fecha_inscripcion)
VALUES (99999, 1, now());

-- Borrar un docente que tiene actividades o grupos: lo impide la clave foránea
DELETE FROM docente WHERE id = 1;
```

## 2. MongoDB

Entrar en la base de datos:

```bash
docker compose exec mongo mongosh campushub
```

Ver los datos:

```javascript
show collections                          // debe aparecer "seguimientos"
db.seguimientos.find().pretty()           // todos los seguimientos
db.seguimientos.find({ actividadId: 1 })  // los de una actividad
db.seguimientos.countDocuments()          // cuántos hay
```

Cada documento conserva la estructura exacta que se envió en `datos`, aunque sea distinta entre
tipos de seguimiento. Spring Data puede añadir un campo `_class` con el nombre de la clase Java:
es normal.

Ejemplo de seguimientos con estructuras diferentes en la misma colección:

```javascript
db.seguimientos.find({}, { tipo: 1, datos: 1, _id: 0 })
```

## 3. Persistencia tras reiniciar

1. Crear datos con la API (o con el flujo de `ejemplos-api.md`).
2. Parar la aplicación con Ctrl + C.
3. Volver a arrancarla con `mvn spring-boot:run`.
4. Comprobar que los datos siguen ahí, de cualquiera de estas formas:
    - `GET http://localhost:8080/api/actividades/1/detalle`
    - `SELECT * FROM actividad;` en `psql`
    - `db.seguimientos.find()` en `mongosh`

Los datos sobreviven porque PostgreSQL y MongoDB los guardan en volúmenes de Docker
(`postgres_data` y `mongo_data`), no en la memoria de la aplicación.

Atención: `docker compose down -v` borra los volúmenes y, con ellos, todos los datos.