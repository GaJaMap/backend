package com.map.gaja.client.infrastructure.repository;

import com.map.gaja.client.domain.model.Client;
import com.map.gaja.group.domain.model.Group;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ClientBulkRepository {
    private final JdbcTemplate template;

    /**
     * 엑셀 파일 저장 시에 Insert 쿼리를 한 번에 해결하기 위한 메소드
     * @param group 고객 리스트가 들어갈 그룹
     * @param newClient 새로 생성된 고객들
     */
    public void saveClientWithGroup(Group group, List<Client> newClient) {
        group.increaseClientCount(newClient.size());

        final String insertSQL = "INSERT INTO " +
                "client (name, phone_number, address, detail, latitude, longitude, group_id, created_at, updated_at) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

        template.batchUpdate(insertSQL, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Client client = newClient.get(i);
                ps.setString(1, client.getName());
                ps.setString(2, client.getPhoneNumber());
                ps.setString(3, client.getAddress().getAddress());
                ps.setString(4, client.getAddress().getDetail());
                ps.setDouble(5, client.getLocation().getLatitude());
                ps.setDouble(6, client.getLocation().getLongitude());
                ps.setLong(7, group.getId());
            }

            @Override
            public int getBatchSize() {
                return newClient.size();
            }
        });
    }
}
