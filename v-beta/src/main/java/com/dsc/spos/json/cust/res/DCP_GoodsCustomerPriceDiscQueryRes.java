package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import com.dsc.spos.json.JSONFieldRequired;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 *
 * @author YapiGenerator自动生成
 * @date 2025/03/27
 */
@Getter
@Setter
public class DCP_GoodsCustomerPriceDiscQueryRes extends JsonRes{

   private List<Datas> datas;

    @Getter
    @Setter
    public class Datas{
          private String custGroupNo;
          private String beginDate;
          private String lastModiTime;
          private String unit;
          private String unitName;
          private String endDate;
          private String price;
          private String pluNo;
          private String category;
          private String customerNo;
          private String discRate;
    }

}