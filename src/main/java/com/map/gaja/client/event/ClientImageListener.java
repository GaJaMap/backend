package com.map.gaja.client.event;

import com.map.gaja.client.domain.model.ClientImage;
import com.map.gaja.client.infrastructure.s3.S3FileService;
import com.map.gaja.client.presentation.dto.subdto.StoredFileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientImageListener {

    private final S3FileService fileService;

    @TransactionalEventListener(
            classes = ClientImageCreationEvent.class,
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void storeImage(ClientImageCreationEvent event) {
        ClientImage dbClientImage = event.getClientImage();
        try {
            StoredFileDto fileDto = new StoredFileDto(dbClientImage.getSavedPath(), dbClientImage.getOriginalName());
            fileService.storeFile(fileDto, event.getImage());
        } catch (Exception e) {
            // TODO: 사용자에게 넘기거나 예외를 처리하거나 - 일단 무조건 사용자에게 책임 넘김
            log.warn("이미지 관련 오류가 발생. clientImage에 저장된 임시 saved_path 필드 조치 필요 : " + dbClientImage.getSavedPath(), e);
        }
    }
}
