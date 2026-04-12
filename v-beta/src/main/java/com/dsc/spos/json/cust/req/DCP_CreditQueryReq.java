package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.*;

import java.util.List;

@Data
public class DCP_CreditQueryReq extends JsonBasicReq
{
    // 信用额度查询优化 2021/01/21 By wangzyc

	private level1Elm request;

	@Data
	public class level1Elm{
	    private String totPqty; // 合计录入数量
	    private String totAmt; // 合计零售金额
	    private String totDistriAmt; // 合计进货金额
	    private String utotDistriAmt; // 原合计进货金额；未参与促销不传
	    private List<level2ELm> goodsList; // 要货商品明细
    }

    @Data
    public class level2ELm{
        private String pluNo; // 商品编号
        private String featureNo; // 商品特征码
        private String category; // 商品分类编码
        private String punit; // 要货单位
        private String pqty; // 要货数量
        private String price ; // 零售价
        private String distriPrice; // 进货价
        private String uDistriPrice; // 原进货价；未参与促销不用传
        private String amt; // 金额
        private String distriAmt; // 进货金额
    }
	
	
}
