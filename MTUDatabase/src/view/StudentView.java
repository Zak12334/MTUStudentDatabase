package view;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

import controller.StudentController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Student;

public class StudentView extends Application {
	private TableView<Student> studentTable;
	private StudentController studentController;

	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage primaryStage) throws Exception {
		// Initialize the view
		initUI(primaryStage);

		// Initialize the controller
		studentController = new StudentController();

		// Fetch student records from the database and create a mutable list
		ObservableList<Student> studentList = studentController.fetchStudents();
		ObservableList<Student> mutableStudentList = FXCollections.observableArrayList(studentList);

		// Set the mutable list to the table view
		studentTable.setItems(mutableStudentList);
	}

	private void initUI(Stage primaryStage) {
		// Create table columns
		TableColumn<Student, String> idColumn = new TableColumn<>("ID");
		idColumn.setCellValueFactory(new PropertyValueFactory<>("studentID"));

		TableColumn<Student, String> nameColumn = new TableColumn<>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		nameColumn.setPrefWidth(120);

		TableColumn<Student, String> dobColumn = new TableColumn<>("Date of Birth");
		dobColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));

		TableColumn<Student, String> modulesColumn = new TableColumn<>("Modules");
		modulesColumn.setCellValueFactory(new PropertyValueFactory<>("module"));
		modulesColumn.setPrefWidth(150);

		TableColumn<Student, String> gradeColumn = new TableColumn<>("Grade");
		gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));

		TableColumn<Student, String> semesterColumn = new TableColumn<>("Current Semester");
		semesterColumn.setCellValueFactory(new PropertyValueFactory<>("semester"));
		semesterColumn.setPrefWidth(150);

		// Create table view
		studentTable = new TableView<>();
		studentTable.getColumns().addAll(idColumn, nameColumn, dobColumn, modulesColumn, gradeColumn, semesterColumn);
		studentTable.setPrefWidth(650);
		studentTable.setPrefHeight(300);
		studentTable.setEditable(true);

		// Create buttons for adding and removing students
		Button addButton = new Button("Add");
		Button removeButton = new Button("Remove");
		Button addModuleButton = new Button("Add Module");
		Button removeModuleButton = new Button("Remove Module");
		Button changeGradeButton = new Button("Alter Grade");
		Button memoryLeakButton = new Button("Simulate Memory Leak");

		// Add event handlers for the buttons
		addButton.setOnAction(e -> handleAddButton());
		removeButton.setOnAction(e -> handleRemoveButton());
		addModuleButton.setOnAction(e -> handleAddModuleButton());
		removeModuleButton.setOnAction(e -> handleRemoveModuleButton());
		changeGradeButton.setOnAction(e -> handleChnageGradeButton());
		memoryLeakButton.setOnAction(e -> {
			while (true) {
				Student student = new Student("id", "name", LocalDate.now(), "module", "grade", 1);
			}
		});

		// Create GridPane for table view and buttons
		GridPane gridPane = new GridPane();
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		gridPane.add(studentTable, 0, 0, 2, 1);
		gridPane.add(addButton, 0, 1);
		gridPane.add(removeButton, 1, 1);
		gridPane.add(addModuleButton, 0, 2);
		gridPane.add(removeModuleButton, 1, 2);
		gridPane.add(changeGradeButton, 0, 3);
		gridPane.add(memoryLeakButton, 0, 4);
		gridPane.setMinSize(650, 500);

		// Create a VBox to hold the GridPane
		VBox vbox = new VBox(10);
		vbox.getChildren().addAll(gridPane);

		// Create scene and set it on the stage
		Scene scene = new Scene(vbox);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void handleChnageGradeButton() {
		Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
		if (selectedStudent != null) {
			TextInputDialog dialog = new TextInputDialog(selectedStudent.getGrade());
			dialog.setTitle("Change Grade");
			dialog.setHeaderText(null);
			dialog.setContentText("Enter new grade(s) separated by comma:");

			Optional<String> result = dialog.showAndWait();
			if (result.isPresent()) {
				String newGrade = result.get().replaceAll("\\s+", "");
				if (newGrade.matches("^\\d+(,\\d+)*$")) {
					// Update the student's grade with the new value
					selectedStudent.setGrade(newGrade);
					studentController.updateStudentGrade(selectedStudent, newGrade);
					studentTable.refresh();
				} else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText(null);
					alert.setContentText("Invalid grade. Please enter one or more comma-separated numbers.");
					alert.showAndWait();
				}
			}
		} else {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("No Student Selected");
			alert.setHeaderText(null);
			alert.setContentText("Please select a student from the table.");
			alert.showAndWait();
		}
	}

	public void handleRemoveModuleButton() {
		// Get the selected student from the table view
		Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
		if (selectedStudent != null) {
			// Create a new stage for removing a module
			Stage removeModuleStage = new Stage();
			removeModuleStage.setTitle("Remove Module");

			// Create a label and combo box for selecting the module to remove
			Label moduleLabel = new Label("Select a module to remove:");
			ComboBox<String> moduleComboBox = new ComboBox<>(
					FXCollections.observableArrayList(selectedStudent.getModule()));

			// Create a button for removing the module
			Button removeButton = new Button("Remove");
			removeButton.setOnAction(e -> {
				// Get the selected module
				String moduleName = moduleComboBox.getSelectionModel().getSelectedItem();

				// Remove the module from the selected student
				selectedStudent.removeModule(moduleName);

				// Update the database
				studentController.updateStudent(selectedStudent);

				// Update the table view
				studentTable.refresh();

				// Close the remove module window
				removeModuleStage.close();
			});

			// Create a VBox to hold the input field and the remove button
			VBox vbox = new VBox(10);
			vbox.getChildren().addAll(moduleLabel, moduleComboBox, removeButton);

			// Create a scene and set it on the stage
			Scene scene = new Scene(vbox, 500, 500);
			removeModuleStage.setScene(scene);
			removeModuleStage.show();
		} else {
			// Alert the user to select a student
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("No Student Selected");
			alert.setHeaderText(null);
			alert.setContentText("Please select a student to remove a module.");
			alert.showAndWait();
		}
	}

	public void handleAddModuleButton() {
		// Get the selected student from the table view
		Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
		if (selectedStudent != null) {
			// Create a new stage for adding a module and its grade
			Stage addModuleStage = new Stage();
			addModuleStage.setTitle("Add Module");

			// Create labels and text fields for inputting module and grade details
			Label moduleLabel = new Label("Module Name:");
			TextField moduleTextField = new TextField();
			Label gradeLabel = new Label("Grade:");
			TextField gradeTextField = new TextField();

			// Create a button for adding the module and its grade
			Button addButton = new Button("Add");
			addButton.setOnAction(e -> {
				// Get the input values
				String moduleName = moduleTextField.getText();
				int moduleGrade = Integer.parseInt(gradeTextField.getText());

				// Add the new module and its grade to the selected student
				selectedStudent.addModule(moduleName, moduleGrade);

				studentController.addModuleToStudent(selectedStudent, moduleName);

				// Update the table view with the new data
				studentTable.refresh();

				// Close the add module window
				addModuleStage.close();

				// Update the table view
				studentTable.refresh();

				// Close the add module window
				addModuleStage.close();

			});

			// Create a VBox to hold the input fields and the add button
			VBox vbox = new VBox(10);
			vbox.getChildren().addAll(moduleLabel, moduleTextField, gradeLabel, gradeTextField, addButton);

			// Create a scene and set it on the stage
			Scene scene = new Scene(vbox, 500, 500);
			addModuleStage.setScene(scene);
			addModuleStage.show();
		} else {
			// Alert the user to select a student
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("No Student Selected");
			alert.setHeaderText(null);
			alert.setContentText("Please select a student to add a module.");
			alert.showAndWait();
		}
	}

	private void handleAddButton() {
		// Create a new stage for adding a student
		Stage addStudentStage = new Stage();
		addStudentStage.setTitle("Add Student");

		// Create labels and text fields for inputting student details
		Label idLabel = new Label("ID:");
		TextField idTextField = new TextField();
		Label nameLabel = new Label("Name:");
		TextField nameTextField = new TextField();
		Label dobLabel = new Label("Date of Birth (YYYY-MM-DD):");
		TextField dobTextField = new TextField();
		Label moduleLabel = new Label("Modules:");
		TextField moduleTextField = new TextField();
		Label gradeLabel = new Label("Grade:");
		TextField gradeTextField = new TextField();
		Label semesterLabel = new Label("Current Semester:");
		TextField semesterTextField = new TextField();

		// Create a button for adding the student
		Button addButton = new Button("Add");
		addButton.setOnAction(e -> {
			// Get the input values
			String studentID = idTextField.getText();
			String name = nameTextField.getText();
			LocalDate dateOfBirth = LocalDate.parse(dobTextField.getText());
			String module = moduleTextField.getText();
			String grade = gradeTextField.getText();
			int semester = Integer.parseInt(semesterTextField.getText());

			// Create a new student object with the input values
			Student newStudent = new Student(studentID, name, dateOfBirth, module, grade, semester);

			// Add the new student to the table view and the database
			studentController.addStudent(newStudent);

			// Add the new student to the observable list that is bound to the table view
			studentTable.getItems().add(newStudent);

			// Close the add student window
			addStudentStage.close();
		});

		// Create a VBox to hold the input fields and the add button
		VBox vbox = new VBox(10);
		vbox.getChildren().addAll(idLabel, idTextField, nameLabel, nameTextField, dobLabel, dobTextField, moduleLabel,
				moduleTextField, gradeLabel, gradeTextField, semesterLabel, semesterTextField, addButton);

		// Create a scene and set it on the stage
		Scene scene = new Scene(vbox, 500, 500);
		addStudentStage.setScene(scene);
		addStudentStage.show();
	}

	private void handleRemoveButton() {
		// Get the selected student from the table view
		Student selectedStudent = studentTable.getSelectionModel().getSelectedItem();
		if (selectedStudent != null) {
			// Remove the student from the table view and the database
			studentController.removeStudent(selectedStudent);

			// Remove the student from the observable list that is bound to the table view
			studentTable.getItems().remove(selectedStudent);
		} else {
			// Display an error message if no student is selected
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("No student selected");
			alert.setContentText("Please select a student to remove.");
			alert.showAndWait();
		}
	}

	public TableView<Student> getStudentTable() {
		return studentTable;
	}

}