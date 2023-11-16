package com.map.gaja.client.infrastructure.file;

import com.map.gaja.client.domain.exception.InvalidFileException;
import com.map.gaja.client.infrastructure.file.parser.dto.ParsedClientDto;
import com.map.gaja.client.infrastructure.file.exception.FileNotAllowedException;
import com.map.gaja.client.infrastructure.file.parser.FileParser;
import com.map.gaja.client.infrastructure.file.parser.dto.RowVO;
import com.map.gaja.client.infrastructure.file.parser.validator.ClientDataValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


@Component
@RequiredArgsConstructor
public class FileParsingService {
    private final ClientDataValidator clientDataValidator;
    private final ObjectProvider<List<FileParser>> fileParserProvider;

    public List<ParsedClientDto> parseClientFile(MultipartFile file) {
        List<ParsedClientDto> dataList = new ArrayList<>();
        FileParser parser = getParser(file);
        try (parser) {
            parser.init(file);
            int startRowIndex = parser.getStartRowIndex();
            int count = 0;
            while (parser.hasMoreData()) {
                RowVO rowVO = parser.nextRowData();
                int rowIdx = startRowIndex + count;
                ParsedClientDto clientData = convertDataToDto(rowIdx, rowVO);
                dataList.add(clientData);
                count++;
            }
        } catch (Exception e) {
            throw new InvalidFileException(e);
        }

        return dataList;
    }


    /**
     * 알맞는 FileParser를 가져옴
     * @param file "sample.xlsx"
     * @return 엑셀을 파싱할 수 있는 ExcelParser
     */
    private FileParser getParser(MultipartFile file) {
        for (FileParser fileParser : fileParserProvider.getObject()) {
            if(fileParser.supports(file))
                return fileParser;
        }

        throw new FileNotAllowedException();
    }

    /**
     * 유효하지 않은 데이터
     */
    private boolean isValidData(ParsedClientDto clientData) {
        return !clientDataValidator.isInvalidData(clientData);
    }

    /**
     * RowData -> ClientExcelData 변환
     */
    private ParsedClientDto convertDataToDto(int rowIdx, RowVO rowVO) {
        ParsedClientDto clientData = new ParsedClientDto();
        clientData.setRowIdx(rowIdx);
        clientData.setName(rowVO.getName());
        if(!StringUtils.isEmpty(rowVO.getPhoneNumber()))
            clientData.setPhoneNumber(rowVO.getPhoneNumber().replace("-", ""));
        clientData.setAddress(rowVO.getAddress());
        clientData.setAddressDetail(rowVO.getAddressDetail());

        boolean valid = isValidData(clientData);
        clientData.setValid(valid);

        return clientData;
    }
}
