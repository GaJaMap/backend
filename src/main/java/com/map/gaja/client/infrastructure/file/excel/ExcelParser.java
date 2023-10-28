package com.map.gaja.client.infrastructure.file.excel;

import com.map.gaja.client.domain.exception.InvalidFileException;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class ExcelParser {
    private static final int MAXIMUM_EXCEL_ROW_DATA = 200;
    private static final int DATA_START_ROW_INDEX = 1;
    private static final int APP_TO_EXCEL_IDX = 1;


    private static class CellIndexConst {
        private static final int NAME = 0;
        private static final int PHONE_NUMBER = 1;
        private static final int ADDRESS = 2;
        private static final int ADDRESS_DETAIL = 3;
        private static final int MEMO = 4;
    }

    private static class ValidConst {
        private static final String PHONE_NUMBER_PATTERN = "^\\d{2,3}-\\d{3,4}-\\d{4}$";
        private static final int ADDRESS_LENGTH_MIN_LIMIT = 10;
        private static final int ADDRESS_LENGTH_MAX_LIMIT = 40;
        private static final int DETAIL_LENGTH_LIMIT = 20;
        private static final int NAME_LENGTH_LIMIT = 20;
        private static final int MEMO_LENGTH_LIMIT = 200;
    }

    @AllArgsConstructor
    static class RowData {
        public String name;
        public String phoneNumber;
        public String address;
        public String addressDetail;
        public String memo;
    }

    public List<ClientExcelDto> parseClientExcelFile(MultipartFile excel) {
        List<ClientExcelDto> dataList = new ArrayList<>();
        try (InputStream excelStream = excel.getInputStream()) {
            String extension = FilenameUtils.getExtension(excel.getOriginalFilename());
            Workbook workbook = getWorkBook(extension, excelStream);
            Sheet worksheet = workbook.getSheetAt(0);
            int endRow = Math.min(DATA_START_ROW_INDEX + MAXIMUM_EXCEL_ROW_DATA, worksheet.getLastRowNum());
            for (int rowIdx = DATA_START_ROW_INDEX; rowIdx <= endRow; rowIdx++) {
                Row row = worksheet.getRow(rowIdx);

                if (row == null || row.getLastCellNum() == -1) {
                    break; // 사용한 적 없는 ROW
                }

                RowData rowData = getRowData(row);
                ClientExcelDto clientData = new ClientExcelDto();
                if (isEmptyRowData(rowData)) {
                    break; // 데이터가 전부 지워져 있는 ROW
                }
                setDataNormalField(clientData, rowIdx, rowData);

                if (isInvalidRowData(rowData)) {
                    clientData.setIsValid(false);
                }
                else {
                    clientData.setIsValid(true);
                }

                dataList.add(clientData);
            }
        } catch (IOException e) {
            throw new InvalidFileException(e);
        }

        return dataList;
    }

    private boolean isInvalidRowData(RowData rowData) {
        return invalidateName(rowData.name)
                || invalidatePhoneNumber(rowData.phoneNumber)
                || invalidateAddress(rowData.address)
                || invalidateAddressDetail(rowData.addressDetail)
                || invalidateMemo(rowData.memo);
    }

    private void setDataNormalField(ClientExcelDto clientData, int rowIdx, RowData rowData) {
        clientData.setRowIdx(rowIdx+ APP_TO_EXCEL_IDX);
        clientData.setName(rowData.name);
        clientData.setPhoneNumber(rowData.phoneNumber);
        clientData.setAddress(rowData.address);
        clientData.setAddressDetail(rowData.addressDetail);
        clientData.setMemo(rowData.memo);
    }

    private RowData getRowData(Row row) {
        String name = getCellDataOrNull(row.getCell(CellIndexConst.NAME));
        String phoneNumber = getCellDataOrNull(row.getCell(CellIndexConst.PHONE_NUMBER));
        String address = getCellDataOrNull(row.getCell(CellIndexConst.ADDRESS));
        String addressDetail = getCellDataOrNull(row.getCell(CellIndexConst.ADDRESS_DETAIL));
        String memo = getCellDataOrNull(row.getCell(CellIndexConst.MEMO));
        return new RowData(name, phoneNumber, address, addressDetail, memo);
    }

    private boolean isEmptyRowData(RowData rowData) {
        return isEmptyCell(rowData.name) && isEmptyCell(rowData.phoneNumber) && isEmptyCell(rowData.address) && isEmptyCell(rowData.addressDetail);
    }

    private boolean isEmptyCell(String cellData) {
        return cellData == null || cellData.length() == 0;
    }

    private static String getCellDataOrNull(Cell cell) {
        if (cell == null || !cell.getCellType().equals(CellType.STRING)) {
            return null;
        }

        return cell.getStringCellValue();
    }

    private Workbook getWorkBook(String extension, InputStream excelStream) throws IOException {
        if (extension.equals("xlsx")) {
            return new XSSFWorkbook(excelStream);
        } else if (extension.equals("xls")) {
            return new HSSFWorkbook(excelStream);
        } else {
            throw new InvalidFileException();
        }
    }

    private boolean invalidateMemo(String memo) {
        return memo != null && memo.length() > ValidConst.MEMO_LENGTH_LIMIT;
    }

    private boolean invalidateAddressDetail(String addressDetailString) {
        return addressDetailString != null && addressDetailString.length() > ValidConst.DETAIL_LENGTH_LIMIT;
    }

    private boolean invalidateAddress(String addressString) {
        return addressString != null
                && (addressString.length() > ValidConst.ADDRESS_LENGTH_MAX_LIMIT
                        || addressString.length() < ValidConst.ADDRESS_LENGTH_MIN_LIMIT);
    }

    private boolean invalidatePhoneNumber(String phoneNumber) {
        return phoneNumber != null && !Pattern.matches(ValidConst.PHONE_NUMBER_PATTERN, phoneNumber);
    }

    private boolean invalidateName(String name) {
        return isEmptyCell(name) || name.length() > ValidConst.NAME_LENGTH_LIMIT;
    }
}
