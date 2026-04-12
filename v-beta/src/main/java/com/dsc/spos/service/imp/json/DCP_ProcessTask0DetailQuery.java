package com.dsc.spos.service.imp.json;

import cn.hutool.core.convert.Convert;
import com.dsc.spos.json.cust.req.DCP_ProcessTask0DetailQueryReq;
import com.dsc.spos.json.cust.res.DCP_ProcessTask0DetailQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_ProcessTask0DetailQuery extends SPosBasicService<DCP_ProcessTask0DetailQueryReq, DCP_ProcessTask0DetailQueryRes>
{


    @Override
    protected boolean isVerifyFail(DCP_ProcessTask0DetailQueryReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        if (Check.Null(req.getRequest().geteId()))
        {
            errMsg.append("eId不能为空,");
            isFail = true;
        }

        if (Check.Null(req.getRequest().getOrganizationNo()))
        {
            errMsg.append("organizationNo不能为空,");
            isFail = true;
        }
        if (Check.Null(req.getRequest().getProcessTaskNo()))
        {
            errMsg.append("processTaskNo不能为空,");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ProcessTask0DetailQueryReq> getRequestType()
    {
        return new TypeToken<DCP_ProcessTask0DetailQueryReq>(){};
    }

    @Override
    protected DCP_ProcessTask0DetailQueryRes getResponseType()
    {
        return new DCP_ProcessTask0DetailQueryRes();
    }

    @Override
    protected DCP_ProcessTask0DetailQueryRes processJson(DCP_ProcessTask0DetailQueryReq req) throws Exception
    {
        DCP_ProcessTask0DetailQueryRes res=this.getResponseType();

        res.setDatas(res.new level1Elm());
        res.getDatas().setDataList(new ArrayList<>());


        String sql = this.getQuerySql(req);
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);

        //这是分摊的来源单
        String sql_detail_source=this.getQuerySql_Detail_Source(req);
        List<Map<String, Object>> getQData_detail_source=this.doQueryData(sql_detail_source, null);

        if (getQData != null && !getQData.isEmpty())
        {
            for (Map<String, Object> map : getQData)
            {
                DCP_ProcessTask0DetailQueryRes.level2Elm lv2=res.new level2Elm();
                lv2.setAmt(map.get("AMT").toString());
                lv2.setDistriAmt(map.get("DISTRIAMT").toString());
                lv2.setDistriPrice(map.get("DISTRIPRICE").toString());
                lv2.setDtBeginTime1(map.get("BEGIN_TIME1").toString());
                lv2.setDtBeginTime2(map.get("BEGIN_TIME2").toString());
                lv2.setDtBeginTime3(map.get("BEGIN_TIME3").toString());
                lv2.setDtBeginTime4(map.get("BEGIN_TIME4").toString());
                lv2.setDtBeginTime5(map.get("BEGIN_TIME5").toString());
                lv2.setDtBeginTime6(map.get("BEGIN_TIME6").toString());
                lv2.setDtBeginTime7(map.get("BEGIN_TIME7").toString());
                lv2.setDtBeginTime8(map.get("BEGIN_TIME8").toString());
                lv2.setDtBeginTime9(map.get("BEGIN_TIME9").toString());
                lv2.setDtBeginTime10(map.get("BEGIN_TIME10").toString());
                lv2.setDtEndTime1(map.get("END_TIME1").toString());
                lv2.setDtEndTime2(map.get("END_TIME2").toString());
                lv2.setDtEndTime3(map.get("END_TIME3").toString());
                lv2.setDtEndTime4(map.get("END_TIME4").toString());
                lv2.setDtEndTime5(map.get("END_TIME5").toString());
                lv2.setDtEndTime6(map.get("END_TIME6").toString());
                lv2.setDtEndTime7(map.get("END_TIME7").toString());
                lv2.setDtEndTime8(map.get("END_TIME8").toString());
                lv2.setDtEndTime9(map.get("END_TIME9").toString());
                lv2.setDtEndTime10(map.get("END_TIME10").toString());
                lv2.setDtName1(map.get("DTNAME1").toString());
                lv2.setDtName2(map.get("DTNAME2").toString());
                lv2.setDtName3(map.get("DTNAME3").toString());
                lv2.setDtName4(map.get("DTNAME4").toString());
                lv2.setDtName5(map.get("DTNAME5").toString());
                lv2.setDtName6(map.get("DTNAME6").toString());
                lv2.setDtName7(map.get("DTNAME7").toString());
                lv2.setDtName8(map.get("DTNAME8").toString());
                lv2.setDtName9(map.get("DTNAME9").toString());
                lv2.setDtName10(map.get("DTNAME10").toString());
                lv2.setDtNo1(map.get("DTNO1").toString());
                lv2.setDtNo2(map.get("DTNO2").toString());
                lv2.setDtNo3(map.get("DTNO3").toString());
                lv2.setDtNo4(map.get("DTNO4").toString());
                lv2.setDtNo5(map.get("DTNO5").toString());
                lv2.setDtNo6(map.get("DTNO6").toString());
                lv2.setDtNo7(map.get("DTNO7").toString());
                lv2.setDtNo8(map.get("DTNO8").toString());
                lv2.setDtNo9(map.get("DTNO9").toString());
                lv2.setDtNo10(map.get("DTNO10").toString());
                lv2.setpQty1(map.get("PQTY1").toString());
                lv2.setpQty2(map.get("PQTY2").toString());
                lv2.setpQty3(map.get("PQTY3").toString());
                lv2.setpQty4(map.get("PQTY4").toString());
                lv2.setpQty5(map.get("PQTY5").toString());
                lv2.setpQty6(map.get("PQTY6").toString());
                lv2.setpQty7(map.get("PQTY7").toString());
                lv2.setpQty8(map.get("PQTY8").toString());
                lv2.setpQty9(map.get("PQTY9").toString());
                lv2.setpQty10(map.get("PQTY10").toString());
                lv2.setItem(Convert.toInt(map.get("ITEM"), 0));
                lv2.setPluName(map.get("PLU_NAME").toString());
                lv2.setPluNo(map.get("PLUNO").toString());
                lv2.setpQty(Convert.toDouble(map.get("PQTY"),0d));
                lv2.setAdviceQty(Convert.toDouble(map.get("ADVICEQTY"),0d));
                lv2.setAskQty(Convert.toDouble(map.get("ASKQTY"),0d));
                lv2.setPrice(map.get("PRICE").toString());
                lv2.setpUName(map.get("UNAME").toString());
                lv2.setpUnit(map.get("PUNIT").toString());
                lv2.setSpec(map.get("SPEC").toString());
                lv2.setBaseQty(map.get("BASEQTY").toString());
                lv2.setBaseUnit(map.get("BASEUNIT").toString());
                lv2.setUnitRatio(map.get("UNIT_RATIO").toString());
                lv2.setMulQty(map.get("MUL_QTY").toString());
                lv2.setUnitUdLength(map.get("P_UDLENGTH").toString());
                lv2.setBaseUnitUdLength(map.get("BASE_UDLENGTH").toString());
                lv2.setFeatureNo(map.get("FEATURENO").toString());
                lv2.setFeatureName(map.get("FEATURENAME").toString());
                lv2.setBaseUnitName(map.get("BASEUNITNAME").toString());

                lv2.setSourceList(new ArrayList<>());

                //
                List<Map<String, Object>> getQData_detail_sourceTemp=getQData_detail_source.stream().filter(p->p.get("MITEM").toString().equals(lv2.getItem()+"")).collect(Collectors.toList());

                if (getQData_detail_sourceTemp != null && getQData_detail_sourceTemp.size()>0)
                {
                    for (Map<String, Object> detail_sourceMap : getQData_detail_sourceTemp)
                    {
                        DCP_ProcessTask0DetailQueryRes.level3Elm lv3=res.new level3Elm();
                        lv3.setAskQty(Convert.toDouble(detail_sourceMap.get("ASKQTY"),0d));
                        lv3.setShareQty(Convert.toDouble(detail_sourceMap.get("SHAREQTY"),0d));
                        lv3.setoFNo(detail_sourceMap.get("OFNO").toString());
                        lv3.setoItem(detail_sourceMap.get("OITEM").toString());
                        lv3.setoType(detail_sourceMap.get("OTYPE").toString());
                        lv3.setPluNo(detail_sourceMap.get("PLUNO").toString());
                        lv3.setpUName(detail_sourceMap.get("UNAME").toString());
                        lv3.setpUnit(detail_sourceMap.get("PUNIT").toString());
                        lv3.setUnitUdLength(detail_sourceMap.get("UDLENGTH").toString());
                        lv3.setoShop(detail_sourceMap.get("OSHOP").toString());
                        lv3.setoShopName(detail_sourceMap.get("ORG_NAME").toString());
                        lv3.setmItem(detail_sourceMap.get("MITEM").toString());

                        lv2.getSourceList().add(lv3);
                    }
                }
                res.getDatas().getDataList().add(lv2);
            }
        }

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
    protected String getQuerySql(DCP_ProcessTask0DetailQueryReq req) throws Exception
    {
        StringBuffer sqlbuf=new StringBuffer();

        sqlbuf.append("select a.*,b.plu_name,c.uname,d.spec,nvl(c1.udlength,3) P_UDLENGTH,nvl(c2.udlength,0) BASE_UDLENGTH,c3.UNAME BASEUNITNAME, " +
                              "d1.dtname dtname1,d1.begin_time begin_time1,d1.end_time end_time1, " +
                              "d2.dtname dtname2,d2.begin_time begin_time2,d2.end_time end_time2, " +
                              "d3.dtname dtname3,d3.begin_time begin_time3,d3.end_time end_time3, " +
                              "d4.dtname dtname4,d4.begin_time begin_time4,d4.end_time end_time4, " +
                              "d5.dtname dtname5,d5.begin_time begin_time5,d5.end_time end_time5, " +
                              "d6.dtname dtname6,d6.begin_time begin_time6,d6.end_time end_time6, " +
                              "d7.dtname dtname7,d7.begin_time begin_time7,d7.end_time end_time7, " +
                              "d8.dtname dtname8,d8.begin_time begin_time8,d8.end_time end_time8, " +
                              "d9.dtname dtname9,d9.begin_time begin_time9,d9.end_time end_time9, " +
                              "d10.dtname dtname10,d10.begin_time begin_time10,d10.end_time end_time10, " +
                              "c6.featurename " +
                              "from DCP_PROCESSTASK0_DETAIL a " +
                              "left join dcp_goods_lang b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='"+req.getLangType()+"' " +
                              "left join dcp_unit_lang c on a.eid=c.eid and a.punit=c.unit and c.lang_type='"+req.getLangType()+"' " +
                              "left join dcp_unit c1 on a.eid=c1.eid and a.punit=c1.unit " +
                              "left join dcp_unit c2 on a.eid=c2.eid and a.baseunit=c2.unit " +
                              "left join dcp_unit_lang c3 on a.eid=c3.eid and a.baseunit=c3.unit and c3.lang_type='"+req.getLangType()+"' " +
                              "left join dcp_goods d on a.eid=d.eid and a.pluno=d.pluno " +
                              "left join DCP_DINNERTIME d1 on d1.eid=a.eid and d1.shopid=a.shopid and d1.dtno=a.dtno1 " +
                              "left join DCP_DINNERTIME d2 on d2.eid=a.eid and d2.shopid=a.shopid and d2.dtno=a.dtno2 " +
                              "left join DCP_DINNERTIME d3 on d3.eid=a.eid and d3.shopid=a.shopid and d3.dtno=a.dtno3 " +
                              "left join DCP_DINNERTIME d4 on d4.eid=a.eid and d4.shopid=a.shopid and d4.dtno=a.dtno4 " +
                              "left join DCP_DINNERTIME d5 on d5.eid=a.eid and d5.shopid=a.shopid and d5.dtno=a.dtno5 " +
                              "left join DCP_DINNERTIME d6 on d6.eid=a.eid and d6.shopid=a.shopid and d6.dtno=a.dtno6 " +
                              "left join DCP_DINNERTIME d7 on d7.eid=a.eid and d7.shopid=a.shopid and d7.dtno=a.dtno7 " +
                              "left join DCP_DINNERTIME d8 on d8.eid=a.eid and d8.shopid=a.shopid and d8.dtno=a.dtno8 " +
                              "left join DCP_DINNERTIME d9 on d9.eid=a.eid and d9.shopid=a.shopid and d9.dtno=a.dtno9 " +
                              "left join DCP_DINNERTIME d10 on d10.eid=a.eid and d10.shopid=a.shopid and d10.dtno=a.dtno10 " +
                              "left join Dcp_Goods_Feature_Lang c6 on a.eid=c6.eid and a.pluno=c6.pluno and a.featureno=c6.featureno and c6.lang_type='"+req.getLangType()+"' " +
                              "where a.eid='"+req.getRequest().geteId()+"' " +
                              "and a.organizationno='"+req.getRequest().getOrganizationNo()+"' " +
                              "and a.PROCESSTASKNO='"+req.getRequest().getProcessTaskNo()+"' ");


        return sqlbuf.toString();

    }


    private String getQuerySql_Detail_Source(DCP_ProcessTask0DetailQueryReq req) throws Exception
    {
        StringBuffer sb=new StringBuffer();

        sb.append("select a.*,c.uname,c1.udlength,C2.ORG_NAME " +
                          "from DCP_PROCESSTASK0_SOURCE a " +
                          "left join dcp_unit_lang c on a.eid=c.eid and a.punit=c.unit and c.lang_type='"+req.getLangType()+"'  " +
                          "left join dcp_unit c1 on a.eid=c1.eid and a.punit=c1.unit  " +
                          "left join dcp_org_lang c2 on a.eid=c2.eid and a.shopid=c2.organizationno and c2.lang_type='"+req.getLangType()+"' " +
                          "where a.eid='"+req.getRequest().geteId()+"' " +
                          "and a.organizationno='"+req.getRequest().getOrganizationNo()+"' " +
                          "and a.processtaskno='"+req.getRequest().getProcessTaskNo()+"' ");

        return sb.toString();
    }


}
