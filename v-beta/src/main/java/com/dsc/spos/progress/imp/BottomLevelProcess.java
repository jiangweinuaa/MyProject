package com.dsc.spos.progress.imp;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DsmDAO;
import com.dsc.spos.json.cust.req.DCP_BottomLevelProcessReq;
import com.dsc.spos.progress.ProgressService;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.DateFormatUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class BottomLevelProcess extends ProgressService<DCP_BottomLevelProcessReq> {

    public BottomLevelProcess(DCP_BottomLevelProcessReq req) {
        super(req);
        setType(ProgressType.ProgressType_C);
        setMaxStep(4);
    }

    @Override
    public void runProgress() throws Exception {
//        1. 执行时先判断账套对应DCP_bottomLevel内是否有数据，先删除；
//        2. 商品信息抓取
//        a. 调用：DCP_GoodsSetQuery排除category商品类型=费用，且 status =启用；
//        b. 将pluNo商品编码 +商品名称 存入临时表内；建议建立临时表名：ima_tmp ；将 ima_tmp 内商品编码对应统一增加列：列名【低阶码】给值 99【数字格式】
//        c. 商品编码+商品名称+低阶码 三个字段
//        3. bom 信息抓取
//        a. 调用：DCP_BOM 排除STATUS状态：-1未启用 0已禁用
//        b. 将主件编码PLUNO+s商品名称存入临时表；议建立临时表名：bom_tmp ；将 bom_tmp 内商品编码对应产出类型：0一般产品(主件）统一增加列：列名【低阶码】给值 0【数字格式】，产出类型为 1 分解品 统一给【低阶码】98
//                c. 主件编码+商品名称+低阶码 三个字段
//        4. 元件信息抓取
//        a. 调用：DCP_BOM_MATERIAL
//        b. 判断本次执行年度期别在DCP_BOM_MATERIAL 的子件生效日期MATERIAL_BDATE大于等于≥和子件失效日期MATERIAL_EDATE小于＜；如不在日期内不抓取；
//        c. 将子件编码MATERIAL_PLUNO+商品名称+低阶码存入临时表；议建立临时表名：bmb_tmp，增加列【低阶码】给值 99【数字格式】
//        d. 当子件编码存在于DCP_BOM 内给低阶码值为【1】 数字格式
//        e. 子件编码+商品名称+低阶码 三个字段
//        5. 替换料
//        a. 调用DCP_BOM_MATERIAL_R
//        b. 判断本次执行年度期别在DCP_BOM_MATERIAL_R 的REPLACE_PLUNO 替代料生效日期REPLACE_BDATE大于等于≥和子件失效日期REPLACE_EDATE小于＜；如不在日期内不抓取；
//        c. 将子件编码REPLACE_PLUNO+商品名称+低阶码存入临时表；议建立临时表名：bmb_tmp_R，增加列【低阶码】给值 99【数字格式】
//        d. 当替代料件存在于DCP_BOM 内给低阶码值为【1】 数字格式
//        e. 替代料件+商品名称+低阶码 三个字段将
//        6. 建立【低阶码】表
//        a. 将 ima_tmp 表直接给入
//        b. 将 bom_tmp表内主键编码对应【商品编码】更新对应的【低阶码】
//        c. 将 bmb_tmp 表内子件编码对应【商品编码】更新对应的【低阶码】
//        d. 将 bmb_tmp_R 表内子件编码对应【商品编码】更新对应的【低阶码】
//        7. 数据建立完成，反馈前端；
        DsmDAO dao = StaticInfo.dao;
        DCP_BottomLevelProcessReq req = getReq();

        incStep();
        setStepDescription("正在删除低阶码");
        ColumnDataValue condition = new ColumnDataValue();

        condition.append("EID", DataValues.newString(req.geteId()));
        condition.append("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));
        condition.append("ACCOUNT", DataValues.newString(req.getRequest().getAccount()));

        this.addProcessBean(new DataProcessBean(DataBeans.getDelBean("dcp_bottomlevel", condition)));

        incStep();
        setStepDescription("正在查询低阶码信息");
        List<Map<String, Object>> getData = dao.executeQuerySQL(getSqlQuery(req), null);
        if (null != getData) {
            int i = 1;
            incStep();
            for (Map<String, Object> oneData : getData) {
                setStepDescription("正在写入数据" + i + "/" + getData.size());

                ColumnDataValue dcp_bottomlevel = new ColumnDataValue();
                dcp_bottomlevel.append("EID", DataValues.newString(req.geteId()));
                dcp_bottomlevel.append("STATUS", DataValues.newInteger(0));
                dcp_bottomlevel.append("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
                dcp_bottomlevel.append("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
                dcp_bottomlevel.append("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));
                dcp_bottomlevel.append("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));
                dcp_bottomlevel.append("ACCOUNT", DataValues.newString(req.getRequest().getAccount()));
                dcp_bottomlevel.append("PLUNO", DataValues.newString(oneData.get("PLUNO")));
                dcp_bottomlevel.append("BOTTOMLEVEL", DataValues.newString(oneData.get("BOTTOMLEVEL")));
                dcp_bottomlevel.append("ITEM", DataValues.newInteger(i++));

                addProcessBean(new DataProcessBean(DataBeans.getInsBean("dcp_bottomlevel", dcp_bottomlevel)));

            }

        }

        incStep();
        setStepDescription("正在进行数据持久化");
        if (CollectionUtils.isNotEmpty(this.getPData())) {
            dao.useTransactionProcessData(this.getPData());
        }

        incStep();
        setStepDescription("执行成功！");

    }

    @Override
    public void beforeRun() {

    }

    @Override
    public void afterRun() {

    }


    private String getSqlQuery(DCP_BottomLevelProcessReq req) {
        String bDate = DateFormatUtils.getDate(req.getRequest().getYear() + req.getRequest().getPeriod());

        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT PLUNO,MIN(BOTTOMLEVEL) BOTTOMLEVEL ")
                .append("FROM ( ")
                .append("   SELECT PLUNO, 99 as BOTTOMLEVEL ")
                .append("      FROM DCP_GOODS")
                .append("      WHERE STATUS = '100'")
                .append(" UNION ALL ")
                .append("   SELECT PLUNO, CASE ")
                .append("                 WHEN BOMTYPE = '0' THEN 0 ")
                .append("                 WHEN BOMTYPE = '1' THEN 98 ")
                .append("                 ELSE 99 END AS BOTTOMLEVEL ")
                .append("      FROM DCP_BOM ")
                .append("      WHERE STATUS = '100'")
                .append(" UNION ALL ")
                .append(" SELECT MATERIAL_PLUNO, ")
                .append("             CASE WHEN NVL(b.PLUNO, '') = '' THEN 99 ")
                .append("               ELSE 1 END AS BOTTOMLEVEL ")
                .append("      FROM DCP_BOM_MATERIAL a ")
                .append("      LEFT JOIN DCP_BOM b ON a.EID = b.EID and a.MATERIAL_PLUNO = b.PLUNO ")
                .append(" WHERE TO_CHAR(MATERIAL_EDATE,'yyyy-mm-dd')>='").append(bDate).append("' AND TO_CHAR(MATERIAL_BDATE,'yyyy-mm-dd')<='").append(bDate).append("'")
                .append(" UNION ALL ")
                .append(" SELECT REPLACE_PLUNO, ")
                .append(" CASE WHEN NVL(b.PLUNO, '') = '' THEN 99 ELSE 1 END AS BOTTOMLEVEL ")
                .append("      FROM DCP_BOM_MATERIAL_R a ")
                .append("      LEFT JOIN DCP_BOM b ON a.EID = b.EID and a.REPLACE_PLUNO = b.PLUNO ")
                .append(" WHERE TO_CHAR(REPLACE_EDATE,'yyyy-mm-dd') >='").append(bDate).append("' AND TO_CHAR(REPLACE_BDATE,'yyyy-mm-dd')<='").append(bDate).append("'")
                .append(")TEMP GROUP BY PLUNO");

        return sb.toString();
    }

}
