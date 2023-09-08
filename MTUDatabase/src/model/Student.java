package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import controller.StudentController;
import javafx.scene.control.TextInputDialog;

public class Student {
	private String studentID;
	private String name;
	private LocalDate dateOfBirth;
	private String module;
	private String grade;
	private int semester;

	public Student(String studentID, String name, LocalDate dateOfBirth, String module, String grade, int semester) {
		this.studentID = studentID;
		this.name = name;
		this.dateOfBirth = dateOfBirth;
		this.module = module;
		this.grade = grade;
		this.semester = semester;
	}

	public String getStudentID() {
		return studentID;
	}

	public void setStudentID(String studentID) {
		this.studentID = studentID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public int getSemester() {
		return semester;
	}

	public void setSemester(int semester) {
		this.semester = semester;
	}

	public void addModule(String moduleName, int moduleGrade) {
		if (moduleName == null || moduleName.isEmpty()) {
			throw new IllegalArgumentException("Module name cannot be empty or null");
		}
		if (this.module == null || this.module.isEmpty()) {
			this.module = moduleName;
			this.grade = Integer.toString(moduleGrade);
		} else {
			this.module += "," + moduleName;
			this.grade += "," + Integer.toString(moduleGrade);
		}
	}

	public void removeModule(String moduleName) {
		if (module != null && !module.isEmpty()) {
			String[] modules = module.split(",");
			List<String> moduleList = new ArrayList<>(Arrays.asList(modules));
			if (moduleList.size() == 1) {
				// If there is only one module, remove it
				this.module = null;
			} else {
				// If there are multiple modules, ask the user to select which one to remove
				TextInputDialog dialog = new TextInputDialog();
				dialog.setTitle("Remove Module");
				dialog.setHeaderText(null);
				dialog.setContentText("Select the module to remove:");
				dialog.getEditor().setText(modules[0]);
				Optional<String> result = dialog.showAndWait();
				if (result.isPresent()) {
					String moduleToRemove = result.get().trim();
					if (moduleList.contains(moduleToRemove)) {
						moduleList.remove(moduleToRemove);
						String newModuleString = String.join(",", moduleList);
						if (newModuleString.isEmpty()) {
							this.module = null;
						} else {
							this.module = newModuleString;
						}
					}
				}
			}
		}
	}

}
