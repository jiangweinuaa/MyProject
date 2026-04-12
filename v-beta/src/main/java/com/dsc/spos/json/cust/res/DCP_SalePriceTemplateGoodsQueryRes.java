package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import com.dsc.spos.json.JSONFieldRequired;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 *
 * @author YapiGenerator自动生成
 * @date 2025/02/26
 */
@Getter
@Setter
public class DCP_SalePriceTemplateGoodsQueryRes extends JsonRes{

   private List<Datas> datas;

    @Getter
    @Setter
    public class Datas{
          private String templateId;
          private String endDate;
          private String createOpName;
          private String pluNo;
          private String isDiscount;
          private String ofNo;
          private String isProm;
          private double price;
          private String departId;
          private String oItem;
          private String departName;
          private String lastModiOpId;
          private String lastModiOpName;
          private String employeeName;
          private String item;
          private String redisUpdateSuccess;
          private String createOpId;
          private String unitName;
          private double standardPrice;
          private String employeeId;
          private String confirmTime;
          private String beginDate;
          private String lastModiTime;
          private String unit;
          private String createTime;
          private String minPrice;
          private String featureNo;
          private String pluName;
          private String status;
    }

}