package com.example.gearfit.controllers;

import com.example.gearfit.models.Exercise;
import com.example.gearfit.models.ExerciseSet;
import com.example.gearfit.models.Routine;
import com.example.gearfit.repositories.RoutineDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class RoutineDayTableController {

    public Label title;

    private int routineId;

    private String routineDay;

    private List<Exercise> exercises;

    private Exercise currentExercise;

    private ExerciseSet currentSet;

    private Button selectedExerciseButton;

    private Button selectedSetButton;

    @FXML
    private VBox exercisesContainer;

    @FXML
    public ScrollPane exercisesScrollPane;

    @FXML
    private TextField weightField;

    @FXML
    private TextField repetitionsField;

    @FXML
    private Label actualExerciseClicked;
    @FXML
    private Label actualExerciseSetClicked;
    @FXML
    private AnchorPane rootPane;

    private List<String> daysToDaySelector;

    private Routine routineToDaySelector;

    @FXML
    public void initialize() {
        if (exercisesContainer == null) {
            System.out.println("El contenedor de ejercicios no está inicializado.");
        } else {
            System.out.println("El contenedor de ejercicios está inicializado.");
        }

        if (exercisesScrollPane == null) {
            System.out.println("El ScrollPane no está inicializado.");
        } else {
            // System.out.println("El ScrollPane está inicializado.");

            // Añade un filtro para manejar el desplazamiento con la rueda del ratón
            exercisesScrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
                if (event.getDeltaY() != 0) { // Si hay desplazamiento vertical
                    double deltaY = event.getDeltaY(); // Magnitud del desplazamiento
                    exercisesScrollPane.setVvalue(
                            exercisesScrollPane.getVvalue() - deltaY / exercisesScrollPane.getHeight()
                    );
                    event.consume(); // Consumir el evento para evitar conflictos
                }
            });
        }

    }

    public void setRoutineId(int routineId) {
        this.routineId = routineId;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
        displayExercises();
    }

    public void setRoutineDay(String routineDay) {
        this.routineDay = routineDay;
        title.setText("Rutina del " + routineDay);
    }

    private void displayExercises() {
        exercisesContainer.getChildren().clear(); // Limpiar el contenedor

        if (exercises == null || exercises.isEmpty()) {
            System.out.println("No hay ejercicios para mostrar.");
            actualExerciseClicked.setText("Crea tu primer ejercicio");
            return;
        } else {
            actualExerciseClicked.setText("Selecciona un ejercicio");
        }

        for (Exercise exercise : exercises) {
            VBox exerciseBox = new VBox();
            exerciseBox.getStyleClass().add("exercise-box");

            Button exerciseButton = new Button();
            exerciseButton.getStyleClass().add("exercise-button");

            // Crear el contenido del ejercicio
            VBox exerciseContent = new VBox();
            exerciseContent.getStyleClass().add("exercise-content");

            Label nameLabel = new Label("Ejercicio: " + exercise.getName());
            Label tempoLabel = new Label("Tempo de ejecución: " + exercise.getTempo());
            Label restLabel = new Label("Descanso entre series: " + exercise.getRestTime() + " segundos");

            exerciseContent.getChildren().addAll(nameLabel, tempoLabel, restLabel);
            exerciseButton.setGraphic(exerciseContent);

            // Agregar el botón del ejercicio al VBox
            exerciseBox.getChildren().add(exerciseButton);

            // Cargar y mostrar las series automáticamente
            loadAndDisplaySets(exercise, exerciseContent);

            // Evento para seleccionar el ejercicio
            exerciseButton.setOnAction(event -> {
                if (selectedExerciseButton != null) {
                    selectedExerciseButton.getStyleClass().remove("exercise-selected");
                }

                exerciseButton.getStyleClass().add("exercise-selected");
                selectedExerciseButton = exerciseButton;

                currentExercise = exercise;

                // Actualizar el Label con el nombre del ejercicio seleccionado
                actualExerciseClicked.setText("Ejercicio: " + exercise.getName());
                System.out.println("Ejercicio seleccionado: " + exercise.getName());
            });

            // Agregar el VBox del ejercicio al contenedor principal
            exercisesContainer.getChildren().add(exerciseBox);
        }
    }

    private void loadAndDisplaySets(Exercise exercise, VBox exerciseContent) {
        // Crear un ScrollPane para las series
        ScrollPane setsScrollPane = new ScrollPane();
        setsScrollPane.getStyleClass().add("sets-scrollpane");
        setsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        HBox setsBox = new HBox();
        setsBox.getStyleClass().add("sets-box");

        List<ExerciseSet> sets = RoutineDAO.getExerciseSetsByExerciseId(exercise.getId());
        exercise.setSets(sets); // Actualiza el objeto Exercise con las series cargadas

        if (sets == null || sets.isEmpty()) {
            //System.out.println("No hay series para mostrar para el ejercicio: " + exercise.getName());
            return;
        }

        for (ExerciseSet set : sets) {
            Button setButton = new Button("Serie " + set.getSetNumber() + ": " + set.getRepetitions() + " reps, " + set.getWeight() + " kg");
            setButton.getStyleClass().add("set-button");

            setButton.setOnAction(event -> {
                if (selectedSetButton != null) {
                    selectedSetButton.getStyleClass().remove("set-selected");
                }

                setButton.getStyleClass().add("set-selected");

                selectedSetButton = setButton;
                currentSet = set;

                // Actualizar el Label con información del conjunto seleccionado
                actualExerciseSetClicked.setText("Serie número " + set.getSetNumber());

                repetitionsField.setText(String.valueOf(set.getRepetitions()));
                weightField.setText(String.valueOf(set.getWeight()));

                System.out.println("Serie seleccionada: " + set.getSetNumber());
            });

            setsBox.getChildren().add(setButton);
        }

        // Agregar el HBox de series al ScrollPane
        setsScrollPane.setContent(setsBox);

        // Agregar el ScrollPane al VBox del ejercicio (exerciseContent)
        exerciseContent.getChildren().add(setsScrollPane);

    }

    @FXML
    private void handleAddExercise() {
        // Crear un diálogo personalizado
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Agregar Ejercicio");
        dialog.setHeaderText("Ingrese los detalles del nuevo ejercicio:");

        // Crear el layout del diálogo
        VBox dialogContent = new VBox(10);
        dialogContent.setStyle("-fx-padding: 10;");

        // Campo para el nombre del ejercicio
        TextField nameField = new TextField();
        nameField.setPromptText("Nombre del ejercicio");

        // Campo para el tempo
        TextField tempoField = new TextField("3-1-1-0"); // Valor por defecto
        tempoField.setPromptText("Tempo (e.g., 3-1-1-0)");

        // Campo para el tiempo de descanso
        TextField restField = new TextField("60"); // Valor por defecto
        restField.setPromptText("Tiempo de descanso (en segundos)");

        // Agregar los campos al contenido del diálogo
        dialogContent.getChildren().addAll(
                new Label("Nombre del Ejercicio:"),
                nameField,
                new Label("Tempo:"),
                tempoField,
                new Label("Tiempo de Descanso (segundos):"),
                restField
        );

        // Añadir el contenido al diálogo
        dialog.getDialogPane().setContent(dialogContent);

        // Botones de Aceptar y Cancelar
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Manejar el resultado del diálogo
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Validar y obtener los datos ingresados
                String exerciseName = nameField.getText();
                String tempo = tempoField.getText();
                int restTime = Integer.parseInt(restField.getText());

                if (exerciseName == null || exerciseName.trim().isEmpty()) {
                    throw new IllegalArgumentException("El nombre del ejercicio no puede estar vacío.");
                }

                if (tempo == null || tempo.trim().isEmpty()) {
                    throw new IllegalArgumentException("El tempo no puede estar vacío.");
                }

                if (restTime <= 0) {
                    throw new IllegalArgumentException("El tiempo de descanso debe ser mayor que 0.");
                }

                // Crear un nuevo objeto Exercise
                Exercise newExercise = new Exercise();
                newExercise.setName(exerciseName);
                newExercise.setTempo(tempo);
                newExercise.setRestTime(restTime);
                newExercise.setRoutineId(routineId); // Asignar el ID de la rutina

                // Lógica para guardar el nuevo ejercicio en la base de datos
                boolean success = RoutineDAO.addExercise(newExercise, routineDay);
                if (success) {
                    // Mostrar un mensaje de éxito
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Ejercicio Agregado");
                    alert.setHeaderText(null);
                    alert.setContentText("El ejercicio ha sido agregado exitosamente.");
                    alert.showAndWait();

                    actualExerciseClicked.setText("Selecciona un ejercicio");

                    // Actualizar la lista de ejercicios
                    exercises = RoutineDAO.getExercisesByRoutineDay(routineId, routineDay);
                    displayExercises(); // Mostrar los ejercicios actualizados
                } else {
                    // Manejo de errores
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Error al agregar el ejercicio.");
                    alert.setContentText("No se pudo agregar el ejercicio. Intente de nuevo.");
                    alert.showAndWait();
                }
            } catch (NumberFormatException e) {
                // Manejo de errores de formato
                showAlert("Error", "El tiempo de descanso debe ser un número válido.", Alert.AlertType.ERROR);
            } catch (IllegalArgumentException e) {
                // Manejo de validación de datos
                showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    // Manejar la acción de agregar una serie
    @FXML
    private void handleAddSeries() {
        if (currentExercise == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("No se ha seleccionado un ejercicio.");
            alert.setContentText("Por favor, seleccione un ejercicio para agregar una serie.");
            alert.showAndWait();
            return;
        }

        try {
            // Obtener los valores de los campos de texto
            int repetitions = Integer.parseInt(repetitionsField.getText());
            double weight = Double.parseDouble(weightField.getText());

            // Llamar al método para agregar la serie
            addSeriesToExercise(currentExercise, repetitions, weight);

            // Mostrar un mensaje de éxito
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Serie Agregada");
            alert.setHeaderText(null);
            alert.setContentText("La serie ha sido agregada exitosamente.");
            alert.showAndWait();
        } catch (NumberFormatException e) {
            // Manejo de errores de formato
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Datos inválidos.");
            alert.setContentText("Por favor, ingrese valores numéricos válidos para repeticiones y peso.");
            alert.showAndWait();
        }
    }

    // Manejar la acción de actualizar una serie
    @FXML
    private void handleUpdateSeries() {
        // Obtener los valores de los campos de texto
        int repetitions = Integer.parseInt(repetitionsField.getText());
        double weight = Double.parseDouble(weightField.getText());

        // Actualizar los datos de la serie
        currentSet.setRepetitions(repetitions);
        currentSet.setWeight(weight);

        // Llamar al método para actualizar la serie
        updateSeriesInExercise(currentExercise, currentSet);
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    @FXML
    public void handleDeleteExercise(ActionEvent event) {
        if (currentExercise == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("No se ha seleccionado un ejercicio.");
            alert.setContentText("Por favor, seleccione un ejercicio para eliminar.");
            alert.showAndWait();
            return;
        }

        // Confirmar la eliminación
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar Eliminación");
        confirmAlert.setHeaderText("¿Estás seguro de que deseas eliminar el ejercicio " + currentExercise.getName() + "?");
        confirmAlert.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Lógica para eliminar el ejercicio
            boolean success = RoutineDAO.deleteExercise(currentExercise.getId());
            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Ejercicio Eliminado");
                alert.setHeaderText(null);
                alert.setContentText("El ejercicio ha sido eliminado exitosamente.");
                alert.showAndWait();

                actualExerciseClicked.setText("Selecciona un ejercicio");

                // Actualizar la lista de ejercicios
                exercises = RoutineDAO.getExercisesByRoutineDay(routineId, routineDay);
                displayExercises(); // Mostrar los ejercicios actualizados
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error al eliminar el ejercicio.");
                alert.setContentText("No se pudo eliminar el ejercicio. Intente de nuevo.");
                alert.showAndWait();
            }
        }
    }


    public void addSeriesToExercise(Exercise exercise, int repetitions, double weight) {
        int nextSetNumber = exercise.getTotalSets() + 1; // Asumimos que el próximo número de serie es el total de series + 1
        ExerciseSet newSet = new ExerciseSet(0, exercise.getId(), nextSetNumber, repetitions, weight); // ID será generado automáticamente
        if (RoutineDAO.addExerciseSet(newSet)) {
            exercise.addSet(newSet); // Agregar la serie al objeto Exercise
            displayExercises(); // Actualizar la visualización
        } else {
            System.out.println("Error al agregar la serie.");
        }
    }

    public void updateSeriesInExercise(Exercise exercise, ExerciseSet updatedSet) {
        if (RoutineDAO.updateExerciseSet(updatedSet)) {
            // Actualizar la serie en el objeto Exercise
            for (ExerciseSet set : exercise.getSets()) {
                if (set.getId() == updatedSet.getId()) {
                    set.setRepetitions(updatedSet.getRepetitions());
                    set.setWeight(updatedSet.getWeight());
                    break;
                }
            }
            displayExercises(); // Actualizar la visualización
        } else {
            System.out.println("Error al actualizar la serie.");
        }
    }

    @FXML
    public void goBack(ActionEvent event) {
        try {
            // Cargar la vista RoutineDaySelector
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/gearfit/RoutineDaySelector.fxml"));
            Parent mainContent = loader.load();

            // Obtener el controlador de RoutineDaySelector
            RoutineDaySelectorController controller = loader.getController();
            controller.setRoutine(routineToDaySelector); // Asegúrate de que tengas acceso a la rutina actual
            controller.setDays(daysToDaySelector); // Asegúrate de que tengas acceso a los días actuales

            // Reemplazar el contenido del rootPane con el nuevo contenido
            rootPane.getChildren().setAll(mainContent);

            // Anclar a los bordes
            AnchorPane.setTopAnchor(mainContent, 0.0);
            AnchorPane.setBottomAnchor(mainContent, 0.0);
            AnchorPane.setLeftAnchor(mainContent, 0.0);
            AnchorPane.setRightAnchor(mainContent, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setRoutine(Routine routineToTable) {
        this.routineToDaySelector = routineToTable;
    }

    public void setDays(List<String> daysToTable) {
        this.daysToDaySelector = daysToTable;
    }
}