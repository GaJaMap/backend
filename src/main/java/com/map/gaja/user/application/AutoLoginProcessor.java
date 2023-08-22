package com.map.gaja.user.application;

import com.map.gaja.client.apllication.ClientQueryService;
import com.map.gaja.client.presentation.dto.response.ClientListResponse;
import com.map.gaja.group.infrastructure.GroupRepository;
import com.map.gaja.group.presentation.dto.response.GroupInfo;
import com.map.gaja.user.domain.model.User;
import com.map.gaja.user.infrastructure.UserRepository;
import com.map.gaja.user.presentation.dto.response.AutoLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.map.gaja.user.application.UserServiceHelper.findExistingUser;

@Service
@RequiredArgsConstructor
public class AutoLoginProcessor {
    private final GroupRepository groupRepository;
    private final ClientQueryService clientQueryService;
    private final UserRepository userRepository;

    /**
     * 앱 실행 시 자동로그인에 대한 응답으로 사용자 최근 접속일도 처리.
     * @param email 사용자 이메일
     * @return 최근에 참조한 그룹정보와 그 그룹에 속한 클라이언트
     */
    @Transactional
    public AutoLoginResponse process(String email) {
        User user = findExistingUser(userRepository, email);
        user.updateLastLoginDate(); //최근 접속일 update

        Long referenceGroupId = user.getReferenceGroupId();
        GroupInfo groupInfo = null;
        ClientListResponse clientListResponse;

        if (isWholeGroup(referenceGroupId)) { //최근에 참조한 그룹이 전체일 경우
            clientListResponse = clientQueryService.findAllClient(email, null);
        } else { //최근에 참조한 그룹이 특정 그룹일 경우
            groupInfo = groupRepository.findGroupInfoById(referenceGroupId); //최근에 참조한 그룹 정보 조회
            clientListResponse = clientQueryService.findAllClientsInGroup(groupInfo.getGroupId(), null);
        }

        AutoLoginResponse response = new AutoLoginResponse(clientListResponse, groupInfo);
        return response;
    }

    private boolean isWholeGroup(Long referenceGroupId) {
        if (referenceGroupId == null) {
            return true;
        }
        return false;
    }
}
