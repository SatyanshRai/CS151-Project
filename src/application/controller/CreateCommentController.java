package application.controller;

import java.io.File;
import java.io.FileWriter;
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
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	@FXML public void initialize() {
		String filePath = "./data/saved-projects.csv"; // Provide the path to your CSV file
        List<String> projectNames = commonObjects.readProjectNames(filePath);
        projectNames.sort(null);
        projectBox.getItems().addAll(projectNames);
	}
	
	@FXML public void confirmButtonPressedOP(ActionEvent event) {
		commonObjects.backToMainMenu(event, stage, scene, root);
	}
	
	@FXML public void backButtonPressedOP(ActionEvent event) {
		commonObjects.backToMainMenu(event, stage, scene, root);
	}
	
	
}
