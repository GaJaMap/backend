package com.map.gaja.client.apllication;

import com.map.gaja.client.presentation.dto.subdto.GroupInfoDto;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.domain.model.ClientAddress;
import com.map.gaja.client.domain.model.ClientImage;
import com.map.gaja.client.domain.model.ClientLocation;
import com.map.gaja.client.presentation.dto.request.NewClientBulkRequest;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.client.presentation.dto.request.subdto.AddressDto;
import com.map.gaja.client.presentation.dto.request.subdto.LocationDto;
import com.map.gaja.client.presentation.dto.response.ClientListResponse;
import com.map.gaja.client.presentation.dto.response.ClientResponse;
import com.map.gaja.client.presentation.dto.subdto.StoredFileDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Request -> Entity 또는 Entity -> Response 컨버터
 */
public class ClientConvertor {
    protected static ClientListResponse entityToDto(List<Client> clients) {
        List<ClientResponse> responseClients = new ArrayList<>();

        clients.forEach(client -> {
            ClientResponse clientResponse = entityToDto(client);
            responseClients.add(clientResponse);
        });

        return new ClientListResponse(responseClients);
    }

    protected static ClientResponse entityToDto(Client client) {


        return new ClientResponse(
                client.getId(),
                new GroupInfoDto(client.getGroup().getId(), client.getGroup().getName()),
                client.getName(),
                client.getPhoneNumber(),
                voToDto(client.getAddress()),
                voToDto(client.getLocation()),
                new StoredFileDto(client.getClientImage().getSavedPath(), client.getClientImage().getOriginalName()),
                null
        );
    }

    protected static List<Client> dtoToEntity(NewClientBulkRequest request) {
        List<Client> clients = new ArrayList<>();
        request.getClients().forEach(client -> clients.add(dtoToEntity(client, null)));
        return clients;
    }

    protected static Client dtoToEntity(NewClientRequest request, Group group) {
        AddressDto address = request.getAddress();
        LocationDto location = request.getLocation();
        return new Client(
                request.getClientName(),
                request.getPhoneNumber(),
                dtoToVo(address),
                dtoToVo(location),
                group
            );
    }

    protected static Client dtoToEntity(NewClientRequest request, Group group, StoredFileDto storedFileDto) {
        AddressDto address = request.getAddress();
        LocationDto location = request.getLocation();
        ClientImage clientImage = new ClientImage(storedFileDto.getOriginalFileName(), storedFileDto.getFilePath());
        return new Client(
                request.getClientName(),
                request.getPhoneNumber(),
                dtoToVo(address),
                dtoToVo(location),
                group,
                clientImage
        );
    }

    protected static ClientLocation dtoToVo(LocationDto location) {
        return new ClientLocation(location.getLatitude(), location.getLongitude());
    }

    protected static ClientAddress dtoToVo(AddressDto address) {
        return new ClientAddress(address.getProvince(), address.getCity(), address.getDistrict(), address.getDetail());
    }

    protected static LocationDto voToDto(ClientLocation location) {
        return new LocationDto(location.getLatitude(), location.getLongitude());
    }

    protected static AddressDto voToDto(ClientAddress address) {
        return new AddressDto(address.getProvince(), address.getCity(), address.getDistrict(), address.getDetail());
    }


}
