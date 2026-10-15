package turnos;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import turnos.controlador.TurnoController;
import turnos.persistencia.MySQLTurnoRepository;
import turnos.persistencia.PostgreSQLTurnoRepository;
import turnos.servicio.TurnoService;
import turnos.vista.VentanaPrincipal;

/**
 * Aplicación JavaFX.
 *
 * El método start() es el PUNTO DE ENSAMBLAJE: el único lugar donde se
 * decide qué implementaciones concretas usa la aplicación y cómo se conectan.
 */
public class AppTurnos extends Application {

    @Override
    public void start(Stage stage) {

        // ================= PUNTO DE ENSAMBLAJE =================
       // TurnoService service = new TurnoService(new MySQLTurnoRepository());
        //String origenDatos = "MySQL";
        TurnoService service = new TurnoService(new PostgreSQLTurnoRepository());
        String origenDatos = "PostgreSQL";
        // Para migrar a PostgreSQL, reemplace las dos líneas anteriores por:
        // TurnoService service = new TurnoService(new PostgreSQLTurnoRepository());
        // String origenDatos = "PostgreSQL";
        // =======================================================

        VentanaPrincipal vista = new VentanaPrincipal();
        vista.mostrarOrigenDatos(origenDatos);

        TurnoController controller = new TurnoController(service, vista);
        controller.iniciar();

        Scene escena = new Scene(vista, 980, 680);
        escena.getStylesheets().add(
                getClass().getResource("/turnos/vista/estilos.css").toExternalForm());

        stage.setTitle("Gestión de Turnos – EIF206");
        stage.setMinWidth(820);
        stage.setMinHeight(640);
        stage.setScene(escena);
        stage.show();
    }
}
