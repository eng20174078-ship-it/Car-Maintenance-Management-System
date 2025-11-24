package com.mycompany.app;

import com.jfoenix.controls.JFXButton;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MyJavaFXApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        JFXButton button = new JFXButton("Click Me!");
        button.setStyle("-fx-background-color: #11248dff; -fx-text-fill: white;");

        StackPane root = new StackPane();
        root.getChildren().add(button);

        Scene scene = new Scene(root, 500, 550);
        primaryStage.setTitle("JavaFX with JFoenix");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
