package com.map.gaja.user.presentation.web;

import com.map.gaja.global.authentication.AuthenticationHandler;
import com.map.gaja.global.log.TimeCheckLog;
import com.map.gaja.user.domain.model.User;
import com.map.gaja.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@TimeCheckLog
@Controller
@RequiredArgsConstructor
public class WebUserLoginController {

    private final UserRepository userRepository;
    private final AuthenticationHandler authenticationHandler;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/testLogin")
    public String loginSuccess(String loginEmail) {
        // 임시 로그인
        System.out.println(loginEmail);
        User user = userRepository.findByEmail(loginEmail).get();

        authenticationHandler.saveContext(loginEmail, user.getAuthority().toString());
        return "redirect:/";
    }
}
