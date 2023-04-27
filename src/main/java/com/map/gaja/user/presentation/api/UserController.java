package com.map.gaja.user.presentation.api;

import com.map.gaja.user.presentation.dto.request.Req;
import com.map.gaja.user.presentation.dto.response.Res;
import com.map.gaja.user.presentation.swagger.UserApiSpecification;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController implements UserApiSpecification {

    @Override
    @PostMapping("/test")
    public Res test(@RequestBody Req req) {
        return new Res(1L);
    }
}
