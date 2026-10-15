# Sistema de gestión de turnos

**EIF206 – Programación III** · JDBC, patrón Repository y migración MySQL → PostgreSQL

## Integrantes

- (nombre 1)
- (nombre 2)

## Requisitos

Java 21+, Maven (incluido en NetBeans), MySQL y PostgreSQL con servidor en ejecución.
JavaFX y los drivers JDBC los descarga Maven.

## Configuración

### Variables de entorno

| Variable | Ejemplo |
|---|---|
| `MYSQL_URL` | `jdbc:mysql://localhost:3306/turnos` |
| `MYSQL_USER` | `root` |
| `MYSQL_PASSWORD` | (su contraseña) |
| `PG_URL` | `jdbc:postgresql://localhost:5432/turnos` |
| `PG_USER` | `postgres` |
| `PG_PASSWORD` | (su contraseña) |

En NetBeans: *Project Properties → Actions → Run project* y *Run file via main()* → *Set Properties*
(`Env.MYSQL_PASSWORD=...`). Ese `nbactions.xml` está en `.gitignore`: no se sube.

### Bases de datos

- MySQL: ejecutar `sql/mysql_turnos.sql` completo.
- PostgreSQL: `CREATE DATABASE turnos;`, conectarse a ella y ejecutar `sql/postgresql_turnos.sql`.

## Ejecución

Ejecutar siempre `turnos.Main`. El motor se elige en el bloque **PUNTO DE ENSAMBLAJE** de `AppTurnos.start()`:

```java
// MySQL
TurnoService service = new TurnoService(new MySQLTurnoRepository());
String origenDatos = "MySQL";
// PostgreSQL
TurnoService service = new TurnoService(new PostgreSQLTurnoRepository());
String origenDatos = "PostgreSQL";
```

## Estructura del proyecto

```text
turnos/
├── Main, AppTurnos              ← arranque y punto de ensamblaje
├── modelo/                      ← Turno, EstadoTurno
├── vista/                       ← VentanaPrincipal (JavaFX + CSS)
├── controlador/                 ← TurnoController
├── servicio/                    ← TurnoService (reglas; depende de TurnoRepository)
└── persistencia/
    ├── TurnoRepository          ← contrato (interfaz)
    ├── PersistenciaException    ← error propio, sin dependencia de JDBC
    ├── ConexionMySQL / ConexionPostgreSQL
    ├── MySQLTurnoRepository / PostgreSQLTurnoRepository
    ├── MemoriaTurnoRepository   ← Reto 1 (opcional)
    └── PruebaConexion / PruebaRepositorio
```

## Persistencia

### MySQL
`MySQLTurnoRepository` + `ConexionMySQL`. Consultas con `PreparedStatement`, recursos en
`try-with-resources`, `Optional` en la búsqueda y `SQLException` traducida a `PersistenciaException`.

### PostgreSQL
`PostgreSQLTurnoRepository` + `ConexionPostgreSQL`: misma estructura; solo cambia la clase de conexión.

## Migración

**¿Qué cambió?** Solo la capa de persistencia (clase de conexión y repositorio concreto) y el bloque
de ensamblaje de `AppTurnos`. (Memoria → MySQL además obligó a refactorizar `TurnoService` una vez,
para depender de `TurnoRepository`.)

**¿Qué permaneció igual?** `Turno`, `EstadoTurno`, `TurnoRepository`, `PersistenciaException`,
`TurnoService`, `TurnoController`, `VentanaPrincipal` y `estilos.css`.

**¿Cómo lo verificaron?** `git diff` sin cambios en esos archivos entre la versión MySQL y la
PostgreSQL, y los ocho casos de prueba con capturas donde se ve la insignia "Datos: …".

## Desafío: ArchivoTurnoRepository

1. **Métodos:** los cuatro del contrato: `guardar`, `listar`, `buscarPorNumero`, `actualizar`.
2. **¿Cambia `TurnoService`, `TurnoController` o `VentanaPrincipal`?** No. Solo se crea la clase y se
   cambia el bloque de ensamblaje.
3. **¿Quién conoce el formato?** Únicamente `ArchivoTurnoRepository` (leer/escribir líneas y convertirlas
   en `Turno`, igual que `mapear()` en los repositorios JDBC).
4. **Formato de línea:** `numero;ESTADO`, por ejemplo `1;PENDIENTE`. Se guarda `estado.name()`.
5. **Errores:** si el archivo no existe o está dañado se lanza `PersistenciaException` (envolviendo la
   `IOException` o `IllegalArgumentException`). El controlador la atrapa como `RuntimeException` y el
   usuario ve en la barra: "No fue posible completar la operación. Intente de nuevo o contacte al
   administrador."; el detalle va a la consola. Un archivo inexistente al inicio podría tratarse como lista vacía.

```java
public class ArchivoTurnoRepository implements TurnoRepository {
    // usa java.nio.file.Files; reescribe turnos.txt en guardar() y actualizar()
}
```

## Reflexión final

(Cada integrante completa las cuatro frases de la guía, sección 11, con sus propias palabras.)

- Antes de realizar esta actividad pensaba que cambiar de MySQL a PostgreSQL implicaba...
- Después de realizar la actividad comprendí que...
- La parte de nuestro diseño que permite realizar el cambio es...
- Una parte de nuestro código que todavía podría mejorarse es...
