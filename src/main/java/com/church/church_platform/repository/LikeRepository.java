package com.church.church_platform.repository;

import com.church.church_platform.entity.Like;
import com.church.church_platform.entity.Post;
import com.church.church_platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository
        extends JpaRepository<Like, Long> {

    boolean existsByUserAndPost(User user, Post post);

    Optional<Like> findByUserAndPost(User user, Post post);

    Long countByPost(Post post);
}