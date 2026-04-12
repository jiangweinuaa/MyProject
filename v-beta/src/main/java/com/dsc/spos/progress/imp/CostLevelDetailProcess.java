package com.dsc.spos.progress.imp;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DsmDAO;
import com.dsc.spos.json.cust.req.DCP_CostLevelDetailProcessReq;
import com.dsc.spos.progress.ProgressService;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CostLevelDetailProcess extends ProgressService<DCP_CostLevelDetailProcessReq> {

    public CostLevelDetailProcess(DCP_CostLevelDetailProcessReq req) {
        super(req);
        setType(ProgressType.ProgressType_D);
        setMaxStep(10);
    }

    @Override
    public void runProgress() throws Exception {
//        1. 执行时先判断执行的账套+年度期别与DCP_Acount_SetingQuery中账套小于关账日期提醒前端报错
//        2. 账套+年度期别对应DCP_COSTLEVELDETAIL内是否有数据，先删除；
//        3. 建立DCP_COSTLEVELDETAIL成本阶明细表，账套+年度期别+法人组织按执行时默认赋值
//        4. 调用DCP_GoodsSetQuery
//        a. 将商品编码状态status为有效，将商品编码pluNo/商品名称name/商品特征atrrName/商品特征名称/基本单位unit/基准单位名称baseUnitName存入DCP_COSTLEVELDETAIL成本阶明细表，同时需要判断商品编码对应商品模板和要货模板编码templateId对应的组织是否归属与账套，不归属商品编码不写入；
//        b. 判断商品编码对应【是否虚拟商品：N否Y-是】VIRTUAL=是，更新【按发料计算的低阶码】MATERIALBOM存入为： 0【数字格式】；同时存货类型PLUTYPE更新为：1：制成品；后续调用完工入库和工单都不做修改调整；
//        5. 调用MES_BATCHTASK
//        a. 将组织编码ORGANIZATIONNO归属与法人组织且对应的账套，排除MES_BATCHTASK中状态为：成本结案的工单；抓取未结案工单+已结案工单，对应的主件编号PLUNO对应更新DCP_COSTLEVELDETAIL中【按BOM计算的低阶码】MATERIALBOM存入为：0 【数字格式】；同时存货类型PLUTYPE更新为：0：商品；
//        6. 调用完工入库查询DCP_PStockInQuery
//        a. 按账套对应法人组下归属组织+会计期查询完工入库时间范围
//        b. 查询DCP_PSTOCKIN_DETAIL 成品明细
//        ⅰ. 将单据类型docType=0.完工入库1.组合入库3.组合合并 同时状态= 6.待加工 2.已完成将对应的pluNo成品品号对应的对应更新CP_COSTLEVELDETAIL中【按发料计算的低阶码】MATERIALBOM存入为：0 【数字格式】；同时存货类型PLUTYPE更新为：0：商品；；
//        ⅱ. 将单据类型docType=2.拆解出库4.组合拆解同时状态= 6.待加工 2.已完成将对应的pluNo成品品号对应的对应更新CP_COSTLEVELDETAIL中【按发料计算的低阶码】MATERIALBOM存入为：1【数字格式】；同时存货类型PLUTYPE更新为：0：商品；；
//        c. 查询DCP_PSTOCKIN_MATERIAL  原料明细
//        ⅰ. 将单据类型docType=0.完工入库1.组合入库3.组合合并 同时状态= 6.待加工 2.已完成将对应的pluNo成品品号对应的对应更新CP_COSTLEVELDETAIL中【按发料计算的低阶码】MATERIALBOM存入为：99【数字格式】；同时存货类型PLUTYPE更新为：3：原料；
//        ⅱ. 将单据类型docType=2.拆解出库4.组合拆解同时状态= 6.待加工 2.已完成将对应的pluNo成品品号对应的对应更新CP_COSTLEVELDETAIL中【按发料计算的低阶码】MATERIALBOM存入为：98【数字格式】；同时存货类型PLUTYPE更新为：3：原料；
//        7. 除以上商品物料外，未赋值的商品调用DCP_BOTTOMLEVEL 将商品编码+商品名称对应的低阶码赋值到MATERIALBOM存入对应的数字；【数字格式】；另需要判断判断商品类型sourceType枚举1自制 2采购 3委外；2采购/3委外  存货类型PLUTYPE更新为：数字为 99 更新为3：原料；1自制 数字为 0 更新为 0 商品；
//        8. 更新【成本分群】和【主分群码】
//        a. 【主分群码】取值调用DCP_GoodsSetQuery将商品对应的品类编码存入
//        b. 【成本分群】调用 财务参数，按参数层级对应存入商品对应的品类层级别；举例：乌龙茶，财务参数成本分阶层为：2，商品对应品类为原材料【茶类】1001 层级
//        9. 更新成本阶
//        a. 调用：DCP_costLevelQuery 将【成本分群】对应的成本阶赋值给成本阶COSTLEVEL，取截止成本阶；
//        10. 效能：1 分钟以内


        DsmDAO dao = StaticInfo.dao;
        DCP_CostLevelDetailProcessReq req = getReq();

        String eid = req.geteId();
        String corp = req.getRequest().getCorp();
        String accountId = req.getRequest().getAccountID();

        String finGroupingLevel = PosPub.getPARA_SMS(dao, eid, corp, "FINGROUPINGLEVEL");

        incStep("正在判断关账日期!");
        String querySql = " SELECT * FROM DCP_ACOUNT_SETTING WHERE eid='" + req.geteId() + "' AND ACCOUNTID='" + accountId + "'";
        List<Map<String, Object>> accountInfo = dao.executeQuerySQL(querySql, null);
        if (null == accountInfo) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "帐套信息不存在!");
        } else {
            String closeDate = DateFormatUtils.getPlainYearMonth(accountInfo.get(0).get("CLOSINGDATE").toString());
            String nowDate = req.getRequest().getYear() + req.getRequest().getPeriod();
            if (nowDate.compareTo(closeDate) < 0) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "当前日期不可小于关账日期!");
            }
        }

        incStep("正在删除DCP_COSTLEVELDETAIL内数据");
        ColumnDataValue condition = new ColumnDataValue();
        condition.add("EID", DataValues.newString(eid));
        condition.add("CORP", DataValues.newString(corp));
        condition.add("ACCOUNTID", DataValues.newString(accountId));
        addProcessBean(new DataProcessBean(DataBeans.getDelBean("DCP_COSTLEVELDETAIL", condition)));

        incStep("正在查询商品");
        List<Map<String, Object>> goodsList = dao.executeQuerySQL(getQueryGoodsSql(req), null);

        incStep("正在查询工单");
        List<Map<String, Object>> mesTask = dao.executeQuerySQL(getQueryMES_BATCHTASK(req), null);

        incStep("正在查询完工入库明细");
        List<Map<String, Object>> pStockDetail = dao.executeQuerySQL(getQueryDCP_PStockInDetail(req), null);

        incStep("正在查询完工入库原料明细");
        List<Map<String, Object>> pStockMaterial = dao.executeQuerySQL(getQueryDCP_PStockIn_Material(req), null);

        incStep("正在查询成本阶");
        List<Map<String, Object>> costLevel = dao.executeQuerySQL(getQueryCostLevel(req), null);

        incStep("正在写入成本阶明细表");
        for (int i = 0; i < goodsList.size(); i++) {
            setStepDescription("正在写入成本阶明细表" + i + "/" + goodsList.size());

            Map<String, Object> onePluNo = goodsList.get(i);

            Map<String, Object> pluNoCondition = new HashMap<>();
            pluNoCondition.put("EID", DataValues.newString(onePluNo.get("EID").toString()));
            pluNoCondition.put("PLUNO", DataValues.newString(onePluNo.get("PLUNO").toString()));

//            Map<String, Object> pluNoFeatureNoCondition = new HashMap<>();
//            pluNoFeatureNoCondition.put("EID", DataValues.newString(onePluNo.get("EID").toString()));
//            pluNoFeatureNoCondition.put("PLUNO", DataValues.newString(onePluNo.get("PLUNO").toString()));
//            pluNoFeatureNoCondition.put("FEATURENO", DataValues.newString(onePluNo.get("FEATURENO").toString()));

            ColumnDataValue dcp_costleveldetail = new ColumnDataValue();
            dcp_costleveldetail.add("EID", DataValues.newString(req.geteId()));
            dcp_costleveldetail.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));
            dcp_costleveldetail.add("ACCOUNT", DataValues.newString(req.getRequest().getAccount()));
            dcp_costleveldetail.add("CORP", DataValues.newString(req.getRequest().getCorp()));
            dcp_costleveldetail.add("CORPNAME", DataValues.newString(req.getRequest().getCorp_Name()));
            dcp_costleveldetail.add("YEAR", DataValues.newString(req.getRequest().getYear()));
            dcp_costleveldetail.add("PERIOD", DataValues.newString(req.getRequest().getPeriod()));

            dcp_costleveldetail.add("ITEM", DataValues.newInteger(i + 1));
            dcp_costleveldetail.add("PLUNO", DataValues.newString(onePluNo.get("PLUNO").toString()));
