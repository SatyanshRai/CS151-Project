package application.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import application.CommonObjects;
import application.Main;
import javafx.beans.property.SimpleIntegerProperty;
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
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import java.sql.*;
import java.time.LocalDate;

import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class ShowCommentsController {

	private CommonObjects commonObjects = CommonObjects.getInstance();

	private Stage stage;
	private Scene scene;
	private Parent root;
	PreparedStatement pst;
    ResultSet rs;
    Connection conn;

	
	@FXML private ChoiceBox<String> projectChoiceBox;
	@FXML private ChoiceBox<String> ticketChoiceBox;

	@FXML
	private TableView<Comment> commentTableView;
	private ObservableList<Comment> tableData = FXCollections.observableArrayList();
	
	@FXML private TableColumn<Comment, String> ticketCol;
	@FXML private TableColumn<Comment, String> projectCol;
	@FXML private TableColumn<Comment, String> commentCol;
	@FXML private TableColumn<Comment, String> dateCol;

	
	String projectPath;
	String ticketPath;
	
    Integer index;
	
	List<String> allProjectNames;
	List<String> allTicketNames;
	List<Comment> allComments;
	
	@FXML TextArea commentDescDisplay;

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
		
		commentTableView.setItems(tableData);
		ticketCol.setCellValueFactory(new PropertyValueFactory<>("ticketName"));
		projectCol.setCellValueFactory(new PropertyValueFactory<>("projectName"));
		commentCol.setCellValueFactory(new PropertyValueFactory<>("commentDescription"));
		dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

		
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
	
	@FXML
    void getItem(MouseEvent event) {
        index = commentTableView.getSelectionModel().getSelectedIndex();

        if (index <= -1) {
            return;
        }

        commentDescDisplay.setText(commentCol.getCellData(index).toString());
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
				List<String> commentDates = readCommentsDateForTicketFromDatabase(allTicketNames.get(j));
				List<Integer> commentIDs = readCommentsIDForTicketFromDatabase(allTicketNames.get(j));
				for (int k = 0; k < commentTexts.size(); k++) {
					allComments.add(new Comment(commentTexts.get(k), allTicketNames.get(j), allProjectNames.get(i), commentDates.get(k), commentIDs.get(k)));
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
	
	public List<String> readCommentsDateForTicketFromDatabase(String selectedTicket) {
		List<String> commentDates = new ArrayList<String>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			//Obtain the connection from the Main class
			connection = Main.getConnection();
			
			//SQL query to fetch comment titles for the selected ticket
			String sql = "SELECT comment_date FROM Comments WHERE ticket_id = (SELECT ticket_id FROM Tickets WHERE ticket_title = ?)";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, selectedTicket);
			resultSet = preparedStatement.executeQuery();
			
			while (resultSet.next()) {
				String commentDate = resultSet.getString("comment_date");
				commentDates.add(commentDate);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return commentDates;
	}
	
	public List<Integer> readCommentsIDForTicketFromDatabase(String selectedTicket) {
		List<Integer> commentIDs = new ArrayList<Integer>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			//Obtain the connection from the Main class
			connection = Main.getConnection();
			
			//SQL query to fetch comment titles for the selected ticket
			String sql = "SELECT comment_id FROM Comments WHERE ticket_id = (SELECT ticket_id FROM Tickets WHERE ticket_title = ?)";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, selectedTicket);
			resultSet = preparedStatement.executeQuery();
			
			while (resultSet.next()) {
				int commentID = resultSet.getInt("comment_id");
				commentIDs.add(commentID);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return commentIDs;
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
		private final SimpleStringProperty cDate;
		private final SimpleIntegerProperty cID;
		
		private Comment(String commentDescription, String ticketName, String projectName, String cDate, int cID) {
			this.commentDescription = new SimpleStringProperty(commentDescription);
			this.ticketName = new SimpleStringProperty(ticketName);
			this.projectName = new SimpleStringProperty(projectName);
			this.cDate = new SimpleStringProperty(cDate);
			this.cID = new SimpleIntegerProperty(cID);
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
		
		public String getDate() {
			return cDate.get();
		}
		public int getID() {
			return cID.get();
		}
	}

	@FXML public void saveChangesOP() {
		index = commentTableView.getSelectionModel().getSelectedIndex();
		conn = Main.getConnection();

        if (index >= 0) {
            try {
                String commentDescription = commentDescDisplay.getText();
                String date;
                date = LocalDate.now().toString();
    			String updateQuery = "UPDATE Comments SET comment_text=?, comment_date=? WHERE comment_id=?";
    			String currTicket = ticketCol.getCellData(index).toString();
                String currProject = projectCol.getCellData(index).toString();
                int currID = tableData.get(index).getID();
                pst = conn.prepareStatement(updateQuery);
                pst.setString(1, commentDescription);
                pst.setString(2, date);
                pst.setInt(3, currID);
                pst.executeUpdate();

                Comment updatedItem = new Comment(commentDescription, currTicket, currProject, date, currID);
                commentTableView.getItems().set(index, updatedItem);
                tableData.clear();
                repopulate();
                searchButtonOP();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
	}

	@FXML public void deleteCommentOP() {
		index = commentTableView.getSelectionModel().getSelectedIndex();
		conn = Main.getConnection();
		
        if (index >= 0) {
            try {
                String commentText = commentCol.getCellData(index).toString();

                String deleteQuery = "DELETE FROM Comments WHERE comment_text = ?";
                pst = conn.prepareStatement(deleteQuery);
                pst.setString(1, commentText);
                pst.executeUpdate();

                commentTableView.getItems().remove(index);
                commentDescDisplay.clear();
                
                tableData.clear();
                repopulate();
                searchButtonOP();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
	}
	private void repopulate() {
		allProjectNames = readProjectNamesFromDatabase();
		generateCommentObjects();
		
		commentTableView.setItems(tableData);
		ticketCol.setCellValueFactory(new PropertyValueFactory<>("ticketName"));
		projectCol.setCellValueFactory(new PropertyValueFactory<>("projectName"));
		commentCol.setCellValueFactory(new PropertyValueFactory<>("commentDescription"));
		dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
    }
}
