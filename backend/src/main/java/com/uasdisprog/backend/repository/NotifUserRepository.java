package com.uasdisprog.backend.repository;

import com.uasdisprog.backend.entity.NotifUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotifUserRepository extends JpaRepository<NotifUser, Integer> {
    List<NotifUser> findByCustomerIdOrderByNotifikasi_CreatedAtDesc(Integer customerId);
    
    @Query("SELECT COUNT(n) FROM NotifUser n WHERE n.customer.id = :customerId AND n.isRead = false")
    Long countUnreadByCustomerId(@Param("customerId") Integer customerId);
    
    @Query("SELECT n FROM NotifUser n WHERE n.customer.id = :customerId AND n.isRead = false ORDER BY n.notifikasi.createdAt DESC")
    List<NotifUser> findUnreadByCustomerId(@Param("customerId") Integer customerId);
}
