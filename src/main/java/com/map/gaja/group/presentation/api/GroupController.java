package com.map.gaja.group.presentation.api;

import com.map.gaja.global.log.TimeCheckLog;
import com.map.gaja.group.application.GroupService;
import com.map.gaja.group.presentation.api.specification.GroupApiSpecification;
import com.map.gaja.group.presentation.dto.request.GroupCreateRequest;
import com.map.gaja.group.presentation.dto.request.GroupUpdateRequest;
import com.map.gaja.group.presentation.dto.response.GroupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@TimeCheckLog
@RestController
@RequestMapping("/api/group")
@RequiredArgsConstructor
public class GroupController implements GroupApiSpecification {
    private final GroupService groupService;

    @Override
    @PostMapping
    public ResponseEntity<Long> create(
            @AuthenticationPrincipal(expression = "name") String email,
            @Valid @RequestBody GroupCreateRequest request
    ) {
        Long groupId = groupService.create(email, request);

        return new ResponseEntity<>(groupId, HttpStatus.CREATED);
    }

    @Override
    @GetMapping
    public ResponseEntity<GroupResponse> read(@AuthenticationPrincipal(expression = "name") String email, @PageableDefault Pageable pageable) {
        GroupResponse response = groupService.findGroups(email, pageable);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal(expression = "name") String email, @PathVariable Long groupId) {
        groupService.delete(email, groupId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    @PutMapping("/{groupId}")
    public ResponseEntity<Void> update(
            @AuthenticationPrincipal(expression = "name") String email,
            @PathVariable Long groupId,
            @Valid @RequestBody GroupUpdateRequest request
    ) {
        groupService.updateName(email, groupId, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
