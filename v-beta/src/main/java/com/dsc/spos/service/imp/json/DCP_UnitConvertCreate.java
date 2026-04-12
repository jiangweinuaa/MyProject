package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_UnitConvertCreateReq;
import com.dsc.spos.json.cust.res.DCP_UnitConvertCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_UnitConvertCreate extends SPosAdvanceService<DCP_UnitConvertCreateReq, DCP_UnitConvertCreateRes> {

    @Override
    protected void processDUID(DCP_UnitConvertCreateReq req, DCP_UnitConvertCreateRes res) throws Exception {
        // TODO Auto-generated method stub
        String sql = null;
        try {
            String eId = req.geteId();
            String oPId = req.getEmployeeNo();
            String oPName = req.getEmployeeName();
            String oUnit = req.getRequest().getoUnit();
            String unit = req.getRequest().getUnit();
            String unitRatio = req.getRequest().getUnitRatio();
            String deptID = req.getDepartmentNo();
            //String unitRatioReverse = String.valueOf(BigDecimalUtils.div(1, Double.parseDouble(unitRatio),3));
            String status = req.getRequest().getStatus();

            String lastmoditime = DateFormatUtils.getNowDateTime();
            //oPId =req.getOpNO();
            //oPName ="";
            sql = this.isRepeat(unit, oUnit, eId);
            List<Map<String, Object>> unitDatas = this.doQueryData(sql, null);
            if (unitDatas.size() <= 0) {
                //String[] columns1 = { "OUNIT", "UNIT", "UNIT_RATIO", "STATUS", "EID","CREATETIME" };
                String[] columns1 = {"OUNIT", "UNIT", "UNIT_RATIO", "STATUS", "EID", "CREATETIME", "CREATEOPID", "CREATEOPNAME", "CREATEDEPTID"}; // modi by 01029 20240730
                DataValue[] insValue1 = null;
                DataValue[] insValue2 = null;
                insValue1 = new DataValue[]{
                        new DataValue(oUnit, Types.VARCHAR),
                        new DataValue(unit, Types.VARCHAR),
                        new DataValue(unitRatio, Types.VARCHAR),
                        new DataValue(status, Types.VARCHAR),
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(lastmoditime, Types.DATE),
                        new DataValue(oPId, Types.VARCHAR),
                        new DataValue(oPName, Types.VARCHAR),
                        new DataValue(deptID, Types.VARCHAR),
                };

                InsBean ib1 = new InsBean("DCP_UNITCONVERT", columns1);
                ib1.addValues(insValue1);

                this.addProcessData(new DataProcessBean(ib1)); // 新增單頭
                this.doExecuteDataToDB();
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");
            } else {
                res.setSuccess(false);
                res.setServiceStatus("200");
                res.setServiceDescription("服务执行失败 : 来源单位  '" + oUnit + "' 转换为 目标单位  '" + unit + "' 的信息已存在");
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
    protected List<InsBean> prepareInsertData(DCP_UnitConvertCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_UnitConvertCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_UnitConvertCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_UnitConvertCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        if (req.getRequest() == null) {
            errMsg.append("requset不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        // 必传值不为空
        String unit = req.getRequest().getUnit();
        String oUnit = req.getRequest().getoUnit();
        if (Check.Null(unit)) {
            errMsg.append("目标单位不能为空值 ");
            isFail = true;
        }

        if (Check.Null(oUnit)) {
            errMsg.append("来源单位不能为空值 ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_UnitConvertCreateReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_UnitConvertCreateReq>() {
        };
    }

    @Override
    protected DCP_UnitConvertCreateRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_UnitConvertCreateRes();
    }

    /**
     * 判断单位转换信息是否已存在或重复
     *
     * @param unit
     * @param oUnit
     * @param eId
     * @return
     */
    private String isRepeat(String unit, String oUnit, String eId) {
        String sql = null;
        sql = "select * from DCP_UNITCONVERT "
                + " where UNIT = '" + unit + "' "
                + " and ounit = '" + oUnit + "'"
                + " and EID = '" + eId + "'"
        ;
        return sql;
    }

}
