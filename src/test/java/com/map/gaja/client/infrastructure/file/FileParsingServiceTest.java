package com.map.gaja.client.infrastructure.file;

import com.map.gaja.client.infrastructure.file.exception.FileNotAllowedException;
import com.map.gaja.client.infrastructure.file.parser.FileParser;
import com.map.gaja.client.infrastructure.file.parser.dto.ClientFileDto;
import com.map.gaja.client.infrastructure.file.parser.dto.RowVO;
import com.map.gaja.client.infrastructure.file.parser.validator.ClientDataValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileParsingServiceTest {
    @Mock
    ClientDataValidator clientDataValidator;
    @Mock
    ObjectProvider<List<FileParser>> fileParserProvider;
    @InjectMocks
    FileParsingService parsingService;

    RowVO row1 = new RowVO("Name1", "010-1111-2222", "Address1", "AddressDetail1");
//    RowVO row2 = new RowVO("Name2", "01022223333", "Address2", "AddressDetail2");

    @Test
    @DisplayName("정상 파싱")
    void success() {
        int rowIdx = 1;
        boolean isInvalid = false;

        FileParser parser = mock(FileParser.class);
        when(parser.supports(any())).thenReturn(true);
        when(parser.hasMoreData()).thenReturn(true, false);
        when(parser.nextRowData()).thenReturn(row1);
        when(parser.getStartRowIndex()).thenReturn(rowIdx);

        when(fileParserProvider.getObject()).thenReturn(List.of(parser));
        when(clientDataValidator.isInvalidData(any())).thenReturn(isInvalid);
        List<ClientFileDto> result = parsingService.parseClientFile(TestFileCreator.getMockExcelFile());
        ClientFileDto clientFileDto = result.get(0);

        assertThat(clientFileDto.getIsValid()).isEqualTo(!isInvalid);
        assertThat(clientFileDto.getName()).isEqualTo(row1.getName());
        assertThat(clientFileDto.getAddress()).isEqualTo(row1.getAddress());
        assertThat(clientFileDto.getAddressDetail()).isEqualTo(row1.getAddressDetail());
        assertThat(clientFileDto.getPhoneNumber()).isEqualTo(row1.getPhoneNumber().replace("-", ""));
    }

    @Test
    @DisplayName("Paser를 찾을 수 없음")
    void notFoundParser() throws IOException {
        FileParser parser = mock(FileParser.class);
        when(parser.supports(any())).thenReturn(false);
        when(fileParserProvider.getObject()).thenReturn(List.of(parser));

        assertThrows(FileNotAllowedException.class,
                () -> parsingService.parseClientFile(TestFileCreator.getFailExcelFile()));
    }

}