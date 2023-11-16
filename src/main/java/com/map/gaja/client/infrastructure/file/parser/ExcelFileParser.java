package com.map.gaja.client.infrastructure.file.parser;

import com.map.gaja.client.domain.exception.InvalidFileException;
import com.map.gaja.client.infrastructure.file.parser.dto.RowVO;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Function;

@Scope("prototype")
@Component
public class ExcelFileParser implements FileParser {

    private final Map<String, Function<InputStream, Workbook>> supportedExcelFile =
            Map.of("xlsx", this::createXSSFWorkbook, "xls", this::createHSSFWorkbook);

    private InputStream excelStream;
    private Workbook workbook;
    private Sheet worksheet;

    private final int startRowIdx = 2;
    private final int endRowIdx = 1002;
    private final int nameColumnIdx = 1;
    private final int numberColumnIdx = 2;
    private final int addressColumnIdx = 3;
    private final int addressDetailColumnIdx = 4;

    private int currentRowIdx;

    @Override
    public void init(MultipartFile excel) {
        try {
            excelStream = excel.getInputStream();
            String extension = getExtension(excel);
            workbook = getWorkBook(extension, excelStream);
            worksheet = workbook.getSheetAt(0);
            currentRowIdx = startRowIdx;
        } catch (IOException e) {
            close();
            throw new InvalidFileException(e);
        }
    }

    @Override
    public void close() {
        try {
            if (workbook != null)
                workbook.close();
            if (excelStream != null)
                excelStream.close();
        } catch (Exception e) {
            throw new InvalidFileException(e);
        }
    }

    @Override
    public boolean supports(MultipartFile file) {
        String extension = getExtension(file);
        return supportedExcelFile.keySet().contains(extension);
    }

    @Override
    public RowVO nextRowData() {
        if(currentRowIdx == endRowIdx)
            return null;

        RowVO rowVO = getCurrentRowData();
        currentRowIdx++;

        return rowVO;
    }

    private RowVO getCurrentRowData() {
        Row row = worksheet.getRow(currentRowIdx);
        return getRowData(row);
    }

    @Override
    public boolean hasMoreData() {
        RowVO rowVO = getCurrentRowData();
        return hasData(rowVO);
    }

    @Override
    public int getStartRowIndex() {
        // 데이터가 0부터 시작하지만 사용자가 보는 엑셀은 1부터 시작
        return startRowIdx+1;
    }

    private static String getExtension(MultipartFile excel) {
        return FilenameUtils.getExtension(excel.getOriginalFilename());
    }

    private RowVO getRowData(Row row) {
        String name = getCellDataOrNull(row.getCell(nameColumnIdx));
        String phoneNumber = getCellDataOrNull(row.getCell(numberColumnIdx));
        String address = getCellDataOrNull(row.getCell(addressColumnIdx));
        String addressDetail = getCellDataOrNull(row.getCell(addressDetailColumnIdx));
        return new RowVO(name, phoneNumber, address, addressDetail);
    }

    /**
     * RowData가 비어있는지 확인
     */
    private boolean hasData(RowVO rowVO) {
        return rowVO != null &&
                (hasString(rowVO.getName()) || hasString(rowVO.getPhoneNumber())
                        || hasString(rowVO.getAddress()) || hasString(rowVO.getAddressDetail()));
    }

    private boolean hasString(String val) {
        return !StringUtils.isEmpty(val);
    }

    private Workbook getWorkBook(String extension, InputStream excelStream) throws IOException {
        return supportedExcelFile.get(extension).apply(excelStream);
    }

    /**
     * String 셀 데이터 가져오기
     */
    private static String getCellDataOrNull(Cell cell) {
        if (cell == null || !cell.getCellType().equals(CellType.STRING)) {
            return null;
        }

        return cell.getStringCellValue();
    }

    /**
     * .xlsx 확장자의 엑셀 파일 생성
     */
    private Workbook createXSSFWorkbook(InputStream inputStream) {
        try {
            return new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            throw new InvalidFileException(e);
        }
    }

    /**
     * .xls 확장자의 엑셀 파일 생성
     */
    private Workbook createHSSFWorkbook(InputStream inputStream) {
        try {
            return new HSSFWorkbook(inputStream);
        } catch (IOException e) {
            throw new InvalidFileException(e);
        }
    }
}
