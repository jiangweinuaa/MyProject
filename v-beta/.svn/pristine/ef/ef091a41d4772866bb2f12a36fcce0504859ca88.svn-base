package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_MaterialReplaceCreateReq;
import com.dsc.spos.json.cust.res.DCP_MaterialReplaceCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.DateFormatUtils;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_MaterialReplaceCreate extends SPosAdvanceService<DCP_MaterialReplaceCreateReq, DCP_MaterialReplaceCreateRes> {

    @Override
    protected void processDUID(DCP_MaterialReplaceCreateReq req, DCP_MaterialReplaceCreateRes res) throws Exception {

        String eId = req.geteId();
        String createTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());


        //写入表MES_MATERIAL_REPLACE，并记录创建信息

        DCP_MaterialReplaceCreateReq.LevelElm request = req.getRequest();
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
                    " and a.material_unit='"+materialUnit+"' ";
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

        ColumnDataValue mpColumns=new ColumnDataValue();
        mpColumns.add("EID", DataValues.newString(eId));
        mpColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNo));
        mpColumns.add("STATUS", DataValues.newString(status));
        mpColumns.add("REPLACETYPE", DataValues.newString(replaceType));
        mpColumns.add("MATERIAL_PLUNO", DataValues.newString(materialPluNo));
        mpColumns.add("MATERIAL_QTY", DataValues.newString(materialQty));
        mpColumns.add("MATERIAL_UNIT", DataValues.newString(materialUnit));
        mpColumns.add("REPLACE_PLUNO", DataValues.newString(replacePluNo));
        mpColumns.add("REPLACE_QTY", DataValues.newString(replaceQty));
        mpColumns.add("REPLACE_UNIT", DataValues.newString(replaceUnit));
        mpColumns.add("REPLACE_BDATE", DataValues.newDate(DateFormatUtils.getDateTime(replaceBDate)));
        mpColumns.add("REPLACE_EDATE", DataValues.newDate(DateFormatUtils.getDateTime(replaceEDate)));
        mpColumns.add("PRIORITY", DataValues.newString(priority));
        mpColumns.add("MEMO", DataValues.newString(memo));

        mpColumns.add("CREATEOPID", DataValues.newString(req.getOpNO()));
        mpColumns.add("CREATETIME", DataValues.newDate(createTime));

        String[] mpColumnNames = mpColumns.getColumns().toArray(new String[0]);
        DataValue[] mpDataValues = mpColumns.getDataValues().toArray(new DataValue[0]);
        InsBean mpib=new InsBean("MES_MATERIAL_REPLACE",mpColumnNames);
        mpib.addValues(mpDataValues);
        this.addProcessData(new DataProcessBean(mpib));


        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_MaterialReplaceCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_MaterialReplaceCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_MaterialReplaceCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_MaterialReplaceCreateReq req) throws Exception {
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
    protected TypeToken<DCP_MaterialReplaceCreateReq> getRequestType() {
        return new TypeToken<DCP_MaterialReplaceCreateReq>() {
        };
    }

    @Override
    protected DCP_MaterialReplaceCreateRes getResponseType() {
        return new DCP_MaterialReplaceCreateRes();
    }
}