package com.map.gaja.temp.users;

import com.map.gaja.user.domain.model.Authority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/users")
public class TempUserController {
    @GetMapping
    public String findUser(Model model, String email) {
        model.addAttribute("users", new UserDetailInfo(email, LocalDateTime.now(), LocalDateTime.now(), 20L, Authority.VIP));

        return "admin/users/userDetail";
    }
}
