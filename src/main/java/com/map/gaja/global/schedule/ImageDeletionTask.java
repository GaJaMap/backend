package com.map.gaja.global.schedule;

import com.map.gaja.client.domain.exception.S3NotWorkingException;
import com.map.gaja.client.domain.model.ClientImage;
import com.map.gaja.client.infrastructure.repository.ClientImageRepository;
import com.map.gaja.client.infrastructure.repository.ClientQueryRepository;
import com.map.gaja.client.infrastructure.s3.S3FileService;

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

    /**
     * DB에 삭제할 ClientImage가 존재하지 않을 때까지 LIMIT_SIZE만큼 ClientImage를 조회하고 s3에 해당 경로 이미지 삭제
     * */
    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul") // 초 분 시 => 한국 시간 매일 새벽 3시에 작업 수행
    @Transactional
    public void execute() {
        long startTime = System.nanoTime();

        //삭제할 데이터를 한번에 조회하는 것이 아니라 LIMIT_SIZE만큼 조회
        while (true) {
            List<ClientImage> clientImages = clientQueryRepository.findImagePathsToDelete(0L, LIMIT_SIZE);
            List<Long> deleteIds = new ArrayList<>(); //삭제 할 id 저장

            for (ClientImage clientImage : clientImages) {
                try {
                    s3FileService.removeFile(clientImage.getSavedPath());
                } catch (S3NotWorkingException e) {
                    clientImageRepository.deleteClientImagesInIds(deleteIds); // s3예외가 발생했지만 s3에서 삭제된 이미지가 있을 수도 있으므로 테이블에서도 레코드 삭제
                    return;
                }
                deleteIds.add(clientImage.getId());
            }

            clientImageRepository.deleteClientImagesInIds(deleteIds);
            if (clientImages.size() <= LIMIT_SIZE) { // 다음 데이터가 존재하는지 체크
                break;
            }
        }

        long executionTime = (System.nanoTime() - startTime) / 1000000;
        log.info("Time: {} ms", executionTime);
    }
}
