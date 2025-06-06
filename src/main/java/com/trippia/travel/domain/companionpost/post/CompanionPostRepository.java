package com.trippia.travel.domain.companionpost.post;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanionPostRepository extends JpaRepository<CompanionPost, Long>, CompanionPostRepositoryCustom  {
}
