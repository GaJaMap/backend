package com.map.gaja.client.infrastructure.file.parser.validator;

import com.map.gaja.client.infrastructure.file.parser.dto.ParsedClientDto;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * 파일에서 파싱한 고객 정보 유효성 검사
 */
@Component
public class ClientDataValidator {
    private final int DETAIL_LENGTH_LIMIT = 40;
    private final int ADDRESS_LENGTH_MIN_LIMIT = 10;
    private final int ADDRESS_LENGTH_MAX_LIMIT = 40;
    private final String PHONE_NUMBER_PATTERN = "^[0-9]{7,12}$";
    private final int NAME_LENGTH_LIMIT = 20;



    public boolean isInvalidData(ParsedClientDto rowData) {
        return invalidateName(rowData.getName())
                || invalidatePhoneNumber(rowData.getPhoneNumber())
                || invalidateAddress(rowData.getAddress())
                || invalidateAddressDetail(rowData.getAddressDetail());
    }

    private boolean invalidateAddressDetail(String addressDetailString) {
        return addressDetailString != null && addressDetailString.length() > DETAIL_LENGTH_LIMIT;
    }

    private boolean invalidateAddress(String addressString) {
        return addressString != null
                && (addressString.length() > ADDRESS_LENGTH_MAX_LIMIT
                || addressString.length() < ADDRESS_LENGTH_MIN_LIMIT);
    }

    private boolean invalidatePhoneNumber(String phoneNumber) {
        return phoneNumber != null && !Pattern.matches(PHONE_NUMBER_PATTERN, phoneNumber);
    }

    private boolean invalidateName(String name) {
        return name == null || name.length() > NAME_LENGTH_LIMIT;
    }
}
