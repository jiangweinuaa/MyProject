package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_StockAllocationRuleDeleteReq;
import com.dsc.spos.json.cust.res.DCP_StockAllocationRuleDeleteRes;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;
import com.dsc.spos.service.imp.json.DCP_StockAllocationRuleUpdate;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 服务函数：DCP_StockAllocationRuleDelete
 * 服务说明：分配规则设置删除
 *
 * @author wangzyc 2021-03-16
 */
public class DCP_StockAllocationRuleDelete extends SPosAdvanceService<DCP_StockAllocationRuleDeleteReq, DCP_StockAllocationRuleDeleteRes> {

    @Override
    protected void processDUID(DCP_StockAllocationRuleDeleteReq req, DCP_StockAllocationRuleDeleteRes res) throws Exception {
        /**
         * 删除逻辑：
         * 先删除规则表中对应商品规则
         * 同步删除 DCP_STOCK_CHANNEL 表中数据
         * 同步减 DCP_STOCK 预留数
         * 然后写删除类型的单据记录
         */
        String opNo = req.getOpNO();
        String opName = req.getOpName();

        Calendar cal = Calendar.getInstance();//获得当前时间
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String lastmodiTime = df.format(cal.getTime());

        DCP_StockAllocationRuleDeleteReq.level1Elm request = req.getRequest();
        String eId = req.geteId();
        List<DCP_StockAllocationRuleDeleteReq.level2Elm> pluList = request.getPluList();

        String[] billColumn = {"EID","CHANNELID","BILLNO","BILLTYPE","TOTPQTY","TOTCQTY","MEMO",
                "CREATEOPID","CREATEOPNAME","CREATETIME","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME","DEALTYPE"};

        String[] billGoodsColumn = {"EID","CHANNELID","BILLNO","BILLTYPE","ITEM","ORGANIZATIONNO","PLUNO",
                "SUNIT","FEATURENO","WAREHOUSE","DIRECTION","SQTY","BASEUNIT","BQTY"};

        String [] columns_STOCK = {"EID","ORGANIZATIONNO","PLUNO","FEATURENO","WAREHOUSE","BASEUNIT","QTY","LOCKQTY","ONLINEQTY","CREATEOPID"
                ,"CREATEOPNAME","CREATETIME","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME"};

        String sql = "";

        try {
            // 查询商品在库存分配表中的记录
            sql = getPluNoChannel(req);
            List<Map<String, Object>> pluChannels = this.doQueryData(sql, null);

            Map<String, Boolean> condition1 = new HashMap<String, Boolean>(); //查詢條件
            condition1.put("EID", true);
            condition1.put("FEATURENO", true);
            condition1.put("ORGANIZATIONNO", true);
            condition1.put("PLUNO", true);
            condition1.put("SUNIT", true);
            //调用过滤函数
            List<Map<String, Object>> listtlhead= MapDistinct.getMap(pluChannels, condition1);

            ArrayList<StockChannel> stockChannels = new ArrayList<>();
            Set<String> channels = new HashSet<>();

            for (DCP_StockAllocationRuleDeleteReq.level2Elm level2Elm : pluList) {
                String featureNo = level2Elm.getFeatureNo();
                featureNo = Check.Null(featureNo)==true?" ":featureNo;

                String organizationNo = level2Elm.getOrganizationNo();
                String pluNo = level2Elm.getPluNo();
                String sUnit = level2Elm.getSUnit();

                /**
                 * 删除规则表
                 */
                DelBean db1 = new DelBean("DCP_STOCK_ALLOCATION_RULE");
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("ORGANIZATIONNO", new DataValue(organizationNo, Types.VARCHAR));
                db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
                db1.addCondition("FEATURENO", new DataValue(featureNo, Types.VARCHAR));
                db1.addCondition("SUNIT", new DataValue(sUnit, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));

                // 查询该组织的出货仓
                String sql2 = this.getOrgWarehouse(eId,organizationNo);
                List<Map<String, Object>> getOrgWarehouse = this.doQueryData(sql2, null);
                String warehouse = "";
                if(!CollectionUtils.isEmpty(getOrgWarehouse)){
                     warehouse = getOrgWarehouse.get(0).get("OUT_COST_WAREHOUSE").toString();
                }

                StockChannel channel = new StockChannel();
                channel.setEId(eId);
                channel.setPluNo(pluNo);
                channel.setOrganizationNo(organizationNo);
                channel.setFeatureNo(featureNo);
                channel.setSUnit(sUnit);
                channel.setWarehouse(warehouse);
                channel.setChannels(new ArrayList<StockChannel.channel>());

                /**
                 * 删除库存分配表
                 */
                DelBean db2 = new DelBean("DCP_STOCK_CHANNEL");
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db2.addCondition("ORGANIZATIONNO", new DataValue(organizationNo, Types.VARCHAR));
                db2.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
                db2.addCondition("FEATURENO", new DataValue(featureNo, Types.VARCHAR));
                db2.addCondition("SUNIT", new DataValue(sUnit, Types.VARCHAR));
                db2.addCondition("WAREHOUSE", new DataValue(warehouse, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db2));

                /**
                 * 查询商品对应的库存分配表信息
                 */
                sql2 = this.getPluNoChannel(channel);
                // 查询出相关商品的 所有渠道分配
                List<Map<String, Object>> getStockChannel = this.doQueryData(sql, null);
                if(!CollectionUtils.isEmpty(getStockChannel)){
                    String baseUnit = getStockChannel.get(0).get("BASEUNIT").toString();
                    channel.setBaseunit(baseUnit);
                    for (Map<String, Object> pluStockChannel : getStockChannel) {

                        String channelid = pluStockChannel.get("CHANNELID").toString();
                        String onlineqty = pluStockChannel.get("ONLINEQTY").toString();
                        String bOnlineQty = pluStockChannel.get("BONLINEQTY").toString();
                        double bOnlineQty2 = Double.parseDouble(bOnlineQty);
                        String direction = "-1";
                        if(bOnlineQty2<0){
                            direction = "1";
                            bOnlineQty2 = bOnlineQty2*-1;
                        }

                        channels.add(channelid);

                        StockChannel.channel channel1 = channel.new channel();
                        channel1.setChannelId(channelid);
                        channel1.setWarehouseId(warehouse);
                        channel1.setBOnlineqty(bOnlineQty);
                        channel1.setOnlineqty(onlineqty);
                        channel.getChannels().add(channel1);

                        UptBean ub2 = null;
                        ub2 = new UptBean("DCP_STOCK");
                        // condition
                        ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ub2.addCondition("ORGANIZATIONNO", new DataValue(organizationNo, Types.VARCHAR));
                        ub2.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
                        ub2.addCondition("FEATURENO", new DataValue(featureNo, Types.VARCHAR));
                        ub2.addCondition("WAREHOUSE", new DataValue(warehouse, Types.VARCHAR));
                        if(direction.equals("-1")){
                            ub2.addUpdateValue("ONLINEQTY", new DataValue(bOnlineQty2, Types.VARCHAR, DataValue.DataExpression.SubSelf));
                        }else if(direction.equals("1")){
                            ub2.addUpdateValue("ONLINEQTY", new DataValue(bOnlineQty2, Types.VARCHAR, DataValue.DataExpression.UpdateSelf));
                        }

                        ub2.addUpdateValue("LASTMODITIME", new DataValue(lastmodiTime, Types.DATE));
                        ub2.addUpdateValue("LASTMODIOPID", new DataValue(opNo, Types.VARCHAR));
                        ub2.addUpdateValue("LASTMODIOPNAME", new DataValue(opName, Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(ub2));
                    }
                }
                stockChannels.add(channel);

            }

            // ************************************* 如果更改了DCP_STOCK_CHANNEL 要去写库存分配单据记录 ************************
            String billType = "QDFH001"; //BILLTYPE ，单据类型，此处根据SA要求，固定为QDFH001
            String billNo = this.queryMaxBillNo(req);
            int createbillNo = 0;
            for (String channel : channels) {
                int item = 1;
                int totcqty = 0;
                int totpqty = 0;
                boolean flag =false;  // 是否写单头
                if(createbillNo>0){
                    long i;
                    billNo = billNo.substring(4, billNo.length());
                    i = Long.parseLong(billNo) + 1;
                    billNo = i + "";
                    billNo = "QDFH" + billNo;
                }
                for (StockChannel stockChannel : stockChannels) {
                    flag = true;
                    for (StockChannel.channel stockChannelChannel : stockChannel.getChannels()) {
                        String channelId = stockChannelChannel.getChannelId();
                        if(channel.equals(channelId)){
                            if(Check.Null(stockChannelChannel.getWarehouseId())){
                                flag = false;
                            }
                            String onlineqty = stockChannelChannel.getOnlineqty();
                            String bOnlineqty = stockChannelChannel.getBOnlineqty();
                            totpqty += Double.parseDouble(onlineqty);
                            DataValue[] insValue2 = null;

                            insValue2 = new DataValue[]{
                                    new DataValue(eId, Types.VARCHAR),
                                    new DataValue(channelId, Types.VARCHAR),
                                    new DataValue(billNo, Types.VARCHAR), // 没有
                                    new DataValue(billType, Types.VARCHAR), //BILLTYPE ，单据类型，此处根据SA要求，固定为QDFH001
                                    new DataValue(item, Types.VARCHAR),
                                    new DataValue(stockChannel.getOrganizationNo(), Types.VARCHAR),

                                    new DataValue(stockChannel.getPluNo(), Types.VARCHAR),
                                    new DataValue(stockChannel.getSUnit(), Types.VARCHAR),
                                    new DataValue(stockChannel.getFeatureNo(), Types.VARCHAR),
                                    new DataValue(stockChannelChannel.getWarehouseId(), Types.VARCHAR),
                                    new DataValue("-1", Types.VARCHAR), //DIRECTION ,调整方向 ： 1增加，  -1 减少
                                    new DataValue(onlineqty, Types.VARCHAR),

                                    new DataValue(stockChannel.getBaseunit(), Types.VARCHAR),
                                    new DataValue(bOnlineqty, Types.VARCHAR)

                            };
                            InsBean ib2 = new InsBean("DCP_STOCK_CHANNEL_BILLGOODS", billGoodsColumn);
                            ib2.addValues(insValue2);
                            this.addProcessData(new DataProcessBean(ib2));
                            item++;
                        }
                    }
                    if(!CollectionUtils.isEmpty(stockChannel.getChannels())){
                        totcqty++;
                    }
                }
                if(flag){
                    DataValue[] insValue3 = null;
                    insValue3 = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(channel, Types.VARCHAR),
                            new DataValue(billNo, Types.VARCHAR),
                            new DataValue(billType, Types.VARCHAR),
                            new DataValue(totpqty, Types.VARCHAR), //总数量
                            new DataValue(totcqty, Types.VARCHAR), // 种类数（有多少个商品 ，即item）

                            new DataValue("", Types.VARCHAR),//memo
                            new DataValue(opNo, Types.VARCHAR),
                            new DataValue(opName, Types.VARCHAR),
                            new DataValue(lastmodiTime, Types.DATE),
                            new DataValue(opNo, Types.VARCHAR),
                            new DataValue(opName, Types.VARCHAR),
                            new DataValue(lastmodiTime, Types.DATE),
                            new DataValue("2", Types.VARCHAR), //dealType  0:新增   1：调整   2：删除
                    };
                    InsBean ib3 = new InsBean("DCP_STOCK_CHANNEL_BILL", billColumn);
                    ib3.addValues(insValue3);
                    this.addProcessData(new DataProcessBean(ib3));
                    createbillNo++;
                }
            }
            this.doExecuteDataToDB();
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败："+e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_StockAllocationRuleDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockAllocationRuleDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockAllocationRuleDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_StockAllocationRuleDeleteReq req) throws Exception {
        boolean isFail = false;
        List<DCP_StockAllocationRuleDeleteReq.level2Elm> pluList = req.getRequest().getPluList();
        StringBuffer errMsg = new StringBuffer("");
        if(CollectionUtils.isEmpty(pluList)){
            errMsg.append("商品列表不可为空值, ");
            isFail = true;
        }
        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_StockAllocationRuleDeleteReq> getRequestType() {
        return new TypeToken<DCP_StockAllocationRuleDeleteReq>(){};
    }

    @Override
    protected DCP_StockAllocationRuleDeleteRes getResponseType() {
        return new DCP_StockAllocationRuleDeleteRes();
    }

    /**
     * 查询组织下的出货仓
     * @param eId
     * @param organizationNo
     * @return
     */
    private String getOrgWarehouse(String eId,String organizationNo){
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT OUT_COST_WAREHOUSE FROM DCP_ORG WHERE eId = '"+eId+"' AND ORGANIZATIONNO = '"+organizationNo+"'");
        sql = sqlbuf.toString();
        return sql;
    }

    /**
     * 查询商品在库存分配表中现有的记录
     * @param req
     * @return
     */
    private String getPluNoChannel(DCP_StockAllocationRuleDeleteReq req){
        DCP_StockAllocationRuleDeleteReq.level1Elm request = req.getRequest();
        List<DCP_StockAllocationRuleDeleteReq.level2Elm> pluList = request.getPluList();
        int size = pluList.size();
        String[] organizationNoArray = new String[size] ;
        String[] plunoArray = new String[size] ;
        String[] featureNoArray = new String[size] ;
        String[] sunitArray = new String[size] ;
        int i=0;
        for (DCP_StockAllocationRuleDeleteReq.level2Elm lv2 : request.getPluList())
        {
            String organizationNo = "",pluNo = "",featureNo="",sunit="";
            if(!Check.Null(lv2.getOrganizationNo())){
                organizationNo = lv2.getOrganizationNo();
            }
            if(!Check.Null(lv2.getPluNo())){
                pluNo = lv2.getPluNo();
            }
            if(!Check.Null(lv2.getFeatureNo())){
                featureNo = lv2.getFeatureNo();;
            }else{
                featureNo = " ";
            }
            if(!Check.Null(lv2.getSUnit())){
                sunit = lv2.getSUnit();
            }
            organizationNoArray[i] = organizationNo;
            plunoArray[i] = pluNo;
            featureNoArray[i] = featureNo;
            sunitArray[i] = sunit;
            i++;
        }

        String organizationNoStr = getString(organizationNoArray);
        String plunoStr = getString(plunoArray);
        String featureStr = getString(featureNoArray);
        String sunitStr = getString(sunitArray);
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT a.EID, a.ORGANIZATIONNO, a.CHANNELID, a.PLUNO, a.FEATURENO ,a.WAREHOUSE,a.ONLINEQTY,a.LOCKQTY,a.BASEUNIT," +
                "a.BONLINEQTY ,a.BLOCKQTY, a.SUNIT " +
                "FROM DCP_STOCK_CHANNEL a " +
                " where EID = '"+req.geteId()+"'");
        if(!Check.Null(organizationNoStr)&&organizationNoStr.length()>2){
            sqlbuf.append(" AND ORGANIZATIONNO IN ("+organizationNoStr+")");
        }
        if(!Check.Null(plunoStr)&&plunoStr.length()>2){
            sqlbuf.append(" AND PLUNO IN ("+plunoStr+")");
        }
        if(!Check.Null(featureStr)&&featureStr.length()>2){
            sqlbuf.append(" AND featureNo IN ("+featureStr+")");
        }
        if(!Check.Null(sunitStr)&&sunitStr.length()>2){
            sqlbuf.append(" AND SUNIT IN ("+sunitStr+")");
        }
        sql = sqlbuf.toString();
        return sql;
    }

    /**
     * 查询商品对应的库存分配表信息
     * @param stockChannel
     * @return
     */
    private String getPluNoChannel(StockChannel stockChannel){
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append("SELECT a.EID, a.ORGANIZATIONNO, a.CHANNELID, a.PLUNO, a.FEATURENO ,a.WAREHOUSE,a.ONLINEQTY,a.LOCKQTY,a.BASEUNIT,a.BONLINEQTY ,a.BLOCKQTY, a.SUNIT " +
                " FROM DCP_STOCK_CHANNEL a " +
                " where EID = '"+stockChannel.getEId()+"' AND ORGANIZATIONNO = '"+stockChannel.getOrganizationNo()+"' AND PLUNO = '"+stockChannel.getPluNo()+"' " +
                " AND featureNo = '"+stockChannel.getFeatureNo()+"' AND SUNIT = '"+stockChannel.getSUnit()+"' AND WAREHOUSE  = '"+stockChannel.getWarehouse()+"'");
        sql = sqlbuf.toString();
        return sql;
    }

    /**
     * 获取最新单据号
     * @param req
     * @return
     * @throws Exception
     */
    private String queryMaxBillNo(DCP_StockAllocationRuleDeleteReq req) throws Exception{
        String billNo = "";
        String eId = req.geteId();
        String billType = "QDFH001";
//        String channelId = req.getRequest().getPluList().get(0).getChannelId();

//		String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String bDate = sdf.format(now);

        String sql = " select max(billNo) as billNo from DCP_STOCK_CHANNEL_BILL where eId = '"+eId+"' and billNo like 'QDFH"+bDate+"%%%' "
//				 + " and  channelId = '"+channelId+"' "
                ;

        List<Map<String, Object>> getQData = this.doQueryData(sql, null);

        if (getQData != null && getQData.isEmpty() == false) {

            billNo = (String) getQData.get(0).get("BILLNO");

            if (billNo != null && billNo.length() > 0) {
                long i;
                billNo = billNo.substring(4, billNo.length());
                i = Long.parseLong(billNo) + 1;
                billNo = i + "";
                billNo = "QDFH" + billNo;

            } else {
                billNo = "QDFH" + bDate + "00001";
            }
        } else {
            billNo = "QDFH" + bDate + "00001";
        }

        return billNo;
    }


    protected String getString(String[] str)
    {
        String str2 = "";
        for (String s:str)
        {
            str2 = str2 + "'" + s + "'"+ ",";
        }
        if (str2.length()>0)
        {
            str2=str2.substring(0,str2.length()-1);
        }

        return str2;
    }

}
