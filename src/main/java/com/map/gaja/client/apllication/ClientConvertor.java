package com.map.gaja.client.apllication;

import com.map.gaja.bundle.domain.model.Bundle;
import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.domain.model.ClientAddress;
import com.map.gaja.client.domain.model.ClientImage;
import com.map.gaja.client.domain.model.ClientLocation;
import com.map.gaja.client.presentation.dto.request.NewClientBulkRequest;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.client.presentation.dto.subdto.AddressDto;
import com.map.gaja.client.presentation.dto.subdto.LocationDto;
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
        ClientAddress address = client.getAddress();
        ClientLocation location = client.getLocation();
        return new ClientResponse(client.getId(), client.getBundle().getId(), client.getName(), client.getPhoneNumber(),
                new AddressDto(address.getProvince(), address.getCity(), address.getDistrict(), address.getDetail()),
                new LocationDto(location.getLatitude(), location.getLongitude()),
                null
        );
    }

    protected static List<Client> dtoToEntity(NewClientBulkRequest request) {
        List<Client> clients = new ArrayList<>();
        request.getClients().forEach(client -> clients.add(dtoToEntity(client, null)));
        return clients;
    }

    protected static Client dtoToEntity(NewClientRequest request, Bundle bundle) {
        AddressDto address = request.getAddress();
        LocationDto location = request.getLocation();
        return new Client(
                request.getClientName(),
                request.getPhoneNumber(),
                new ClientAddress(address.getProvince(), address.getCity(), address.getDistrict(), address.getDetail()),
                new ClientLocation(location.getLatitude(), location.getLongitude()),
                bundle
            );
    }

    protected static Client dtoToEntity(NewClientRequest request, Bundle bundle, StoredFileDto storedFileDto) {
        AddressDto address = request.getAddress();
        LocationDto location = request.getLocation();
        ClientImage clientImage = new ClientImage(storedFileDto.getOriginalFileName(), storedFileDto.getFilePath());
        return new Client(
                request.getClientName(),
                request.getPhoneNumber(),
                new ClientAddress(address.getProvince(), address.getCity(), address.getDistrict(), address.getDetail()),
                new ClientLocation(location.getLatitude(), location.getLongitude()),
                bundle,
                clientImage
        );
    }
}
