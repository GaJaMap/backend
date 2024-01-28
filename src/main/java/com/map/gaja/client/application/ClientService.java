package com.map.gaja.client.application;

import com.map.gaja.client.domain.model.ClientImage;
import com.map.gaja.client.infrastructure.file.parser.dto.ParsedClientDto;
import com.map.gaja.client.infrastructure.repository.ClientBulkRepository;
import com.map.gaja.client.presentation.dto.request.simple.SimpleClientBulkRequest;
import com.map.gaja.client.presentation.dto.response.ClientOverviewResponse;
import com.map.gaja.global.authentication.AuthenticationRepository;
import com.map.gaja.group.application.util.GroupServiceHelper;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.group.domain.service.IncreasingClientService;
import com.map.gaja.group.infrastructure.GroupRepository;
import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.infrastructure.repository.ClientQueryRepository;
import com.map.gaja.client.infrastructure.repository.ClientRepository;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.client.presentation.dto.subdto.StoredFileDto;
import com.map.gaja.client.domain.exception.ClientNotFoundException;
import com.map.gaja.user.application.UserServiceHelper;
import com.map.gaja.user.domain.model.Authority;
import com.map.gaja.user.domain.model.User;
import com.map.gaja.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.map.gaja.client.application.ClientConvertor.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {
    private final ClientRepository clientRepository;
    private final GroupRepository groupRepository;

    private final ClientQueryRepository clientQueryRepository;
    private final IncreasingClientService increasingClientService;
    private final AuthenticationRepository securityUserGetter;
    private final UserRepository userRepository;


    /**
     * 이미지 없는 고객 등록
     * @param clientRequest 고객 등록 요청 정보
     * @return 만들어진 고객 ID
     */
    public ClientOverviewResponse saveClient(NewClientRequest clientRequest, String loginEmail) {
        User user = userRepository.findByEmail(loginEmail);
        Group group = GroupServiceHelper.findGroupByIdForUpdating(groupRepository, clientRequest.getGroupId());
        Client client = dtoToEntity(clientRequest, group, user);
        clientRepository.save(client);
        increasingClientService.increaseByOne(group, securityUserGetter.getAuthority().get(0));
        return entityToOverviewDto(client);
    }

    /**
     * 이미지와 함께 고객 등록
     *
     * @param clientRequest 고객 등록 요청 정보
     * @param loginEmail 요청을 보내는 사용자 이메일
     * @return 만들어진 고객
     */
    public ClientOverviewResponse saveClientWithImage(NewClientRequest clientRequest, String loginEmail) {
        User user = userRepository.findByEmail(loginEmail);
        Group group = GroupServiceHelper.findGroupByIdForUpdating(groupRepository, clientRequest.getGroupId());
        ClientImage clientImage = ClientImage.create(loginEmail, clientRequest.getClientImage());
        Client client = dtoToEntity(clientRequest, group, user, clientImage);
        client.updateImage(clientImage);

        clientRepository.save(client);
        increasingClientService.increaseByOne(group, securityUserGetter.getAuthority().get(0));
        return entityToOverviewDto(client);
    }

    public void deleteClient(long clientId) {
        Client deletedClient = clientRepository.findClientWithGroupForUpdate(clientId)
                .orElseThrow(() -> new ClientNotFoundException());
        Group group = deletedClient.getGroup();

        group.decreaseClientCount(1);
        deletedClient.removeClientImage();

        clientRepository.delete(deletedClient);
    }

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
        existingClient.removeClientImage();
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
        existingClient.removeClientImage();

        return entityToOverviewDto(existingClient);
    }

    private void updateClientGroupIfChanged(NewClientRequest updateRequest, Client existingClient) {
        if (isUpdatedGroup(existingClient, updateRequest)) {
            updateClientGroup(existingClient, updateRequest);
        }
    }

    /**
     * 두 개의 그룹을 조회해서 Client의 기존 그룹을 새로운 그룹으로 변경
     * 기존 그룹 ClientCount -1
     * 새 그룹 ClientCount +1
     * 기존 그룹과, 새 그룹 각각의 Lock이 필요하다.
     */
    private void updateClientGroup(Client existingClient, NewClientRequest updateRequest) {
        Group existingGroup = GroupServiceHelper.findGroupByIdForUpdating(groupRepository, existingClient.getGroup().getId());
        Group updatedGroup = GroupServiceHelper.findGroupByIdForUpdating(groupRepository, updateRequest.getGroupId());

        existingGroup.decreaseClientCount(1);
        existingClient.updateGroup(updatedGroup);
        increasingClientService.increaseByOne(updatedGroup, securityUserGetter.getAuthority().get(0));
    }

    /**
     * Request 정보에 Group이 기존 Group에서 변경됐니?
     */
    private static boolean isUpdatedGroup(Client existingClient, NewClientRequest updateRequest) {
        return existingClient.getGroup().getId() != updateRequest.getGroupId();
    }
}
