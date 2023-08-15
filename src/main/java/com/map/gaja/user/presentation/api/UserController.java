package com.map.gaja.user.presentation.api;

import com.map.gaja.client.apllication.ClientQueryService;
import com.map.gaja.client.presentation.dto.response.ClientListResponse;
import com.map.gaja.global.log.TimeCheckLog;
import com.map.gaja.group.application.GroupService;
import com.map.gaja.group.presentation.dto.response.GroupInfo;
import com.map.gaja.user.application.UserService;
import com.map.gaja.user.presentation.dto.request.LoginRequest;
import com.map.gaja.user.presentation.api.specification.UserApiSpecification;
import com.map.gaja.user.presentation.dto.response.AutoLoginResponse;
import com.map.gaja.user.presentation.dto.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@TimeCheckLog
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController implements UserApiSpecification {
    private final UserService userService;
    private final ClientQueryService clientQueryService;
    private final GroupService groupService;

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
    public ResponseEntity<Void> withdrawal(@AuthenticationPrincipal(expression = "name") String email, HttpSession session) {
        userService.withdrawal(email);
        session.invalidate();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/auto-login")
    public ResponseEntity<AutoLoginResponse> autoLogin(@AuthenticationPrincipal(expression = "name") String email) {
        GroupInfo groupInfo = groupService.findGroup(email);

        ClientListResponse clientListResponse;
        if (isWholeGroup(groupInfo)) { //최근에 참조한 그룹이 전체일 경우
            clientListResponse = clientQueryService.findAllClient(email, null);
        } else { //최근에 참조한 그룹이 특정 그룹일 경우
            clientListResponse = clientQueryService.findAllClientsInGroup(groupInfo.getGroupId(), null);
        }

        AutoLoginResponse response = new AutoLoginResponse(clientListResponse, groupInfo);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private boolean isWholeGroup(GroupInfo groupInfo) {
        if (groupInfo == null) {
            return true;
        }
        return false;
    }
}