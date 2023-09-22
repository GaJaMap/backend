package com.map.gaja.temp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderSearch {

    private String memberName; //회원 이름
    private InquiryStatus inquiryStatus; //주문 상태[ORDER, CANCEL]
    private InquiryCategory inquiryCategory; //주문 상태[ORDER, CANCEL]
}
