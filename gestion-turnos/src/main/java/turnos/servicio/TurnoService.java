package turnos.servicio;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import turnos.modelo.EstadoTurno;
import turnos.modelo.Turno;
import turnos.persistencia.TurnoRepository;

/**
 * Reglas del negocio para la gestión de turnos.
 *
 * Esta clase NO conoce la interfaz gráfica.
 *
 * Depende únicamente del contrato TurnoRepository: no sabe si los datos
 * están en MySQL, PostgreSQL, archivos o memoria.
 */
public class TurnoService {

    private final TurnoRepository repository;

    public TurnoService(TurnoRepository repository) {
        this.repository = repository;
    }

    // =====================================================================
    // REGLAS DEL NEGOCIO
    // =====================================================================

    /** Crea un turno nuevo con el siguiente número disponible. */
    public Turno crearTurno() {
        int siguiente = obtenerTodos().stream()
                .mapToInt(Turno::getNumero)
                .max()
                .orElse(0) + 1;

        Turno turno = new Turno(siguiente, EstadoTurno.PENDIENTE);
        insertar(turno);
        return turno;
    }

    /** Todos los turnos, ordenados por número. */
    public List<Turno> listarTodos() {
        return obtenerTodos().stream()
                .sorted(Comparator.comparingInt(Turno::getNumero))
                .toList();
    }

    /** Turnos que se encuentran en un estado específico. */
    public List<Turno> listarPorEstado(EstadoTurno estado) {
        return listarTodos().stream()
                .filter(t -> t.getEstado() == estado)
                .toList();
    }

    /** Cantidad de turnos en cada estado (incluye los estados con 0). */
    public Map<EstadoTurno, Long> contarPorEstado() {
        Map<EstadoTurno, Long> conteo = obtenerTodos().stream()
                .collect(Collectors.groupingBy(
                        Turno::getEstado,
                        () -> new EnumMap<>(EstadoTurno.class),
                        Collectors.counting()));

        for (EstadoTurno estado : EstadoTurno.values()) {
            conteo.putIfAbsent(estado, 0L);
        }
        return conteo;
    }

    /** Llama al turno pendiente con el número más bajo. */
    public Turno llamarSiguiente() {
        Turno turno = listarPorEstado(EstadoTurno.PENDIENTE).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No hay turnos pendientes."));
        return llamar(turno.getNumero());
    }

    /** Regla: solo un turno PENDIENTE puede pasar a LLAMADO. */
    public Turno llamar(int numero) {
        Turno turno = buscarObligatorio(numero);
        if (turno.getEstado() != EstadoTurno.PENDIENTE) {
            throw new IllegalStateException(
                    "El turno " + numero + " no está pendiente.");
        }
        turno.setEstado(EstadoTurno.LLAMADO);
        guardarCambios(turno);
        return turno;
    }

    /** Regla: solo un turno LLAMADO puede pasar a ATENDIDO. */
    public Turno atender(int numero) {
        Turno turno = buscarObligatorio(numero);
        if (turno.getEstado() != EstadoTurno.LLAMADO) {
            throw new IllegalStateException(
                    "Solo se puede atender un turno que haya sido llamado.");
        }
        turno.setEstado(EstadoTurno.ATENDIDO);
        guardarCambios(turno);
        return turno;
    }

    /** Regla: solo se cancelan turnos PENDIENTES o LLAMADOS. */
    public Turno cancelar(int numero) {
        Turno turno = buscarObligatorio(numero);
        if (turno.getEstado() == EstadoTurno.ATENDIDO
                || turno.getEstado() == EstadoTurno.CANCELADO) {
            throw new IllegalStateException(
                    "El turno " + numero + " ya fue "
                    + turno.getEstado().getEtiqueta().toLowerCase() + ".");
        }
        turno.setEstado(EstadoTurno.CANCELADO);
        guardarCambios(turno);
        return turno;
    }

    private Turno buscarObligatorio(int numero) {
        return buscar(numero)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe el turno " + numero + "."));
    }

    // =====================================================================
    // ACCESO A DATOS (delegado en el repositorio)
    //
    // Todas las reglas del negocio usan ÚNICAMENTE estos cuatro métodos.
    // =====================================================================

    private void insertar(Turno turno) {
        repository.guardar(turno);
    }

    private List<Turno> obtenerTodos() {
        return repository.listar();
    }

    private Optional<Turno> buscar(int numero) {
        return repository.buscarPorNumero(numero);
    }

    private void guardarCambios(Turno turno) {
        // Ahora es imprescindible: el Turno devuelto por el repositorio es una
        // copia leída de la base de datos, no el objeto almacenado.
        repository.actualizar(turno);
    }
}
