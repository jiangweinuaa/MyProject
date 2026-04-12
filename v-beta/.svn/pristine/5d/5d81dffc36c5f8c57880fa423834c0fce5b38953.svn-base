package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_WoProdRepStatCreateReq;
import com.dsc.spos.json.cust.res.DCP_WoProdRepStatCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.dsc.spos.utils.price.GoodsPrice;
import com.dsc.spos.utils.price.ProdGoods;
import com.dsc.spos.utils.price.WoProdGetPriceUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_WoProdRepStatCreate extends SPosAdvanceService<DCP_WoProdRepStatCreateReq, DCP_WoProdRepStatCreateRes> {
    @Override
    protected void processDUID(DCP_WoProdRepStatCreateReq req, DCP_WoProdRepStatCreateRes res) throws Exception {

//        汇总数据来源：1：依工单
//                生成工单
//        1. 调用：MES_BATCHTASK表
//        a. 【法人组织】下所有organizatinno组织编号对应配料任务单；
//        b. MES_BATCHTASK表中单据状态=1确定，[完工状态】PRODUCTSTATUS=Y或Z
//        2. 调用MES_PROCESS_REPORT
//        a. 用【报工单号】与【来源任务单号】做关联
//        b. 入库数量=汇总【合格数量】，数字：千分位，6 位小数
//        c. 【 入库金额】= 【入库数量】*上期成本单价；上期成本单价无，取期初开帐，期初开帐无取商品交易售价
//        3. 调用MES_PROCESS_REPORT_DETAIL
//        a. 排除作废、未审核的报工单
//        b. 汇总【实际工时】/【实际机时】数字格式，千分位，保留 3 位小数；
//        c. 单位为分，如果报工为秒需做转换；
//        4. 调用MES_BOM_SUBPROCESS
//        a. 【标准工时】/【标准人工】需要乘以入库数量；
//        b. 单位为：分钟
//        5. 整批产生时，拆件工单的标准工时、标准机时，不适合用料件基本资料的标准工机时*数量，故直接给0
//        6. 拆件工单若已经结案将期末在制数量预设为 0。
//        7. 【工单批次产生】判断日期是否是成本现行期别
//        8. 生成数据时判断【工单编号】是否已经存在，已存在排除不生成；
//        门店工单
//        调用：DCP_PROCESSTASK_DETAIL
//        1. PROCESSTASKNO加工任务单的商品状态为：2 已完成，3 已取餐；
//        2. 【入库数量】：PSTOCKIN_QTY 完工入库数
//        3. 【 入库金额】= 【入库数量】*上期成本单价；上期成本单价无，取期初开帐，期初开帐无取商品交易售价
//        4. 标准工时/标准人工：调MES_BOM_SUBPROCESS
//        a. 【标准工时】/【标准人工】需要乘以入库数量；
//        b. 单位为：分钟

        String accountSql = " SELECT ACCOUNTID,COST_DOMAIN FROM DCP_ACOUNT_SETTING a " +
                "  LEFT JOIN DCP_ORG b on a.EID=b.EID and a.CORP=b.ORGANIZATIONNO " +
                "  WHERE a.EID='" + req.geteId() + "'" +
                "  AND a.STATUS='100' AND a.ACCTTYPE='1' AND a.CORP='" + req.getRequest().getCorp() + "'";
        List<Map<String, Object>> accountInfo = doQueryData(accountSql, null);
        if (CollectionUtils.isEmpty(accountInfo)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "当前法人" + req.getRequest().getCorp() + "未设置主帐套，请检查帐套设置!");
        }

        List<Map<String, Object>> qData = doQueryData(getQuerySql2(req), null);

        if (CollectionUtils.isEmpty(qData)) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "当前没有可统计工单！");
        }
        String wDate = DateFormatUtils.getPlainDate(req.getRequest().getWDate());

        String querySql = " SELECT * FROM dcp_woprodrepstat a WHERE a.EID='" + req.geteId() + "' AND to_char(a.WDATE,'YYYYMMDD') like '%%" + wDate.substring(0, 6) + "%%'";
        List<Map<String, Object>> existed = doQueryData(querySql, null);
        String pOrderNo = req.getRequest().getPorderNo();
        if (CollectionUtils.isNotEmpty(existed)) {
            String oldStatus = existed.get(0).get("STATUS").toString();
            pOrderNo = existed.get(0).get("PORDERNO").toString();
            if (!"-1".equals(oldStatus)) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "当前生产报工统计已生成，请删除后再试！");
            }
        }

        ColumnDataValue delCondition = new ColumnDataValue();
        delCondition.add("EID", DataValues.newString(req.geteId()));
        delCondition.add("CORP", DataValues.newString(req.getRequest().getCorp()));
        delCondition.add("PORDERNO", DataValues.newString(pOrderNo));
        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("dcp_woprodrepstat", delCondition)));

        if (StringUtils.isEmpty(pOrderNo)
                || "CBGD".equals(req.getRequest().getPorderNo())
        ) {
            pOrderNo = getNormalNOWithDate(req, "CBGD", req.getRequest().getWDate());
        } else {
            pOrderNo = req.getRequest().getPorderNo();
        }

        String accountId = accountInfo.get(0).get("ACCOUNTID").toString();
        String costDomain = accountInfo.get(0).get("COST_DOMAIN").toString();


        List<ProdGoods> prodGoodsList = new ArrayList<>();
        for (Map<String, Object> oneData : qData) {
            ProdGoods prodGoods;
            if ("Y".equals(costDomain)) {
                prodGoods = new ProdGoods(
                        req.getRequest().getCorp(),
                        oneData.get("PLUNO").toString(),
                        Double.parseDouble(StringUtils.toString(oneData.get("BASEQTY"), "0"))
                );
            } else {
                prodGoods = new ProdGoods(
                        oneData.get("SHOPID").toString(),
                        oneData.get("PLUNO").toString(),
                        Double.parseDouble(StringUtils.toString(oneData.get("BASEQTY"), "0"))
                );
            }
            prodGoodsList.add(prodGoods);
        }

        List<GoodsPrice> goodsPriceList = WoProdGetPriceUtils.getGoodsPrice(
                accountId,
                prodGoodsList,
                DateFormatUtils.getPlainDate(req.getRequest().getWDate()
                ));

        int item = 0;
        for (Map<String, Object> oneData : qData) {
            ColumnDataValue dcp_woprodrepstat = new ColumnDataValue();
            dcp_woprodrepstat.add("EID", DataValues.newString(req.geteId()));
            dcp_woprodrepstat.add("CORP", DataValues.newString(req.getRequest().getCorp()));
            dcp_woprodrepstat.add("PORDERNO", DataValues.newString(pOrderNo));

//            dcp_woprodrepstat.add("WDATE", DataValues.newDate(oneData.get("ACCOUNT_DATE").toString()));
            dcp_woprodrepstat.add("WDATE", DataValues.newDate(DateFormatUtils.getDate(req.getRequest().getWDate())));
            dcp_woprodrepstat.add("STATUS", DataValues.newInteger("-1"));
//
            dcp_woprodrepstat.add("ITEM", DataValues.newInteger(++item));
            dcp_woprodrepstat.add("PLUNO", DataValues.newString(oneData.get("PLUNO").toString()));
            dcp_woprodrepstat.add("PLUNAME", DataValues.newString(oneData.get("PLU_NAME").toString()));
            dcp_woprodrepstat.add("COSTCENTERNO", DataValues.newString(oneData.get("DEPARTID").toString()));
            dcp_woprodrepstat.add("COSTCENTER", DataValues.newString(oneData.get("DEPARTNAME").toString()));

            if (StringUtils.isEmpty(oneData.get("OOFNO").toString())) {
                dcp_woprodrepstat.add("TASKID", DataValues.newString(oneData.get("PSTOCKINNO").toString()));
            } else {
                dcp_woprodrepstat.add("TASKID", DataValues.newString(oneData.get("OOFNO").toString()));
            }

            dcp_woprodrepstat.add("INVQTY", DataValues.newString(oneData.get("BASEQTY").toString()));

            double amt = Double.parseDouble(oneData.get("DISTRIAMT").toString());
            List<GoodsPrice> searchData;
            if ("Y".equals(costDomain)) {
                searchData = goodsPriceList.stream().filter(x -> x.getPluNo().equals(oneData.get("PLUNO").toString())
                        && x.getOrgNo().equals(req.getRequest().getCorp())
                ).collect(Collectors.toList());
            } else {
                searchData = goodsPriceList.stream().filter(x -> x.getPluNo().equals(oneData.get("PLUNO").toString())
                && x.getOrgNo().equals(oneData.get("SHOPID").toString())
                ).collect(Collectors.toList());
            }

            if (CollectionUtils.isNotEmpty(searchData)) {
                GoodsPrice goodsPrice = searchData.get(0);
                if (null != goodsPrice && StringUtils.isNotEmpty(goodsPrice.getFirstValidPrice())) {
                    amt = BigDecimalUtils.mul(
                            Double.parseDouble(goodsPrice.getFirstValidPrice()),
                            goodsPrice.getQty()
                    );
                }
            }

            dcp_woprodrepstat.add("INVAMOUNT", DataValues.newString(amt));
//            dcp_woprodrepstat.add("ENDWIPQTY", DataValues.newString(oneData.get("ENDWIPQTY").toString()));
//            dcp_woprodrepstat.add("ENDWIPEQRATE", DataValues.newString(oneData.get("ENDWIPEQRATE").toString()));
//            dcp_woprodrepstat.add("ENDWIPEQQTY", DataValues.newString(oneData.get("ENDWIPEQQTY").toString()));
            dcp_woprodrepstat.add("REPORTEDQUY", DataValues.newString(oneData.get("BASEQTY").toString()));

            dcp_woprodrepstat.add("PSTOCKINNO", DataValues.newString(oneData.get("PSTOCKINNO").toString()));
            dcp_woprodrepstat.add("PSTOCKINITEM", DataValues.newString(oneData.get("ITEM").toString()));

//            double actHours = secondToMinute(oneData.get("SPREPORTTIME").toString());
//            double actmachinehrs = secondToMinute(oneData.get("SREPORTSTATUS").toString());
//
//            dcp_woprodrepstat.add("ACTHOURS", DataValues.newString(actHours));
//            dcp_woprodrepstat.add("ACTMACHINEHRS", DataValues.newString(actmachinehrs));
//
//            dcp_woprodrepstat.add("STDHOURS", DataValues.newString(BigDecimalUtils.div(actHours, Double.parseDouble(oneData.get("SPPQTY").toString()))));
//            dcp_woprodrepstat.add("STDMACHINEHRS", DataValues.newString(BigDecimalUtils.div(actmachinehrs, Double.parseDouble(oneData.get("SPPQTY").toString()))));
//
//            dcp_woprodrepstat.add("REMARKS", DataValues.newString("工单生产报工统计生成"));

            dcp_woprodrepstat.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
            dcp_woprodrepstat.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
            dcp_woprodrepstat.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("dcp_woprodrepstat", dcp_woprodrepstat)));
        }

        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    private double secondToMinute(String second) {
        int s = Integer.parseInt(second);
        return BigDecimalUtils.div(s, 60);
    }


    @Override
    protected List<InsBean> prepareInsertData(DCP_WoProdRepStatCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_WoProdRepStatCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_WoProdRepStatCreateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_WoProdRepStatCreateReq req) throws Exception {
        return false;
    }

    protected String getQuerySql2(DCP_WoProdRepStatCreateReq req) throws Exception {
        StringBuilder builder = new StringBuilder();

        builder.append(" SELECT " +
                        " b.SHOPID,b.PSTOCKINNO,b.ITEM,b.PLUNO,gl.PLU_NAME " +
                        " ,g.SPEC,a.DEPARTID,a.OOFNO,b.BASEQTY,b.DISTRIAMT " +
                        " ,b.BASEUNIT,a.ACCOUNT_DATE,dl1.DEPARTNAME ")
                .append(" FROM DCP_PSTOCKIN a ")
                .append(" LEFT JOIN DCP_PSTOCKIN_DETAIL b on a.EID=b.eid and a.PSTOCKINNO=b.PSTOCKINNO  ")
                .append(" LEFT JOIN DCP_DEPARTMENT_LANG dl1 on dl1.eid=a.eid and dl1.DEPARTNO = a.DEPARTID AND dl1.LANG_TYPE='").append(req.getLangType()).append("'")
                .append(" LEFT JOIN DCP_GOODS g on g.eid=b.eid and b.PLUNO=g.PLUNO ")
                .append(" LEFT JOIN DCP_GOODS_LANG gl ON b.EID=gl.EID AND b.PLUNO=gl.PLUNO AND gl.LANG_TYPE='").append(req.getLangType()).append("'")
        ;

        builder.append(" WHERE a.EID='").append(req.geteId()).append("'");
//        builder.append(" and a.STATUS='2' and a.PROCESS_STATUS in ('Y')");
        builder.append(" and a.STATUS='2' ");
        builder.append(" and a.ORGANIZATIONNO='").append(req.getRequest().getCorp()).append("'");

        if (StringUtils.isNotEmpty(req.getRequest().getWDate())) {

            String wDate = DateFormatUtils.getPlainDate(req.getRequest().getWDate());

            String firstDay = wDate.substring(0, 6) + "01";
            String lastDay = DateFormatUtils.getYearMonthLastDate(
                    wDate.substring(0, 4),
                    wDate.substring(4, 6)
            );

            builder.append(" and a.ACCOUNT_DATE BETWEEN '")
                    .append(firstDay).append("' AND '").append(lastDay).append("'");
        }


        return builder.toString();
    }


    protected String getQuerySql(DCP_WoProdRepStatCreateReq req) throws Exception {

        StringBuilder builder = new StringBuilder();

        builder.append(" SELECT a.EID,a.BATCHTASKNO,b.SPASSQTY,b.SPPQTY,c.SPREPORTTIME,c.SEREPORTTIME " +
                " FROM MES_BATCHTASK a " +
                "         INNER JOIN (SELECT EID, BATCHTASKNO, SUM(PASSQTY) SPASSQTY, SUM(PPQTY) SPPQTY " +
                "                     FROM MES_PROCESS_REPORT " +
                "                     WHERE STATUS = '1' " +
                "                     GROUP BY EID, BATCHTASKNO) b on a.EID = b.EID and a.BATCHTASKNO = b.BATCHTASKNO " +
                "         INNER JOIN(SELECT a.EID, " +
                "                           a.BATCHTASKNO, " +
                "                           SUM(NVL(PREPORTTIME, 0)) SPREPORTTIME, " +
                "                           SUM(NVL(EREPORTTIME, 0)) SEREPORTTIME " +
                "                    FROM MES_PROCESS_REPORT a " +
                "                             LEFT JOIN MES_PROCESS_REPORT_DETAIL b on a.eid = b.eid and a.REPORTNO = b.REPORTNO " +
                "                    WHERE STATUS = '1' " +
                "                    GROUP BY a.EID, a.BATCHTASKNO) c " +
                "                   on b.eid = c.eid and b.BATCHTASKNO = c.BATCHTASKNO ");

        builder.append(" WHERE a.EID='").append(req.geteId()).append("'");
        builder.append(" and a.STATUS='1' and a.PRODUCTSTATUS in ('Y','Z')");
        builder.append(" and a.ORGANIZATIONNO='").append(req.getRequest().getCorp()).append("'");
        builder.append(" and a.BDATE='").append(req.getRequest().getWDate()).append("'");

        return builder.toString();
    }


    @Override
    protected TypeToken<DCP_WoProdRepStatCreateReq> getRequestType() {
        return new TypeToken<DCP_WoProdRepStatCreateReq>() {
        };
    }

    @Override
    protected DCP_WoProdRepStatCreateRes getResponseType() {
        return new DCP_WoProdRepStatCreateRes();
    }
}
