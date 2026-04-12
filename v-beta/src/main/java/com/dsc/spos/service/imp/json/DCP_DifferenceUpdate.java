package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_DifferenceUpdateReq;
import com.dsc.spos.json.cust.req.DCP_DifferenceUpdateReq.level1Elm;
import com.dsc.spos.json.cust.req.DCP_DifferenceUpdateReq.levelElm;
import com.dsc.spos.json.cust.res.DCP_DifferenceUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_DifferenceUpdate extends SPosAdvanceService<DCP_DifferenceUpdateReq, DCP_DifferenceUpdateRes> {
    @Override
    protected void processDUID(DCP_DifferenceUpdateReq req, DCP_DifferenceUpdateRes res) throws Exception {
        levelElm request = req.getRequest();
        String DifferenceNo = request.getDifferenceNo();
        String shopId = req.getShopId();
        String bDate = request.getBDate();
        String warehouse = request.getWarehouse();
        Calendar cal = Calendar.getInstance();//获得当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String MODIFY_DATE = df.format(cal.getTime());
        df = new SimpleDateFormat("HHmmss");
        String MODIFY_TIME = df.format(cal.getTime());

        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String ofNo = req.getRequest().getOfNo();
        List<level1Elm> datas = request.getDatas();
        String diffAppealTimes = PosPub.getPARA_SMS(dao, req.geteId(), "", "Diff_Appeal_Times");

        //查询difference
        String differSql = "select * from DCP_DIFFERENCE a where a.eid='" + eId + "' " +
                " and a.differenceno='" + DifferenceNo + "'";
        List<Map<String, Object>> differList = this.doQueryData(differSql, null);
        if (differList.size() <= 0) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "差异单不存在！");
        } else {
            String status = differList.get(0).get("STATUS").toString();
            String differShopId = differList.get(0).get("SHOPID").toString();
            String differOrganizationno = differList.get(0).get("ORGANIZATIONNO").toString();
            String transfer_shop = differList.get(0).get("TRANSFER_SHOP").toString();
            organizationNO=differOrganizationno;

            if(!status.equals("0")) {
                if (!status.equals("1")  || !transfer_shop.equals(req.getOrganizationNO())) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "不可编辑！");
                }
            }

        }

        if(req.getOrganizationNO().equals(request.getTransferShop())&&"1".equals(request.getStatus())){
            for (level1Elm par : request.getDatas()) {
                if (Check.Null(par.getPqty())) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "item:" + par.getItem() + "pqty需传入！");
                }
            }
        }
        for (level1Elm par : request.getDatas()){
            if(Check.Null(par.getPqty())){
                par.setPqty(par.getReqQty());
            }
        }

        String stockInDetailSql = "select a.CONFIRM_DATE,b.* from dcp_stockin a" +
                " left join dcp_stockin_detail b on a.eid=b.eid and a.organizationno=b.organizationno and a.stockinno=b.stockinno" +
                " where a.eid='" + eId + "' " +
                "  and a.stockinno='" + ofNo + "'";//and a.organizationno='" + organizationNO + "'
        List<Map<String, Object>> stockInDetailList = this.doQueryData(stockInDetailSql, null);
        if (stockInDetailList.size() <= 0) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "入库单不存在！");
        } else {
            String confirmDate = stockInDetailList.get(0).get("CONFIRM_DATE").toString();
            LocalDate confirmDateLocal = LocalDate.parse(confirmDate, DateTimeFormatter.BASIC_ISO_DATE);
            LocalDate localDate = confirmDateLocal.plusDays(Integer.parseInt(diffAppealTimes));
            if (LocalDate.now().isAfter(localDate)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "入库单确认日期时间+差异申诉截止时效已过期，无法创建单据！");
            }
        }
        if (datas.size() <= 0) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "差异调整明细不可为空！");
        } else {
            for (DCP_DifferenceUpdateReq.level1Elm par : datas) {
                String oitem = par.getOItem();
                List<Map<String, Object>> filterList = stockInDetailList.stream().filter(x -> x.get("ITEM").toString().equals(oitem)).collect(Collectors.toList());
                if (CollUtil.isEmpty(filterList)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "oitem:" + oitem + "调整商品不存在来源入库单！");
                } else {
                    String batch_no = filterList.get(0).get("BATCH_NO").toString();
                    BigDecimal pqty = new BigDecimal(filterList.get(0).get("PQTY").toString());
                    if (!Check.Null(batch_no) && Check.Null(par.getBatchNo())) {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "item:" + par.getItem() + "批号不可为空！");
                    }
                    //BigDecimal reqQty = new BigDecimal(Math.abs(Double.parseDouble(par.getPQty())));
                    //if (pqty.compareTo(reqQty) < 0) {
                       // throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "item:" + par.getItem() + "差异量不能大于" + pqty + "！");
                    //}
                }
            }
        }

        String differenceNo = request.getDifferenceNo();

        ColumnDataValue condition = new ColumnDataValue();

        condition.add("EID", DataValues.newString(req.geteId()));
        //condition.add("SHOPID", DataValues.newString(req.getShopId()));
        condition.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
        condition.add("DIFFERENCENO", DataValues.newString(differenceNo));


        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_DIFFERENCE_DETAIL", condition)));

        int totCQty = 0;

        for (level1Elm par : request.getDatas()) {

            totCQty++;

            ColumnDataValue dcp_difference_detail = new ColumnDataValue();

            dcp_difference_detail.add("EID", DataValues.newString(req.geteId()));
            dcp_difference_detail.add("SHOPID", DataValues.newString(organizationNO));
            dcp_difference_detail.add("DIFFERENCENO", DataValues.newString(differenceNo));
            dcp_difference_detail.add("ITEM", DataValues.newString(par.getItem()));
            dcp_difference_detail.add("OITEM", DataValues.newString(par.getOItem()));
            dcp_difference_detail.add("PLUNO", DataValues.newString(par.getPluNo()));
            dcp_difference_detail.add("PUNIT", DataValues.newString(par.getPunit()));
            dcp_difference_detail.add("PQTY", DataValues.newString(par.getPqty()));
            dcp_difference_detail.add("BASEUNIT", DataValues.newString(par.getBaseUnit()));
            dcp_difference_detail.add("BASEQTY", DataValues.newString(par.getBaseQty()));
            dcp_difference_detail.add("UNIT_RATIO", DataValues.newString(par.getUnitRatio()));
            dcp_difference_detail.add("BSNO", DataValues.newString(par.getBsNo()));
            dcp_difference_detail.add("PRICE", DataValues.newString(par.getPrice()));
            dcp_difference_detail.add("AMT", DataValues.newString(par.getAmt()));
            dcp_difference_detail.add("REQ_QTY", DataValues.newString(par.getReqQty()));
            dcp_difference_detail.add("DIFF_QTY", DataValues.newString(par.getRealQty()));

            dcp_difference_detail.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
            dcp_difference_detail.add("WAREHOUSE", DataValues.newString(par.getWarehouse()));
            dcp_difference_detail.add("BATCH_NO", DataValues.newString(par.getBatchNo()));
            dcp_difference_detail.add("PROD_DATE", DataValues.newString(par.getProdDate()));
            dcp_difference_detail.add("DISTRIPRICE", DataValues.newString(par.getDistriPrice()));
            dcp_difference_detail.add("DISTRIAMT", DataValues.newString(par.getDistriAmt()));
            dcp_difference_detail.add("LOAD_ITEM", DataValues.newString(par.getLoad_item()));
            dcp_difference_detail.add("FEATURENO", DataValues.newString(par.getFeatureNo()));
            dcp_difference_detail.add("TRANSFER_BATCHNO", DataValues.newString(par.getTransferBatchNo()));
//                dcp_difference_detail.add("OFNO", DataValues.newString(par.getOfNo()));
//                dcp_difference_detail.add("OITEM", DataValues.newString(par.getOItem()));
//                dcp_difference_detail.add("OOTYPE", DataValues.newString(par.getOoType()));
//                dcp_difference_detail.add("OOFNO", DataValues.newString(par.getOofNo()));
//                dcp_difference_detail.add("OOITEM", DataValues.newString(par.getOoItem()));

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_DIFFERENCE_DETAIL", dcp_difference_detail)));
        }

        //单身为0，不要插入单头记录
        if (!request.getDatas().isEmpty()) {

            ColumnDataValue dcp_difference = new ColumnDataValue();

            dcp_difference.add("TOT_PQTY", DataValues.newString(request.getTotPqty()));
            dcp_difference.add("TOT_AMT", DataValues.newString(request.getTotAmt()));
            dcp_difference.add("TOT_CQTY", DataValues.newString(totCQty));
            dcp_difference.add("STATUS", DataValues.newString(request.getStatus()));
            dcp_difference.add("BDATE", DataValues.newString(request.getBDate()));
            dcp_difference.add("DIFFERENCE_ID", DataValues.newString(request.getDifferenceID()));
            dcp_difference.add("OTYPE", DataValues.newString(request.getOType()));
            dcp_difference.add("OFNO", DataValues.newString(request.getOfNo()));

            dcp_difference.add("LOAD_DOCNO", DataValues.newString(request.getLoadDocNo()));
            dcp_difference.add("LOAD_DOCTYPE", DataValues.newString(request.getLoadDocType()));
            dcp_difference.add("DOC_TYPE", DataValues.newString(request.getDocType()));
            dcp_difference.add("WAREHOUSE", DataValues.newString(request.getWarehouse()));
            dcp_difference.add("TRANSFER_WAREHOUSE", DataValues.newString(request.getTransferWarehouse()));
            dcp_difference.add("CREATETYPE", DataValues.newString(request.getCreateType()));
            dcp_difference.add("INVWAREHOUSE", DataValues.newString(request.getInvWarehouse()));
            dcp_difference.add("TRANSFER_SHOP", DataValues.newString(request.getTransferShop()));
            dcp_difference.add("TOT_DISTRIAMT", DataValues.newString(request.getTotDistriAmt()));
            dcp_difference.add("UPDATE_TIME", DataValues.newString(DateFormatUtils.getTimestamp()));
            dcp_difference.add("TRAN_TIME", DataValues.newString(DateFormatUtils.getTimestamp()));


            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_DIFFERENCE", condition, dcp_difference))); // 新增單頭
        }


        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
        //}
        //catch (Exception e)
        //{
        //	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        //}
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_DifferenceUpdateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_DifferenceUpdateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_DifferenceUpdateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_DifferenceUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        levelElm request = req.getRequest();

        List<level1Elm> datas = request.getDatas();

        if (Check.Null(request.getBDate())) {
            errMsg.append("营业日期不可为空值, ");
            isFail = true;
        }

        if (Check.Null(request.getDifferenceNo())) {
            errMsg.append("差异申诉单号不可为空值, ");
            isFail = true;
        }

        if (Check.Null(request.getWarehouse())) {
            errMsg.append("仓库不可为空值, ");
            isFail = true;
        }
        if (Check.Null(request.getTotPqty())) {
            errMsg.append("合计录入数量不可为空值, ");
            isFail = true;
        }

        if (Check.Null(request.getTotAmt())) {
            errMsg.append("合计录入数量不可为空值, ");
            isFail = true;
        }

        if (Check.Null(request.getTotDistriAmt())) {
            errMsg.append("合计进货金额可为空值, ");
            isFail = true;
        }

        if (Check.Null(request.getTotCqty())) {
            errMsg.append("合计品种数量不可为空值, ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        for (level1Elm par : datas) {
            if (Check.Null(par.getItem())) {
                errMsg.append("项次不可为空值, ");
                isFail = true;
            }

            if (Check.Null(par.getPluNo())) {
                errMsg.append("商品编码不可为空值, ");
                isFail = true;
            }

            if (Check.Null(par.getPunit())) {
                errMsg.append("单位不可为空值, ");
                isFail = true;
            }

            if (Check.Null(par.getReqQty())) {
                errMsg.append("差异申请数量不可为空值, ");
                isFail = true;
            }

            if (Check.Null(par.getWarehouse())) {
                errMsg.append("仓库不可为空值, ");
                isFail = true;
            }

            if (isFail) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
            }
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_DifferenceUpdateReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_DifferenceUpdateReq>() {
        };
    }

    @Override
    protected DCP_DifferenceUpdateRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_DifferenceUpdateRes();
    }

}
