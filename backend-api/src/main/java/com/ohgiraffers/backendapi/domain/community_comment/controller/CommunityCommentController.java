package com.ohgiraffers.backendapi.domain.community_comment.controller;

import com.ohgiraffers.backendapi.domain.community_comment.dto.CommunityCommentRequest;
import com.ohgiraffers.backendapi.domain.community_comment.dto.CommunityCommentResponse;
import com.ohgiraffers.backendapi.domain.community_comment.service.CommunityCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community/comments")
public class CommunityCommentController {

    private final CommunityCommentService service;

    @PostMapping("/post/{postId}")
    public ResponseEntity<CommunityCommentResponse> create(
            @PathVariable Long postId,
            @RequestBody CommunityCommentRequest.Create request
    ) {
        return ResponseEntity.ok(
                CommunityCommentResponse.from(service.create(request, postId, 1L))
        );
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommunityCommentResponse>> findByPost(
            @PathVariable Long postId
    ) {
        return ResponseEntity.ok(
                service.findByPostId(postId).stream()
                        .map(CommunityCommentResponse::from)
                        .toList()
        );
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommunityCommentResponse> update(
            @PathVariable Long commentId,
            @RequestBody CommunityCommentRequest.Update request
    ) {
        return ResponseEntity.ok(
                CommunityCommentResponse.from(service.update(commentId, request))
        );
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Long commentId) {
        service.delete(commentId);
        return ResponseEntity.noContent().build();
    }
}
