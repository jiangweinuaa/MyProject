package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_UnitMsgUpdateReq;
import com.dsc.spos.json.cust.req.DCP_UnitMsgUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_UnitMsgUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.google.gson.reflect.TypeToken;

public class DCP_UnitMsgUpdate extends SPosAdvanceService<DCP_UnitMsgUpdateReq, DCP_UnitMsgUpdateRes> {

    @Override
    protected void processDUID(DCP_UnitMsgUpdateReq req, DCP_UnitMsgUpdateRes res) throws Exception {
        // TODO Auto-generated method stub
        try {
            String unit = req.getRequest().getUnit();
            String udLength = req.getRequest().getUdLength();
            String status = req.getRequest().getStatus();
            String eId = req.geteId();
            int unitType = req.getRequest().getUnitType();
            int roundType = req.getRequest().getRoundType();

            String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            List<level1Elm> datas = req.getRequest().getUnitName_lang();
            DelBean db2 = new DelBean("DCP_UNIT_LANG");
            db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db2.addCondition("UNIT", new DataValue(unit, Types.VARCHAR));

            this.addProcessData(new DataProcessBean(db2));
            if (datas != null && datas.size() > 0) {
                for (DCP_UnitMsgUpdateReq.level1Elm par : datas) {
                    String unitName = par.getName();
                    String langType = par.getLangType();

                    ColumnDataValue langValue = new ColumnDataValue();
                    langValue.add("EID", DataValues.newString(eId));
                    langValue.add("UNIT", DataValues.newString(unit));
                    langValue.add("LANG_TYPE", DataValues.newString(langType));
                    langValue.add("UNAME", DataValues.newString(unitName));

                    // 添加原因码多语言信息
                    InsBean ib2 = DataBeans.getInsBean("DCP_UNIT_LANG", langValue);
                    this.addProcessData(new DataProcessBean(ib2));
                }
            }

            ColumnDataValue condition = new ColumnDataValue();
            ColumnDataValue dataValue = new ColumnDataValue();
            condition.add("EID", DataValues.newString(eId));
            condition.add("UNIT", DataValues.newString(unit));

            dataValue.add("UDLENGTH", DataValues.newInteger(udLength));
            dataValue.add("UNITTYPE", DataValues.newInteger(unitType));
            dataValue.add("ROUNDTYPE", DataValues.newInteger(roundType));
            dataValue.add("STATUS", DataValues.newInteger(status));
            dataValue.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
            dataValue.add("LASTMODIOPNAME", DataValues.newString(req.getEmployeeName()));
            dataValue.add("LASTMODITIME", DataValues.newDate(lastmoditime));

            UptBean ub1  = DataBeans.getUptBean("DCP_UNIT", condition, dataValue);
            this.addProcessData(new DataProcessBean(ub1));

            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

        } catch (Exception e) {
            // TODO Auto-generated catch block
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行异常:" + e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_UnitMsgUpdateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_UnitMsgUpdateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_UnitMsgUpdateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_UnitMsgUpdateReq req) throws Exception {
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
    protected TypeToken<DCP_UnitMsgUpdateReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_UnitMsgUpdateReq>() {
        };
    }

    @Override
    protected DCP_UnitMsgUpdateRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_UnitMsgUpdateRes();
    }
}
