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

public class ShowCommentsController {

	private CommonObjects commonObjects = CommonObjects.getInstance();

	private Stage stage;
	private Scene scene;
	private Parent root;
	
	@FXML private ChoiceBox<String> projectChoiceBox;
	@FXML private ChoiceBox<String> ticketChoiceBox;

	@FXML
	private TableView<Comment> ticketTableView;
	private ObservableList<Comment> tableData = FXCollections.observableArrayList();
	
	@FXML private TableColumn ticketCol;
	@FXML private TableColumn projectCol;
	@FXML private TableColumn commentCol;
	
	String projectPath;
	String ticketPath;
	
	List<String> allProjectNames;
	List<String> allTicketNames;
	List<Comment> allComments;

	@FXML TextField searchBar;

	@FXML AnchorPane projectMenuBox;

	public void initialize() throws IOException {
		//gets all projects from the database and makes Ticket objects from them
		allProjectNames = readProjectNamesFromDatabase();
		generateCommentObjects();
		
		// Populate the projectChoiceBox with project names from the database
		projectChoiceBox.getItems().add("N/A"); //default value
		projectChoiceBox.getItems().addAll(allProjectNames);
		projectChoiceBox.getSelectionModel().selectFirst();
		ticketChoiceBox.getItems().add("N/A");
		ticketChoiceBox.getSelectionModel().selectFirst();
		
		ticketTableView.setItems(tableData);
		ticketCol.setCellValueFactory(new PropertyValueFactory<>("ticketName"));
		projectCol.setCellValueFactory(new PropertyValueFactory<>("projectName"));
		commentCol.setCellValueFactory(new PropertyValueFactory<>("commentDescription"));
		
		// Listen for changes in project selection
		projectChoiceBox.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> {
					tableData.clear();
					if (newValue.equals("N/A")) { //shows all Comments in the Table if user selects N/A
						ticketChoiceBox.getSelectionModel().selectFirst();
						displayAllComments();
					} else if (newValue != null) { //shows all Comments for selected Project
						tableData.addAll(getCommentsUnderProject(newValue));
						ticketChoiceBox.getSelectionModel().selectFirst();
						for (int i = ticketChoiceBox.getItems().size() - 1; i > 0; i--) {
							ticketChoiceBox.getItems().remove(i);
						}
						ticketChoiceBox.getItems().addAll(readTicketsForProjectFromDatabase(newValue));
					}
				});
		
		ticketChoiceBox.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldValue, newValue) -> {
					String selectedProject = projectChoiceBox.getSelectionModel().getSelectedItem();
					tableData.clear();
					if (newValue.equals("N/A")) {
						tableData.addAll(getCommentsUnderProject(selectedProject));
					} else if (newValue != null) {
						tableData.addAll(getCommentsUnderTicketAndProject(newValue, selectedProject));
					}
				});
		
		displayAllComments();	
	}
	
	public void displayAllComments() {
		tableData.clear();
		tableData.addAll(allComments);
	}
	
	public List<Comment> getCommentsUnderProject(String selectedProject) {
		List<Comment> comments = new ArrayList<Comment>();
		for (Comment comment : allComments) {
			if (comment.getProjectName().equals(selectedProject)) {
				comments.add(comment);
			}
		}
		return comments;
	}
	
	public List<Comment> getCommentsUnderTicketAndProject(String selectedTicket, String selectedProject) {
		List<Comment> comments = new ArrayList<Comment>();
		for (Comment comment : allComments) {
			if (comment.getProjectName().equals(selectedProject) && comment.getTicketName().equals(selectedTicket)) {
				comments.add(comment);
			}
		}
		return comments;
	}
	
	public void generateCommentObjects() {
		allComments = new ArrayList<Comment>();
		for (int i = 0; i < allProjectNames.size(); i++) {
			allTicketNames = readTicketsForProjectFromDatabase(allProjectNames.get(i));
			for (int j = 0; j < allTicketNames.size(); j++) {
				List<String> commentTexts = readCommentsForTicketFromDatabase(allTicketNames.get(j));
				for (int k = 0; k < commentTexts.size(); k++) {
					allComments.add(new Comment(commentTexts.get(k), allTicketNames.get(j), allProjectNames.get(i)));
				}
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
	
	public List<String> readCommentsForTicketFromDatabase(String selectedTicket) {
		List<String> commentData = new ArrayList<String>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			//Obtain the connection from the Main class
			connection = Main.getConnection();
			
			//SQL query to fetch comment titles for the selected ticket
			String sql = "SELECT comment_text FROM Comments WHERE ticket_id = (SELECT ticket_id FROM Tickets WHERE ticket_title = ?)";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, selectedTicket);
			resultSet = preparedStatement.executeQuery();
			
			while (resultSet.next()) {
				String commentText = resultSet.getString("comment_text");
				commentData.add(commentText);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return commentData;
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
		List<Comment> commentList = new ArrayList<Comment>();
		
		String projectName = projectChoiceBox.getSelectionModel().getSelectedItem();
		String ticketName = ticketChoiceBox.getSelectionModel().getSelectedItem();
		if (projectName.equals("N/A") && ticketName.equals("N/A")) {
			commentList = searchComments(searchBar.getText(), allComments);
		} else if (!projectName.equals("N/A") && ticketName.equals("N/A")) {
			commentList = searchComments(searchBar.getText(), getCommentsUnderProject(projectName));
		} else if (!projectName.equals("N/A") && !ticketName.equals("N/A")) {
			commentList = searchComments(searchBar.getText(), getCommentsUnderTicketAndProject(ticketName, projectName));
		}
		
		tableData.addAll(commentList);
	}
	
	public List<Comment> searchComments(String keyword, List<Comment> comments) {
        List<Comment> matchingComments = new ArrayList<>();

        for (Comment comment : comments) {
            if (comment.getTicketName().toLowerCase().contains(keyword.toLowerCase()) ||
                comment.getProjectName().toLowerCase().contains(keyword.toLowerCase()) ||
                comment.getCommentDescription().toLowerCase().contains(keyword.toLowerCase())) {
                matchingComments.add(comment);
            }
        }

        return matchingComments;
    }
	
	public static class Comment {
		private final SimpleStringProperty commentDescription;
		private final SimpleStringProperty ticketName;
		private final SimpleStringProperty projectName;
		
		private Comment(String commentDescription, String ticketName, String projectName) {
			this.commentDescription = new SimpleStringProperty(commentDescription);
			this.ticketName = new SimpleStringProperty(ticketName);
			this.projectName = new SimpleStringProperty(projectName);
		}
		
		public String getCommentDescription() {
			return commentDescription.get();
		}
		
		public String getTicketName() {
			return ticketName.get();
		}
		
		public String getProjectName() {
			return projectName.get();
		}
	}
}
