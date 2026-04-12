package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import com.dsc.spos.json.JSONFieldRequired;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 *
 * @author YapiGenerator自动生成
 * @date 2025/04/10
 */
@Getter
@Setter
public class DCP_HrExpStatQueryRes extends JsonRes{

   private List<Datas> datas;

    @Getter
    @Setter
    public class Datas{
          private List<WorkList> workList;
          private String corp ;
          private String period;
          private String accountID ;
          private String cost_Calculation ;
          private String year ;
          private String account ;
          private String status;
    }
    @Getter
    @Setter
    public class WorkList{
          private String costCenter ;
          private String idleExpense ;
          private String allocAmount;
          private String item;
          private String costCenterNo ;
          private String allocBaseSum ;
          private String fixedExpense ;
          private String unitCost;
          private String totExpense ;
          private String allocFormula ;
          private String allocType ;
          private String stdCapacity ;
    }


}