package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import com.dsc.spos.json.JSONFieldRequired;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 *
 * @author YapiGenerator自动生成
 * @date 2025/02/28
 */
@Getter
@Setter
public class DCP_PriceAdjustQueryRes extends JsonRes{

   private List<Data> datas;

    @Getter
    @Setter
    public class Data{
          private String purTemplateName;
          private String lastmodifyID;
          private String creatorName;
          private String cancel_datetime;
          private String memo;
          private String employeeID;
          private String confirm_datetime;
          private String orgNo;
          private String ownerName;
          private String ownDeptID;
          private String ownDeptName;
          private String create_datetime;
          private String supplier;
          private String cancelBy;
          private String cancelByName;
          private String billNo;
          private String departName;
          private String lastmodify_datetime;
          private String supplierName;
          private String employeeName;
          private String totcQty;
          private String creatorDeptName;
          private String templateNo;
          private String creatorDeptID;
          private String orgName;
          private String confirmByName;
          private String confirmBy;
          private String isUpdateSdPrice;
          private String creatorID;
          private String ownerID;
          private String invalidDate;
          private String lastmodifyName;
          private String purTemplateNo;
          private String bType;
          private String isUpdate_DefpurPrice;
          private String bDate;
          private String templateName;
          private String departID;
          private String effectiveDate;
          private String status;
    }

}