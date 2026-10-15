package turnos.persistencia;

import turnos.modelo.EstadoTurno;
import turnos.modelo.Turno;

/** Prueba del repositorio sin interfaz. Para PostgreSQL cambie SOLO la línea marcada. */
public class PruebaRepositorio {

    public static void main(String[] args) {
       TurnoRepository repo = new PostgreSQLTurnoRepository();   // <- única línea a cambiar

        System.out.println("Listar: " + repo.listar());
        System.out.println("Buscar 1: " + repo.buscarPorNumero(1));
        System.out.println("Buscar 999: " + repo.buscarPorNumero(999));

        try {
            repo.guardar(new Turno(1, EstadoTurno.PENDIENTE));
        } catch (PersistenciaException e) {
            System.out.println("Duplicado: " + e.getMessage());
        }
    }
}
