package gm.tareas.controlador;

import gm.tareas.modelo.Tarea;
import gm.tareas.servicio.TareaServicio;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class IndexControlador implements Initializable {
    // Implementar un logger para mostrar información en Terminal
    private static final Logger logger = LoggerFactory.getLogger(IndexControlador.class);

    // Instanciar servicios de conexión y consulta a BD
    @Autowired
    private TareaServicio tareaServicio;

    // Definir elementos de UI del archivo index.xfml
    @FXML
    private TableView<Tarea> tareaTabla;

    @FXML
    private TableColumn<Tarea, Integer> idTareaColumna;

    @FXML
    private TableColumn<Tarea, String> nombreTareaColumna;

    @FXML
    private TableColumn<Tarea, String> responsableColumna;

    @FXML
    private TableColumn<Tarea, String> estatusColumna;

    @FXML
    private TextField nombreTareaTexto;

    @FXML
    private TextField responsableTexto;

    @FXML
    private TextField estatusTexto;

    // Definir listaTareas como observable, es decir que se actualizan los cambios cuando cambia la lista
    private final ObservableList<Tarea> tareaList = FXCollections.observableArrayList();

    // ID interno para manejo de actualziación de tareas
    private Integer idTareaInterno;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tareaTabla.getSelectionModel().setSelectionMode(SelectionMode.SINGLE); // Establecer la seleccion de 1 sola columna en la tabla
        configurarColumnas(); // Establecer columnas con clase Tarea
        listarTareas(); // Cargar tareas de BD
    }

    private void configurarColumnas () {
        // Asociar columnas de index.fxml con atributos de clase Tarea.java
        idTareaColumna.setCellValueFactory(new PropertyValueFactory<>("idTarea"));
        nombreTareaColumna.setCellValueFactory(new PropertyValueFactory<>("nombreTarea"));
        responsableColumna.setCellValueFactory(new PropertyValueFactory<>("responsable"));
        estatusColumna.setCellValueFactory(new PropertyValueFactory<>("estatus"));
    }

    // Nota - Los métodos asociados a eventos On en el index.fxml deben ser públicos
    public void agregarTarea() {
        if (nombreTareaTexto.getText().isEmpty()) {
            mostrarMensaje("Error de validación", "Debe proporcinar una tarea");
            nombreTareaTexto.requestFocus();
            return;
        } else {
            var tarea = new Tarea();
            recolectarDatosFormulario(tarea); // Se obtiene la información de los textfield y se agrega al objeto tarea
            tarea.setIdTarea(null);
            tareaServicio.guardarTarea(tarea); // Se llama al servicio de Spring que hace el INSERT en BD
            mostrarMensaje("Información", "Tarea agregada");
            limpiarFormulario();
            listarTareas();
        }
    }

    public void cargarTareaFormulario () {
        var tarea = tareaTabla.getSelectionModel().getSelectedItem();
        if (tarea != null) {
            idTareaInterno = tarea.getIdTarea();
            nombreTareaTexto.setText(tarea.getNombreTarea());
            responsableTexto.setText(tarea.getResponsable());
            estatusTexto.setText(tarea.getEstatus());
        }
    }

    public void modificarTarea () {
        if(idTareaInterno == null) {
            mostrarMensaje("Información","Debe seleccionar una tarea");
            return;
        }
        if (nombreTareaTexto.getText().isEmpty()) {
            mostrarMensaje("Error de validación","Debe proporcionar una tarea");
            nombreTareaTexto.requestFocus();
            return;
        }
        var tarea = new Tarea();
        recolectarDatosFormulario(tarea);
        tareaServicio.guardarTarea(tarea);
        mostrarMensaje("Información","Tarea modificada");
        limpiarFormulario();
        listarTareas();
    }

    public void eliminarTarea () {
        var tarea = tareaTabla.getSelectionModel().getSelectedItem();
        if(tarea != null) {
            logger.info("Registro a eliminar: " + tarea.toString());
            tareaServicio.eliminarTarea(tarea);
            mostrarMensaje("Información","Tarea eliminada: " + tarea.getIdTarea());
            limpiarFormulario();
            listarTareas();
        } else {
            mostrarMensaje("Error","No se ha seleccionado ninguna tarea");
        }
    }

    private void listarTareas() {
        tareaList.clear();
        tareaList.addAll(tareaServicio.listarTareas());
        tareaTabla.setItems(tareaList); // Agregar elementos de la lista de tareas a la tabla de index.fxml
    }

    private void mostrarMensaje (String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void recolectarDatosFormulario (Tarea tarea) {
        if (idTareaInterno != null) {
            tarea.setIdTarea(idTareaInterno);
        }
        tarea.setNombreTarea(nombreTareaTexto.getText());
        tarea.setResponsable(responsableTexto.getText());
        tarea.setEstatus(estatusTexto.getText());
    }

    public void limpiarFormulario () {
        idTareaInterno = null;
        nombreTareaTexto.clear();
        responsableTexto.clear();
        estatusTexto.clear();
    }
}