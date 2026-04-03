package com.crm.repository;

import com.crm.entity.CrmCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * CRM 卡片数据访问层
 */
@Repository
public interface CrmCardRepository extends JpaRepository<CrmCard, Integer> {

    /**
     * 根据卡号查询卡片信息
     * @param cardNo 卡号
     * @return 卡片信息
     */
    @Query("SELECT c FROM CrmCard c WHERE c.cardNo = :cardNo")
    Optional<CrmCard> findByCardNo(@Param("cardNo") String cardNo);
}
