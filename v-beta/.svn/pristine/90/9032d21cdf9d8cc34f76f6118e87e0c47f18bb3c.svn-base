package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_CardRwRuleUpdateReq;
import com.dsc.spos.json.cust.res.DCP_CardRwRuleUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.json.JSONObject;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DCP_CardRwRuleUpdate extends SPosAdvanceService<DCP_CardRwRuleUpdateReq, DCP_CardRwRuleUpdateRes>
{


    @Override
    protected void processDUID(DCP_CardRwRuleUpdateReq req, DCP_CardRwRuleUpdateRes res) throws Exception
    {

        String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        String ruleName = req.getRequest().getRuleName();
        String mediaType = req.getRequest().getMediaType();
        String rwType = req.getRequest().getRwType();
        String priority = req.getRequest().getPriority();
        String status = req.getRequest().getStatus();
        String ruleType = req.getRequest().getRuleType();
        List<DCP_CardRwRuleUpdateReq.paramInfo> params = req.getRequest().getParams();

        String ruleId=req.getRequest().getRuleId();

        //
        JSONObject request_params = new JSONObject();
        for (DCP_CardRwRuleUpdateReq.paramInfo param : params)
        {
            String groupId=param.getGroupId();
            List<DCP_CardRwRuleUpdateReq.paramListInfo> paramList=param.getParamList();

            for (DCP_CardRwRuleUpdateReq.paramListInfo paramListInfo : paramList)
            {
                if (paramListInfo.getCurValue() == null)
                {
                    paramListInfo.setCurValue("");
                }

                if (request_params.has(paramListInfo.getParam())==false)
                {
                    request_params.put(paramListInfo.getParam(),paramListInfo.getCurValue());
                }
            }
        }

        //
        String json_params=request_params.toString();

        if (Check.Null(ruleId))//新增
        {
            ruleId=new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());



            String[] columns_CARDRWRULE ={"EID","RULEID","RULENAME","PRIORITY" ,
                    "MEDIATYPE","RWTYPE","RULETYPE", "MEMO","STATUS","PARAMS","CREATEOPID","CREATEOPNAME",
                    "CREATETIME","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME"
            };
            DataValue[] insValue_CARDRWRULE = new DataValue[]
                    {
                            new DataValue(req.geteId(), Types.VARCHAR),
                            new DataValue(ruleId, Types.VARCHAR),
                            new DataValue(ruleName, Types.VARCHAR),
                            new DataValue(priority, Types.INTEGER),
                            new DataValue(mediaType, Types.VARCHAR),
                            new DataValue(rwType, Types.VARCHAR),
                            new DataValue(ruleType, Types.VARCHAR),
                            new DataValue(req.getRequest().getMemo(), Types.VARCHAR),
                            new DataValue(status, Types.INTEGER),
                            new DataValue(json_params, Types.VARCHAR),
                            new DataValue(req.getOpNO(), Types.VARCHAR),
                            new DataValue(req.getOpName(), Types.VARCHAR),
                            new DataValue(lastmoditime, Types.DATE),
                            new DataValue(req.getOpNO(), Types.VARCHAR),
                            new DataValue(req.getOpName(), Types.VARCHAR),
                            new DataValue(lastmoditime, Types.DATE)
                    };


            InsBean ib_CARDRWRULE = new InsBean("DCP_CARDRWRULE", columns_CARDRWRULE);
            ib_CARDRWRULE.addValues(insValue_CARDRWRULE);
            this.addProcessData(new DataProcessBean(ib_CARDRWRULE));

            List<DCP_CardRwRuleUpdateReq.shopInfo> shopList=req.getRequest().getShopList();

            //适用门店
            if (ruleType.equals("2"))
            {
                if (shopList!=null && shopList.size()>0)
                {
                    String[] columns_CARDRWRULE_SHOP  ={"EID","RULEID","SHOPID","SERIALNO" ,
                            "LASTMODIOPID","LASTMODITIME"
                    };

                    int i=0;
                    for (DCP_CardRwRuleUpdateReq.shopInfo shopInfo : shopList)
                    {
                        i=i+1;
                        DataValue[] insValue_CARDRWRULE_SHOP = new DataValue[]
                                {
                                        new DataValue(req.geteId(), Types.VARCHAR),
                                        new DataValue(ruleId, Types.VARCHAR),
                                        new DataValue(shopInfo.getShopId(), Types.VARCHAR),
                                        new DataValue(i, Types.INTEGER),
                                        new DataValue(req.getOpNO(), Types.VARCHAR),
                                        new DataValue(lastmoditime, Types.DATE)
                                };

                        InsBean ib_CARDRWRULE_SHOP = new InsBean("DCP_CARDRWRULE_SHOP", columns_CARDRWRULE_SHOP);
                        ib_CARDRWRULE_SHOP.addValues(insValue_CARDRWRULE_SHOP);
                        this.addProcessData(new DataProcessBean(ib_CARDRWRULE_SHOP));
                    }
                }
            }


            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        }
        else //修改
        {
            //删除适用门店
            DelBean db1 = new DelBean("DCP_CARDRWRULE_SHOP");
            db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
            db1.addCondition("RULEID", new DataValue(ruleId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));

            //修改
            UptBean ub1 = new UptBean("DCP_CARDRWRULE");
            ub1.addUpdateValue("RULENAME", new DataValue(ruleName, Types.VARCHAR));
            ub1.addUpdateValue("PRIORITY", new DataValue(priority, Types.INTEGER));
            ub1.addUpdateValue("MEDIATYPE", new DataValue(mediaType, Types.VARCHAR));
            ub1.addUpdateValue("RWTYPE", new DataValue(rwType, Types.VARCHAR));
            ub1.addUpdateValue("RULETYPE", new DataValue(ruleType, Types.VARCHAR));
            ub1.addUpdateValue("MEMO", new DataValue(req.getRequest().getMemo(), Types.VARCHAR));
            ub1.addUpdateValue("STATUS", new DataValue(status, Types.INTEGER));
            ub1.addUpdateValue("PARAMS", new DataValue(json_params, Types.VARCHAR));
            ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
            ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(req.getOpName(), Types.VARCHAR));
            ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));

            //condition
            ub1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
            ub1.addCondition("RULEID", new DataValue(ruleId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));

            List<DCP_CardRwRuleUpdateReq.shopInfo> shopList=req.getRequest().getShopList();

            //适用门店
            if (ruleType.equals("2"))
            {
                if (shopList!=null && shopList.size()>0)
                {
                    String[] columns_CARDRWRULE_SHOP  ={"EID","RULEID","SHOPID","SERIALNO" ,
                            "LASTMODIOPID","LASTMODITIME"
                    };

                    int i=0;
                    for (DCP_CardRwRuleUpdateReq.shopInfo shopInfo : shopList)
                    {
                        i=i+1;
                        DataValue[] insValue_CARDRWRULE_SHOP = new DataValue[]
                                {
                                        new DataValue(req.geteId(), Types.VARCHAR),
                                        new DataValue(ruleId, Types.VARCHAR),
                                        new DataValue(shopInfo.getShopId(), Types.VARCHAR),
                                        new DataValue(i, Types.INTEGER),
                                        new DataValue(req.getOpNO(), Types.VARCHAR),
                                        new DataValue(lastmoditime, Types.DATE)
                                };

                        InsBean ib_CARDRWRULE_SHOP = new InsBean("DCP_CARDRWRULE_SHOP", columns_CARDRWRULE_SHOP);
                        ib_CARDRWRULE_SHOP.addValues(insValue_CARDRWRULE_SHOP);
                        this.addProcessData(new DataProcessBean(ib_CARDRWRULE_SHOP));
                    }
                }
            }

            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_CardRwRuleUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CardRwRuleUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CardRwRuleUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_CardRwRuleUpdateReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if(req.getRequest()==null)
        {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        String ruleName = req.getRequest().getRuleName();
        String mediaType = req.getRequest().getMediaType();
        String rwType = req.getRequest().getRwType();
        String priority = req.getRequest().getPriority();
        String status = req.getRequest().getStatus();
        String ruleType = req.getRequest().getRuleType();
        List<DCP_CardRwRuleUpdateReq.paramInfo> params = req.getRequest().getParams();

        if(Check.Null(ruleName))
        {
            errMsg.append("请求参数ruleName不能为空值 ");
            isFail = true;
        }
        if(Check.Null(mediaType))
        {
            errMsg.append("请求参数mediaType不能为空值 ");
            isFail = true;
        }
        if(Check.Null(rwType))
        {
            errMsg.append("请求参数rwType不能为空值 ");
            isFail = true;
        }
        if(Check.Null(priority))
        {
            errMsg.append("请求参数priority不能为空值 ");
            isFail = true;
        }
        if(Check.Null(status))
        {
            errMsg.append("请求参数status不能为空值 ");
            isFail = true;
        }
        if(Check.Null(ruleType))
        {
            errMsg.append("请求参数ruleType不能为空值 ");
            isFail = true;
        }
        if(params==null)
        {
            errMsg.append("请求参数params不能为空值 ");
            isFail = true;
        }
        else
        {
            for (DCP_CardRwRuleUpdateReq.paramInfo param : params)
            {
                String groupId=param.getGroupId();
                if(Check.Null(groupId))
                {
                    errMsg.append("请求参数params.groupId不能为空值 ");
                    isFail = true;
                }
                List<DCP_CardRwRuleUpdateReq.paramListInfo> paramList=param.getParamList();
                if(paramList==null)
                {
                    errMsg.append("请求参数params.paramList不能为空值 ");
                    isFail = true;
                }
                else
                {
                    for (DCP_CardRwRuleUpdateReq.paramListInfo paramListInfo : paramList)
                    {
                        String v_param=paramListInfo.getParam();
                        if(Check.Null(v_param))
                        {
                            errMsg.append("请求参数params.paramList.param不能为空值 ");
                            isFail = true;
                        }
                    }
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
    protected TypeToken<DCP_CardRwRuleUpdateReq> getRequestType()
    {
        return new TypeToken<DCP_CardRwRuleUpdateReq>(){};
    }

    @Override
    protected DCP_CardRwRuleUpdateRes getResponseType()
    {
        return new DCP_CardRwRuleUpdateRes();
    }


}
