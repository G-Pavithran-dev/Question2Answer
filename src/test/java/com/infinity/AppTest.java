    package com.infinity;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class AppTest 
{
    XSSFWorkbook workbook;
    XSSFSheet sheet1;
    XSSFSheet sheet2;
    List<ExcellData>excellDatasList=new ArrayList<>();
    @BeforeTest
    public void XssfClassesDefinition() throws Exception
    {
        workbook=new XSSFWorkbook("Question.xlsx");
        sheet1=workbook.getSheetAt(0);
        sheet2=workbook.getSheetAt(1);
    }
    @BeforeMethod
    public void ExcellDataRetrieval() throws Exception
    {
        int rowCount=sheet1.getLastRowNum();
        int colCount=sheet1.getRow(1).getLastCellNum();
        for(int i=1;i<=rowCount;i++)
        {
                XSSFRow currentrow=sheet1.getRow(i);
        
                int curSerialNumber=(int)currentrow.getCell(0).getNumericCellValue();
                String curQuestion=currentrow.getCell(1).toString();
                int curMarks=(int)currentrow.getCell(2).getNumericCellValue();
                String curAdditionalInfo=currentrow.getCell(3)==null?"":currentrow.getCell(3).toString();
            
                ExcellData excellDataobject=new ExcellData(curSerialNumber, curQuestion, curMarks, curAdditionalInfo);
                excellDatasList.add(excellDataobject);
        }
    }
    @Test
    public void test1()
    {
        // output check
        System.out.println(excellDatasList.get(1).getQuestion());
    }
}
