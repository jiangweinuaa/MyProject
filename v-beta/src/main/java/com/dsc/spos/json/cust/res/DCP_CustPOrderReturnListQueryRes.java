package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import com.dsc.spos.json.JSONFieldRequired;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 *
 * @author YapiGenerator自动生成
 * @date 2025/04/07
 */
@Getter
@Setter
public class DCP_CustPOrderReturnListQueryRes extends JsonRes{

   private List<Datas> datas;

    @Getter
    @Setter
    public class Datas{
          private String item;
          private String unitName;
          private String featureName;
          private String returnQty;
          private String pluNo;
          private String pluBarcode;
          private String canReturnQty;
          private String stockOutQty;
          private String taxCode;
          private String listImage;
          private String spec;
          private String taxRate;
          private String baseUnit;
          private String unit;
          private String receivingQty;
          private String bDate;
          private String inclTax;
          private String pOrderNo;
          private String qty;
          private String featureNo;
          private String isGift;
          private String pluName;
          private String refPurPrice;
          private String taxCalType;
          private String unitRatio;
          private String retailPrice;
          private String price;
    }

}