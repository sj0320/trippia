package com.trippia.travel.domain.companionpost.post;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompanionPostRepository extends JpaRepository<CompanionPost, Long>, CompanionPostRepositoryCustom  {
    @Query("SELECT p FROM CompanionPost p LEFT JOIN FETCH p.comments WHERE p.user.id = :userId")
    List<CompanionPost> findAllWithCommentsByUserId(@Param("userId") Long userId);

    @Query("SELECT DISTINCT p FROM CompanionPost p LEFT JOIN FETCH p.comments ORDER BY p.createdAt DESC")
    List<CompanionPost> findAllWithComments(Pageable pageable);
}
