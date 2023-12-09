package com.map.gaja.user.application;

import com.map.gaja.client.application.ClientConvertor;
import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.infrastructure.repository.ClientQueryRepository;
import com.map.gaja.client.infrastructure.s3.S3UrlGenerator;
import com.map.gaja.client.presentation.dto.response.ClientListResponse;
import com.map.gaja.client.presentation.dto.response.ClientOverviewResponse;
import com.map.gaja.group.infrastructure.GroupRepository;
import com.map.gaja.group.presentation.dto.response.GroupInfo;
import com.map.gaja.user.domain.model.User;
import com.map.gaja.user.infrastructure.UserRepository;
import com.map.gaja.user.presentation.dto.response.AutoLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.map.gaja.user.application.UserServiceHelper.findByEmailAndActive;

@Service
@RequiredArgsConstructor
public class AutoLoginProcessor {
    private final GroupRepository groupRepository;
    private final ClientQueryRepository clientQueryRepository;
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
        user.updateLastLoginDate();

        Long referenceGroupId = user.getReferenceGroupId();
        GroupInfo referenceGroupInfo = getReferenceGroupInfo(referenceGroupId);

        ClientListResponse recentGroupClients = getRecentGroupClients(email, referenceGroupId);

        return new AutoLoginResponse(recentGroupClients, s3UrlGenerator.getS3Url(), referenceGroupInfo);
    }

    private ClientListResponse getRecentGroupClients(String email, Long referenceGroupId) {
        if (isWholeGroup(referenceGroupId)) {
            List<ClientOverviewResponse> clientList = clientQueryRepository.findActiveClientByEmail(email, null);
            return new ClientListResponse(clientList);
        }

        List<Client> clients = clientQueryRepository.findByGroup_Id(referenceGroupId, null);
        return ClientConvertor.entityToDto(clients);
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
