package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_MaterialReplaceUpdateReq;
import com.dsc.spos.json.cust.res.DCP_MaterialReplaceUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_MaterialReplaceUpdate extends SPosAdvanceService<DCP_MaterialReplaceUpdateReq, DCP_MaterialReplaceUpdateRes> {

    @Override
    protected void processDUID(DCP_MaterialReplaceUpdateReq req, DCP_MaterialReplaceUpdateRes res) throws Exception {

        String eId = req.geteId();
        String createTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());

        DCP_MaterialReplaceUpdateReq.LevelElm request = req.getRequest();
        String replaceType = request.getReplaceType();
        String organizationNo = request.getOrganizationNo();
        String materialPluNo = request.getMaterialPluNo();
        String materialUnit = request.getMaterialUnit();
        String materialQty = request.getMaterialQty();
        String replacePluNo = request.getReplacePluNo();
        String replaceQty = request.getReplaceQty();
        String replaceUnit = request.getReplaceUnit();
        String replaceBDate = request.getReplaceBDate();
        String replaceEDate = request.getReplaceEDate();
        String status = request.getStatus();
        String priority = request.getPriority();
        String memo = request.getMemo();

        if("0".equals(replaceType)){
            organizationNo="ALL";
        }
        if(Check.Null(priority)){
            //入参priority为空时，根据replaceType+organizationNo+materialPluNo+materialUnit查询获取优先级最大值+1

            String sql="select max(a.priority) as priority from MES_MATERIAL_REPLACE a " +
                    " where a.eid='"+req.geteId()+"' " +
                    " and a.replacetype='"+replaceType+"' and a.organizationno='"+organizationNo+"' " +
                    " and a.material_pluno='"+materialPluNo+"' " +
                    " and a.material_unit='"+materialUnit+"' " +
                    " and a.replace_pluno !='"+replacePluNo+"' ";
            List<Map<String, Object>> list = this.doQueryData(sql, null);
            if(list.size()>0){
                priority=list.get(0).get("PRIORITY").toString();
                if(Check.Null(priority)){
                    priority="0";
                }
                priority=Integer.parseInt(priority)+1+"";
            }else{
                priority="1";
            }
        }

        UptBean ub1 = new UptBean("MES_MATERIAL_REPLACE");
        ub1.addCondition("EID", DataValues.newString(eId));
        ub1.addCondition("MATERIAL_UNIT", DataValues.newString(materialUnit));
        ub1.addCondition("REPLACE_PLUNO", DataValues.newString(replacePluNo));
        ub1.addCondition("MATERIAL_PLUNO", DataValues.newString(materialPluNo));

        ub1.addUpdateValue("ORGANIZATIONNO", DataValues.newString(organizationNo));
        ub1.addUpdateValue("STATUS", DataValues.newString(status));
        ub1.addUpdateValue("REPLACETYPE", DataValues.newString(replaceType));
        ub1.addUpdateValue("MATERIAL_QTY", DataValues.newString(materialQty));
        ub1.addUpdateValue("REPLACE_QTY", DataValues.newString(replaceQty));
        ub1.addUpdateValue("REPLACE_UNIT", DataValues.newString(replaceUnit));
        ub1.addUpdateValue("REPLACE_BDATE", DataValues.newDate(DateFormatUtils.getDateTime(replaceBDate)));
        ub1.addUpdateValue("REPLACE_EDATE", DataValues.newDate(DateFormatUtils.getDateTime(replaceEDate)));
        ub1.addUpdateValue("PRIORITY", DataValues.newString(priority));
        ub1.addUpdateValue("MEMO", DataValues.newString(memo));

        ub1.addUpdateValue("LASTMODIOPID", DataValues.newString(req.getOpNO()));
        ub1.addUpdateValue("LASTMODITIME", DataValues.newDate(createTime));


        this.addProcessData(new DataProcessBean(ub1));


        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_MaterialReplaceUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_MaterialReplaceUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_MaterialReplaceUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_MaterialReplaceUpdateReq req) throws Exception {
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
    protected TypeToken<DCP_MaterialReplaceUpdateReq> getRequestType() {
        return new TypeToken<DCP_MaterialReplaceUpdateReq>() {
        };
    }

    @Override
    protected DCP_MaterialReplaceUpdateRes getResponseType() {
        return new DCP_MaterialReplaceUpdateRes();
    }
}