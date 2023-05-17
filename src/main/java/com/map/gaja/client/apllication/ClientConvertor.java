package com.map.gaja.client.apllication;

import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.domain.model.ClientAddress;
import com.map.gaja.client.domain.model.ClientLocation;
import com.map.gaja.client.presentation.dto.request.NewClientBulkRequest;
import com.map.gaja.client.presentation.dto.request.subdto.AddressDto;
import com.map.gaja.client.presentation.dto.request.subdto.LocationDto;
import com.map.gaja.client.presentation.dto.response.ClientListResponse;
import com.map.gaja.client.presentation.dto.response.ClientResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Request -> Entity 또는 Entity -> Response 컨버터
 */
public class ClientConvertor {
    protected static ClientListResponse entityToDto(List<Client> clients) {
        List<ClientResponse> responseClients = new ArrayList<>();

        clients.forEach(client -> {
            ClientResponse clientResponse = new ClientResponse();
            clientResponse.setClientId(client.getId());
            clientResponse.setName(client.getName());
            responseClients.add(clientResponse);
        });

        return new ClientListResponse(responseClients);
    }

    protected static List<Client> dtoToEntity(NewClientBulkRequest request) {
        List<Client> clients = new ArrayList<>();
        request.getClients().forEach(client ->
                {
                    AddressDto address = client.getAddress();
                    LocationDto location = client.getLocation();
                    clients.add(
                            new Client(
                                client.getClientName(),
                                client.getPhoneNumber(),
                                new ClientAddress(address.getProvince(), address.getCity(), address.getDistrict(), address.getDetail()),
                                new ClientLocation(location.getLatitude(), location.getLongitude()),
                                null // 임시
                            )
                        );
                }
        );
        return clients;
    }

    protected static ClientResponse entityToDto(Client client) {
        return new ClientResponse(client.getId(), client.getName());
    }
}
