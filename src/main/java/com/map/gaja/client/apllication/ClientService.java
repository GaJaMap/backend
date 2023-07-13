package com.map.gaja.client.apllication;

import com.map.gaja.client.presentation.dto.request.simple.SimpleClientBulkRequest;
import com.map.gaja.group.domain.exception.GroupNotFoundException;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.group.infrastructure.GroupRepository;
import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.domain.model.ClientAddress;
import com.map.gaja.client.domain.model.ClientLocation;
import com.map.gaja.client.infrastructure.repository.ClientQueryRepository;
import com.map.gaja.client.infrastructure.repository.ClientRepository;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.client.presentation.dto.subdto.StoredFileDto;
import com.map.gaja.client.domain.exception.ClientNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.map.gaja.client.apllication.ClientConvertor.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {
    private final ClientRepository clientRepository;
    private final GroupRepository groupRepository;
    private final ClientQueryRepository clientQueryRepository;

    /**
     * 이미지 없는 고객 등록
     * @param clientRequest 고객 등록 요청 정보
     * @return 만들어진 고객 ID
     */
    public Long saveClient(NewClientRequest clientRequest) {
        Group group = groupRepository.findById(clientRequest.getGroupId())
                .orElseThrow(() -> new GroupNotFoundException());
        Client client = dtoToEntity(clientRequest, group);
        clientRepository.save(client);
        return client.getId();
    }

    /**
     * 이미지와 함께 고객 등록
     * @param clientRequest 고객 등록 요청 정보
     * @param storedFileDto S3에 저장된 이미지 저장 정보
     * @return 만들어진 고객 ID
     */
    public Long saveClientWithImage(NewClientRequest clientRequest, StoredFileDto storedFileDto) {
        Group group = groupRepository.findById(clientRequest.getGroupId())
                .orElseThrow(() -> new GroupNotFoundException());
        Client client = dtoToEntity(clientRequest, group, storedFileDto);
        clientRepository.save(client);
        return client.getId();
    }

    public void deleteClient(long clientId) {
        Client deletedClient = clientQueryRepository.findClientWithGroup(clientId)
                .orElseThrow(() -> new ClientNotFoundException());
        deletedClient.removeGroup();

        clientRepository.delete(deletedClient);
    }

    /**
     * 고객 정보 변경
     * @param existingClientId 기존 고객 ID
     * @param updateRequest 고객 업데이트 요청 정보
     */
    public void changeClient(
            Long existingClientId,
            NewClientRequest updateRequest
    ) {
        Client existingClient = clientQueryRepository.findClientWithGroup(existingClientId)
                .orElseThrow(() -> new ClientNotFoundException());

        Group updatedGroup = getUpdatedGroup(updateRequest, existingClient);
        ClientAddress updatedAddress = dtoToVo(updateRequest.getAddress());
        ClientLocation updatedLocation = dtoToVo(updateRequest.getLocation());

        existingClient.updateClient(
                updateRequest.getClientName(),
                updateRequest.getPhoneNumber(),
                updatedAddress,
                updatedLocation,
                updatedGroup
        );
    }

    /**
     * 고객 + 고객 이미지 정보 변경
     * @param existingClientId 기존 고객 ID
     * @param updateRequest 고객 업데이트 요청 정보
     * @param updatedFileDto 고객 이미지 업데이트 정보
     */
    public void changeClientWithImage(
            Long existingClientId,
            NewClientRequest updateRequest,
            StoredFileDto updatedFileDto
    ) {
        Client existingClient = clientQueryRepository.findClientWithGroup(existingClientId)
                .orElseThrow(() -> new ClientNotFoundException());

        Group updatedGroup = getUpdatedGroup(updateRequest, existingClient);
        ClientAddress updatedAddress = dtoToVo(updateRequest.getAddress());
        ClientLocation updatedLocation = dtoToVo(updateRequest.getLocation());

        existingClient.updateClient(
                updateRequest.getClientName(),
                updateRequest.getPhoneNumber(),
                updatedAddress,
                updatedLocation,
                updatedGroup
        );

        existingClient.getClientImage()
                .updateImage(updatedFileDto.getOriginalFileName(), updatedFileDto.getFilePath());
    }

    private static boolean isUpdatedGroup(NewClientRequest updateRequest, Client existingClient) {
        return existingClient.getGroup().getId() != updateRequest.getGroupId();
    }

    private Group getUpdatedGroup(NewClientRequest updateRequest, Client existingClient) {
        if (isUpdatedGroup(updateRequest, existingClient)) {
            return groupRepository.findById(updateRequest.getGroupId())
                    .orElseThrow(GroupNotFoundException::new);
        } else {
            return existingClient.getGroup();
        }
    }

    public List<Long> saveSimpleClientList(SimpleClientBulkRequest bulkRequest) {
        Group group = groupRepository.findById(bulkRequest.getGroupId())
                .orElseThrow(GroupNotFoundException::new);

        List<Long> savedIdList = new ArrayList<>();
        bulkRequest.getClients().forEach((clientRequest) -> {
            Client client = dtoToEntity(clientRequest, group);
            clientRepository.save(client);
            savedIdList.add(client.getId());
        });

        return savedIdList;
    }
}
