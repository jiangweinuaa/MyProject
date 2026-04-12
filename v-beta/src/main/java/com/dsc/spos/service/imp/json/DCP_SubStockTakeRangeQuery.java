package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_SubStockTakeRangeQueryReq;
import com.dsc.spos.json.cust.res.DCP_SubStockTakeRangeQueryRes;
import com.dsc.spos.json.cust.res.DCP_SubStockTakeRangeQueryRes.level1Elm;
import com.dsc.spos.json.cust.res.DCP_SubStockTakeRangeQueryRes.level2Elm;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * 服务函数：DCP_SubStockTakeRangeQuery
 * 服务说明：盘点子任务范围查询
 * @author jinzma
 * @since  2021-03-08
 */
public class DCP_SubStockTakeRangeQuery extends SPosBasicService<DCP_SubStockTakeRangeQueryReq, DCP_SubStockTakeRangeQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_SubStockTakeRangeQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        String subStockTakeNo = req.getRequest().getSubStockTakeNo();
        String stockTakeNo = req.getRequest().getStockTakeNo();
        if (Check.Null(subStockTakeNo)) {
            errMsg.append("盘点子任务单号不能为空,");
            isFail = true;
        }
        if (Check.Null(stockTakeNo)) {
            errMsg.append("来源盘点单不能为空,");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_SubStockTakeRangeQueryReq> getRequestType() {
        return new TypeToken<DCP_SubStockTakeRangeQueryReq>(){};
    }

    @Override
    protected DCP_SubStockTakeRangeQueryRes getResponseType() {
        return new DCP_SubStockTakeRangeQueryRes();
    }

    @Override
    protected DCP_SubStockTakeRangeQueryRes processJson(DCP_SubStockTakeRangeQueryReq req) throws Exception {
        DCP_SubStockTakeRangeQueryRes res = this.getResponse();
        level1Elm datas = res.new level1Elm();
        datas.setPluList(new ArrayList<level2Elm>());
        try {
            // 拼接返回图片路径
            String isHttps = PosPub.getPARA_SMS(dao, req.geteId(), "", "ISHTTPS");
            String httpStr=isHttps.equals("1")?"https://":"http://";
            String domainName=PosPub.getPARA_SMS(dao, req.geteId(), "", "DomainName");

            String sql=this.getQuerySql(req);
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            for (Map<String, Object> oneData : getQData) {
                level2Elm oneLv2 = res.new level2Elm();
                String pluNo = oneData.get("PLUNO").toString();
                String pluName = oneData.get("PLUNAME").toString();
                String featureNo = oneData.get("FEATURENO").toString();
                String featureName = oneData.get("FEATURENAME").toString();
                String punit = oneData.get("PUNIT").toString();
                String punitName = oneData.get("PUNITNAME").toString();
                String status = oneData.get("STATUS").toString();
                // 增加商品图片 BY jinzma 20210315
                String listImage = oneData.get("LISTIMAGE").toString();
                if(!Check.Null(listImage)){
                    if (domainName.endsWith("/")) {
                        listImage = httpStr+domainName+"resource/image/" +listImage;
                    }else {
                        listImage = httpStr+domainName+"/resource/image/" +listImage;
                    }
                }

                oneLv2.setFeatureName(featureName);
                oneLv2.setFeatureNo(featureNo);
                oneLv2.setPluName(pluName);
                oneLv2.setPluNo(pluNo);
                oneLv2.setPunit(punit);
                oneLv2.setPunitName(punitName);
                oneLv2.setStatus(status);
                oneLv2.setListImage(listImage);

                datas.getPluList().add(oneLv2);
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
    protected String getQuerySql(DCP_SubStockTakeRangeQueryReq req) throws Exception {
        StringBuffer sqlbuf=new StringBuffer();
        String eId = req.geteId();
        String shopid = req.getShopId();
        String langType = req.getLangType();
        String subStockTakeNo = req.getRequest().getSubStockTakeNo();
        String stockTakeNo = req.getRequest().getStockTakeNo();
        String status = req.getRequest().getStatus();  //状态（空：全部；0：未盘点；100已盘点）
        String keyTxt = req.getRequest().getKeyTxt();

        sqlbuf.append(""
                + " select a.eid,a.shopid,a.stocktakeno,a.pluno,a.featureno,a.punit,nvl2(b.pluno,N'100',N'0') as status,"
                + " c.plu_name as pluName,d.featurename,e.uname as punitName,image.listImage"
                + " from dcp_stocktake_detail a"
                + " left join dcp_substocktake_detail b on a.eid=b.eid and a.shopid=b.shopid and b.substocktakeno='"+subStockTakeNo+"' "
                + " and a.pluno=b.pluno and a.featureno=b.featureno "
                + " left join dcp_goods_lang c on a.eid=c.eid and a.pluno=c.pluno and c.lang_type='"+langType+"'"
                + " left join dcp_goods_feature_lang d on a.eid=d.eid and a.pluno=d.pluno and a.featureno=d.featureno and d.lang_type='"+langType+"'"
                + " left join dcp_unit_lang e on a.eid=e.eid and a.punit=e.unit and e.lang_type='"+langType+"'"
                + " left join dcp_goodsimage image on a.eid=image.eid and image.pluno=a.pluno and image.apptype='ALL' "
                + " where a.eid='"+eId+"' and a.shopid='"+shopid+"' and a.stocktakeno='"+stockTakeNo+"' "
                + " ");

        if (!Check.Null(status)){
            if (status.equals("0")){
                sqlbuf.append(" and b.pluno is null");
            }else{
                sqlbuf.append(" and b.pluno is not null");
            }
        }
        if (!Check.Null(keyTxt)){
            sqlbuf.append(" and (a.pluno like '%"+keyTxt+"%' or c.plu_name like '%"+keyTxt+"%') ");
        }

        sqlbuf.append(" order by a.item");
        return  sqlbuf.toString();
    }
}
