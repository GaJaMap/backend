package com.map.gaja.user.application;

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

import java.time.LocalDateTime;
import java.util.List;

import static com.map.gaja.user.application.UserServiceHelper.findById;
import static com.map.gaja.user.constant.UserConstant.WHOLE_GROUP_INFO;

@Service
@RequiredArgsConstructor
public class AutoLoginProcessor {
    private final GroupRepository groupRepository;
    private final ClientQueryRepository clientQueryRepository;
    private final UserRepository userRepository;
    private final S3UrlGenerator s3UrlGenerator;

    @Transactional(readOnly = true)
    public AutoLoginResponse process(Long userId) {
        User user = findById(userRepository, userId);

        user.updateLastLoginDateIfDifferent(LocalDateTime.now());

        GroupInfo referenceGroupInfo = getReferenceGroupInfo(user.getReferenceGroupId());

        ClientListResponse recentGroupClients = getReferenceClients(user);

        return new AutoLoginResponse(recentGroupClients, s3UrlGenerator.getS3Url(), referenceGroupInfo);
    }

    private ClientListResponse getReferenceClients(User user) {
        if (isWholeGroup(user.getReferenceGroupId())) {
            List<ClientOverviewResponse> clientList = clientQueryRepository.findWholeGroupClients(user.getId(), user.getAuthority().getClientLimitCount());
            return new ClientListResponse(clientList);
        }

        List<ClientOverviewResponse> clientList = clientQueryRepository.findRecentGroupClients(user.getReferenceGroupId());
        return new ClientListResponse(clientList);

    }

    private GroupInfo getReferenceGroupInfo(Long referenceGroupId) {
        GroupInfo groupInfo = groupRepository.findGroupInfoById(referenceGroupId)
                .orElse(WHOLE_GROUP_INFO);
        return groupInfo;
    }

    private boolean isWholeGroup(Long referenceGroupId) {
        if (referenceGroupId == null) {
            return true;
        }
        return false;
    }
}
