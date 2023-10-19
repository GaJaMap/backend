package com.map.gaja.client.apllication.aop;

import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.global.authentication.CurrentSecurityUserGetter;
import com.map.gaja.global.authentication.PrincipalDetails;
import com.map.gaja.user.domain.exception.ImageUploadPermissionException;
import com.map.gaja.user.domain.model.Authority;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientImageAuthCheckingAspectTest {

    @Mock
    private CurrentSecurityUserGetter userGetter;

    @InjectMocks
    private ClientImageAuthCheckingAspect clientImageAuthCheckingAspect;

    private PrincipalDetails mockFreeUser;
    private PrincipalDetails mockVipUser;

    @BeforeEach
    void beforeEach() {
        mockFreeUser = new PrincipalDetails(1L, "test@example.com", Authority.FREE.toString());
        mockVipUser = new PrincipalDetails(1L, "test@example.com", Authority.VIP.toString());
    }

    @Test
    @DisplayName("기본 이미지를 사용하는 Free User")
    void testFreeUser() throws Exception {
        when(userGetter.getCurrentUser()).thenReturn(mockFreeUser);
        JoinPoint joinPoint = mock(JoinPoint.class);
        NewClientRequest clientRequest = new NewClientRequest();
        clientRequest.setIsBasicImage(true);
        Object[] args = {clientRequest};
        Mockito.when(joinPoint.getArgs()).thenReturn(args);

        clientImageAuthCheckingAspect.checkAuthority(joinPoint);
    }

    @Test
    @DisplayName("추가 이미지를 사용하는 VIP User")
    void testVipUser() throws Exception {
        when(userGetter.getCurrentUser()).thenReturn(mockVipUser);
        JoinPoint joinPoint = mock(JoinPoint.class);
        NewClientRequest clientRequest = new NewClientRequest();
        clientRequest.setIsBasicImage(false);
        Object[] args = {clientRequest};
        Mockito.when(joinPoint.getArgs()).thenReturn(args);

        clientImageAuthCheckingAspect.checkAuthority(joinPoint);
    }

    @Test
    @DisplayName("추가 이미지를 사용하는 Free User")
    void testFreeUserUsingImage() throws Exception {
        when(userGetter.getCurrentUser()).thenReturn(mockFreeUser);
        JoinPoint joinPoint = mock(JoinPoint.class);
        NewClientRequest clientRequest = new NewClientRequest();
        clientRequest.setIsBasicImage(false);
        Object[] args = {clientRequest};
        Mockito.when(joinPoint.getArgs()).thenReturn(args);

        assertThrows(ImageUploadPermissionException.class,
                () -> clientImageAuthCheckingAspect.checkAuthority(joinPoint));
    }

}