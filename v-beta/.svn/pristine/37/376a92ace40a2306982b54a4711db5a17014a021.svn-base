package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_FaPiaoProjDeleteReq;
import com.dsc.spos.json.cust.res.DCP_FaPiaoProjDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_FaPiaoProjDelete extends SPosAdvanceService<DCP_FaPiaoProjDeleteReq, DCP_FaPiaoProjDeleteRes> {
    @Override
    protected void processDUID(DCP_FaPiaoProjDeleteReq req, DCP_FaPiaoProjDeleteRes res) throws Exception {

        String eId = req.geteId();
        String opNo = req.getOpNO(); // 创建/修改人
        String opName = req.getOpName(); // 创建/修改人名称
        String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()); // 创建/修改人时间

        DCP_FaPiaoProjDeleteReq.levelRequest request = req.getRequest();

        List<DCP_FaPiaoProjDeleteReq.project> getIdList = request.getProjectIdList();
        List<String> enableList = new ArrayList<String>();//已启用列表


        if (!CollectionUtils.isEmpty(getIdList))
        {
            String sql = "";
            for (DCP_FaPiaoProjDeleteReq.project par : getIdList)
            {

                String projectId = par.getProjectId();
                sql = " select STATUS from DCP_FAPIAO_PROJ where eid='"+eId+"' and PROJECTID='"+projectId+"' ";
                List<Map<String, Object>> getData = this.doQueryData(sql, null);
                if(getData==null||getData.isEmpty())
                {
                    continue;
                }
                String status = getData.get(0).getOrDefault("STATUS","").toString();
                if ("100".equals(status))
                {
                    enableList.add(projectId);
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "项目ID("+projectId+")已启用状态，不能删除!");
                }

                DelBean db1 = new DelBean("DCP_FAPIAO_PROJ");
                db1.addCondition("EID",new DataValue(eId,Types.VARCHAR));
                db1.addCondition("PROJECTID",new DataValue(projectId,Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));
                //删除多语言
                DelBean db2 = new DelBean("DCP_FAPIAO_PROJ_LANG");
                db2.addCondition("EID",new DataValue(eId,Types.VARCHAR));
                db2.addCondition("PROJECTID",new DataValue(projectId,Types.VARCHAR));

                this.addProcessData(new DataProcessBean(db2));
            }
        }

        if (this.pData.size()>0)
        {
            this.doExecuteDataToDB();
        }

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_FaPiaoProjDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_FaPiaoProjDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_FaPiaoProjDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_FaPiaoProjDeleteReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_FaPiaoProjDeleteReq.levelRequest request = req.getRequest();
        if (request == null) {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }


        List<DCP_FaPiaoProjDeleteReq.project> params = request.getProjectIdList();
        if (!CollectionUtils.isEmpty(params)) {
            for (DCP_FaPiaoProjDeleteReq.project param : params) {
                if (Check.Null(param.getProjectId())) {
                    errMsg.append("编码不能为空值,");
                    isFail = true;
                }
            }
        } else {
            errMsg.append("编码列表不能为空值 ");
            isFail = true;
        }



        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_FaPiaoProjDeleteReq> getRequestType() {
        return new TypeToken<DCP_FaPiaoProjDeleteReq>(){};
    }

    @Override
    protected DCP_FaPiaoProjDeleteRes getResponseType() {
        return new DCP_FaPiaoProjDeleteRes();
    }

}
