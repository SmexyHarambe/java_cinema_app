package com.uasdisprog.tcpserver.repository;

import com.uasdisprog.tcpserver.entity.NotifUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotifUserRepository extends JpaRepository<NotifUser, Integer> {
    
    @Query("SELECT COUNT(n) FROM NotifUser n WHERE n.customerId = :customerId AND n.isRead = false")
    Long countUnreadByCustomerId(@Param("customerId") Integer customerId);
    
    @Query("SELECT n FROM NotifUser n WHERE n.customerId = :customerId ORDER BY n.id DESC")
    List<NotifUser> findByCustomerIdOrderByIdDesc(@Param("customerId") Integer customerId);

    void deleteByNotifIdAndCustomerId(Integer notifId, Integer customerId);
}
