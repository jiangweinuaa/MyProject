package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_FaPiaoProjCreateReq;
import com.dsc.spos.json.cust.res.DCP_FaPiaoProjCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_FaPiaoProjUpdate extends SPosAdvanceService<DCP_FaPiaoProjCreateReq, DCP_FaPiaoProjCreateRes> {
    @Override
    protected void processDUID(DCP_FaPiaoProjCreateReq req, DCP_FaPiaoProjCreateRes res) throws Exception {

        String eId = req.geteId();
        String opNo = req.getOpNO(); // 创建/修改人
        String opName = req.getOpName(); // 创建/修改人名称
        String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()); // 创建/修改人时间

        DCP_FaPiaoProjCreateReq.levelRequest request = req.getRequest();
        String projectId = request.getProjectId();
        List<DCP_FaPiaoProjCreateReq.projectName> getLangDatas = request.getProjectName_lang();
        if (!CheckIsExist(req))
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "项目编码("+projectId+")不存在，无法修改");
        }

        UptBean up1 = new UptBean("DCP_FAPIAO_PROJ");
        up1.addCondition("EID",new DataValue(eId,Types.VARCHAR));
        up1.addCondition("PROJECTID",new DataValue(projectId,Types.VARCHAR));

        up1.addUpdateValue("TAXCODE",new DataValue(request.getTaxCode(),Types.VARCHAR));
        up1.addUpdateValue("TAXRATE",new DataValue(request.getTaxRate(),Types.VARCHAR));
        up1.addUpdateValue("TYPE",new DataValue(request.getType(),Types.VARCHAR));
        up1.addUpdateValue("STATUS",new DataValue(request.getStatus(),Types.VARCHAR));
        up1.addUpdateValue("LASTMODIOPID",new DataValue(req.getOpNO(),Types.VARCHAR));
        up1.addUpdateValue("LASTMODIOPNAME",new DataValue(req.getOpName(),Types.VARCHAR));
        up1.addUpdateValue("LASTMODITIME",new DataValue(lastmoditime,Types.DATE));
        this.addProcessData(new DataProcessBean(up1));
        //删除多语言
        DelBean db1 = new DelBean("DCP_FAPIAO_PROJ_LANG");
        db1.addCondition("EID",new DataValue(eId,Types.VARCHAR));
        db1.addCondition("PROJECTID",new DataValue(projectId,Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db1));

        if (!CollectionUtils.isEmpty(getLangDatas))
        {
            String[] columnsName = {
                    "EID","PROJECTID","LANG_TYPE","PROJECTNAME","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME"
            };
            InsBean ib2 = new InsBean("DCP_FAPIAO_PROJ_LANG", columnsName);
            for (DCP_FaPiaoProjCreateReq.projectName par : getLangDatas)
            {
                DataValue[] insValueDetail = new DataValue[]
                        {
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(projectId, Types.VARCHAR),
                                new DataValue(par.getLangType(), Types.VARCHAR),
                                new DataValue(par.getName(), Types.VARCHAR),
                                new DataValue(req.getOpNO(), Types.VARCHAR),
                                new DataValue(req.getOpName(), Types.VARCHAR),
                                new DataValue(lastmoditime, Types.DATE)
                        };

                ib2.addValues(insValueDetail);
            }
            this.addProcessData(new DataProcessBean(ib2));
        }

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_FaPiaoProjCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_FaPiaoProjCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_FaPiaoProjCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_FaPiaoProjCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_FaPiaoProjCreateReq.levelRequest request = req.getRequest();
        if (request == null) {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if(Check.Null(request.getProjectId())){
            errMsg.append("项目编码不能为空值,");
            isFail = true;
        }
        if (Check.Null(request.getProjectName())) {
            errMsg.append("项目名称不能为空值,");
            isFail = true;
        }
        if (Check.Null(request.getTaxCode())) {
            errMsg.append("税收编码不能为空值,");
            isFail = true;
        }
        if (Check.Null(request.getTaxRate())) {
            errMsg.append("税率不能为空值,");
            isFail = true;
        }
        if (Check.Null(request.getType())) {
            errMsg.append("类型不能为空值 ");
            isFail = true;
        }

        if (Check.Null(request.getStatus())) {
            errMsg.append("状态不能为空值 ");
            isFail = true;
        }

        List<DCP_FaPiaoProjCreateReq.projectName> params = request.getProjectName_lang();
        if (!CollectionUtils.isEmpty(params)) {
            for (DCP_FaPiaoProjCreateReq.projectName param : params) {
                if (Check.Null(param.getLangType())) {
                    errMsg.append("语言别不能为空值,");
                    isFail = true;
                }
                if (Check.Null(param.getName())) {
                    errMsg.append("多语言名称不能为空值,");
                    isFail = true;
                }
            }
        } else {
            errMsg.append("项目名称多语言不能为空值 ");
            isFail = true;
        }



        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_FaPiaoProjCreateReq> getRequestType() {
        return new TypeToken<DCP_FaPiaoProjCreateReq>(){};
    }

    @Override
    protected DCP_FaPiaoProjCreateRes getResponseType() {
        return new DCP_FaPiaoProjCreateRes();
    }

    private boolean CheckIsExist(DCP_FaPiaoProjCreateReq req) throws Exception
    {
        boolean flag = false;
        String sql = " select * from DCP_FAPIAO_PROJ where EID='"+req.geteId()+"' and PROJECTID='"+req.getRequest().getProjectId()+"'";
        List<Map<String,Object>> getQData = this.doQueryData(sql,null);
        if (getQData!=null&&!getQData.isEmpty())
        {
            flag = true;
        }

        return flag;
    }
}
