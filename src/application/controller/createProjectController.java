package application.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import application.CommonObjects;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.Node;


public class createProjectController{

	private CommonObjects commonObjects = CommonObjects.getInstance();
	@FXML AnchorPane projectMenuBox;
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	@FXML private TextField projectName;
	@FXML private DatePicker datePick;
	@FXML private TextArea projectDesc;
	@FXML private Button confirmButton;
	
	@FXML
	public void initialize() {
		datePick.setValue(LocalDate.now());
		confirmButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				confirmNewProjectOp();
			}
		});
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
	
	@FXML 
	public void dateEnteredOp() {
		
	}
	
	@FXML 
	public void confirmNewProjectOp() {
		try {
			File savedProjects = new File("./data/saved-projects.csv");
			boolean newFile = !savedProjects.exists();
			Writer fileWriter = new FileWriter(savedProjects, true);
			
			int serialNum = 1;
			if (newFile) {
				System.out.println("test");
				fileWriter.append("Name|Date| Description\n");
			} else {
				serialNum += commonObjects.readProjectNames("./data/saved-projects.csv").size() - 1;
			}
			
			fileWriter.append(projectName.getText().replaceAll("\\|", "-") + "|" + 
							datePick.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "|" + 
							projectDesc.getText().replaceAll("\\|", "-").replaceAll("\n", "\\\\n") + "|" +
							serialNum +
							"\n");
			fileWriter.flush();
			fileWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
