package application.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.CommonObjects;
import application.Main; // Import the Main class
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ShowProjectsController {

    private CommonObjects commonObjects = CommonObjects.getInstance();
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private ListView<String> projectListView;

    public void initialize() {
        List<String> projectData = readProjectDataFromDatabase();

        // Display project names and creation dates in a ListView
        projectListView.getItems().addAll(projectData);
    }

    public List<String> readProjectDataFromDatabase() {
        List<String> projectData = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Obtain the connection from the Main class
            connection = Main.getConnection();

            // SQL query to fetch project names and creation dates
            String sql = "SELECT project_name, date FROM Projects";
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String projectName = resultSet.getString("project_name");
                String date = resultSet.getString("date");
                projectData.add(projectName + "     -     " + date);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null)
                    resultSet.close();
                if (preparedStatement != null)
                    preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return projectData;
    }

    @FXML
    public void backFromCreateProjectOp(ActionEvent event) {
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
}
