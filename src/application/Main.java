package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.fxml.FXMLLoader;

public class Main extends Application {

	private static Connection connection;

	public static Connection getConnection() {
		Connection connection = null;
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:data/BugDB.db");
			System.out.println("Database connection established successfully.");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Failed to establish a database connection.");
		}
		return connection;
	}

	@Override
	public void start(Stage primaryStage) {

		try {
			HBox mainBox = (HBox) FXMLLoader.load(getClass().getClassLoader().getResource("view/Main.fxml"));
			Scene scene = new Scene(mainBox);
			scene.getStylesheets().add(getClass().getClassLoader().getResource("css/application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();

			// Keep a reference of mainBox inside CommonObjects.
			CommonObjects commonObjects = CommonObjects.getInstance();
			commonObjects.setMainBox(mainBox);

		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveProject() {

	}

	public static void main(String[] args) {
		launch(args);
	}
}