//            dcp_costleveldetail.add("FEATURENO", DataValues.newString(onePluNo.get("FEATURENO").toString()));
            dcp_costleveldetail.add("BASEUNIT", DataValues.newString(onePluNo.get("BASEUNIT").toString()));
            dcp_costleveldetail.add("STATUS", DataValues.newInteger(100));

            dcp_costleveldetail.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
            dcp_costleveldetail.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
            dcp_costleveldetail.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

            Map<String, Object> costLevelCondition = new HashMap<>();
            costLevelCondition.put("EID", DataValues.newString(req.geteId()));
            costLevelCondition.put("COSTGROUPINGID", DataValues.newString(onePluNo.get("UP_CATEGORY").toString()));

            List<Map<String, Object>> oneCostLevel = MapDistinct.getWhereMap(costLevel, costLevelCondition, false);

            if (null != oneCostLevel && !oneCostLevel.isEmpty()) {
                dcp_costleveldetail.add("COSTLEVEL", DataValues.newString(oneCostLevel.get(0).get("END_COSTLEVEL").toString()));
            } else {
                dcp_costleveldetail.add("COSTLEVEL", DataValues.newString("0"));
            }

            String costGroupingId = getCostGroupingId(goodsList, onePluNo.get("CATEGORY").toString(), finGroupingLevel);
            dcp_costleveldetail.add("COSTGROUPINGID", DataValues.newString(costGroupingId));
            dcp_costleveldetail.add("CATEGORY", DataValues.newString(onePluNo.get("CATEGORY").toString()));

            String virtual = onePluNo.get("VIRTUAL").toString();
            if ("Y".equals(virtual)) {
                dcp_costleveldetail.add("MATERIALBOM", DataValues.newString(0));
                dcp_costleveldetail.add("PLUTYPE", DataValues.newString(1));
            } else {
                dcp_costleveldetail.add("MATERIALBOM", DataValues.newString(0));
                dcp_costleveldetail.add("PLUTYPE", DataValues.newString(0));
            }
