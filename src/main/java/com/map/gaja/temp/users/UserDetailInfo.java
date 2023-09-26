package com.map.gaja.temp.users;

import com.map.gaja.user.domain.model.Authority;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailInfo {
    private String email;
    private LocalDateTime createdDay;
    private LocalDateTime currentPaymentDay;
    private Long groupCount;
    private Authority authority;
}
