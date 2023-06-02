package com.map.gaja.bundle.presentation.api;

import com.map.gaja.bundle.application.BundleService;
import com.map.gaja.bundle.presentation.api.specification.BundleApiSpecification;
import com.map.gaja.bundle.presentation.dto.request.BundleCreateRequest;
import com.map.gaja.bundle.presentation.dto.response.BundleResponse;
import com.map.gaja.global.annotation.LoginEmail;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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

    @Override
    @GetMapping
    public ResponseEntity<BundleResponse> read(@LoginEmail String email, @PageableDefault Pageable pageable) {
        BundleResponse response = bundleService.findBundles(email, pageable);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    @DeleteMapping("/{bundleId}")
    public ResponseEntity<Void> delete(@LoginEmail String email, @PathVariable Long bundleId) {
        bundleService.delete(email, bundleId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
