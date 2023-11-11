package com.map.gaja.client.apllication;

import com.map.gaja.client.domain.model.ClientImage;
import com.map.gaja.client.infrastructure.file.parser.dto.ClientFileDto;
import com.map.gaja.client.infrastructure.repository.ClientBulkRepository;
import com.map.gaja.client.presentation.dto.request.simple.SimpleClientBulkRequest;
import com.map.gaja.client.presentation.dto.response.ClientOverviewResponse;
import com.map.gaja.global.authentication.CurrentSecurityUserGetter;
import com.map.gaja.group.application.util.GroupServiceHelper;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.group.domain.service.IncreasingClientService;
import com.map.gaja.group.infrastructure.GroupRepository;
import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.domain.model.ClientAddress;
import com.map.gaja.client.domain.model.ClientLocation;
import com.map.gaja.client.infrastructure.repository.ClientQueryRepository;
import com.map.gaja.client.infrastructure.repository.ClientRepository;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.client.presentation.dto.subdto.StoredFileDto;
import com.map.gaja.client.domain.exception.ClientNotFoundException;
import com.map.gaja.user.domain.model.Authority;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.map.gaja.client.apllication.ClientConvertor.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {
    private final ClientRepository clientRepository;
    private final ClientBulkRepository clientBulkRepository;
    private final GroupRepository groupRepository;

    private final ClientQueryRepository clientQueryRepository;
    private final IncreasingClientService increasingClientService;
    private final CurrentSecurityUserGetter securityUserGetter;


    /**
     * 이미지 없는 고객 등록
     * @param clientRequest 고객 등록 요청 정보
     * @return 만들어진 고객 ID
     */
    public ClientOverviewResponse saveClient(NewClientRequest clientRequest) {
        Group group = GroupServiceHelper.findGroupByIdForUpdating(groupRepository, clientRequest.getGroupId());
        Client client = dtoToEntity(clientRequest, group);
        clientRepository.save(client);
        increasingClientService.increase(group, securityUserGetter.getAuthority().get(0), 1);
        return entityToOverviewDto(client);
    }

    /**
     * 이미지와 함께 고객 등록
     *
     * @param clientRequest 고객 등록 요청 정보
     * @param storedFileDto S3에 저장된 이미지 저장 정보
     * @return 만들어진 고객 ID
     */
    public ClientOverviewResponse saveClientWithImage(NewClientRequest clientRequest, StoredFileDto storedFileDto) {
        Group group = GroupServiceHelper.findGroupByIdForUpdating(groupRepository, clientRequest.getGroupId());
        Client client = dtoToEntity(clientRequest, group, storedFileDto);
        clientRepository.save(client);
        increasingClientService.increase(group, securityUserGetter.getAuthority().get(0), 1);
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

        if (isUpdatedGroup(existingClient, updateRequest)) {
            updateClientGroup(existingClient, updateRequest);
        }
        updateClientField(existingClient, updateRequest);

        return entityToOverviewDto(existingClient);
    }

    /**
     * 고객 + 고객 이미지 정보 변경
     * @param existingClientId 기존 고객 ID
     * @param updateRequest    고객 업데이트 요청 정보
     * @param updatedFileDto   고객 이미지 업데이트 정보
     */
    public ClientOverviewResponse updateClientWithNewImage(
            Long existingClientId,
            NewClientRequest updateRequest,
            StoredFileDto updatedFileDto
    ) {
        Client existingClient = clientQueryRepository.findClientWithImage(existingClientId)
                .orElseThrow(() -> new ClientNotFoundException());

        if (isUpdatedGroup(existingClient, updateRequest)) {
            updateClientGroup(existingClient, updateRequest);
        }
        updateClientField(existingClient, updateRequest);

        ClientImage clientImage = new ClientImage(updatedFileDto.getOriginalFileName(), updatedFileDto.getFilePath());
        existingClient.removeClientImage();
        existingClient.updateImage(clientImage);

        return entityToOverviewDto(existingClient);
    }

    /**
     * 기본 이미지(null)를 사용하는 고객으로 업데이트
     */
    public ClientOverviewResponse updateClientWithBasicImage(Long existingClientId, NewClientRequest updateRequest) {
        // 고객 + 고객 이미지를 조인해서 가져오기
        Client existingClient = clientQueryRepository.findClientWithImage(existingClientId)
                .orElseThrow(() -> new ClientNotFoundException());

        if (isUpdatedGroup(existingClient, updateRequest)) {
            updateClientGroup(existingClient, updateRequest);
        }
        updateClientField(existingClient, updateRequest);
        existingClient.removeClientImage();

        return entityToOverviewDto(existingClient);
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
        increasingClientService.increase(updatedGroup, securityUserGetter.getAuthority().get(0), 1);
    }

    /**
     * ClientImage, Group을 제외한 순수 고객 정보만을 업데이트 한다.
     */
    private void updateClientField(Client existingClient, NewClientRequest updateRequest) {
        ClientAddress updatedAddress = dtoToVo(updateRequest.getAddress());
        ClientLocation updatedLocation = dtoToVo(updateRequest.getLocation());

        existingClient.updateClientField(
                updateRequest.getClientName(),
                updateRequest.getPhoneNumber(),
                updatedAddress,
                updatedLocation
        );
    }

    /**
     * Request 정보에 Group이 기존 Group에서 변경됐니?
     */
    private static boolean isUpdatedGroup(Client existingClient, NewClientRequest updateRequest) {
        return existingClient.getGroup().getId() != updateRequest.getGroupId();
    }

    public List<Long> saveSimpleClientList(SimpleClientBulkRequest bulkRequest) {
        Group group = GroupServiceHelper.findGroupByIdForUpdating(groupRepository, bulkRequest.getGroupId());

        List<Client> savedClient = new ArrayList<>();
        bulkRequest.getClients().forEach((clientRequest) -> {
            savedClient.add(dtoToEntity(clientRequest, group));
        });

        increasingClientService.increase(group, securityUserGetter.getAuthority().get(0), savedClient.size());
        clientRepository.saveAll(savedClient);
        return savedClient.stream().mapToLong(Client::getId).boxed()
                .collect(Collectors.toList());

        /*
         * 모바일의 빠른 진행을 위해 ID를 반환하도록 수정
         * 만약 모바일에서 새로고침 기능이 완성된다면 해당 코드를 사용하고
         * 반환타입을 void로 수정할 것
         */
        // clientBulkRepository.saveClientWithGroup(group, savedClient);
    }

    /**
     * 파싱한 엑셀 데이터 저장.
     * JPA와 관계없이 저장하기 때문에 ID를 반환하지 않음.
     * 비동기로 처리한 정보를 받기 때문에 securityUserGetter.getAuthority()로 세션 정보 가져오기 불가능
     */
    public void saveClientExcelData(Long groupId, List<ClientFileDto> excelData, List<Authority> authority) {
        Group group = GroupServiceHelper.findGroupByIdForUpdating(groupRepository, groupId);

        List<Client> savedClient = new ArrayList<>();
        excelData.forEach((clientData) -> savedClient.add(dtoToEntity(clientData, group)));

        increasingClientService.increase(group, authority.get(0), savedClient.size());
        clientBulkRepository.saveClientWithGroup(group, savedClient);
    }

    /**
     * 그룹 내의 특정 고객들을 제거
     */
    public void deleteBulkClient(Long groupId, List<Long> clientIds) {
        clientRepository.markClientImageAsDeleted(clientIds);
        clientRepository.deleteClientsInClientIds(clientIds);

        Group group = GroupServiceHelper.findGroupByIdForUpdating(groupRepository, groupId);
        group.decreaseClientCount(clientIds.size());
    }
}
