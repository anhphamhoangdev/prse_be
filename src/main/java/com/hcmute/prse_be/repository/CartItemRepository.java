package com.hcmute.prse_be.repository;

import com.hcmute.prse_be.dtos.CartItemDTO;
import com.hcmute.prse_be.entity.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {

    @Query("""
        SELECT ci 
        FROM CartItemEntity ci 
        WHERE ci.cartId = :cartId
    """)
    List<CartItemEntity> findByCartId(Long cartId);

    @Query("""
        SELECT ci 
        FROM CartItemEntity ci 
        WHERE ci.cartId = :cartId AND ci.courseId = :courseId
    """)
    CartItemEntity findByCartIdAndCourseId(Long cartId, Long courseId);

}
