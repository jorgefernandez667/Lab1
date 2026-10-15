package turnos.controlador;

import turnos.modelo.EstadoTurno;
import turnos.modelo.Turno;
import turnos.servicio.TurnoService;
import turnos.vista.VentanaPrincipal;

/**
 * Controlador: conecta la vista con el servicio.
 *
 * - Recibe los eventos de la vista (clics, cambios de filtro).
 * - Pide al servicio que ejecute la regla de negocio correspondiente.
 * - Actualiza la vista con el resultado o con un mensaje adecuado.
 *
 * El controlador NO contiene reglas de negocio ni acceso a datos.
 */
public class TurnoController {

    private final TurnoService service;
    private final VentanaPrincipal vista;

    private EstadoTurno filtro;   // null = mostrar todos
    private Turno turnoEnAtencion;

    public TurnoController(TurnoService service, VentanaPrincipal vista) {
        this.service = service;
        this.vista = vista;

        vista.alCrearTurno(this::crearTurno);
        vista.alLlamarSiguiente(this::llamarSiguiente);
        vista.alAtender(this::atenderSeleccionado);
        vista.alCancelar(this::cancelarSeleccionado);
        vista.alCambiarFiltro(this::cambiarFiltro);
    }

    /** Carga los datos iniciales en pantalla. */
    public void iniciar() {
        ejecutar(() -> vista.mostrarExito("Listo. Seleccione una acción."));
    }

    // =====================================================================
    // Acciones del usuario
    // =====================================================================

    private void crearTurno() {
        ejecutar(() -> {
            Turno turno = service.crearTurno();
            vista.mostrarExito("Se creó el turno " + turno.getNumero() + ".");
        });
    }

    private void llamarSiguiente() {
        ejecutar(() -> {
            turnoEnAtencion = service.llamarSiguiente();
            vista.mostrarExito("Se llamó al turno " + turnoEnAtencion.getNumero() + ".");
        });
    }

    private void atenderSeleccionado() {
        Turno seleccionado = vista.turnoSeleccionado();
        if (seleccionado == null) {
            vista.mostrarAdvertencia("Seleccione un turno en la tabla.");
            return;
        }
        ejecutar(() -> {
            Turno turno = service.atender(seleccionado.getNumero());
            actualizarTurnoEnAtencion(turno);
            vista.mostrarExito("El turno " + turno.getNumero() + " fue atendido.");
        });
    }

    private void cancelarSeleccionado() {
        Turno seleccionado = vista.turnoSeleccionado();
        if (seleccionado == null) {
            vista.mostrarAdvertencia("Seleccione un turno en la tabla.");
            return;
        }
        ejecutar(() -> {
            Turno turno = service.cancelar(seleccionado.getNumero());
            actualizarTurnoEnAtencion(turno);
            vista.mostrarExito("El turno " + turno.getNumero() + " fue cancelado.");
        });
    }

    private void cambiarFiltro(String textoFiltro) {
        filtro = null;
        for (EstadoTurno estado : EstadoTurno.values()) {
            if (estado.getEtiqueta().equals(textoFiltro)) {
                filtro = estado;
            }
        }
        ejecutar(() -> { });
    }

    // =====================================================================
    // Apoyo
    // =====================================================================

    /**
     * Ejecuta una acción, refresca la pantalla y traduce los errores
     * a mensajes comprensibles para el usuario.
     */
    private void ejecutar(Runnable accion) {
        try {
            accion.run();
            refrescar();
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Errores esperados: una regla de negocio no se cumple.
            vista.mostrarAdvertencia(e.getMessage());
        } catch (RuntimeException e) {
            // Errores inesperados (por ejemplo, de acceso a datos).
            // El usuario recibe un mensaje claro; el detalle técnico va a la consola.
            vista.mostrarError("No fue posible completar la operación. "
                    + "Intente de nuevo o contacte al administrador.");
            e.printStackTrace();
        }
    }

    private void refrescar() {
        vista.mostrarTurnos(filtro == null
                ? service.listarTodos()
                : service.listarPorEstado(filtro));
        vista.mostrarResumen(service.contarPorEstado());
        vista.mostrarTurnoActual(turnoEnAtencion);
    }

    private void actualizarTurnoEnAtencion(Turno turno) {
        if (turnoEnAtencion != null
                && turnoEnAtencion.getNumero() == turno.getNumero()) {
            turnoEnAtencion = turno;
        }
    }
}
