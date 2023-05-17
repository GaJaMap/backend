package com.map.gaja.client.apllication;

import com.map.gaja.client.apllication.exception.UnsupportedFileTypeException;
import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.domain.model.ClientAddress;
import com.map.gaja.client.domain.model.ClientLocation;
import com.map.gaja.client.infrastructure.file.ClientFileParser;
import com.map.gaja.client.infrastructure.repository.ClientRepository;
import com.map.gaja.client.presentation.dto.request.NewClientBulkRequest;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.client.presentation.dto.response.ClientListResponse;
import com.map.gaja.client.presentation.dto.response.ClientDeleteResponse;
import com.map.gaja.client.presentation.dto.response.ClientResponse;
import com.map.gaja.client.presentation.dto.subdto.AddressDto;
import com.map.gaja.client.presentation.dto.subdto.LocationDto;
import com.map.gaja.client.presentation.exception.ClientNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.map.gaja.client.apllication.ClientConvertor.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {
    private final ClientRepository clientRepository;
    private final List<ClientFileParser> parsers;

    public ClientListResponse saveClients(NewClientBulkRequest requestClients) {
        List<Client> clients = dtoToEntity(requestClients);
        clientRepository.saveAll(clients);
        ClientListResponse response = entityToDto(clients);
        return response;
    }

    public ClientDeleteResponse deleteClient(Long clientId) {
        clientRepository.deleteById(clientId);
        return new ClientDeleteResponse(
                HttpStatus.OK.value(),
                clientId,
                "정상적으로 삭제되었습니다."
        );
    }

    public ClientListResponse parseFileAndSave(MultipartFile file) {
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

        return saveClients(clients);
    }

    public ClientResponse changeClient(Long clientId, NewClientRequest clientRequest) {
        Client clientBeChanged = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));

        // 일단 애플리케이션에 몰아넣고 나중에 도메인과 분리함.
        Client changedClient = dtoToEntity(clientRequest);
        ClientLocation changedLocation = changedClient.getLocation();
        ClientAddress changedAddress = changedClient.getAddress();

        clientBeChanged.changeName(clientRequest.getClientName());
        clientBeChanged.changePhoneNumber(clientRequest.getPhoneNumber());
        clientBeChanged.changeBundle(null); // 임시 정보
        clientBeChanged.changeLocation(changedLocation, changedAddress);

        return entityToDto(clientBeChanged);
    }
}
