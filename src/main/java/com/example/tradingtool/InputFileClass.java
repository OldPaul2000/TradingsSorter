package com.example.tradingtool;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class InputFileClass {

    private String inputfileURI = "";
    private File inputFile;

    private List<String> inputFileLines = new ArrayList<>();
    private List<String> tradesBefore15 = new ArrayList<>();
    private List<String> tradesAfter15 = new ArrayList<>();

    private int before15Wins = 0;
    private int before15Losses = 0;
    private int before15WinsAndLosses = 0;

    private int after15Wins = 0;
    private int after15Losses = 0;
    private int after15WinsAndLosses = 0;

    private static int beginIndex = 0;


    public void getInputFileLocation(String fileName){
        inputfileURI = FileSystems.getDefault().getPath("").normalize().toString();
        inputfileURI += fileName;
        inputFile = new File(inputfileURI);
    }

    private String formatDoubleValues(int index,double value){
        String formatedValue = "";
        if(index == 0){
            formatedValue = String.format("%.0f",value);
        }
        else if((index == 2) || (index == 10) || (index == 12)){
            formatedValue = String.format("%.2f",value);
        }
        else if(((index >= 4) && (index <= 7)) || (index == 9)){
            formatedValue = String.format("%.5f",value);
        }
        else if((index == 11) || (index == 13)){
            formatedValue = String.format("%.1f",value);
        }
        return formatedValue;
    }

    public void writeFileLinesToList(){
        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFile));
            XSSFWorkbook workbook = new XSSFWorkbook(bis)){
            XSSFSheet tradesSheet = workbook.getSheetAt(0);
            int rows = tradesSheet.getLastRowNum();

            boolean nullCell = false;
            StringBuilder sb = new StringBuilder();
            for(int i = 1; i < rows; i++)
            {
                XSSFRow row = tradesSheet.getRow(i);
                for(int j = 0; j < row.getLastCellNum() - 1; j++)
                {
                    XSSFCell cell = row.getCell(j);
                    if(cell == null){
                        nullCell = true;
                        break;
                    }
                    else if((cell.getCellType() == CellType.NUMERIC) && ((j == 3) || (j == 8))){
                        LocalDateTime ldt = cell.getLocalDateTimeCellValue();
                        if(ldt.getSecond() == 0){
                            sb.append(ldt.toString().replaceAll("T", " ") + ":00" + "    ");
                        }
                        else{
                            sb.append(ldt.toString().replaceAll("T", " ") + "    ");
                        }
                    }
                    else if((cell.getCellType() == CellType.NUMERIC) && (j == 14)){
                        LocalTime lt = cell.getLocalDateTimeCellValue().toLocalTime();
                        sb.append(lt.toString() + "    ");
                    }
                    else if(cell.getCellType() == CellType.NUMERIC){
                        sb.append(formatDoubleValues(j,cell.getNumericCellValue()) + "    ");
                    }
                    else if(cell.getCellType() == CellType.STRING && j == 13){
                        double value = Double.parseDouble(cell.getStringCellValue());
                        sb.append(value + "    ");
                    }
                    else if(cell.getCellType() == CellType.STRING){
                        sb.append(cell.getStringCellValue() + "    ");
                    }
                }
                if(nullCell){
                    break;
                }
                inputFileLines.add(sb.toString());
                sb.setLength(0);
            }
        }
        catch (IOException e){

        }
    }

    public void resetWinsAndLosses (){
        before15Wins = 0;
        before15Losses = 0;
        before15WinsAndLosses = 0;
        after15Wins = 0;
        after15Losses = 0;
        after15WinsAndLosses = 0;
    }

    public void processInputFileLines(){
        tradesBefore15.clear();
        tradesAfter15.clear();
        resetWinsAndLosses();
        String[] infos;

        String type = "";
        double size = 0;
        String openTime = "";
        double openPrice = 0;
        double SL = 0;
        double initialSL = 0;
        double TP = 0;
        String closeTime = "";
        double closePrice = 0;
        double PL = 0;
        double pips = 0;
        double commission = 0;
        double drawdown = 0;
        String duration = "";

        beginIndex = inputFileLines.get(0).matches("Order(\\s+)Type(\\s+)Size(\\s+).+") ? 1 : 0;

        for(int i = beginIndex; i < inputFileLines.size(); i++)
        {
            infos = inputFileLines.get(i).split("\\t| {2,}");
            type = infos[1];
            size = Double.parseDouble(infos[2].replace(",","."));
            openTime = infos[3].replace(" ","T");
            openPrice = Double.parseDouble(infos[4].replace(",","."));
            SL = Double.parseDouble(infos[5].replace(",","."));
            initialSL = Double.parseDouble(infos[6].replace(",","."));
            TP = Double.parseDouble(infos[7].replace(",","."));
            closeTime = infos[8].replace(" ","T");;
            closePrice = Double.parseDouble(infos[9].replace(",","."));
            PL = Double.parseDouble(infos[10].replace(",","."));
            pips = Double.parseDouble(infos[11].replace(",","."));
            commission = Double.parseDouble(infos[12].replace(",","."));
            drawdown = Double.parseDouble(infos[13].replace(",","."));
            duration = infos[14];

            LocalDateTime dateTime = LocalDateTime.parse(openTime);
            if(dateTime.getHour() >= 0 && dateTime.getHour() < 15){
                tradesBefore15.add(inputFileLines.get(i) + "\n");
                before15Wins += PL > 100 ? 1 : 0;
                before15Losses += PL < -100 ? 1 : 0;
                before15WinsAndLosses += PL >= -100 && PL <= 100 ? 1 : 0;
            }
            else{
                tradesAfter15.add(inputFileLines.get(i) + "\n");
                after15Wins += PL > 100 ? 1 : 0;
                after15Losses += PL < -100 ? 1 : 0;
                after15WinsAndLosses += PL >= -100 && PL <= 100 ? 1 : 0;
            }
        }
        inputFileLines.clear();
    }

    public int getBefore15Wins(){
        return before15Wins;
    }

    public int getBefore15Losses(){
        return before15Losses;
    }

    public int getBefore15WinsAndLosses(){
        return before15WinsAndLosses;
    }

    public int getAfter15Wins(){
        return after15Wins;
    }

    public int getAfter15Losses(){
        return after15Losses;
    }

    public int getAfter15lWinsAndLosses(){
        return after15WinsAndLosses;
    }

    public List<String> getTradesBefore15(){
        return tradesBefore15;
    }

    public List<String> getTradesAfter15(){
        return tradesAfter15;
    }

    public boolean tableHasOnlyEmptyRows(){
        return inputFileLines.size() == 0;
    }

    public boolean fileIsEmpty(){
        return inputFile.length() == 0;
    }

}
