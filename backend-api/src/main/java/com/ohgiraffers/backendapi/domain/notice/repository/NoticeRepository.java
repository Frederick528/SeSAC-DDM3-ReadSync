package com.ohgiraffers.backendapi.domain.notice.repository;

import com.ohgiraffers.backendapi.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
