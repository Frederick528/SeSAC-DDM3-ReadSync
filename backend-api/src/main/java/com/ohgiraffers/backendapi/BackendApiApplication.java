package com.ohgiraffers.backendapi;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class BackendApiApplication {

    public static void main(String[] args) {
        // .env 파일 로드 및 시스템 프로퍼티 설정
        Dotenv dotenv = Dotenv.configure()
                .directory("./") // root directory (backend-api)
                .ignoreIfMissing()
                .load();

        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });

        SpringApplication.run(BackendApiApplication.class, args);
    }

}
