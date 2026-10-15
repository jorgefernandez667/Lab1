package turnos.vista;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.beans.property.SimpleObjectProperty;
import turnos.modelo.EstadoTurno;
import turnos.modelo.Turno;

/**
 * Ventana principal (vista).
 *
 * Responsabilidad: MOSTRAR información y AVISAR cuando el usuario hace algo.
 * La vista no toma decisiones, no conoce TurnoService ni la base de datos.
 * Toda la apariencia se define en estilos.css.
 */
public class VentanaPrincipal extends BorderPane {

    public static final String FILTRO_TODOS = "Todos";

    private final Label lblOrigenDatos = new Label();
    private final Label lblTurnoActual = new Label("—");
    private final Label lblEstadoActual = new Label("Sin turno en atención");

    private final Button btnNuevo = new Button("Nuevo turno");
    private final Button btnLlamar = new Button("Llamar siguiente");
    private final Button btnAtender = new Button("Atender seleccionado");
    private final Button btnCancelar = new Button("Cancelar seleccionado");

    private final ComboBox<String> cmbFiltro = new ComboBox<>();
    private final TableView<Turno> tabla = new TableView<>();
    private final GridPane panelContadores = new GridPane();
    private final Label lblMensaje = new Label("Listo.");

    public VentanaPrincipal() {
        getStyleClass().add("raiz");
        setTop(crearEncabezado());
        setLeft(crearPanelLateral());
        setCenter(crearPanelCentral());
        setBottom(crearBarraEstado());
    }

    // =====================================================================
    // Construcción de la interfaz
    // =====================================================================

    private HBox crearEncabezado() {
        Label titulo = new Label("Gestión de Turnos");
        titulo.getStyleClass().add("titulo");

        Label subtitulo = new Label("EIF206 · Programación III");
        subtitulo.getStyleClass().add("subtitulo");

        VBox textos = new VBox(2, titulo, subtitulo);

        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);

        lblOrigenDatos.getStyleClass().add("insignia-origen");

