package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_DifferenceCreateReq;
import com.dsc.spos.json.cust.req.DCP_DifferenceCreateReq.level1Elm;
import com.dsc.spos.json.cust.req.DCP_DifferenceCreateReq.levelElm;
import com.dsc.spos.json.cust.res.DCP_DifferenceCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_DifferenceCreate extends SPosAdvanceService<DCP_DifferenceCreateReq, DCP_DifferenceCreateRes> {

    @Override
    protected boolean isVerifyFail(DCP_DifferenceCreateReq req) throws Exception {

        return false;
    }

    @Override
    protected void processDUID(DCP_DifferenceCreateReq req, DCP_DifferenceCreateRes res) throws Exception {
        levelElm request = req.getRequest();
        String differenceNo = "";
        String bdate = request.getBDate();
        String eId = req.geteId();
        String shopId = req.getShopId();
        String difference_id = request.getDifferenceID();
        String ofNo = request.getOfNo();
        String organizationNO = req.getOrganizationNO();
        //单身明细信息
        List<level1Elm> jsonDatas = request.getDatas();

        String diffAppealTimes = PosPub.getPARA_SMS(dao, req.geteId(), "", "Diff_Appeal_Times");

        //1、检查【来源入库单号OFNO】是否已存在有效的差异申诉单，存在则返回提示该入库单已有差异申诉单；--有
        //3、差异申诉明细不可为空！--有
        //4、检查时效：判断系统时间已超出【入库单确认日期时间+差异申诉截止时效】，则无法创建单据！--加
        //5、检查差异调整明细是否存在来源入库单，不存在则提示调整商品不存在来源入库单！  --加
        //6、检查差异调整明细在来源入库单是否存在收货批号，若存在该行【批号】栏位不可为空！ --加
        //7、检查录入数据是否合法：0<申请量<=ABS【差异量】, 且方向不可发生变化，即默认-2，不可以调到+2） --加
        String stockInDetailSql = "select a.CONFIRM_DATE,b.* from dcp_stockin a" +
                " left join dcp_stockin_detail b on a.eid=b.eid and a.organizationno=b.organizationno and a.stockinno=b.stockinno" +
                " where a.eid='" + eId + "' " +
                " and a.organizationno='" + organizationNO + "' and a.stockinno='" + ofNo + "'";
        List<Map<String, Object>> stockInDetailList = this.doQueryData(stockInDetailSql, null);
        if (stockInDetailList.size() <= 0) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "入库单不存在！");
        } else {
            String confirmDate = stockInDetailList.get(0).get("CONFIRM_DATE").toString();
            LocalDateTime confirmDateLocal = LocalDateTime.parse(confirmDate+diffAppealTimes,  DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));//new DateTimeFormatter("yyyyMMddHHmmss")
            if(LocalDateTime.now().isAfter(confirmDateLocal)){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "入库单确认日期时间+差异申诉截止时效已过期，无法创建单据！");
            }
        }
        if (jsonDatas.size() <= 0) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "差异调整明细不可为空！");
        } else {
            for (level1Elm par : jsonDatas) {
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
                    //BigDecimal reqQty = new BigDecimal(Math.abs(Double.parseDouble(par.getReqQty())));
                    //if (pqty.compareTo(reqQty) < 0) {
                        //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "item:" + par.getItem() + "差异量不能大于" + pqty + "！");
                    //}
                }
            }
        }
        //try
        //{
        String sql = ""
                + " select difference_id from dcp_difference "
                + " where (eid='" + eId + "' and shopid='" + shopId + "' and ofno='" + ofNo + "' )"
                + " or difference_id='" + difference_id + "' ";

        List<Map<String, Object>> getQData_checkGuid = this.doQueryData(sql, null);
        //GUID存在否判断
        if (getQData_checkGuid == null || getQData_checkGuid.isEmpty()) {
//            DifferenceNo = this.getDifferenceNo(req);
            differenceNo = getOrderNO(req, "CYSS");
            res.setDifferenceNo(differenceNo);
            String createDate = DateFormatUtils.getNowPlainDate();
            String createTime = DateFormatUtils.getNowPlainTime();
            String docType = request.getDocType().substring(0, 1);
            String oType = request.getOType().substring(0, 1);

            int totCQty = 0;

            for (level1Elm par : jsonDatas) {

                totCQty++;
                ColumnDataValue dcp_difference_detail = new ColumnDataValue();

                dcp_difference_detail.add("EID", DataValues.newString(req.geteId()));
                dcp_difference_detail.add("SHOPID", DataValues.newString(req.getShopId()));
                dcp_difference_detail.add("DIFFERENCENO", DataValues.newString(differenceNo));
                dcp_difference_detail.add("ITEM", DataValues.newString(par.getItem()));
                dcp_difference_detail.add("OITEM", DataValues.newString(par.getOItem()));
                dcp_difference_detail.add("PLUNO", DataValues.newString(par.getPluNo()));
                dcp_difference_detail.add("PUNIT", DataValues.newString(par.getPunit()));
                dcp_difference_detail.add("PQTY", DataValues.newString(par.getReqQty()));
                dcp_difference_detail.add("BASEUNIT", DataValues.newString(par.getBaseUnit()));
                dcp_difference_detail.add("BASEQTY", DataValues.newString(par.getBaseQty()));
                dcp_difference_detail.add("UNIT_RATIO", DataValues.newString(par.getUnitRatio()));
                dcp_difference_detail.add("BSNO", DataValues.newString(par.getBsNo()));
                dcp_difference_detail.add("PRICE", DataValues.newString(par.getPrice()));
                dcp_difference_detail.add("AMT", DataValues.newString(par.getAmt()));
                dcp_difference_detail.add("REQ_QTY", DataValues.newString(par.getReqQty()));
                dcp_difference_detail.add("DIFF_QTY", DataValues.newString(par.getRealQty()));

                dcp_difference_detail.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
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
            if (!jsonDatas.isEmpty()) {

                ColumnDataValue dcp_difference = new ColumnDataValue();

                dcp_difference.add("EID", DataValues.newString(req.geteId()));
                dcp_difference.add("SHOPID", DataValues.newString(req.getShopId()));
                dcp_difference.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
                dcp_difference.add("DIFFERENCENO", DataValues.newString(differenceNo));
                dcp_difference.add("CREATEBY", DataValues.newString(req.getEnableMultiLang()));
                dcp_difference.add("CREATE_DATE", DataValues.newString(createDate));
                dcp_difference.add("CREATE_TIME", DataValues.newString(createTime));
                dcp_difference.add("MODIFYBY", DataValues.newString(req.getEnableMultiLang()));
                dcp_difference.add("MODIFY_DATE", DataValues.newString(createDate));
                dcp_difference.add("MODIFY_TIME", DataValues.newString(createTime));
                dcp_difference.add("SUBMITBY", DataValues.newString(req.getEnableMultiLang()));
                dcp_difference.add("SUBMIT_DATE", DataValues.newString(createDate));
                dcp_difference.add("SUBMIT_TIME", DataValues.newString(createTime));
                dcp_difference.add("TOT_PQTY", DataValues.newString(request.getTotPqty()));
                dcp_difference.add("TOT_AMT", DataValues.newString(request.getTotAmt()));
                dcp_difference.add("TOT_CQTY", DataValues.newString(totCQty));
                dcp_difference.add("STATUS", DataValues.newString("0"));
                dcp_difference.add("BDATE", DataValues.newString(bdate));
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


                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_DIFFERENCE", dcp_difference))); // 新增單頭
            }

            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");


        } else {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据重复提交！");
        }
        //}
        //catch(Exception e )
        //{
        //	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        //}

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_DifferenceCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_DifferenceCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_DifferenceCreateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected TypeToken<DCP_DifferenceCreateReq> getRequestType() {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_DifferenceCreateReq>() {
        };
    }

    @Override
    protected DCP_DifferenceCreateRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_DifferenceCreateRes();
    }

    /**
     * 差异申请单号生成规则：
     * 固定编码CYSS+年月日+5位流水号
     *
     * @param req
     * @return
     * @throws Exception
     */
    protected String getDifferenceNo(DCP_DifferenceCreateReq req) throws Exception {
        String DifferenceNo = "";
        String sql = "";
        String shopId = req.getShopId();
        String organizationNO = req.getShopId();
        ;
        String eId = req.geteId();
        String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
        StringBuffer sqlbuf = new StringBuffer("");

        String[] conditionValues = {shopId, eId, organizationNO}; // 查询条件
        String ajustnoHead = "CYSS" + bDate;
        sqlbuf.append("select DIFFERENCENO from (select MAX(DIFFERENCENO) DIFFERENCENO from DCP_DIFFERENCE "
                + " where SHOPID=? "
                + " and EID=? "
                + " and ORGANIZATIONNO=? "
                + " and DIFFERENCENO like '" + ajustnoHead + "%%')");
        sql = sqlbuf.toString();

        List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
        if (getQData != null && getQData.isEmpty() == false) {
            DifferenceNo = (String) getQData.get(0).get("DIFFERENCENO");

            if (DifferenceNo != null && DifferenceNo.length() > 0) {
                long i;
                DifferenceNo = DifferenceNo.substring(4, DifferenceNo.length());
                i = Long.parseLong(DifferenceNo) + 1;
                DifferenceNo = i + "";
                DifferenceNo = "CYSS" + DifferenceNo;
            } else {
                //当天无单，从00001开始
                DifferenceNo = "CYSS" + bDate + "00001";
            }
        } else {
            //当天无单，从00001开始
            DifferenceNo = "CYSS" + bDate + "00001";
        }

        return DifferenceNo;
    }


}
