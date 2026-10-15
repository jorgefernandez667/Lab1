package turnos.persistencia;

/**
 * Error de persistencia independiente de la tecnología (JDBC, archivos, etc.).
 * Es RuntimeException para que el contrato TurnoRepository no dependa de
 * SQLException.
 */
public class PersistenciaException extends RuntimeException {

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    public PersistenciaException(String mensaje) {
        super(mensaje);
    }
}
