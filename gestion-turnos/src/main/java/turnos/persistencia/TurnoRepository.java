package turnos.persistencia;

import java.util.List;
import java.util.Optional;
import turnos.modelo.Turno;

/**
 * Contrato de persistencia de turnos.
 * No contiene nada específico de MySQL, PostgreSQL, archivos, SQL ni JDBC.
 */
public interface TurnoRepository {

    void guardar(Turno turno);

    List<Turno> listar();

    Optional<Turno> buscarPorNumero(int numero);

    void actualizar(Turno turno);
}
