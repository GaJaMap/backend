package com.map.gaja.user.presentation.api;

import com.map.gaja.user.application.UserService;
import com.map.gaja.user.presentation.dto.request.LoginRequest;
import com.map.gaja.user.presentation.api.specification.UserApiSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController implements UserApiSpecification {
    private final UserService userService;

    @Override
    @PostMapping("/login")
    public ResponseEntity<Integer> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

}
