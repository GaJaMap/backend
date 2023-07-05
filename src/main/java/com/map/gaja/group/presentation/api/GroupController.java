package com.map.gaja.group.presentation.api;

import com.map.gaja.group.application.GroupService;
import com.map.gaja.group.presentation.api.specification.GroupApiSpecification;
import com.map.gaja.group.presentation.dto.request.GroupCreateRequest;
import com.map.gaja.group.presentation.dto.request.GroupUpdateRequest;
import com.map.gaja.group.presentation.dto.response.GroupResponse;
import com.map.gaja.global.annotation.LoginEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/group")
@RequiredArgsConstructor
public class GroupController implements GroupApiSpecification {
    private final GroupService groupService;

    @Override
    @PostMapping
    public ResponseEntity<Void> create(
            @LoginEmail String email,
            @RequestBody GroupCreateRequest request
    ) {
        groupService.create(email, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @Override
    @GetMapping
    public ResponseEntity<GroupResponse> read(@LoginEmail String email, @PageableDefault Pageable pageable) {
        GroupResponse response = groupService.findGroups(email, pageable);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> delete(@LoginEmail String email, @PathVariable Long groupId) {
        groupService.delete(email, groupId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    @PutMapping
    public ResponseEntity<Void> update(@LoginEmail String email, @RequestBody GroupUpdateRequest request) {
        groupService.updateName(email, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
