package com.map.gaja.client.application;

import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.domain.model.ClientAddress;
import com.map.gaja.client.domain.model.ClientLocation;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;

import static com.map.gaja.client.application.ClientConvertor.dtoToVo;

public class ClientUpdater {
    /**
     * ClientImage, Group을 제외한 순수 고객 정보만을 업데이트 한다.
     */
    protected static void updateClient(Client existingClient, NewClientRequest updateRequest) {
        ClientAddress updatedAddress = dtoToVo(updateRequest.getAddress());
        ClientLocation updatedLocation = dtoToVo(updateRequest.getLocation());

        existingClient.updateWithoutClientImage(
                updateRequest.getClientName(),
                updateRequest.getPhoneNumber(),
                updatedAddress,
                updatedLocation
        );
    }
}
