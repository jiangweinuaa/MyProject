package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import com.dsc.spos.json.JSONFieldRequired;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 *
 * @author YapiGenerator自动生成
 * @date 2025/03/12
 */
@Getter
@Setter
public class DCP_CustomerPriceAdjustQueryRes extends JsonRes{

   private List<Datas> datas;

    @Getter
    @Setter
    public class Datas{
          private String createDeptId;
          private String endDate;
          private String createOpName;
          private String memo;
          private String ownOpName;
          private String totCqty;
          private String modifyTime;
          private String ownDeptName;
          private String departId;
          private String cancelBy;
          private String cancelByName;
          private String ownOpId;
          private String modifyByName;
          private String billNo;
          private String departName;
          private String employeeName;
          private String modifyBy;
          private String createOpId;
          private String createDeptName;
          private String totCustQty;
          private String confirmByName;
          private String confirmBy;
          private String employeeId;
          private String confirmTime;
          private String beginDate;
          private String ownDeptId;
          private String bDate;
          private String createTime;
          private String cancelTime;
          private String status;
    }

}