package application.controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;

import application.CommonObjects;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
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
	@FXML DatePicker datePick;
	
	@FXML
	public void initialize() {
		datePick.setValue(LocalDate.now());
	}
	
	@FXML public void backFromCreateProjectOp(ActionEvent event) {
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
	
	@FXML public void dateEnteredOp() {
		
	}
	@FXML public void confirmNewProjectOp() {
		
	}

}
