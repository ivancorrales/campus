# CampusHub

Proyecto de 2.º DAM. API REST para gestionar las actividades complementarias de un centro
(talleres, charlas, visitas...), con sus docentes, grupos, estudiantes e inscripciones.

Usamos **PostgreSQL** para los datos estructurados y **MongoDB** para los seguimientos de las
actividades. Los controllers y los DTO del proyecto base no los hemos modificado.

## Requisitos previos

- JDK 21
- Maven 3.9 o superior
- Docker Desktop (o Docker Engine) con Docker Compose v2

## Arrancar la aplicación

```bash
mvn spring-boot:run
```

Spring Boot detecta el `compose.yaml` y levanta PostgreSQL, MongoDB y Redis antes de arrancar.
Redis no lo usamos (era opcional). Si preferimos gestionar los contenedores a mano:

```bash
docker compose up -d
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.docker.compose.enabled=false
```

Con la aplicación arrancada, Swagger UI está en <http://localhost:8080/swagger-ui.html>.

## Ejecutar las pruebas

```bash
mvn spotless:apply   # aplicar el formato
mvn test             # pruebas unitarias (*Test)
mvn verify           # pruebas de integración (*IT, necesitan Docker) y comprobación de formato
```

Las pruebas usan Testcontainers: crean sus propias bases de datos y no tocan las del `compose.yaml`.

## Consultar las bases de datos

```bash
docker compose exec postgres psql -U campushub -d campushub
docker compose exec mongo mongosh campushub
```

Algunos comandos útiles (`\dt`, `SELECT * FROM inscripcion;`, `db.seguimientos.find()`...) están en
[`docs/evidencias-persistencia.md`](docs/evidencias-persistencia.md).

## Detener y reiniciar el entorno

- Si arrancamos con `mvn spring-boot:run`, al parar la aplicación (Ctrl + C) se paran también los
  contenedores. Los **datos se conservan** porque están en volúmenes de Docker.
- Con el arranque manual: `docker compose stop` para parar y `docker compose start` para volver a
  empezar.
- `docker compose down` elimina los contenedores pero conserva los datos.
- `docker compose down -v` **borra también los datos**. Hay que usarlo si se arrancó antes una
  versión anterior del proyecto, porque Hibernate (`ddl-auto=update`) no añade los `CHECK` a tablas
  que ya existían.

## Estructura del proyecto

```text
controller/       endpoints REST (sin modificar)
controller/dto/   DTO de entrada y salida (sin modificar)
service/          lógica de negocio
repository/       repositorios de Spring Data (JPA y MongoDB)
entity/           entidades JPA (PostgreSQL)
document/         documento de MongoDB (Seguimiento)
mapper/           conversión entre entidades/documentos y DTO
util/             validaciones y excepciones
docs/             documentación
```

## Documentación

- [`docs/modelo-datos.md`](docs/modelo-datos.md): diagrama, índices y restricciones
- [`docs/decisiones-tecnicas.md`](docs/decisiones-tecnicas.md): qué va en cada base de datos y por qué
- [`docs/evidencias-persistencia.md`](docs/evidencias-persistencia.md): cómo comprobar que los datos están guardados
- [`docs/ejemplos-api.md`](docs/ejemplos-api.md): ejemplos para probar la API
- [`docs/api-contract.md`](docs/api-contract.md): contrato de la API