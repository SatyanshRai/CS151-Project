package application.controller;

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
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;

public class ShowProjectsTableController implements Initializable {

    private CommonObjects commonObjects = CommonObjects.getInstance();
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private AnchorPane projectMenuBox;

    @FXML
    private TextField searchBar;

    @FXML
    private TextField txtProjectName;

    @FXML
    private DatePicker txtDate;

    @FXML
    private TextArea txtDescription;

    @FXML
    private Button SaveChanges;

    @FXML
    private Button DeleteProject;

    @FXML
    private TableView<ProjectsItems> tbProjects;

    @FXML
    private TableColumn<ProjectsItems, String> colProjectName;

    @FXML
    private TableColumn<ProjectsItems, String> colDate;

    @FXML
    private TableColumn<ProjectsItems, String> colDescription;

    ObservableList<ProjectsItems> listI = FXCollections.observableArrayList();
    Connection conn;
    ResultSet rs;
    PreparedStatement pst;
    Integer index;

    @FXML
    void getitem(MouseEvent event) {
        index = tbProjects.getSelectionModel().getSelectedIndex();

        if (index <= -1) {
            return;
        }

        txtProjectName.setText(colProjectName.getCellData(index).toString());
        txtDate.setPromptText(colDate.getCellData(index).toString());
        txtDescription.setText(colDescription.getCellData(index).toString());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            repopulate();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @FXML
    void backFromCreateProjectOp(ActionEvent event) {
        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource("view/Main.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void searchButtonOP(ActionEvent event) {
        String searchText = searchBar.getText().toLowerCase();

        ObservableList<ProjectsItems> filteredList = FXCollections.observableArrayList();

        for (ProjectsItems item : listI) {
            if (item.getProjectName().toLowerCase().contains(searchText) ||
                    item.getDate().toLowerCase().contains(searchText) ||
                    item.getDescription().toLowerCase().contains(searchText)) {
                filteredList.add(item);
            }
        }

        tbProjects.setItems(filteredList);
    }

    @FXML
    private void deleteProject(ActionEvent event) {
        index = tbProjects.getSelectionModel().getSelectedIndex();

        if (index >= 0) {
            try {
                String projectName = colProjectName.getCellData(index).toString();

                String deleteQuery = "DELETE FROM Projects WHERE project_name = ?";
                pst = conn.prepareStatement(deleteQuery);
                pst.setString(1, projectName);
                pst.executeUpdate();

                tbProjects.getItems().remove(index);
                clearFields();
                
                listI.clear();
                repopulate();
                searchButtonOP(event);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void repopulate() {
    	try {
    		conn = Main.getConnection();
            rs = conn.createStatement().executeQuery("SELECT project_name, date, project_desc FROM Projects");

            while (rs.next()) {
                listI.add(new ProjectsItems(rs.getString("project_name"), rs.getString("date"),
                        rs.getString("project_desc")));

            }
            colProjectName.setCellValueFactory(new PropertyValueFactory<>("projectName"));
            colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
            colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

            tbProjects.setItems(listI);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }

    @FXML
    private void saveChanges(ActionEvent event) {
        index = tbProjects.getSelectionModel().getSelectedIndex();

        if (index >= 0) {
            try {
                String projectName = txtProjectName.getText();
                String date;
                if(txtDate.getValue() == null) date = colDate.getCellData(index).toString();
                else date = txtDate.getValue().toString();
                String description = txtDescription.getText();

                String updateQuery = "UPDATE Projects SET project_name=?, date=?, project_desc=? WHERE project_name=?";
                pst = conn.prepareStatement(updateQuery);
                pst.setString(1, projectName);
                pst.setString(2, date);
                pst.setString(3, description);
                pst.setString(4, colProjectName.getCellData(index).toString());
                pst.executeUpdate();

                ProjectsItems updatedItem = new ProjectsItems(projectName, date, description);
                tbProjects.getItems().set(index, updatedItem);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void clearFields() {
        txtProjectName.clear();
        txtDate.getEditor().clear();
        txtDescription.clear();
    }

}
