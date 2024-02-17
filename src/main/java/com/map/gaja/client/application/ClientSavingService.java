package com.map.gaja.client.application;

import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.domain.model.ClientImage;
import com.map.gaja.client.infrastructure.repository.ClientQueryRepository;
import com.map.gaja.client.infrastructure.repository.ClientRepository;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.client.presentation.dto.response.ClientOverviewResponse;
import com.map.gaja.global.authentication.AuthenticationRepository;
import com.map.gaja.group.application.util.GroupServiceHelper;
import com.map.gaja.group.domain.exception.GroupNotFoundException;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.group.domain.service.IncreasingClientService;
import com.map.gaja.group.infrastructure.GroupRepository;
import com.map.gaja.user.domain.model.User;
import com.map.gaja.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.map.gaja.client.application.ClientConvertor.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ClientSavingService {
    private final ClientRepository clientRepository;
    private final GroupRepository groupRepository;
    private final AuthenticationRepository securityUserGetter;
    private final UserRepository userRepository;

    /**
     * 이미지 없는 고객 등록
     * @param clientRequest 고객 등록 요청 정보
     * @return 만들어진 고객 ID
     */
    public ClientOverviewResponse saveClient(NewClientRequest clientRequest, String loginEmail) {
        User user = userRepository.findByEmail(loginEmail);
        Group group = groupRepository.findByIdAndUserEmail(clientRequest.getGroupId(), loginEmail)
                .orElseThrow(GroupNotFoundException::new);
        Client client = dtoToEntity(clientRequest, group, user);
        clientRepository.save(client);
        return entityToOverviewDto(client);
    }

    /**
     * 이미지와 함께 고객 등록
     *
     * @param clientRequest 고객 등록 요청 정보
     * @param loginEmail 요청을 보내는 사용자 이메일
     * @return 만들어진 고객
     */
    public ClientOverviewResponse saveClientWithImage(NewClientRequest clientRequest, String loginEmail) {
        User user = userRepository.findByEmail(loginEmail);
        Group group = groupRepository.findByIdAndUserEmail(clientRequest.getGroupId(), loginEmail)
                .orElseThrow(GroupNotFoundException::new);
        ClientImage clientImage = ClientImage.create(loginEmail, clientRequest.getClientImage());
        Client client = dtoToEntity(clientRequest, group, user, clientImage);

        clientRepository.save(client);
        return entityToOverviewDto(client);
    }
}
