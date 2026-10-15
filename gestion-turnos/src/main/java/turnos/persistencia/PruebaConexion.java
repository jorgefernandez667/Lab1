package turnos.persistencia;

import java.sql.Connection;
import java.sql.SQLException;

/** Clase temporal de prueba. Cambie ConexionMySQL por ConexionPostgreSQL para probar el otro motor. */
public class PruebaConexion {

    public static void main(String[] args) {
        try (Connection cn = ConexionPostgreSQL.obtenerConexion()) {
            System.out.println("Conexión exitosa");
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
