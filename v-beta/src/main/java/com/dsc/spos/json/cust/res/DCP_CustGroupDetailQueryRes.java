package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import com.dsc.spos.json.JSONFieldRequired;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 *
 * @author YapiGenerator自动生成
 * @date 2025/03/11
 */
@Getter
@Setter
public class DCP_CustGroupDetailQueryRes extends JsonRes{

   private List<Datas> datas;

    @Getter
    @Setter
    public class Datas{
          private String lastModiOpId;
          private String lastModiOpName;
          private String createDeptId;
          private String createOpId;
          private String createDeptName;
          private String createOpName;
          private String memo;
          private String custGroupNo;
          private String lastModiTime;
          private String createTime;
          private List<CustomerList> customerList;
          private List<Detail> detail;
          private String custGroupName;
          private String status;
    }

    @Getter
    @Setter
    public class CustomerList{
          private String beginDate;
          private String fName;
          private String endDate;
          private String sName;
          private String customerNo;
          private String status;
    }

    @Getter
    @Setter
    public class Detail{
          private String item;
          private String attrId;
          private String attrType;
          private String attrName;
          private String status;
    }


}