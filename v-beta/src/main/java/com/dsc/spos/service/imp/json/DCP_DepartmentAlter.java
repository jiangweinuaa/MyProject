package com.dsc.spos.service.imp.json;


import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_DepartmentAlterReq;
import com.dsc.spos.json.cust.res.DCP_DepartmentAlterRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_DepartmentAlter extends SPosAdvanceService<DCP_DepartmentAlterReq, DCP_DepartmentAlterRes> {

    private static final String OPR_TYPE_I = "I"; //新增

    private static final String OPR_TYPE_U = "U"; //修改

    @Override
    protected void processDUID(DCP_DepartmentAlterReq req, DCP_DepartmentAlterRes res) throws Exception {
        try {

            String sql =
                    " SELECT * FROM DCP_DEPARTMENT WHERE EID='" + req.geteId() + "' AND DEPARTNO='" + req.getRequest().getDeptNo() + "' ";

            List<Map<String, Object>> qData = this.doQueryData(sql, null);

            String oprType = req.getRequest().getOprType();

            String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            if (OPR_TYPE_I.equals(oprType)) {

                if (CollectionUtils.isNotEmpty(qData)) {
                    throw new RuntimeException(req.getRequest().getDeptNo() + "已存在");
                }


                ColumnDataValue dataValue = new ColumnDataValue();
                dataValue.add("EID", DataValues.newString(req.geteId()));
                dataValue.add("DEPARTNO", DataValues.newString(req.getRequest().getDeptNo()));
                dataValue.add("UPDEPARTNO", DataValues.newString(req.getRequest().getUpperDept().trim()));
                dataValue.add("ORGANIZATIONNO", DataValues.newString(req.getRequest().getOrgNo()));
                dataValue.add("MANAGER", DataValues.newString(req.getRequest().getManager()));
                dataValue.add("STATUS", DataValues.newInteger(req.getRequest().getStatus()));
                dataValue.add("RESPONSIBILITYCENTERTYPE", DataValues.newString(req.getRequest().getResponsibilityCenterType()));
                dataValue.add("RESPCENTER", DataValues.newString(req.getRequest().getRespCenter()));
                dataValue.add("ISPRODUCTGROUP", DataValues.newString(req.getRequest().getIsProductGroup()));

                dataValue.add("CREATEBY", DataValues.newString(req.getOpNO()));
                dataValue.add("CREATE_DTIME", DataValues.newDate(lastmoditime));
                dataValue.add("MODIFYBY", DataValues.newString(req.getOpNO()));
                dataValue.add("MODIFY_DTIME", DataValues.newDate(lastmoditime));
                dataValue.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));

                InsBean ins1 = DataBeans.getInsBean("DCP_DEPARTMENT", dataValue);
                this.addProcessData(new DataProcessBean(ins1));

                for (DCP_DepartmentAlterReq.DepartLang param1 : req.getRequest().getDept_lang()) {
                    String langType = param1.getLangType();
                    String fullName = "";
                    for (DCP_DepartmentAlterReq.DeptFNameLang param : req.getRequest().getDeptFname_lang()) {
                        if (langType.equals(param.getLangType())) {
                            fullName = param.getName();
                            break;
                        }
                    }

                    ColumnDataValue lang = new ColumnDataValue();

                    lang.add("EID", DataValues.newString(req.geteId()));
                    lang.add("LANG_TYPE", DataValues.newString(langType));
                    lang.add("DEPARTNO", DataValues.newString(req.getRequest().getDeptNo()));
                    lang.add("DEPARTNAME", DataValues.newString(param1.getName()));
                    lang.add("FULLNAME", DataValues.newString(fullName));

                    InsBean ins2 = DataBeans.getInsBean("DCP_DEPARTMENT_LANG", lang);
                    this.addProcessData(new DataProcessBean(ins2));
                }

            } else if (OPR_TYPE_U.equals(oprType)) {

                if (CollectionUtils.isEmpty(qData)) {
                    throw new RuntimeException(req.getRequest().getDeptNo() + "不存在");
                }

                String oIsProductGroup = qData.get(0).get("ISPRODUCTGROUP").toString();

                if ("Y".equals(oIsProductGroup) && "N".equals(req.getRequest().getIsProductGroup())) {
                    //检查MES_PRODUCT_GROUP_GOODS
                    String querySql = " SELECT PLUNO FROM MES_PRODUCT_GROUP_GOODS WHERE PGROUPNO = '"+req.getRequest().getDeptNo()+"'";
                    List<Map<String, Object>> mData = doQueryData(querySql, null);
                    if (CollectionUtils.isNotEmpty(mData)) {
                       throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400,"班组存在商品，班组部门不可取消");
                    }
                }

                ColumnDataValue condition = new ColumnDataValue();
                ColumnDataValue dataValue = new ColumnDataValue();
                condition.add("EID", DataValues.newString(req.geteId()));
                condition.add("DEPARTNO", DataValues.newString(req.getRequest().getDeptNo()));

                dataValue.add("UPDEPARTNO", DataValues.newString(req.getRequest().getUpperDept().trim()));
                dataValue.add("ORGANIZATIONNO", DataValues.newString(req.getRequest().getOrgNo()));
                dataValue.add("MANAGER", DataValues.newString(req.getRequest().getManager()));
                dataValue.add("STATUS", DataValues.newInteger(req.getRequest().getStatus()));
                dataValue.add("RESPONSIBILITYCENTERTYPE", DataValues.newString(req.getRequest().getResponsibilityCenterType()));
                dataValue.add("RESPCENTER", DataValues.newString(req.getRequest().getRespCenter()));
                dataValue.add("ISPRODUCTGROUP", DataValues.newString(req.getRequest().getIsProductGroup()));
                dataValue.add("MODIFYBY", DataValues.newString(req.getOpNO()));
                dataValue.add("MODIFY_DTIME", DataValues.newDate(lastmoditime));

                UptBean uptBean1 = DataBeans.getUptBean("DCP_DEPARTMENT", condition, dataValue);
                this.addProcessData(new DataProcessBean(uptBean1));
                DelBean delBean = DataBeans.getDelBean("DCP_DEPARTMENT_LANG", condition);
                this.addProcessData(new DataProcessBean(delBean));

                for (DCP_DepartmentAlterReq.DepartLang param1 : req.getRequest().getDept_lang()) {
                    String langType = param1.getLangType();
                    String fullName = "";
                    for (DCP_DepartmentAlterReq.DeptFNameLang param : req.getRequest().getDeptFname_lang()) {
                        if (langType.equals(param.getLangType())) {
                            fullName = param.getName();
                            break;
                        }
                    }

                    ColumnDataValue lang = new ColumnDataValue();

                    lang.add("EID", DataValues.newString(req.geteId()));
                    lang.add("LANG_TYPE", DataValues.newString(langType));
                    lang.add("DEPARTNO", DataValues.newString(req.getRequest().getDeptNo()));
                    lang.add("DEPARTNAME", DataValues.newString(param1.getName()));
                    lang.add("FULLNAME", DataValues.newString(fullName));

                    InsBean ins2 = DataBeans.getInsBean("DCP_DEPARTMENT_LANG", lang);
                    this.addProcessData(new DataProcessBean(ins2));
                }

            }

            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行异常:" + e.getMessage());
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_DepartmentAlterReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_DepartmentAlterReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_DepartmentAlterReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_DepartmentAlterReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (req.getRequest().getDept_lang() == null) {
            errMsg.append("简称多语言不能为空");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (req.getRequest().getDeptFname_lang() == null) {
            errMsg.append("全称多语言不能为空");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (req.getRequest().getDeptFname_lang().size() != req.getRequest().getDept_lang().size()) {
            errMsg.append("简称全称无法匹配");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }


        for (DCP_DepartmentAlterReq.DepartLang par : req.getRequest().getDept_lang()) {
            if (Check.Null(par.getLangType())) {
                errMsg.append("多语言类型不可为空值, ");
                isFail = true;
            }
        }

        for (DCP_DepartmentAlterReq.DeptFNameLang par : req.getRequest().getDeptFname_lang()) {
            if (Check.Null(par.getLangType())) {
                errMsg.append("多语言类型不可为空值, ");
                isFail = true;
            }
        }

        if (Check.Null(req.getRequest().getOprType())) {
            errMsg.append("操作类型不能为空值 ");
            isFail = true;
        }

        if (Check.Null(req.getRequest().getDeptNo())) {
            errMsg.append("部门编号不能为空值 ");
            isFail = true;
        }
        if (Check.Null(req.getRequest().getUpperDept())) {
            errMsg.append("上级部门不能为空值 ");
            isFail = true;
        }
        if (Check.Null(req.getRequest().getOrgNo())) {
            errMsg.append("归属组织不能为空值 ");
            isFail = true;
        }
        if (Check.Null(req.getRequest().getStatus())) {
            errMsg.append("状态码不能为空值 ");
            isFail = true;
        }
        if (Check.Null(req.getRequest().getManager())) {
            errMsg.append("部门主管不能为空值 ");
            isFail = true;
        }

        if (req.getRequest().getDeptNo() != null && req.getRequest().getUpperDept() != null) {
            if (req.getRequest().getDeptNo().equals(req.getRequest().getUpperDept())) {
                errMsg.append("上级部门不能选择本部门！");
                isFail = true;
            }
        }


        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_DepartmentAlterReq> getRequestType() {
        return new TypeToken<DCP_DepartmentAlterReq>() {
        };

    }

    @Override
    protected DCP_DepartmentAlterRes getResponseType() {
        return new DCP_DepartmentAlterRes();
    }

    private boolean isDepartmentExist(String... keys) {
        String sql = null;
        try {
            sql = " SELECT * FROM DCP_DEPARTMENT WHERE EID='%s' AND DEPARTNO='%s' ";
            sql = String.format(sql, keys);
            List<Map<String, Object>> DepartmentDatas = this.doQueryData(sql, null);
            if (DepartmentDatas != null && DepartmentDatas.size() > 0) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

}
