package com.map.gaja.client.infrastructure.file.excel;

import com.map.gaja.client.presentation.dto.request.subdto.AddressDto;
import com.map.gaja.client.presentation.dto.request.subdto.LocationDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientExcelData {
    private String name;
    private String phoneNumber;
    private String address;
    private String addressDetail;
    private LocationDto location;
    private Boolean isValid;

    public void setLocation(LocationDto location) {
        this.location = location;
    }

    public void setValid(Boolean valid) {
        isValid = valid;
    }
}
