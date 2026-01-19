package com.ohgiraffers.backendapi.domain.community_post.controller;

import com.ohgiraffers.backendapi.domain.community_post.dto.CommunityPostRequest;
import com.ohgiraffers.backendapi.domain.community_post.dto.CommunityPostResponse;
import com.ohgiraffers.backendapi.domain.community_post.service.CommunityPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community/posts")
public class CommunityPostController {

    private final CommunityPostService service;

    @PostMapping
    public ResponseEntity<CommunityPostResponse> create(
            @RequestBody CommunityPostRequest.Create request
    ) {
        return ResponseEntity.ok(
                CommunityPostResponse.from(service.create(request, 1L))
        );
    }

    @GetMapping("/{postId}")
    public ResponseEntity<CommunityPostResponse> find(@PathVariable Long postId) {
        return ResponseEntity.ok(
                CommunityPostResponse.from(service.find(postId))
        );
    }

    @GetMapping
    public ResponseEntity<List<CommunityPostResponse>> findAll() {
        return ResponseEntity.ok(
                service.findAll().stream()
                        .map(CommunityPostResponse::from)
                        .toList()
        );
    }

    @PutMapping("/{postId}")
    public ResponseEntity<CommunityPostResponse> update(
            @PathVariable Long postId,
            @RequestBody CommunityPostRequest.Update request
    ) {
        return ResponseEntity.ok(
                CommunityPostResponse.from(service.update(postId, request))
        );
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(@PathVariable Long postId) {
        service.delete(postId);
        return ResponseEntity.noContent().build();
    }
}
