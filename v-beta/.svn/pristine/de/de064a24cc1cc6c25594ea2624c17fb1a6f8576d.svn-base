package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_UnitMsgCreateReq;
import com.dsc.spos.json.cust.req.DCP_UnitMsgCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_UnitMsgCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

public class DCP_UnitMsgCreate extends SPosAdvanceService<DCP_UnitMsgCreateReq, DCP_UnitMsgCreateRes> {

    @Override
    protected void processDUID(DCP_UnitMsgCreateReq req, DCP_UnitMsgCreateRes res) throws Exception {
        // TODO Auto-generated method stub
        String sql = null;
        try {
            String unit = req.getRequest().getUnit();
            String udLength = req.getRequest().getUdLength();
            String status = req.getRequest().getStatus();
            String eId = req.geteId();

            //单位类型：枚举值1-数量单位、2-体积单位、3-面积单位、4-长度单位、5-重量单位
            //舍入类型：1-四舍五入、2-四舍六入五成双、3-无条件舍弃、4-无条件进位
            int unitType = req.getRequest().getUnitType();
            int roundType = req.getRequest().getRoundType();

            String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            sql = this.isRepeat(unit, eId);
            List<Map<String, Object>> unitDatas = this.doQueryData(sql, null);
            if (unitDatas.isEmpty()) {

                ColumnDataValue dataValue = new ColumnDataValue();
                dataValue.add("EID", DataValues.newString(eId));
                dataValue.add("UNIT", DataValues.newString(unit));
                dataValue.add("UDLENGTH", DataValues.newInteger(udLength));
                dataValue.add("UNITTYPE", DataValues.newInteger(unitType));
                dataValue.add("ROUNDTYPE", DataValues.newInteger(roundType));
                dataValue.add("STATUS", DataValues.newInteger(status));
                dataValue.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
                dataValue.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
                dataValue.add("CREATEOPNAME", DataValues.newString(req.getEmployeeName()));
                dataValue.add("CREATETIME", DataValues.newDate(lastmoditime));
                dataValue.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
                dataValue.add("LASTMODIOPNAME", DataValues.newString(req.getEmployeeName()));
                dataValue.add("LASTMODITIME", DataValues.newDate(lastmoditime));

                InsBean ib1 = DataBeans.getInsBean("DCP_UNIT",dataValue);
                this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

                List<level1Elm> datas = req.getRequest().getUnitName_lang();
                if (datas != null && datas.size() > 0) {
                    for (level1Elm par : datas) {
                        String unitName = par.getName();
                        String langType = par.getLangType();

                        ColumnDataValue langValue = new ColumnDataValue();
                        langValue.add("EID",DataValues.newString(eId));
                        langValue.add("UNIT",DataValues.newString(unit));
                        langValue.add("LANG_TYPE",DataValues.newString(langType));
                        langValue.add("UNAME",DataValues.newString(unitName));

                        // 添加原因码多语言信息
                        InsBean ib2 = DataBeans.getInsBean("DCP_UNIT_LANG",langValue);
                        this.addProcessData(new DataProcessBean(ib2));
                    }
                }


                this.doExecuteDataToDB();
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");
            } else {
                res.setSuccess(false);
                res.setServiceStatus("200");
                res.setServiceDescription("服务执行失败: 单位信息：" + unit + "已存在 ");
                return;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行异常:" + e.getMessage());

        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_UnitMsgCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_UnitMsgCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_UnitMsgCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_UnitMsgCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        if (req.getRequest() == null) {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        // 必传值不为空
        String unit = req.getRequest().getUnit();
        List<level1Elm> datas = req.getRequest().getUnitName_lang();

        if (datas == null) {
            errMsg.append("多语言资料不能为空");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        for (level1Elm par : datas) {
            if (Check.Null(par.getLangType())) {
                errMsg.append("多语言类型不可为空值, ");
                isFail = true;
            }
        }

        if (Check.Null(unit)) {
            errMsg.append("单位编码不能为空值 ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_UnitMsgCreateReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_UnitMsgCreateReq>() {
        };
    }

    @Override
    protected DCP_UnitMsgCreateRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_UnitMsgCreateRes();
    }

    /**
     * 判断单位信息时候已存在或重复
     *
     * @param unit
     * @param eId
     * @return
     */
    private String isRepeat(String unit, String eId) {
        String sql = null;
        sql = "select * from DCP_UNIT "
                + " where UNIT = '" + unit + "' "
                + " and EID = '" + eId + "'";
        return sql;
    }

}
	
