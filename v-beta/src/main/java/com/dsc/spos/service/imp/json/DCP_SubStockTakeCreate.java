package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_SubStockTakeCreateReq;
import com.dsc.spos.json.cust.res.DCP_SubStockTakeCreateRes;
import com.dsc.spos.json.cust.res.DCP_SubStockTakeCreateRes.level1Elm;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/*
 * 服务函数：DCP_SubStockTakeCreate
 * 服务说明：盘点子任务新增
 * @author jinzma
 * @since  2021-02-24
 */
public class DCP_SubStockTakeCreate extends SPosAdvanceService<DCP_SubStockTakeCreateReq, DCP_SubStockTakeCreateRes> {
    @Override
    protected void processDUID(DCP_SubStockTakeCreateReq req, DCP_SubStockTakeCreateRes res) throws Exception {
        String eId=req.geteId();
        String shopId=req.getShopId();
        String createBy = req.getOpNO();
        String createByName = req.getOpName();
        String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        level1Elm datas = res.new level1Elm();
        try{
            String bDate = req.getRequest().getbDate();
            String stockTakeNo = req.getRequest().getStockTakeNo();
            String deviceNo = req.getRequest().getDeviceNo();
            String stockType = req.getRequest().getStockType();
            //增加单据检查
            String sql = " select a.stocktakeno,b.substocktakeno,b.stocktype,b.status,b.importstatus,b.deviceno from dcp_stocktake a"
                    + " left join dcp_substocktake b on a.eid=b.eid and a.shopid=b.shopid and a.stocktakeno=b.stocktakeno "
                    + " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.stocktakeno='"+stockTakeNo+"' and a.status='0'";
            List<Map<String, Object>> getQData = this.doQueryData(sql,null);
            if (getQData==null || getQData.isEmpty()){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "盘点单不存在或已确认!");
            }

            if (stockType.equals("1")){
                //初盘检查，不存在复盘资料且盘点单状态==0
                for (Map<String, Object> oneData : getQData) {
                    String getSubStockTakeNo= oneData.get("SUBSTOCKTAKENO").toString(); //盘点子任务单号
                    String getStatus = oneData.get("STATUS").toString(); //状态（0：新建（待盘点）； 2：已确定）
                    String getImportStatus = oneData.get("IMPORTSTATUS").toString(); //导入状态（0：未导入；100：已导入）
                    String getStockType = oneData.get("STOCKTYPE").toString(); //盘点类型（1初盘；2复盘）
                    String getDeviceno = oneData.get("DEVICENO").toString();
                    if (getStockType.equals("2")) {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "盘点子任务单号:"+getSubStockTakeNo+" 存在复盘资料,无法创建初盘单!");
                    }
                    if (!Check.Null(getDeviceno) && getStatus.equals("2") && getDeviceno.equals(deviceNo)){
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "盘点子任务单号:"+getSubStockTakeNo+" 已确定,无法创建初盘单!");
                    }
                    if (getImportStatus.equals("100")){
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "盘点子任务单号:"+getSubStockTakeNo+" 已导入,无法创建初盘单!");
                    }
                }
            }else {
                //复盘检查，存在初盘资料且初盘全部确认并完成导入，盘点单状态==0
                String subStockTakeNo = getQData.get(0).get("SUBSTOCKTAKENO").toString();
                if (Check.Null(subStockTakeNo)){
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "不存在初盘资料,无法创建复盘单!");
                }else{
                    for (Map<String, Object> oneData : getQData){
                        String getSubStockTakeNo= oneData.get("SUBSTOCKTAKENO").toString(); //盘点子任务单号
                        String getStatus = oneData.get("STATUS").toString(); //状态（0：新建（待盘点）； 2：已确定）
                        String getImportStatus = oneData.get("IMPORTSTATUS").toString(); //导入状态（0：未导入；100：已导入）
                        String getStockType = oneData.get("STOCKTYPE").toString(); //盘点类型（1初盘；2复盘）
                        String getDeviceno = oneData.get("DEVICENO").toString();
                        if (getStockType.equals("1") && (getStatus.equals("0")||getImportStatus.equals("0"))){
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "初盘盘点子任务单号:"+getSubStockTakeNo+" 未确定或导入,无法创建复盘单!");
                        }
                        if (getStockType.equals("2") && ((getStatus.equals("2") && !Check.Null(getDeviceno) && getDeviceno.equals(deviceNo)) ||getImportStatus.equals("100"))){
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "复盘盘点子任务单号:"+getSubStockTakeNo+" 已确定或已导入,无法创建复盘单!");
                        }
                    }
                }
            }

            String subStockTakeId = req.getRequest().getSubStockTakeId();
            sql="select * from dcp_substocktake"
                    + " where substocktakeid='"+subStockTakeId+"' "
                    + " or (eid='"+eId+"' and shopid='"+shopId+"' and deviceno='"+deviceNo+"' "
                    + " and stocktype='"+stockType+"' and stocktakeno='"+stockTakeNo+"') ";
            getQData.clear();
            getQData = this.doQueryData(sql,null);
            if (getQData == null || getQData.isEmpty()) {
                String memo=req.getRequest().getMemo();
                String subStockTakeNo = getSubStockTakeNo(req);

                ////添加单头数据
                String[] columns = {
                        "EID","SHOPID","SUBSTOCKTAKENO","SUBSTOCKTAKEID","STOCKTAKENO","BDATE",
                        "DEVICENO","STOCKTYPE","STATUS","MEMO","IMPORTSTATUS",
                        "CREATEOPID","CREATEOPNAME","CREATETIME"
                };
                DataValue[]	insValue = new DataValue[] {
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(shopId, Types.VARCHAR),
                        new DataValue(subStockTakeNo, Types.VARCHAR),
                        new DataValue(subStockTakeId, Types.VARCHAR),
                        new DataValue(stockTakeNo, Types.VARCHAR),
                        new DataValue(bDate, Types.VARCHAR),
                        new DataValue(deviceNo, Types.VARCHAR),
                        new DataValue(stockType, Types.VARCHAR),
                        new DataValue("0", Types.VARCHAR),
                        new DataValue(memo, Types.VARCHAR),
                        new DataValue("0", Types.VARCHAR),
                        new DataValue(createBy, Types.VARCHAR),
                        new DataValue(createByName, Types.VARCHAR),
                        new DataValue(createTime, Types.DATE)
                };

                InsBean ib = new InsBean("DCP_SUBSTOCKTAKE", columns);
                ib.addValues(insValue);
                this.addProcessData(new DataProcessBean(ib));

                this.doExecuteDataToDB();

                datas.setSubStockTakeNo(subStockTakeNo);
                res.setDatas(datas);
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");
            }
            else {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "盘点子任务已存在!");
            }
        }
        catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_SubStockTakeCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_SubStockTakeCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_SubStockTakeCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_SubStockTakeCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        String bDate = req.getRequest().getbDate();
        String stockTakeNo = req.getRequest().getStockTakeNo();
        String deviceNo = req.getRequest().getDeviceNo();
        String stockType = req.getRequest().getStockType();
        String subStockTakeId = req.getRequest().getSubStockTakeId();

        if (Check.Null(bDate)) {
            errMsg.append("单据日期不能为空,");
            isFail = true;
        }
        if (Check.Null(stockTakeNo)) {
            errMsg.append("来源盘点单不能为空,");
            isFail = true;
        }
        if (Check.Null(deviceNo)) {
            errMsg.append("设备号不能为空,");
            isFail = true;
        }
        if (Check.Null(stockType)) {
            errMsg.append("盘点类型不能为空,");
            isFail = true;
        }
        if (Check.Null(subStockTakeId)) {
            errMsg.append("盘点子任务单据唯一标识不能为空,");
            isFail = true;
        }
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_SubStockTakeCreateReq> getRequestType() {
        return new TypeToken<DCP_SubStockTakeCreateReq>(){};
    }

    @Override
    protected DCP_SubStockTakeCreateRes getResponseType() {
        return new DCP_SubStockTakeCreateRes();
    }

    private String getSubStockTakeNo(DCP_SubStockTakeCreateReq req) throws Exception {
        String eId=req.geteId();
        String shopId=req.getShopId();
        String stockTakeNo = req.getRequest().getStockTakeNo();
        String sql=" select max(substocktakeno) as substocktakeno from dcp_substocktake"
                + " where eid='"+eId+"' and shopid='"+shopId+"' and stocktakeno='"+stockTakeNo+"' ";
        List<Map<String, Object>> getQData = this.doQueryData(sql,null);
        String subStockTakeNo= getQData.get(0).get("SUBSTOCKTAKENO").toString();
        if (Check.Null(subStockTakeNo)) {
            subStockTakeNo = stockTakeNo + "_" + "001";
        }
        else {
            subStockTakeNo = subStockTakeNo.substring(subStockTakeNo.lastIndexOf("_")+1); // KCPD202102250001_002
            long i = Long.parseLong(subStockTakeNo) + 1;
            subStockTakeNo = i + "";
            if (subStockTakeNo.length()==1)
                subStockTakeNo = "00"+subStockTakeNo;
            if (subStockTakeNo.length()==2)
                subStockTakeNo = "0"+subStockTakeNo;
            subStockTakeNo = stockTakeNo+"_"+subStockTakeNo;
        }
        return subStockTakeNo;
    }


}
