package application;
	
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Main extends Application {
	//Creation of initial Main class and project done by Satyansh
	@Override
	public void start(Stage primaryStage) {
		//Set the initial scene ("Hello World" for JavaFX)
		try {
			VBox root = new VBox();
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		
			
			//temporary code that allows navigation to the "Create New Project" Scene from the home page via a button
			//feel free to modify the Button and it's location - Asher
			Scene createProjectScene = createProject();
			Button createProjBtn = new Button("Create New Project");
			createProjBtn.setOnAction(event -> {
				primaryStage.setScene(createProjectScene);
			});
			
			root.getChildren().add(createProjBtn);
			// end of temp code
			
		} catch(Exception e) {
			e.printStackTrace();
		}	
	}
	
	//creates Scene for the "Create New Project" page
	private Scene createProject() {
		Label title = new Label("Bug Tracker");
		VBox vbox = new VBox(title);
		Label header = new Label("Create New Project");
		
		Label name = new Label("Project's name *");
		TextField nameField = new TextField();
		nameField.setPromptText("Enter the project's name");
		
		ObservableList<String> monthOpts = FXCollections.observableArrayList();	
		for (int i = 1; i <= 12; i++) {
			monthOpts.add(String.valueOf(i));
		}
		ComboBox monthBox = new ComboBox(monthOpts);
		
		ObservableList<String> dayOpts = FXCollections.observableArrayList();	
		for (int i = 1; i <= 31; i++) {
			dayOpts.add(String.valueOf(i));
		}
		ComboBox dayBox = new ComboBox(dayOpts);
		
		ObservableList<String> yearOpts = FXCollections.observableArrayList();	
		for (int i = 2023; i >= 1950; i--) {
			yearOpts.add(String.valueOf(i));
		}
		ComboBox yearBox = new ComboBox(yearOpts);
		
		Label projDate = new Label("Project's Starting Date");
		HBox dateField = new HBox(monthBox, dayBox, yearBox);
		
		Label desc = new Label("Project's description");
		TextField descField = new TextField();
		nameField.setPromptText("Enter the project's description");
		
		vbox.getChildren().add(name);
		vbox.getChildren().add(nameField);
		vbox.getChildren().add(projDate);
		vbox.getChildren().add(dateField);
		vbox.getChildren().add(desc);
		vbox.getChildren().add(descField);
		
		Scene createProjectScene = new Scene(vbox, 500, 500);
		return createProjectScene;
	}
	
	//Main method
	public static void main(String[] args) {
		launch(args);
	}
}
