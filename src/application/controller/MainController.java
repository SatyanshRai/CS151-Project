package application.controller;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

public class MainController {

	@FXML HBox mainBox;


	@FXML public void createProjectOp() {
		
		URL url = getClass().getClassLoader().getResource("view/CreateProject.fxml");
		
		try {
			AnchorPane pane1 = (AnchorPane) FXMLLoader.load(url);
			
			if (mainBox.getChildren().size() > 0)
				mainBox.getChildren().remove(0);
				
			mainBox.getChildren().add(pane1);
		} 
		
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
