package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_RestrictGroupDeleteReq;
import com.dsc.spos.json.cust.res.DCP_RestrictGroupDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;
import java.util.Map;

public class DCP_RestrictGroupDelete extends SPosAdvanceService<DCP_RestrictGroupDeleteReq, DCP_RestrictGroupDeleteRes> {

    @Override
    protected boolean isVerifyFail(DCP_RestrictGroupDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        //必传值不为空
        String groupType = req.getRequest().getGroupType();
        String groupNo = req.getRequest().getGroupNo();


        if(Check.Null(groupType)){
            errMsg.append("控制组类型不能为空值 ");
            isFail = true;
        }
        if(Check.Null(groupNo)){
            errMsg.append("控制组编号不能为空值 ");
            isFail = true;
        }


        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    private boolean CheckRestrictGroup(String eid, String groupType,String groupNo) throws Exception {
        String sql="select * from DCP_RESTRICTGROUP a" +
                " where a.GROUPTYPE='"+groupType+"' and a.groupno='"+groupNo+"' and a.status='-1' and a.eid='"+eid+"' ";
        List<Map<String, Object>> getQData=this.doQueryData(sql, null);

        return getQData.size()>0;
    }

    /**
     * 查询多语言信息
     */
    @Override
    protected String getQuerySql(DCP_RestrictGroupDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    protected void processDUID(DCP_RestrictGroupDeleteReq req, DCP_RestrictGroupDeleteRes res) throws Exception {
        // TODO Auto-generated method stub
        String sql = "";
        //try
       // {
            String groupType = req.getRequest().getGroupType();
            String groupNo = req.getRequest().getGroupNo();
            String eId = req.geteId();

            if(!CheckRestrictGroup(eId,groupType,groupNo)){
                res.setSuccess(false);
                res.setServiceStatus("000");
                res.setServiceDescription("非【未启用】状态数据不可删除！");
                return;
            }


            DelBean db1 = new DelBean("DCP_RESTRICTGROUP");
            db1.addCondition("GROUPTYPE", new DataValue(groupType, Types.VARCHAR));
            db1.addCondition("GROUPNO", new DataValue(groupNo, Types.VARCHAR));
            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));

            DelBean db2 = new DelBean("DCP_RESTRICTGROUP_LANG");
            db2.addCondition("GROUPTYPE", new DataValue(groupType, Types.VARCHAR));
            db2.addCondition("GROUPNO", new DataValue(groupNo, Types.VARCHAR));
            db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db2));

            DelBean db3 = new DelBean("DCP_RESTRICTGROUP_DEPT");
            db3.addCondition("GROUPTYPE", new DataValue(groupType, Types.VARCHAR));
            db3.addCondition("GROUPNO", new DataValue(groupNo, Types.VARCHAR));
            db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db3));

            DelBean db4 = new DelBean("DCP_RESTRICTGROUP_EMP");
            db4.addCondition("GROUPTYPE", new DataValue(groupType, Types.VARCHAR));
            db4.addCondition("GROUPNO", new DataValue(groupNo, Types.VARCHAR));
            db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db4));

            DelBean db5 = new DelBean("DCP_RESTRICTGROUP_ORG");
            db5.addCondition("GROUPTYPE", new DataValue(groupType, Types.VARCHAR));
            db5.addCondition("GROUPNO", new DataValue(groupNo, Types.VARCHAR));
            db5.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db5));

            DelBean db6 = new DelBean("DCP_RESTRICTGROUP_BIZPARTNER");
            db6.addCondition("GROUPTYPE", new DataValue(groupType, Types.VARCHAR));
            db6.addCondition("GROUPNO", new DataValue(groupNo, Types.VARCHAR));
            db6.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db6));

            //DelBean db6 = new DelBean("DCP_RESTRICTGROUP_GOODS");
            //db6.addCondition("GROUPTYPE", new DataValue(groupType, Types.VARCHAR));
            //db6.addCondition("GROUPNO", new DataValue(groupNo, Types.VARCHAR));
            //db6.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            //this.addProcessData(new DataProcessBean(db6));

            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
       // }
       // catch (Exception e)
       // {
        //    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, e.getMessage());
       // }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_RestrictGroupDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_RestrictGroupDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_RestrictGroupDeleteReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    protected TypeToken<DCP_RestrictGroupDeleteReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_RestrictGroupDeleteReq>(){};
    }
    @Override
    protected DCP_RestrictGroupDeleteRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_RestrictGroupDeleteRes();
    }

}
