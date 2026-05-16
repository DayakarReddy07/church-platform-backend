package com.church.church_platform.repository;

import com.church.church_platform.entity.Church;
import com.church.church_platform.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository
        extends JpaRepository<Post, Long> {

    List<Post> findByChurchOrderByCreatedAtDesc(Church church);

    List<Post> findAllByOrderByCreatedAtDesc();
}