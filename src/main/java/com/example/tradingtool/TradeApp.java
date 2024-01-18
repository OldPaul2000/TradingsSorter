package com.example.tradingtool;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class TradeApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TradeApp.class.getResource("tradeToolGui.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 630, 546);
        Path path = FileSystems.getDefault().getPath("");
        Image image = new Image(path.toAbsolutePath() + File.separator + "LogoIcon.png");
        stage.setResizable(false);
        stage.getIcons().add(image);
        stage.setTitle("Trades Sorter");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}