package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_PackageBySubProQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_PackageBySubProQuery_OpenRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_PackageBySubProQuery_Open extends SPosBasicService<DCP_PackageBySubProQuery_OpenReq, DCP_PackageBySubProQuery_OpenRes> {
    @Override
    protected boolean isVerifyFail(DCP_PackageBySubProQuery_OpenReq req) throws Exception {
        boolean isFail = false;
        if(req.getRequest()==null)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "request节点不能为空!");
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_PackageBySubProQuery_OpenReq> getRequestType() {
        return new TypeToken<DCP_PackageBySubProQuery_OpenReq>(){};
    }

    @Override
    protected DCP_PackageBySubProQuery_OpenRes getResponseType() {
        return new DCP_PackageBySubProQuery_OpenRes();
    }

    @Override
    protected DCP_PackageBySubProQuery_OpenRes processJson(DCP_PackageBySubProQuery_OpenReq req) throws Exception {
        DCP_PackageBySubProQuery_OpenRes res = this.getResponse();
        DCP_PackageBySubProQuery_OpenRes.levelDatas datas = res.new levelDatas();
        datas.setGoodList(new ArrayList<>());
        List<DCP_PackageBySubProQuery_OpenReq.packageItem> packageItemList = req.getRequest().getPackageItems();
        if (packageItemList==null||packageItemList.isEmpty())
        {
            res.setDatas(datas);
            return res;
        }
        String eId = req.geteId();
        String withSql = "";
        String pluNos = "";
        for (DCP_PackageBySubProQuery_OpenReq.packageItem item : packageItemList)
        {
            if (item.getItemNo()==null||item.getItemNo().trim().isEmpty())
            {
                continue;
            }
            pluNos += item.getItemNo()+",";
        }
        MyCommon myCommon = new MyCommon();
        Map<String, String> map= new HashMap<>();
        map.put("DPLUNO", pluNos);
        withSql=myCommon.getFormatSourceMultiColWith(map);
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append(" with P as ("+withSql+") ");
        sqlbuf.append(" select A.PLUNO,B.DPLUNO,B.QTY,C.SUNIT,CL.PLU_NAME,UL.UNAME from dcp_pgoodsclass A ");
        sqlbuf.append(" inner join  dcp_pgoodsclass_detail B on A.EID=B.EID AND A.PLUNO=B.PLUNO AND A.PCLASSNO=B.PCLASSNO AND A.INVOWAY=B.INVOWAY ");
        sqlbuf.append(" inner join P  on B.DPLUNO=P.DPLUNO ");
        sqlbuf.append(" inner join dcp_goods C on A.EID=C.EID AND A.PLUNO=C.PLUNO ");
        sqlbuf.append(" left join dcp_goods_lang CL on C.EID=CL.EID AND C.PLUNO=CL.PLUNO AND CL.LANG_TYPE='zh_CN'");
        sqlbuf.append(" left join dcp_unit_lang UL on C.EID=UL.EID AND C.SUNIT=UL.UNIT AND UL.LANG_TYPE='zh_CN'");
        sqlbuf.append(" where A.EID='"+eId+"' and A.INVOWAY=1 ");//INVOWAY=1 ，必须
        String sql = sqlbuf.toString();
        HelpTools.writelog_fileName("【有赞查询套餐主商品】查询sql:"+sql,"DCP_PackageBySubProQuery_Open");
        List<Map<String,Object>> getQData = this.doQueryData(sql,null);
        Map<String,Boolean> conditionMap = new HashMap<>();
        conditionMap.put("PLUNO",true);
        conditionMap.put("DPLUNO",true);
        List<Map<String,Object>> getQData_distinct = MapDistinct.getMap(getQData,conditionMap);

        //循环传入的
        for (DCP_PackageBySubProQuery_OpenReq.packageItem item : packageItemList)
        {
            DCP_PackageBySubProQuery_OpenRes.level1Elm goodsItem = res.new level1Elm();
            goodsItem.setPackageItemNo(item.getItemNo());
            goodsItem.setPluNoList(new ArrayList<>());
            if (item.getItemNo()==null||item.getItemNo().trim().isEmpty())
            {
                continue;
            }
            Map<String,Object> itemNoMap = new HashMap<>();
            itemNoMap.put("DPLUNO",item.getItemNo());
            List<Map<String,Object>> getQData_itemNo = MapDistinct.getWhereMap(getQData_distinct,itemNoMap,true);
            if (getQData_itemNo!=null&&!getQData_itemNo.isEmpty())
            {
                for (Map<String,Object> par : getQData_itemNo)
                {
                    DCP_PackageBySubProQuery_OpenRes.level2Elm pluNoItem = res.new level2Elm();
                    pluNoItem.setPluNo(par.get("PLUNO").toString());
                    pluNoItem.setPluName(par.get("PLU_NAME").toString());
                    pluNoItem.setUnit(par.get("SUNIT").toString());
                    pluNoItem.setUnitName(par.get("UNAME").toString());
                    pluNoItem.setConversionRatio(par.get("QTY").toString());
                    goodsItem.getPluNoList().add(pluNoItem);

                }
            }
           datas.getGoodList().add(goodsItem);


        }

        res.setDatas(datas);
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_PackageBySubProQuery_OpenReq req) throws Exception {
        return null;
    }
}
