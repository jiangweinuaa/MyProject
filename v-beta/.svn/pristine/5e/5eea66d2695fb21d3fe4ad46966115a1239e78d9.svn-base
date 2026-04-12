package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_Blob1CreateReq;
import com.dsc.spos.json.cust.res.DCP_Blob1CreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_Blob1Create extends SPosAdvanceService<DCP_Blob1CreateReq, DCP_Blob1CreateRes>
{
    @Override
    protected void processDUID(DCP_Blob1CreateReq req, DCP_Blob1CreateRes res) throws Exception
    {
        //新增SQL

        /**
        String[] columnsNOTICE ={"NOTICENO","CONTENT"};
        DataValue[] insValue1 = null;

        String json = "{\"currency\":\"CNY\",\"amount\":100}";

        String hugeJson = "{"
                + "\"serviceId\":\"DCP_Blob1Create\","
                + "\"requestId\":\"805B409118236EAC\","
                + "\"plantType\":\"nrc\","
                + "\"version\":\"3.4.0.7-2024-09-19\","
                + "\"langType\":\"zh_CN\","
                + "\"timestamp\":\"20260310160859787\","
                + "\"remind\":true,"
                + "\"token\":\"803643d168df4117381377df98fc593e\","
                + "\"request\":{"
                + "\"no\":\"002\","
                + "\"content\":\"{\"currency\":\"CNY\",\"amount\":100,"
                + "\"items\":["
                + "{\\\"itemId\\\":\\\"ITM001\\\",\\\"name\\\":\\\"笔记本电脑\\\",\\\"qty\\\":5,\\\"price\\\":8000,"
                + "\\\"specs\\\":{\\\"cpu\\\":\\\"i7\\\",\\\"ram\\\":\\\"16GB\\\",\\\"storage\\\":\\\"512GB SSD\\\"},"
                + "\\\"attachments\\\":["
                + "{\\\"type\\\":\\\"image\\\",\\\"url\\\":\\\"https://example.com/img/laptop.jpg\\\",\\\"size\\\":2048000},"
                + "{\\\"type\\\":\\\"manual\\\",\\\"url\\\":\\\"https://example.com/manual.pdf\\\",\\\"size\\\":1500000}"
                + "],"
                + "\\\"remark\\\":\\\"高配办公设备\\\""
                + "},"
                + "{\\\"itemId\\\":\\\"ITM002\\\",\\\"name\\\":\\\"显示器\\\",\\\"qty\\\":5,\\\"price\\\":2500,"
                + "\\\"specs\\\":{\\\"size\\\":\\\"27寸\\\",\\\"resolution\\\":\\\"2K\\\"},"
                + "\\\"attachments\\\":[],"
                + "\\\"remark\\\":\\\"外接显示设备\\\""
                + "}"
                + "],"
                + "\\\"supplier\\\":{"
                + "\\\"id\\\":\\\"SUP1001\\\","
                + "\\\"name\\\":\\\"华强电子有限公司\\\","
                + "\\\"contact\\\":\\\"张经理\\\","
                + "\\\"phone\\\":\\\"13800138000\\\","
                + "\\\"address\\\":\\\"广东省深圳市福田区华强北路XX号\\\","
                + "\\\"certifications\\\":[\\\"ISO9001\\\",\\\"GB/T 28001\\\"]"
                + "},"
                + "\\\"delivery\\\":{"
                + "\\\"date\\\":\\\"2026-03-15\\\","
                + "\\\"warehouse\\\":\\\"WH-NRC-01\\\","
                + "\\\"address\\\":\\\"北京市朝阳区工业大道NRC仓库1号\\\","
                + "\\\"logistics\\\":\\\"顺丰速运\\\","
                + "\\\"trackingNo\\\":\\\"SF123456789CN\\\""
                + "},"
                + "\\\"notes\\\":\\\""
                + "此订单为年度采购计划第3批次，需在3月15日前完成交付。\\n"
                + "所有设备必须通过质检后方可入库。\\n"
                + "发票信息请按合同编号 DC-2025-0310 开具。\\n"
                + "如有变更，请提前48小时通知采购部。\\n"
                + "技术支持联系邮箱：support@company.com\\n"
                + "紧急联系电话：400-123-4567\\n"
                + "备注备注备注...（此处可继续添加大量文本以增大JSON体积）"
                + "\""
                + "}\""
                + "}"
                + "}";


        insValue1 = new DataValue[]
                {
                        new DataValue(req.getRequest().getNo(), Types.VARCHAR),
                        new DataValue(hugeJson, Types.BLOB),


                };


        InsBean ib1 = new InsBean("DCP_BLOB_TEST", columnsNOTICE);
        ib1.addValues(insValue1);
        this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

        this.doExecuteDataToDB();
         **/


        //测试一下库存先加后减
        //增加在库数
        String procedure = "SP_DCP_STOCKCHANGE_V35";
        Map<Integer, Object> inputParameter = new HashMap<Integer, Object>();
        inputParameter.put(1, 99);                              //--企业ID
        inputParameter.put(2, null);
        inputParameter.put(3, "NJDGW");                           //--组织
        inputParameter.put(4, "");
        inputParameter.put(5, "");
        inputParameter.put(6, "0");                      //--单据类型
        inputParameter.put(7, "phtz20221212001");                           //--单据号
        inputParameter.put(8, 1);    //--单据行号
        inputParameter.put(9, "0");
        inputParameter.put(10, "1");                               //--异动方向 1=加库存 -1=减库存
        inputParameter.put(11, "2026-03-26");   //--营业日期 yyyy-MM-dd
        inputParameter.put(12, "1403020001");   //--品号
        inputParameter.put(13, ""); //--特征码
        inputParameter.put(14, "NJDGW01");            //--仓库
        inputParameter.put(15, "");     //--批号
        inputParameter.put(16, "");     //--location
        inputParameter.put(17, "BAG");        //--交易单位
        inputParameter.put(18, 1000);         //--交易数量
        inputParameter.put(19, "BAG");     //--基准单位
        inputParameter.put(20, 1000);      //--基准数量
        inputParameter.put(21, 1);   //--换算比例
        inputParameter.put(22, 0);        //--零售价
        inputParameter.put(23, 0);          //--零售金额
        inputParameter.put(24, 1);  //--进货价
        inputParameter.put(25, 2);    //--进货金额

        inputParameter.put(26,0);
        inputParameter.put(27,0);
        inputParameter.put(28,"");
        inputParameter.put(29,1);

        inputParameter.put(30, "2026-03-25");                           //--入账日期 yyyy-MM-dd
        inputParameter.put(31, "2026-03-25");    //--批号的生产日期 yyyy-MM-dd
        inputParameter.put(32, "20260325");        //--单据日期
        inputParameter.put(33, "");                                     //--异动原因
        inputParameter.put(34, "");                                     //--异动描述
        inputParameter.put(35, req.getOpNO());                          //--操作员

        ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
        this.addProcessData(new DataProcessBean(pdb));


        Map<Integer, Object> inputParameter2 = new HashMap<Integer, Object>();
        inputParameter2.put(1, 99);                              //--企业ID
        inputParameter2.put(2, null);
        inputParameter2.put(3, "NJDGW");                           //--组织
        inputParameter2.put(4, "");
        inputParameter2.put(5, "");
        inputParameter2.put(6, "0");                      //--单据类型
        inputParameter2.put(7, "phtz20221212001");                           //--单据号
        inputParameter2.put(8, 1);    //--单据行号
        inputParameter2.put(9, "0");
        inputParameter2.put(10, "-1");                               //--异动方向 1=加库存 -1=减库存
        inputParameter2.put(11, "2026-03-26");   //--营业日期 yyyy-MM-dd
        inputParameter2.put(12, "1403020001");   //--品号
        inputParameter2.put(13, ""); //--特征码
        inputParameter2.put(14, "NJDGW01");            //--仓库
        inputParameter2.put(15, "");     //--批号
        inputParameter2.put(16, "");     //--location
        inputParameter2.put(17, "BAG");        //--交易单位
        inputParameter2.put(18, 1000);         //--交易数量
        inputParameter2.put(19, "BAG");     //--基准单位
        inputParameter2.put(20, 1000);      //--基准数量
        inputParameter2.put(21, 1);   //--换算比例
        inputParameter2.put(22, 0);        //--零售价
        inputParameter2.put(23, 0);          //--零售金额
        inputParameter2.put(24, 1);  //--进货价
        inputParameter2.put(25, 2);    //--进货金额

        inputParameter2.put(26,0);
        inputParameter2.put(27,0);
        inputParameter2.put(28,"");
        inputParameter2.put(29,1);

        inputParameter2.put(30, "2026-03-25");                           //--入账日期 yyyy-MM-dd
        inputParameter2.put(31, "2026-03-25");    //--批号的生产日期 yyyy-MM-dd
        inputParameter2.put(32, "20260325");        //--单据日期
        inputParameter2.put(33, "");                                     //--异动原因
        inputParameter2.put(34, "");                                     //--异动描述
        inputParameter2.put(35, req.getOpNO());                          //--操作员

        ProcedureBean pdb2 = new ProcedureBean(procedure, inputParameter2);
        this.addProcessData(new DataProcessBean(pdb2));

        this.doExecuteDataToDB();



        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_Blob1CreateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_Blob1CreateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_Blob1CreateReq req) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_Blob1CreateReq req) throws Exception
    {
        boolean isFail = false;


        return isFail;
    }

    @Override
    protected TypeToken<DCP_Blob1CreateReq> getRequestType()
    {
        // TODO Auto-generated method stub
        return new TypeToken<DCP_Blob1CreateReq>(){};
    }

    @Override
    protected DCP_Blob1CreateRes getResponseType() {
        // TODO Auto-generated method stub
        return new DCP_Blob1CreateRes();
    }

}
