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

public class CreateTicketController{

	private CommonObjects commonObjects = CommonObjects.getInstance();
	@FXML AnchorPane ticketMenuBox;
	@FXML TextField ticketName;
	@FXML TextArea ticketDescription;
	@FXML ChoiceBox<String> projectBox;
	private Stage stage;
	private Scene scene;
	private Parent root;
	private List<List<String>> projects;
	
	@FXML public void initialize() throws IOException {
		String filePath = "./data/saved-projects.csv"; // Provide the path to your CSV file
        projects = commonObjects.readProjectNamesAndSerialNumber(filePath);
        //projects.sort(null);
        for (int i = 0; i < projects.size(); i++) {
        	projectBox.getItems().add(projects.get(i).get(0) + "," + projects.get(i).get(1));
        }
	}
	
	@FXML public void confirmButtonPressedOP(ActionEvent event) {
		try {
			File savedTickets = new File("./data/saved-tickets.csv");
			//boolean newFile = !savedTickets.exists();
			Writer fileWriter = new FileWriter(savedTickets, true);
			
			String projectLabel = projectBox.getValue();
			
			fileWriter.append(projectLabel + "|" +
							ticketName.getText().replaceAll("\\|", "-") + "|" +
							ticketDescription.getText().replaceAll("\\|", "-").replaceAll("\n", "\\\\n") + 
							"\n");
			
			fileWriter.flush();
			fileWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		commonObjects.backToMainMenu(event, stage, scene, root);
	}
	
	@FXML public void backButtonPressedOP(ActionEvent event) {
		commonObjects.backToMainMenu(event, stage, scene, root);
	}
	
	
}
