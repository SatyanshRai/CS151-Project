
package application.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import application.CommonObjects;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class ShowTicketsController {
    
	
	private CommonObjects commonObjects = CommonObjects.getInstance();
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	@FXML ChoiceBox<String> selectProject;
	
	@FXML
    private ListView<String> ticketListView;
	String projectPath;
	String ticketPath;

    public void initialize() throws IOException {
    	projectPath = "./data/saved-projects.csv";
        ticketPath = "./data/saved-tickets.csv"; // Provide the path to your CSV file
        List<List<String>> projects = commonObjects.readProjectNamesAndSerialNumber(projectPath);

        // Display project names in a ListView
        for (int i = 0; i < projects.size(); i++) {
        	selectProject.getItems().add(projects.get(i).get(0) + "," + projects.get(i).get(1));
        }
        
        selectProject.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
        	try {
				updateDisplay(selectProject.getValue());
			} catch (IOException e) {
				e.printStackTrace();
			}
        });
    }

    public void updateDisplay(String projectId) throws IOException {
    	ticketListView.getItems().clear();
    	List<String> tickets = commonObjects.getTicketNames(ticketPath, projectId);
    	ticketListView.getItems().addAll(tickets);
    }
    
//    public static List<String> getTicketNames(String filePath, String projectId) throws IOException {
//        List<String> ticketNames = new ArrayList<>();
//        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                String[] columns = line.split("\\|");
//                if (columns[0].trim().equals(projectId)) {
//                    ticketNames.add(columns[1].trim());
//                }
//            }
//        }
//        return ticketNames;
//    }
    
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
}
