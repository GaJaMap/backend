package com.map.gaja.global.authentication.imageuploads;

import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.global.authentication.AuthenticationRepository;
import com.map.gaja.global.authentication.imageuploads.checkers.ImageUploadRequestChecker;
import com.map.gaja.user.domain.exception.ImageUploadPermissionException;
import com.map.gaja.user.domain.model.Authority;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageAuthCheckingAspectTest {

    @Mock
    private AuthenticationRepository userGetter;

    @Mock
    private ImageUploadRequestChecker mockChecker;
    @Mock
    private List<ImageUploadRequestChecker> requestCheckers;

    @InjectMocks
    private ImageAuthCheckingAspect imageAuthCheckingAspect;

    @Test
    @DisplayName("기본 이미지를 사용하는 Free User")
    void testFreeUser() throws Exception {
        when(requestCheckers.iterator()).thenReturn(Arrays.asList(mockChecker).iterator());
        when(userGetter.getAuthority()).thenReturn(List.of(Authority.FREE));
        when(mockChecker.isSupported(Mockito.any())).thenReturn(true);
        when(mockChecker.isImageUploadingRequest(Mockito.any())).thenReturn(false);

        JoinPoint joinPoint = mock(JoinPoint.class);
        NewClientRequest clientRequest = new NewClientRequest();
        clientRequest.setIsBasicImage(true);
        Object[] args = {clientRequest};
        when(joinPoint.getArgs()).thenReturn(args);

        imageAuthCheckingAspect.checkAuthority(joinPoint);
    }

    @Test
    @DisplayName("추가 이미지를 사용하는 VIP User")
    void testVipUser() throws Exception {
        when(userGetter.getAuthority()).thenReturn(List.of(Authority.VIP));
        JoinPoint joinPoint = mock(JoinPoint.class);
        imageAuthCheckingAspect.checkAuthority(joinPoint);
    }

    @Test
    @DisplayName("추가 이미지를 사용하는 Free User")
    void testFreeUserUsingImage() throws Exception {
        when(requestCheckers.iterator()).thenReturn(Arrays.asList(mockChecker).iterator());
        when(userGetter.getAuthority()).thenReturn(List.of(Authority.FREE));
        when(mockChecker.isSupported(Mockito.any())).thenReturn(true);
        when(mockChecker.isImageUploadingRequest(Mockito.any())).thenReturn(true);

        JoinPoint joinPoint = mock(JoinPoint.class);
        NewClientRequest clientRequest = new NewClientRequest();
        clientRequest.setIsBasicImage(false);
        Object[] args = {clientRequest};
        when(joinPoint.getArgs()).thenReturn(args);

        assertThrows(ImageUploadPermissionException.class,
                () -> imageAuthCheckingAspect.checkAuthority(joinPoint));
    }

}