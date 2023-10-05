package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	//Creation of initial Main class and project done by Satyansh
	@Override
	public void start(Stage primaryStage) {
		//Set the initial scene ("Hello World" for JavaFX)
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//Main method
	public static void main(String[] args) {
		launch(args);
	}
}
