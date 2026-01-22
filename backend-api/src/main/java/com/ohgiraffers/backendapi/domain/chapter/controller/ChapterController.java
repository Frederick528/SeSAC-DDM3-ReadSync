package com.ohgiraffers.backendapi.domain.chapter.controller;

import com.ohgiraffers.backendapi.domain.chapter.dto.ChapterRequestDTO;
import com.ohgiraffers.backendapi.domain.chapter.dto.ChapterResponseDTO;
import com.ohgiraffers.backendapi.domain.chapter.dto.ChapterUrlRequestDTO;
import com.ohgiraffers.backendapi.domain.chapter.service.ChapterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/chapters")
@RequiredArgsConstructor
@Tag(name = "Chapter API", description = "도서 챕터 관리 및 뷰어 연동")
public class ChapterController {

    private final ChapterService chapterService;

    @Operation(summary = "[관리자] 챕터 등록 (파일 업로드)", description = "JSON 파일을 업로드하여 챕터를 생성. 메타데이터 미입력 시 파일에서 자동 추출.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChapterResponseDTO> createChapter(
            @RequestPart(value = "file") MultipartFile file,
            @RequestParam(value = "bookId") Long bookId,
            @RequestParam(value = "chapterName", required = false) String chapterName,
            @RequestParam(value = "sequence", required = false) Integer sequence) {
        ChapterRequestDTO requestDTO = ChapterRequestDTO.builder()
                .bookId(bookId)
                .chapterName(chapterName)
                .sequence(sequence)
                .file(file)
                .build();

        ChapterResponseDTO response = chapterService.createChapter(requestDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "[관리자] 챕터 등록 (URL 기반)", description = "URL을 입력하여 챕터를 생성. 문단 개수(paragraphs)도 함께 입력 가능.")
    @PostMapping("/url")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChapterResponseDTO> createChapterByUrl(
            @RequestBody ChapterUrlRequestDTO requestDTO) {
        ChapterResponseDTO response = chapterService.createChapterByUrl(requestDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "[관리자] 챕터 수정 (파일 업로드)", description = "챕터의 파일 또는 메타데이터를 수정. 파일 변경 시 'isEmbedded' 상태가 초기화됨.")
    @PutMapping(value = "/{chapterId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChapterResponseDTO> updateChapter(
            @PathVariable Long chapterId,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "chapterName", required = false) String chapterName,
            @RequestParam(value = "sequence", required = false) Integer sequence) {
        // 수정 시 bookId는 보통 변경하지 않으므로 DTO에 null 혹은 기존값 유지
        ChapterRequestDTO requestDTO = ChapterRequestDTO.builder()
                .chapterName(chapterName)
                .sequence(sequence)
                .file(file)
                .build();

        ChapterResponseDTO response = chapterService.updateChapter(chapterId, requestDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "[관리자] 챕터 수정 (URL 기반)", description = "URL 및 메타데이터를 수정. URL 변경 시 'isEmbedded' 상태가 초기화됨.")
    @PutMapping("/{chapterId}/url")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChapterResponseDTO> updateChapterByUrl(
            @PathVariable Long chapterId,
            @RequestBody ChapterUrlRequestDTO requestDTO) {
        ChapterResponseDTO response = chapterService.updateChapterByUrl(chapterId, requestDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "[관리자] 챕터 삭제", description = "챕터와 연관된 파일 및 데이터를 영구 삭제.")
    @DeleteMapping("/{chapterId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteChapter(@PathVariable Long chapterId) {
        chapterService.deleteChapter(chapterId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "[사용자/관리자용] 챕터 조회 (뷰어용)", description = "챕터의 상세 정보와 함께 파일에 저장된 본문 내용(JSON)을 반환.")
    @GetMapping("/{chapterId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ChapterResponseDTO> getChapter(@PathVariable Long chapterId) {
        ChapterResponseDTO response = chapterService.getChapter(chapterId);
        return ResponseEntity.ok(response);
    }
}