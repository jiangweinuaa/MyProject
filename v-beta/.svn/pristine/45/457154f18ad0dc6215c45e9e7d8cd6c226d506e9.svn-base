package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_LightProGoodsCreateReq;
import com.dsc.spos.json.cust.req.DCP_LightProGoodsCreateReq.levelPlu;
import com.dsc.spos.json.cust.req.DCP_LightProGoodsCreateReq.levelShop;
import com.dsc.spos.json.cust.res.DCP_LightProGoodsCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DCP_LightProGoodsCreate extends SPosAdvanceService<DCP_LightProGoodsCreateReq, DCP_LightProGoodsCreateRes> {
    @Override
    protected void processDUID(DCP_LightProGoodsCreateReq req, DCP_LightProGoodsCreateRes res) throws Exception {

        String eId = req.geteId();
        String opNo = req.getOpNO();
        String opName = req.getOpName();
        String lastModiTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        ArrayList<DataProcessBean> DPB_del = new ArrayList<>();
        ArrayList<DataProcessBean> DPB_in = new ArrayList<>();
        String[] columns1 = { "EID", "SHOPNO", "PLUNO", "FEATURENO","UNIT", "CREATEOPID" ,"CREATEOPNAME","CREATETIME"};
        for (levelPlu pluInfo : req.getRequest().getPluList())
        {
            String pluNo = pluInfo.getPluNo();
            String featureNo = pluInfo.getFeatureNo();
            if (featureNo==null||featureNo.isEmpty())
            {
                featureNo = " ";
            }
            String unitNo = pluInfo.getUnitNo();
            InsBean ib1 = new InsBean("DCP_LIGHTPROGOODS", columns1);
            String exsql = " delete from DCP_LIGHTPROGOODS where EID='"+eId+"' and PLUNO='"+pluNo+"' and FEATURENO='"+featureNo+"'";
            String conSql = "";
            for (levelShop shopInfo : req.getRequest().getShopList())
            {
                String shopNo = shopInfo.getShopNo();
                if (conSql.isEmpty())
                {
                    conSql = "'"+shopNo+"'";
                }
                else
                {
                    conSql = conSql+",'"+shopNo+"'";
                }
                DataValue[] insValue1 = null;
                insValue1 = new DataValue[] {
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(shopNo, Types.VARCHAR),
                        new DataValue(pluNo, Types.VARCHAR),
                        new DataValue(featureNo, Types.VARCHAR),
                        new DataValue(unitNo, Types.VARCHAR) ,
                        new DataValue(opNo, Types.VARCHAR) ,
                        new DataValue(opName, Types.VARCHAR) ,
                        new DataValue(lastModiTimeStr, Types.DATE)
                };
                ib1.addValues(insValue1);
            }
            exsql = exsql + " and SHOPNO in("+conSql+")";
            ExecBean execBean = new ExecBean(exsql);
            DPB_del.add(new DataProcessBean(execBean));
            DPB_in.add(new DataProcessBean(ib1));
        }

        //先添加删除sql
        this.pData.addAll(DPB_del);
        this.pData.addAll(DPB_in);
        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_LightProGoodsCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_LightProGoodsCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_LightProGoodsCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_LightProGoodsCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        if (req.getRequest()==null)
        {
            errMsg.append("request不能为空值,");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if (req.getRequest().getPluList()==null||req.getRequest().getPluList().isEmpty())
        {
            errMsg.append("商品列表pluList不可为空, ");
            isFail = true;
        }
        if (req.getRequest().getShopList()==null||req.getRequest().getShopList().isEmpty())
        {
            errMsg.append("门店列表shopList不可为, ");
            isFail = true;
        }
        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        for (levelPlu par :req.getRequest().getPluList())
        {
            if (par.getPluNo()==null||par.getPluNo().isEmpty())
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品列表节点pluNo不可为空值,");
            }
        }
        for (levelShop par :req.getRequest().getShopList())
        {
            if (par.getShopNo()==null||par.getShopNo().isEmpty())
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "门店列表节点shopNo不可为空值,");
            }
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_LightProGoodsCreateReq> getRequestType() {
        return new TypeToken<DCP_LightProGoodsCreateReq>(){};
    }

    @Override
    protected DCP_LightProGoodsCreateRes getResponseType() {
        return new DCP_LightProGoodsCreateRes();
    }
}
