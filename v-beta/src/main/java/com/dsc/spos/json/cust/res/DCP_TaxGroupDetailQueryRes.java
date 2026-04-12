package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import com.dsc.spos.json.JSONFieldRequired;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 *
 * @author YapiGenerator自动生成
 * @date 2025/02/25
 */
@Getter
@Setter
public class DCP_TaxGroupDetailQueryRes extends JsonRes{

   private List<Datas> datas;

    @Getter
    @Setter
    public class Datas{
          private String lastModiOpId;
          private String lastModiOpName;
          private String taxGroupName;
          private String createDeptId;
          private String createOpId;
          private String createDeptName;
          private String createOpName;
          private List<GoodsList> goodsList;
          private String memo;
          private String taxCode;
          private String taxGroupNo;
          private String goodsQty;
          private String taxName;
          private String taxRate;
          private String lastModiTime;
          private String inclTax;
          private String createTime;
          private String status;
    }

    @Getter
    @Setter
    public class GoodsList{
          private String attrId;
          private String goodsQty;
          private String attrType;
          private String attrName;
          private String status;
    }

}