package turnos.modelo;

/**
 * Estados posibles de un turno.
 *
 * Flujo normal:  PENDIENTE → LLAMADO → ATENDIDO
 * Cancelación:   PENDIENTE o LLAMADO → CANCELADO
 */
public enum EstadoTurno {

    PENDIENTE("Pendiente"),
    LLAMADO("Llamado"),
    ATENDIDO("Atendido"),
    CANCELADO("Cancelado");

    private final String etiqueta;

    EstadoTurno(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /** Texto amigable para mostrar en la interfaz. */
    public String getEtiqueta() {
        return etiqueta;
    }
}
