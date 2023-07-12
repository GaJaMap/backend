package com.map.gaja.user.presentation.api;

import com.map.gaja.user.application.UserService;
import com.map.gaja.user.presentation.dto.request.LoginRequest;
import com.map.gaja.user.presentation.api.specification.UserApiSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController implements UserApiSpecification {
    private final UserService userService;

    @Override
    @PostMapping("/login")
    public ResponseEntity<Long> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> withdrawal(@AuthenticationPrincipal String email, HttpSession session) {
        userService.withdrawal(email);
        session.invalidate();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
