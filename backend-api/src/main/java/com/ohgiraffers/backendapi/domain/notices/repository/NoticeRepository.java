package com.ohgiraffers.backendapi.domain.notices.repository;

import com.ohgiraffers.backendapi.domain.notices.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
