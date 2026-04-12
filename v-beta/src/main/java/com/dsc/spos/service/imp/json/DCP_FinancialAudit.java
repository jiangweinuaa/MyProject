package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_FinancialAuditReq;
import com.dsc.spos.json.cust.res.DCP_FinancialAuditRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_FinancialAudit extends SPosAdvanceService<DCP_FinancialAuditReq, DCP_FinancialAuditRes>
{


    @Override
    protected void processDUID(DCP_FinancialAuditReq req, DCP_FinancialAuditRes res) throws Exception
    {
        String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String uptime =new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());

        String eId=req.geteId();
        String shopId=req.getRequest().getShopId();
        String bDate=req.getRequest().getBDate();
        String oprType=req.getRequest().getOprType();
        String auditOpno=req.getRequest().getAuditOpno();
        String auditOpName=req.getRequest().getAuditOpName();
        List<DCP_FinancialAuditReq.level2Elm> payList=req.getRequest().getPayList();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdf_Date = new SimpleDateFormat("yyyy-MM-dd");


        //前端手动新增的支付方式要先插入
        if (oprType.equals("1"))
        {
            List<DCP_FinancialAuditReq.level2Elm> insertPays=payList.stream().filter(p->"Y".equals(p.getIsNewPay())).collect(Collectors.toList());
            if (insertPays != null && insertPays.size()>0)
            {
                //找出最大ITEM
                int maxItem=0;
                String sql_maxItem="select NVL(MAX(ITEM),0) ITEM from DCP_CLOSE_SHOPDETAIL where eid='"+eId+"' and shopid='"+shopId+"' and edate='"+sdf.format(sdf_Date.parse(bDate))+"' ";
                List<Map<String, Object>> getData_maxItem=this.doQueryData(sql_maxItem, null);
                if (getData_maxItem != null && getData_maxItem.size()>0)
                {
                    maxItem=Integer.parseInt(getData_maxItem.get(0).get("ITEM").toString());
                }

                String[] columns_close_shop_detail =
                        {
                                "EID","SHOPID","EDATE","ITEM","PAYTYPE","PAYTYPENAME","PAYCHANNELCODE","PAYCHANNELCODENAME",
                                "PAYCODE","PAYCODEERP","ISCREDIT","TOTAMT","SENDPAY","EXTRAAMT","SALEAMT","ORDERAMT",
                                "RECHARGEAMT","SALECARDSAMT","SALECOUPONAMT","CUSTOMERRETURNAMT","MERDISCOUNT","THIRDDISCOUNT",
                                "CHARGEAMOUNT","TCAMT","DIFFAMT","ISAUDIT","TOTAMT_AUDIT","DIFFAMT_AUDIT","MERRECEIVE",
                                "ORDERFINALAMT","TRAN_TIME"
                        };
                for (DCP_FinancialAuditReq.level2Elm insertPay : insertPays)
                {
                    //查找支付方式映射
                    String sql_payment=" select a.paytype,a.paycode,b.payname,b.paycodeerp,c.payname paytypename from dcp_paytype a " +
                            " left join dcp_payment b on a.eid=b.eid and a.paycode=b.paycode " +
                            " left join dcp_paytype_lang c on a.eid=c.eid and a.paytype=c.paytype and c.lang_type='"+req.getLangType()+"' " +
                            " where a.eid='"+eId+"' and a.paytype='"+insertPay.getPayType()+"' ";
                    List<Map<String, Object>> getData_payment=this.doQueryData(sql_payment, null);


                    if (getData_payment == null || getData_payment.size()==0)
                    {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "新增的支付方式，找不到相关基础资料信息！");
                    }
                    String v_paycode=getData_payment.get(0).get("PAYCODE").toString();
                    //String v_payname=getData_payment.get(0).get("PAYNAME").toString();
                    String v_paycodeERP=getData_payment.get(0).get("PAYCODEERP").toString();
                    String v_payTypename=getData_payment.get(0).get("PAYTYPENAME").toString();

                    String sql="select * from DCP_CLOSE_SHOPDETAIL where eid='"+eId+"' and shopid='"+shopId+"' and edate='"+sdf.format(sdf_Date.parse(bDate))+"' and PAYTYPE='"+insertPay.getPayType()+"' ";
                    List<Map<String, Object>> getData=this.doQueryData(sql, null);
                    if (getData == null || getData.size()==0)
                    {
                        //
                        maxItem+=1;

                        DataValue[] insValue1 = null;

                        insValue1 = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(shopId, Types.VARCHAR),
                                new DataValue(sdf.format(sdf_Date.parse(bDate)), Types.VARCHAR),
                                new DataValue(maxItem, Types.VARCHAR),
                                new DataValue(insertPay.getPayType(), Types.VARCHAR),
                                new DataValue(v_payTypename, Types.VARCHAR),
                                new DataValue("", Types.VARCHAR),
                                new DataValue("", Types.VARCHAR),
                                new DataValue(v_paycode, Types.VARCHAR),
                                new DataValue(v_paycodeERP, Types.VARCHAR),
                                new DataValue("N", Types.VARCHAR),
                                new DataValue("0", Types.VARCHAR),
                                new DataValue("0", Types.VARCHAR),
                                new DataValue("0", Types.VARCHAR),
                                new DataValue("0", Types.VARCHAR),
                                new DataValue("0", Types.VARCHAR),
                                new DataValue("0", Types.VARCHAR),
                                new DataValue("0", Types.VARCHAR),
                                new DataValue("0", Types.VARCHAR),
                                new DataValue("0", Types.VARCHAR),
                                new DataValue("0", Types.VARCHAR),
                                new DataValue("0", Types.VARCHAR),
                                new DataValue("0", Types.VARCHAR),
                                new DataValue("0", Types.VARCHAR),
                                new DataValue("0", Types.VARCHAR),
                                new DataValue("N", Types.VARCHAR),
                                new DataValue("0", Types.VARCHAR),
                                new DataValue("0", Types.VARCHAR),
                                new DataValue("0", Types.VARCHAR),
                                new DataValue("0", Types.VARCHAR),
                                new DataValue(uptime, Types.VARCHAR)
                        };

                        InsBean ib1 = new InsBean("DCP_CLOSE_SHOPDETAIL", columns_close_shop_detail);
                        ib1.addValues(insValue1);
                        this.addProcessData(new DataProcessBean(ib1)); // 新增
                        //新增的支付方式先保存
                        this.doExecuteDataToDB();

                        //清空
                        this.pData.clear();
                    }
                }
            }
        }


        //给单头累加
        BigDecimal totamt=new BigDecimal(0);
        BigDecimal diffamt=new BigDecimal(0);

        //查询数据
        String sql="select * from DCP_CLOSE_SHOPDETAIL where eid='"+eId+"' and shopid='"+shopId+"' and edate='"+sdf.format(sdf_Date.parse(bDate))+"' ";
        List<Map<String, Object>> getData=this.doQueryData(sql, null);
        if (getData == null || getData.size()==0)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "根据传入的门店和日期，找不到数据！");
        }

        boolean b_Exist_Detail=false;

        for (DCP_FinancialAuditReq.level2Elm pay : payList)
        {
            List<Map<String, Object>> tempData=null;
            if (pay.getMethodType().equals("PAYTYPE"))
            {
                tempData=getData.stream().filter(p->p.get("PAYTYPE").toString().equals(pay.getPayType())).collect(Collectors.toList());
            }
            else
            {
                tempData=getData.stream().filter(p->p.get("PAYCHANNELCODE").toString().equals(pay.getPayType())).collect(Collectors.toList());
            }

            //找不到，就跳过了
            if (tempData == null && tempData.size()==0)
            {
               continue;
            }


            //
            if (oprType.equals("1"))
            {
                //
                totamt=totamt.add(new BigDecimal(pay.getTotAmtAudit()));
                diffamt=diffamt.add(new BigDecimal(pay.getDiffAmtAudit()));
            }

            //比如2笔资料分别是40、60
            //现在稽核金额是101，那么就直接分给第一笔41,等于说只更新第一笔，另1笔60不用更新

            //这个是分组的累加的
            BigDecimal bdm_group_diffamt=new BigDecimal("0");
            BigDecimal bdm_group_totamt=new BigDecimal("0");
            //从后往前
            for (int i = tempData.size()-1; i >=0; i--)
            {
                UptBean ub1 = new UptBean("DCP_CLOSE_SHOPDETAIL");

                ub1.addUpdateValue("ISAUDIT", new DataValue(oprType.equals("1")?"Y":"N",Types.VARCHAR));
                ub1.addUpdateValue("TRAN_TIME", new DataValue(uptime,Types.VARCHAR));

                //最后1笔
                if (i !=0)
                {
                    BigDecimal v_TOTAMT=new BigDecimal(tempData.get(i).get("TOTAMT").toString()).setScale(2,BigDecimal.ROUND_HALF_UP);
                    BigDecimal v_DIFFAMT=new BigDecimal(tempData.get(i).get("DIFFAMT").toString()).setScale(2,BigDecimal.ROUND_HALF_UP);

                    bdm_group_totamt=bdm_group_totamt.add(v_TOTAMT);
                    bdm_group_diffamt=bdm_group_diffamt.add(v_DIFFAMT);

                    if (oprType.equals("1"))
                    {
                        //稽核来源值
                        ub1.addUpdateValue("TOTAMT_AUDIT", new DataValue(v_TOTAMT, Types.FLOAT));
                        ub1.addUpdateValue("DIFFAMT_AUDIT", new DataValue(v_DIFFAMT,Types.FLOAT));
                    }
                    else
                    {
                        ub1.addUpdateValue("TOTAMT_AUDIT", new DataValue(0, Types.FLOAT));
                        ub1.addUpdateValue("DIFFAMT_AUDIT", new DataValue(0,Types.FLOAT));
                    }
                }
                else
                {
                    if (oprType.equals("1"))
                    {
                        ub1.addUpdateValue("TOTAMT_AUDIT", new DataValue(new BigDecimal(pay.getTotAmtAudit()).subtract(bdm_group_totamt).setScale(2,BigDecimal.ROUND_HALF_UP), Types.FLOAT));
                        ub1.addUpdateValue("DIFFAMT_AUDIT", new DataValue(new BigDecimal(pay.getDiffAmtAudit()).subtract(bdm_group_diffamt).setScale(2,BigDecimal.ROUND_HALF_UP),Types.FLOAT));
                    }
                    else
                    {
                        ub1.addUpdateValue("TOTAMT_AUDIT", new DataValue(0, Types.FLOAT));
                        ub1.addUpdateValue("DIFFAMT_AUDIT", new DataValue(0,Types.FLOAT));
                    }
                }

                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                ub1.addCondition("EDATE", new DataValue(sdf.format(sdf_Date.parse(bDate)), Types.VARCHAR));
                String item=tempData.get(i).get("ITEM").toString();
                ub1.addCondition("ITEM", new DataValue(item, Types.INTEGER));

                this.addProcessData(new DataProcessBean(ub1));

                b_Exist_Detail=true;
            }

        }

        if (!b_Exist_Detail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "稽核明细找不到相应的稽核数据！");
        }

        UptBean ub1 = new UptBean("DCP_CLOSE_SHOP");
        ub1.addUpdateValue("ISAUDIT", new DataValue(oprType.equals("1")?"Y":"N",Types.VARCHAR));
        ub1.addUpdateValue("AUDITOPNO", new DataValue(auditOpno,Types.VARCHAR));
        ub1.addUpdateValue("AUDITOPNAME", new DataValue(auditOpName,Types.VARCHAR));
        ub1.addUpdateValue("AUDITTIME", new DataValue(lastmoditime,Types.DATE));

        ub1.addUpdateValue("TOTAMT_AUDIT", new DataValue(totamt.setScale(2,BigDecimal.ROUND_HALF_UP),Types.FLOAT));
        ub1.addUpdateValue("DIFFAMT_AUDIT", new DataValue(diffamt.setScale(2,BigDecimal.ROUND_HALF_UP),Types.FLOAT));
        ub1.addUpdateValue("TRAN_TIME", new DataValue(uptime,Types.VARCHAR));

        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
        ub1.addCondition("EDATE", new DataValue(sdf.format(sdf_Date.parse(bDate)), Types.VARCHAR));
        this.addProcessData(new DataProcessBean(ub1));

        //
        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");
        return;

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_FinancialAuditReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_FinancialAuditReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_FinancialAuditReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_FinancialAuditReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if (req.getRequest()==null)
        {
            isFail = true;
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        String shopId=req.getRequest().getShopId();
        String bDate=req.getRequest().getBDate();
        String oprType=req.getRequest().getOprType();
        String auditOpno=req.getRequest().getAuditOpno();
        String auditOpName=req.getRequest().getAuditOpName();
        List<DCP_FinancialAuditReq.level2Elm> payList=req.getRequest().getPayList();
        if (Check.Null(shopId))
        {
            errMsg.append("shopId不能为空, ");
            isFail = true;
        }
        if (Check.Null(bDate))
        {
            errMsg.append("bDate不能为空, ");
            isFail = true;
        }
        if (Check.Null(bDate)==false && bDate.length()!=10)
        {
            errMsg.append("营业日期bDate必须是YYYY-MM-DD格式, ");
            isFail = true;
        }

        if (Check.Null(oprType))
        {
            errMsg.append("oprType不能为空, ");
            isFail = true;
        }
        if (Check.Null(oprType))
        {
            errMsg.append("auditOpno不能为空, ");
            isFail = true;
        }
        if (Check.Null(oprType))
        {
            errMsg.append("auditOpName不能为空, ");
            isFail = true;
        }
        if (payList==null || payList.size()==0)
        {
            errMsg.append("payList不能为空, ");
            isFail = true;
        }
        else
        {
            for (DCP_FinancialAuditReq.level2Elm pay : payList)
            {
                if (Check.Null(pay.getMethodType()))
                {
                    errMsg.append("methodType不能为空, ");
                    isFail = true;
                }
                if (Check.Null(pay.getPayType()))
                {
                    errMsg.append("payType不能为空, ");
                    isFail = true;
                }
            }
        }
        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_FinancialAuditReq> getRequestType()
    {
        return new TypeToken<DCP_FinancialAuditReq>(){};
    }

    @Override
    protected DCP_FinancialAuditRes getResponseType()
    {
        return new DCP_FinancialAuditRes();
    }

}
