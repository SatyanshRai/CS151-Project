package application.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import application.CommonObjects;
import application.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import java.sql.*;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class ShowTicketsController {

	private CommonObjects commonObjects = CommonObjects.getInstance();

	private Stage stage;
	private Scene scene;
	private Parent root;
	@FXML
	private ChoiceBox<String> projectChoiceBox;

	@FXML
	private ListView<String> ticketListView;
	String projectPath;
	String ticketPath;

	@FXML TextField searchBar;

	@FXML AnchorPane projectMenuBox;

	public void initialize() throws IOException {
		// Populate the projectChoiceBox with project names from the database
		List<String> projectNames = readProjectNamesFromDatabase();
		projectChoiceBox.getItems().addAll(projectNames);

		// Listen for changes in project selection
		projectChoiceBox.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> {
					if (newValue != null) {
						List<String> ticketData = readTicketsForProjectFromDatabase(newValue);
						// Display ticket titles in the ListView
						ticketListView.getItems().setAll(ticketData);
					}
				});
	}

	public void updateDisplay(String projectId) throws IOException {
		ticketListView.getItems().clear();
		List<String> tickets = commonObjects.getTicketNames(ticketPath, projectId);
		ticketListView.getItems().addAll(tickets);
	}

	public List<String> readProjectNamesFromDatabase() {
		List<String> projectNames = new ArrayList<>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			// Obtain the connection from the Main class
			connection = Main.getConnection();

			// SQL query to fetch project names
			String sql = "SELECT project_name FROM Projects";
			preparedStatement = connection.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				String projectName = resultSet.getString("project_name");
				projectNames.add(projectName);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// Close resources
		}

		return projectNames;
	}

	public List<String> readTicketsForProjectFromDatabase(String selectedProject) {
		List<String> ticketData = new ArrayList<>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			// Obtain the connection from the Main class
			connection = Main.getConnection();

			// SQL query to fetch ticket titles for the selected project
			String sql = "SELECT ticket_title FROM Tickets WHERE project_id = (SELECT project_id FROM Projects WHERE project_name = ?)";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, selectedProject);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				String ticketTitle = resultSet.getString("ticket_title");
				ticketData.add(ticketTitle);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// Close resources
		}

		return ticketData;
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML public void searchButtonOP() {
		ticketListView.getItems().clear();
		ticketListView.getItems().addAll(commonObjects.searchList(searchBar.getText(), readTicketsForProjectFromDatabase(projectChoiceBox.getSelectionModel().getSelectedItem())));
	}
}
