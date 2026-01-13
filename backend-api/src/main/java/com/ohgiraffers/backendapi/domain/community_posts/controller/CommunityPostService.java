public List<CommunityPost> findAll() {
    return communityPostRepository.findAll();
}

public CommunityPost update(Long postId, CommunityPost request) {
    CommunityPost post = findById(postId);

    post.setTitle(request.getTitle());
    post.setContent(request.getContent());
    post.setUpdatedAt(LocalDateTime.now());

    return communityPostRepository.save(post);
}

public void delete(Long postId) {
    communityPostRepository.deleteById(postId);
}
