package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import com.dsc.spos.json.JSONFieldRequired;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 *
 * @author YapiGenerator自动生成
 * @date 2025/04/02
 */
@Getter
@Setter
public class DCP_CostAllocQueryRes extends JsonRes{

   private List<Datas> datas;

    @Getter
    @Setter
    public class Datas{
          private String accountId;
          private String period;
          private String year;
          private String costCenter;
          private String allocType;
          private List<AllocList> allocList;
          private String account;
          private String corp;
          private String corpName;
    }

    @Getter
    @Setter
    public class AllocList{
          private String Status;
          private String organizationID;
          private String deptProperty;
          private String allocSource;
          private String item;
          private String deptId;
          private String amt;
          private String dept;
          private String org_Name;
          private String allocFormula;
          private String allocWeight;
    }

}