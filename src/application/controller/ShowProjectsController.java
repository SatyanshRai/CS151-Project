
// package application.controller;

// import java.io.BufferedReader;
// import java.io.FileReader;
// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.List;

// import application.CommonObjects;
// import javafx.event.ActionEvent;
// import javafx.fxml.FXML;
// import javafx.fxml.FXMLLoader;
// import javafx.scene.Node;
// import javafx.scene.Parent;
// import javafx.scene.Scene;
// import javafx.scene.control.ListView;
// import javafx.stage.Stage;

// public class ShowProjectsController {

// 	private CommonObjects commonObjects = CommonObjects.getInstance();

// 	private Stage stage;
// 	private Scene scene;
// 	private Parent root;

// 	@FXML
//     private ListView<String> projectListView;

//     public void initialize() {
//         String filePath = "./data/saved-projects.csv"; // Provide the path to your CSV file
//         List<String> projectNames = readProjectNames(filePath);

//         // Display project names in a ListView
//         projectListView.getItems().addAll(projectNames);
//     }

//     public static List<String> readProjectNames(String filePath) {
//         List<String> projectNames = new ArrayList<>();

//         try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
//             String line;

//             while ((line = reader.readLine()) != null) {
//                 String[] parts = line.split("\\|");
//                 if (parts.length >= 1) {
//                     String projectName = parts[0].replaceAll("-", "|");
//                     projectNames.add(projectName);
//                 }
//             }
//         } catch (IOException e) {
//             e.printStackTrace();
//         }

//         return projectNames;
//     }

//     @FXML 
// 	public void backFromCreateProjectOp(ActionEvent event) {
// 		try {
// 			root = FXMLLoader.load(getClass().getClassLoader().getResource("view/Main.fxml"));
// 			stage = (Stage)((Node)event.getSource()).getScene().getWindow();
// 			scene = new Scene(root);
// 			stage.setScene(scene);
// 			stage.show();
// 		} catch (IOException e) {
// 			// TODO Auto-generated catch block
// 			e.printStackTrace();
// 		}
// 	}
// }

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
                projectData.add(projectName + " - " + date);
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
