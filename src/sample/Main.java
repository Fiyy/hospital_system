package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;
import com.mysql.cj.jdbc.Driver;

public class Main extends Application {
    public static Stage stage = new Stage();
    public static final String username = "root";
    public static final String password = "Zhangyun0130";
    public static final String URL = "jdbc:mysql://localhost:3306/hospital_system";
    public static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    public static String name = null;
    private static Pane loginRoot;
    private static Pane doctorRoot;
    private static Pane patientRoot;
    private static Scene loginScene;
    private static Scene doctorScene;
    private static Scene patientScene;

    @Override
    public void start(Stage primaryStage) throws Exception{
        stage = primaryStage;

        loginRoot = FXMLLoader.load(getClass().getResource("Login.fxml"));
        loginScene = new Scene(loginRoot, loginRoot.getPrefWidth(), loginRoot.getPrefHeight());
        loginScene.getStylesheets().add(getClass().getResource("Login.css").toExternalForm());

        doctorRoot = FXMLLoader.load(getClass().getResource("Doctor.fxml"));
        doctorScene = new Scene(doctorRoot, doctorRoot.getPrefWidth(), doctorRoot.getPrefHeight());

        patientRoot = FXMLLoader.load(getClass().getResource("patient.fxml"));
        patientScene = new Scene(patientRoot, patientRoot.getPrefWidth(), patientRoot.getPrefHeight());

        stage.setTitle("医院挂号系统");
        setLogin();
        stage.show();
    }
    public static void setLogin()
    {
        stage.setScene(loginScene);
    }
    public static void setPatient()
    {
        stage.setScene(patientScene);
    }
    public static void setDoctor()
    {
        stage.setScene(doctorScene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