        HBox encabezado = new HBox(textos, espacio, lblOrigenDatos);
        encabezado.setAlignment(Pos.CENTER_LEFT);
        encabezado.getStyleClass().add("encabezado");
        return encabezado;
    }

    private VBox crearPanelLateral() {
        // Tarjeta del turno en atención
        Label tituloTarjeta = new Label("TURNO EN ATENCIÓN");
        tituloTarjeta.getStyleClass().add("tarjeta-titulo");
        lblTurnoActual.getStyleClass().add("numero-actual");
        lblEstadoActual.getStyleClass().add("tarjeta-detalle");

        VBox tarjeta = new VBox(4, tituloTarjeta, lblTurnoActual, lblEstadoActual);
        tarjeta.setAlignment(Pos.CENTER);
        tarjeta.getStyleClass().addAll("tarjeta", "tarjeta-destacada");

        // Botones de acción
        btnNuevo.getStyleClass().addAll("boton", "boton-primario");
        btnLlamar.getStyleClass().addAll("boton", "boton-secundario");
        btnAtender.getStyleClass().addAll("boton", "boton-secundario");
        btnCancelar.getStyleClass().addAll("boton", "boton-peligro");
        for (Button b : List.of(btnNuevo, btnLlamar, btnAtender, btnCancelar)) {
            b.setMaxWidth(Double.MAX_VALUE);
        }

        Label tituloAcciones = new Label("ACCIONES");
        tituloAcciones.getStyleClass().add("seccion-titulo");
        VBox acciones = new VBox(8, tituloAcciones, btnNuevo, btnLlamar,
                btnAtender, btnCancelar);

        // Resumen por estado
        Label tituloResumen = new Label("RESUMEN");
        tituloResumen.getStyleClass().add("seccion-titulo");
        panelContadores.setHgap(8);
        panelContadores.setVgap(8);
        VBox resumen = new VBox(8, tituloResumen, panelContadores);

        VBox panel = new VBox(18, tarjeta, acciones, resumen);
        panel.getStyleClass().add("panel-lateral");
        return panel;
    }

    private VBox crearPanelCentral() {
        Label tituloLista = new Label("Turnos registrados");
        tituloLista.getStyleClass().add("seccion-titulo-grande");

        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);

        Label lblFiltro = new Label("Mostrar:");
        lblFiltro.getStyleClass().add("etiqueta");

        cmbFiltro.getItems().add(FILTRO_TODOS);
        for (EstadoTurno e : EstadoTurno.values()) {
            cmbFiltro.getItems().add(e.getEtiqueta());
        }
        cmbFiltro.setValue(FILTRO_TODOS);
        cmbFiltro.getStyleClass().add("filtro");

        HBox barra = new HBox(10, tituloLista, espacio, lblFiltro, cmbFiltro);
        barra.setAlignment(Pos.CENTER_LEFT);

        configurarTabla();
        VBox.setVgrow(tabla, Priority.ALWAYS);

        VBox panel = new VBox(14, barra, tabla);
        panel.getStyleClass().add("panel-central");
        return panel;
    }

    private void configurarTabla() {
        TableColumn<Turno, Integer> colNumero = new TableColumn<>("Número");
        colNumero.setCellValueFactory(c ->
                new SimpleObjectProperty<>(c.getValue().getNumero()));
        colNumero.getStyleClass().add("columna-numero");

        TableColumn<Turno, EstadoTurno> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(c ->
                new SimpleObjectProperty<>(c.getValue().getEstado()));
        // Cada estado se dibuja como una "insignia" con su propio color (ver CSS)
        colEstado.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(EstadoTurno estado, boolean vacio) {
                super.updateItem(estado, vacio);
                if (vacio || estado == null) {
                    setGraphic(null);
                } else {
                    Label insignia = new Label(estado.getEtiqueta());
                    insignia.getStyleClass().addAll("insignia-estado",
                            "estado-" + estado.name().toLowerCase());
                    setGraphic(insignia);
                }
                setText(null);
            }
        });

        tabla.getColumns().add(colNumero);
        tabla.getColumns().add(colEstado);
        colNumero.setPrefWidth(140);
        // La columna Estado ocupa el resto del ancho disponible
        colEstado.prefWidthProperty().bind(
                tabla.widthProperty().subtract(colNumero.widthProperty()).subtract(32));
        tabla.setPlaceholder(new Label("No hay turnos para mostrar."));
        tabla.getStyleClass().add("tabla");
    }

    private HBox crearBarraEstado() {
        lblMensaje.getStyleClass().add("mensaje");
        HBox barra = new HBox(lblMensaje);
        barra.setAlignment(Pos.CENTER_LEFT);
        barra.getStyleClass().add("barra-estado");
        return barra;
    }

    // =====================================================================
    // Eventos: la vista solo AVISA; el controlador decide qué hacer.
    // =====================================================================

    public void alCrearTurno(Runnable accion) {
        btnNuevo.setOnAction(e -> accion.run());
    }

    public void alLlamarSiguiente(Runnable accion) {
        btnLlamar.setOnAction(e -> accion.run());
    }

    public void alAtender(Runnable accion) {
        btnAtender.setOnAction(e -> accion.run());
    }

    public void alCancelar(Runnable accion) {
        btnCancelar.setOnAction(e -> accion.run());
    }

    /** El consumidor recibe el texto del filtro elegido ("Todos", "Pendiente", ...). */
    public void alCambiarFiltro(Consumer<String> accion) {
        cmbFiltro.setOnAction(e -> accion.accept(cmbFiltro.getValue()));
    }

    // =====================================================================
    // Métodos para que el controlador actualice la pantalla
    // =====================================================================

    public void mostrarTurnos(List<Turno> turnos) {
        Turno seleccionado = turnoSeleccionado();
        tabla.setItems(FXCollections.observableArrayList(turnos));
        tabla.refresh();
        if (seleccionado != null) {
            turnos.stream()
                    .filter(t -> t.getNumero() == seleccionado.getNumero())
                    .findFirst()
                    .ifPresent(t -> tabla.getSelectionModel().select(t));
        }
    }

    public void mostrarResumen(Map<EstadoTurno, Long> conteo) {
        panelContadores.getChildren().clear();
        int i = 0;
        for (EstadoTurno estado : EstadoTurno.values()) {
            Label cantidad = new Label(String.valueOf(conteo.getOrDefault(estado, 0L)));
            cantidad.getStyleClass().add("contador-numero");
            Label nombre = new Label(estado.getEtiqueta());
            nombre.getStyleClass().add("contador-nombre");

            VBox contador = new VBox(0, cantidad, nombre);
            contador.getStyleClass().addAll("contador",
                    "estado-" + estado.name().toLowerCase());
            contador.setMaxWidth(Double.MAX_VALUE);
            GridPane.setHgrow(contador, Priority.ALWAYS);
            panelContadores.add(contador, i % 2, i / 2);
            i++;
        }
    }

    public void mostrarTurnoActual(Turno turno) {
        if (turno == null) {
            lblTurnoActual.setText("—");
            lblEstadoActual.setText("Sin turno en atención");
        } else {
            lblTurnoActual.setText(String.format("%03d", turno.getNumero()));
            lblEstadoActual.setText(turno.getEstado().getEtiqueta());
        }
    }

    /** Muestra qué mecanismo de persistencia está usando la aplicación. */
    public void mostrarOrigenDatos(String origen) {
        lblOrigenDatos.setText("Datos: " + origen);
    }

    public Turno turnoSeleccionado() {
        return tabla.getSelectionModel().getSelectedItem();
    }

    public void mostrarExito(String mensaje) {
        mostrarMensaje(mensaje, "mensaje-exito");
    }

    public void mostrarAdvertencia(String mensaje) {
        mostrarMensaje(mensaje, "mensaje-advertencia");
    }

    public void mostrarError(String mensaje) {
        mostrarMensaje(mensaje, "mensaje-error");
    }

    private void mostrarMensaje(String mensaje, String claseCss) {
        lblMensaje.setText(mensaje);
        lblMensaje.getStyleClass().removeAll(
                "mensaje-exito", "mensaje-advertencia", "mensaje-error");
        lblMensaje.getStyleClass().add(claseCss);
    }
}
