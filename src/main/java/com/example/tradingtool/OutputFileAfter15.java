package com.example.tradingtool;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.FileSystems;
import java.util.List;

public class OutputFileAfter15 extends OutputFileBefore15{
    //This class writes all the trades from InputFileClass that are after 15:00

    private String inputfileURI = "";
    private File outputFile;


    public void getOutputFileLocation(String fileName){
        inputfileURI = FileSystems.getDefault().getPath("").normalize().toString();
        outputFile = new File(inputfileURI + fileName);
    }

    private XSSFWorkbook getCurrentWorkbook(File file){
        if(file.length() == 0){
            return null;
        }
        XSSFWorkbook currentWorkbook = null;
        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))){
            currentWorkbook = new XSSFWorkbook(bis);
        }
        catch (IOException e){

        }
        return currentWorkbook;
    }

    private void writeColumnsNames(XSSFWorkbook workbook){
        XSSFSheet sheet = workbook.getSheetAt(0);
        XSSFRow row = sheet.createRow(0);
        String[] columnsNames = {"Order","Type","Size","Open Time","Open Price","SL","Initial SL",
                "TP","Close Time","Close Price","P/L","Pips","Commission","Drawdown","Duration"};
        for(int i = 0; i < 15; i++)
        {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(columnsNames[i]);
            CellStyle style = workbook.createCellStyle();
            style.setFillBackgroundColor(IndexedColors.BLUE_GREY.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cell.setCellStyle(style);
        }
    }

    public void writeFile(List<String> inputLines){
        XSSFWorkbook workbook;
        if(outputFile.length() != 0){
            workbook = getCurrentWorkbook(outputFile);
            int lastRow = workbook.getSheetAt(0).getLastRowNum();
            XSSFCell cell;
            if(lastRow < 0){
                cell = null;
            }
            else{
                cell = workbook.getSheetAt(0).getRow(0).getCell(0);
            }
            if(lastRow != 0 && (cell == null || !cell.getStringCellValue().equals("Order"))){
                clearFile();
                workbook = new XSSFWorkbook();
                workbook.createSheet("Sheet1");
                writeColumnsNames(workbook);
            }
        }
        else{
            workbook = new XSSFWorkbook();
            workbook.createSheet("Sheet1");
            writeColumnsNames(workbook);
        }
        try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile))){
            XSSFSheet sheet = workbook.getSheetAt(0);
            int lastRow = sheet.getLastRowNum() + 1;
            String[] line;
            int index = 0;
            for(int i = lastRow; i < inputLines.size() + lastRow; i++)
            {
                XSSFRow row = sheet.createRow(i);
                line = inputLines.get(index).split(" {2,}");
                for(int j = 0; j < line.length; j++)
                {
                    XSSFCell cell = row.createCell(j);
                    cell.setCellValue(line[j]);
                }
                index++;
            }
            sheet.createRow(sheet.getLastRowNum() + 1);
            sheet.setColumnWidth(3,5000);
            sheet.setColumnWidth(4,2600);
            sheet.setColumnWidth(8,5000);
            sheet.setColumnWidth(9,2600);
            sheet.setColumnWidth(12,2800);
            sheet.setColumnWidth(13,2600);

            workbook.write(bos);
        }
        catch (IOException e){

        }
    }

    private void clearFile(){
        try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile))){
            bos.flush();
        }
        catch (IOException e){

        }
    }

}