//            dcp_costleveldetail.add("MATERIALSOURCE", DataValues.newString(0));

            List<Map<String, Object>> matchTask = MapDistinct.getWhereMap(mesTask, pluNoCondition, true);
            if (CollectionUtils.isNotEmpty(matchTask)) {
                dcp_costleveldetail.add("MATERIALBOM", DataValues.newString(0));
                dcp_costleveldetail.add("PLUTYPE", DataValues.newString(0));
            }

            List<Map<String, Object>> matchPStockIn = MapDistinct.getWhereMap(pStockDetail, pluNoCondition, true);
            if (CollectionUtils.isNotEmpty(matchTask)) {
                String docType = matchPStockIn.get(0).get("DOC_TYPE").toString();
                if ("2".equals(docType)) {
                    dcp_costleveldetail.add("MATERIALBOM", DataValues.newString(1));
                } else {
                    dcp_costleveldetail.add("MATERIALBOM", DataValues.newString(0));
                }
                dcp_costleveldetail.add("PLUTYPE", DataValues.newString(0));
            }

            List<Map<String, Object>> matchMaterial = MapDistinct.getWhereMap(pStockMaterial, pluNoCondition, true);
            if (CollectionUtils.isNotEmpty(matchMaterial)) {
                String docType = matchMaterial.get(0).get("DOC_TYPE").toString();
                if ("2".equals(docType)) {
                    dcp_costleveldetail.add("MATERIALBOM", DataValues.newString(98));
                } else {
                    dcp_costleveldetail.add("MATERIALBOM", DataValues.newString(99));
                }
                dcp_costleveldetail.add("PLUTYPE", DataValues.newString(3));
            }

            this.addProcessBean(new DataProcessBean(DataBeans.getInsBean("DCP_COSTLEVELDETAIL", dcp_costleveldetail)));
        }

        incStep("正在进行数据持久化");
        if (CollectionUtils.isNotEmpty(this.getPData())) {
            dao.useTransactionProcessData(this.getPData());
        }

        incStep("执行成功！");

    }

    @Override
    public void beforeRun() {

    }

    @Override
    public void afterRun() {

    }

    private String getQueryGoodsSql(DCP_CostLevelDetailProcessReq req) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT DISTINCT a.EID,a.PLUNO," +
                " a.VIRTUAL,b.PLU_NAME,a.BASEUNIT,BASE.UNAME BASEUNITNNAME " +
                " ,c.FEATURENO " +       //去除特征码
                ",a.CATEGORY,k.CATEGORYLEVEL,k.UP_CATEGORY " +
                " FROM DCP_GOODS A " +
                " LEFT JOIN DCP_GOODS_LANG B ON A.EID=b.EID and a.PLUNO=b.PLUNO AND B.LANG_TYPE='" + req.getLangType() + "'" +
                " LEFT JOIN DCP_UNIT_LANG BASE ON A.EID=BASE.EID AND A.BASEUNIT=BASE.UNIT AND BASE.LANG_TYPE='" + req.getLangType() + "' " +
                " LEFT JOIN DCP_GOODS_FEATURE C ON a.EID=c.EID and a.PLUNO=C.PLUNO AND C.STATUS='100' " +
                " left join DCP_category k on a.EID=k.eid and a.CATEGORY=k.CATEGORY "
        );

        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");
        sb.append(" AND a.STATUS='100' ");

        return sb.toString();
    }

    private String getQueryMES_BATCHTASK(DCP_CostLevelDetailProcessReq req) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT DISTINCT a.EID,a.PLUNO,a.BOMNO " +
                " FROM MES_BATCHTASK a "
        );
        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");
        sb.append(" AND a.STATUS<>'2' ");  //排除作废单据
        sb.append(" AND (a.PRODUCTSTATUS='N' OR a.PRODUCTSTATUS='Y') ");
        sb.append(" AND a.ORGANIZATIONNO='").append(req.getRequest().getCorp()).append("'");
        sb.append(" AND a.BDATE like '%%").append(req.getRequest().getYear() + req.getRequest().getPeriod()).append("%%'");

        return sb.toString();
    }

    private String getQueryDCP_PStockInDetail(DCP_CostLevelDetailProcessReq req) {
        StringBuilder sb = new StringBuilder();
        String yearMonth = req.getRequest().getYear() + req.getRequest().getPeriod();

        sb.append(" SELECT DISTINCT a.EID,a.DOC_TYPE,b.PLUNO,b.FEATURENO " +
                " FROM DCP_PSTOCKIN a " +
                " INNER JOIN DCP_PSTOCKIN_DETAIL b ON a.EID=b.EID and a.SHOPID=b.SHOPID and a.PSTOCKINNO=b.PSTOCKINNO and a.ORGANIZATIONNO=b.ORGANIZATIONNO "
        );

        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");
        sb.append(" AND (a.STATUS='6' OR  a.STATUS='2' )");
        sb.append(" AND a.BDATE like '%%").append(yearMonth).append("%%'");
        sb.append(" AND a.ORGANIZATIONNO='").append(req.getRequest().getCorp()).append("'");

        return sb.toString();
    }

    private String getQueryDCP_PStockIn_Material(DCP_CostLevelDetailProcessReq req) {
        StringBuilder sb = new StringBuilder();
        String yearMonth = req.getRequest().getYear() + req.getRequest().getPeriod();

        sb.append(" SELECT DISTINCT a.EID,a.DOC_TYPE,b.PLUNO,b.FEATURENO " +
                " FROM DCP_PSTOCKIN a " +
                " INNER JOIN DCP_PSTOCKIN_MATERIAL b ON a.EID=b.EID and a.SHOPID=b.SHOPID and a.PSTOCKINNO=b.PSTOCKINNO and a.ORGANIZATIONNO=b.ORGANIZATIONNO "
        );

        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");
        sb.append(" AND (a.STATUS='6' OR  a.STATUS='2' ) ");
        sb.append(" AND a.BDATE like '%%").append(yearMonth).append("%%'");
        sb.append(" AND a.ORGANIZATIONNO='").append(req.getRequest().getCorp()).append("'");

        return sb.toString();
    }

    private String getQueryCostLevel(DCP_CostLevelDetailProcessReq req) {
        StringBuilder sb = new StringBuilder();

//        String yearMonth = req.getRequest().getYear() + req.getRequest().getPeriod();

        sb.append(" SELECT a.* " +
                " FROM DCP_COSTLEVEL a "
        );

        sb.append(" WHERE a.EID='").append(req.geteId()).append("'");
        sb.append(" AND a.STATUS='100' ");


        return sb.toString();
    }

    private String getCostGroupingId(List<Map<String, Object>> goodsList, String category, String finGroupingLevel) {

        Map<String, Object> condition = new HashMap<>();
        condition.put("CATEGORY", category);

        List<Map<String, Object>> goodsInfo = MapDistinct.getWhereMap(goodsList, condition, false);

        double categoryLevel = Double.parseDouble(goodsInfo.get(0).get("CATEGORYLEVEL").toString());
        String up_category = goodsInfo.get(0).get("UP_CATEGORY").toString();

        if (categoryLevel == Double.parseDouble(finGroupingLevel)) {
            return category;
        } else {
            return getCostGroupingId(goodsList, up_category, finGroupingLevel);
        }

    }


}
