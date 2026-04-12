package com.dsc.spos.json.cust.req;

import  com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.json.JSONFieldRequired;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 *
 * @author liyyd
 * @date 2025/02/21
 */
@Getter
@Setter
public class DCP_FeeCreateReq extends JsonBasicReq{

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request{
          @JSONFieldRequired(display="多语言")
          private List<Datas> datas;
          @JSONFieldRequired(display="费用编号")
          private String fee;
          @JSONFieldRequired(display="开票否：")
          private String isInvoiceIssued ;
          @JSONFieldRequired(display="费用类型")
          private String feeType;
          @JSONFieldRequired(display="费用性质")
          private String feeNature ;
          @JSONFieldRequired(display="税别")
          private String taxCode;
          @JSONFieldRequired(display="纳入结算否")
          private String inSettlement;
          @JSONFieldRequired(display="价款类别")
          private String priceCategory ;
          @JSONFieldRequired(display="费用名称")
          private String feeName;
          @JSONFieldRequired(display="核算制度")
          private String accountingPolicy ;
          private String isTourGroup;
          @JSONFieldRequired(display="状态")
          private String status;
          private String feeAllocation;
    }

    @Getter
    @Setter
    public class Datas{
		@JSONFieldRequired
		private boolean typecnfflg;
		@JSONFieldRequired(display="费用名称")
		private String feeName;
		@JSONFieldRequired(display="语言别")
		private String langType;
		@JSONFieldRequired(display="状态")
		private String status;
    }


}