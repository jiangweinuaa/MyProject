package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import com.dsc.spos.json.JSONFieldRequired;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 *
 * @author YapiGenerator自动生成
 * @date 2025/04/21
 */
@Getter
@Setter
public class DCP_CostLevelDetailUpdateRes extends JsonRes{

   private Datas datas;

    @Getter
    @Setter
    public class Datas{
          private String corp ;
          private String accountID ;
          private List<BottomList> bottomList;
          private String account ;
          private String status;
    }

    @Getter
    @Setter
    public class BottomList{
          private String baseUnit ;
          private String item;
          private String pluNo;
          private String MaterialSource ;
          private String pluType ;
          private String costLevel ;
          private String IsJointProd;
          private String mcgCode;
          private String featureNo ;
          private String MaterialBom ;
          private String costGroupingId ;
    }


}