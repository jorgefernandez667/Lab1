package turnos.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexionMySQL {

    private static final String URL =
            System.getenv().getOrDefault("MYSQL_URL",
                    "jdbc:mysql://localhost:3306/turnos");

    private static final String USER =
            System.getenv().getOrDefault("MYSQL_USER", "root");

    private static final String PASSWORD =
            System.getenv("MYSQL_PASSWORD");

    private ConexionMySQL() {
        // clase utilitaria: no se instancia
    }

    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
