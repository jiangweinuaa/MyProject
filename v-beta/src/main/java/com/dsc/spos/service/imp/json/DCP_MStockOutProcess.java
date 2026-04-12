package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_MStockOutProcessReq;
import com.dsc.spos.json.cust.res.DCP_MStockOutProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_MStockOutProcess extends SPosAdvanceService<DCP_MStockOutProcessReq, DCP_MStockOutProcessRes> {

    @Override
    protected void processDUID(DCP_MStockOutProcessReq req, DCP_MStockOutProcessRes res) throws Exception {

        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String mStockoutNo = req.getRequest().getMStockOutNo();
        String accountDate = req.getRequest().getAccountDate();

        String createTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        //String createDate = new SimpleDateFormat("yyyyMMdd").format(new Date());

        String sql="select * from DCP_MStockOut a where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' " +
                " and a.mstockoutno='"+mStockoutNo+"' ";
        List<Map<String, Object>> list = this.doQueryData(sql, null);
        if(list.size()>0){
            String status = list.get(0).get("STATUS").toString();
            String bDate = list.get(0).get("BDATE").toString();
            String memo = list.get(0).get("MEMO").toString();
            String adjustStatus = list.get(0).get("ADJUSTSTATUS").toString();
            String omStockoutNo = list.get(0).get("OMSTOCKOUTNO").toString();
            if(!"0".equals(status)){
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "非新建状态不可过账！".toString());
            }

            String accountTime=new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());

            if(Check.NotNull(accountDate)){
                accountTime=new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new SimpleDateFormat("yyyyMMdd").parse(accountDate));
            }
            //accountTime  改成yyyy-MM-dd
            String accountTime2=new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("yyyyMMdd HH:mm:ss").parse(accountTime));

            UptBean ub1 = new UptBean("DCP_MSTOCKOUT");
            ub1.addUpdateValue("STATUS", new DataValue("1", Types.VARCHAR));

            ub1.addUpdateValue("CONFIRMOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
            ub1.addUpdateValue("CONFIRMTIME", new DataValue(createTime, Types.DATE));
            ub1.addUpdateValue("ACCOUNTOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
            ub1.addUpdateValue("ACCOUNTTIME", new DataValue(accountTime, Types.DATE));

            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
            ub1.addCondition("MSTOCKOUTNO", new DataValue(mStockoutNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));

            String detailSql="select * from DCP_MSTOCKOUT_DETAIL a where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' and a.mstockoutno='"+mStockoutNo+"'";
            List<Map<String, Object>> detailList = this.doQueryData(detailSql, null);
            List<Map<String, Object>> filterRows = detailList.stream().filter(x -> x.get("ISBUCKLE").toString().equals("1")).collect(Collectors.toList());

            if(filterRows.size()>0){
                for (Map<String, Object> oneData : filterRows){
                    String procedure = "SP_DCP_STOCKCHANGE_VX";
                    Map<Integer, Object> inputParameter = new HashMap<>();
                    inputParameter.put(1, eId);                                      //--企业ID
                    inputParameter.put(2, null);
                    inputParameter.put(3, organizationNO);
                    inputParameter.put(4, "11");
                    inputParameter.put(5, "U");
                    inputParameter.put(6, "11");                                     //--单据类型
                    inputParameter.put(7, mStockoutNo);    //--单据号
                    inputParameter.put(8, oneData.get("ITEM").toString());           //--单据行号
                    inputParameter.put(9, "0");        //序号
                    inputParameter.put(10, "-1");                                     //--异动方向 1=加库存 -1=减库存
                    inputParameter.put(11, accountTime2);          //--营业日期 yyyy-MM-dd
                    inputParameter.put(12, oneData.get("PLUNO").toString());          //--品号
                    inputParameter.put(13, oneData.get("FEATURENO").toString());                                //--特征码
                    inputParameter.put(14, oneData.get("WAREHOUSE").toString());                               //--仓库
                    inputParameter.put(15, oneData.get("BATCHNO").toString());      //--批号
                    inputParameter.put(16, oneData.get("LOCATION").toString());
                    inputParameter.put(17, oneData.get("PUNIT").toString());         //--交易单位
                    inputParameter.put(18, oneData.get("PQTY").toString());          //--交易数量
                    inputParameter.put(19, oneData.get("BASEUNIT").toString());      //--基准单位
                    inputParameter.put(20, oneData.get("BASEQTY").toString());       //--基准数量
                    inputParameter.put(21, oneData.get("UNIT_RATIO").toString());    //--换算比例
                    inputParameter.put(22, oneData.get("PRICE").toString());         //--零售价
                    inputParameter.put(23, oneData.get("AMT").toString());           //--零售金额
                    inputParameter.put(24, oneData.get("DISTRIPRICE").toString());   //--进货价
                    inputParameter.put(25, oneData.get("DISTRIAMT").toString());     //--进货金额
                    inputParameter.put(26, accountTime2);                             //--入账日期 yyyy-MM-dd
                    inputParameter.put(27, oneData.get("PRODDATE").toString());     //--批号的生产日期 yyyy-MM-dd
                    inputParameter.put(28, bDate);         //--单据日期
                    inputParameter.put(29, "");          //--异动原因
                    inputParameter.put(30, memo);          //--异动描述
                    inputParameter.put(31, req.getOpNO());

                    ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                    this.addProcessData(new DataProcessBean(pdb));
                }

            }

            if("2".equals(adjustStatus)){
                UptBean ub12 = new UptBean("DCP_MSTOCKOUT");
                ub12.addUpdateValue("ADJUSTSTATUS", new DataValue("1", Types.VARCHAR));
                ub12.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub12.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
                ub12.addCondition("MSTOCKOUTNO", new DataValue(omStockoutNo, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub12));
            }


        }


        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_MStockOutProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_MStockOutProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_MStockOutProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_MStockOutProcessReq req) throws Exception {
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
    protected TypeToken<DCP_MStockOutProcessReq> getRequestType() {
        return new TypeToken<DCP_MStockOutProcessReq>() {
        };
    }

    @Override
    protected DCP_MStockOutProcessRes getResponseType() {
        return new DCP_MStockOutProcessRes();
    }
}

