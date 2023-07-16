package com.map.gaja.client.apllication;

import com.map.gaja.client.infrastructure.file.excel.ClientExcelData;
import com.map.gaja.client.infrastructure.s3.S3UrlGenerator;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Request -> Entity 또는 Entity -> Response 컨버터
 */
public class ClientConvertor {
    protected static ClientListResponse entityToDto(List<Client> clients) {
        List<ClientOverviewResponse> responseClients = new ArrayList<>();

        clients.forEach(client -> {
            ClientOverviewResponse clientResponse = entityToOverviewDto(client);
            responseClients.add(clientResponse);
        });

        return new ClientListResponse(responseClients);
    }

    protected static ClientDetailResponse entityToDetailDto(Client client, S3UrlGenerator s3UrlGenerator) {
        StoredFileDto image = getStoredFileUrlDto(client.getClientImage(), s3UrlGenerator);
        return new ClientDetailResponse(
                client.getId(),
                new GroupInfoDto(client.getGroup().getId(), client.getGroup().getName()),
                client.getName(),
                client.getPhoneNumber(),
                voToDto(client.getAddress()),
                voToDto(client.getLocation()),
                image,
                null
        );
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

    protected static Client dtoToEntity(SimpleNewClientRequest request, Group group) {
        return new Client(
                request.getClientName(),
                request.getPhoneNumber(),
                group
        );
    }

    protected static Client dtoToEntity(ClientExcelData clientData, Group group) {
        ClientAddress address = new ClientAddress(null, clientData.getAddress(), null, clientData.getAddressDetail()); // 임시
        ClientLocation location = dtoToVo(clientData.getLocation());
        return new Client(
                clientData.getName(),
                clientData.getPhoneNumber(),
                address,
                location,
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
        return (location == null) ? new LocationDto() :
                new LocationDto(location.getLatitude(), location.getLongitude());
    }

    protected static AddressDto voToDto(ClientAddress address) {
        return (address == null) ? new AddressDto() :
                new AddressDto(address.getProvince(), address.getCity(), address.getDistrict(), address.getDetail());
    }


    private static StoredFileDto getStoredFileUrlDto(ClientImage clientImage, S3UrlGenerator s3UrlGenerator) {
        if (clientImage == null) {
            return new StoredFileDto();
        }
        else {
            String imageUrl = s3UrlGenerator.getS3Url() + clientImage.getSavedPath();
            return new StoredFileDto(imageUrl, clientImage.getOriginalName());
        }
    }

    private static StoredFileDto getStoredFileDto(ClientImage clientImage) {
        return (clientImage == null) ? new StoredFileDto() : new StoredFileDto(clientImage.getSavedPath(), clientImage.getOriginalName());
    }

}
