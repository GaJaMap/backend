package com.map.gaja.client.application;

import com.map.gaja.client.domain.model.ClientImage;
import com.map.gaja.client.presentation.dto.response.ClientOverviewResponse;
import com.map.gaja.global.authentication.AuthenticationRepository;
import com.map.gaja.group.domain.exception.GroupNotFoundException;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.group.infrastructure.GroupRepository;
import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.infrastructure.repository.ClientQueryRepository;
import com.map.gaja.client.infrastructure.repository.ClientRepository;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.client.domain.exception.ClientNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.map.gaja.client.application.ClientConvertor.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientUpdatingService {
    private final ClientRepository clientRepository;
    private final GroupRepository groupRepository;
    private final ClientQueryRepository clientQueryRepository;
    private final AuthenticationRepository securityUserGetter;

    /**
     * 이미지 정보를 제외한 고객 정보 변경
     *
     * @param existingClientId 기존 고객 ID
     * @param updateRequest    고객 업데이트 요청 정보
     */
    public ClientOverviewResponse updateClientWithoutImage(
            Long existingClientId,
            NewClientRequest updateRequest
    ) {
        // 이미지를 안쓴다면 findById로 충분하다.
        Client existingClient = clientRepository.findById(existingClientId)
                .orElseThrow(() -> new ClientNotFoundException());

        updateClientGroupIfChanged(updateRequest, existingClient);
        ClientUpdater.updateClient(existingClient, updateRequest);

        return entityToOverviewDto(existingClient);
    }

    /**
     * 고객 + 고객 이미지 정보 변경
     * @param existingClientId 기존 고객 ID
     * @param updateRequest    고객 업데이트 요청 정보
     * @param loginEmail   요청 사용자 이메일
     */
    public ClientOverviewResponse updateClientWithNewImage(
            Long existingClientId,
            NewClientRequest updateRequest,
            String loginEmail
    ) {
        Client existingClient = clientQueryRepository.findClientWithImage(existingClientId)
                .orElseThrow(() -> new ClientNotFoundException());
        ClientImage newImage = ClientImage.create(loginEmail, updateRequest.getClientImage());

        updateClientGroupIfChanged(updateRequest, existingClient);
        ClientUpdater.updateClient(existingClient, updateRequest);
        existingClient.updateImage(newImage);

        return entityToOverviewDto(existingClient);
    }

    /**
     * 기본 이미지(null)를 사용하는 고객으로 업데이트
     */
    public ClientOverviewResponse updateClientWithBasicImage(Long existingClientId, NewClientRequest updateRequest) {
        // 고객 + 고객 이미지를 조인해서 가져오기
        Client existingClient = clientQueryRepository.findClientWithImage(existingClientId)
                .orElseThrow(() -> new ClientNotFoundException());

        updateClientGroupIfChanged(updateRequest, existingClient);
        ClientUpdater.updateClient(existingClient, updateRequest);
        existingClient.removeImage();

        return entityToOverviewDto(existingClient);
    }

    private void updateClientGroupIfChanged(NewClientRequest updateRequest, Client existingClient) {
        if (isUpdatedGroup(existingClient, updateRequest)) {
            Group changedGroup = groupRepository.findByIdAndUserEmail(updateRequest.getGroupId(), securityUserGetter.getEmail())
                    .orElseThrow(GroupNotFoundException::new);
            existingClient.updateGroup(changedGroup);
        }
    }

    /**
     * Request 정보에 Group이 기존 Group에서 변경됐니?
     */
    private static boolean isUpdatedGroup(Client existingClient, NewClientRequest updateRequest) {
        return existingClient.getGroup().getId() != updateRequest.getGroupId();
    }
}
