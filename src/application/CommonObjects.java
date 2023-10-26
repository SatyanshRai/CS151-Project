package application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class CommonObjects {

	private static CommonObjects commonObjects = new CommonObjects();
	
	private HBox mainBox;
	
	private CommonObjects()	{}

	public static CommonObjects getInstance() {
		return commonObjects;
	}

	public HBox getMainBox() {
		return mainBox;
	}

	public void setMainBox(HBox mainBox) {
		this.mainBox = mainBox;
	}

	public List<String> readProjectNames(String filePath) {
		List<String> projectNames = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 1) {
                    String projectName = parts[0].replaceAll("-", "|");
                    projectNames.add(projectName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return projectNames;
	}

	public void backToMainMenu(ActionEvent event, Stage stage, Scene scene, Parent root) {
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

}
