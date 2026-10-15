package turnos.modelo;

/**
 * Modelo de dominio: un turno de atención.
 *
 * No conoce la interfaz gráfica ni la forma en que se almacena.
 */
public class Turno {

    private final int numero;
    private EstadoTurno estado;

    public Turno(int numero, EstadoTurno estado) {
        this.numero = numero;
        this.estado = estado;
    }

    public int getNumero() {
        return numero;
    }

    public EstadoTurno getEstado() {
        return estado;
    }

    public void setEstado(EstadoTurno estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Turno{numero=" + numero + ", estado=" + estado + "}";
    }
}
