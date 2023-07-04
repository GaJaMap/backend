package com.map.gaja.client.apllication;

import com.map.gaja.group.domain.exception.GroupNotFoundException;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.group.infrastructure.GroupRepository;
import com.map.gaja.client.apllication.exception.UnsupportedFileTypeException;
import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.domain.model.ClientAddress;
import com.map.gaja.client.domain.model.ClientLocation;
import com.map.gaja.client.infrastructure.file.ClientFileParser;
import com.map.gaja.client.infrastructure.repository.ClientQueryRepository;
import com.map.gaja.client.infrastructure.repository.ClientRepository;
import com.map.gaja.client.presentation.dto.request.NewClientBulkRequest;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.client.presentation.dto.response.*;
import com.map.gaja.client.presentation.dto.subdto.StoredFileDto;
import com.map.gaja.client.domain.exception.ClientNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import static com.map.gaja.client.apllication.ClientConvertor.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {
    private final ClientRepository clientRepository;
    private final GroupRepository groupRepository;
    private final ClientQueryRepository clientQueryRepository;
    private final List<ClientFileParser> parsers;

    public CreatedClientResponse saveClient(NewClientRequest clientRequest) {
        Group group = groupRepository.findById(clientRequest.getGroupId())
                .orElseThrow(() -> new GroupNotFoundException());
        Client client = dtoToEntity(clientRequest, group);
        clientRepository.save(client);
        return new CreatedClientResponse(client.getId());
    }

    public CreatedClientResponse saveClient(NewClientRequest clientRequest, StoredFileDto storedFileDto) {
        Group group = groupRepository.findById(clientRequest.getGroupId())
                .orElseThrow(() -> new GroupNotFoundException());
        Client client = dtoToEntity(clientRequest, group, storedFileDto);
        clientRepository.save(client);
        return new CreatedClientResponse(client.getId());
    }

    public CreatedClientListResponse saveClientList(NewClientBulkRequest clientsRequest) {
        List<Client> clients = dtoToEntity(clientsRequest);
        clientRepository.saveAll(clients);

        List<CreatedClientResponse> clientIdList = clients.stream().map(client -> client.getId())
                .map(CreatedClientResponse::new).collect(Collectors.toList());
        CreatedClientListResponse response = new CreatedClientListResponse(clientIdList);
        return response;
    }

    public void deleteClient(long clientId) {
        Client deletedClient = clientQueryRepository.findClientWithGroup(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));
        deletedClient.removeGroup();

        clientRepository.delete(deletedClient);
    }

    public CreatedClientListResponse parseFileAndSave(MultipartFile file) {
        NewClientBulkRequest clients = null;
        for (ClientFileParser parser : parsers) {
            if(parser.isSupported(file)) {
                clients = parser.parse(file);
                break;
            }
        }

        if (clients == null) {
            String oriName = file.getOriginalFilename();
            String fileType = oriName.substring(oriName.lastIndexOf(".")+1);
            throw new UnsupportedFileTypeException(fileType); // 지원하지 않는 파일형식
        }

        return saveClientList(clients);
    }

    public ClientResponse changeClient(
            Long existingClientId,
            NewClientRequest updateRequest
    ) {
        Client existingClient = clientQueryRepository.findClientWithGroup(existingClientId)
                .orElseThrow(() -> new ClientNotFoundException(existingClientId));

        // 일단 애플리케이션에 몰아넣고 나중에 도메인과 분리함.
        Group updatedGroup = groupRepository.findById(updateRequest.getGroupId())
                .orElseThrow(GroupNotFoundException::new);

        ClientAddress updatedAddress = new ClientAddress(
                updateRequest.getAddress().getProvince(),
                updateRequest.getAddress().getCity(),
                updateRequest.getAddress().getDistrict(),
                updateRequest.getAddress().getDetail()
        );

        ClientLocation updatedLocation = new ClientLocation(
                updateRequest.getLocation().getLatitude(),
                updateRequest.getLocation().getLongitude()
        );

        existingClient.updateClient(
                updateRequest.getClientName(),
                updateRequest.getPhoneNumber(),
                updatedAddress,
                updatedLocation,
                updatedGroup
        );

        return entityToDto(existingClient);
    }
}
