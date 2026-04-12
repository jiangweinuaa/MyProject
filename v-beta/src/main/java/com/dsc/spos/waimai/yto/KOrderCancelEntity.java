package com.dsc.spos.waimai.yto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Date 2021/6/11 16:12
 * @Author mustang
 */
@Data
@Accessors(chain = true)
public class KOrderCancelEntity extends BaseEntity {
    String customerCode;
    String logisticsNo;
    String cancelDesc;
}
