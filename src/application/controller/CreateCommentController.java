package application.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import application.CommonObjects;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;

public class CreateCommentController{

	private CommonObjects commonObjects = CommonObjects.getInstance();
	@FXML AnchorPane ticketMenuBox;
	@FXML TextField ticketName;
	@FXML TextArea ticketDescription;
	@FXML ChoiceBox<String> projectBox;
	@FXML ChoiceBox<String> ticketBox;
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	@FXML public void initialize() throws IOException {
		String projectPath = "./data/saved-projects.csv"; // Provide the path to your CSV file
		String ticketPath = "./data/saved-tickets.csv";
        List<List<String>> projects = commonObjects.readProjectNamesAndSerialNumber(projectPath);
        
        // Display project names in a ListView
        for (int i = 0; i < projects.size(); i++) {
        	projectBox.getItems().add(projects.get(i).get(0) + "," + projects.get(i).get(1));
        }
        
        projectBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
        	try {
				updateTicketBox(ticketPath, projectBox.getValue());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        });
	}
	
	private void updateTicketBox(String filePath, String projectId) throws IOException {
		ticketBox.getItems().clear();
		List<String> tickets = commonObjects.getTicketNames(filePath, projectId);
		ticketBox.getItems().addAll(tickets);
	}
	
	
	@FXML public void confirmButtonPressedOP(ActionEvent event) {
		commonObjects.backToMainMenu(event, stage, scene, root);
	}
	
	@FXML public void backButtonPressedOP(ActionEvent event) {
		commonObjects.backToMainMenu(event, stage, scene, root);
	}
	
	
}
