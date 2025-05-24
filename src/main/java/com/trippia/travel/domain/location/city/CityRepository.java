package com.trippia.travel.domain.location.city;

import com.trippia.travel.controller.dto.city.response.CityCountResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CityRepository extends JpaRepository<City, Long> {

    @Query("SELECT new com.trippia.travel.controller.dto.city.response.CityCountResponse(c.id, COUNT(d)) " +
            "FROM Diary d " +
            "JOIN d.city c " +
            "GROUP BY c.id " +
            "ORDER BY COUNT(d) DESC")
    List<CityCountResponse> findTopDiaryCities(Pageable pageable);
}
