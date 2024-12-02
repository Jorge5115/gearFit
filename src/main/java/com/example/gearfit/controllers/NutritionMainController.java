package com.example.gearfit.controllers;

import com.example.gearfit.connections.SessionManager;
import com.example.gearfit.models.DailyFood;
import com.example.gearfit.models.Food;
import com.example.gearfit.repositories.NutritionDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NutritionMainController {

    private int userId;

    public Label currentDateLabel;

    public Label totalCaloriesLabel;

    public VBox vboxBreakfastList, vboxLunchList, vboxSnackList, vboxDinnerList;

    public Label currentFatsLabel, currentCarbosLabel, currentProteinsLabel, currentCaloriesLabel;

    private double totalCalories;

    private double currentFats, currentCarbs, currentProteins, currentCalories;

    @FXML
    private VBox vboxFoodRegisteredList;  // VBox para mostrar los alimentos registrados

    @FXML
    public void initialize() {
        userId = SessionManager.getCurrentUser().getId();
        totalCalories = SessionManager.getCurrentUser().getCalories();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy"); // Formato de fecha
        currentDateLabel.setText(LocalDate.now().format(dateFormatter));

        if (totalCalories == 0) {
            showAlert("Ajustes de Usuario", "Por favor, ve a la pestaña de ajustes y establece tus calorías diarias objetivo.", Alert.AlertType.WARNING);
        } else {
            totalCaloriesLabel.setText("Objetivo: " + String.valueOf(formatNumber(totalCalories)) + " kcal");
        }

        updateFoodsVBox();
        updateMealsVBox();
    }

    private String formatNumber(double number) {
        // Verifica si el valor es un entero (sin decimales)
        if (number % 1 == 0) {
            return String.valueOf((int) number); // Convierte a entero si no hay decimales
        } else {
            return String.format("%.1f", number); // Formato con un decimal
        }
    }

    // Actualizar los VBox de desayuno y almuerzo
    private void updateMealsVBox() {
        // Reiniciar los valores acumulativos
        currentFats = 0;
        currentCarbs = 0;
        currentProteins = 0;
        currentCalories = 0;

        LocalDate today = LocalDate.now();
        List<DailyFood> dailyFoods = NutritionDAO.getDailyFoodsByUserIdAndDate(userId, today);

        // Limpiar los VBox
        vboxBreakfastList.getChildren().clear();
        vboxLunchList.getChildren().clear();
        vboxSnackList.getChildren().clear();
        vboxDinnerList.getChildren().clear();

        // Agregar alimentos consumidos a los VBox correspondientes
        for (DailyFood dailyFood : dailyFoods) {
            // Obtener la información del alimento usando su ID
            Food userFood = NutritionDAO.getUserFoodById(dailyFood.getFoodId());

            if (userFood != null) {
                // Calcular los valores nutricionales en función de los gramos consumidos
                double gramsConsumed = dailyFood.getGrams();
                double fats = (userFood.getFats() / 100) * gramsConsumed;
                double carbs = (userFood.getCarbs() / 100) * gramsConsumed;
                double proteins = (userFood.getProtein() / 100) * gramsConsumed;
                double calories = (userFood.getCalories() / 100) * gramsConsumed;

                // Crear el texto del botón
                String buttonText = String.format("%s: %.1fg - G: %.1fg, C: %.1fg, P: %.1fg, Kcal: %.1f",
                        userFood.getName(), gramsConsumed, fats, carbs, proteins, calories);

                Button mealButton = new Button(buttonText);
                mealButton.getStyleClass().add("meal-button");

                // Agregar el manejador de clics para el botón de comida
                mealButton.setOnAction(event -> handleMealButtonClicked(dailyFood, userFood.getName()));

                // Agregar el botón al VBox correspondiente
                if (dailyFood.getMealType().equalsIgnoreCase("Desayuno")) {
                    vboxBreakfastList.getChildren().add(mealButton);
                } else if (dailyFood.getMealType().equalsIgnoreCase("Almuerzo")) {
                    vboxLunchList.getChildren().add(mealButton);
                } else if (dailyFood.getMealType().equalsIgnoreCase("Merienda")) {
                    vboxSnackList.getChildren().add(mealButton);
                } else if (dailyFood.getMealType().equalsIgnoreCase("Cena")) {
                    vboxDinnerList.getChildren().add(mealButton);
                }

                // Acumular los valores
                currentFats += fats;
                currentCarbs += carbs;
                currentProteins += proteins;
                currentCalories += calories;
            } else {
                System.out.println("No se encontró el alimento con ID: " + dailyFood.getFoodId());
            }
        }

        // Actualizar las etiquetas con los valores calculados
        currentFatsLabel.setText(String.format("%.1f", currentFats));
        currentCarbosLabel.setText(String.format("%.1f", currentCarbs));
        currentProteinsLabel.setText(String.format("%.1f", currentProteins));
        currentCaloriesLabel.setText(String.format("%.1f", currentCalories));
    }

    private void handleMealButtonClicked(DailyFood dailyFood, String foodName) {
        // Crear una ventana emergente para confirmar la eliminación del alimento diario
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar Alimento Diario");
        alert.setHeaderText("¿Deseas eliminar " + foodName + " de tu registro diario?");
        alert.setContentText("Esta acción no se puede deshacer.");

        ButtonType deleteButtonType = new ButtonType("Eliminar", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(deleteButtonType, cancelButtonType);

        // Mostrar el alert y esperar la respuesta del usuario
        alert.showAndWait().ifPresent(response -> {
            if (response == deleteButtonType) {
                // Si el usuario confirma, eliminar el alimento diario
                boolean success = NutritionDAO.deleteDailyFood(dailyFood.getId());
                if (success) {
                    // Actualizar la vista después de la eliminación
                    updateMealsVBox();
                    System.out.println(foodName + " ha sido eliminado exitosamente.");
                } else {
                    System.out.println("Error al eliminar " + foodName + ".");
                }
            }
        });
    }

    // Manejar el clic en el botón "Añadir Alimento"
    @FXML
    public void handleAddNewFood(ActionEvent event) {
        // Crear una ventana emergente (Dialog) para agregar un nuevo alimento
        Dialog<Food> dialog = new Dialog<>();
        dialog.setTitle("Añadir Nuevo Alimento");

        // Establecer el tipo de botón para la ventana emergente
        ButtonType addButtonType = new ButtonType("Añadir", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, cancelButtonType);

        // Crear los campos de texto para ingresar los datos del alimento
        GridPane grid = new GridPane();
        TextField nameField = new TextField();
        nameField.setPromptText("Nombre del alimento");
        TextField fatsField = new TextField();
        fatsField.setPromptText("Grasas por 100g");
        TextField carbsField = new TextField();
        carbsField.setPromptText("Carbohidratos por 100g");
        TextField proteinsField = new TextField();
        proteinsField.setPromptText("Proteínas por 100g");
        TextField kcalField = new TextField();
        kcalField.setPromptText("Calorías por 100g");

        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Grasas:"), 0, 1);
        grid.add(fatsField, 1, 1);
        grid.add(new Label("Carbohidratos:"), 0, 2);
        grid.add(carbsField, 1, 2);
        grid.add(new Label("Proteínas:"), 0, 3);
        grid.add(proteinsField, 1, 3);
        grid.add(new Label("Calorías:"), 0, 4);
        grid.add(kcalField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Convertir los valores de la ventana emergente a un objeto Food
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    String name = nameField.getText();
                    double fats = Double.parseDouble(fatsField.getText());
                    double carbs = Double.parseDouble(carbsField.getText());
                    double proteins = Double.parseDouble(proteinsField.getText());
                    double kcal = Double.parseDouble(kcalField.getText());

                    // Crear el objeto Food
                    Food food = new Food(0, userId, name, fats, carbs, proteins, kcal);

                    // Agregar el alimento a la base de datos
                    boolean success = NutritionDAO.addFood(food);
                    if (success) {
                        // Asignar el ID generado a la entidad
                        return food;
                    } else {
                        showAlert("Error", "No se pudo agregar el alimento.", Alert.AlertType.ERROR);
                    }
                } catch (NumberFormatException e) {
                    showAlert("Error", "Por favor, ingrese valores válidos para los nutrientes.", Alert.AlertType.ERROR);
                }
            }
            return null;
        });
        dialog.showAndWait();

        // Después de agregar el alimento, actualizar la lista
        updateFoodsVBox();
    }

    // Actualizar el VBox con los alimentos registrados
    private void updateFoodsVBox() {
        vboxFoodRegisteredList.getChildren().clear(); // Limpiar la lista

        // Obtener los alimentos de la base de datos para el usuario
        List<Food> foods = NutritionDAO.getFoodsByUserId(userId);

        // Agregar cada alimento como un botón
        for (Food food : foods) {
            Button foodButton = new Button(food.getName());
            foodButton.getStyleClass().add("food-button");

            // Manejar clic en el botón
            foodButton.setOnAction(event -> handleFoodButtonClicked(food));
            vboxFoodRegisteredList.getChildren().add(foodButton);
        }
    }

    // Maneja la acción de editar o eliminar un alimento
    private void handleFoodButtonClicked(Food food) {
        // Crear una ventana emergente para editar o eliminar el alimento
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Editar/Eliminar Alimento");
        alert.setHeaderText("¿Qué deseas hacer con el alimento?");
        alert.setContentText("Selecciona una opción:");

        ButtonType editButtonType = new ButtonType("Editar");
        ButtonType deleteButtonType = new ButtonType("Eliminar");
        ButtonType cancelButtonType = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(editButtonType, deleteButtonType, cancelButtonType);

        alert.showAndWait().ifPresent(response -> {
            if (response == editButtonType) {
                openEditFoodDialog(food);  // Llamar al método para editar el alimento
            } else if (response == deleteButtonType) {
                boolean success = NutritionDAO.deleteFood(food.getId());  // Eliminar el alimento de la base de datos
                if (success) {
                    updateFoodsVBox();  // Actualizar la lista después de eliminarlo
                    updateMealsVBox();
                } else {
                    showAlert("Error", "No se pudo eliminar el alimento.", Alert.AlertType.ERROR);
                }
            }
        });
    }

    // Abrir una ventana emergente para editar los datos del alimento
    private void openEditFoodDialog(Food food) {
        Dialog<Food> dialog = new Dialog<>();
        dialog.setTitle("Editar Alimento");

        // Botones
        ButtonType saveButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        // Campos de edición
        GridPane grid = new GridPane();
        TextField nameField = new TextField(food.getName());
        TextField fatsField = new TextField(String.valueOf(food.getFats()));
        TextField carbsField = new TextField(String.valueOf(food.getCarbs()));
        TextField proteinsField = new TextField(String.valueOf(food.getProtein()));
        TextField kcalField = new TextField(String.valueOf(food.getCalories()));

        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Grasas:"), 0, 1);
        grid.add(fatsField, 1, 1);
        grid.add(new Label("Carbohidratos:"), 0, 2);
        grid.add(carbsField, 1, 2);
        grid.add(new Label("Proteínas:"), 0, 3);
        grid.add(proteinsField, 1, 3);
        grid.add(new Label("Calorías:"), 0, 4);
        grid.add(kcalField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                food.setName(nameField.getText());
                food.setFatsPer100g(Double.parseDouble(fatsField.getText()));
                food.setCarbsPer100g(Double.parseDouble(carbsField.getText()));
                food.setProteinsPer100g(Double.parseDouble(proteinsField.getText()));
                food.setKcalPer100g(Double.parseDouble(kcalField.getText()));

                // Actualizar el alimento en la base de datos
                boolean success = NutritionDAO.updateFood(food);
                if (success) {
                    return food;
                } else {
                    showAlert("Error", "No se pudo guardar los cambios.", Alert.AlertType.ERROR);
                }
            }
            return null;
        });

        dialog.showAndWait();
        updateFoodsVBox();
        updateMealsVBox();
    }

    // Mostrar alertas
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleAddBreakfast(ActionEvent event) {
        addMeal("Desayuno");
    }

    public void handleAddLunch(ActionEvent event) {
        addMeal("Almuerzo");
    }

    public void handleAddSnack(ActionEvent event) {
        addMeal("Merienda");
    }

    public void handleAddDinner(ActionEvent event) {
        addMeal("Cena");
    }

    private void addMeal(String mealType) {
        // Crear una ventana emergente (Dialog) para agregar un nuevo registro de comida
        Dialog<DailyFood> dialog = new Dialog<>();
        dialog.setTitle("Añadir " + mealType);

        // Establecer el tipo de botón para la ventana emergente
        ButtonType addButtonType = new ButtonType("Añadir", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, cancelButtonType);

        // Campos de texto para ingresar los datos de la comida
        GridPane grid = new GridPane();
        ComboBox<Food> foodComboBox = new ComboBox<>();
        foodComboBox.getItems().addAll(NutritionDAO.getFoodsByUserId(userId));
        TextField gramsField = new TextField();
        gramsField.setPromptText("Gramos consumidos");

        grid.add(new Label("Alimento:"), 0, 0);
        grid.add(foodComboBox, 1, 0);
        grid.add(new Label("Gramos:"), 0, 1);
        grid.add(gramsField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Convertir los valores de la ventana emergente a un objeto DailyFood
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    Food selectedFood = foodComboBox.getValue();
                    double grams = Double.parseDouble(gramsField.getText());

                    if (selectedFood != null) {
                        // Crear el objeto DailyFood
                        DailyFood dailyFood = new DailyFood(0, selectedFood.getId(), LocalDate.now(), mealType, grams);

                        // Agregar el registro de comida a la base de datos
                        boolean success = NutritionDAO.addDailyFood(dailyFood);

                        if (success) {
                            return dailyFood;
                        } else {
                            showAlert("Error", "No se pudo agregar el registro de comida.", Alert.AlertType.ERROR);
                        }
                    } else {
                        showAlert("Error", "Por favor, selecciona un alimento.", Alert.AlertType.ERROR);
                    }
                } catch (NumberFormatException e) {
                    showAlert("Error", "Por favor, ingrese un valor válido para los gramos.", Alert.AlertType.ERROR);
                }
            }
            return null;
        });
        dialog.showAndWait();
        updateMealsVBox();
    }

}
