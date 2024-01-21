package com.map.gaja.client.application;

import com.map.gaja.client.infrastructure.file.parser.dto.ParsedClientDto;
import com.map.gaja.client.presentation.dto.request.simple.SimpleNewClientRequest;
import com.map.gaja.client.presentation.dto.response.ClientDetailResponse;
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
import com.map.gaja.client.presentation.dto.response.ClientOverviewResponse;
import com.map.gaja.client.presentation.dto.subdto.StoredFileDto;
import com.map.gaja.user.domain.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Request -> Entity 또는 Entity -> Response 컨버터
 */
public class ClientConvertor {
    public static ClientListResponse entityToDto(List<Client> clients) {
        List<ClientOverviewResponse> responseClients = new ArrayList<>();

        clients.forEach(client -> {
            ClientOverviewResponse clientResponse = entityToOverviewDto(client);
            responseClients.add(clientResponse);
        });

        return new ClientListResponse(responseClients);
    }

    protected static ClientOverviewResponse entityToOverviewDto(Client client) {
        return new ClientOverviewResponse(
                client.getId(),
                new GroupInfoDto(client.getGroup().getId(), client.getGroup().getName()),
                client.getName(),
                client.getPhoneNumber(),
                voToDto(client.getAddress()),
                voToDto(client.getLocation()),
                getStoredFileDto(client.getClientImage()),
                client.getCreatedAt()
        );
    }


    protected static ClientDetailResponse entityToDetailDto(Client client) {
        StoredFileDto image = getStoredFileUrlDto(client.getClientImage());
        return new ClientDetailResponse(
                client.getId(),
                new GroupInfoDto(client.getGroup().getId(), client.getGroup().getName()),
                client.getName(),
                client.getPhoneNumber(),
                voToDto(client.getAddress()),
                voToDto(client.getLocation()),
                image,
                null,
                client.getCreatedAt()
        );
    }

    protected static List<Client> dtoToEntity(NewClientBulkRequest request) {
        List<Client> clients = new ArrayList<>();
        request.getClients().forEach(client -> clients.add(dtoToEntity(client, null, null)));
        return clients;
    }

    protected static Client dtoToEntity(NewClientRequest request, Group group, User user) {
        AddressDto address = request.getAddress();
        LocationDto location = request.getLocation();
        return Client.createWithLocation(
                request.getClientName(),
                request.getPhoneNumber(),
                dtoToVo(address),
                dtoToVo(location),
                group,
                user
            );
    }

    protected static Client dtoToEntity(SimpleNewClientRequest request, Group group, User user) {
        return Client.create(
                request.getClientName(),
                request.getPhoneNumber(),
                group,
                user
        );
    }

    protected static Client dtoToEntity(ParsedClientDto clientData, Group group, User user) {
        ClientAddress address = new ClientAddress(clientData.getAddress(), clientData.getAddressDetail());
        ClientLocation location = dtoToVo(clientData.getLocation());
        return Client.createWithLocation(
                clientData.getName(),
                clientData.getPhoneNumber(),
                address,
                location,
                group,
                user
        );
    }

    protected static ClientLocation dtoToVo(LocationDto location) {
        return (location == null || location.getLongitude() == null || location.getLatitude() == null) ? new ClientLocation() :
                new ClientLocation(location.getLatitude(), location.getLongitude());
    }

    protected static ClientAddress dtoToVo(AddressDto address) {
        return (address == null) ? new ClientAddress() :
                new ClientAddress(address.getMainAddress(), address.getDetail());
    }

    protected static LocationDto voToDto(ClientLocation location) {
        return (location == null || location.getLocation() == null) ? new LocationDto() :
                new LocationDto(location.getLocation().getY(), location.getLocation().getX());
    }

    protected static AddressDto voToDto(ClientAddress address) {
        return (address == null) ? new AddressDto() :
                new AddressDto(address.getAddress(), address.getDetail());
    }


    private static StoredFileDto getStoredFileUrlDto(ClientImage clientImage) {
        if (clientImage == null) {
            return new StoredFileDto();
        }
        else {
            return new StoredFileDto(clientImage.getSavedPath(), clientImage.getOriginalName());
        }
    }

    private static StoredFileDto getStoredFileDto(ClientImage clientImage) {
        return (clientImage == null) ? new StoredFileDto() : new StoredFileDto(clientImage.getSavedPath(), clientImage.getOriginalName());
    }

}
