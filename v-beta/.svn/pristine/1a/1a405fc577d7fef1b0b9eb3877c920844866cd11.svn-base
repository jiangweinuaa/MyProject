package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ProcessTaskPendingQueryReq;
import com.dsc.spos.json.cust.res.DCP_ProcessTaskPendingQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_ProcessTaskPendingQuery extends SPosBasicService<DCP_ProcessTaskPendingQueryReq, DCP_ProcessTaskPendingQueryRes> {

    @Override
    protected boolean isVerifyFail(DCP_ProcessTaskPendingQueryReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        if (isFail){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }

    @Override
    protected TypeToken<DCP_ProcessTaskPendingQueryReq> getRequestType() {
        return new TypeToken<DCP_ProcessTaskPendingQueryReq>(){};
    }

    @Override
    protected DCP_ProcessTaskPendingQueryRes getResponseType() {
        return new DCP_ProcessTaskPendingQueryRes();
    }

    @Override
    protected DCP_ProcessTaskPendingQueryRes processJson(DCP_ProcessTaskPendingQueryReq req) throws Exception {
        DCP_ProcessTaskPendingQueryRes res = this.getResponse();
        res.setDatas(new ArrayList<>());
        String opType = req.getRequest().getOpType();
        //1.入参opType=1
        //查询DCP_PROCESSTASK_DETAIL明细数据
        //条件：DCP_PROCESSTASK.STATUS IN ('6','8')
        //AND DCP_PROCESSTASK_DETAIL.GOODSSTATUS IN ('0','1')
        //AND NVL(PICKSTATUS,'N')='N'
        //2.入参opType=2
        //查询DCP_PROCESSTASK_DETAIL明细数据
        //条件：DCP_PROCESSTASK.STATUS IN ('6','8')
        //AND DCP_PROCESSTASK_DETAIL.GOODSSTATUS IN ('0','1')
        //AND DCP_PROCESSTASK_DETAIL.DISPTYPE='1'
        //AND PQTY-BATCHQTY>0
        String sql="";
        if ("1".equals(opType)){
            sql="select a.PROCESSTASKNO,a.item,a.pluno,a.featureno,c.plu_name as pluname,d.featurename,a.pqty,a.PSTOCKIN_QTY as pstockinqty,a.BATCHQTY,a.punit,e.uname as punitname," +
                    " a.begindate,a.enddate,a.bomno,a.versionnum,a.SEMIWOTYPE " +
                    " from DCP_PROCESSTASK_DETAIL a " +
                    " inner join dcp_processtask b on a.eid=b.eid and a.organizationno=b.organizationno  and a.PROCESSTASKNO=b.PROCESSTASKNO " +
                    " left join dcp_goods_lang c on c.eid=a.eid and c.pluno=a.pluno and c.lang_type='"+req.getLangType()+"' " +
                    " left join dcp_goods_feature_lang d on d.eid=a.eid and d.pluno=a.pluno and d.featureno=a.featureno and d.lang_type='"+req.getLangType()+"' " +
                    " left join dcp_unit_lang e on e.eid=a.eid and e.unit=a.punit and e.lang_type='"+req.getLangType()+"' " +
                    " where b.status in ('6','8') and a.GOODSSTATUS in ('0','1') " +
                    " and a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' " +
                    " and NVL(a.PICKSTATUS,'N')='N' ";

        }
        else if("2".equals(opType)){
            sql="select a.PROCESSTASKNO,a.item,a.pluno,a.featureno,c.plu_name as pluname,d.featurename,a.pqty,a.PSTOCKIN_QTY as pstockinqty,a.BATCHQTY,a.punit,e.uname as punitname," +
                    " a.begindate,a.enddate,a.bomno,a.versionnum,a.SEMIWOTYPE " +
                    " from DCP_PROCESSTASK_DETAIL a " +
                    " inner join dcp_processtask b on a.eid=b.eid and a.organizationno=b.organizationno  and a.PROCESSTASKNO=b.PROCESSTASKNO " +
                    " left join dcp_goods_lang c on c.eid=a.eid and c.pluno=a.pluno and c.lang_type='"+req.getLangType()+"' " +
                    " left join dcp_goods_feature_lang d on d.eid=a.eid and d.pluno=a.pluno and d.featureno=a.featureno and d.lang_type='"+req.getLangType()+"' " +
                    " left join dcp_unit_lang e on e.eid=a.eid and e.unit=a.punit and e.lang_type='"+req.getLangType()+"' " +
                    " where b.status in ('6','8') and a.GOODSSTATUS in ('0','1') " +
                    " and a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' " +
                    " and a.DISPTYPE='1' and a.PQTY-nvl(a.BATCHQTY,0)>0  ";
        }

        //入参opType增加枚举值3.报工
        //查询DCP_PROCESSTASK_DETAIL明细数据
        //条件：DCP_PROCESSTASK.STATUS IN ('6','8')
        //AND DCP_PROCESSTASK_DETAIL.DISPATCHQTY=0
        else if("3".equals(opType)){
            sql="select a.PROCESSTASKNO,a.item,a.pluno,a.featureno,c.plu_name as pluname,d.featurename,a.pqty,a.PSTOCKIN_QTY as pstockinqty,a.BATCHQTY,a.punit,e.uname as punitname," +
                    " a.begindate,a.enddate,a.bomno,a.versionnum,a.SEMIWOTYPE " +
                    " from DCP_PROCESSTASK_DETAIL a " +
                    " inner join dcp_processtask b on a.eid=b.eid and a.organizationno=b.organizationno  and a.PROCESSTASKNO=b.PROCESSTASKNO " +
                    " left join dcp_goods_lang c on c.eid=a.eid and c.pluno=a.pluno and c.lang_type='"+req.getLangType()+"' " +
                    " left join dcp_goods_feature_lang d on d.eid=a.eid and d.pluno=a.pluno and d.featureno=a.featureno and d.lang_type='"+req.getLangType()+"' " +
                    " left join dcp_unit_lang e on e.eid=a.eid and e.unit=a.punit and e.lang_type='"+req.getLangType()+"' " +
                    " where b.status in ('6','8') and a.DISPATCHQTY ='0' " +
                    " and a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' " +
                    "  ";
        }

        if(Check.NotNull(req.getRequest().getProdType())){
            sql+=" and b.PRODTYPE='"+req.getRequest().getProdType()+"'";
        }
        if(Check.NotNull(req.getRequest().getDateType())){
            if("1".equals(req.getRequest().getDateType())){
                if(Check.NotNull(req.getRequest().getBeginDate())){
                    sql+=" and a.bdate>='"+req.getRequest().getBeginDate()+"' ";
                }
                if(Check.NotNull(req.getRequest().getEndDate())){
                    sql+=" and a.bdate<='"+req.getRequest().getEndDate()+"' ";
                }
            }
            else if("2".equals(req.getRequest().getDateType())){
                if(Check.NotNull(req.getRequest().getBeginDate())){
                    sql+=" and a.begindate>='"+req.getRequest().getBeginDate()+"' ";
                }
                if(Check.NotNull(req.getRequest().getEndDate())){
                    sql+=" and a.begindate<='"+req.getRequest().getEndDate()+"' ";
                }
            }
            else if("3".equals(req.getRequest().getDateType())){
                if(Check.NotNull(req.getRequest().getBeginDate())){
                    sql+=" and a.enddate>='"+req.getRequest().getBeginDate()+"' ";
                }
                if(Check.NotNull(req.getRequest().getEndDate())){
                    sql+=" and a.enddate<='"+req.getRequest().getEndDate()+"' ";
                }
            }
        }

        if(Check.NotNull(req.getRequest().getDepartId())){
            sql+=" and b.departid='"+req.getRequest().getDepartId()+"'";
        }
        if(Check.NotNull(req.getRequest().getPluNo())){
            sql+=" and a.pluno='"+req.getRequest().getPluNo()+"'";
        }

        if(sql.length()>0){
            List<Map<String, Object>> list = this.doQueryData(sql, null);
            for (Map<String, Object> map : list) {
                DCP_ProcessTaskPendingQueryRes.level1Elm level1Elm = res.new level1Elm();

                level1Elm.setProcessTaskNo(map.get("PROCESSTASKNO").toString());
                level1Elm.setItem(map.get("ITEM").toString());
                level1Elm.setPluNo(map.get("PLUNO").toString());
                level1Elm.setPluName(map.get("PLUNAME").toString());
                level1Elm.setFeatureNo(map.get("FEATURENO").toString());
                level1Elm.setFeatureName(map.get("FEATURENAME").toString());
                level1Elm.setPqty(map.get("PQTY").toString());
                level1Elm.setPStockInQty(map.get("PSTOCKINQTY").toString());
                level1Elm.setBatchQty(map.get("BATCHQTY").toString());
                level1Elm.setPunit(map.get("PUNIT").toString());
                level1Elm.setPunitName(map.get("PUNITNAME").toString());
                level1Elm.setBeginDate(map.get("BEGINDATE").toString());
                level1Elm.setEndDate(map.get("ENDDATE").toString());
                level1Elm.setBomNo(map.get("BOMNO").toString());
                level1Elm.setVersionNum(map.get("VERSIONNUM").toString());
                level1Elm.setSemiWoType(map.get("SEMIWOTYPE").toString());
                res.getDatas().add(level1Elm);

            }
        }

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_ProcessTaskPendingQueryReq req) throws Exception {
        return null;
    }


}
