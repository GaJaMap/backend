package com.map.gaja.global.schedule;

import com.map.gaja.client.domain.exception.S3NotWorkingException;
import com.map.gaja.client.domain.model.ClientImage;
import com.map.gaja.client.infrastructure.repository.ClientImageRepository;
import com.map.gaja.client.infrastructure.repository.ClientQueryRepository;
import com.map.gaja.client.infrastructure.s3.S3FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImageDeletionTask {
    private static final long LIMIT_SIZE = 1000L;
    private final S3FileService s3FileService;
    private final ClientQueryRepository clientQueryRepository;
    private final ClientImageRepository clientImageRepository;

    @Transactional
    public int deleteClientImages() {
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
