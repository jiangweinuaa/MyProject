package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_FinancialAuditQueryReq;
import com.dsc.spos.json.cust.res.DCP_FinancialAuditDetailRes;
import com.dsc.spos.json.cust.res.DCP_FinancialAuditQueryRes;
import com.dsc.spos.json.cust.res.DCP_GoodsBrandQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_FinancialAuditQuery extends SPosBasicService<DCP_FinancialAuditQueryReq, DCP_FinancialAuditQueryRes>
{

    @Override
    protected boolean isVerifyFail(DCP_FinancialAuditQueryReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if (req.getRequest()==null)
        {
            isFail = true;
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        String beginBDate=req.getRequest().getBeginBDate();
        String endBDate=req.getRequest().getEndBDate();
        String beginAuditDate=req.getRequest().getBeginAuditDate();
        String endAuditDate=req.getRequest().getEndAuditDate();

        if (Check.Null(beginBDate)==false && beginBDate.length()!=10)
        {
            errMsg.append("开始营业日期beginBDate必须是YYYY-MM-DD格式, ");
            isFail = true;
        }
        if (Check.Null(endBDate)==false && endBDate.length()!=10)
        {
            errMsg.append("截止营业日期endBDate必须是YYYY-MM-DD格式, ");
            isFail = true;
        }
        if (Check.Null(beginAuditDate)==false && beginAuditDate.length()!=10)
        {
            errMsg.append("开始稽核日期beginAuditDate必须是YYYY-MM-DD格式, ");
            isFail = true;
        }
        if (Check.Null(endAuditDate)==false && endAuditDate.length()!=10)
        {
            errMsg.append("截止稽核日期endAuditDate必须是YYYY-MM-DD格式, ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_FinancialAuditQueryReq> getRequestType()
    {
        return new TypeToken<DCP_FinancialAuditQueryReq>(){};
    }

    @Override
    protected DCP_FinancialAuditQueryRes getResponseType()
    {
        return new DCP_FinancialAuditQueryRes();
    }

    @Override
    protected DCP_FinancialAuditQueryRes processJson(DCP_FinancialAuditQueryReq req) throws Exception
    {
        DCP_FinancialAuditQueryRes res=this.getResponse();

        String shopId = req.getShopId();
        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();;
        String langType_cur = req.getLangType();

        String sql=getQuerySql(req);
        List<Map<String, Object>> getAudit =this.doQueryData(sql, null);
        int totalRecords=0;								//总笔数
        int totalPages=0;									//总页数
        res.setDatas(new ArrayList<DCP_FinancialAuditQueryRes.level1Elm>());

        StringBuffer sJoinEdate=new StringBuffer("");
        StringBuffer sJoinEid=new StringBuffer("");
        StringBuffer sJoinShop=new StringBuffer("");

        BigDecimal v_cardandcouponprepay=new BigDecimal(0);
        BigDecimal v_orderprepay=new BigDecimal(0);
        BigDecimal v_totamt=new BigDecimal(0);
        BigDecimal v_tcamt=new BigDecimal(0);
        BigDecimal v_merreceive=new BigDecimal(0);
        BigDecimal v_totamtaudit=new BigDecimal(0);
        BigDecimal v_diffamtaudit=new BigDecimal(0);
        BigDecimal v_diffamt=new BigDecimal(0);
        BigDecimal v_extraamt=new BigDecimal(0);

        if (getAudit != null && getAudit.isEmpty() == false)
        {
            String num = getAudit.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);

            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            for (Map<String, Object> oneData : getAudit)
            {
                DCP_FinancialAuditQueryRes.level1Elm lv1=res.new level1Elm();
                lv1.setShopId(oneData.get("SHOPID").toString());
                lv1.setShopName(oneData.get("ORG_NAME").toString());
                lv1.setAuditOpName(oneData.get("AUDITOPNAME").toString());
                lv1.setAuditOpno(oneData.get("AUDITOPNO").toString());
                lv1.setAuditTime(oneData.get("AUDITTIME").toString());
                lv1.setBDate(oneData.get("EDATE").toString());
                lv1.setIsAudit(oneData.get("ISAUDIT").toString());
                lv1.setCardAndCouponPrePay(new BigDecimal(oneData.get("CARDANDCOUPONPREPAY").toString()).doubleValue());
                lv1.setCustomerReturnAmt(new BigDecimal(oneData.get("CUSTOMERRETURNAMT").toString()).doubleValue());
                lv1.setDiffAmt(new BigDecimal(oneData.get("DIFFAMT").toString()).doubleValue());
                lv1.setMerreceive(new BigDecimal(oneData.get("MERRECEIVE").toString()).doubleValue());
                lv1.setDiffAmtAudit(new BigDecimal(oneData.get("DIFFAMT_AUDIT").toString()).doubleValue());
                lv1.setExportCount(new BigDecimal(oneData.get("EXPORTCOUNT").toString()).intValue());
                lv1.setExtraAmt(new BigDecimal(oneData.get("EXTRAAMT").toString()).doubleValue());
                lv1.setOrderAmt(new BigDecimal(oneData.get("ORDERAMT").toString()).doubleValue());
                lv1.setOrderPrePay(new BigDecimal(oneData.get("ORDERPREPAY").toString()).doubleValue());
                lv1.setRechargeAmt(new BigDecimal(oneData.get("RECHARGEAMT").toString()).doubleValue());
                lv1.setSaleAmt(new BigDecimal(oneData.get("SALEAMT").toString()).doubleValue());
                lv1.setSaleCardsAmt(new BigDecimal(oneData.get("SALECARDSAMT").toString()).doubleValue());
                lv1.setSaleCouponAmt(new BigDecimal(oneData.get("SALECOUPONAMT").toString()).doubleValue());
                lv1.setSendPayAmt(new BigDecimal(oneData.get("SENDPAY").toString()).doubleValue());
                lv1.setTcAmt(new BigDecimal(oneData.get("TCAMT").toString()).doubleValue());
                lv1.setTotAmt(new BigDecimal(oneData.get("TOTAMT").toString()).doubleValue());
                lv1.setTotAmtAudit(new BigDecimal(oneData.get("TOTAMT_AUDIT").toString()).doubleValue());
                lv1.setPayList(new ArrayList<>());


                //累加一下
                v_cardandcouponprepay=v_cardandcouponprepay.add(new BigDecimal(lv1.getCardAndCouponPrePay()));
                v_orderprepay=v_orderprepay.add(new BigDecimal(lv1.getOrderPrePay()));
                v_totamt=v_totamt.add(new BigDecimal(lv1.getTotAmt()));
                v_tcamt=v_tcamt.add(new BigDecimal(lv1.getTcAmt()));
                v_merreceive=v_merreceive.add(new BigDecimal(lv1.getMerreceive()));
                v_totamtaudit=v_totamtaudit.add(new BigDecimal(lv1.getTotAmtAudit()));
                v_diffamtaudit=v_diffamtaudit.add(new BigDecimal(lv1.getDiffAmtAudit()));
                v_diffamt=v_diffamt.add(new BigDecimal(lv1.getDiffAmt()));
                v_extraamt=v_extraamt.add(new BigDecimal(lv1.getExtraAmt()));


                sJoinEdate.append(oneData.get("EDATE").toString()+",");
                sJoinEid.append(oneData.get("EID").toString()+",");
                sJoinShop.append(oneData.get("SHOPID").toString()+",");

                res.getDatas().add(lv1);
            }

            //
            Map<String, String> mapOrder=new HashMap<String, String>();
            mapOrder.put("SHOPID", sJoinShop.toString());
            mapOrder.put("EDATE", sJoinEdate.toString());
            mapOrder.put("EID", sJoinEid.toString());
            //
            MyCommon cm=new MyCommon();
            String withasSql_Orderno=cm.getFormatSourceMultiColWith(mapOrder);
            mapOrder=null;

            //paychannelcode不为空且c.funcno!=200的会员类支付(储值卡、会员电子券)不能聚合累加
            StringBuffer sqlbuf=new StringBuffer(" with a AS ( "
                                                         + withasSql_Orderno + " ) "
                                                         + "select b.*,c.funcno from a " +
                                                         "inner join DCP_CLOSE_SHOPDETAIL b on a.eid=b.eid and a.shopid=b.shopid and a.edate=b.edate " +
                                                         "left join dcp_paytype c on b.eid=c.eid and b.paytype=c.paytype " +
                                                         "order by a.eid,a.shopid,a.edate,b.PAYTYPE ");


            List<Map<String, Object>> getAudit_detail =this.doQueryData(sqlbuf.toString(), null);
            if (getAudit_detail != null && getAudit_detail.isEmpty() == false)
            {

                for (DCP_FinancialAuditQueryRes.level1Elm data : res.getDatas())
                {
                    List<Map<String, Object>> temp=getAudit_detail.stream().filter(p-> p.get("SHOPID").toString().equals(data.getShopId()) && p.get("EDATE").toString().equals(data.getBDate())).collect(Collectors.toList());

                    //这个结果是要汇总，跟明细那个是一样的

                    //1、支付渠道为空的或者(支付渠道有值且c.funcno!=200）
                    List<Map<String, Object>> getQPayType=temp.stream().filter(p->p.get("PAYCHANNELCODE").toString().equals("") || (p.get("PAYCHANNELCODE").toString().equals("")==false && p.get("FUNCNO").toString().equals("200")==false)).collect(Collectors.toList());
                    for (Map<String, Object> map2 : getQPayType)
                    {
                        if (map2.get("EDATE").toString().equals(""))
                        {
                            continue;
                        }
                        DCP_FinancialAuditQueryRes.level2Elm lv2=res.new level2Elm();
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
                        data.getPayList().add(lv2);
                    }

                    //2、汇总PAYCHANNELCODE相同的,c.funcno==200
                    List<Map<String, Object>> getQChannel=temp.stream().filter(p->p.get("PAYCHANNELCODE").toString().equals("")==false && p.get("FUNCNO").toString().equals("200")).collect(Collectors.toList());
                    Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
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

                        DCP_FinancialAuditQueryRes.level2Elm lv2=res.new level2Elm();
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
                        data.getPayList().add(lv2);
                    }
                }
            }
        }

        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);

        res.setCardAndCouponPrePay(v_cardandcouponprepay.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
        res.setOrderPrePay(v_orderprepay.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
        res.setTotAmt(v_totamt.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
        res.setTcAmt(v_tcamt.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
        res.setMerreceive(v_merreceive.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
        res.setTotAmtAudit(v_totamtaudit.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
        res.setDiffAmtAudit(v_diffamtaudit.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
        res.setDiffAmt(v_diffamt.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
        res.setExtraAmt(v_extraamt.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());

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
    protected String getQuerySql(DCP_FinancialAuditQueryReq req) throws Exception
    {
        String sql = null;
        StringBuffer sqlbuf = new StringBuffer("");

        String shopId = req.getShopId();
        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();;
        String langType_cur = req.getLangType();

        String beginBDate=req.getRequest().getBeginBDate();
        String endBDate=req.getRequest().getEndBDate();
        String beginAuditDate=req.getRequest().getBeginAuditDate();
        String endAuditDate=req.getRequest().getEndAuditDate();
        String shopGroupNo=req.getRequest().getShopGroupNo();
        String[] shopList=req.getRequest().getShopList();
        String auditStatus=req.getRequest().getAuditStatus();
        String auditOpNo=req.getRequest().getAuditOpNo();
        String exportStatus=req.getRequest().getExportStatus();

        int pageNumber = req.getPageNumber();
        int pageSize = req.getPageSize();
        //分页起始位置
        int startRow=(pageNumber-1) * pageSize;


        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdf_Date = new SimpleDateFormat("yyyy-MM-dd");

        sqlbuf.append("select * from (" +
                              "select COUNT(t.eid ) OVER() NUM ,rownum rn, t.* " +
                              "from (" +
                              "select a.*,c.org_name from DCP_CLOSE_SHOP a " );

        if(shopGroupNo!=null && shopGroupNo.length()>0)
        {
            sqlbuf.append( "left join DCP_SHOPGROUP b on a.eid=b.eid and a.shopid=b.shopid ");
        }

        sqlbuf.append( "left join DCP_ORG_LANG c on a.eid=c.eid and a.shopid=c.organizationno and c.lang_type='"+langType_cur+"' " );

        if(shopList!=null && shopList.length>0)
        {
            StringBuffer sJoinShop=new StringBuffer("");
            StringBuffer sJoinEid=new StringBuffer("");

            for (String shop : shopList)
            {
                sJoinShop.append(shop+",");
                sJoinEid.append(eId+",");
            }
            //
            Map<String, String> mapShop=new HashMap<String, String>();
            mapShop.put("SHOPID", sJoinShop.toString());
            mapShop.put("EID", sJoinEid.toString());
            //
            MyCommon cm=new MyCommon();
            String withasSql_shop=cm.getFormatSourceMultiColWith(mapShop);
            mapShop=null;

            sqlbuf.append("inner join " +
                                  "( " + withasSql_shop +
                                  " ) d on a.eid=d.eid and a.shopid=d.shopid " );
        }

        sqlbuf.append("where a.eid='"+eId+"' ");


        if(beginBDate!=null && beginBDate.length()>0)
        {
            sqlbuf.append( "and a.edate>='"+sdf.format(sdf_Date.parse(beginBDate))+"' " );
        }
        if(endBDate!=null && endBDate.length()>0)
        {
            sqlbuf.append("and a.edate<='"+sdf.format(sdf_Date.parse(endBDate))+"' " );
        }
        if(beginAuditDate!=null && beginAuditDate.length()>0)
        {
            sqlbuf.append("and to_char(trunc(a.audittime),'yyyymmdd')>='"+sdf.format(sdf_Date.parse(beginAuditDate))+"' " );
        }
        if(endAuditDate!=null && endAuditDate.length()>0)
        {
            sqlbuf.append("and to_char(trunc(a.audittime),'yyyymmdd')<='"+sdf.format(sdf_Date.parse(endAuditDate))+"' " );
        }

        if(auditStatus!=null && auditStatus.length()>0)
        {
            sqlbuf.append("and a.isaudit='"+auditStatus+"' " );
        }
        if(auditOpNo!=null && auditOpNo.length()>0)
        {
            sqlbuf.append("and a.auditopno='"+auditOpNo+"' " );
        }
        if(exportStatus!=null && exportStatus.length()>0)
        {
            if (exportStatus.equals("Y"))
            {
                sqlbuf.append("and a.exportcount>0 " );
            }
            else
            {
                sqlbuf.append("and a.exportcount=0 " );
            }
        }
        if(shopGroupNo!=null && shopGroupNo.length()>0)
        {
            sqlbuf.append("and b.shopgroupno='"+shopGroupNo+"' " );
        }

        sqlbuf.append("order by a.edate desc,a.shopid) t" );
        sqlbuf.append(") where rn>"+startRow+" and rn<="+(startRow + pageSize));


        sql=sqlbuf.toString();

        return sql;
    }


}
