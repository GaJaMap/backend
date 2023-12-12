package com.map.gaja.user.constant;

import com.map.gaja.group.presentation.dto.response.GroupInfo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserConstant {
    public static final String APP_LOGIN = "APP";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final Supplier<GroupInfo> WHOLE_GROUP_INFO = () -> new GroupInfo() {
        @Override
        public Long getGroupId() {
            return -1L;
        }

        @Override
        public String getGroupName() {
            return "전체";
        }

        @Override
        public Integer getClientCount() {
            return -1;
        }
    };

}
