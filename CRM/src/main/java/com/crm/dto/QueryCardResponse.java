package com.crm.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 查询卡片响应 DTO
 */
@Data
public class QueryCardResponse {

    // 基本信息
    private Integer cardId;
    private String eid;
    private String cardNo;
    private String cardSnNo;
    private String password;
    private Integer mainCard;
    private String memberId;
    private String cardTypeId;
    private Integer cardKind;
    private Integer eCardSign;
    private Integer cardMaded;

    // 日期信息
    private Date validDate;
    private Date generateDate;
    private Date issueDate;
    private Date activeDate;

    // 联系信息
    private String mobile;

    // 金额信息
    private BigDecimal faceAmount;
    private BigDecimal amount;
    private BigDecimal amount1;
    private BigDecimal amount2;

    // 充值信息
    private Integer rechTimes;
    private BigDecimal rechAmount1;
    private BigDecimal rechAmount2;
    private Date lastRechTime;

    // 消费信息
    private BigDecimal payTimes;
    private BigDecimal payAmount1;
    private BigDecimal payAmount2;
    private Date lastPayTime;

    // 消费统计
    private Integer conTimes;
    private BigDecimal conAmount;
    private Date lastConTime;

    // 积分信息
    private Integer integrateTimes;
    private BigDecimal allPoint;
    private BigDecimal validPoint;
    private BigDecimal usedPoint;
    private BigDecimal invalidPoint;
    private BigDecimal bean;

    // 签名
    private String signature;

    // 组织信息
    private String companyId;
    private String shopId;
    private String channelId;
    private String employeeId;
    private String departId;

    // 状态信息
    private String remark;
    private Integer status;

    // 操作信息
    private String createOpId;
    private Date createTime;
    private String lastModiOpId;
    private Date lastModiTime;
    private Integer oldStatus;

    // 分支信息
    private Integer branchType;
    private String branchId;
    private Integer underway;

    // 售前售后
    private Integer befSaleStatus;
    private BigDecimal befSaleAmount1;
    private BigDecimal befSaleAmount2;
    private String saleBillNo;
    private BigDecimal aftSaleAmount1;
    private BigDecimal aftSaleAmount2;

    // 客户信息
    private String custId;
    private Integer fixCard;

    // 等级信息
    private Date levelBeginDate;
    private Date levelEndDate;
    private BigDecimal toBeInvalidPoint;
    private Date toBeInvalidEndDate;
    private Date levelBeginDate2;

    // 绑定信息
    private String bindPassword;
    private String actionKey;
    private Date doneInvalidEndDate;
    private String orgCardSnNo;
    private Integer isOldCard;
    private Integer bindToQimai;
    private String memberName;
    private Integer sex;
    private String identityCode;
    private String generateBillNo;
    private Date bindToQimaiDate;
    private String changeReason;
    private String rechargeDeadline;
    private Date bindCardValidDate;

    // 响应状态
    private Integer code;
    private String message;
}
