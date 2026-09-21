package com.gms.gmsmvp.repository;

import com.gms.gmsmvp.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    boolean existsByPaymentReference(String paymentReference);

    @Query("""
            select coalesce(sum(p.amount), 0) from Payment p
            where p.membershipId = :membershipId
              and p.paymentStatus not in ('REFUNDED', 'CANCELLED')
            """)
    BigDecimal sumPaidForMembership(@Param("membershipId") Long membershipId);

    @Query("""
            select p from Payment p
            join Member mem on mem.id = p.memberId
            join Membership ms on ms.id = p.membershipId
            where p.gymId = :gymId
              and (:status is null or p.paymentStatus = :status)
              and (
                    :search is null
                    or lower(p.paymentReference) like lower(concat('%', :search, '%'))
                    or lower(mem.memberCode) like lower(concat('%', :search, '%'))
                    or lower(mem.firstName) like lower(concat('%', :search, '%'))
                    or lower(mem.lastName) like lower(concat('%', :search, '%'))
                    or lower(ms.membershipCode) like lower(concat('%', :search, '%'))
                    or lower(p.transactionReference) like lower(concat('%', :search, '%'))
              )
            """)
    Page<Payment> search(@Param("gymId") Long gymId,
                         @Param("search") String search,
                         @Param("status") String status,
                         Pageable pageable);

    List<Payment> findByMembershipIdOrderByPaymentDateDescIdDesc(Long membershipId);

    @Query("""
            select coalesce(sum(p.amount), 0) from Payment p
            where p.gymId = :gymId
              and p.paymentDate >= :fromDate
              and p.paymentDate <= :toDate
              and p.paymentStatus not in ('REFUNDED', 'CANCELLED')
            """)
    BigDecimal sumCollected(@Param("gymId") Long gymId,
                            @Param("fromDate") java.time.LocalDate fromDate,
                            @Param("toDate") java.time.LocalDate toDate);

    @Query("""
            select p.paymentMethod, coalesce(sum(p.amount), 0), count(p)
            from Payment p
            where p.gymId = :gymId
              and p.paymentDate >= :fromDate
              and p.paymentDate <= :toDate
              and p.paymentStatus not in ('REFUNDED', 'CANCELLED')
            group by p.paymentMethod
            order by p.paymentMethod
            """)
    List<Object[]> sumCollectedByMethod(@Param("gymId") Long gymId,
                                        @Param("fromDate") java.time.LocalDate fromDate,
                                        @Param("toDate") java.time.LocalDate toDate);

    @Query("""
            select p.paymentDate, coalesce(sum(p.amount), 0), count(p)
            from Payment p
            where p.gymId = :gymId
              and p.paymentDate >= :fromDate
              and p.paymentDate <= :toDate
              and p.paymentStatus not in ('REFUNDED', 'CANCELLED')
            group by p.paymentDate
            order by p.paymentDate
            """)
    List<Object[]> sumCollectedByDay(@Param("gymId") Long gymId,
                                     @Param("fromDate") java.time.LocalDate fromDate,
                                     @Param("toDate") java.time.LocalDate toDate);
}
