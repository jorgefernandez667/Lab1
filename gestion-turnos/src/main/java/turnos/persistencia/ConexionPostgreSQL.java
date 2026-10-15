package turnos.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexionPostgreSQL {

    private static final String URL =
            System.getenv().getOrDefault("PG_URL",
                    "jdbc:postgresql://localhost:5432/turnos");

    private static final String USER =
            System.getenv().getOrDefault("PG_USER", "postgres");

    private static final String PASSWORD =
            System.getenv("PG_PASSWORD");

    private ConexionPostgreSQL() {
        // clase utilitaria: no se instancia
    }

    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
