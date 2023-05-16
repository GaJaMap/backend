package com.map.gaja.client.presentation.dto.request;

import com.map.gaja.client.presentation.dto.request.subdto.LocationDto;
import com.map.gaja.client.presentation.dto.response.ClientResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NearbyClientSearchRequest {
    private LocationDto location; // 위도 경도
    private double radius; // 반경(미터)
}
