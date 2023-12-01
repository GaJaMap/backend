package com.map.gaja.client.apllication;

import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.domain.model.ClientAddress;
import com.map.gaja.client.domain.model.ClientImage;
import com.map.gaja.client.domain.model.ClientLocation;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.client.presentation.dto.subdto.StoredFileDto;

import static com.map.gaja.client.apllication.ClientConvertor.dtoToVo;

public class ClientUpdater {
    /**
     * ClientImage, Group을 제외한 순수 고객 정보만을 업데이트 한다.
     */
    protected static void updateClient(Client existingClient, NewClientRequest updateRequest) {
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
     * ClientImage 업데이트
     */
    protected static void updateClientImage(Client existingClient, StoredFileDto updatedFileDto) {
        ClientImage clientImage = new ClientImage(updatedFileDto.getOriginalFileName(), updatedFileDto.getFilePath());
        existingClient.removeClientImage();
        existingClient.updateImage(clientImage);
    }
}
