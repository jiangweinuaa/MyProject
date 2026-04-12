package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_FinancialAuditExportGet_u8cReq;
import com.dsc.spos.json.cust.res.DCP_FinancialAuditExportGet_u8cRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.waimai.sftc.shop;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;


import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_FinancialAuditExportGet_u8c extends SPosBasicService<DCP_FinancialAuditExportGet_u8cReq, DCP_FinancialAuditExportGet_u8cRes>
{


    @Override
    protected boolean isVerifyFail(DCP_FinancialAuditExportGet_u8cReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if (req.getRequest()==null)
        {
            isFail = true;
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        String bDate=req.getRequest().getBDate();
        String opNo=req.getRequest().getOpNo();
        List<DCP_FinancialAuditExportGet_u8cReq.level2Elm> shoplist=req.getRequest().getShopList();

        if (Check.Null(bDate))
        {
            errMsg.append("营业日期bDate不能为空, ");
            isFail = true;
        }

        if (Check.Null(bDate)==false && bDate.length()!=10)
        {
            errMsg.append("营业日期bDate必须是YYYY-MM-DD格式, ");
            isFail = true;
        }

        if (Check.Null(opNo))
        {
            errMsg.append("opNo不能为空, ");
            isFail = true;
        }

        if (shoplist != null && shoplist.size()>0)
        {
            for (DCP_FinancialAuditExportGet_u8cReq.level2Elm shop : shoplist)
            {
                if (Check.Null(shop.getShopId()))
                {
                    errMsg.append("shopList节点下shopId不能为空, ");
                    isFail = true;
                }
            }
        }
        else
        {
            errMsg.append("shopList节点不能为空, ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;

    }

    @Override
    protected TypeToken<DCP_FinancialAuditExportGet_u8cReq> getRequestType()
    {
        return new TypeToken<DCP_FinancialAuditExportGet_u8cReq>(){};
    }

    @Override
    protected DCP_FinancialAuditExportGet_u8cRes getResponseType()
    {
        return new DCP_FinancialAuditExportGet_u8cRes();
    }

    @Override
    protected DCP_FinancialAuditExportGet_u8cRes processJson(DCP_FinancialAuditExportGet_u8cReq req) throws Exception
    {
        String uptime =new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());

        String ctime =new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        SimpleDateFormat year_sdf = new SimpleDateFormat("yyyy");
        SimpleDateFormat month_sdf = new SimpleDateFormat("MM");
        SimpleDateFormat day_sdf = new SimpleDateFormat("dd");
        SimpleDateFormat sdf_Date = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        DCP_FinancialAuditExportGet_u8cRes res=this.getResponseType();

        String eId = req.geteId();

        String bDate=req.getRequest().getBDate();
        String opNo=req.getRequest().getOpNo();
        List<DCP_FinancialAuditExportGet_u8cReq.level2Elm> shoplist=req.getRequest().getShopList();

        String opno_out="";

        //1、操作人映射检核：
        String sql_opno="SELECT OPNO_OUT FROM DCP_VOUCHER_EMP WHERE EID='"+eId+"' AND OPNO='"+opNo+"' ";
        List<Map<String, Object>> getDataOpno =this.doQueryData(sql_opno, null);
        if (getDataOpno == null || getDataOpno.size()==0)
        {
            res.setDatas(null);
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription("opno需要做映射！");
            return res;
        }
        else
        {
            opno_out=getDataOpno.get(0).get("OPNO_OUT").toString();
        }

        //多门店，暂时先这样，等调试完数据在优化
        String multi_shop="";
        StringBuffer sb=new StringBuffer();
        for (DCP_FinancialAuditExportGet_u8cReq.level2Elm shop : shoplist)
        {
            sb.append("'"+shop.getShopId()+"',");
        }
        multi_shop=sb.delete(sb.length()-1,sb.length()).toString();
        //2、数据必须稽核检核：
        String sql_Audit="SELECT SHOPID FROM Dcp_Close_Shop WHERE EID='"+eId+"' AND SHOPID IN ("+multi_shop+") AND EDATE='"+sdf.format(sdf_Date.parse(bDate))+"' and isaudit<>'Y'  ";
        List<Map<String, Object>> getDataAudit =this.doQueryData(sql_Audit, null);
        if (getDataAudit != null && getDataAudit.size()>0)
        {
            StringBuffer error_sb=new StringBuffer();
            for (Map<String, Object> map : getDataAudit)
            {
                error_sb.append("门店:"+map.get("SHOPID").toString()+"数据未稽核,");
            }
            res.setDatas(null);
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription(error_sb.toString());
            return res;
        }
        //3、稽核明细支付款别必须映射凭证录入：
        String sql_VoucherEntry="select * from ( " +
                "select distinct EID, " +
                "CASE WHEN PAYCHANNELCODE IS NULL THEN N'PAYTYPE' ELSE N'PAYCHANNEL' END AS METHODTYPE, " +
                "CASE WHEN PAYCHANNELCODE IS NULL THEN PAYTYPE else PAYCHANNELCODE end as paytype, " +
                "CASE WHEN PAYCHANNELCODE IS NULL THEN PAYTYPENAME else PAYCHANNELCODENAME end as PAYNAME " +
                "FROM DCP_CLOSE_SHOPDETAIL  WHERE EID='"+eId+"' AND SHOPID IN ("+multi_shop+") AND EDATE='"+sdf.format(sdf_Date.parse(bDate))+"' " +
                ") A " +
                "LEFT JOIN (SELECT * FROM DCP_VOUCHERENTRY WHERE VOUCHERTYPE=1 ) B ON A.EID=B.EID AND A.METHODTYPE=B.ENTRYTYPE AND A.PAYTYPE=B.ENTRYID " +
                "WHERE B.EID IS NULL  ";
        List<Map<String, Object>> getDataVoucherEntry =this.doQueryData(sql_VoucherEntry, null);
        if (getDataVoucherEntry != null && getDataVoucherEntry.size()>0)
        {
            StringBuffer error_sb=new StringBuffer();
            for (Map<String, Object> map : getDataVoucherEntry)
            {
                error_sb.append("DCP_CLOSE_SHOPDETAIL表:"+map.get("METHODTYPE").toString()+"="+map.get("PAYTYPE").toString()+","+map.get("PAYNAME").toString()+"的数据在DCP_VOUCHERENTRY表不存在,");
            }
            res.setDatas(null);
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription(error_sb.toString());
            return res;
        }
        //4、固定收费科目必须做映射：
        String sql_Account="select COUNT(*) NUM from DCP_ACCOUNTINGSUBJECT where eid='"+eId+"' and subjectid in ('600101','630106','220503','220501') ";
        List<Map<String, Object>> getDataAccount =this.doQueryData(sql_Account, null);
        if (getDataAccount == null || getDataAccount.size()==0)
        {
            res.setDatas(null);
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription("固定收费科目编号:600101、630106、220503、220501必须存在！");
            return res;
        }
        else
        {
            if (!getDataAccount.get(0).get("NUM").toString().equals("4"))
            {
                res.setDatas(null);
                res.setSuccess(false);
                res.setServiceStatus("100");
                res.setServiceDescription("固定收费科目编号:600101、630106、220503、220501必须存在！");
                return res;
            }
        }
        //5、外部机构必须做映射：
        String sql_VoucherOrg="select ORGID,ORGID_OUT,ORGNAME_OUT,COMPID_OUT,COMPNAME_OUT from DCP_VOUCHER_ORG where eid='"+eId+"' and orgid in ("+multi_shop+") ";
        List<Map<String, Object>> getDataVoucherOrg =this.doQueryData(sql_VoucherOrg, null);
        if (getDataVoucherOrg != null && getDataVoucherOrg.size()>0)
        {
            for (DCP_FinancialAuditExportGet_u8cReq.level2Elm shop : shoplist)
            {
                List<Map<String, Object>> tempData=getDataVoucherOrg.stream().filter(p->p.get("ORGID").toString().equals(shop.getShopId())).collect(Collectors.toList());
                if (tempData == null || tempData.size()==0)
                {
                    res.setDatas(null);
                    res.setSuccess(false);
                    res.setServiceStatus("100");
                    res.setServiceDescription("门店:"+shop.getShopId()+"必须做外部机构映射！");
                    return res;
                }
                else
                {
                    for (Map<String, Object> tempDatum : tempData)
                    {
                        String ORGID_OUT=tempDatum.get("ORGID_OUT").toString();
                        String ORGNAME_OUT=tempDatum.get("ORGNAME_OUT").toString();
                        String COMPID_OUT=tempDatum.get("COMPID_OUT").toString();
                        String COMPNAME_OUT=tempDatum.get("COMPNAME_OUT").toString();
                        if (Check.Null(ORGID_OUT) || Check.Null(ORGNAME_OUT)||Check.Null(COMPID_OUT)||Check.Null(COMPNAME_OUT))
                        {
                            res.setDatas(null);
                            res.setSuccess(false);
                            res.setServiceStatus("100");
                            res.setServiceDescription("门店:"+shop.getShopId()+"外部机构映射字段ORGID_OUT、ORGNAME_OUT、COMPID_OUT、COMPNAME_OUT都有值！");
                            return res;
                        }
                    }
                }
            }
        }
        else
        {
            res.setDatas(null);
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription("入参门店必须做外部机构映射！");
            return res;
        }


        //开始汇总查询
        String sql=getQuerySql(req);
        List<Map<String, Object>> getAudit =this.doQueryData(sql, null);

        //
        if (getAudit == null || getAudit.size()==0)
        {
            res.setDatas(null);
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription("查不到资料！");
            return res;
        }


        //
        DCP_FinancialAuditExportGet_u8cRes.level1Elm lv1=res.new level1Elm();
        lv1.setProp_billtype("gl");
        lv1.setProp_filename(ctime);
        lv1.setProp_isexchange("Y");
        lv1.setProp_operation("req");
        lv1.setProp_proc("add");
        lv1.setProp_receiver(getAudit.get(0).get("COMPNAME_OUT").toString());
        lv1.setProp_replace("Y");
        lv1.setProp_roottag("voucher");
        lv1.setProp_sender("1101");

        DCP_FinancialAuditExportGet_u8cRes.levelVoucher voucher=res.new levelVoucher();
        voucher.setProp_id("");
        //head
        DCP_FinancialAuditExportGet_u8cRes.levelVoucher_Head voucher_head=res.new levelVoucher_Head();
        voucher_head.setFiscal_year(year_sdf.format(sdf_Date.parse(bDate)));
        voucher_head.setAccounting_period(month_sdf.format(sdf_Date.parse(bDate)));
        voucher_head.setAttachment_number("0");
        voucher_head.setCompany(lv1.getProp_receiver());
        voucher_head.setDate(bDate);
        voucher_head.setEnter(opno_out);
        voucher_head.setSignature("N");
        voucher_head.setVoucher_id("0");
        voucher_head.setVoucher_making_system("外部系统交换平台");
        voucher_head.setVoucher_type("销售凭证");
        voucher.setVoucher_head(voucher_head);
        //body
        DCP_FinancialAuditExportGet_u8cRes.levelVoucher_Body voucher_body=res.new levelVoucher_Body();
        List<DCP_FinancialAuditExportGet_u8cRes.levelVoucher_Entry> entryList=new ArrayList<>();

        //单头主键字段
        Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
        condition.put("SHOPID", true);
        //调用过滤函数
        List<Map<String, Object>> getQHeader= MapDistinct.getMap(getAudit, condition);

        //从1开始编号，在凭证内唯一
        int entry_id=1;

        for (Map<String, Object> map : getQHeader)
        {
            List<Map<String, Object>> getAuditShopdetail=getAudit.stream().filter(p->p.get("SHOPID").toString().equals(map.get("SHOPID").toString())).collect(Collectors.toList());

            if (getAuditShopdetail != null && getAuditShopdetail.size()>0)
            {
                for (Map<String, Object> map_shopdetail : getAuditShopdetail)
                {

                    DCP_FinancialAuditExportGet_u8cRes.levelVoucher_Entry entry=res.new levelVoucher_Entry();
                    //[001]宿州路店04月27日销售收入
                    entry.setAbstract("["+map_shopdetail.get("ORGID_OUT").toString()+"]" +map_shopdetail.get("ORGNAME_OUT").toString()+month_sdf.format(sdf_Date.parse(bDate))+"月"+day_sdf.format(sdf_Date.parse(bDate))+"日销售收入");
                    entry.setAccount_code(map_shopdetail.get("SUBJECTID").toString());
                    entry.setBDate(bDate);
                    entry.setCompId_out(map_shopdetail.get("COMPID_OUT").toString());
                    entry.setCompName_out(map_shopdetail.get("COMPNAME_OUT").toString());
                    entry.setCredit_quantity("0");
                    entry.setCurrency("CNY");
                    entry.setEntry_id(entry_id+"");

                    entry.setOrgId_out(map_shopdetail.get("ORGID_OUT").toString());
                    entry.setOrgName_out(map_shopdetail.get("ORGNAME_OUT").toString());

                    //
                    BigDecimal primary_debit_amount=new BigDecimal("0");
                    String debitorcredit=map_shopdetail.get("DEBITORCREDIT").toString();
                    String direction=map_shopdetail.get("DIRECTION").toString();

                    //DCP_VOUCHERENTRY表录入凭证对应的科目编号不存在
                    if (!direction.equals("1") && !direction.equals("-1"))
                    {
                        res.setDatas(null);
                        res.setSuccess(false);
                        res.setServiceStatus("100");
                        res.setServiceDescription("凭证录入DCP_VOUCHERENTRY表字段subjectid的值:"+map_shopdetail.get("SUBJECTID").toString()+"对应的科目编号不存在！");
                        return res;
                    }

                    BigDecimal totalamt=new BigDecimal(map_shopdetail.get("TOTAMT_AUDIT").toString());
                    if (debitorcredit.equals("1"))
                    {
                        primary_debit_amount=totalamt.multiply(new BigDecimal(debitorcredit)).multiply(new BigDecimal(direction));
                    }
                    entry.setPrimary_debit_amount(primary_debit_amount.toPlainString());

                    //
                    BigDecimal primary_credit_amount=new BigDecimal("0");
                    if (debitorcredit.equals("-1"))
                    {
                        primary_credit_amount=totalamt.multiply(new BigDecimal(debitorcredit)).multiply(new BigDecimal(direction));
                    }
                    entry.setPrimary_credit_amount(primary_credit_amount.toPlainString());

                    //
                    BigDecimal natural_debit_currency=new BigDecimal("0");
                    if (debitorcredit.equals("1"))
                    {
                        natural_debit_currency=totalamt.multiply(new BigDecimal(debitorcredit)).multiply(new BigDecimal(direction));
                    }
                    entry.setNatural_debit_currency(natural_debit_currency.toPlainString());

                    //
                    BigDecimal natural_credit_currency=new BigDecimal("0");
                    if (debitorcredit.equals("-1"))
                    {
                        natural_credit_currency=totalamt.multiply(new BigDecimal(debitorcredit)).multiply(new BigDecimal(direction));
                    }
                    entry.setNatural_credit_currency(natural_credit_currency.toPlainString());

                    entry.setSecondary_credit_amount("0");
                    entry.setSecondary_debit_amount("0");
                    entry.setShopId(map_shopdetail.get("SHOPID").toString());

                    //
                    String auxiType=map_shopdetail.get("AUXILIARYTYPE").toString();
                    if (auxiType.equals("CUST"))
                    {
                        DCP_FinancialAuditExportGet_u8cRes.levelAuxiliary auxiliary=res.new levelAuxiliary();
                        DCP_FinancialAuditExportGet_u8cRes.levelAuxiliary_Item auxiliaryItem=res. new levelAuxiliary_Item();
                        auxiliaryItem.setProp_name("客商辅助");
                        auxiliaryItem.setValue_text(map_shopdetail.get("CUSTOMERNO").toString());
                        auxiliary.setItem(auxiliaryItem);
                        entry.setAuxiliary_accounting(auxiliary);
                    }
                    if (auxiType.equals("DEPT"))
                    {
                        DCP_FinancialAuditExportGet_u8cRes.levelAuxiliary auxiliary=res.new levelAuxiliary();
                        DCP_FinancialAuditExportGet_u8cRes.levelAuxiliary_Item auxiliaryItem=res. new levelAuxiliary_Item();
                        auxiliaryItem.setProp_name("部门档案");
                        auxiliaryItem.setValue_text(map_shopdetail.get("ORGID_OUT").toString());
                        auxiliary.setItem(auxiliaryItem);
                        entry.setAuxiliary_accounting(auxiliary);
                    }

                    //原币贷方发生额,用友导入原币金额必须不能为0
                    if (primary_credit_amount.compareTo(BigDecimal.ZERO)==0 && primary_debit_amount.compareTo(BigDecimal.ZERO)==0)
                    {
                        continue;
                    }

                    entryList.add(entry);

                    entry_id+=1;
                }
            }
        }

        voucher_body.setEntry(entryList);

        voucher.setVoucher_body(voucher_body);
        lv1.setVoucher(voucher);

        res.setDatas(lv1);

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception
    {

    }

    @Override
    protected String getQuerySql(DCP_FinancialAuditExportGet_u8cReq req) throws Exception
    {
        SimpleDateFormat sdf_Date = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        String eId=req.geteId();
        String bDate=req.getRequest().getBDate();
        String opNo=req.getRequest().getOpNo();
        List<DCP_FinancialAuditExportGet_u8cReq.level2Elm> shoplist=req.getRequest().getShopList();

        //多门店，暂时先这样，等调试完数据在优化
        String multi_shop="";
        StringBuffer sb=new StringBuffer();
        for (DCP_FinancialAuditExportGet_u8cReq.level2Elm shop : shoplist)
        {
            sb.append("'"+shop.getShopId()+"',");
        }
        multi_shop=sb.delete(sb.length()-1,sb.length()).toString();

        String sql = null;
        StringBuffer sqlbuf = new StringBuffer("SELECT  AA.*,BB.SUBJECTNAME,BB.DIRECTION,BB.AUXILIARYTYPE,CC.ORGID_OUT,CC.ORGNAME_OUT,CC.COMPID_OUT,CC.COMPNAME_OUT " +
                                                       "FROM " +
                                                       "( " +
                                                       "select eid,shopid,edate,isaudit,CUSTOMERNO,SUBJECTID,DEBITORCREDIT ,sum(TOTAMT_AUDIT) as TOTAMT_AUDIT " +
                                                       "from " +
                                                       "( " +
                                                       "SELECT A.EID,A.SHOPID,A.EDATE,A.METHODTYPE,A.PAYTYPE,ISAUDIT,SUM(A.TOTAMT_AUDIT) AS TOTAMT_AUDIT,A.CUSTOMERNO,NVL(B.SUBJECTID,N'') AS SUBJECTID ,B.DEBITORCREDIT " +
                                                       "FROM " +
                                                       "( " +
                                                       "SELECT A.EID,A.SHOPID,A.EDATE, " +
                                                       "CASE WHEN A.PAYCHANNELCODE IS NULL THEN N'PAYTYPE' ELSE N'PAYCHANNEL' END AS METHODTYPE, " +
                                                       "CASE WHEN A.PAYCHANNELCODE IS NULL THEN A.PAYTYPE else A.PAYCHANNELCODE end as paytype, " +
                                                       "A.ISAUDIT, " +
                                                       "CASE WHEN NVL(A.ISCREDIT,N'N') = 'N' THEN A.TOTAMT_AUDIT  ELSE B.CREDITAMT END AS TOTAMT_AUDIT, " +
                                                       "CASE WHEN NVL(A.ISCREDIT,N'N') = 'N' THEN N'' ELSE B.CUSTOMERNO END AS CUSTOMERNO " +
                                                       "FROM DCP_CLOSE_SHOPDETAIL A " +
                                                       "LEFT JOIN DCP_CLOSE_SHOPDETAIL_CREDIT B ON A.EID=B.EID AND A.SHOPID=B.SHOPID AND A.EDATE=B.EDATE AND A.ITEM=B.UPITEM AND A.ISCREDIT='Y' " +
                                                       "WHERE A.EID='"+eId+"' AND A.SHOPID in("+multi_shop+") AND A.EDATE='"+sdf.format(sdf_Date.parse(bDate))+"' and A.ISAUDIT='Y' " +
                                                       ") A " +
                                                       "LEFT JOIN (SELECT * FROM DCP_VOUCHERENTRY WHERE VOUCHERTYPE=1 ) B ON A.EID=B.EID AND A.METHODTYPE=B.ENTRYTYPE AND A.PAYTYPE=B.ENTRYID " +
                                                       "GROUP BY A.EID,A.SHOPID,A.EDATE,A.METHODTYPE,A.PAYTYPE,A.ISAUDIT,A.CUSTOMERNO,B.SUBJECTID,B.DEBITORCREDIT " +
                                                       "UNION ALL " +
                                                       "SELECT EID,SHOPID,EDATE,N'' AS METHODTYPE,N'' AS PAYTYPE,ISAUDIT ,MERRECEIVE-CARDANDCOUPONPREPAY-ORDERPREPAY,N'' AS CUSTOMERNO,N'600101' AS SUBJECTID,-1 AS DEBIRORCREDIT " +
                                                       "FROM DCP_CLOSE_SHOP " +
                                                       "WHERE EID='"+eId+"' AND SHOPID in("+multi_shop+") AND EDATE='"+sdf.format(sdf_Date.parse(bDate))+"' AND ISAUDIT='Y' " +
                                                       "UNION ALL " +
                                                       "SELECT EID,SHOPID,EDATE,N'' AS METHODTYPE,N'' AS PAYTYPE,ISAUDIT  ,DIFFAMT_AUDIT,N'' AS CUSTOMERNO,N'630106' AS SUBJECTID,-1 AS DEBIRORCREDIT " +
                                                       "FROM DCP_CLOSE_SHOP " +
                                                       "WHERE EID='"+eId+"' AND SHOPID in("+multi_shop+") AND EDATE='"+sdf.format(sdf_Date.parse(bDate))+"' AND ISAUDIT='Y' " +
                                                       "UNION ALL " +
                                                       "SELECT EID,SHOPID,EDATE,N'' AS METHODTYPE,N'' AS PAYTYPE,ISAUDIT,CARDANDCOUPONPREPAY,N'' AS CUSTOMERNO,N'220503' AS SUBJECTID,-1 AS DEBIRORCREDIT " +
                                                       "FROM DCP_CLOSE_SHOP " +
                                                       "WHERE EID='"+eId+"' AND SHOPID in("+multi_shop+") AND EDATE='"+sdf.format(sdf_Date.parse(bDate))+"' AND ISAUDIT='Y' " +
                                                       "UNION ALL " +
                                                       "SELECT EID,SHOPID,EDATE,N'' AS METHODTYPE,N'' AS PAYTYPE,ISAUDIT ,ORDERPREPAY,N'' AS CUSTOMERNO,N'220501' AS SUBJECTID,-1 AS DEBIRORCREDIT " +
                                                       "FROM DCP_CLOSE_SHOP " +
                                                       "WHERE EID='"+eId+"' AND SHOPID in("+multi_shop+") AND EDATE='"+sdf.format(sdf_Date.parse(bDate))+"' AND ISAUDIT='Y' " +
                                                       ")tmp " +
                                                       "group by eid,shopid,edate,isaudit,CUSTOMERNO,SUBJECTID,DEBITORCREDIT " +
                                                       ") AA " +
                                                       "LEFT JOIN DCP_ACCOUNTINGSUBJECT BB ON AA.EID=BB.EID AND AA.SUBJECTID=BB.SUBJECTID " +
                                                       "LEFT JOIN DCP_VOUCHER_ORG CC ON AA.EID=CC.EID AND AA.SHOPID=CC.ORGID " +
                                                       "order by AA.eid,AA.shopid,AA.edate ");


        sql=sqlbuf.toString();

        return sql;
    }


}
