package application.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import application.CommonObjects;
import application.Main;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import java.sql.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
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
	private TableView<Ticket> ticketTableView;
	private ObservableList<Ticket> tableData = FXCollections.observableArrayList();
	
	@FXML private TableColumn ticketCol;
	@FXML private TableColumn projectCol;
	
	String projectPath;
	String ticketPath;
	
	List<String> allProjectNames;
	List<Ticket> allTickets;

	@FXML TextField searchBar;

	@FXML AnchorPane projectMenuBox;

	public void initialize() throws IOException {
		//gets all projects from the database and makes Ticket objects from them
		allProjectNames = readProjectNamesFromDatabase();
		generateTicketObjects();
		
		// Populate the projectChoiceBox with project names from the database
		projectChoiceBox.getItems().add("N/A"); //default value
		projectChoiceBox.getItems().addAll(allProjectNames);
		projectChoiceBox.getSelectionModel().selectFirst();
		
		ticketTableView.setItems(tableData);
		ticketCol.setCellValueFactory(new PropertyValueFactory<>("ticketName"));
		projectCol.setCellValueFactory(new PropertyValueFactory<>("projectName"));
		
		// Listen for changes in project selection
		projectChoiceBox.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> {
					tableData.clear();
					if (newValue.equals("N/A")) { //shows all Tickets in the Table if user selects N/A
						displayAllTickets();
					}
					else if (newValue != null) { //shows all Tickets for selected Project
						List<String> ticketData = readTicketsForProjectFromDatabase(newValue);
					
						for(int i = 0; i < ticketData.size(); i++) {
							tableData.add(new Ticket(ticketData.get(i), newValue));
						}
					}
				});
		
		displayAllTickets();	
	}
	
	public void displayAllTickets() {
		tableData.clear();
		tableData.addAll(allTickets);
	}
	
	public void generateTicketObjects() {
		allTickets = new ArrayList<Ticket>();
		for (int i = 0; i < allProjectNames.size(); i++) {
			List<String> ticketList = readTicketsForProjectFromDatabase(allProjectNames.get(i));
			for (int j = 0; j < ticketList.size(); j++) {
				allTickets.add(new Ticket(ticketList.get(j), allProjectNames.get(i)));
			}
		}
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
		tableData.clear();
		List<Ticket> ticketList = new ArrayList<Ticket>();
		List<String> ticketNames = new ArrayList<String>();
		
		String projectName = projectChoiceBox.getSelectionModel().getSelectedItem();
		if (projectName.equals("N/A")) {
			ticketList = searchTickets(searchBar.getText(), allTickets);
		} else {
			ticketNames = commonObjects.searchList(searchBar.getText(), readTicketsForProjectFromDatabase(projectName));
		}
		
		for (int i = 0; i < ticketNames.size(); i++) {
			tableData.add(new Ticket(ticketNames.get(i), projectName));
		}
		
		for (int i = 0; i < ticketList.size(); i++) {
			tableData.add(ticketList.get(i));
		}
	}
	
	public List<Ticket> searchTickets(String keyword, List<Ticket> tickets) {
        List<Ticket> matchingTickets = new ArrayList<>();

        for (Ticket ticket : tickets) {
            if (ticket.getTicketName().toLowerCase().contains(keyword.toLowerCase()) ||
                ticket.getProjectName().toLowerCase().contains(keyword.toLowerCase())) {
                matchingTickets.add(ticket);
            }
        }

        return matchingTickets;
    }
	
	public static class Ticket {
		private final SimpleStringProperty ticketName;
		private final SimpleStringProperty projectName;
		
		private Ticket(String ticketName, String projectName) {
			this.ticketName = new SimpleStringProperty(ticketName);
			this.projectName = new SimpleStringProperty(projectName);
		}
		
		public String getTicketName() {
			return ticketName.get();
		}
		
		public String getProjectName() {
			return projectName.get();
		}
	}
}
