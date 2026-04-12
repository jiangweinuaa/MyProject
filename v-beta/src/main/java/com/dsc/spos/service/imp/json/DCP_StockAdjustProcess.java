package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_StockAdjustProcessReq;
import com.dsc.spos.json.cust.res.DCP_StockAdjustProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.bc.BcReq;
import com.dsc.spos.utils.bc.BcRes;
import com.google.gson.reflect.TypeToken;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

/**
 * 服務函數：DCP_StockAdjustProcess
 * 服务说明：库存调整单确认
 * @author jinzma
 * @since  2024-08-08
 */
public class DCP_StockAdjustProcess extends SPosAdvanceService<DCP_StockAdjustProcessReq, DCP_StockAdjustProcessRes> {
    @Override
    protected void processDUID(DCP_StockAdjustProcessReq req, DCP_StockAdjustProcessRes res) throws Exception {

       // try {
            String eId = req.geteId();
            String shopId = req.getShopId();
            String adjustNo = req.getRequest().getAdjustNo();
        String oprType = req.getRequest().getOprType();

        String opNo = req.getOpNO();
            String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String sTime = new SimpleDateFormat("HHmmss").format(new Date());
            String accountDate =req.getRequest().getAccountDate(); //PosPub.getAccountDate_SMS(dao, eId, shopId);

        if(Check.Null(accountDate)){
            accountDate=sDate;
        }

        String sql= " select a.status,c.ISLOCATION,d.isbatch,b.* from dcp_adjust a"
                + " inner join dcp_adjust_detail b on a.eid=b.eid and a.shopid=b.shopid and a.adjustno=b.adjustno " +
                " left join dcp_warehouse c on a.warehouse=c.warehouse and a.eid=c.eid and a.organizationno=c.organizationno " +
                " left join dcp_goods d on d.eid=a.eid and d.pluno=b.pluno "
                + " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.adjustno='"+adjustNo+"' "
                + " order by b.item";
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        if(CollUtil.isEmpty(getQData)){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, " 单据不存在! ");
        }
        String status = getQData.get(0).get("STATUS").toString();

        //审核、取消审核、过账、作废
        if("confirm".equals(oprType)){

            if(!"0".equals(status)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, " 单据状态非新建不可执行审核! ");
            }

