package turnos;

import javafx.application.Application;

/**
 * Clase de arranque.
 *
 * Se usa una clase separada (que NO hereda de Application) para que el
 * proyecto se ejecute desde NetBeans sin configurar módulos de JavaFX.
 * Ejecute SIEMPRE esta clase, no AppTurnos.
 */
public class Main {

    public static void main(String[] args) {
        Application.launch(AppTurnos.class, args);
    }
}
