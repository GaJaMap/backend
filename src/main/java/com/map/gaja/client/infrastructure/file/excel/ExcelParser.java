package com.map.gaja.client.infrastructure.file.excel;

import com.map.gaja.client.domain.exception.InvalidFileException;
import com.map.gaja.client.presentation.dto.request.subdto.LocationDto;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
    private static MockLocationGetter locationGetter = new MockLocationGetter();
    private static final String PHONE_NUMBER_PATTERN = "^\\d{2,3}-\\d{3,4}-\\d{4}$";
    private static final int ADDRESS_LENGTH_LIMIT = 50;
    private static final int DETAIL_LENGTH_LIMIT = 20;
    private static final int NAME_LENGTH_LIMIT = 15;

    static class MockLocationGetter {
        public void settingLocation(List<ClientExcelData> clientData) {
            clientData.forEach((cd) -> {
                cd.setLocation(new LocationDto(37.51, 127.023));
            });
        }
    }

    public List<ClientExcelData> parseClientExcelFile(MultipartFile excel) {
        List<ClientExcelData> dataList = new ArrayList<>();
        try (InputStream excelStream = excel.getInputStream()){
            String extension = FilenameUtils.getExtension(excel.getOriginalFilename());
            Workbook workbook = getWorkBook(extension, excelStream);
            Sheet worksheet = workbook.getSheetAt(0);

            for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
                Row row = worksheet.getRow(i);

                ClientExcelData clientData = new ClientExcelData();

                String name = row.getCell(0).getStringCellValue();
                String phoneNumber = row.getCell(1).getStringCellValue();
                String address = row.getCell(2).getStringCellValue();
                String addressDetail = row.getCell(3).getStringCellValue();

                clientData.setName(name);
                clientData.setPhoneNumber(phoneNumber);
                clientData.setAddress(address);
                clientData.setAddressDetail(addressDetail);

                if (invalidateName(name)
                        || invalidatePhoneNumber(phoneNumber)
                        || invalidateAddress(address)
                        || invalidateAddressDetail(addressDetail)) {
                    clientData.setIsValid(false);
                    continue;
                }
                else {
                    clientData.setIsValid(true);
                }

                dataList.add(clientData);
            }
        } catch (IOException e) {
            throw new InvalidFileException(e);
        }

        locationGetter.settingLocation(dataList);
        return dataList;
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

    private boolean invalidateAddressDetail(String addressDetailString) {
        return addressDetailString.length() > DETAIL_LENGTH_LIMIT;
    }

    private boolean invalidateAddress(String addressString) {
        return addressString.length() > ADDRESS_LENGTH_LIMIT;
    }

    private boolean invalidatePhoneNumber(String phoneNumber) {
        return !Pattern.matches(PHONE_NUMBER_PATTERN, phoneNumber);
    }

    private boolean invalidateName(String name) {
        return name.length() > NAME_LENGTH_LIMIT;
    }
}
