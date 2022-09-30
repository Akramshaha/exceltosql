package co.in.nextgencoder.exceltosql.controller;

import co.in.nextgencoder.exceltosql.nativequery.ManualNativeQuery;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.gson.GsonBuilderCustomizer;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.io.*;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private ManualNativeQuery manualNativeQuery;

    @GetMapping("/")
    public String goHome(){
        return "index";
    }

    @PostMapping("/convert")
    public String convertTo(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes,
                            HttpServletRequest request, HttpServletResponse response) throws IOException {

        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        XSSFSheet sheet = workbook.getSheetAt(0);
        Row row1 = sheet.getRow(0);
        int i =1;
        String tableName = file.getOriginalFilename().replaceAll(".xlsx","").replaceAll(" ","_");
        String str="CREATE TABLE "+tableName+" ( ";
        for(Cell cell: row1){
            if(cell.getColumnIndex()+1 == row1.getLastCellNum()){
                str+=cell.getStringCellValue().replaceAll(" ","_")+" VARCHAR(255)";
            }else {
                str+=cell.getStringCellValue().replaceAll(" ","_")+" VARCHAR(255),";
            }

            i++;
        }
        str += " );";

        sheet.removeRow(row1);
        String str2 = " INSERT INTO "+tableName+" VALUES";
        int lastRowNum = sheet.getLastRowNum();
        for (Row rw: sheet){
            str2+=" (";
            for (Cell cl: rw){
                cl.setCellType(CellType.STRING);
                if(cl.getColumnIndex()+1 == rw.getLastCellNum()){
                    str2+="\""+cl.getStringCellValue()+"\"";
                }else {
                    str2+="\""+cl.getStringCellValue()+"\",";
                }
            }
            if(rw.getRowNum() != lastRowNum){
                str2+="),";
            }else{
                str2+=");";
            }
        }
        System.out.println(str);
        System.out.println(str2);

        //To save whole excel with header as column name in DataBase
        /*
        manualNativeQuery.nativeCreateTableQuery(str);
        manualNativeQuery.nativeCreateInsertIntoQuery(str2);
        */
        String filePath = "/home/akram/var/data/exceltosql/exceltosql.sql";
        File newFile = new File(filePath);
        if(newFile.createNewFile()){
            System.out.println("File Created");
        }else {
            System.out.println("File Already exists");
        }

        FileWriter fileWriter = new FileWriter(filePath);
        fileWriter.write(str);
        fileWriter.write("\n");
        fileWriter.write(str2);
        fileWriter.close();

        response.setContentType("application/sql");
        response.setHeader("Content-Disposition", String.format("inline; filename=\"" + newFile.getName() + "\""));
        response.setContentLength((int) newFile.length());
        response.getOutputStream();
        return "index";
    }
}
