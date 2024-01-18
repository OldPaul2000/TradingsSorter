package com.example.tradingtool;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MainController {

    @FXML
    public Button sortTradesButton;

    @FXML
    public TextField losesBefore15;

    @FXML
    public TextField winsBefore15;

    @FXML
    public TextField losesAndWinsBefore15;

    @FXML
    public TextField losesAfter15;

    @FXML
    public TextField winsAfter15;

    @FXML
    public TextField losesAndWinsAfter15;

    @FXML
    public Label fileIsEmptyWarning;

    @FXML
    public ComboBox<String> inputTradesComboBox;

    @FXML
    public ComboBox<String> before15ComboBox;

    @FXML
    public ComboBox<String> after15ComboBox;

    @FXML
    public AnchorPane background;

    InputFileClass inputFile = new InputFileClass();
    OutputFileBefore15 outputFileBefore15 = new OutputFileBefore15();
    OutputFileAfter15 outputFileAfter15 = new OutputFileAfter15();


    public void sortTrades(){
        final double DELAY_COEFF = 55;
        double buttonCenter = sortTradesButton.getLayoutX() + sortTradesButton.getWidth() / 2 - DELAY_COEFF;
        if(inputFile.fileIsEmpty()){
            getValuesToGui();
            fileIsEmptyWarning.setLayoutX(buttonCenter - fileIsEmptyWarning.getPrefWidth() / 2);
            fileIsEmptyWarning.setText("Fisierul este gol!");
        }
        else{
            fileIsEmptyWarning.setText("");
            inputFile.writeFileLinesToList();
            if(inputFile.tableHasOnlyEmptyRows()){
                fileIsEmptyWarning.setLayoutX(buttonCenter - fileIsEmptyWarning.getPrefWidth() / 2);
                fileIsEmptyWarning.setText("Fisierul este gol!");
            }
            else{
                inputFile.processInputFileLines();
                outputFileBefore15.writeFile(inputFile.getTradesBefore15());
                outputFileAfter15.writeFile(inputFile.getTradesAfter15());
                getValuesToGui();
                inputFile.resetWinsAndLosses();
            }
        }
    }

    public void setStyles(){
        String styleClassName = "styling.css";
        background.setBackground(new Background(new BackgroundFill(Color.valueOf("05466B"), CornerRadii.EMPTY, Insets.EMPTY)));
        sortTradesButton.getStylesheets().add(getClass().getResource(styleClassName).toExternalForm());
        fileIsEmptyWarning.getStylesheets().add(getClass().getResource(styleClassName).toExternalForm());
        losesBefore15.getStylesheets().add(getClass().getResource(styleClassName).toExternalForm());
        winsBefore15.getStylesheets().add(getClass().getResource(styleClassName).toExternalForm());
        losesAndWinsBefore15.getStylesheets().add(getClass().getResource(styleClassName).toExternalForm());
        losesAfter15.getStylesheets().add(getClass().getResource(styleClassName).toExternalForm());
        winsAfter15.getStylesheets().add(getClass().getResource(styleClassName).toExternalForm());
        losesAndWinsAfter15.getStylesheets().add(getClass().getResource(styleClassName).toExternalForm());
        inputTradesComboBox.getStylesheets().add(getClass().getResource(styleClassName).toExternalForm());
        before15ComboBox.getStylesheets().add(getClass().getResource(styleClassName).toExternalForm());
        after15ComboBox.getStylesheets().add(getClass().getResource(styleClassName).toExternalForm());
    }

    public void getValuesToGui(){
        losesBefore15.setText(inputFile.getBefore15Losses() + "");
        winsBefore15.setText(inputFile.getBefore15Wins() + "");
        losesAndWinsBefore15.setText(inputFile.getBefore15WinsAndLosses() + "");
        losesAfter15.setText(inputFile.getAfter15Losses() + "");
        winsAfter15.setText(inputFile.getAfter15Wins() + "");
        losesAndWinsAfter15.setText(inputFile.getAfter15lWinsAndLosses() + "");
    }

    public void initialize(){
        configureGuiNodes();
        setStyles();
        loadFilesLocations();
        getValuesToGui();
    }

    private void configureGuiNodes(){
        losesBefore15.setEditable(false);
        winsBefore15.setEditable(false);
        losesAndWinsBefore15.setEditable(false);
        losesAfter15.setEditable(false);
        winsAfter15.setEditable(false);
        losesAndWinsAfter15.setEditable(false);
        fileIsEmptyWarning.setAlignment(Pos.CENTER);
        sortTradesButton.setDisable(true);
    }

    //This method will load all Excel files from the current directory to the comboBoxes
    public void loadFilesLocations(){
        Path localDirectory = FileSystems.getDefault().getPath("").normalize();
        List<String> filesNames = new ArrayList<>();
        try(DirectoryStream<Path> files = Files.newDirectoryStream(localDirectory,"*.xlsx")){
            files.forEach(value -> filesNames.add(value.toString()));
        }
        catch (IOException e){

        }
        for(int i = 0; i < filesNames.size(); i++)
        {
            String justFileName = filesNames.get(i);
            justFileName = justFileName.replaceAll(".*\\\\(.*)","$1");
            inputTradesComboBox.getItems().add(justFileName);
            before15ComboBox.getItems().add(justFileName);
            after15ComboBox.getItems().add(justFileName);
        }
    }

    private boolean inputFileSelected = false;
    private boolean before15FileSelected = false;
    private boolean after15FileSelected = false;

    public void setInputTradesFileName(){
        inputFile.getInputFileLocation(inputTradesComboBox.getValue());
        inputFileSelected = true;
        if(inputFileSelected && before15FileSelected && after15FileSelected){
            sortTradesButton.setDisable(false);
        }
    }

    public void setBefore15TradesFileName(){
        outputFileBefore15.getOutputFileLocation(before15ComboBox.getValue());
        before15FileSelected = true;
        if(inputFileSelected && before15FileSelected && after15FileSelected){
            sortTradesButton.setDisable(false);
        }
    }

    public void setAfter15TradesFileName() throws Exception{
        outputFileAfter15.getOutputFileLocation(after15ComboBox.getValue());
        after15FileSelected = true;
        if(inputFileSelected && before15FileSelected && after15FileSelected){
            sortTradesButton.setDisable(false);
        }
    }

}