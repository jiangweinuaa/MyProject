package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import com.dsc.spos.json.JSONFieldRequired;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 *
 * @author YapiGenerator自动生成
 * @date 2025/02/24
 */
@Getter
@Setter
public class DCP_FeeQueryRes extends JsonRes{

   private List<Level1Elm> datas;

    @Getter
    @Setter
    public class Level1Elm{
          private String accountingPolicy;
          private String isInvoiceIssued;
          private String inSettlement;
          private List<Level2Elm> datas;
          private String fee;
          private String feeName;
          private String feeNature;
          private String feeType;
          private String taxCode;
          private String taxName;
          private String status;
          private String feeAllocation;
          private String isTourGroup;
          private String priceCategory;

    }
    @Getter
    @Setter
    public class Level2Elm{
          private String feeName;
          private String langType;
          private String status;
    }


}