package controller;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Student;

public class StudentController {
	private Connection connection;

	public StudentController() {
		// Set up the JDBC connection
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/citstudentrecord";
			String username = "root";
			String password = "Zakosman1998@";
			connection = DriverManager.getConnection(url, username, password);
			System.out.println("Connected to database");
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public ObservableList<Student> fetchStudents() {
		ObservableList<Student> studentList = FXCollections.observableArrayList();
		try {
			String query = "SELECT * FROM students";
			PreparedStatement statement = connection.prepareStatement(query);
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {
				String studentID = resultSet.getString("student_id");
				String name = resultSet.getString("name");
				LocalDate dateOfBirth = resultSet.getDate("date_of_birth").toLocalDate();
				String module = resultSet.getString("modules");
				String grade = resultSet.getString("grade");
				int currentSemester = resultSet.getInt("current_semester");
				Student student = new Student(studentID, name, dateOfBirth, module, grade, currentSemester);
				studentList.add(student);
			}

			resultSet.close();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return studentList;
	}

	public void addStudent(Student student) {
		String sql = "INSERT INTO students (student_id, name, date_of_birth, modules, grade, current_semester) VALUES (?, ?, ?, ?, ?, ?)";

		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			// Set the parameter values
			statement.setString(1, student.getStudentID());
			statement.setString(2, student.getName());
			statement.setDate(3, Date.valueOf(student.getDateOfBirth()));
			statement.setString(4, student.getModule());
			statement.setString(5, student.getGrade());
			statement.setInt(6, student.getSemester());

			// Execute the statement
			statement.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public void updateStudent(Student student) {
	    try {
	        String query = "UPDATE students SET name = ?, date_of_birth = ?, modules = ?, grade = ?, current_semester = ? WHERE student_id = ?";
	        PreparedStatement statement = connection.prepareStatement(query);
	        statement.setString(1, student.getName());
	        statement.setDate(2, java.sql.Date.valueOf(student.getDateOfBirth()));
	        statement.setString(3, student.getModule());
	        String grade = student.getGrade();
	        if (grade.length() > 4) { // check if grade string is too long
	            grade = grade.substring(0, 4); // truncate the string if necessary
	        }
	        statement.setString(4, grade);
	        statement.setInt(5, student.getSemester());
	        statement.setString(6, student.getStudentID());
	        statement.executeUpdate();
	        statement.close();
	    } catch (SQLException ex) {
	        ex.printStackTrace();
	    }
	}

	public void removeModuleFromStudent(Student student, String moduleName) {
		try {
			String query = "UPDATE students SET modules = REPLACE(modules, ?, '') WHERE student_id = ?";
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, moduleName);
			statement.setString(2, student.getStudentID());
			statement.executeUpdate();
			statement.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public void removeStudent(Student student) {
		try {
			String query = "DELETE FROM students WHERE student_id = ?";
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, student.getStudentID());
			statement.executeUpdate();
			statement.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addModuleToStudent(Student student, String moduleName) {
		try {
			String query = "UPDATE students SET modules = CONCAT(modules, ',', ?) WHERE student_id = ?";
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, moduleName);
			statement.setString(2, student.getStudentID());
			statement.executeUpdate();
			statement.close();

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public void addModuleToStudent(Student student, String moduleName, float grade) {
		try {
			String query = "UPDATE students SET modules = CONCAT(modules, ',', ?), grades = CONCAT(grades, ', ', ?) WHERE student_id = ?";
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, moduleName);
			statement.setFloat(2, grade);
			statement.setString(3, student.getStudentID());
			statement.executeUpdate();
			statement.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public void updateStudentGrade(Student student, String newGrade) {
		try {
			String query = "UPDATE students SET grade = ? WHERE student_id = ?";
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setString(1, newGrade);
			statement.setString(2, student.getStudentID());
			statement.executeUpdate();
			statement.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public void closeConnection() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}