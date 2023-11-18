package application.controller;

public class ProjectsItems {

    String projectName, date, description;

    public ProjectsItems(String projectName, String date, String description) {
        this.projectName = projectName;
        this.date = date;
        this.description = description;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
