package com.gms.gmsmvp.repository;

import com.gms.gmsmvp.entity.MembershipPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {

    Optional<MembershipPlan> findByIdAndGymId(Long id, Long gymId);

    @Query("""
            select p from MembershipPlan p
            where p.gymId = :gymId
              and (:active is null or p.active = :active)
              and (
                    :search is null
                    or lower(p.planName) like lower(concat('%', :search, '%'))
                    or lower(coalesce(p.description, '')) like lower(concat('%', :search, '%'))
              )
            """)
    Page<MembershipPlan> search(@Param("gymId") Long gymId,
                                @Param("search") String search,
                                @Param("active") Boolean active,
                                Pageable pageable);

    List<MembershipPlan> findByGymIdAndActiveTrueOrderByPlanNameAsc(Long gymId);
}
