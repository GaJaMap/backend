package com.map.gaja.user.presentation.api;

import com.map.gaja.global.log.TimeCheckLog;
import com.map.gaja.user.application.AutoLoginProcessor;
import com.map.gaja.user.application.UserService;
import com.map.gaja.user.presentation.dto.request.LoginRequest;
import com.map.gaja.user.presentation.api.specification.UserApiSpecification;
import com.map.gaja.user.presentation.dto.response.AutoLoginResponse;
import com.map.gaja.user.presentation.dto.response.LoginResponse;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Timed("user.login")
@TimeCheckLog
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController implements UserApiSpecification {
    private final UserService userService;
    private final AutoLoginProcessor autoLoginProcessor;

    @Override
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return new ResponseEntity<>(userService.login(request), HttpStatus.OK);
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    @DeleteMapping
    public ResponseEntity<Void> withdrawal(@AuthenticationPrincipal(expression = "userId") Long userId, HttpSession session) {
        userService.withdrawal(userId);
        session.invalidate();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    @GetMapping("/auto-login")
    public ResponseEntity<AutoLoginResponse> autoLogin(@AuthenticationPrincipal(expression = "userId") Long userId) {
        AutoLoginResponse response = autoLoginProcessor.process(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}