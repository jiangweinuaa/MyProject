package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_GoodsTemplateGoodsEnableReq;
import com.dsc.spos.json.cust.res.DCP_GoodsTemplateGoodsEnableRes;
import com.dsc.spos.model.Template_POS_GoodsTemplateDetailRedisUpdate;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_GoodsTemplateGoodsEnable extends SPosAdvanceService<DCP_GoodsTemplateGoodsEnableReq, DCP_GoodsTemplateGoodsEnableRes>
{


    @Override
    protected void processDUID(DCP_GoodsTemplateGoodsEnableReq req, DCP_GoodsTemplateGoodsEnableRes res) throws Exception
    {

        //同步缓存
        Template_POS_GoodsTemplateDetailRedisUpdate v_template=new Template_POS_GoodsTemplateDetailRedisUpdate();
        v_template.setTemplateId(req.getRequest().getTemplateId());
        v_template.setPluList(new ArrayList<>());


        String eId=req.geteId();
        String oprType=req.getRequest().getOprType();
        String templateId=req.getRequest().getTemplateId();
        List<DCP_GoodsTemplateGoodsEnableReq.plu> pluList=req.getRequest().getPluList();

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

        if (pluList != null && pluList.size()>0)
        {
            for (DCP_GoodsTemplateGoodsEnableReq.plu plu : pluList)
            {
                UptBean ub1 = new UptBean("dcp_goodstemplate_goods");
                //add Value
                ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
                ub1.addUpdateValue("REDISUPDATESUCCESS", new DataValue("N", Types.VARCHAR));
                ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getEmployeeNo(), Types.VARCHAR));
                String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));


                //condition
                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
                ub1.addCondition("PLUNO", new DataValue(plu.getPluNo(), Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub1));


                //同步缓存
                Template_POS_GoodsTemplateDetailRedisUpdate.plu vplu=v_template.new plu();
                vplu.setPluNo(plu.getPluNo());
                v_template.getPluList().add(vplu);
            }
        }
        else
        {
            UptBean ub1 = new UptBean("dcp_goodstemplate_goods");
            //add Value
            ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
            ub1.addUpdateValue("REDISUPDATESUCCESS", new DataValue("N", Types.VARCHAR));

            //condition
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
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
        //
        PosPub.POS_GoodsTemplateDetailRedisUpdate_Cache(posUrl, apiUserCode, apiUserKey,v_template);



        return;


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_GoodsTemplateGoodsEnableReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_GoodsTemplateGoodsEnableReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_GoodsTemplateGoodsEnableReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_GoodsTemplateGoodsEnableReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            isFail = true;
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        String oprType=req.getRequest().getOprType();
        String templateId=req.getRequest().getTemplateId();
        List<DCP_GoodsTemplateGoodsEnableReq.plu> pluList=req.getRequest().getPluList();

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

        if (pluList != null && pluList.size()>0)
        {
            for (DCP_GoodsTemplateGoodsEnableReq.plu plu : pluList)
            {
                if(Check.Null(plu.getPluNo()))
                {
                    errMsg.append("品号不能为空值 ");
                    isFail = true;
                }
            }

        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_GoodsTemplateGoodsEnableReq> getRequestType()
    {
        return new TypeToken<DCP_GoodsTemplateGoodsEnableReq>(){};
    }

    @Override
    protected DCP_GoodsTemplateGoodsEnableRes getResponseType()
    {
        return new DCP_GoodsTemplateGoodsEnableRes();
    }
}
