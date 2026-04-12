package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_ProdTemplateEnableReq;
import com.dsc.spos.json.cust.res.DCP_ProdTemplateEnableRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_ProdTemplateEnable extends SPosAdvanceService<DCP_ProdTemplateEnableReq, DCP_ProdTemplateEnableRes> {

    @Override
    protected void processDUID(DCP_ProdTemplateEnableReq req, DCP_ProdTemplateEnableRes res) throws Exception {

        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String templateId = req.getRequest().getTemplateId();
        String opType = req.getRequest().getOpType();
        if("1".equals(opType)){

            //得判断已经生效的模板了
            String sql = "select a.templateid,b.pluno,c.organizationno,a.restrictOrg from DCP_PRODTEMPLATE a" +
                    " left join DCP_PRODTEMPLATE_GOODS b on a.eid=b.eid and a.templateid=b.templateid " +
                    " left join DCP_PRODTEMPLATE_RANGE c on a.eid=c.eid and a.templateid=c.templateid " +
                    " where a.eid='"+eId+"' and a.templateid!='"+templateId+"' and a.status=100 and a.status='100' and b.status=100 and (a.restrictOrg='0' or (a.restrictOrg='1' and c.status='100')) ";
            List<Map<String, Object>> yetlist = this.doQueryData(sql, null);

            String mySql="select * from DCP_PRODTEMPLATE a where a.eid='"+eId+"' and a.templateid='"+templateId+"'";
            List<Map<String, Object>> mylist = this.doQueryData(mySql, null);

            String myGoodsSql="select distinct " +
                    " a.pluno,nvl(b.organizationno,'') as organizationno" +
                    " from DCP_PRODTEMPLATE_GOODS a " +
                    " left join DCP_PRODTEMPLATE_RANGE b on a.templateid=b.templateid " +
                    " where a.eid='"+eId+"' and a.templateid='"+templateId+"'";
            List<Map<String, Object>> myGoodslist = this.doQueryData(myGoodsSql, null);

            if(mylist.size()>0){
                String restrictOrg = mylist.get(0).get("RESTRICTORG").toString();
                if("0".equals(restrictOrg)){
                    //要看有没有品存在了
                    if(myGoodslist.size()>0){
                        for (Map<String, Object> map : myGoodslist){
                            List<Map<String, Object>> collect = yetlist.stream().filter(x -> x.get("PLUNO").toString().equals(map.get("PLUNO").toString())).collect(Collectors.toList());
                            if(collect.size()>0){
                                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "商品"+map.get("PLUNO").toString()+"已存在有效模板！");
                            }
                        }
                    }
                }else{
                    if(myGoodslist.size()>0) {
                        for (Map<String, Object> map : myGoodslist) {
                            List<Map<String, Object>> collect = yetlist.stream().filter(x -> x.get("PLUNO").toString().equals(map.get("PLUNO").toString())
                                    && x.get("ORGANIZATIONNO").toString().equals(map.get("ORGANIZATIONNO").toString())).collect(Collectors.toList());
                            if (collect.size() > 0) {
                                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "商品" + map.get("PLUNO").toString() + "在组织"+map.get("ORGANIZATIONNO").toString()+"已存在有效模板！");
                            }
                        }
                    }
                }
            }


            UptBean ub1 = new UptBean("DCP_PRODTEMPLATE");
            ub1.addUpdateValue("STATUS", new DataValue("100", Types.VARCHAR));
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));

            UptBean ub2 = new UptBean("DCP_PRODTEMPLATE_GOODS");
            ub2.addUpdateValue("STATUS", new DataValue("100", Types.VARCHAR));
            ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub2.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub2));

            UptBean ub3 = new UptBean("DCP_PRODTEMPLATE_RANGE");
            ub3.addUpdateValue("STATUS", new DataValue("100", Types.VARCHAR));
            ub3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub3.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub3));
        }
        if("2".equals(opType)){
            UptBean ub1 = new UptBean("DCP_PRODTEMPLATE");
            ub1.addUpdateValue("STATUS", new DataValue("0", Types.VARCHAR));
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));

            UptBean ub2 = new UptBean("DCP_PRODTEMPLATE_GOODS");
            ub2.addUpdateValue("STATUS", new DataValue("0", Types.VARCHAR));
            ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub2.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub2));

            UptBean ub3 = new UptBean("DCP_PRODTEMPLATE_RANGE");
            ub3.addUpdateValue("STATUS", new DataValue("0", Types.VARCHAR));
            ub3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub3.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub3));
        }

        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_ProdTemplateEnableReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_ProdTemplateEnableReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_ProdTemplateEnableReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_ProdTemplateEnableReq req) throws Exception {
        boolean isFail = false;
        StringBuilder errMsg = new StringBuilder("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }


        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_ProdTemplateEnableReq> getRequestType() {
        return new TypeToken<DCP_ProdTemplateEnableReq>() {
        };
    }

    @Override
    protected DCP_ProdTemplateEnableRes getResponseType() {
        return new DCP_ProdTemplateEnableRes();
    }
}

