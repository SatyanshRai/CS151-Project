
package application.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import application.CommonObjects;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class ShowProjectsController {
    
	
	private CommonObjects commonObjects = CommonObjects.getInstance();
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	@FXML
    private ListView<String> projectListView;

    public void initialize() {
        String filePath = "./data/saved-projects.csv"; // Provide the path to your CSV file
        List<String> projectNames = readProjectNames(filePath);

        // Display project names in a ListView
        projectListView.getItems().addAll(projectNames);
    }

    public static List<String> readProjectNames(String filePath) {
        List<String> projectNames = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 1) {
                    String projectName = parts[0].replaceAll("-", "|");
                    projectNames.add(projectName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return projectNames;
    }
    
    @FXML 
	public void backFromCreateProjectOp(ActionEvent event) {
		try {
			root = FXMLLoader.load(getClass().getClassLoader().getResource("view/Main.fxml"));
			stage = (Stage)((Node)event.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
