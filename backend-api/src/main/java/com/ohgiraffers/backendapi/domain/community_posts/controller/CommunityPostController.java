package com.ohgiraffers.backendapi.domain.community_posts.controller;

import com.ohgiraffers.backendapi.domain.community_posts.entity.CommunityPost;
import com.ohgiraffers.backendapi.domain.community_posts.service.CommunityPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community/posts")
public class CommunityPostController {

    private final CommunityPostService communityPostService;

    /** 게시글 생성 */
    @PostMapping
    public ResponseEntity<CommunityPost> create(@RequestBody CommunityPost post) {
        return ResponseEntity.ok(communityPostService.create(post));
    }

    /** 게시글 단건 조회 */
    @GetMapping("/{postId}")
    public ResponseEntity<CommunityPost> findById(@PathVariable Long postId) {
        return ResponseEntity.ok(communityPostService.findById(postId));
    }

    /** 게시글 전체 조회 */
    @GetMapping
    public ResponseEntity<List<CommunityPost>> findAll() {
        return ResponseEntity.ok(communityPostService.findAll());
    }

    /** 게시글 수정 */
    @PutMapping("/{postId}")
    public ResponseEntity<CommunityPost> update(
            @PathVariable Long postId,
            @RequestBody CommunityPost request
    ) {
        return ResponseEntity.ok(communityPostService.update(postId, request));
    }

    /** 게시글 삭제 */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(@PathVariable Long postId) {
        communityPostService.delete(postId);
        return ResponseEntity.noContent().build();
    }
}
