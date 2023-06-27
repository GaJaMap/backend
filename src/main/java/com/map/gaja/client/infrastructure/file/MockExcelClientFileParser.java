package com.map.gaja.client.infrastructure.file;

import com.map.gaja.client.presentation.dto.request.NewClientBulkRequest;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.client.presentation.dto.request.subdto.AddressDto;
import com.map.gaja.client.presentation.dto.request.subdto.LocationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class MockExcelClientFileParser implements ClientFileParser {

    @Override
    public boolean isSupported(MultipartFile file) {
        String oriName = file.getOriginalFilename();
        String fileType = oriName.substring(oriName.lastIndexOf(".")+1);
        log.info("fileType = {}", fileType);

        return fileType.equals(SupportedFileType.XLS.getType())
                || fileType.equals(SupportedFileType.XLSX.getType());
    }

    @Override
    public NewClientBulkRequest parse(MultipartFile file) {
        log.info("ExcelClientFileParser.parse");
        // 일단 단순 값 반환.
        List<NewClientRequest> list = new ArrayList<>();
        NewClientRequest mock1 = new NewClientRequest("Mock1", 0L, "010-1111-2222",
                new AddressDto("aa", "bb", "cc", "dd"), new LocationDto(34.34, 125.125), null);
        NewClientRequest mock2 = new NewClientRequest("Mock2", 0L, "010-1111-2222",
                new AddressDto("aa", "bb", "cc", "dd"), new LocationDto(35.35, 126.126), null);

        list.add(mock1);
        list.add(mock2);

        return new NewClientBulkRequest(list);
    }
}
