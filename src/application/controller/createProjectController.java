package application.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import application.CommonObjects;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.Node;

public class createProjectController {

	private CommonObjects commonObjects = CommonObjects.getInstance();
	@FXML
	AnchorPane projectMenuBox;

	private Stage stage;
	private Scene scene;
	private Parent root;
	@FXML
	private TextField projectName;
	@FXML
	private DatePicker datePick;
	@FXML
	private TextArea projectDesc;
	@FXML
	private Button confirmButton;

	@FXML
	public void initialize() {
		datePick.setValue(LocalDate.now());
		confirmButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				confirmNewProjectOp();
			}
		});
	}

	@FXML
	public void backFromCreateProjectOp(ActionEvent event) {
		try {
			root = FXMLLoader.load(getClass().getClassLoader().getResource("view/Main.fxml"));
			stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void dateEnteredOp() {

	}

	private Connection connection;

	@FXML
	public void confirmNewProjectOp() {
		if (connectToDatabase()) {
			try {
				// Prepare an SQL INSERT statement
				String sql = "INSERT INTO Projects (project_name, date, project_desc) VALUES (?, ?, ?)";
				PreparedStatement preparedStatement = connection.prepareStatement(sql);
				preparedStatement.setString(1, projectName.getText());
				preparedStatement.setString(2, datePick.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
				preparedStatement.setString(3, projectDesc.getText());
				preparedStatement.executeUpdate();

				// Close the prepared statement and database connection
				preparedStatement.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean connectToDatabase() {
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:data/BugDB.db");
			System.out.println("Database connection established successfully.");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Failed to establish a database connection.");
			return false;
		}
	}

}
