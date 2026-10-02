package turnos.app;

import java.nio.file.Files;
import java.nio.file.Path;
import turnos.persistencia.ArchivoTurnoRepository;
import turnos.persistencia.Historial;
import turnos.persistencia.PersistenciaException;
import turnos.persistencia.TurnoRepository;
import turnos.servicio.TurnoService;

public class Main {

    public static void main(String[] args) {
        // Rutas RELATIVAS al proyecto: funcionan en cualquier computadora
        Path archivoTurnos = Path.of("data", "turnos.txt");
        Historial historial = new Historial(Path.of("data", "historial.txt"));

        // Único lugar donde se elige la implementación
        TurnoRepository repositorio = new ArchivoTurnoRepository(archivoTurnos);
        TurnoService servicio = new TurnoService(repositorio);

        try {
            // 1. Guardar
            var t1 = servicio.crearTurno();
            var t2 = servicio.crearTurno();
            var t3 = servicio.crearTurno();
            historial.registrar("Creados turnos " + t1.getNumero() + ", " + t2.getNumero() + ", " + t3.getNumero());

            // 2. Listar
            System.out.println("== Todos los turnos ==");
            servicio.listarTodos().forEach(System.out::println);

            // 3. Buscar con Optional
            System.out.println("\n== Buscar turno " + t2.getNumero() + " ==");
            servicio.buscar(t2.getNumero())
                    .ifPresentOrElse(System.out::println,
                            () -> System.out.println("No encontrado"));
            servicio.buscar(999)
                    .ifPresentOrElse(System.out::println,
                            () -> System.out.println("Turno 999: no encontrado"));

            // 4. Actualizar estado
            servicio.llamarSiguiente().ifPresent(t -> {
                System.out.println("\nLlamando: " + t);
                historial.registrar("Llamado turno " + t.getNumero());
                servicio.atender(t.getNumero());
                historial.registrar("Atendido turno " + t.getNumero());
            });

            System.out.println("\n== Pendientes ==");
            servicio.listarPendientes().forEach(System.out::println);

            System.out.println("\n== Contenido de " + archivoTurnos + " ==");
            Files.readAllLines(archivoTurnos).forEach(System.out::println);

            System.out.println("\n== Historial ==");
            historial.leer().forEach(System.out::println);

        } catch (PersistenciaException e) {
            System.err.println("Error de almacenamiento: " + e.getMessage());
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.err.println("Regla de negocio: " + e.getMessage());
        } catch (java.io.IOException e) {
            System.err.println("Error leyendo el archivo: " + e.getMessage());
        }
    }
}
