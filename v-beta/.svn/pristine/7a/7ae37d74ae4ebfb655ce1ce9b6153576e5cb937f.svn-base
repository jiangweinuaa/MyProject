package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_PreCostCalChkExportReq;
import com.dsc.spos.json.cust.res.DCP_PreCostCalChkExportRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataValues;
import com.dsc.spos.utils.excel.ExcelUtils;
import com.google.gson.reflect.TypeToken;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_PreCostCalChkExport extends SPosAdvanceService<DCP_PreCostCalChkExportReq, DCP_PreCostCalChkExportRes> {

    @Override
    protected void processDUID(DCP_PreCostCalChkExportReq req, DCP_PreCostCalChkExportRes res) throws Exception {
        String eId = req.geteId();
        String lastmoditime =	new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

        //1.入库相关  2.材料相关 3.4.工单相关
        String bType = req.getRequest().getBType();
        String corp = req.getRequest().getCorp();
        String corp_name = req.getRequest().getCorp_Name();
        String accountID = req.getRequest().getAccountID();
        String account = req.getRequest().getAccount();
        String year = req.getRequest().getYear();
        String period = req.getRequest().getPeriod();

        String dateSmall=year+(Integer.valueOf(period)<10?"0"+period:period)+"01";
        String dateLarge=year+(Integer.valueOf(period)<10?"0"+period:period)+"31";

        List<DCP_PreCostCalChkExportRes.CkList> errorMessage=new ArrayList();

        if("1".equals(bType)|| Check.Null(bType)){
            //05.料件本期入库金额为 0
            String settleSql="select a.billno,a.pluno,d.plu_name as pluname,a.featureno,e.featurename,f.spec,nvl(a.billprice,0) billprice  " +
                    " from DCP_SETTLEDATA a " +
                    " left join DCP_SSTOCKIN_DETAIL b on a.eid=b.eid and a.billno=b.sstockinno and a.item=b.item " +
                    " left join DCP_SSTOCKOUT_DETAIL c on a.eid=c.eid and a.billno=c.sstockoutno and a.item=c.item " +
                    " left join dcp_goods_lang d on d.eid=a.eid and d.pluno=a.pluno and d.lang_type='"+req.getLangType()+"'" +
                    " left join dcp_goods_feature_lang e on e.eid=a.eid and e.featureno=a.featureno and e.pluno=a.pluno and e.lang_type='"+req.getLangType()+"' " +
                    " left join dcp_goods f on f.eid=a.eid and f.pluno=a.pluno " +
                    " where a.eid='"+eId+"' and a.organizationno='"+corp+"'" +
                    " and a.year='"+year+"' and a.month='"+period+"'" +
                    " and a.btype in ('1','2','3','6') and nvl(a.billprice,0)<=0 " +
                    " and nvl(b.isgift,'N')='N' and nvl(c.isgift,'N')='N' ";
            List<Map<String, Object>> list1 = this.doQueryData(settleSql, null);
            String inSettleSql="select a.billno,a.pluno,d.plu_name as pluname,a.featureno,e.featurename,f.spec,nvl(a.billprice,0) billprice   " +
                    " from DCP_INTERSETTLEMENT a " +
                    " left join dcp_goods_lang d on d.eid=a.eid and d.pluno=a.pluno and d.lang_type='"+req.getLangType()+"'" +
                    " left join dcp_goods_feature_lang e on e.eid=a.eid and e.featureno=a.featureno and e.pluno=a.pluno and e.lang_type='"+req.getLangType()+"' " +
                    " left join dcp_goods f on f.eid=a.eid and f.pluno=a.pluno " +
                    " where a.eid='"+eId+"' and a.organizationno='"+corp+"'" +
                    " and a.year='"+year+"' and a.month='"+period+"'" +
                    "  and nvl(a.billprice,0)<=0 " +
                    "  ";
            List<Map<String, Object>> list2 = this.doQueryData(inSettleSql, null);
            List<Map<String, Object>> filterList1 = list1.stream().filter(x -> new BigDecimal(x.get("BILLPRICE").toString()).compareTo(BigDecimal.ZERO) == 0).collect(Collectors.toList());
            List<Map<String, Object>> filterList2 = list2.stream().filter(x -> new BigDecimal(x.get("BILLPRICE").toString()).compareTo(BigDecimal.ZERO)== 0).collect(Collectors.toList());
            insertMessage(req,res, errorMessage, filterList1,"料件本期入库金额为0");
            insertMessage(req,res, errorMessage, filterList2,"料件本期入库金额为0");

            //06.料件本期入库金额为负数
            List<Map<String, Object>> filterList3 = list1.stream().filter(x -> new BigDecimal(x.get("BILLPRICE").toString()).compareTo(BigDecimal.ZERO) < 0).collect(Collectors.toList());
            List<Map<String, Object>> filterList4 = list2.stream().filter(x -> new BigDecimal(x.get("BILLPRICE").toString()).compareTo(BigDecimal.ZERO) < 0).collect(Collectors.toList());
            insertMessage(req,res, errorMessage, filterList3,"料件本期入库金额为负数");
            insertMessage(req,res, errorMessage, filterList4,"料件本期入库金额为负数");

            //07.料件在杂项单内单价为 0
            String misSql="select a.PRODNO as PLUNO,a.PRODNAME as pluname,a.FEATURENO,'' as SPEC,'' AS BILLNO " +
                    " from DCP_MISCINOUT a " +
                    " where a.eid='"+eId+"' and a.ACCOUNTID='"+accountID+"' " +
                    " and a.corp='"+corp+"' and a.year='"+year+"' " +
                    " and a.PERIOD='"+period+"' and a.MATERIAL=0 ";
            List<Map<String, Object>> list3 = this.doQueryData(misSql, null);
            insertMessage(req,res, errorMessage, list3,"料件在杂项单内单价为0");

        }

        if("2".equals(bType)|| Check.Null(bType)){
            //01.此料号未参与成本阶计算
            String sql1="select a.*,b.plu_name as pluname,c.featurename from DCP_STOCK_DETAIL a " +
                    " left join dcp_goods_lang  b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='"+req.getLangType()+"'" +
                    " left join DCP_GOODS_FEATURE_LANG c on c.eid=a.eid and c.pluno=a.pluno and c.featureno=a.featureno and c.lang_type='"+req.getLangType()+"'" +
                    " where a.eid='"+eId+"' " +
                    " and a.organizationno='"+corp+"' " +
                    " and to_char(a.bdate,'yyyyMMdd')>='"+dateSmall+"' " +
                    " and to_char(a.bdate,'yyyyMMdd')<='"+dateLarge+"' ";
            List<Map<String, Object>> list1 = this.doQueryData(sql1, null);

            String sql2="select a.*,b.plu_name as pluname,c.featurename from DCP_COSTLEVELDETAIL a" +
                    " left join dcp_goods_lang  b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='"+req.getLangType()+"'" +
                    " left join DCP_GOODS_FEATURE_LANG c on c.eid=a.eid and c.pluno=a.pluno and c.featureno=a.featureno and c.lang_type='"+req.getLangType()+"'" +
                    " where a.eid='"+eId+"' and a.ACCOUNTID='"+accountID+"' " +
                    " and a.CORP='"+corp+"' and a.year='"+year+"' " +
                    " and a.period='"+period+"' ";
            List<Map<String, Object>> list2 = this.doQueryData(sql2, null);
            List<String> costPluNos = list2.stream()
                    .map(x -> x.get("PLUNO").toString()+";"+x.get("PLUNAME").toString()+";"+x.get("FEATURENO").toString()+";"+x.get("FEATURENAME").toString())
                    .distinct().collect(Collectors.toList());
            List<String> pluNos = list1.stream()
                    .map(x -> x.get("PLUNO").toString()+";"+x.get("PLUNAME").toString()+";"+x.get("FEATURENO").toString()+";"+x.get("FEATURENAME").toString())
                    .distinct().collect(Collectors.toList());
            pluNos.removeAll(costPluNos);
            if(pluNos.size()>0){
                for (String pluNo:pluNos){
                    if(pluNo.length()>0){
                        String pluNoArr[]=pluNo.split(";");
                        DCP_PreCostCalChkExportRes.CkList singleCk = res.new CkList();
                        singleCk.setCorp(corp);
                        singleCk.setCorpName(corp_name);
                        singleCk.setYear(year);
                        singleCk.setPeriod(period);
                        singleCk.setAccountID(accountID);
                        singleCk.setAccount(account);
                        singleCk.setPluNo(pluNoArr[0]);
                        singleCk.setPluName(pluNoArr[1]);
                        singleCk.setFeatureNo(pluNoArr[2]);
                        singleCk.setBType(bType);
                        singleCk.setBNo("");
                        singleCk.setErrorMessage("此料号未参与成本阶计算");
                        errorMessage.add(singleCk);
                    }
                }
            }

            //04.料号未设置商品分类
            String sql3="select a.pluno,b.plu_name as pluname from dcp_goods a" +
                    " left join dcp_goods_lang  b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='"+req.getLangType()+"'" +
                    " where a.eid='"+eId+"' " +
                    " and to_char(a.CREATETIME,'yyyyMMdd')>='"+dateSmall+"'" +
                    " and to_char(a.createtime,'yyyyMMdd')<='"+dateLarge+"' " +
                    " and ( a.category='' or a.category is null) " ;
            List<Map<String, Object>> list3 = this.doQueryData(sql3, null);

            for (Map<String, Object> singleList3:list3){
                DCP_PreCostCalChkExportRes.CkList singleCk = res.new CkList();
                singleCk.setCorp(corp);
                singleCk.setCorpName(corp_name);
                singleCk.setYear(year);
                singleCk.setPeriod(period);
                singleCk.setAccountID(accountID);
                singleCk.setAccount(account);
                singleCk.setPluNo(singleList3.get("PLUNO").toString());
                singleCk.setPluName(singleList3.get("PLUNAME").toString());
                singleCk.setFeatureNo("");
                singleCk.setBType(bType);
                singleCk.setBNo("");
                singleCk.setErrorMessage("料号未设置商品分类");
                errorMessage.add(singleCk);
            }
        }

        if("3".equals(bType)||"4".equals(bType)|| Check.Null(bType)){
            //02.工单已入库,但未有报工资料
            String taskSql="select a.BATCHTASKNO as billno,a.BATCHTASKNO,c.plu_name as pluname ,'' as featureno " +
                    " from MES_BATCHTASK a " +
                    " left join MES_PROCESS_REPORT b on a.BATCHTASKNO=b.BATCHTASKNO and a.eid=b.eid and a.organizationno=b.organizationno " +
                    " left join dcp_goods_lang c on c.eid=a.eid and c.pluno=a.pluno and c.lang_type='"+req.getLangType()+"' " +
                    " where a.eid='"+eId+"' and a.organizationno='"+corp+"' " +
                    " and a.PRODUCTSTATUS in ('Y','Z') and a.BEGINDATE>='"+dateSmall+"'" +
                    " and a.BEGINDATE<='"+dateLarge+"' and nvl(b.reportno,'')='' ";
            List<Map<String, Object>> list1 = this.doQueryData(taskSql, null);
            insertMessage(req,res, errorMessage, list1,"工单已入库,但未有报工资料");
            //03.截止当前期别工单已完工入库数量*，料未发足，请检查！（一般工单） --暂不处理
            //08.成本中心为空
            String taskSql2="select a.BATCHTASKNO as billno,a.BATCHTASKNO,c.plu_name as pluname ,'' as featureno " +
                    " from MES_BATCHTASK a " +
                    " left join dcp_goods_lang c on c.eid=a.eid and c.pluno=a.pluno and c.lang_type='"+req.getLangType()+"' " +
                    " where a.eid='"+eId+"' and a.organizationno='"+corp+"' " +
                    " and a.PRODUCTSTATUS in ('Y','Z') and a.BEGINDATE>='"+dateSmall+"'" +
                    " and a.BEGINDATE<='"+dateLarge+"' and nvl(a.RESPCENTER,'')='' ";
            List<Map<String, Object>> list2 = this.doQueryData(taskSql2, null);
            insertMessage(req,res, errorMessage, list2,"成本中心为空");

        }

        List<DCP_PreCostCalChkExportRes.CkList> returnMessage = errorMessage.stream()
                .collect(Collectors.toList());

        List data=new ArrayList();
        //列
        Map headerMap=new HashMap();
        headerMap.put("法人", "法人");
        headerMap.put("法人名称", "法人名称");
        headerMap.put("年度", "年度");
        headerMap.put("期别", "期别");
        headerMap.put("账套", "账套");
        headerMap.put("账套名称", "账套名称");
        headerMap.put("商品编码", "商品编码");
        headerMap.put("商品名称", "商品名称");
        headerMap.put("特征", "特征");
        headerMap.put("作业名称", "作业名称");
        headerMap.put("单据编号", "单据编号");
        headerMap.put("报错信息", "报错信息");
        data.add(headerMap);
        //值
        for (DCP_PreCostCalChkExportRes.CkList singleMessage:returnMessage){
            String bTypeMessage="";
            switch (singleMessage.getBType()){
                case "1":
                    bTypeMessage="入库相关";
                    break;
                case "2":
                    bTypeMessage="材料相关";
                    break;
                case "3":
                    bTypeMessage="工单相关";
                    break;
                case "4":
                    bTypeMessage="工单相关";
                    break;
            }
            Map<String, Object> singleData = new HashMap<>();
            singleData.put("法人", singleMessage.getCorp());
            singleData.put("法人名称", singleMessage.getCorpName());
            singleData.put("年度", singleMessage.getYear());
            singleData.put("期别", singleMessage.getPeriod());
            singleData.put("账套", singleMessage.getAccountID());
            singleData.put("账套名称", singleMessage.getAccount());
            singleData.put("商品编码", singleMessage.getPluNo());
            singleData.put("商品名称", singleMessage.getPluName());
            singleData.put("特征", singleMessage.getFeatureNo());
            singleData.put("作业名称", bTypeMessage);
            singleData.put("单据编号", singleMessage.getBNo());
            singleData.put("报错信息", singleMessage.getErrorMessage());
            data.add(singleData);
        }
        String excelName = "成本计算前检查"+lastmoditime;
        String directory="PreCostCalChk";
        String filePath=ExcelUtils.convertToExcelWithDirectory(data, excelName,"PreCostCalChk");

        String createTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());

        //文件信息写表
        //随机id
        String mainTaskId = UUID.randomUUID().toString().replace("-", "");
        ColumnDataValue mainColumns=new ColumnDataValue();
        mainColumns.add("EID", DataValues.newString(eId));
        mainColumns.add("MAINTASKID", DataValues.newString(mainTaskId));
        mainColumns.add("YEAR", DataValues.newString(req.getRequest().getYear()));
        mainColumns.add("PERIOD", DataValues.newString(req.getRequest().getPeriod()));
        mainColumns.add("JOBNAME", DataValues.newString(excelName));
        mainColumns.add("DIRECTORY", DataValues.newString(directory));
        mainColumns.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
        mainColumns.add("CREATETIME", DataValues.newDate(createTime));
        mainColumns.add("ACCOUNTID", DataValues.newString(req.getRequest().getAccountID()));
        mainColumns.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
        mainColumns.add("STATUS", DataValues.newString("0"));
        mainColumns.add("IMPSTATEINFO", DataValues.newString("0"));

        String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
        DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
        InsBean ib=new InsBean("DCP_EXPORTDETAIL",mainColumnNames);
        ib.addValues(mainDataValues);
        this.addProcessData(new DataProcessBean(ib));

        this.doExecuteDataToDB();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PreCostCalChkExportReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PreCostCalChkExportReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PreCostCalChkExportReq req) throws Exception {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_PreCostCalChkExportReq req) throws Exception {
        // TODO 自动生成的方法存根
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");


        if (isFail)
        {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_PreCostCalChkExportReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_PreCostCalChkExportReq>(){};
    }

    @Override
    protected DCP_PreCostCalChkExportRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_PreCostCalChkExportRes();
    }

    private void insertMessage(DCP_PreCostCalChkExportReq req, DCP_PreCostCalChkExportRes res, List<DCP_PreCostCalChkExportRes.CkList> errorMessageList, List<Map<String, Object>> list1, String errorMessage) {
        String bType = req.getRequest().getBType();
        String corp = req.getRequest().getCorp();
        String corp_name = req.getRequest().getCorp_Name();
        String accountID = req.getRequest().getAccountID();
        String account = req.getRequest().getAccount();
        String year = req.getRequest().getYear();
        String period = req.getRequest().getPeriod();
        for (Map<String, Object> map : list1){
            DCP_PreCostCalChkExportRes.CkList singleCk = res.new CkList();
            singleCk.setCorp(corp);
            singleCk.setCorpName(corp_name);
            singleCk.setYear(year);
            singleCk.setPeriod(period);
            singleCk.setAccountID(accountID);
            singleCk.setAccount(account);
            singleCk.setPluNo(map.get("PLUNO").toString());
            singleCk.setPluName(map.get("PLUNAME").toString());
            singleCk.setFeatureNo(map.get("FEATURENO").toString());
            singleCk.setBType(bType);
            singleCk.setBNo(map.get("BILLNO").toString());
            singleCk.setErrorMessage(errorMessage);
            errorMessageList.add(singleCk);
        }
    }

}