            List<Map<String, Object>> filterRows = getQData.stream().filter(x -> (new BigDecimal(x.get("PQTY").toString()).compareTo(BigDecimal.ZERO)) != 0).collect(Collectors.toList());
            if(CollUtil.isEmpty(filterRows)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, " 单据无调整行! ");
            }

            UptBean ub2 = new UptBean("DCP_ADJUST");
            ub2.addUpdateValue("STATUS", DataValues.newString("1"));
            ub2.addUpdateValue("MODIFYBY", DataValues.newString(req.getOpNO()));
            ub2.addUpdateValue("MODIFY_DATE",DataValues.newString(sDate));
            ub2.addUpdateValue("MODIFY_TIME",DataValues.newString(sTime));
            ub2.addUpdateValue("CONFIRMBY", DataValues.newString(req.getOpNO()));
            ub2.addUpdateValue("CONFIRM_DATE", DataValues.newString(sDate));
            ub2.addUpdateValue("CONFIRM_TIME", DataValues.newString(sTime));

            ub2.addCondition("EID", DataValues.newString(req.geteId()));
            ub2.addCondition("ADJUSTNO",DataValues.newString(adjustNo));
            ub2.addCondition("ORGANIZATIONNO",DataValues.newString(req.getOrganizationNO()));
            this.addProcessData(new DataProcessBean(ub2));
            this.doExecuteDataToDB();

        }
        else if("unconfirm".equals(oprType)){
            if(!"1".equals(status)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, " 单据状态非审核不可执行取消审核! ");
            }
            UptBean ub2 = new UptBean("DCP_ADJUST");
            ub2.addUpdateValue("STATUS", DataValues.newString("0"));
            ub2.addUpdateValue("MODIFYBY", DataValues.newString(req.getOpNO()));
            ub2.addUpdateValue("MODIFY_DATE",DataValues.newString(sDate));
            ub2.addUpdateValue("MODIFY_TIME",DataValues.newString(sTime));
            ub2.addUpdateValue("CONFIRMBY", DataValues.newString(""));
            ub2.addUpdateValue("CONFIRM_DATE", DataValues.newString(""));
            ub2.addUpdateValue("CONFIRM_TIME", DataValues.newString(""));

            ub2.addCondition("EID", DataValues.newString(req.geteId()));
            ub2.addCondition("ADJUSTNO",DataValues.newString(adjustNo));
            ub2.addCondition("ORGANIZATIONNO",DataValues.newString(req.getOrganizationNO()));
            this.addProcessData(new DataProcessBean(ub2));
            this.doExecuteDataToDB();
        }
        else if("post".equals(oprType)){

            //1.单据状态非“0新建”或“1待过账”不可执行过账！
            //2.调整仓库启用库位管理，则字段「库位location」不可为空或空格！
            //3.调整商品启用批号管理【启用批号管理isBatch=Y】，且当前调整组织参数【启用批号管理Is_BatchNO=Y】，则字段「批号BATCH_NO」不可为空或空格！

            String isLocation = getQData.get(0).get("ISLOCATION").toString();
            String isBatchPara = PosPub.getPARA_SMS(dao, req.geteId(), req.getOrganizationNO(), "Is_BatchNO");
            if(!(status.equals("1")||status.equals("0"))){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, " 单据状态非审核不可执行过账! ");
            }
            if("Y".equals(isLocation)){
                for (Map<String, Object> oneQData:getQData){
                    if(Check.Null(oneQData.get("LOCATION").toString())){
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, " 库位不可为空！ ");
                    }
                }
            }
            if("Y".equals(isBatchPara)){
                for (Map<String, Object> oneQData:getQData){
                    if("Y".equals(oneQData.get("ISBATCH").toString())){
                        if(Check.Null(oneQData.get("BATCH_NO").toString())){
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, " 批号不可为空！ ");
                        }
                    }
                }
            }

            BcReq bcReq=new BcReq();
            bcReq.setServiceType("StockAdjustProcess");
            BcRes bcRes = PosPub.getBTypeAndCostCode(bcReq);
            String bType = bcRes.getBType();
            String costCode = bcRes.getCostCode();

            if(Check.Null(bType)||Check.Null(costCode)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "请先设置业务类型和成本码！");
            }

            //4.负库存检查：（仅针对调整数<0的品项）  --存储过程里面有校验了
            //-调整仓库【启用负库存管控isCheckStock=Y】，则调整商品（含库位/批号）的库存数若小于调整数（取绝对值），则报错返回提示库存不足！
            //-调整仓库【启用负库存管控isCheckStock=N】，则判断同时满足「组织参数isCheckStock=Y」&「商品参数stockManageType=“2库存不足禁止出库”」的品项为「启用负库存管控」，检查该商品（含库位/批号）的库存数若小于调整数（取绝对值），则报错返回提示库存不足！

            for (Map<String, Object> oneQData:getQData){


                //写库存流水
                String procedure = "SP_DCP_STOCKCHANGE_VX";
                Map<Integer, Object> inputParameter = new HashMap<>();
                inputParameter.put(1, eId);                                      //--企业ID
                inputParameter.put(2,"");                //--渠道
                inputParameter.put(3, shopId);                                   //--组织
                inputParameter.put(4, bType);
                inputParameter.put(5, costCode);
                inputParameter.put(6, "10");                                     //--单据类型
                inputParameter.put(7, adjustNo);                                 //--单据号
                inputParameter.put(8, oneQData.get("ITEM").toString());          //--单据行号
                inputParameter.put(9,"0");														 //单据行号（T用）
                inputParameter.put(10, "1");                                      //--异动方向 1=加库存 -1=减库存
                inputParameter.put(11, sDate);                                    //--营业日期 yyyy-MM-dd
                inputParameter.put(12, oneQData.get("PLUNO").toString());         //--品号
                inputParameter.put(13, oneQData.get("FEATURENO").toString());     //--特征码
                inputParameter.put(14, oneQData.get("WAREHOUSE").toString());    //--仓库
                inputParameter.put(15, oneQData.get("BATCH_NO").toString());     //--批号
                inputParameter.put(16,"");                          //--库位
                inputParameter.put(17, oneQData.get("PUNIT").toString());        //--交易单位
                inputParameter.put(18, oneQData.get("PQTY").toString());         //--交易数量
                inputParameter.put(19, oneQData.get("BASEUNIT").toString());     //--基准单位
                inputParameter.put(20, oneQData.get("BASEQTY").toString());      //--基准数量
                inputParameter.put(21, oneQData.get("UNIT_RATIO").toString());   //--换算比例
                inputParameter.put(22, oneQData.get("PRICE").toString());        //--零售价
                inputParameter.put(23, oneQData.get("AMT").toString());          //--零售金额
                inputParameter.put(24, oneQData.get("DISTRIPRICE").toString());  //--进货价
                inputParameter.put(25, oneQData.get("DISTRIAMT").toString());    //--进货金额
                inputParameter.put(26, accountDate);                             //--入账日期 yyyy-MM-dd
                inputParameter.put(27, oneQData.get("PROD_DATE").toString());    //--批号的生产日期 yyyy-MM-dd
                inputParameter.put(28, sDate);                                   //--单据日期
                inputParameter.put(29, "");                                      //--异动原因
                inputParameter.put(30, "退货差异调整");                            //--异动描述
                inputParameter.put(31, opNo);                                    //--操作员

                ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                this.addProcessData(new DataProcessBean(pdb));

            }


            //DCP_ADJUST
            UptBean ub1 = new UptBean("DCP_ADJUST");

            //add value
            ub1.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));
            //ub1.addUpdateValue("CONFIRMBY", new DataValue(opNo, Types.VARCHAR));
            //ub1.addUpdateValue("CONFIRM_DATE", new DataValue(sDate, Types.VARCHAR));
            //ub1.addUpdateValue("CONFIRM_TIME", new DataValue(sTime, Types.VARCHAR));
            ub1.addUpdateValue("ACCOUNTBY", new DataValue(opNo, Types.VARCHAR));
            ub1.addUpdateValue("ACCOUNT_DATE", new DataValue(accountDate, Types.VARCHAR));
            ub1.addUpdateValue("ACCOUNT_TIME", new DataValue(sTime, Types.VARCHAR));
            ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

            // condition
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
            ub1.addCondition("ADJUSTNO", new DataValue(adjustNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));


            this.doExecuteDataToDB();
        }
        else if("cancel".equals(oprType)){
            //作废：oprType=“cancel”
            //检查：
            //1.单据状态非“0新建”不可执行作废！
            //
            //处理：
            //更新单据状态=“3已作废”，记录异动信息：修改人MODIFYBY/修改日期MODIFY_DATE/修改时间MODIFY_TIME/作废人CANCELBY/作废日期CANCEL_DATE/作废时间CANCEL_TIME
            if(!"0".equals(status)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, " 单据状态非新建不可执行作废! ");
            }

            UptBean ub2 = new UptBean("DCP_ADJUST");
            ub2.addUpdateValue("STATUS", DataValues.newString("-1"));
            ub2.addUpdateValue("MODIFYBY", DataValues.newString(req.getOpNO()));
            ub2.addUpdateValue("MODIFY_DATE",DataValues.newString(sDate));
            ub2.addUpdateValue("MODIFY_TIME",DataValues.newString(sTime));
            ub2.addUpdateValue("CANCELBY", DataValues.newString(req.getOpNO()));
            ub2.addUpdateValue("CANCEL_DATE", DataValues.newString(sDate));
            ub2.addUpdateValue("CANCEL_TIME", DataValues.newString(sTime));

            ub2.addCondition("EID", DataValues.newString(req.geteId()));
            ub2.addCondition("ADJUSTNO",DataValues.newString(adjustNo));
            ub2.addCondition("ORGANIZATIONNO",DataValues.newString(req.getOrganizationNO()));
            this.addProcessData(new DataProcessBean(ub2));
            this.doExecuteDataToDB();

        }

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");



        //}catch (Exception e){
            //【ID1032555】【乐沙儿3.3.0.3】在门店管理出库单据点确定时，存在负库存时的提示返回error executing work，
            // 需要能够返回SP_DCP_StockChange返回的报错，提示门店  by jinzma 20230526
          //  String description=e.getMessage();
          //  try {
           //     StringWriter errors = new StringWriter();
           //     PrintWriter pw=new PrintWriter(errors);
           //     e.printStackTrace(pw);

           //     pw.flush();
          //      pw.close();

         //       errors.flush();
         //       errors.close();

         //       description = errors.toString();

          //      if (description.indexOf("品号")>0 && description.indexOf("库存出库")>0) {
          //          description = description.substring(description.indexOf("品号"), description.indexOf("库存出库") + 4);
          //      }

          //  } catch (Exception ignored) {

          //  }

         //   throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, description);

        //}
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_StockAdjustProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockAdjustProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockAdjustProcessReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_StockAdjustProcessReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        if (req.getRequest() == null){
            errMsg.append("request 不可为空值,");
            isFail=true;
        }else {
            if(Check.Null(req.getRequest().getAdjustNo())) {
                errMsg.append("adjustNo 不可为空值,");
                isFail=true;
            }
        }


        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_StockAdjustProcessReq> getRequestType() {
        return new TypeToken<DCP_StockAdjustProcessReq>(){};
    }

    @Override
    protected DCP_StockAdjustProcessRes getResponseType() {
        return new DCP_StockAdjustProcessRes();
    }
}
