package com.map.gaja.user.presentation.api;

import com.map.gaja.user.application.UserService;
import com.map.gaja.user.presentation.dto.request.LoginRequest;
import com.map.gaja.user.presentation.dto.request.Req;
import com.map.gaja.user.presentation.dto.response.Res;
import com.map.gaja.user.presentation.api.specification.UserApiSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController implements UserApiSpecification {
    private final UserService userService;

    @Override
    @PostMapping("/test")
    public Res test(@RequestBody Req req) {
        return new Res(1L);
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<Integer> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping("/hi")
    public String hi(@AuthenticationPrincipal String email) {
//        HttpSession session = request.getSession(false);
//        System.out.println(session.getId());
//        System.out.println(session.toString());
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //session.invalidate();

        return email;
    }

}
