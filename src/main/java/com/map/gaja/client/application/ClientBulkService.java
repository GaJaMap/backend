package com.map.gaja.client.application;

import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.infrastructure.file.parser.dto.ParsedClientDto;
import com.map.gaja.client.infrastructure.repository.ClientBulkRepository;
import com.map.gaja.client.infrastructure.repository.ClientRepository;
import com.map.gaja.client.presentation.dto.request.simple.SimpleClientBulkRequest;
import com.map.gaja.global.authentication.AuthenticationRepository;
import com.map.gaja.group.application.util.GroupServiceHelper;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.group.domain.service.IncreasingClientService;
import com.map.gaja.group.infrastructure.GroupRepository;
import com.map.gaja.user.domain.model.Authority;
import com.map.gaja.user.domain.model.User;
import com.map.gaja.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.map.gaja.client.application.ClientConvertor.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ClientBulkService {
    private final ClientRepository clientRepository;
    private final GroupRepository groupRepository;
    private final ClientBulkRepository clientBulkRepository;
    private final UserRepository userRepository;
    private final IncreasingClientService increasingClientService;
    private final AuthenticationRepository securityUserGetter;

    /**
     * 파싱한 엑셀 데이터 저장.
     * JPA와 관계없이 저장하기 때문에 ID를 반환하지 않음.
     * 비동기로 처리한 정보를 받기 때문에 securityUserGetter.getAuthority()로 세션 정보 가져오기 불가능
     */
    public void saveClientExcelData(Long groupId, List<ParsedClientDto> excelData, List<Authority> authority, String loginEmail) {
        User user = userRepository.findByEmail(loginEmail);
        Group group = GroupServiceHelper.findGroupByIdForUpdating(groupRepository, groupId);

        List<Client> savedClient = new ArrayList<>();
        excelData.forEach((clientData) -> savedClient.add(dtoToEntity(clientData, group, user)));

        increasingClientService.increaseByMany(group, authority.get(0), savedClient.size());
        clientBulkRepository.saveClientWithGroup(group, savedClient);
    }

    public List<Long> saveSimpleClientList(SimpleClientBulkRequest bulkRequest, String loginEmail) {
        User user = userRepository.findByEmail(loginEmail);
        Group group = GroupServiceHelper.findGroupByIdForUpdating(groupRepository, bulkRequest.getGroupId());

        List<Client> savedClient = new ArrayList<>();
        bulkRequest.getClients().forEach((clientRequest) -> {
            savedClient.add(dtoToEntity(clientRequest, group, user));
        });

        increasingClientService.increaseByMany(group, securityUserGetter.getAuthority().get(0), savedClient.size());
        clientRepository.saveAll(savedClient);
        return savedClient.stream().mapToLong(Client::getId).boxed()
                .collect(Collectors.toList());

        /*
         * 모바일의 빠른 진행을 위해 ID를 반환하도록 수정
         * 만약 모바일에서 새로고침 기능이 완성된다면 해당 코드를 사용하고
         * 반환타입을 void로 수정할 것
         */
        // clientBulkRepository.saveClientWithGroup(group, savedClient);
    }

    /**
     * 그룹 내의 특정 고객들을 제거
     */
    public void deleteBulkClient(Long groupId, List<Long> clientIds) {
        clientRepository.markClientImageAsDeleted(clientIds);
        clientRepository.deleteClientsInClientIds(clientIds);

        Group group = GroupServiceHelper.findGroupByIdForUpdating(groupRepository, groupId);
        group.decreaseClientCount(clientIds.size());
    }
}
