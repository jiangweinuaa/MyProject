package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_FinancialAuditDetailReq;
import com.dsc.spos.json.cust.res.DCP_FinancialAuditDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_FinancialAuditDetail extends SPosBasicService<DCP_FinancialAuditDetailReq, DCP_FinancialAuditDetailRes>
{

    @Override
    protected boolean isVerifyFail(DCP_FinancialAuditDetailReq req) throws Exception
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
        String shopId=req.getRequest().getShopId();

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

        if (Check.Null(shopId))
        {
            errMsg.append("门店shopId不能为空, ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;

    }

    @Override
    protected TypeToken<DCP_FinancialAuditDetailReq> getRequestType()
    {
        return new TypeToken<DCP_FinancialAuditDetailReq>(){};
    }

    @Override
    protected DCP_FinancialAuditDetailRes getResponseType()
    {
        return new DCP_FinancialAuditDetailRes();
    }

    @Override
    protected DCP_FinancialAuditDetailRes processJson(DCP_FinancialAuditDetailReq req) throws Exception
    {
        DCP_FinancialAuditDetailRes res=this.getResponseType();

        String eId = req.geteId();;

        String sql=getQuerySql(req);
        List<Map<String, Object>> getAudit =this.doQueryData(sql, null);
        res.setDatas(res.new level1Elm());

        if (getAudit != null && getAudit.isEmpty() == false)
        {
            //单头主键字段
            Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
            condition.put("EID", true);
            condition.put("SHOPID", true);
            condition.put("EDATE", true);
            //调用过滤函数
            List<Map<String, Object>> getQHeader= MapDistinct.getMap(getAudit, condition);
            for (Map<String, Object> map : getQHeader)
            {
                String isaudit = map.get("ISAUDIT").toString();
                String auditopno = map.get("AUDITOPNO").toString();
                String auditopname = map.get("AUDITOPNAME").toString();
                String audittime = map.get("AUDITTIME").toString();

                DCP_FinancialAuditDetailRes.level1Elm lv1=res.new level1Elm();

                lv1.setAuditOpName(auditopname);
                lv1.setAuditOpno(auditopno);
                lv1.setIsAudit(isaudit);
                lv1.setPayList(new ArrayList<>());


                //1、支付渠道为空的或者(支付渠道有值且c.funcno!=200）
                List<Map<String, Object>> getQPayType=getAudit.stream().filter(p->p.get("PAYCHANNELCODE").toString().equals("") || (p.get("PAYCHANNELCODE").toString().equals("")==false && p.get("FUNCNO").toString().equals("200")==false)).collect(Collectors.toList());

                for (Map<String, Object> map2 : getQPayType)
                {
                    if (map2.get("EDATE").toString().equals(""))
                    {
                      continue;
                    }
                    DCP_FinancialAuditDetailRes.level2Elm lv2=res.new level2Elm();
                    lv2.setMethodType("PAYTYPE");
                    lv2.setDiffAmt(new BigDecimal(map2.get("DIFFAMT").toString()).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
                    lv2.setMerreceive(new BigDecimal(map2.get("MERRECEIVE").toString()).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
                    lv2.setDiffAmtAudit(new BigDecimal(map2.get("DIFFAMT_AUDIT").toString()).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
                    lv2.setExtraAmt(new BigDecimal(map2.get("EXTRAAMT").toString()).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
                    lv2.setPayName(map2.get("PAYTYPENAME").toString());
                    lv2.setPayType(map2.get("PAYTYPE").toString());
                    lv2.setTcAmt(new BigDecimal(map2.get("TCAMT").toString()).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
                    lv2.setTotAmt(new BigDecimal(map2.get("TOTAMT").toString()).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
                    lv2.setTotAmtAudit(new BigDecimal(map2.get("TOTAMT_AUDIT").toString()).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
                    lv2.setTotAmtAudit_ref(new BigDecimal(lv2.getMerreceive()).add(new BigDecimal(lv2.getDiffAmt())).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());

                    lv1.getPayList().add(lv2);
                }

                //2、汇总PAYCHANNELCODE相同的,c.funcno==200
                List<Map<String, Object>> getQChannel=getAudit.stream().filter(p->p.get("PAYCHANNELCODE").toString().equals("")==false && p.get("FUNCNO").toString().equals("200")).collect(Collectors.toList());
                condition = new HashMap<String, Boolean>(); //查询条件
                condition.put("PAYCHANNELCODE", true);
                //调用过滤函数
                List<Map<String, Object>> getQHeader_Ch= MapDistinct.getMap(getQChannel, condition);
                for (Map<String, Object> map2 : getQHeader_Ch)
                {
                    if (map2.get("PAYCHANNELCODE").toString().equals(""))
                    {
                        continue;
                    }

                    List<Map<String, Object>> tempData=getQChannel.stream().filter(p->p.get("PAYCHANNELCODE").toString().equals(map2.get("PAYCHANNELCODE").toString())).collect(Collectors.toList());

                    DCP_FinancialAuditDetailRes.level2Elm lv2=res.new level2Elm();
                    lv2.setMethodType("PAYCHANNEL");
                    lv2.setDiffAmt(new BigDecimal(tempData.stream().mapToDouble(p->new BigDecimal(p.get("DIFFAMT").toString()).doubleValue()).sum()).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
                    lv2.setMerreceive(new BigDecimal(tempData.stream().mapToDouble(p->new BigDecimal(p.get("MERRECEIVE").toString()).doubleValue()).sum()).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
                    lv2.setDiffAmtAudit(new BigDecimal(tempData.stream().mapToDouble(p->new BigDecimal(p.get("DIFFAMT_AUDIT").toString()).doubleValue()).sum()).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
                    lv2.setExtraAmt(new BigDecimal(tempData.stream().mapToDouble(p->new BigDecimal(p.get("EXTRAAMT").toString()).doubleValue()).sum()).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
                    lv2.setPayName(map2.get("PAYCHANNELCODENAME").toString());
                    lv2.setPayType(map2.get("PAYCHANNELCODE").toString());
                    lv2.setTcAmt(new BigDecimal(tempData.stream().mapToDouble(p->new BigDecimal(p.get("TCAMT").toString()).doubleValue()).sum()).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
                    lv2.setTotAmt(new BigDecimal(tempData.stream().mapToDouble(p->new BigDecimal(p.get("TOTAMT").toString()).doubleValue()).sum()).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
                    lv2.setTotAmtAudit(new BigDecimal(tempData.stream().mapToDouble(p->new BigDecimal(p.get("TOTAMT_AUDIT").toString()).doubleValue()).sum()).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
                    lv2.setTotAmtAudit_ref(new BigDecimal(lv2.getMerreceive()).add(new BigDecimal(lv2.getDiffAmt())).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());

                    lv1.getPayList().add(lv2);
                }

                res.setDatas(lv1);

                //这里单头1天只会有一笔，直接跳出吧
                break;
            }
        }

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception
    {

    }

    @Override
    protected String getQuerySql(DCP_FinancialAuditDetailReq req) throws Exception
    {
        String eId = req.geteId();
        String bDate=req.getRequest().getBDate();
        String shopId=req.getRequest().getShopId();

        String sql = null;
        StringBuffer sqlbuf = new StringBuffer("");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdf_Date = new SimpleDateFormat("yyyy-MM-dd");


        sqlbuf.append("select a.isaudit,a.auditopno,a.auditopname,a.audittime,b.*,c.funcno from DCP_CLOSE_SHOP a " +
                              "left join DCP_CLOSE_SHOPDETAIL b on a.eid=b.eid and a.shopid=b.shopid and a.edate=b.edate " +
                              "left join dcp_paytype c on b.eid=c.eid and b.paytype=c.paytype " +
                              "where a.eid='"+eId+"' and a.edate='"+sdf.format(sdf_Date.parse(bDate))+"' and a.shopid='"+shopId+"' " +
                              "order by a.eid,a.shopid,a.edate,b.PAYTYPE ");



        sql=sqlbuf.toString();

        return sql;
    }

}
