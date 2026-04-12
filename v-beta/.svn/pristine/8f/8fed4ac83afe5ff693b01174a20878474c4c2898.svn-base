package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_FinancialAuditQueryRes extends JsonRes
{
    private List<level1Elm> datas;

    private double cardAndCouponPrePay;//卡券预付金额
    private double orderPrePay;//订金预付金额
    private double totAmt;//应收金额，含溢收，不含找零
    private double extraAmt;//溢收金额
    private double tcAmt;//交班录入金额/应缴金额
    private double diffAmt;//交班差异金额
    private double merreceive;//商户实收金额
    private double totAmtAudit;//稽核金额
    private double diffAmtAudit;//稽核差额

    @Data
    public class level1Elm
    {
        private String shopId;
        private String shopName;
        private String bDate;
        private double cardAndCouponPrePay;//卡券预付金额
        private double orderPrePay;//订金预付金额
        private double totAmt;//应收金额，含溢收，不含找零
        private double extraAmt;//溢收金额
        private double tcAmt;//交班录入金额/应缴金额
        private double diffAmt;//交班差异金额
        private double merreceive;//商户实收金额
        private double totAmtAudit;//稽核金额
        private double diffAmtAudit;//稽核差额
        private String isAudit;//是否已稽核Y/N
        private String auditOpno;//稽核人编码
        private String auditOpName;//稽核人名称
        private String auditTime;//稽核时间 YYYY-MM-DD HH:MM:SS
        private int exportCount;//导出次数
        private double sendPayAmt;//卡赠送金额抵扣
        private double saleAmt;//销售单收款金额
        private double orderAmt;//订单收款金额
        private double rechargeAmt;//卡充值收款金额
        private double saleCardsAmt;//售卡收款金额
        private double saleCouponAmt;//售券收款金额
        private double customerReturnAmt;//大客户回款金额
        private List<level2Elm> payList;
    }

    @Data
    public class level2Elm
    {
        private String methodType;//
        private String payType;//
        private String payName;//
        private double totAmt;//
        private double extraAmt;//
        private double tcAmt;//
        private double diffAmt;//
        private double merreceive;//
        private double totAmtAudit_ref;//
        private double totAmtAudit;//
        private double diffAmtAudit;//
    }


}
