package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_LightProGoodsQueryReq;
import com.dsc.spos.json.cust.res.DCP_LightProGoodsQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_LightProGoodsQuery extends SPosBasicService<DCP_LightProGoodsQueryReq, DCP_LightProGoodsQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_LightProGoodsQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_LightProGoodsQueryReq> getRequestType() {
        return new TypeToken<DCP_LightProGoodsQueryReq>(){};
    }

    @Override
    protected DCP_LightProGoodsQueryRes getResponseType() {
        return new DCP_LightProGoodsQueryRes();
    }

    @Override
    protected DCP_LightProGoodsQueryRes processJson(DCP_LightProGoodsQueryReq req) throws Exception {
        DCP_LightProGoodsQueryRes res = this.getResponse();
        res.setDatas(new ArrayList<>());
        if (req.getPageSize()==0)
        {
            req.setPageSize(20);
        }
        if (req.getPageNumber()==0)
        {
            req.setPageNumber(1);
        }
        int totalRecords = 0; //总笔数
        int totalPages = 0;
        String sql = this.getQuerySql(req);
        List<Map<String , Object>> getQDateList = this.doQueryData(sql, null);
        if (getQDateList != null && !getQDateList.isEmpty())
        {
            String num = getQDateList.get(0).get("NUM").toString();
            totalRecords=Integer.parseInt(num);

            //算總頁數
            totalPages = totalRecords / req.getPageSize();
            totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

            for (Map<String, Object> oneData : getQDateList)
            {
                DCP_LightProGoodsQueryRes.Data oneLv1 = res.new Data();
                oneLv1.setPluNo(oneData.get("PLUNO").toString());
                oneLv1.setPluName(oneData.get("PLU_NAME").toString());
                oneLv1.setShopNo(oneData.get("SHOPNO").toString());
                oneLv1.setShopName(oneData.get("ORG_NAME").toString());
                oneLv1.setUnitNo(oneData.get("UNIT").toString());
                oneLv1.setUnitName(oneData.get("UNAME").toString());
                oneLv1.setFeatureNo(oneData.get("FEATURENO").toString());
                oneLv1.setFeatureName(oneData.get("FEATURENAME").toString());
                oneLv1.setCreateOpId(oneData.get("CREATEOPID").toString());
                oneLv1.setCreateOpName(oneData.get("CREATEOPNAME").toString());
                oneLv1.setCreateTime(oneData.get("CREATETIME").toString());
                res.getDatas().add(oneLv1);
            }

        }

        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(totalRecords);
        res.setTotalPages(totalPages);
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_LightProGoodsQueryReq req) throws Exception {
        StringBuffer sqlBuffer = new StringBuffer("");
        String belfirm = "";
        String shopNo = "";
        String keyTxt = "";
        String langType = req.getLangType();
        String eId = req.geteId();
        if (langType==null||langType.isEmpty())
        {
            langType="zh_CN";
        }
        if (req.getRequest()!=null)
        {
            belfirm = req.getRequest().getBelfirm();
            shopNo = req.getRequest().getShopNo();
            keyTxt = req.getRequest().getKeyTxt();

        }
        int pageNumber=req.getPageNumber();
        int pageSize=req.getPageSize();
        //計算起啟位置
        int startRow=(pageNumber-1) * pageSize;
        int endRow = startRow+pageSize;
        sqlBuffer.append("select * from (");
        sqlBuffer.append(" select COUNT(*) OVER() NUM , row_number() OVER(ORDER BY A.PLUNO, A.SHOPNO) rn, A.*,AL.ORG_NAME,B.PLU_NAME,C.FEATURENAME,D.UNAME from DCP_LIGHTPROGOODS A");
        sqlBuffer.append(" left join DCP_ORG_LANG AL on A.EID=AL.EID AND A.SHOPNO=AL.ORGANIZATIONNO AND AL.LANG_TYPE='"+langType+"'");
        sqlBuffer.append(" left join DCP_GOODS_LANG B on A.EID=B.EID AND A.PLUNO=B.PLUNO AND B.LANG_TYPE='"+langType+"'");
        sqlBuffer.append(" left join DCP_GOODS_FEATURE_LANG C on A.EID=C.EID AND A.PLUNO=C.PLUNO AND A.FEATURENO=C.FEATURENO AND C.LANG_TYPE='"+langType+"'");
        sqlBuffer.append(" left join DCP_UNIT_LANG D on A.EID=D.EID AND A.UNIT=D.UNIT AND D.LANG_TYPE='"+langType+"'");
        sqlBuffer.append(" where A.EID='"+eId+"'");
        if (keyTxt!=null&&!keyTxt.isEmpty())
        {
            sqlBuffer.append(" and (A.PLUNO like '%%"+keyTxt+"%%' or B.PLU_NAME like '%%"+keyTxt+"%%')");
        }
        //门店如果不为空，不需要判断公司别了。
        if (shopNo!=null&&!shopNo.isEmpty())
        {
            sqlBuffer.append(" and A.SHOPNO='"+shopNo+"'");
        }
        else
        {
            if (belfirm!=null&&!belfirm.isEmpty())
            {
                sqlBuffer.append(" and A.SHOPNO in");
                sqlBuffer.append(" (select organizationno from dcp_org where eid='"+eId+"' and belfirm='"+belfirm+"')");
            }
        }
        sqlBuffer.append(") where rn>"+startRow+" and rn<="+endRow);

        return sqlBuffer.toString();
    }
}
