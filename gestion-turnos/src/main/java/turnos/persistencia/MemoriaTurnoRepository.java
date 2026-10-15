package turnos.persistencia;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import turnos.modelo.EstadoTurno;
import turnos.modelo.Turno;

/** Reto 1: repositorio en memoria (no requiere base de datos). */
public class MemoriaTurnoRepository implements TurnoRepository {

    private final Map<Integer, Turno> turnos = new TreeMap<>();

    public MemoriaTurnoRepository() {
        guardar(new Turno(1, EstadoTurno.PENDIENTE));
        guardar(new Turno(2, EstadoTurno.PENDIENTE));
        guardar(new Turno(3, EstadoTurno.ATENDIDO));
    }

    @Override
    public void guardar(Turno turno) {
        if (turnos.containsKey(turno.getNumero())) {
            throw new PersistenciaException("Ya existe el turno " + turno.getNumero());
        }
        turnos.put(turno.getNumero(), turno);
    }

    @Override
    public List<Turno> listar() {
        return new ArrayList<>(turnos.values());
    }

    @Override
    public Optional<Turno> buscarPorNumero(int numero) {
        return Optional.ofNullable(turnos.get(numero));
    }

    @Override
    public void actualizar(Turno turno) {
        if (!turnos.containsKey(turno.getNumero())) {
            throw new PersistenciaException(
                    "No se pudo actualizar: no existe el turno " + turno.getNumero());
        }
        turnos.put(turno.getNumero(), turno);
    }
}
