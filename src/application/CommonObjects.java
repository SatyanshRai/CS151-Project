package application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    private CommonObjects() {
    }

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

    public static List<String> getTicketNames(String filePath, String projectId) throws IOException {
        List<String> ticketNames = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] columns = line.split("\\|");
                if (columns[0].trim().equals(projectId)) {
                    ticketNames.add(columns[1].trim());
                }
            }
        }
        return ticketNames;
    }

    public static List<List<String>> readProjectNamesAndSerialNumber(String filePath) throws IOException {
        List<List<String>> resultList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    // Skip the header
                    isFirstLine = false;
                    continue;
                }

                String[] columns = line.split("\\|");
                String name = columns[0].trim();
                String serialNumber = columns[columns.length - 1].trim();

                List<String> entry = new ArrayList<>();
                entry.add(name);
                entry.add(serialNumber);
                resultList.add(entry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }

    public List<List<Object>> readProjectsFromDatabase() {
        List<List<Object>> projects = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = Main.getConnection(); // Access the database connection from your Main class

            // SQL query to fetch project information
            String sql = "SELECT project_name, date, project_id FROM Projects";
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String projectName = resultSet.getString("project_name");
                String date = resultSet.getString("date");
                int projectId = resultSet.getInt("project_id");
                List<Object> projectInfo = new ArrayList<>();
                projectInfo.add(projectName);
                projectInfo.add(date);
                projectInfo.add(projectId);
                projects.add(projectInfo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null)
                    resultSet.close();
                if (preparedStatement != null)
                    preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return projects;
    }

    public List<String> readTicketNames(String filePath) {
        List<String> ticketNames = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 1) {
                    String ticketName = parts[0].replaceAll("-", "|");
                    ticketNames.add(ticketName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ticketNames;
    }

    public void backToMainMenu(ActionEvent event, Stage stage, Scene scene, Parent root) {
        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource("view/Main.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public List<String> searchList(String searchWords, List<String> listOfStrings) {
		List<String> searchWordsArray = Arrays.asList(searchWords.trim().split(" "));
		
		return listOfStrings.stream().filter(input -> {
			return searchWordsArray.stream().allMatch(word ->
					input.toLowerCase().contains(word.toLowerCase()));
		}).collect(Collectors.toList());
	}

}
