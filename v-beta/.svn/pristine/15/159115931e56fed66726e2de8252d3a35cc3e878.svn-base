package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_SalePriceTemplateGoodsEnableReq;
import com.dsc.spos.json.cust.res.DCP_SalePriceTemplateGoodsEnableRes;
import com.dsc.spos.model.Template_POS_GoodsChannelPriceRedisUpdate;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.DateFormatUtils;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_SalePriceTemplateGoodsEnable extends SPosAdvanceService<DCP_SalePriceTemplateGoodsEnableReq, DCP_SalePriceTemplateGoodsEnableRes>
{


    @Override
    protected void processDUID(DCP_SalePriceTemplateGoodsEnableReq req, DCP_SalePriceTemplateGoodsEnableRes res) throws Exception
    {

        //同步缓存
        List<Template_POS_GoodsChannelPriceRedisUpdate> templateList=new ArrayList<>();
        //
        Template_POS_GoodsChannelPriceRedisUpdate pos_goodsChannelPriceRedisUpdate=new Template_POS_GoodsChannelPriceRedisUpdate();
        pos_goodsChannelPriceRedisUpdate.setTemplateId(req.getRequest().getTemplateId());
        pos_goodsChannelPriceRedisUpdate.setPluList(new ArrayList<>());
        templateList.add(pos_goodsChannelPriceRedisUpdate);


        String eId=req.geteId();

        String  oprType=req.getRequest().getOprType();//操作类型：1-启用2-禁用
        String  templateId=req.getRequest().getTemplateId();
        List<DCP_SalePriceTemplateGoodsEnableReq.plu>  pluList=req.getRequest().getPluList();

        String status="";
        if (oprType.equals("1"))
        {
            status="100";
        }
        else if (oprType.equals("2"))
        {
            status="0";
        }
        else
        {
            res.setSuccess(false);
            res.setServiceStatus("100");
            res.setServiceDescription("操作类型的值不在规格中！");
            return;
        }

        for (DCP_SalePriceTemplateGoodsEnableReq.plu plu : pluList)
        {
            //同步缓存
            Template_POS_GoodsChannelPriceRedisUpdate.plu vplu=pos_goodsChannelPriceRedisUpdate.new plu();
            vplu.setItem(plu.getItem());
            vplu.setPluNo(plu.getPluNo());

            UptBean ub1 = new UptBean("DCP_SALEPRICETEMPLATE_PRICE");
            //add Value
            ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
            ub1.addUpdateValue("REDISUPDATESUCCESS", new DataValue("N",Types.VARCHAR));
            ub1.addUpdateValue("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
            ub1.addUpdateValue("LASTMODITIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

            //condition
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
            ub1.addCondition("ITEM", new DataValue(plu.getItem(), Types.VARCHAR));
            ub1.addCondition("PLUNO", new DataValue(plu.getPluNo(), Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));
        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");


        //同步缓存
        String posUrl = PosPub.getPOS_INNER_URL(req.geteId());
        String apiUserSql = "SELECT ITEM,ITEMVALUE FROM PLATFORM_BASESETTEMP WHERE EID = '" + req.geteId() + "'" +
                " AND ( ITEM = 'ApiUserCode' OR ITEM = 'ApiUserKey' )";
        List<Map<String, Object>> result = this.doQueryData(apiUserSql, null);
        String apiUserCode = "";
        String apiUserKey = "";
        if (result != null && result.size() == 2) {
            for (Map<String, Object> map : result) {
                if (map.get("ITEM") != null && map.get("ITEM").toString().equals("ApiUserCode")) {
                    apiUserCode = map.get("ITEMVALUE").toString();
                } else {
                    apiUserKey = map.get("ITEMVALUE").toString();
                }
            }
        }


        PosPub.POS_GoodsChannelPriceRedisUpdate_Cache(posUrl, apiUserCode, apiUserKey,templateList);


        return;

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_SalePriceTemplateGoodsEnableReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_SalePriceTemplateGoodsEnableReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_SalePriceTemplateGoodsEnableReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_SalePriceTemplateGoodsEnableReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            isFail = true;
            errMsg.append("request不能为空 ");
            throw new DispatchService.SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        String  oprType=req.getRequest().getOprType();//操作类型：1-启用2-禁用
        String  templateId=req.getRequest().getTemplateId();
        List<DCP_SalePriceTemplateGoodsEnableReq.plu>  pluList=req.getRequest().getPluList();
        if(Check.Null(oprType))
        {
            errMsg.append("操作类型不能为空值 ");
            isFail = true;
        }

        if(Check.Null(templateId))
        {
            errMsg.append("模板编号不能为空值 ");
            isFail = true;
        }

        for (DCP_SalePriceTemplateGoodsEnableReq.plu plu : pluList)
        {
            if(Check.Null(plu.getItem()))
            {
                errMsg.append("商品序号不能为空值 ");
                isFail = true;
            }
            if(Check.Null(plu.getPluNo()))
            {
                errMsg.append("商品pluNo不能为空值 ");
                isFail = true;
            }
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;

    }

    @Override
    protected TypeToken<DCP_SalePriceTemplateGoodsEnableReq> getRequestType()
    {
        return new TypeToken<DCP_SalePriceTemplateGoodsEnableReq>(){};
    }

    @Override
    protected DCP_SalePriceTemplateGoodsEnableRes getResponseType()
    {
        return new DCP_SalePriceTemplateGoodsEnableRes();
    }
}
