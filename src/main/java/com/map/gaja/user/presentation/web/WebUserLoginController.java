package com.map.gaja.user.presentation.web;

import com.map.gaja.global.authentication.AuthenticationRepository;
import com.map.gaja.global.authentication.PrincipalDetails;
import com.map.gaja.global.log.TimeCheckLog;
import com.map.gaja.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@TimeCheckLog
@Controller
@RequiredArgsConstructor
public class WebUserLoginController {

    private final UserRepository userRepository;
    private final AuthenticationRepository authenticationRepository;

    @GetMapping("/login")
    public String login(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails != null) {
            return "redirect:/";
        }

        return "login";
    }

//    @GetMapping("/testLogin")
//    public String loginSuccess() {
//        // 임시 로그인
//        String loginEmail = "email3@example.com";
//        User user = userRepository.findByEmailAndActive(loginEmail).get();
//
//        authenticationRepository.saveContext(user.getId(), loginEmail, user.getAuthority().toString());
//        return "redirect:/";
//    }
}
