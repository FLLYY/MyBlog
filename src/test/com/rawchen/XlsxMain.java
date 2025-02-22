package com.rawchen;

import java.io.IOException;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XlsxMain {

    public static void main( String[] args) throws IOException {
        XlsxMain xlsxMain = new XlsxMain();

        xlsxMain.readXlsx();
    }

    private void readXlsx() throws IOException{
        String fileName = "C:\\Users\\86199\\Desktop\\后端开发\\blog-ssm-master\\blog-ssm\\src\\main\\resources\\upload\\file\\aa.xlsx";
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook( fileName);

        // 循环工作表Sheet
        for(int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++){
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt( numSheet);
            if(xssfSheet == null){
                continue;
            }

            // 循环行Row
            for(int rowNum = 0; rowNum <= xssfSheet.getLastRowNum(); rowNum++ ){
                XSSFRow xssfRow = xssfSheet.getRow( rowNum);
                if(xssfRow == null){
                    continue;
                }

                // 循环列Cell
                for(int cellNum = 0; cellNum <= xssfRow.getLastCellNum(); cellNum++){
                    XSSFCell xssfCell = xssfRow.getCell( cellNum);
                    if(xssfCell == null){
                        continue;
                    }
                    System.out.print("   "+getValue(xssfCell));
                }
                System.out.println();
            }
        }
    }

    @SuppressWarnings("static-access")
    private String getValue(XSSFCell xssfCell){
        if(xssfCell.getCellType() == CellType.BOOLEAN){
            return String.valueOf(xssfCell.getBooleanCellValue());
        } else if(xssfCell.getCellType() == CellType.NUMERIC){
            return String.valueOf(xssfCell.getNumericCellValue());
        } else {
            return String.valueOf(xssfCell.getStringCellValue());
        }
    }




}