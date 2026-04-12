package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_PreCostCalChkProcessReq;
import com.dsc.spos.json.cust.res.DCP_PreCostCalChkProcessRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_PreCostCalChkProcess extends SPosBasicService<DCP_PreCostCalChkProcessReq, DCP_PreCostCalChkProcessRes> {
    @Override
    protected boolean isVerifyFail(DCP_PreCostCalChkProcessReq req) throws Exception {
        // TODO 自动生成的方法存根
        if (req.getRequest() == null) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "request节点不存在！");
        }
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();

        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_PreCostCalChkProcessReq> getRequestType() {
        // TODO 自动生成的方法存根
        return new TypeToken<DCP_PreCostCalChkProcessReq>() {
        };
    }

    @Override
    protected DCP_PreCostCalChkProcessRes getResponseType() {
        // TODO 自动生成的方法存根
        return new DCP_PreCostCalChkProcessRes();
    }


    @Override
    protected DCP_PreCostCalChkProcessRes processJson(DCP_PreCostCalChkProcessReq req) throws Exception {
        DCP_PreCostCalChkProcessRes res = this.getResponse();
        String eId = req.geteId();

        //1.入库相关  2.材料相关 3.4.工单相关
        String bType = req.getRequest().getBType();
        String corp = req.getRequest().getCorp();
        String corp_name = req.getRequest().getCorp_Name();
        String accountID = req.getRequest().getAccountID();
        String account = req.getRequest().getAccount();
        String year = req.getRequest().getYear();
        String period = req.getRequest().getPeriod();

        String dateSmall = year + (Integer.valueOf(period) < 10 ? "0" + period : period) + "01";
        String dateLarge = year + (Integer.valueOf(period) < 10 ? "0" + period : period) + "31";

        List<DCP_PreCostCalChkProcessRes.CkList> errorMessage = new ArrayList();

        if ("1".equals(bType) || Check.Null(bType)) {
            //05.料件本期入库金额为 0
            String settleSql = "select a.billno,a.pluno,d.plu_name as pluname,a.featureno,e.featurename,f.spec,nvl(a.billprice,0) billprice  " +
                    " from DCP_SETTLEDATA a " +
                    " inner join DCP_SSTOCKIN_DETAIL b on a.eid=b.eid and a.billno=b.sstockinno and a.item=b.item " +
                    " left join dcp_goods_lang d on d.eid=a.eid and d.pluno=a.pluno and d.lang_type='" + req.getLangType() + "'" +
                    " left join dcp_goods_feature_lang e on e.eid=a.eid and e.featureno=a.featureno and e.pluno=a.pluno and e.lang_type='" + req.getLangType() + "' " +
                    " left join dcp_goods f on f.eid=a.eid and f.pluno=a.pluno " +
                    " where a.eid='" + eId + "' and a.organizationno='" + corp + "'" +
                    " and a.year='" + year + "' and a.month='" + period + "'" +
                    " and a.btype in ('1','2','3','6') and nvl(a.billprice,0)<=0 " +
                    "  and nvl(b.isgift, '0') = '0' ";

            List<Map<String, Object>> listIn = this.doQueryData(settleSql, null);

            settleSql = "select a.billno,a.pluno,d.plu_name as pluname,a.featureno,e.featurename,f.spec,nvl(a.billprice,0) billprice  " +
                    " from DCP_SETTLEDATA a " +
                    " inner join DCP_SSTOCKOUT_DETAIL c on a.eid=c.eid and a.billno=c.sstockoutno and a.item=c.item " +
                    " left join dcp_goods_lang d on d.eid=a.eid and d.pluno=a.pluno and d.lang_type='" + req.getLangType() + "'" +
                    " left join dcp_goods_feature_lang e on e.eid=a.eid and e.featureno=a.featureno and e.pluno=a.pluno and e.lang_type='" + req.getLangType() + "' " +
                    " left join dcp_goods f on f.eid=a.eid and f.pluno=a.pluno " +
                    " where a.eid='" + eId + "' and a.organizationno='" + corp + "'" +
                    " and a.year='" + year + "' and a.month='" + period + "'" +
                    " and a.btype in ('1','2','3','6') and nvl(a.billprice,0)<=0 " +
                    "  and nvl(c.isgift, '0') = '0' ";
            List<Map<String, Object>> listOut = this.doQueryData(settleSql, null);

            String inSettleSql = "select a.billno,a.pluno,d.plu_name as pluname,a.featureno,e.featurename,f.spec,nvl(a.billprice,0) billprice   " +
                    " from DCP_INTERSETTLEMENT a " +
                    " left join dcp_goods_lang d on d.eid=a.eid and d.pluno=a.pluno and d.lang_type='" + req.getLangType() + "'" +
                    " left join dcp_goods_feature_lang e on e.eid=a.eid and e.featureno=a.featureno and e.pluno=a.pluno and e.lang_type='" + req.getLangType() + "' " +
                    " left join dcp_goods f on f.eid=a.eid and f.pluno=a.pluno " +
                    " where a.eid='" + eId + "' and a.organizationno='" + corp + "'" +
                    " and a.year='" + year + "' and a.month='" + period + "'" +
                    "  and nvl(a.billprice,0)<=0 " +
                    "  ";
            List<Map<String, Object>> list2 = this.doQueryData(inSettleSql, null);
            List<Map<String, Object>> filterList1 = listIn.stream().filter(x -> new BigDecimal(x.get("BILLPRICE").toString()).compareTo(BigDecimal.ZERO) == 0).collect(Collectors.toList());
            List<Map<String, Object>> filterList3 = listOut.stream().filter(x -> new BigDecimal(x.get("BILLPRICE").toString()).compareTo(BigDecimal.ZERO) == 0).collect(Collectors.toList());
            List<Map<String, Object>> filterList2 = list2.stream().filter(x -> new BigDecimal(x.get("BILLPRICE").toString()).compareTo(BigDecimal.ZERO) == 0).collect(Collectors.toList());
            insertMessage(req, res, errorMessage, filterList1, "料件本期入库金额为0");
            insertMessage(req, res, errorMessage, filterList2, "料件本期入库金额为0");
            insertMessage(req, res, errorMessage, filterList3, "料件本期入库金额为0");

            //06.料件本期入库金额为负数
            filterList1 = listIn.stream().filter(x -> new BigDecimal(x.get("BILLPRICE").toString()).compareTo(BigDecimal.ZERO) < 0).collect(Collectors.toList());
            filterList2 = listOut.stream().filter(x -> new BigDecimal(x.get("BILLPRICE").toString()).compareTo(BigDecimal.ZERO) < 0).collect(Collectors.toList());
            filterList3 = list2.stream().filter(x -> new BigDecimal(x.get("BILLPRICE").toString()).compareTo(BigDecimal.ZERO) < 0).collect(Collectors.toList());
            insertMessage(req, res, errorMessage, filterList1, "料件本期入库金额为负数");
            insertMessage(req, res, errorMessage, filterList2, "料件本期入库金额为负数");
            insertMessage(req, res, errorMessage, filterList3, "料件本期入库金额为负数");

            //07.料件在杂项单内单价为 0
            String misSql = "select a.PRODNO as PLUNO,a.PRODNAME as pluname,a.FEATURENO,'' as SPEC,'' AS BILLNO " +
                    " from DCP_MISCINOUT a " +
                    " where a.eid='" + eId + "' and a.ACCOUNTID='" + accountID + "' " +
                    " and a.corp='" + corp + "' and a.year='" + year + "' " +
                    " and a.PERIOD='" + period + "' and a.MATERIAL=0 ";
            List<Map<String, Object>> list3 = this.doQueryData(misSql, null);
            insertMessage(req, res, errorMessage, list3, "料件在杂项单内单价为0");

        }


        if ("2".equals(bType) || Check.Null(bType)) {
            //01.此料号未参与成本阶计算
            String sql1 = "select a.*,b.plu_name as pluname,c.featurename from DCP_STOCK_DETAIL a " +
                    " left join dcp_goods_lang  b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='" + req.getLangType() + "'" +
                    " left join DCP_GOODS_FEATURE_LANG c on c.eid=a.eid and c.pluno=a.pluno and c.featureno=a.featureno and c.lang_type='" + req.getLangType() + "'" +
                    " where a.eid='" + eId + "' " +
                    " and a.organizationno='" + corp + "' " +
                    " and to_char(a.bdate,'yyyyMMdd')>='" + dateSmall + "' " +
                    " and to_char(a.bdate,'yyyyMMdd')<='" + dateLarge + "' ";
            List<Map<String, Object>> list1 = this.doQueryData(sql1, null);

            String sql2 = "select a.*,b.plu_name as pluname,c.featurename from DCP_COSTLEVELDETAIL a" +
                    " left join dcp_goods_lang  b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='" + req.getLangType() + "'" +
                    " left join DCP_GOODS_FEATURE_LANG c on c.eid=a.eid and c.pluno=a.pluno and c.featureno=a.featureno and c.lang_type='" + req.getLangType() + "'" +
                    " where a.eid='" + eId + "' and a.ACCOUNTID='" + accountID + "' " +
                    " and a.CORP='" + corp + "' and a.year='" + year + "' " +
                    " and a.period='" + period + "' ";
            List<Map<String, Object>> list2 = this.doQueryData(sql2, null);
            List<String> costPluNos = list2.stream()
                    .map(x -> x.get("PLUNO").toString() + ";" + x.get("PLUNAME").toString() + ";" + x.get("FEATURENO").toString() + ";" + x.get("FEATURENAME").toString())
                    .distinct().collect(Collectors.toList());
            List<String> pluNos = list1.stream()
                    .map(x -> x.get("PLUNO").toString() + ";" + x.get("PLUNAME").toString() + ";" + x.get("FEATURENO").toString() + ";" + x.get("FEATURENAME").toString())
                    .distinct().collect(Collectors.toList());
            pluNos.removeAll(costPluNos);
             /*  去除 此料号未参与成本阶计算
            if (!pluNos.isEmpty()) {
                for (String pluNo : pluNos) {
                    if (!pluNo.isEmpty()) {
                        String[] pluNoArr = pluNo.split(";");
                        DCP_PreCostCalChkProcessRes.CkList singleCk = res.new CkList();
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

 */

            //04.料号未设置商品分类
            String sql3 = "select a.pluno,b.plu_name as pluname from dcp_goods a" +
                    " left join dcp_goods_lang  b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='" + req.getLangType() + "'" +
                    " where a.eid='" + eId + "' " +
                    " and to_char(a.CREATETIME,'yyyyMMdd')>='" + dateSmall + "'" +
                    " and to_char(a.createtime,'yyyyMMdd')<='" + dateLarge + "' " +
                    " and ( a.category='' or a.category is null) ";
            List<Map<String, Object>> list3 = this.doQueryData(sql3, null);

            for (Map<String, Object> singleList3 : list3) {
                DCP_PreCostCalChkProcessRes.CkList singleCk = res.new CkList();
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


        if ("3".equals(bType) || "4".equals(bType) || Check.Null(bType)) {
            //02.工单已入库,但未有报工资料
            String taskSql = "select a.BATCHTASKNO as billno,a.BATCHTASKNO,a.PLUNO,c.plu_name as pluname ,'' as featureno " +
                    " from MES_BATCHTASK a " +
                    " left join MES_PROCESS_REPORT b on a.BATCHTASKNO=b.BATCHTASKNO and a.eid=b.eid and a.organizationno=b.organizationno " +
                    " left join dcp_goods_lang c on c.eid=a.eid and c.pluno=a.pluno and c.lang_type='" + req.getLangType() + "' " +
                    " where a.eid='" + eId + "' and a.organizationno='" + corp + "' " +
                    " and a.PRODUCTSTATUS in ('Y','Z') and a.BEGINDATE>='" + dateSmall + "'" +
                    " and a.BEGINDATE<='" + dateLarge + "' and nvl(b.reportno,'')='' ";
            List<Map<String, Object>> list1 = this.doQueryData(taskSql, null);
            insertMessage(req, res, errorMessage, list1, "工单已入库,但未有报工资料");
            //03.截止当前期别工单已完工入库数量*，料未发足，请检查！（一般工单） --暂不处理
            //08.成本中心为空
            String taskSql2 = "select a.BATCHTASKNO as billno,a.BATCHTASKNO,a.PLUNO,c.plu_name as pluname ,'' as featureno " +
                    " from MES_BATCHTASK a " +
                    " left join dcp_goods_lang c on c.eid=a.eid and c.pluno=a.pluno and c.lang_type='" + req.getLangType() + "' " +
                    " where a.eid='" + eId + "' and a.organizationno='" + corp + "' " +
                    " and a.PRODUCTSTATUS in ('Y','Z') and a.BEGINDATE>='" + dateSmall + "'" +
                    " and a.BEGINDATE<='" + dateLarge + "' and nvl(a.RESPCENTER,'')='' ";
            List<Map<String, Object>> list2 = this.doQueryData(taskSql2, null);
            insertMessage(req, res, errorMessage, list2, "成本中心为空");

        }

        int pageSize = req.getPageSize();
        int startRow = ((req.getPageNumber() - 1) * req.getPageSize());
        startRow = ((req.getPageNumber() - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
        startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料

        List<DCP_PreCostCalChkProcessRes.CkList> returnMessage = errorMessage.stream()
                .skip(startRow)
                .limit(pageSize)
                .collect(Collectors.toList());

        res.setPageNumber(req.getPageNumber());
        res.setPageSize(req.getPageSize());
        res.setTotalRecords(errorMessage.size());
        int totalPages = 0;
        if (errorMessage.size() > 0) {
            totalPages = errorMessage.size() / req.getPageSize();
            totalPages = (errorMessage.size() % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
        }
        res.setTotalPages(totalPages);
        res.setCkList(returnMessage);
        return res;
    }

    private void insertMessage(DCP_PreCostCalChkProcessReq req, DCP_PreCostCalChkProcessRes res, List<DCP_PreCostCalChkProcessRes.CkList> errorMessageList, List<Map<String, Object>> list1, String errorMessage) {
        String bType = req.getRequest().getBType();
        String corp = req.getRequest().getCorp();
        String corp_name = req.getRequest().getCorp_Name();
        String accountID = req.getRequest().getAccountID();
        String account = req.getRequest().getAccount();
        String year = req.getRequest().getYear();
        String period = req.getRequest().getPeriod();
        for (Map<String, Object> map : list1) {
            DCP_PreCostCalChkProcessRes.CkList singleCk = res.new CkList();
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


    @Override
    protected void processRow(Map<String, Object> row) throws Exception {
        // TODO 自动生成的方法存根

    }

    @Override
    protected String getQuerySql(DCP_PreCostCalChkProcessReq req) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();

        return sqlbuf.toString();
    }

}

