package com.map.gaja.user.application;

import com.map.gaja.client.apllication.ClientQueryService;
import com.map.gaja.client.infrastructure.s3.S3UrlGenerator;
import com.map.gaja.client.presentation.dto.response.ClientListResponse;
import com.map.gaja.group.infrastructure.GroupRepository;
import com.map.gaja.group.presentation.dto.response.GroupInfo;
import com.map.gaja.user.domain.model.User;
import com.map.gaja.user.infrastructure.UserRepository;
import com.map.gaja.user.presentation.dto.response.AutoLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.map.gaja.user.application.UserServiceHelper.findByEmailAndActive;

@Service
@RequiredArgsConstructor
public class AutoLoginProcessor {
    private final GroupRepository groupRepository;
    private final ClientQueryService clientQueryService;
    private final UserRepository userRepository;
    private final S3UrlGenerator s3UrlGenerator;

    /**
     * 앱 실행 시 자동로그인에 대한 응답으로 사용자 최근 접속일도 처리.
     *
     * @param email 사용자 이메일
     * @return 최근에 참조한 그룹정보와 그 그룹에 속한 클라이언트
     */
    @Transactional
    public AutoLoginResponse process(String email) {
        User user = findByEmailAndActive(userRepository, email);
        user.updateLastLoginDate(); //최근 접속일 update

        Long referenceGroupId = user.getReferenceGroupId();
        GroupInfo groupInfo = getReferenceGroupInfo(referenceGroupId); //최근에 참조한 그룹 정보 조회

        ClientListResponse recentGroupClients = getRecentGroupClients(email, referenceGroupId); //최근에 참조한 그룹에 속한 client 조회

        AutoLoginResponse response = new AutoLoginResponse(recentGroupClients, s3UrlGenerator.getS3Url(), groupInfo);
        return response;
    }

    private ClientListResponse getRecentGroupClients(String email, Long referenceGroupId) {
        if (isWholeGroup(referenceGroupId)) { //최근에 참조한 그룹이 전체일 경우
            return clientQueryService.findAllClient(email, null);
        }

        return clientQueryService.findAllClientsInGroup(referenceGroupId, null);
    }

    private GroupInfo getReferenceGroupInfo(Long referenceGroupId) {
        GroupInfo groupInfo = groupRepository.findGroupInfoById(referenceGroupId)
                .orElseGet(this::createWholeGroup);
        return groupInfo;
    }

    private GroupInfo createWholeGroup() {
        return new GroupInfo() {
            @Override
            public Long getGroupId() {
                return -1L;
            }

            @Override
            public String getGroupName() {
                return "전체";
            }

            @Override
            public Integer getClientCount() {
                return -1;
            }
        };
    }

    private boolean isWholeGroup(Long referenceGroupId) {
        if (referenceGroupId == null) {
            return true;
        }
        return false;
    }
}
