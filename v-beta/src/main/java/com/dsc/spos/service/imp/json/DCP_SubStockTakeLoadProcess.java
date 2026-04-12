package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_SubStockTakeLoadProcessReq;
import com.dsc.spos.json.cust.res.DCP_SubStockTakeLoadProcessRes;
import com.dsc.spos.json.cust.res.DCP_SubStockTakeLoadProcessRes.level1Elm;
import com.dsc.spos.json.cust.res.DCP_SubStockTakeLoadProcessRes.level2Elm;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

/*
 * 服务函数：DCP_SubStockTakeLoadProcess
 * 服务说明：盘点子任务导入
 * @author jinzma
 * @since  2021-03-09
 */
public class DCP_SubStockTakeLoadProcess extends SPosAdvanceService<DCP_SubStockTakeLoadProcessReq, DCP_SubStockTakeLoadProcessRes> {
    @Override
    protected void processDUID(DCP_SubStockTakeLoadProcessReq req, DCP_SubStockTakeLoadProcessRes res) throws Exception {
        String eId = req.geteId();
        String shopId = req.getShopId();
        String stockTakeNo = req.getRequest().getStockTakeNo();
        String stockType = req.getRequest().getStockType();  //盘点类型（1初盘；2复盘）
        String companyId = req.getBELFIRM();
        String lastModiOpId = req.getOpNO();
        String lastModiOpName = req.getOpName();
        String lastModiTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        StringBuffer docType = new StringBuffer(); //0-全盘  1-抽盘  2-模板
        StringBuffer warehouse = new StringBuffer();
        StringBuffer bdate = new StringBuffer();
        level1Elm datas = res.new level1Elm();
        datas.setSubStockTakeList(new ArrayList<level2Elm>());
        try{
            //获取公司编号
            if (Check.Null(companyId)) {
                String sql=" select belfirm from dcp_org where eid='"+eId+"' and organizationno='"+shopId+"' ";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                companyId = getQData.get(0).get("BELFIRM").toString();
            }
            //单据检查
            StringBuffer errorString = new StringBuffer();
            List<String> subStockTakeNos = new ArrayList<String>();
            if (checkBill(req,errorString,docType,warehouse,bdate,subStockTakeNos)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errorString.toString());
            }
            if (subStockTakeNos==null || subStockTakeNos.isEmpty()){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "无满足条件的盘点子任务单据!");
            }
            //查询到的子任务单号和实际处理的子任务单号保持一致,后续更新子任务的导入状态
            String subStockTakeNo="";
            for (String oneSubStockTakeNo:subStockTakeNos){
                subStockTakeNo = subStockTakeNo + "'"+oneSubStockTakeNo+"',";
            }
            subStockTakeNo = subStockTakeNo.substring(0,subStockTakeNo.length()-1);
            String sql = " select stock.*,goods.baseunit,goods.cunit as punit,u1.unitratio,u2.udlength from ("
                    + " select b.pluno,b.featureno,sum(b.baseqty) as baseqty,"
                    + " max(b.ref_baseqty) keep(dense_rank first order by b.tran_time) ref_baseqty"
                    + " from dcp_substocktake a"
                    + " inner join dcp_substocktake_detail b on a.eid=b.eid and a.shopid=b.shopid and a.substocktakeno=b.substocktakeno"
                    + " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.stocktakeno='"+stockTakeNo+"' "
                    + " and a.stocktype='"+stockType+"' and a.substocktakeno in ("+subStockTakeNo+")"
                    + " group by pluno,featureno"
                    + " )stock"
                    + " left join dcp_goods goods on stock.pluno=goods.pluno and goods.eid='"+eId+"'"
                    + " left join dcp_goods_unit u1 on u1.eid='"+eId+"' and goods.pluno=u1.pluno and goods.cunit=u1.ounit and goods.baseunit=u1.unit"
                    + " left join dcp_unit u2 on u2.eid='"+eId+"' and goods.cunit=u2.unit"
                    + " order by stock.pluno,stock.featureno"
                    + " ";
            List<Map<String, Object>> getQData = this.doQueryData(sql,null);
            if (getQData == null || getQData.isEmpty()) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "无满足条件的盘点子任务单据!");
            }
            //获取商品价格和进货价
            MyCommon mc = new MyCommon();
            List<Map<String, Object>> getPrice = mc.getSalePrice_distriPrice(dao, eId, companyId, shopId, getQData,companyId);
            //1初盘
            if (stockType.equals("1")){
                fStockTake(req,docType.toString(),warehouse.toString(),bdate.toString(),getQData,getPrice);
            }
            //2复盘
            if (stockType.equals("2")){
                rStockTake(req,docType.toString(),warehouse.toString(),bdate.toString(),getQData,getPrice);
            }

            //盘点子任务处理 && 返回处理
            for (String oneSubStockTakeNo:subStockTakeNos){
                //修改子任务状态 DCP_SUBSTOCKTAKE
                UptBean ub = new UptBean("DCP_SUBSTOCKTAKE");
                //add Value
                ub.addUpdateValue("IMPORTSTATUS",new DataValue("100", Types.VARCHAR)); // 导入状态（0：未导入；100：已导入）
                ub.addUpdateValue("LASTMODIOPID",new DataValue(lastModiOpId, Types.VARCHAR));
                ub.addUpdateValue("LASTMODIOPNAME",new DataValue(lastModiOpName, Types.VARCHAR));
                ub.addUpdateValue("LASTMODITIME",new DataValue(lastModiTime, Types.DATE));
                //condition
                ub.addCondition("EID",new DataValue(eId, Types.VARCHAR));
                ub.addCondition("SHOPID",new DataValue(shopId, Types.VARCHAR));
                ub.addCondition("SUBSTOCKTAKENO",new DataValue(oneSubStockTakeNo, Types.VARCHAR));

                this.addProcessData(new DataProcessBean(ub));

                //返回处理
                level2Elm oneLv2 = res.new level2Elm();
                oneLv2.setSubStockTakeNo(oneSubStockTakeNo);
                datas.getSubStockTakeList().add(oneLv2);
            }

            this.doExecuteDataToDB();

            //复盘重新再计算一次单头的总数量、总金额等
            try {
                if (stockType.equals("2")) {
                    sql = " select max(item) as totcqty,sum(pqty) as totpqty,sum(amt) as totamt,sum(distriamt) as totdistriamt"
                            + " from dcp_stocktake_detail a"
                            + " where a.eid='" + eId + "' and a.shopid='" + shopId + "' and a.stocktakeno='" + stockTakeNo + "'";
                    List<Map<String, Object>> getStocktakeQData = this.doQueryData(sql, null);
                    String totPqty = getStocktakeQData.get(0).get("TOTPQTY").toString();
                    String totAmt = getStocktakeQData.get(0).get("TOTAMT").toString();
                    String totDistriAmt = getStocktakeQData.get(0).get("TOTDISTRIAMT").toString();
                    String totCqty = getStocktakeQData.get(0).get("TOTCQTY").toString();

                    //修改盘点单单头资料
                    UptBean ub = new UptBean("DCP_STOCKTAKE");
                    ub.addUpdateValue("TOT_PQTY", new DataValue(totPqty, Types.VARCHAR));
                    ub.addUpdateValue("TOT_AMT", new DataValue(totAmt, Types.VARCHAR));
                    ub.addUpdateValue("TOT_CQTY", new DataValue(totCqty, Types.VARCHAR));
                    ub.addUpdateValue("TOT_DISTRIAMT", new DataValue(totDistriAmt, Types.VARCHAR));
                    // condition
                    ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
                    ub.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub.addCondition("STOCKTAKENO", new DataValue(stockTakeNo, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub));
                }
            }catch (Exception e){

            }

            res.setDatas(datas);
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

        }catch (Exception e){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_SubStockTakeLoadProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_SubStockTakeLoadProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_SubStockTakeLoadProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_SubStockTakeLoadProcessReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        String stockTakeNo = req.getRequest().getStockTakeNo();
        String stockType = req.getRequest().getStockType();

        if (Check.Null(stockTakeNo)) {
            errMsg.append("盘点单号不能为空,");
            isFail = true;
        }
        if (Check.Null(stockType)) {
            errMsg.append("导入盘点类型不能为空,");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_SubStockTakeLoadProcessReq> getRequestType() {
        return new TypeToken<DCP_SubStockTakeLoadProcessReq>(){};
    }

    @Override
    protected DCP_SubStockTakeLoadProcessRes getResponseType() {
        return new DCP_SubStockTakeLoadProcessRes();
    }

    //盘点子任务导入前单据检查
    private boolean checkBill(DCP_SubStockTakeLoadProcessReq req,StringBuffer errorString,StringBuffer docType,StringBuffer warehouse,StringBuffer bdate,List<String> subStockTakeNos) throws Exception{
        String eId = req.geteId();
        String shopId = req.getShopId();
        String stockTakeNo = req.getRequest().getStockTakeNo();
        String stockType = req.getRequest().getStockType();  //盘点类型（1初盘；2复盘）
        String subStockImport="0"; //0初复均未导入；1初盘导入；2初复盘均导入
        if (stockType.equals("2"))
            subStockImport="1";
        String sql = " select a.stocktakeno,a.doc_type,a.warehouse,a.bdate,b.substocktakeno,b.stocktype,b.status,b.importstatus from dcp_stocktake a"
                + " left join dcp_substocktake b on a.eid=b.eid and a.shopid=b.shopid and a.stocktakeno=b.stocktakeno "
                + " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.stocktakeno='"+stockTakeNo+"' and a.status='0' and a.substockimport='"+subStockImport+"' ";
        List<Map<String, Object>> getQData = this.doQueryData(sql,null);
        //检查盘点单状态==0 新增状态
        if (getQData==null || getQData.isEmpty()){
            errorString.append("盘点单导入状态异常或单据已确认!");
            return true;
        }
        docType.append(getQData.get(0).get("DOC_TYPE").toString()); //0-全盘  1-抽盘  2-模板
        warehouse.append(getQData.get(0).get("WAREHOUSE").toString());
        bdate.append(getQData.get(0).get("BDATE").toString());

        if (stockType.equals("1")){
            //初盘检查，初盘盘点子任务必须是“已完成”且“未导入”，存在未完成（未提交）的盘点子任务，报错：以下xxxx盘点录入单未完成提交，请检查谢谢！
            for (Map<String, Object> oneData : getQData) {
                String getSubStockTakeNo= oneData.get("SUBSTOCKTAKENO").toString(); //盘点子任务单号
                String getStatus = oneData.get("STATUS").toString(); //状态（0：新建（待盘点）； 2：已确定）
                String getImportStatus = oneData.get("IMPORTSTATUS").toString(); //导入状态（0：未导入；100：已导入）
                String getStockType = oneData.get("STOCKTYPE").toString(); //盘点类型（1初盘；2复盘）
                if (getStockType.equals("2")) {
                    errorString.append("存在复盘资料,复盘子任务单号:"+getSubStockTakeNo+" ,无法导入初盘单!");
                    return true;
                }
                if (getStatus.equals("0")){
                    errorString.append("盘点子任务单号:"+getSubStockTakeNo+" 未完成提交,无法导入初盘单!");
                    return true;
                }
                if (getImportStatus.equals("100")){
                    errorString.append("盘点子任务单号:"+getSubStockTakeNo+" 已导入,无法再导入初盘单!");
                    return true;
                }
                subStockTakeNos.add(getSubStockTakeNo);
            }
        }else {
            //复盘检查，存在初盘资料且初盘全部确认并完成导入，盘点单状态==0
            String subStockTakeNo = getQData.get(0).get("SUBSTOCKTAKENO").toString();
            if (Check.Null(subStockTakeNo)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "不存在初盘资料,无法导入复盘单!");
            }else{
                for (Map<String, Object> oneData : getQData){
                    String getSubStockTakeNo= oneData.get("SUBSTOCKTAKENO").toString(); //盘点子任务单号
                    String getStatus = oneData.get("STATUS").toString(); //状态（0：新建（待盘点）； 2：已确定）
                    String getImportStatus = oneData.get("IMPORTSTATUS").toString(); //导入状态（0：未导入；100：已导入）
                    String getStockType = oneData.get("STOCKTYPE").toString(); //盘点类型（1初盘；2复盘）
                    if (getStockType.equals("1") && (getStatus.equals("0")||getImportStatus.equals("0"))){
                        errorString.append("初盘盘点子任务单号:"+getSubStockTakeNo+" 未确定或导入,无法导入复盘单!");
                        return true;
                    }
                    if (getStatus.equals("0")){
                        errorString.append("盘点子任务单号:"+getSubStockTakeNo+" 未完成提交,无法导入复盘单!");
                        return true;
                    }
                    if (getStockType.equals("2") && getImportStatus.equals("100")){
                        errorString.append("复盘盘点子任务单号:"+getSubStockTakeNo+" 已导入,无法再导入复盘单!");
                        return true;
                    }
                    if (getStockType.equals("2") && getStatus.equals("2") && getImportStatus.equals("0")){
                        subStockTakeNos.add(getSubStockTakeNo);
                    }
                }
            }
        }
        return false;
    }
    //初盘处理
    private void fStockTake(DCP_SubStockTakeLoadProcessReq req,String docType,String warehouse,String bdate,List<Map<String, Object>> getQData,List<Map<String, Object>> getPrice) throws Exception{
        String eId = req.geteId();
        String shopId = req.getShopId();
        String stockTakeNo = req.getRequest().getStockTakeNo();
        String modifyBy = req.getOpNO();
        Calendar cal = Calendar.getInstance();//获得当前时间
        SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
        String modifyDate = dfDate.format(cal.getTime());
        SimpleDateFormat dfTime = new SimpleDateFormat("HHmmss");
        String modifyTime = dfTime.format(cal.getTime());
        BigDecimal totPqty = new BigDecimal("0");
        BigDecimal totAmt = new BigDecimal("0");
        BigDecimal totDistriAmt = new BigDecimal("0");

        //docType 0-全盘  1-抽盘  2-模板
        if (docType.equals("2")){
            //修改 DCP_STOCKTAKE_DETAIL
            UptBean ub = new UptBean("DCP_STOCKTAKE_DETAIL");
            //add Value
            ub.addUpdateValue("PQTY",new DataValue("0", Types.VARCHAR));
            ub.addUpdateValue("BASEQTY",new DataValue("0", Types.VARCHAR));
            ub.addUpdateValue("AMT",new DataValue("0", Types.VARCHAR));
            ub.addUpdateValue("DISTRIAMT",new DataValue("0", Types.VARCHAR));
            ub.addUpdateValue("FQTY",new DataValue("0", Types.VARCHAR));
            ub.addUpdateValue("FBASEQTY",new DataValue("0", Types.VARCHAR));
            ub.addUpdateValue("RQTY",new DataValue("0", Types.VARCHAR));
            ub.addUpdateValue("RBASEQTY",new DataValue("0", Types.VARCHAR));
            //condition
            ub.addCondition("EID",new DataValue(eId, Types.VARCHAR));
            ub.addCondition("SHOPID",new DataValue(shopId, Types.VARCHAR));
            ub.addCondition("STOCKTAKENO",new DataValue(stockTakeNo, Types.VARCHAR));

            this.addProcessData(new DataProcessBean(ub));
        }else{
            //删除 DCP_STOCKTAKE_DETAIL
            DelBean db = new DelBean("DCP_STOCKTAKE_DETAIL");
            db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
            db.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
            db.addCondition("STOCKTAKENO", new DataValue(stockTakeNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db));
        }
        String[] columns = {
                "EID","SHOPID","ORGANIZATIONNO","STOCKTAKENO","WAREHOUSE","ITEM","PLUNO","FEATURENO",
                "BATCH_NO","PROD_DATE","PQTY","PUNIT","BASEQTY","BASEUNIT","UNIT_RATIO","REF_BASEQTY",
                "PRICE","DISTRIPRICE","AMT","DISTRIAMT",
                "MEMO","BDATE","OITEM",
                "FQTY","FBASEQTY","RQTY","RBASEQTY"
        };
        int i = 0;
        for (Map<String, Object> oneData : getQData){
            i++;
            String pluNo = oneData.get("PLUNO").toString();
            String featureNo = oneData.get("FEATURENO").toString();
            String punit = oneData.get("PUNIT").toString();
            String baseUnit = oneData.get("BASEUNIT").toString();
            String baseQty = oneData.get("BASEQTY").toString();
            String unitRatio = oneData.get("UNITRATIO").toString();
            String refBaseQty = oneData.get("REF_BASEQTY").toString();
            String udlength = oneData.get("UDLENGTH").toString();
            if (!PosPub.isNumeric(udlength))
                udlength="0";
            //商品盘点单位或者基准单位或者盘点单位转换率不存在，异常退出
            if (Check.Null(baseUnit) || Check.Null(punit) || Check.Null(unitRatio)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品:"+pluNo+"的盘点单位或盘点单位转换率不存在!");
            }
            //计算pqty
            BigDecimal baseQty_b = new BigDecimal(baseQty);
            BigDecimal unitRatio_b = new BigDecimal(unitRatio);
            BigDecimal pqty_b = baseQty_b.divide(unitRatio_b, Integer.parseInt(udlength),BigDecimal.ROUND_HALF_UP); // =baseQty / unitRatio
            String pqty=pqty_b.toPlainString();

            //商品取价
            Map<String, Object> condiV=new HashMap<String, Object>();
            condiV.put("PLUNO",pluNo);
            condiV.put("PUNIT",punit);
            List<Map<String, Object>> priceList= MapDistinct.getWhereMap(getPrice, condiV, false);
            String price="0";
            String distriPrice="0";
            String amt="0";
            String distriAmt="0";
            if(priceList!=null && priceList.size()>0 ) {
                price=priceList.get(0).get("PRICE").toString();
                distriPrice=priceList.get(0).get("DISTRIPRICE").toString();
                BigDecimal price_b = new BigDecimal(price);
                BigDecimal distriPrice_b = new BigDecimal(distriPrice);
                amt = price_b.multiply(pqty_b).toString();
                distriAmt = distriPrice_b.multiply(pqty_b).toString();
            }
            totPqty = totPqty.add(pqty_b);
            totAmt = totAmt.add(new BigDecimal(amt));
            totDistriAmt = totDistriAmt.add(new BigDecimal(distriAmt));

            //docType 0-全盘  1-抽盘  2-模板
            if (docType.equals("2")){
                UptBean ub = new UptBean("DCP_STOCKTAKE_DETAIL");
                //add Value
                ub.addUpdateValue("PQTY",new DataValue(pqty, Types.VARCHAR));
                ub.addUpdateValue("PUNIT",new DataValue(punit, Types.VARCHAR));
                ub.addUpdateValue("BASEQTY",new DataValue(baseQty, Types.VARCHAR));
                ub.addUpdateValue("BASEUNIT",new DataValue(baseUnit, Types.VARCHAR));
                ub.addUpdateValue("UNIT_RATIO",new DataValue(unitRatio, Types.VARCHAR));
                ub.addUpdateValue("PRICE",new DataValue(price, Types.VARCHAR));
                ub.addUpdateValue("DISTRIPRICE",new DataValue(distriPrice, Types.VARCHAR));
                ub.addUpdateValue("AMT",new DataValue(amt, Types.VARCHAR));
                ub.addUpdateValue("DISTRIAMT",new DataValue(distriAmt, Types.VARCHAR));
                ub.addUpdateValue("REF_BASEQTY",new DataValue(refBaseQty, Types.VARCHAR));
                ub.addUpdateValue("FQTY",new DataValue(pqty, Types.VARCHAR));
                ub.addUpdateValue("FBASEQTY",new DataValue(baseQty, Types.VARCHAR));
                ub.addUpdateValue("RQTY",new DataValue("0", Types.VARCHAR));
                ub.addUpdateValue("RBASEQTY",new DataValue("0", Types.VARCHAR));
                //condition
                ub.addCondition("EID",new DataValue(eId, Types.VARCHAR));
                ub.addCondition("SHOPID",new DataValue(shopId, Types.VARCHAR));
                ub.addCondition("STOCKTAKENO",new DataValue(stockTakeNo, Types.VARCHAR));
                ub.addCondition("PLUNO",new DataValue(pluNo, Types.VARCHAR));
                ub.addCondition("FEATURENO",new DataValue(featureNo, Types.VARCHAR));

                this.addProcessData(new DataProcessBean(ub));
            }else {
                //添加库存盘点明细 DCP_STOCKTAKE_DETAIL
                DataValue[] insValue = new DataValue[]{
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(shopId, Types.VARCHAR),
                        new DataValue(shopId, Types.VARCHAR),
                        new DataValue(stockTakeNo, Types.VARCHAR),
                        new DataValue(warehouse, Types.VARCHAR),
                        new DataValue(String.valueOf(i), Types.VARCHAR),
                        new DataValue(pluNo, Types.VARCHAR),
                        new DataValue(featureNo, Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),
                        new DataValue(pqty, Types.VARCHAR),
                        new DataValue(punit, Types.VARCHAR),
                        new DataValue(baseQty, Types.VARCHAR),
                        new DataValue(baseUnit, Types.VARCHAR),
                        new DataValue(unitRatio, Types.VARCHAR),
                        new DataValue(refBaseQty, Types.VARCHAR),
                        new DataValue(price, Types.VARCHAR),
                        new DataValue(distriPrice, Types.VARCHAR),
                        new DataValue(amt, Types.VARCHAR),
                        new DataValue(distriAmt, Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),
                        new DataValue(bdate, Types.VARCHAR),
                        new DataValue("0", Types.VARCHAR),
                        new DataValue(pqty, Types.VARCHAR),
                        new DataValue(baseQty, Types.VARCHAR),
                        new DataValue("0", Types.VARCHAR),
                        new DataValue("0", Types.VARCHAR),
                };
                InsBean ib = new InsBean("DCP_STOCKTAKE_DETAIL", columns);
                ib.addValues(insValue);
                this.addProcessData(new DataProcessBean(ib));
            }
        }

        //修改盘点单单头资料
        UptBean ub = new UptBean("DCP_STOCKTAKE");
        ub.addUpdateValue("MODIFY_CHATUSERID", new DataValue(req.getChatUserId(), Types.VARCHAR));
        ub.addUpdateValue("MODIFYBY", new DataValue(modifyBy, Types.VARCHAR));
        ub.addUpdateValue("MODIFY_DATE", new DataValue(modifyDate, Types.VARCHAR));
        ub.addUpdateValue("MODIFY_TIME", new DataValue(modifyTime, Types.VARCHAR));
        ub.addUpdateValue("TOT_PQTY", new DataValue(totPqty.toPlainString(), Types.VARCHAR));
        ub.addUpdateValue("TOT_AMT", new DataValue(totAmt.toPlainString(), Types.VARCHAR));
        //docType 0-全盘  1-抽盘  2-模板
        if (!docType.equals("2")) {
            ub.addUpdateValue("TOT_CQTY", new DataValue(String.valueOf(i), Types.VARCHAR));
        }
        ub.addUpdateValue("TOT_DISTRIAMT", new DataValue(totDistriAmt.toPlainString(), Types.VARCHAR));
        ub.addUpdateValue("SUBSTOCKIMPORT", new DataValue("1", Types.VARCHAR)); // 0初复均未导入；1初盘导入；2初复盘均导入

        // condition
        ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        ub.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
        ub.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
        ub.addCondition("STOCKTAKENO", new DataValue(stockTakeNo, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(ub));
    }
    //复盘处理
    private void rStockTake(DCP_SubStockTakeLoadProcessReq req,String docType,String warehouse,String bdate,List<Map<String, Object>> getQData,List<Map<String, Object>> getPrice) throws Exception {
        String eId = req.geteId();
        String shopId = req.getShopId();
        String stockTakeNo = req.getRequest().getStockTakeNo();
        String modifyBy = req.getOpNO();
        Calendar cal = Calendar.getInstance();//获得当前时间
        SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
        String modifyDate = dfDate.format(cal.getTime());
        SimpleDateFormat dfTime = new SimpleDateFormat("HHmmss");
        String modifyTime = dfTime.format(cal.getTime());

        String sql =" select count(*) over() num,a.*,"
                + " b.tot_cqty,b.tot_pqty,b.tot_amt,b.tot_distriamt"
                + " from dcp_stocktake_detail a"
                + " inner join dcp_stocktake b on a.eid=b.eid and a.shopid=b.shopid and a.stocktakeno=b.stocktakeno"
                + " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.stocktakeno='"+stockTakeNo+"' "
                + " and b.status='0' and b.substockimport='1' ";
        List<Map<String, Object>> getStocktakeQData = this.doQueryData(sql,null);
        if (getQData == null || getQData.isEmpty()) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "盘点单单身没有查询到数据，无法复盘提交!");
        }
        int i =Integer.parseInt(getStocktakeQData.get(0).get("NUM").toString());  //获取单身最大ITEM

        //处理单头几个汇总金额
        String totPqty = getStocktakeQData.get(0).get("TOT_PQTY").toString();
        String totAmt = getStocktakeQData.get(0).get("TOT_AMT").toString();
        String totDistriAmt = getStocktakeQData.get(0).get("TOT_DISTRIAMT").toString();

        BigDecimal totPqty_b = new BigDecimal(totPqty);
        BigDecimal totAmt_b = new BigDecimal(totAmt);
        BigDecimal totDistriAmt_b = new BigDecimal(totDistriAmt);

        for (Map<String, Object> oneData : getQData){
            String pluNo = oneData.get("PLUNO").toString();
            String featureNo = oneData.get("FEATURENO").toString();
            String punit = oneData.get("PUNIT").toString();
            String baseUnit = oneData.get("BASEUNIT").toString();
            String baseQty = oneData.get("BASEQTY").toString();
            String unitRatio = oneData.get("UNITRATIO").toString();
            String refBaseQty = oneData.get("REF_BASEQTY").toString();
            String udlength = oneData.get("UDLENGTH").toString();

            if (!PosPub.isNumeric(udlength))
                udlength="0";
            //商品盘点单位或者基准单位或者盘点单位转换率不存在，异常退出
            if (Check.Null(baseUnit) || Check.Null(punit) || Check.Null(unitRatio)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品:"+pluNo+"的盘点单位或盘点单位转换率不存在!");
            }
            //计算pqty
            BigDecimal baseQty_b = new BigDecimal(baseQty);
            BigDecimal unitRatio_b = new BigDecimal(unitRatio);
            BigDecimal pqty_b = baseQty_b.divide(unitRatio_b, Integer.parseInt(udlength),BigDecimal.ROUND_HALF_UP); // =baseQty / unitRatio
            String pqty=pqty_b.toPlainString();

            //商品取价
            Map<String, Object> condiV=new HashMap<String, Object>();
            condiV.put("PLUNO",pluNo);
            condiV.put("PUNIT",punit);
            List<Map<String, Object>> priceList= MapDistinct.getWhereMap(getPrice, condiV, false);
            String price="0";
            String distriPrice="0";
            String amt="0";
            String distriAmt="0";
            if(priceList!=null && priceList.size()>0 ) {
                price=priceList.get(0).get("PRICE").toString();
                distriPrice=priceList.get(0).get("DISTRIPRICE").toString();
                BigDecimal price_b = new BigDecimal(price);
                BigDecimal distriPrice_b = new BigDecimal(distriPrice);
                amt = price_b.multiply(pqty_b).toString();
                distriAmt = distriPrice_b.multiply(pqty_b).toString();
            }

            //盘点单单身
            String getPqty = "0";
            String getFqty = "0";
            String getAmt = "0";
            String getDistriAmt = "0";
            String getUnitRatio = "1";
            String getPunit="";
            String getBaseQty="0";

            //docType 0-全盘  1-抽盘  2-模板
            if (docType.equals("2")){
                boolean isExist=false;
                for (Map<String, Object> oneStocktake : getStocktakeQData){
                    String getPluNo=oneStocktake.get("PLUNO").toString();
                    String getFeatureNo = oneStocktake.get("FEATURENO").toString();
                    if (getPluNo.equals(pluNo) && getFeatureNo.equals(featureNo)){
                        getPqty = oneStocktake.get("PQTY").toString();
                        getFqty = oneStocktake.get("FQTY").toString();
                        if (Check.Null(getFqty))
                            getFqty="0";
                        getAmt = oneStocktake.get("AMT").toString();
                        getDistriAmt = oneStocktake.get("DISTRIAMT").toString();
                        getPunit = oneStocktake.get("PUNIT").toString();
                        getUnitRatio = oneStocktake.get("UNIT_RATIO").toString();
                        getBaseQty = oneStocktake.get("BASEQTY").toString();
                        isExist=true;
                        break;
                    }
                }
                if (isExist) {
                    //库存单位是否调整处理，重新计算初盘录入数
                    String fqty = getFqty;
                    if (!getPunit.equals(punit) && new BigDecimal(getFqty).compareTo(BigDecimal.ZERO)!=0 ){
                        BigDecimal getFqty_b = new BigDecimal(getFqty);
                        BigDecimal getBaseQty_b = new BigDecimal(getBaseQty);
                        //【ID1016322】【货郎3.0】多PDA盘点-优化项
                        //复盘导入，最新商品的默认盘点单位PUNIT与已导入的PUNIT不一致，则FQTY需要根据最新PUNIT与BASEUNIT的换算率重新根据（FBASEQTY/单位换算率） 计算
                        getFqty_b = getBaseQty_b.divide(unitRatio_b,Integer.parseInt(udlength), BigDecimal.ROUND_HALF_UP);
                        fqty = getFqty_b.toPlainString();
                    }
                    totPqty_b = totPqty_b.subtract(new BigDecimal(getPqty)).add(new BigDecimal(pqty));
                    totAmt_b = totAmt_b.subtract(new BigDecimal(getAmt)).add(new BigDecimal(amt));
                    totDistriAmt_b = totAmt_b.subtract(new BigDecimal(getDistriAmt)).add(new BigDecimal(distriAmt));

                    UptBean ub = new UptBean("DCP_STOCKTAKE_DETAIL");
                    //add Value
                    ub.addUpdateValue("PQTY", new DataValue(pqty, Types.VARCHAR));
                    ub.addUpdateValue("PUNIT", new DataValue(punit, Types.VARCHAR));
                    ub.addUpdateValue("BASEQTY", new DataValue(baseQty, Types.VARCHAR));
                    ub.addUpdateValue("BASEUNIT", new DataValue(baseUnit, Types.VARCHAR));
                    ub.addUpdateValue("UNIT_RATIO", new DataValue(unitRatio, Types.VARCHAR));
                    ub.addUpdateValue("PRICE", new DataValue(price, Types.VARCHAR));
                    ub.addUpdateValue("DISTRIPRICE", new DataValue(distriPrice, Types.VARCHAR));
                    ub.addUpdateValue("AMT", new DataValue(amt, Types.VARCHAR));
                    ub.addUpdateValue("DISTRIAMT", new DataValue(distriAmt, Types.VARCHAR));
                    ub.addUpdateValue("REF_BASEQTY", new DataValue(refBaseQty, Types.VARCHAR));
                    ub.addUpdateValue("FQTY",new DataValue(fqty, Types.VARCHAR));
                    //ub.addUpdateValue("FBASEQTY",new DataValue(, Types.VARCHAR));
                    ub.addUpdateValue("RQTY", new DataValue(pqty, Types.VARCHAR));
                    ub.addUpdateValue("RBASEQTY", new DataValue(baseQty, Types.VARCHAR));
                    //condition
                    ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub.addCondition("STOCKTAKENO", new DataValue(stockTakeNo, Types.VARCHAR));
                    ub.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
                    ub.addCondition("FEATURENO", new DataValue(featureNo, Types.VARCHAR));

                    this.addProcessData(new DataProcessBean(ub));
                }
            }else {
                boolean isExist=false;
                for (Map<String, Object> oneStocktake : getStocktakeQData){
                    String getPluNo=oneStocktake.get("PLUNO").toString();
                    String getFeatureNo = oneStocktake.get("FEATURENO").toString();
                    if (getPluNo.equals(pluNo) && getFeatureNo.equals(featureNo)){
                        getPqty = oneStocktake.get("PQTY").toString();
                        getFqty = oneStocktake.get("FQTY").toString();
                        if (Check.Null(getFqty))
                            getFqty="0";
                        getAmt = oneStocktake.get("AMT").toString();
                        getDistriAmt = oneStocktake.get("DISTRIAMT").toString();
                        getPunit = oneStocktake.get("PUNIT").toString();
                        getUnitRatio = oneStocktake.get("UNIT_RATIO").toString();
                        getBaseQty = oneStocktake.get("BASEQTY").toString();
                        isExist=true;
                        break;
                    }
                }

                //库存单位是否调整处理，重新计算初盘录入数
                String fqty = getFqty;
                if (!getPunit.equals(punit) && new BigDecimal(getFqty).compareTo(BigDecimal.ZERO)!=0 ){
                    BigDecimal getFqty_b = new BigDecimal(getFqty);
                    BigDecimal getBaseQty_b = new BigDecimal(getBaseQty);
                    //【ID1016322】【货郎3.0】多PDA盘点-优化项
                    //复盘导入，最新商品的默认盘点单位PUNIT与已导入的PUNIT不一致，则FQTY需要根据最新PUNIT与BASEUNIT的换算率重新根据（FBASEQTY/单位换算率） 计算
                    getFqty_b = getBaseQty_b.divide(unitRatio_b,Integer.parseInt(udlength), BigDecimal.ROUND_HALF_UP);
                    fqty = getFqty_b.toPlainString();
                }

                if (isExist){
                    totPqty_b = totPqty_b.subtract(new BigDecimal(getPqty)).add(new BigDecimal(pqty));
                    totAmt_b = totAmt_b.subtract(new BigDecimal(getAmt)).add(new BigDecimal(amt));
                    totDistriAmt_b = totDistriAmt_b.subtract(new BigDecimal(getDistriAmt)).add(new BigDecimal(distriAmt));

                    //修改库存盘点明细
                    UptBean ub = new UptBean("DCP_STOCKTAKE_DETAIL");
                    //add Value
                    ub.addUpdateValue("PQTY",new DataValue(pqty, Types.VARCHAR));
                    ub.addUpdateValue("PUNIT",new DataValue(punit, Types.VARCHAR));
                    ub.addUpdateValue("BASEQTY",new DataValue(baseQty, Types.VARCHAR));
                    ub.addUpdateValue("BASEUNIT",new DataValue(baseUnit, Types.VARCHAR));
                    ub.addUpdateValue("UNIT_RATIO",new DataValue(unitRatio, Types.VARCHAR));
                    ub.addUpdateValue("PRICE",new DataValue(price, Types.VARCHAR));
                    ub.addUpdateValue("DISTRIPRICE",new DataValue(distriPrice, Types.VARCHAR));
                    ub.addUpdateValue("AMT",new DataValue(amt, Types.VARCHAR));
                    ub.addUpdateValue("DISTRIAMT",new DataValue(distriAmt, Types.VARCHAR));
                    ub.addUpdateValue("REF_BASEQTY",new DataValue(refBaseQty, Types.VARCHAR));
                    ub.addUpdateValue("FQTY",new DataValue(fqty, Types.VARCHAR));
                    //ub.addUpdateValue("FBASEQTY",new DataValue(, Types.VARCHAR));
                    ub.addUpdateValue("RQTY",new DataValue(pqty, Types.VARCHAR));
                    ub.addUpdateValue("RBASEQTY",new DataValue(baseQty, Types.VARCHAR));
                    //condition
                    ub.addCondition("EID",new DataValue(eId, Types.VARCHAR));
                    ub.addCondition("SHOPID",new DataValue(shopId, Types.VARCHAR));
                    ub.addCondition("STOCKTAKENO",new DataValue(stockTakeNo, Types.VARCHAR));
                    ub.addCondition("PLUNO",new DataValue(pluNo, Types.VARCHAR));
                    ub.addCondition("FEATURENO",new DataValue(featureNo, Types.VARCHAR));

                    this.addProcessData(new DataProcessBean(ub));

                }else {
                    totPqty_b = totPqty_b.add(new BigDecimal(pqty));
                    totAmt_b = totAmt_b.add(new BigDecimal(amt));
                    totDistriAmt_b = totDistriAmt_b.add(new BigDecimal(distriAmt));
                    //添加库存盘点明细 DCP_STOCKTAKE_DETAIL
                    i++;
                    String[] columns = {
                            "EID","SHOPID","ORGANIZATIONNO","STOCKTAKENO","WAREHOUSE","ITEM","PLUNO","FEATURENO",
                            "BATCH_NO","PROD_DATE","PQTY","PUNIT","BASEQTY","BASEUNIT","UNIT_RATIO","REF_BASEQTY",
                            "PRICE","DISTRIPRICE","AMT","DISTRIAMT",
                            "MEMO","BDATE","OITEM",
                            "FQTY","FBASEQTY","RQTY","RBASEQTY"
                    };
                    DataValue[] insValue = new DataValue[]{
                            new DataValue(eId, Types.VARCHAR),
                            new DataValue(shopId, Types.VARCHAR),
                            new DataValue(shopId, Types.VARCHAR),
                            new DataValue(stockTakeNo, Types.VARCHAR),
                            new DataValue(warehouse, Types.VARCHAR),
                            new DataValue(String.valueOf(i), Types.VARCHAR),
                            new DataValue(pluNo, Types.VARCHAR),
                            new DataValue(featureNo, Types.VARCHAR),
                            new DataValue("", Types.VARCHAR),  //BATCH_NO
                            new DataValue("", Types.VARCHAR),  //PROD_DATE
                            new DataValue(pqty, Types.VARCHAR),
                            new DataValue(punit, Types.VARCHAR),
                            new DataValue(baseQty, Types.VARCHAR),
                            new DataValue(baseUnit, Types.VARCHAR),
                            new DataValue(unitRatio, Types.VARCHAR),
                            new DataValue(refBaseQty, Types.VARCHAR),
                            new DataValue(price, Types.VARCHAR),
                            new DataValue(distriPrice, Types.VARCHAR),
                            new DataValue(amt, Types.VARCHAR),
                            new DataValue(distriAmt, Types.VARCHAR),
                            new DataValue("", Types.VARCHAR),  //MEMO
                            new DataValue(bdate, Types.VARCHAR),
                            new DataValue("0", Types.VARCHAR), //OITEM
                            new DataValue("0", Types.VARCHAR), //FQTY
                            new DataValue("0", Types.VARCHAR), //FBASEQTY
                            new DataValue(pqty, Types.VARCHAR),      //RQTY
                            new DataValue(baseQty, Types.VARCHAR),   //RBASEQTY
                    };
                    InsBean ib = new InsBean("DCP_STOCKTAKE_DETAIL", columns);
                    ib.addValues(insValue);
                    this.addProcessData(new DataProcessBean(ib));
                }
            }
        }

        //修改盘点单单头资料
        UptBean ub = new UptBean("DCP_STOCKTAKE");
        ub.addUpdateValue("TOT_CQTY", new DataValue(String.valueOf(i), Types.VARCHAR));
        ub.addUpdateValue("TOT_PQTY", new DataValue(totPqty, Types.VARCHAR));
        ub.addUpdateValue("TOT_AMT", new DataValue(totAmt, Types.VARCHAR));
        ub.addUpdateValue("TOT_DISTRIAMT", new DataValue(totDistriAmt, Types.VARCHAR));
        ub.addUpdateValue("MODIFYBY", new DataValue(modifyBy, Types.VARCHAR));
        ub.addUpdateValue("MODIFY_DATE", new DataValue(modifyDate, Types.VARCHAR));
        ub.addUpdateValue("MODIFY_TIME", new DataValue(modifyTime, Types.VARCHAR));
        ub.addUpdateValue("SUBSTOCKIMPORT", new DataValue("2", Types.VARCHAR)); // 0初复均未导入；1初盘导入；2初复盘均导入

        // condition
        ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        ub.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
        ub.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
        ub.addCondition("STOCKTAKENO", new DataValue(stockTakeNo, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(ub));

    }

}
