package com.dsc.spos.service.imp.json;

import cn.hutool.core.convert.Convert;
import com.dsc.spos.json.cust.req.DCP_ProcessPOrderQueryReq;
import com.dsc.spos.json.cust.res.DCP_ProcessPOrderQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_ProcessPOrderQuery extends SPosBasicService<DCP_ProcessPOrderQueryReq, DCP_ProcessPOrderQueryRes>
{

    @Override
    protected boolean isVerifyFail(DCP_ProcessPOrderQueryReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        if(Check.Null(req.getRequest().getRDateBegin()))
        {
            errMsg.append("rDateBegin不可为空值, ");
            isFail = true;
        }

        if(Check.Null(req.getRequest().getRDateEnd()))
        {
            errMsg.append("rDateEnd不可为空值, ");
            isFail = true;
        }

        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }

    @Override
    protected TypeToken<DCP_ProcessPOrderQueryReq> getRequestType()
    {
        return new TypeToken<DCP_ProcessPOrderQueryReq>(){};
    }

    @Override
    protected DCP_ProcessPOrderQueryRes getResponseType()
    {
        return new DCP_ProcessPOrderQueryRes();
    }

    @Override
    protected DCP_ProcessPOrderQueryRes processJson(DCP_ProcessPOrderQueryReq req) throws Exception
    {
        DCP_ProcessPOrderQueryRes res=this.getResponseType();

        int totalRecords = 0; //总笔数
        int totalPages = 0;

        //查詢資料
        String sql = this.getQuerySql_Count(req);
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        res.setDatas(res.new level1Elm());
        res.getDatas().setDataList(new ArrayList<>());

        if (getQData != null && getQData.isEmpty() == false) // 有資料，取得詳細內容
        {
            String num = getQData.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);

            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            StringBuffer sJoinOrderno=new StringBuffer("");
            StringBuffer sJoinEid=new StringBuffer("");
            StringBuffer sJoinShop=new StringBuffer("");
            for (Map<String, Object> tempmap : getQData)
            {
                sJoinOrderno.append(tempmap.get("PORDERNO").toString()+",");
                sJoinEid.append(tempmap.get("EID").toString()+",");
                sJoinShop.append(tempmap.get("SHOPID").toString()+",");
            }

            //
            Map<String, String> mapOrder=new HashMap<String, String>();
            mapOrder.put("SHOPID", sJoinShop.toString());
            mapOrder.put("PORDERNO", sJoinOrderno.toString());
            mapOrder.put("EID", sJoinEid.toString());
            //
            MyCommon cm=new MyCommon();
            String withasSql_Orderno=cm.getFormatSourceMultiColWith(mapOrder);
            mapOrder=null;

            if (withasSql_Orderno.equals(""))
            {
                res.setSuccess(false);
                res.setServiceDescription("查询失败--单号转换成临时表的方法处理失败！");
                return res;
            }

            String sql_detail=this.getQuerySql_Detail(req,withasSql_Orderno);
            List<Map<String, Object>> getQData_Detail = this.doQueryData(sql_detail, null);

            if (getQData_Detail != null && getQData_Detail.size()>0)
            {
                //去重
                List<Map<String, Object>> getQData_Header=getQData_Detail.stream().filter(PosPub.distinctByKeys(p->p.get("SHOPID").toString(),p->p.get("PORDERNO").toString())).collect(Collectors.toList());
                if (getQData_Header != null && getQData_Header.size()>0)
                {
                    for (Map<String, Object> tempmap : getQData_Header)
                    {
                        DCP_ProcessPOrderQueryRes.level2Elm lv2=res.new level2Elm();
                        lv2.setMemo(tempmap.get("MEMO").toString());
                        lv2.setOShop(tempmap.get("SHOPID").toString());
                        lv2.setOShopName(tempmap.get("ORG_NAME").toString());
                        lv2.setPOrderNo(tempmap.get("PORDERNO").toString());
                        lv2.setPTemplateName(tempmap.get("PTEMPLATE_NAME").toString());
                        lv2.setPTemplateNo(tempmap.get("PTEMPLATENO").toString());
                        lv2.setRDate(tempmap.get("RDATE").toString());
                        lv2.setTotCQty(Convert.toDouble(tempmap.get("TOT_CQTY"),0d));
                        lv2.setTotPQty(Convert.toDouble(tempmap.get("TOT_PQTY"),0d));
                        lv2.setDetailList(new ArrayList<>());

                        List<Map<String, Object>> getQData_Detail_temp=getQData_Detail.stream().filter(p->p.get("SHOPID").toString().equals(tempmap.get("SHOPID").toString()) && p.get("PORDERNO").toString().equals(tempmap.get("PORDERNO").toString())).collect(Collectors.toList());
                        if (getQData_Detail_temp != null && getQData_Detail_temp.size()>0)
                        {
                            for (Map<String, Object> map : getQData_Detail_temp)
                            {
                                DCP_ProcessPOrderQueryRes.level3Elm lv3=res.new level3Elm();
                                lv3.setItem(map.get("ITEM").toString());
                                lv3.setPluNo(map.get("PLUNO").toString());
                                lv3.setPUnit(map.get("PUNIT").toString());
                                lv3.setPUName(map.get("UNAME").toString());
                                lv3.setUnitUdLength(map.get("UDLENGTH").toString());
                                lv3.setPQty(Convert.toDouble(map.get("PQTY"),0d));
                                lv3.setProductQty(Convert.toDouble(map.get("PRODUCTQTY"),0d));
                                lv3.setPluName(map.get("PLU_NAME").toString());
                                lv3.setSpec(map.get("SPEC").toString());
                                lv3.setPrice(map.get("PRICE").toString());
                                lv3.setDistriPrice(map.get("DISTRIPRICE").toString());
                                lv3.setMulQty(map.get("MUL_QTY").toString());
                                lv3.setBaseQty(map.get("BASEQTY").toString());
                                lv3.setBaseUnit(map.get("BASEUNIT").toString());
                                lv3.setUnitRatio(map.get("UNIT_RATIO").toString());
                                lv3.setBaseUnitUdLength(map.get("BASE_UDLENGTH").toString());
                                lv3.setFeatureNo(map.get("FEATURENO").toString());
                                lv3.setFeatureName(map.get("FEATURENAME").toString());

                                lv2.getDetailList().add(lv3);
                            }
                        }
                        res.getDatas().getDataList().add(lv2);
                    }

                }

            }


        }

        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);

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
    protected String getQuerySql(DCP_ProcessPOrderQueryReq req) throws Exception
    {
        return null;
    }


    private String getQuerySql_Count(DCP_ProcessPOrderQueryReq req) throws Exception
    {
        StringBuffer sb=new StringBuffer();

        sb.append("select * from ( " +
                          "select count(*) over() num, row_number() OVER (ORDER BY a.rdate,a.shopid,a.porderno) AS rn,a.eid,a.rdate,a.shopid,a.porderno from ( " +
                          "select distinct a.eid,a.rdate,a.porderno,a.shopid " +
                          "from dcp_porder a " +
                          "inner join dcp_porder_detail b on a.eid=b.eid and a.shopid=b.shopid and a.porderno=b.porderno " +
                          "where a.eid='"+req.geteId()+"' " +
                          "and b.pqty-nvl(b.productqty,0)>0 " +
                          "and a.RECEIPT_ORG='"+req.getOrganizationNO()+"' " +
                          "and a.rdate>='"+req.getRequest().getRDateBegin()+"' " +
                          "and a.rdate<='"+req.getRequest().getRDateEnd()+"' ");

        if (!Check.Null(req.getRequest().getPTemplateNo()))
        {
            sb.append("and a.ptemplateno='"+req.getRequest().getPTemplateNo()+"' ");
        }

        if (!Check.Null(req.getRequest().getOShop()))
        {
            sb.append("and a.shopid='"+req.getRequest().getOShop()+"' ");
        }

        sb.append(") a ");
        sb.append(") where rn>0 and rn<=10 ");

        return sb.toString();
    }


    private String getQuerySql_Detail(DCP_ProcessPOrderQueryReq req,String withasSql) throws Exception
    {
        StringBuffer sqlbuf=new StringBuffer(" with p AS ( "
                                                     + withasSql + " ) "
                                                     + "select a.porderno,a.shopid,c.org_name,a.rdate,a.ptemplateno,d.ptemplate_name,a.memo,a.tot_pqty,a.tot_cqty, " +
                                                     "b.item,b.pluno,b.punit,b.pqty,nvl(b.productqty,0) productqty,c1.udlength,c2.uname,c3.plu_name,c4.spec, " +
                                                     "b.price,b.distriprice,b.MUL_QTY,b.BASEQTY,b.BASEUNIT,b.UNIT_RATIO,nvl(c5.udlength,0) BASE_UDLENGTH,b.FEATURENO, " +
                                                     "c6.featurename " +
                                                     "from p " +
                                                     "inner join dcp_porder a on a.eid=p.eid and a.shopid=p.shopid and a.porderno=p.porderno " +
                                                     "inner join dcp_porder_detail b on a.eid=b.eid and a.shopid=b.shopid and a.porderno=b.porderno " +
                                                     "left join dcp_org_lang c on a.eid=c.eid and a.shopid=c.organizationno and c.lang_type='"+req.getLangType()+"' " +
                                                     "left join dcp_ptemplate d on a.eid=d.eid and a.ptemplateno=d.ptemplateno and d.doc_type='0' " +
                                                     "left join dcp_unit c1 on a.eid=c1.eid and b.punit=c1.unit " +
                                                     "left join dcp_unit_lang c2 on a.eid=c2.eid and b.punit=c2.unit and c2.lang_type='"+req.getLangType()+"' " +
                                                     "left join dcp_goods_lang c3 on a.eid=c3.eid and b.pluno=c3.pluno and c3.lang_type='"+req.getLangType()+"' " +
                                                     "left join dcp_goods c4 on a.eid=c4.eid and b.pluno=c4.pluno " +
                                                     "left join dcp_unit c5 on a.eid=c5.eid and b.baseunit=c5.unit " +
                                                     "left join Dcp_Goods_Feature_Lang c6 on a.eid=c6.eid and b.pluno=c6.pluno and b.featureno=c6.featureno and c6.lang_type='"+req.getLangType()+"' " +
                                                     "where b.pqty-nvl(b.productqty,0)>0 " +
                                                     "and a.rdate>='"+req.getRequest().getRDateBegin()+"' " +
                                                     "and a.rdate<='"+req.getRequest().getRDateEnd()+"' " );

        if (!Check.Null(req.getRequest().getPTemplateNo()))
        {
            sqlbuf.append("and a.ptemplateno='"+req.getRequest().getPTemplateNo()+"' " );

        }
        if (!Check.Null(req.getRequest().getOShop()))
        {
            sqlbuf.append("and a.shopid='"+req.getRequest().getOShop()+"' " );
        }

        sqlbuf.append("and a.RECEIPT_ORG='"+req.getShopId()+"' " +
                              "order by a.rdate,a.shopid,b.item ");


        return sqlbuf.toString();
    }

}
