package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_SubStockTakeGoodsDetailReq;
import com.dsc.spos.json.cust.res.DCP_SubStockTakeGoodsDetailRes;
import com.dsc.spos.json.cust.res.DCP_SubStockTakeGoodsDetailRes.level1Elm;
import com.dsc.spos.json.cust.res.DCP_SubStockTakeGoodsDetailRes.level2Elm;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * 服务函数：DCP_SubStockTakeGoodsDetail
 * 服务说明：商品录入历程
 * @author jinzma
 * @since  2021-03-05
 */
public class DCP_SubStockTakeGoodsDetail extends SPosBasicService<DCP_SubStockTakeGoodsDetailReq, DCP_SubStockTakeGoodsDetailRes> {
    @Override
    protected boolean isVerifyFail(DCP_SubStockTakeGoodsDetailReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        String subStockTakeNo = req.getRequest().getSubStockTakeNo();
        String pluNo = req.getRequest().getPluNo();
        String featureNo = req.getRequest().getFeatureNo();

        if (Check.Null(subStockTakeNo)) {
            errMsg.append("盘点子任务单号不能为空,");
            isFail = true;
        }
        if (Check.Null(pluNo)) {
            errMsg.append("商品编码不能为空,");
            isFail = true;
        }
        if (Check.Null(featureNo)) {
            errMsg.append("特征码不能为空,");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_SubStockTakeGoodsDetailReq> getRequestType() {
        return new TypeToken<DCP_SubStockTakeGoodsDetailReq>(){};
    }

    @Override
    protected DCP_SubStockTakeGoodsDetailRes getResponseType() {
        return new DCP_SubStockTakeGoodsDetailRes();
    }

    @Override
    protected DCP_SubStockTakeGoodsDetailRes processJson(DCP_SubStockTakeGoodsDetailReq req) throws Exception {
        DCP_SubStockTakeGoodsDetailRes res = this.getResponse ();
        level1Elm datas = res.new level1Elm();
        try {
            String sql=this.getQuerySql(req);
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            if (getQData != null && !getQData.isEmpty()) {
                datas.setTrackList(new ArrayList<level2Elm>());
                for (Map<String, Object> oneData : getQData){
                    level2Elm trackList = res.new level2Elm();

                    String goodsCreateId = oneData.get("GOODSCREATEID").toString();
                    String actionType = oneData.get("ACTIONTYPE").toString();
                    String punit = oneData.get("PUNIT").toString();
                    String punitName = oneData.get("PUNITNAME").toString();
                    String baseUnit = oneData.get("BASEUNIT").toString();
                    String baseUnitName = oneData.get("BASEUNITNAME").toString();
                    String unitRatio = oneData.get("UNIT_RATIO").toString();
                    String location = oneData.get("LOCATION").toString();
                    String pqty = oneData.get("PQTY").toString();
                    String baseQty = oneData.get("BASEQTY").toString();
                    String lastModiOpId = oneData.get("LASTMODIOPID").toString();
                    String lastModiOpName = oneData.get("LASTMODIOPNAME").toString();
                    String lastModiTime = oneData.get("LASTMODITIME").toString();

                    trackList.setActionType(actionType);
                    trackList.setBaseQty(baseQty);
                    trackList.setBaseUnit(baseUnit);
                    trackList.setBaseUnitName(baseUnitName);
                    trackList.setGoodsCreateId(goodsCreateId);
                    trackList.setLastModiOpId(lastModiOpId);
                    trackList.setLastModiOpName(lastModiOpName);
                    trackList.setLastModiTime(lastModiTime);
                    trackList.setLocation(location);
                    trackList.setPqty(pqty);
                    trackList.setPunit(punit);
                    trackList.setPunitName(punitName);
                    trackList.setUnitRatio(unitRatio);

                    datas.getTrackList().add(trackList);
                }
            }

            res.setDatas(datas);
            return res;

        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_SubStockTakeGoodsDetailReq req) throws Exception {
        StringBuffer sqlbuf=new StringBuffer();
        String eId = req.geteId();
        String shopid = req.getShopId();
        String langType = req.getLangType();
        String subStockTakeNo = req.getRequest().getSubStockTakeNo();
        String pluNo = req.getRequest().getPluNo();
        String featureNo = req.getRequest().getFeatureNo();
        sqlbuf.append(""
                + " select a.goodsCreateId,a.actionType,a.punit,a.baseUnit,a.unit_Ratio,a.location,a.pqty,a.baseQty,"
                + " a.lastmodiopid,a.lastmodiopname,to_char(a.lastmoditime,'yyyy-MM-dd hh24:mi:ss') as lastmoditime,"
                + " b1.uname as punitname,b2.uname as baseunitname "
                + " from dcp_substocktake_detailtrack a"
                + " left join dcp_unit_lang b1 on a.eid=b1.eid and a.punit=b1.unit and b1.lang_type='"+langType+"'"
                + " left join dcp_unit_lang b2 on a.eid=b2.eid and a.baseunit=b2.unit and b2.lang_type='"+langType+"'"
                + " where a.eid='"+eId+"' and a.shopid='"+shopid+"' and a.substocktakeno='"+subStockTakeNo+"' "
                + " and a.pluno='"+pluNo+"' and a.featureno='"+featureNo+"' "
                + " order by a.lastmoditime desc "
                + " ");

        return sqlbuf.toString();
    }
}
