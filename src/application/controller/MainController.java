package application.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public class MainController implements Initializable {

	@FXML
	HBox mainBox;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Set minimum and maximum sizes for the mainBox or other controls if necessary
		mainBox.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
		mainBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

		// Add listeners for resize events
		mainBox.widthProperty().addListener((observable, oldValue, newValue) -> {
			// Adjust layout or perform other actions as needed
		});

		mainBox.heightProperty().addListener((observable, oldValue, newValue) -> {
			// Adjust layout or perform other actions as needed
		});
	}

	@FXML
	public void createProjectOp() {

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

	@FXML
	public void showProjectsOp() {
		URL url = getClass().getClassLoader().getResource("view/ShowProjectsTable.fxml");

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

	@FXML
	public void showTicketsOp() {
		URL url = getClass().getClassLoader().getResource("view/ShowTicketsTable.fxml");

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

	@FXML
	public void showCommentsOp() {
		URL url = getClass().getClassLoader().getResource("view/ShowComments.fxml");

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

	@FXML
	public void createTicketOP() {
		URL url = getClass().getClassLoader().getResource("view/CreateTicket.fxml");

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

	@FXML
	public void createCommentOP() {
		URL url = getClass().getClassLoader().getResource("view/CreateComment.fxml");

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
