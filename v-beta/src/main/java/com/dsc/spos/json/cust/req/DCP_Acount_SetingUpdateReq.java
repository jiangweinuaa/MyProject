package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

/**
 * @author YapiGenerator自动生成
 * @date 2025/03/17
 */
@Getter
@Setter
public class DCP_Acount_SetingUpdateReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "启用期")
        private String enableDate;
        @JSONFieldRequired(display = "财产编号自动编码否 ")
        private String isPropAutoCode;
        @JSONFieldRequired(display = "资金关帐日")
        private String fundCloseDate;
        @JSONFieldRequired(display = "归属法人")
        private String corp;
        @JSONFieldRequired(display = "在制人工制费转出规则")
        private String wipLaborCostRule;
        @JSONFieldRequired(display = " 固资现行月 ")
        private String assetCurMth;
        @JSONFieldRequired(display = "现行年度")
        private String currentYear;
        @JSONFieldRequired(display = "成本关帐日")
        private String costClosingDate;
        private String apProvisionalTaxType;
        @JSONFieldRequired(display = "账套编号")
        private String accountID;
        @JSONFieldRequired(display = "付款日设置 ")
        private String apPaymentDate;
        @JSONFieldRequired(display = "应收账款日期 ")
        private String arAcctDate;
        @JSONFieldRequired(display = "转入资产清理科目认列损益否 ")
        private String isAssetDispProfit;
        @JSONFieldRequired(display = "应收帐款结账日设定 ")
        private String arClosingDateSet;
        @JSONFieldRequired(display = "应付帐款参数 ")
        private String apParameter;
        @JSONFieldRequired(display = "是否按采购核销订金 ")
        private String isDepositByPurch;
        @JSONFieldRequired(display = "资金现行期别")
        private String fundCurPeriod;
        @JSONFieldRequired(display = "关帐日期")
        private String closingDate;
        @JSONFieldRequired(display = "应付关账日 ")
        private String apClosingDate;
        @JSONFieldRequired(display = "租入资产以使用权资产认列")
        private String leasedAssetRecog;
        @JSONFieldRequired(display = "固资关账日")
        private String assetClosingDate;
        @JSONFieldRequired(display = "应收帐款参数 ")
        private String arParameter;
        @JSONFieldRequired(display = "减值准备回冲否 ")
        private String isImpairmentReverse;
        @JSONFieldRequired(display = "销退是否影响成本")
        private String isSaleRetInflCost;
        @JSONFieldRequired(display = "资产调拨价格设置")
        private String assetTransfPrice;
        @JSONFieldRequired(display = "帐套类型")
        private String acctType;
        @JSONFieldRequired(display = "结算方式")
        private String paymentMethod;
        @JSONFieldRequired(display = "")
        private String status;
        @JSONFieldRequired(display = "固资现行年")
        private String assetCurYear;
        @JSONFieldRequired(display = "人工制费收集规则")
        private String laborCostRule;
        @JSONFieldRequired(display = "应收关账日")
        private String arClosingDate;
        @JSONFieldRequired(display = "卡片自动编码否")
        private String isCardAutoCode;
        @JSONFieldRequired(display = "财产编号与资产编号一致否 ")
        private String isPropAssetCodeMatch;
        @JSONFieldRequired(display = "库存现行年度")
        private String invCurrentYear;
        @JSONFieldRequired(display = "调拨纳入成本计算否")
        private String isTransferInCost;
        @JSONFieldRequired(display = "折旧科目取值")
        private String depreciationItem;
        @JSONFieldRequired(display = "应付账款日期 ")
        private String apAcctDate;
        @JSONFieldRequired(display = "应付帐款结账日设定 ")
        private String apClosingDateSet;
        @JSONFieldRequired(display = "币种")
        private String currency;
        @JSONFieldRequired(display = "库存现行期别")
        private String invCurrentperiod;
        @JSONFieldRequired(display = "现行期别")
        private String currentPeriod;
        private String laborCostAlloc;
        @JSONFieldRequired(display = "转入资产清理否 ")
        private String isAssetDispTransfer;
        @JSONFieldRequired(display = "是否按订单核销订金 ")
        private String isDepositByOrder;
        @JSONFieldRequired(display = "应付暂估立税选项 ")
        private String apProvisionalTaxOpt;
        @JSONFieldRequired(display = "成本现行年度")
        private String costCurrentYear;
        @JSONFieldRequired(display = "库存关帐日")
        private String invClosingDate;
        @JSONFieldRequired(display = "收款日设置 ")
        private String arReceiptDate;
        @JSONFieldRequired(display = "成本现行期别")
        private String costCurrentMth;
        @JSONFieldRequired(display = "帐套名称")
        private String account;
        @JSONFieldRequired(display = "资金现行年度")
        private String fundCurYear;
        @JSONFieldRequired(display = "人工制费分摊否")
        private String isLaborcoStalloc;
        @JSONFieldRequired(display = "科目参照表")
        private String coaRefID;
        @JSONFieldRequired(display = "汇率参照表")
        private String fXRefID;
        @JSONFieldRequired(display = "汇率类型")
        private String fxType;
        @JSONFieldRequired(display = "汇率来源")
        private String fxSource;
    }

}