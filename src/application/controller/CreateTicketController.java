package application.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import application.CommonObjects;
import application.Main;

public class CreateTicketController {

	private CommonObjects commonObjects = CommonObjects.getInstance();
	@FXML
	AnchorPane ticketMenuBox;
	@FXML
	TextField ticketName;
	@FXML
	TextArea ticketDescription;
	@FXML
	ChoiceBox<String> projectBox;
	private Stage stage;
	private Scene scene;
	private Parent root;
	private List<List<Object>> projects;

	@FXML
	public void initialize() {
		projects = commonObjects.readProjectsFromDatabase();
		for (List<Object> project : projects) {
			String projectName = (String) project.get(0);
			projectBox.getItems().add(projectName);
		}
	}

	@FXML
	public void confirmButtonPressedOP(ActionEvent event) {
		if(ticketName.getText().equals("") || projectBox.getSelectionModel().selectedItemProperty() == null) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
		    alert.setHeaderText("Enter Required Information!");
		    alert.showAndWait();
		} else {
			Connection connection = Main.getConnection(); // Access the database connection from Main

			if (connection != null) {
				try {
					String ticketNameText = ticketName.getText();
					String ticketDescriptionText = ticketDescription.getText();

					// Prepare an SQL INSERT statement for the Tickets table
					String sql = "INSERT INTO Tickets (project_id, ticket_title, ticket_desc) VALUES (?, ?, ?)";
					PreparedStatement preparedStatement = connection.prepareStatement(sql);

					// Extract project_id from the ChoiceBox selection
					int projectIndex = projectBox.getSelectionModel().getSelectedIndex();
					int projectID = (int) projects.get(projectIndex).get(2); // Corrected data type

					preparedStatement.setInt(1, projectID);
					preparedStatement.setString(2, ticketNameText);
					preparedStatement.setString(3, ticketDescriptionText);

					// Execute the INSERT statement
					preparedStatement.executeUpdate();

					// Close the prepared statement
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				commonObjects.backToMainMenu(event, stage, scene, root);
			}
		}
	}

	@FXML
	public void backButtonPressedOP(ActionEvent event) {
		commonObjects.backToMainMenu(event, stage, scene, root);
	}
}
