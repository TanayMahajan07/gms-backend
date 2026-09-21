package com.gms.gmsmvp.repository;

import com.gms.gmsmvp.entity.Membership;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

    boolean existsByMembershipCode(String membershipCode);

    boolean existsByMemberIdAndGymIdAndMembershipStatus(Long memberId, Long gymId, String membershipStatus);

    long countByGymIdAndMembershipStatus(Long gymId, String membershipStatus);

    @Query("""
            select m from Membership m
            join Member mem on mem.id = m.memberId
            join MembershipPlan p on p.id = m.planId
            where m.gymId = :gymId
              and (:status is null or m.membershipStatus = :status)
              and (
                    :search is null
                    or lower(m.membershipCode) like lower(concat('%', :search, '%'))
                    or lower(mem.memberCode) like lower(concat('%', :search, '%'))
                    or lower(mem.firstName) like lower(concat('%', :search, '%'))
                    or lower(mem.lastName) like lower(concat('%', :search, '%'))
                    or lower(p.planName) like lower(concat('%', :search, '%'))
              )
            """)
    Page<Membership> search(@Param("gymId") Long gymId,
                            @Param("search") String search,
                            @Param("status") String status,
                            Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Membership m
            set m.membershipStatus = :expiredStatus,
                m.membershipStatusId = :expiredStatusId
            where m.membershipStatus = :activeStatus
              and m.endDate < :today
            """)
    int expirePastDue(@Param("today") LocalDate today,
                      @Param("activeStatus") String activeStatus,
                      @Param("expiredStatus") String expiredStatus,
                      @Param("expiredStatusId") Long expiredStatusId);

    @Query("""
            select m from Membership m
            where m.gymId = :gymId
              and m.membershipStatus = 'ACTIVE'
              and m.endDate >= :today
              and m.endDate <= :horizon
            order by m.endDate asc
            """)
    List<Membership> findExpiringSoon(@Param("gymId") Long gymId,
                                      @Param("today") LocalDate today,
                                      @Param("horizon") LocalDate horizon,
                                      Pageable pageable);

    @Query("""
            select count(m) from Membership m
            where m.gymId = :gymId
              and m.membershipStatus = 'ACTIVE'
              and m.endDate >= :today
              and m.endDate <= :horizon
            """)
    long countExpiringSoon(@Param("gymId") Long gymId,
                           @Param("today") LocalDate today,
                           @Param("horizon") LocalDate horizon);

    @Query("""
            select m from Membership m
            where m.gymId = :gymId
              and m.membershipStatus = 'EXPIRED'
              and not exists (
                    select 1 from Membership a
                    where a.memberId = m.memberId
                      and a.gymId = m.gymId
                      and a.membershipStatus = 'ACTIVE'
              )
            order by m.endDate asc
            """)
    List<Membership> findExpiredNeedingRenew(@Param("gymId") Long gymId, Pageable pageable);

    @Query("""
            select count(m) from Membership m
            where m.gymId = :gymId
              and m.membershipStatus = 'EXPIRED'
              and not exists (
                    select 1 from Membership a
                    where a.memberId = m.memberId
                      and a.gymId = m.gymId
                      and a.membershipStatus = 'ACTIVE'
              )
            """)
    long countExpiredNeedingRenew(@Param("gymId") Long gymId);

    @Query("""
            select count(m) from Membership m
            where m.gymId = :gymId
              and m.amount > (
                    select coalesce(sum(p.amount), 0) from Payment p
                    where p.membershipId = m.id
                      and p.paymentStatus not in ('REFUNDED', 'CANCELLED')
              )
            """)
    long countOutstanding(@Param("gymId") Long gymId);

    @Query("""
            select coalesce(sum(
                m.amount - (
                    select coalesce(sum(p.amount), 0) from Payment p
                    where p.membershipId = m.id
                      and p.paymentStatus not in ('REFUNDED', 'CANCELLED')
                )
            ), 0) from Membership m
            where m.gymId = :gymId
              and m.amount > (
                    select coalesce(sum(p.amount), 0) from Payment p
                    where p.membershipId = m.id
                      and p.paymentStatus not in ('REFUNDED', 'CANCELLED')
              )
            """)
    BigDecimal sumOutstandingAmount(@Param("gymId") Long gymId);

    @Query("""
            select m from Membership m
            where m.gymId = :gymId
              and m.amount > (
                    select coalesce(sum(p.amount), 0) from Payment p
                    where p.membershipId = m.id
                      and p.paymentStatus not in ('REFUNDED', 'CANCELLED')
              )
            order by m.id desc
            """)
    List<Membership> findOutstanding(@Param("gymId") Long gymId, Pageable pageable);
}
