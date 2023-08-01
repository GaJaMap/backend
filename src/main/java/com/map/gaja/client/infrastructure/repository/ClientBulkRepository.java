package com.map.gaja.client.infrastructure.repository;

import com.map.gaja.client.domain.model.Client;
import com.map.gaja.group.domain.model.Group;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

@Repository
@Transactional
@RequiredArgsConstructor
public class ClientBulkRepository {
    private final JdbcTemplate template;

    /**
     * 엑셀 파일 저장 시에 Insert 쿼리를 한 번에 해결하기 위한 메소드
     * @param group 고객 리스트가 들어갈 그룹
     * @param newClient 새로 생성된 고객들
     */
    public void saveClientWithGroup(Group group, List<Client> newClient) {
        final String insertSQL = "INSERT INTO " +
                "client (name, phone_number, address, detail, latitude, longitude, group_id, created_at, updated_at) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

        template.batchUpdate(insertSQL, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Client client = newClient.get(i);
                ps.setString(1, client.getName());
                setStringOrSetNull(ps, 2, client.getPhoneNumber() == null ? null : client.getPhoneNumber());
                setStringOrSetNull(ps, 3, client.getAddress() == null ? null : client.getAddress().getAddress());
                setStringOrSetNull(ps, 4, client.getAddress() == null ? null : client.getAddress().getDetail());
                setDoubleOrSetNull(ps, 5, client.getLocation() == null ? null : client.getLocation().getLatitude());
                setDoubleOrSetNull(ps, 6, client.getLocation() == null ? null : client.getLocation().getLongitude());
                ps.setLong(7, group.getId());
            }

            private void setStringOrSetNull(PreparedStatement ps, int index, String value) throws SQLException {
                if (value != null) {
                    ps.setString(index, value);
                } else {
                    ps.setNull(index, Types.VARCHAR);
                }
            }

            private void setDoubleOrSetNull(PreparedStatement ps, int index, Double value) throws SQLException {
                if (value != null) {
                    ps.setDouble(index, value);
                } else {
                    ps.setNull(index, Types.DOUBLE);
                }
            }

            @Override
            public int getBatchSize() {
                return newClient.size();
            }
        });
    }
}
