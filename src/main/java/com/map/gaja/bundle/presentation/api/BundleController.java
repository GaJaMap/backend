package com.map.gaja.bundle.presentation.api;

import com.map.gaja.bundle.application.BundleService;
import com.map.gaja.bundle.presentation.api.specification.BundleApiSpecification;
import com.map.gaja.bundle.presentation.dto.request.BundleCreateRequest;
import com.map.gaja.global.annotation.LoginEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bundle")
@RequiredArgsConstructor
public class BundleController implements BundleApiSpecification {
    private final BundleService bundleService;

    @Override
    @PostMapping
    public ResponseEntity<Void> create(
            @LoginEmail String email,
            @RequestBody BundleCreateRequest request
    ) {
        bundleService.create(email, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }
}
