package com.map.gaja.memo.infrastructure;

import com.map.gaja.memo.domain.model.Memo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemoRepository extends JpaRepository<Memo, Long> {
}
