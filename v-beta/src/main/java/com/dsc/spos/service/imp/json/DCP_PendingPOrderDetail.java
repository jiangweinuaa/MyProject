package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.json.cust.req.DCP_PendingPOrderDetailReq;
import com.dsc.spos.json.cust.res.DCP_PendingPOrderDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_PendingPOrderDetail extends SPosBasicService<DCP_PendingPOrderDetailReq, DCP_PendingPOrderDetailRes> {

    @Override
    protected boolean isVerifyFail(DCP_PendingPOrderDetailReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        if(req.getRequest()==null) {
            isFail = true;
            errMsg.append("request不能为空 ");
        }

        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_PendingPOrderDetailReq> getRequestType() {
        return new TypeToken<DCP_PendingPOrderDetailReq>(){};
    }

    @Override
    protected DCP_PendingPOrderDetailRes getResponseType() {
        return new DCP_PendingPOrderDetailRes();
    }

    @Override
    protected DCP_PendingPOrderDetailRes processJson(DCP_PendingPOrderDetailReq req) throws Exception {
        DCP_PendingPOrderDetailRes res = this.getResponse();

        res.setDatas(new ArrayList<>());

        //1.DCP_PORDER.STATUS=6(已审核）
        //2.DCP_PORDER.SUPPLIERTYPE="FACTORY"(统配）
        //3.DCP_PORDER.RECEIPT_ORG=当前登陆组织；
        //4.DCP_DEMAND.CLOSESTATUS=0(未结束）
        //5.待发货数>0: 要货底稿DCP_DEMAND.需求量PQTY-已发货量STOCKOUTQTY>0
        String sql="select b.*,d.plu_name as pluname,e.featurename,f.category_name as categoryname,g.uname as punitname,h.uname as baseunitname,c.poqty,c.STOCKOUTNOQTY as noqty,c.STOCKOUTQTY," +
                " d1.spec,d1.category,i.unitratio " +
                " from dcp_porder  a " +
                " inner join dcp_porder_detail b on a.eid=b.eid and a.organizationno=b.organizationno and a.porderno=b.porderno " +
                " inner join dcp_demand c on b.eid=c.eid and b.organizationno=c.organizationno and b.porderno=c.orderno and b.item=c.item " +
                " left join dcp_goods_lang d on d.eid=a.eid and d.pluno=b.pluno and d.lang_type='"+req.getLangType()+"' " +
                " left join dcp_goods d1 on d1.eid=a.eid and d1.pluno=b.pluno " +
                " left join DCP_GOODS_FEATURE_LANG e on e.eid=a.eid and e.pluno=b.pluno and e.featureno=b.pluno and e.lang_type='"+req.getLangType()+"' " +
                " left join DCP_CATEGORY_LANG f on f.eid=a.eid and f.category=d1.category and f.lang_type='"+req.getLangType()+"' " +
                " left join dcp_unit_lang g on g.eid=a.eid and g.unit=b.punit and g.lang_type='"+req.getLangType()+"' " +
                " left join dcp_unit_lANG H on h.eid=b.eid and h.unit=b.baseunit and h.lang_type='"+req.getLangType()+"' " +
                " left join dcp_goods_unit i on i.eid=a.eid and i.ounit=b.punit and i.pluno=b.pluno " +
                " where a.eid='"+req.geteId()+"' " +
                " and a.status='6' and a.suppliertype='FACTORY'" +
                " AND A.RECEIPT_ORG='"+req.getOrganizationNO()+"' " +
                " and c.closestatus='0' " +
                " and a.porderno='"+req.getRequest().getPOrderNo()+"' ";
        List<Map<String, Object>> list = this.doQueryData(sql, null);
        if(CollUtil.isNotEmpty(list)){
            for (Map<String, Object> singleList:list){
                DCP_PendingPOrderDetailRes.DatasLevel datasLevel = res.new DatasLevel();
                datasLevel.setPOrderNo(singleList.get("PORDERNO").toString());
                datasLevel.setItem(singleList.get("ITEM").toString());
                datasLevel.setPluBarcode(singleList.get("PLUBARCODE").toString());
                datasLevel.setPluNo(singleList.get("PLUNO").toString());
                datasLevel.setPluName(singleList.get("PLUNAME").toString());
                datasLevel.setSpec(singleList.get("SPEC").toString());
                datasLevel.setFeatureNo(singleList.get("FEATURENO").toString());
                datasLevel.setFeatureName(singleList.get("FEATURENAME").toString());
                datasLevel.setCategory(singleList.get("CATEGORY").toString());
                datasLevel.setCategoryName(singleList.get("CATEGORYNAME").toString());
                datasLevel.setPUnit(singleList.get("PUNIT").toString());
                datasLevel.setPUnitName(singleList.get("PUNITNAME").toString());
                datasLevel.setBaseUnit(singleList.get("BASEUNIT").toString());
                datasLevel.setUnitRatio(singleList.get("UNITRATIO").toString());
                datasLevel.setPoQty(singleList.get("POQTY").toString());
                datasLevel.setPQty(singleList.get("PQTY").toString());
                datasLevel.setNoQty(singleList.get("NOQTY").toString());
                datasLevel.setStockOutQty(singleList.get("STOCKOUTQTY").toString());
                datasLevel.setDistriPrice(singleList.get("DISTRIPRICE").toString());
                datasLevel.setPrice(singleList.get("PRICE").toString());
                res.getDatas().add(datasLevel);


            }


        }


        return res;

    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
    }

    @Override
    protected String getQuerySql(DCP_PendingPOrderDetailReq req) throws Exception {

        return "";
    }
}