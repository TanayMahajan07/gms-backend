package com.gms.gmsmvp.repository;

import com.gms.gmsmvp.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByMemberCode(String memberCode);

    Optional<Member> findByMemberCode(String memberCode);

    @Query("""
            select m from Member m
            where (:gymId is null or m.gymId = :gymId)
              and (:active is null or m.active = :active)
              and (
                    :search is null
                    or lower(m.memberCode) like lower(concat('%', :search, '%'))
                    or lower(m.firstName) like lower(concat('%', :search, '%'))
                    or lower(m.lastName) like lower(concat('%', :search, '%'))
                    or lower(m.mobile) like lower(concat('%', :search, '%'))
                    or lower(m.email) like lower(concat('%', :search, '%'))
              )
            """)
    Page<Member> search(@Param("search") String search,
                        @Param("gymId") Long gymId,
                        @Param("active") Boolean active,
                        Pageable pageable);
}
