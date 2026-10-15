package turnos.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import turnos.modelo.EstadoTurno;
import turnos.modelo.Turno;

/** Implementación de TurnoRepository con JDBC para PostgreSQL. */
public class PostgreSQLTurnoRepository implements TurnoRepository {

    @Override
    public void guardar(Turno turno) {
        String sql = """
                INSERT INTO turno (numero, estado)
                VALUES (?, ?)
                """;

        try (Connection cn = ConexionPostgreSQL.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, turno.getNumero());
            ps.setString(2, turno.getEstado().name());
            ps.executeUpdate();

        } catch (SQLException e) {
            // Clase 23 de SQLState = violación de integridad (clave duplicada)
            if (e.getSQLState() != null && e.getSQLState().startsWith("23")) {
                throw new PersistenciaException(
                        "Ya existe el turno " + turno.getNumero(), e);
            }
            throw new PersistenciaException(
                    "Error al guardar el turno " + turno.getNumero(), e);
        }
    }

    @Override
    public List<Turno> listar() {
        String sql = """
                SELECT numero, estado
                FROM turno
                ORDER BY numero
                """;

        List<Turno> turnos = new ArrayList<>();

        try (Connection cn = ConexionPostgreSQL.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                turnos.add(mapear(rs));
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar los turnos", e);
        }
        return turnos;
    }

    @Override
    public Optional<Turno> buscarPorNumero(int numero) {
        String sql = """
                SELECT numero, estado
                FROM turno
                WHERE numero = ?
                """;

        try (Connection cn = ConexionPostgreSQL.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, numero);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new PersistenciaException(
                    "Error al buscar el turno " + numero, e);
        }
    }

    @Override
    public void actualizar(Turno turno) {
        String sql = """
                UPDATE turno
                SET estado = ?
                WHERE numero = ?
                """;

        try (Connection cn = ConexionPostgreSQL.obtenerConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, turno.getEstado().name());
            ps.setInt(2, turno.getNumero());

            int filas = ps.executeUpdate();
            if (filas == 0) {
                // Nada se actualizó: el turno no existe (p. ej. lo borraron
                // desde otro puesto). Callar este caso ocultaría un error.
                throw new PersistenciaException(
                        "No se pudo actualizar: no existe el turno "
                        + turno.getNumero());
            }

        } catch (SQLException e) {
            throw new PersistenciaException(
                    "Error al actualizar el turno " + turno.getNumero(), e);
        }
    }

    /** Convierte la fila actual del ResultSet en un Turno. */
    private Turno mapear(ResultSet rs) throws SQLException {
        int numero = rs.getInt("numero");
        EstadoTurno estado = EstadoTurno.valueOf(rs.getString("estado"));
        return new Turno(numero, estado);
    }
}
