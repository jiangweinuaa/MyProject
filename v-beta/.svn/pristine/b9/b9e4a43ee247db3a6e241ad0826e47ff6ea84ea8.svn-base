package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrgDeleteReq;
import com.dsc.spos.json.cust.res.DCP_OrgDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;

public class DCP_OrgDelete extends SPosAdvanceService<DCP_OrgDeleteReq, DCP_OrgDeleteRes> {

    @Override
    protected void processDUID(DCP_OrgDeleteReq req, DCP_OrgDeleteRes res) throws Exception {

        String sql = " SELECT a.ORGANIZATIONNO, a.STATUS, UP_ORG FROM DCP_ORG a " +
                " LEFT JOIN DCP_ORG_LEVEL b ON a.EID=b.EID AND a.ORGANIZATIONNO=b.ORGANIZATIONNO and a.ORG_TYPE=b.ORG_TYPE  " +
                " WHERE a.EID='%s' and ((UP_ORG='%s') or (a.ORGANIZATIONNO='%s' and a.STATUS<>-1 )) ";
        sql = String.format(sql, req.geteId(), req.getRequest().getOrganizationID(),req.getRequest().getOrganizationID());

        List<Map<String, Object>> data = this.doQueryData(sql, null);
        if (null != data && !data.isEmpty()) {
            String orgNo = String.valueOf(data.get(0).get("ORGANIZATIONNO"));
            String status = String.valueOf(data.get(0).get("STATUS"));
            res.setServiceDescription(req.getRequest().getOrganizationID()+"存在下级组织，无法删除");
            if (StringUtils.equals(req.getRequest().getOrganizationID(), orgNo) && !StringUtils.equals("-1",status)){
                res.setServiceDescription(req.getRequest().getOrganizationID()+"已启用，无法删除");
            }

            res.setSuccess(false);
            res.setServiceStatus("100");
            return;
        }




        ColumnDataValue condition = new ColumnDataValue();
        condition.add("EID", DataValues.newString(req.geteId()));
        condition.add("OrganizationNo", DataValues.newString(req.getRequest().getOrganizationID()));

        DelBean del = DataBeans.getDelBean("DCP_ORG",condition);
        this.pData.add(new DataProcessBean(del));
        DelBean del1 = DataBeans.getDelBean("DCP_ORG_LANG",condition);
        this.pData.add(new DataProcessBean(del1));
        DelBean del2 = DataBeans.getDelBean("DCP_Org_Level",condition);
        this.pData.add(new DataProcessBean(del2));
        DelBean del3 = DataBeans.getDelBean("DCP_WareHouse",condition);
        this.pData.add(new DataProcessBean(del3));
        DelBean del4 = DataBeans.getDelBean("DCP_WareHouse_Lang",condition);
        this.pData.add(new DataProcessBean(del4));
        DelBean del5 = DataBeans.getDelBean("DCP_SHOP_ACCOUNT",condition);
        this.pData.add(new DataProcessBean(del5));
        DelBean del6 = DataBeans.getDelBean("DCP_Shop_Tag",condition);
        this.pData.add(new DataProcessBean(del6));

        DelBean del7 = DataBeans.getDelBean("DCP_ORG_LICENSE",condition);
        this.pData.add(new DataProcessBean(del7));

        DelBean del8 = DataBeans.getDelBean("DCP_ORG_PLATFORM",condition);
        this.pData.add(new DataProcessBean(del8));

        //部门
        DelBean del9 = new DelBean("DCP_DEPARTMENT");
        del9.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        del9.addCondition("DEPARTNO", new DataValue(req.getRequest().getOrganizationID(), Types.VARCHAR));
        this.pData.add(new DataProcessBean(del9));

        DelBean del10 = new DelBean("DCP_DEPARTMENT_LANG");
        del10.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
        del10.addCondition("DEPARTNO", new DataValue(req.getRequest().getOrganizationID(), Types.VARCHAR));
        this.pData.add(new DataProcessBean(del10));

        //mes_route 不删


        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }


    @Override
    protected List<InsBean> prepareInsertData(DCP_OrgDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_OrgDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_OrgDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_OrgDeleteReq req) throws Exception {

        StringBuilder errMsg = new StringBuilder("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_OrgDeleteReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_OrgDeleteReq>() {
        };
    }

    @Override
    protected DCP_OrgDeleteRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_OrgDeleteRes();
    }

}
