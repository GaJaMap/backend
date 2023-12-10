package com.map.gaja.global.schedule;

import com.map.gaja.client.infrastructure.repository.ClientImageRepository;
import com.map.gaja.client.infrastructure.repository.ClientRepository;

import com.map.gaja.group.infrastructure.GroupRepository;
import com.map.gaja.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeletionTask {
    private final ClientImageRepository clientImageRepository;
    private final GroupRepository groupRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final ImageDeletionTask imageDeletionTask;

    /**
     * 회원탈퇴, 그룹 삭제, 클라이언트 삭제를 처리하기 위한 스케줄링
     */
    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul") // 초 분 시 => 한국 시간 매일 새벽 3시에 작업 수행
    public void execute() {
        long startTime = System.nanoTime();

        groupRepository.deleteByWithdrawalUser(); //회원탈퇴한 유저들의 그룹 isDeleted true로 변경

        clientImageRepository.markDeleted(); //삭제된 그룹에 속한 클라이언트 이미지 isDeleted true로 변경

        int deletedClientCount = clientRepository.deleteClientsInDeletedGroup(); //삭제된 그룹에 속한 클라이언트 전부 삭제

        int deletedGroupCount = groupRepository.deleteMarkedGroups(); //isDeleted 마크된 그룹 전부 삭제

        int deletedUserCount = userRepository.deleteWithdrawnUsers(); //회원 탈퇴한 유저 전부 삭제

        int deletedImageCount = imageDeletionTask.process(); //이미지 삭제

        long executionTime = (System.nanoTime() - startTime) / 1000000;

        createLog(executionTime, deletedClientCount, deletedGroupCount, deletedUserCount, deletedImageCount);
    }

    private void createLog(long executionTime, int deletedClientCount, int deletedGroupCount, int deletedUserCount, int deletedImageCount) {
        String result = String.format("유저: %d , 그룹: %d , 클라이언트: %d , 이미지: %d", deletedUserCount, deletedGroupCount, deletedClientCount, deletedImageCount);
        log.info("{} => Time: {} ms", result, executionTime);
    }

}
