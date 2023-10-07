package application.controller;

import java.io.IOException;
import java.net.URL;

import application.CommonObjects;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class createProjectController {

	private CommonObjects commonObjects = CommonObjects.getInstance();
	
	@FXML public void backFromCreateProjectOp() {
		
		URL url = getClass().getClassLoader().getResource("view/Main.fxml");
		
		try {
//			AnchorPane pane2 = (AnchorPane) FXMLLoader.load(url);
//			
//			HBox mainBox = commonObjects.getMainBox();
//			
//			//if (mainBox.getChildren().size() > 0)
//			//	mainBox.getChildren().remove(0);
//				
//			mainBox.getChildren().add(pane2);
			
			HBox mainBox = (HBox)FXMLLoader.load(getClass().getClassLoader().getResource("view/Main.fxml"));
			Scene scene = new Scene(mainBox);
			scene.getStylesheets().add(getClass().getClassLoader().getResource("css/application.css").toExternalForm());
//			primaryStage.setScene(scene);
//			primaryStage.show();
			
			//Keep a reference of mainBox inside CommonObjects.
			CommonObjects commonObjects = CommonObjects.getInstance();
			commonObjects.setMainBox(mainBox);
		} 
		
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
