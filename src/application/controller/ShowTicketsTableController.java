package application.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import application.CommonObjects;
import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ShowTicketsTableController implements Initializable {

    private CommonObjects commonObjects = CommonObjects.getInstance();

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private ChoiceBox<String> projectChoiceBox;

    @FXML
    private AnchorPane projectMenuBox;

    @FXML
    private TextField searchBar;

    @FXML
    private TextField txtTicketName;

    @FXML
    private TextArea txtDescription;

    @FXML
    private Button SaveChanges;

    @FXML
    private Button DeleteTicket;

    @FXML
    private TextField txtProjectName;

    List<String> allProjectNames;
    List<Ticket> allTickets;

    @FXML
    private TableView<Ticket> ticketTableView;
    private ObservableList<Ticket> tableData = FXCollections.observableArrayList();

    @FXML
    private TableColumn<Ticket, String> ticketCol;

    @FXML
    private TableColumn<Ticket, String> projectCol;

    @FXML
    private TableColumn<Ticket, String> descCol;

    ObservableList<Ticket> listI = FXCollections.observableArrayList();
    Connection conn;
    ResultSet rs;
    PreparedStatement pst;
    Integer index;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        allProjectNames = readProjectNamesFromDatabase();
        generateTicketObjects();

        // Populate the projectChoiceBox with project names from the database
        projectChoiceBox.getItems().add("N/A"); // default value
        projectChoiceBox.getItems().addAll(allProjectNames);
        projectChoiceBox.getSelectionModel().selectFirst();

        ticketTableView.setItems(tableData);
        ticketCol.setCellValueFactory(new PropertyValueFactory<>("ticketName"));
        projectCol.setCellValueFactory(new PropertyValueFactory<>("projectName"));
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        ticketTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Set the selected ticket details to the fields
                txtTicketName.setText(newSelection.getTicketName());
                txtProjectName.setText(newSelection.getProjectName());
                txtDescription.setText(newSelection.getDescription());
            }
        });
        txtProjectName.setEditable(false);

        projectChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    tableData.clear();
                    if (newValue.equals("N/A")) { // shows all Tickets in the Table if the user selects N/A
                        displayAllTickets();
                    } else if (newValue != null) { // shows all Tickets for the selected Project
                        List<Ticket> ticketData = readTicketsForProjectFromDatabase(newValue);

                        for (int i = 0; i < ticketData.size(); i++) {
                            tableData.add(ticketData.get(i));
                        }

                        // Clear the fields when a project is selected
                        txtTicketName.clear();
                        txtProjectName.clear();
                        txtDescription.clear();
                    }
                });

        // Listen for changes in project selection
        projectChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    tableData.clear();
                    if (newValue.equals("N/A")) { // shows all Tickets in the Table if user selects N/A
                        displayAllTickets();
                    } else if (newValue != null) { // shows all Tickets for selected Project
                        List<Ticket> ticketData = readTicketsForProjectFromDatabase(newValue);

                        for (int i = 0; i < ticketData.size(); i++) {
                            tableData.add(ticketData.get(i));
                        }
                    }
                });

        displayAllTickets();
    }

    public void displayAllTickets() {
        tableData.clear();
        generateTicketObjects(); // Refresh the ticket objects from the database
        tableData.addAll(allTickets);
    }

    public void generateTicketObjects() {
        allTickets = new ArrayList<>();
        for (int i = 0; i < allProjectNames.size(); i++) {
            List<Ticket> ticketList = readTicketsForProjectFromDatabase(allProjectNames.get(i));
            allTickets.addAll(ticketList); // Add all Ticket objects to allTickets list
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
    void deleteProject(ActionEvent event) {
        // Get the selected ticket from the table
        Ticket selectedTicket = ticketTableView.getSelectionModel().getSelectedItem();

        if (selectedTicket != null) {
            // Delete the selected ticket from the database
            if (deleteTicketFromDatabase(selectedTicket)) {
                // Remove the selected ticket from the table
                tableData.remove(selectedTicket);

                // Show a message or perform any additional actions if needed
                System.out.println("Ticket deleted successfully.");
            } else {
                // Show an error message or perform any error handling if needed
                System.err.println("Failed to delete ticket.");
            }
        } else {
            // Show a message indicating that no ticket is selected
            System.out.println("No ticket selected for deletion.");
        }
    }

    private boolean deleteTicketFromDatabase(Ticket ticket) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // Obtain the connection from the Main class
            connection = Main.getConnection();

            // SQL query to delete the ticket
            String sql = "DELETE FROM Tickets WHERE ticket_title = ? AND project_id = (SELECT project_id FROM Projects WHERE project_name = ?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, ticket.getTicketName());
            preparedStatement.setString(2, ticket.getProjectName());

            // Execute the update
            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            // Close resources
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void saveChanges(ActionEvent event) {

        Ticket selectedTicket = ticketTableView.getSelectionModel().getSelectedItem();

        if (selectedTicket != null) {
            // Get the new values from the text fields
            String newTicketName = txtTicketName.getText();
            String newDescription = txtDescription.getText();

            // Update the ticket in the database
            updateTicket(selectedTicket.getTicketName(), selectedTicket.getProjectName(), newTicketName,
                    newDescription);

            // Refresh the table to reflect the changes
            displayAllTickets();
        }
    }

    private void updateTicket(String oldTicketName, String projectName, String newTicketName, String newDescription) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = Main.getConnection();

            // SQL query to update the ticket
            String sql = "UPDATE Tickets SET ticket_title = ?, ticket_desc = ? WHERE project_id = (SELECT project_id FROM Projects WHERE project_name = ?) AND ticket_title = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, newTicketName);
            preparedStatement.setString(2, newDescription);
            preparedStatement.setString(3, projectName);
            preparedStatement.setString(4, oldTicketName);

            // Execute the update
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void searchButtonOP() {
        tableData.clear();
        List<Ticket> ticketList = new ArrayList<>();
        String projectName = projectChoiceBox.getSelectionModel().getSelectedItem();

        if (projectName.equals("N/A")) {
            ticketList = searchTickets(searchBar.getText(), allTickets);
        } else {
            ticketList = searchTickets(searchBar.getText(), readTicketsForProjectFromDatabase(projectName));
        }

        tableData.addAll(ticketList);
    }

    public List<Ticket> readTicketsForProjectFromDatabase(String selectedProject) {
        List<Ticket> ticketData = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Obtain the connection from the Main class
            connection = Main.getConnection();

            // SQL query to fetch ticket data for the selected project
            String sql = "SELECT ticket_title, project_name, project_desc FROM Tickets t JOIN Projects p ON t.project_id = p.project_id WHERE p.project_name = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, selectedProject);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String ticketTitle = resultSet.getString("ticket_title");
                String projectDesc = resultSet.getString("project_desc");
                ticketData.add(new Ticket(ticketTitle, selectedProject, projectDesc));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources
        }

        return ticketData;
    }

    public List<Ticket> searchTickets(String keyword, List<Ticket> tickets) {
        List<Ticket> matchingTickets = new ArrayList<>();

        for (Ticket ticket : tickets) {
            if (ticket.getTicketName().toLowerCase().contains(keyword.toLowerCase())
                    || ticket.getProjectName().toLowerCase().contains(keyword.toLowerCase())
                    || ticket.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                matchingTickets.add(ticket);
            }
        }

        return matchingTickets;
    }

    public static class Ticket {
        private final String ticketName;
        private final String projectName;
        private final String description;

        public Ticket(String ticketName, String projectName, String description) {
            this.ticketName = ticketName;
            this.projectName = projectName;
            this.description = description;
        }

        public String getTicketName() {
            return ticketName;
        }

        public String getProjectName() {
            return projectName;
        }

        public String getDescription() {
            return description;
        }
    }
}
