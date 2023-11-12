// package application.controller;

// import java.io.IOException;
// import java.util.List;
// import application.CommonObjects;
// import javafx.event.ActionEvent;
// import javafx.fxml.FXML;
// import javafx.scene.layout.AnchorPane;
// import javafx.stage.Stage;
// import javafx.scene.control.TextField;
// import javafx.scene.control.TextArea;
// import javafx.scene.Parent;
// import javafx.scene.Scene;
// import javafx.scene.control.ChoiceBox;

// public class CreateCommentController {

// 	private CommonObjects commonObjects = CommonObjects.getInstance();
// 	@FXML
// 	AnchorPane ticketMenuBox;
// 	@FXML
// 	TextField ticketName;
// 	@FXML
// 	TextArea ticketDescription;
// 	@FXML
// 	ChoiceBox<String> projectBox;
// 	@FXML
// 	ChoiceBox<String> ticketBox;
// 	private Stage stage;
// 	private Scene scene;
// 	private Parent root;

// 	@FXML
// 	public void initialize() throws IOException {
// 		String projectPath = "./data/saved-projects.csv"; // Provide the path to your CSV file
// 		String ticketPath = "./data/saved-tickets.csv";
// 		List<List<String>> projects = commonObjects.readProjectNamesAndSerialNumber(projectPath);

// 		// Display project names in a ListView
// 		for (int i = 0; i < projects.size(); i++) {
// 			projectBox.getItems().add(projects.get(i).get(0) + "," + projects.get(i).get(1));
// 		}

// 		projectBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
// 			try {
// 				updateTicketBox(ticketPath, projectBox.getValue());
// 			} catch (IOException e) {
// 				// TODO Auto-generated catch block
// 				e.printStackTrace();
// 			}
// 		});
// 	}

// 	private void updateTicketBox(String filePath, String projectId) throws IOException {
// 		ticketBox.getItems().clear();
// 		List<String> tickets = commonObjects.getTicketNames(filePath, projectId);
// 		ticketBox.getItems().addAll(tickets);
// 	}

// 	@FXML
// 	public void confirmButtonPressedOP(ActionEvent event) {
// 		commonObjects.backToMainMenu(event, stage, scene, root);
// 	}

// 	@FXML
// 	public void backButtonPressedOP(ActionEvent event) {
// 		commonObjects.backToMainMenu(event, stage, scene, root);
// 	}

// }

package application.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.CommonObjects;
import application.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class CreateCommentController {

	private CommonObjects commonObjects = CommonObjects.getInstance();

	@FXML
	AnchorPane ticketMenuBox;
	@FXML
	TextField date; // Assuming 'date' is used for comment date
	@FXML
	TextArea commentDescription;
	@FXML
	ChoiceBox<String> projectBox;
	@FXML
	ChoiceBox<String> ticketBox;
	private Stage stage;
	private List<List<Object>> projects;

	@FXML
	public void initialize() throws IOException {
		// Populate projectBox with project names from the database
		projects = commonObjects.readProjectsFromDatabase();
		for (List<Object> project : projects) {
			String projectName = (String) project.get(0);
			projectBox.getItems().add(projectName);
		}
		projectBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
			try {
				updateTicketBox(newVal);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	private void updateTicketBox(String selectedProject) throws IOException {
		ticketBox.getItems().clear();
		// Assuming you have a method to fetch tickets for a project from the database
		List<String> tickets = readTicketsForProjectFromDatabase(selectedProject);
		ticketBox.getItems().addAll(tickets);
	}

	private List<String> readTicketsForProjectFromDatabase(String selectedProject) {
		List<String> ticketTitles = new ArrayList<>();

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connection = Main.getConnection();

			// SQL query to fetch ticket titles for the selected project
			String sql = "SELECT ticket_title FROM Tickets WHERE project_id = (SELECT project_id FROM Projects WHERE project_name = ?)";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, selectedProject);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				String ticketTitle = resultSet.getString("ticket_title");
				ticketTitles.add(ticketTitle);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// Close resources in the reverse order of their creation to avoid resource
			// leaks
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					e.printStackTrace(); // Handle or log the exception as needed
				}
			}
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace(); // Handle or log the exception as needed
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace(); // Handle or log the exception as needed
				}
			}
		}

		return ticketTitles;
	}

	@FXML
	public void confirmButtonPressedOP(ActionEvent event) {
		// Assuming you want to save the comment to the database
		saveCommentToDatabase();
		commonObjects.backToMainMenu(event, stage, null, null);
	}

	private void saveCommentToDatabase() {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			// Obtain the connection from the Main class or your database utility class
			connection = Main.getConnection();

			// Check if the connection is closed; if closed, log the issue
			if (connection.isClosed()) {
				System.err.println("Database connection is closed.");
				// You may consider reopening the connection here if needed
			}

			// SQL query to insert a new comment
			String sql = "INSERT INTO Comments (ticket_id, comment_text, comment_date) VALUES (?, ?, ?)";
			preparedStatement = connection.prepareStatement(sql);

			// Assuming ticketBox.getValue() returns the selected ticket title
			String selectedTicketTitle = ticketBox.getValue();

			// Assuming you have a method to get the ticket ID by title
			int selectedTicketId = getTicketIdByTitle(selectedTicketTitle);

			preparedStatement.setInt(1, selectedTicketId);
			preparedStatement.setString(2, commentDescription.getText());
			preparedStatement.setString(3, date.getText());

			// Execute the update
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace(); // Handle or log the exception as needed
		} finally {
			// Close resources in the reverse order of their creation to avoid resource
			// leaks
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace(); // Handle or log the exception as needed
				}
			}
			// Do not close the connection here; let it be managed by the application
		}
	}

	private int getTicketIdByTitle(String ticketTitle) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			// Obtain the connection from the Main class or your database utility class
			connection = Main.getConnection(); // Replace Main.getConnection() with your actual method

			// SQL query to fetch ticket ID by title
			String sql = "SELECT ticket_id FROM Tickets WHERE ticket_title = ?";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, ticketTitle);
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				return resultSet.getInt("ticket_id");
			}
		} catch (SQLException e) {
			e.printStackTrace(); // Handle or log the exception as needed
		} finally {
			// Close resources in the reverse order of their creation to avoid resource
			// leaks
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					e.printStackTrace(); // Handle or log the exception as needed
				}
			}
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace(); // Handle or log the exception as needed
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace(); // Handle or log the exception as needed
				}
			}
		}

		return -1;
	}

	@FXML
	public void backButtonPressedOP(ActionEvent event) {
		commonObjects.backToMainMenu(event, stage, null, null);
	}
}
