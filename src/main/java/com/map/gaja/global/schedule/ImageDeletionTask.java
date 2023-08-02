package com.map.gaja.global.schedule;

import com.map.gaja.client.domain.exception.S3NotWorkingException;
import com.map.gaja.client.domain.model.ClientImage;
import com.map.gaja.client.infrastructure.repository.ClientImageRepository;
import com.map.gaja.client.infrastructure.repository.ClientQueryRepository;
import com.map.gaja.client.infrastructure.repository.ClientRepository;
import com.map.gaja.client.infrastructure.s3.S3FileService;

import com.map.gaja.group.infrastructure.GroupRepository;
import com.map.gaja.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ImageDeletionTask {
    private static final long LIMIT_SIZE = 1000L;
    private final S3FileService s3FileService;
    private final ClientQueryRepository clientQueryRepository;
    private final ClientImageRepository clientImageRepository;
    private final GroupRepository groupRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    /**
     * 회원탈퇴, 그룹 삭제, 클라이언트 삭제를 처리하기 위한 스케줄링
     * */
    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul") // 초 분 시 => 한국 시간 매일 새벽 3시에 작업 수행
    @Transactional
    public void execute() {
        long startTime = System.nanoTime();

        groupRepository.deleteByWithdrawalUser(); //회원탈퇴한 유저들의 그룹 isDeleted true로 변경

        clientImageRepository.markDeleted(); //삭제된 그룹에 속한 클라이언트 이미지 isDeleted true로 변경

        int deletedClientCount = clientRepository.deleteClientsInDeletedGroup(); //삭제된 그룹에 속한 클라이언트 전부 삭제

        int deletedGroupCount = groupRepository.deleteMarkedGroups(); //isDeleted 마크된 그룹 전부 삭제

        int deletedUserCount = userRepository.deleteWithdrawnUsers(); //회원 탈퇴한 유저 전부 삭제

        int deletedImageCount = deleteClientImages(); //이미지 삭제
        if(isS3Error(deletedImageCount)){ //s3 오류
            return;
        }

        createLog(startTime, deletedClientCount, deletedGroupCount, deletedUserCount, deletedImageCount);
    }

    private void createLog(long startTime, int deletedClientCount, int deletedGroupCount, int deletedUserCount, int deletedImageCount) {
        String result = String.format("유저: %d , 그룹: %d , 클라이언트: %d , 이미지: %d", deletedUserCount, deletedGroupCount, deletedClientCount, deletedImageCount);

        long executionTime = (System.nanoTime() - startTime) / 1000000;
        log.info("{} => Time: {} ms", result, executionTime);
    }

    private boolean isS3Error(int deletedImageCount) {
        if(deletedImageCount == -1){
            return false;
        }
        return true;
    }

    private int deleteClientImages() {
        int deletedImageCount = 0;

        // DB에 삭제할 ClientImage가 존재하지 않을 때까지 LIMIT_SIZE만큼 ClientImage를 조회하고 s3에 해당 경로 이미지 삭제
        while (true) {
            List<ClientImage> clientImages = clientQueryRepository.findImagePathsToDelete(0L, LIMIT_SIZE);
            List<Long> deleteIds = new ArrayList<>(); //삭제 할 id 저장
            for (ClientImage clientImage : clientImages) {
                try {
                    s3FileService.removeFile(clientImage.getSavedPath());
                } catch (S3NotWorkingException e) {
                    clientImageRepository.deleteClientImagesInIds(deleteIds); // s3예외가 발생했지만 s3에서 삭제된 이미지가 있을 수도 있으므로 테이블에서도 레코드 삭제
                    log.error("S3 오류입니다", e);
                    return -1;
                }
                deleteIds.add(clientImage.getId());
            }

            deletedImageCount += clientImages.size();
            clientImageRepository.deleteClientImagesInIds(deleteIds);
            if (clientImages.size() <= LIMIT_SIZE) { // 다음 데이터가 존재하는지 체크
                break;
            }
        }

        return deletedImageCount;
    }
}
