package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_BatchToMStockOutReq;
import com.dsc.spos.json.cust.req.DCP_MStockOutProcessReq;
import com.dsc.spos.json.cust.res.DCP_BatchToMStockOutRes;
import com.dsc.spos.json.cust.res.DCP_MStockOutProcessRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_BatchToMStockOut  extends SPosAdvanceService<DCP_BatchToMStockOutReq, DCP_BatchToMStockOutRes> {

    @Override
    protected void processDUID(DCP_BatchToMStockOutReq req, DCP_BatchToMStockOutRes res) throws Exception {

        String eId = req.geteId();
        String organizationNO = req.getOrganizationNO();
        String batchNo = req.getRequest().getBatchNo();
        String corp = req.getCorp();
        String orgSql="select * from dcp_org where eid='"+eId+"' and organizationno='"+organizationNO+"'";
        List<Map<String, Object>> orgList = this.doQueryData(orgSql, null);
        String costDomain = orgList.get(0).get("COST_DOMAIN").toString();
        String costDomainId="";
        if("Y".equals(costDomain)){
            costDomainId=organizationNO;
        }else{
            costDomainId=corp;
        }

        String accSql="select a.*,to_char(a.APCLOSINGDATE,'yyyyMMdd') as apclosingdates  from DCP_ACOUNT_SETTING a where a.eid='"+eId+"' and a.corp='"+corp+"'  and a.ACCTTYPE='1' ";
        List<Map<String, Object>> accList = this.doQueryData(accSql, null);
        if(accList.size()<=0){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "找不到对应的账套");
        }
        String accountId = accList.get(0).get("ACCOUNTID").toString();

        String createTime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        String createDate = new SimpleDateFormat("yyyyMMdd").format(new Date());



        String sql1="select * from MES_BATCHING a where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' and a.batchno='"+batchNo+"' and ( a.BUCKLESTATUS IS NULL  OR a.BUCKLESTATUS!='Y')  ";
        List<Map<String, Object>> list = this.doQueryData(sql1, null);

        String detailSql="select b.TOWAREHOUSE,b.pluno,b.punit,b.SHAREPQTY,b.baseunit,b.BASEQTY,b.BATCH,b.FEATURENO,b.location," +
                " b.item,a.OOTYPE,a.oofno,a.ooitem,a.pitem,a.sitem,a.zitem,a.PROCESSNO,c.unitratio,nvl(to_char(d.PRODUCTDATE,'yyyyMMdd'),'') as PRODUCTDATE,nvl(to_char(d.LOSEDATE,'yyyyMMdd'),'') as  LOSEDATE " +
                " from  MES_BATCHING_DETAIL a" +
                " left join MES_BATCHING_DETAIL_MO b on a.eid=b.eid and a.organizationno=b.organizationno and a.batchno=b.batchno and a.item=b.oitem " +
                " left join dcp_goods_unit c on c.eid=a.eid and c.pluno=b.pluno and c.unit=b.baseunit and c.ounit=b.punit" +
                " left join mes_batch d on d.eid=a.eid and d.pluno=b.pluno and d.featureno=b.featureno and d.batchno=b.batch " +
                " where a.eid='"+eId+"' and a.organizationno='"+organizationNO+"' and a.batchno='"+batchNo+"' ";
        List<Map<String, Object>> detailList = this.doQueryData(detailSql, null);

        if(list.size()<=0||detailList.size()<=0){
            res.setSuccess(false);
            res.setServiceDescription("找不到对应的单据！");
            return;
        }
        Map<String, Object> batching = list.get(0);

        BigDecimal totCqty = BigDecimal.ZERO;
        BigDecimal totPqty= BigDecimal.ZERO;
        BigDecimal totAmt= BigDecimal.ZERO;
        BigDecimal totDistriAmt= BigDecimal.ZERO;

        totCqty=new BigDecimal(detailList.stream().map(x->{
            Map map = new HashMap<>();
            map.put("PLUNO",x.get("PLUNO").toString());
            map.put("FEATURENO",x.get("FEATURENO").toString());
            return map;
        }).distinct().collect(Collectors.toList()).size());

        String mStockoutNo = this.getOrderNO(req, "SCKL");

        //获取价格
        List<Map<String, Object>> plus = new ArrayList<>();
        Map<String, Boolean> condition = new HashMap<>(); //查詢條件
        condition.put("PLUNO", true);
        List<Map<String, Object>> getQPlu=MapDistinct.getMap(detailList, condition);
        for (Map<String, Object> onePlu :getQPlu ) {
            Map<String, Object> plu = new HashMap<>();
            plu.put("PLUNO", onePlu.get("PLUNO").toString());
            plu.put("PUNIT", onePlu.get("PUNIT").toString());
            plu.put("BASEUNIT", onePlu.get("BASEUNIT").toString());
            plu.put("UNITRATIO", onePlu.get("UNITRATIO").toString());
            plus.add(plu);
        }

        MyCommon mc = new MyCommon();
        List<Map<String, Object>> getPluPrice = mc.getSalePrice_distriPrice(StaticInfo.dao,eId, req.getBELFIRM(), organizationNO,plus,"");


        int item=0;
        for (Map<String, Object> detail : detailList){
            item++;

            BigDecimal sharePqty = new BigDecimal(detail.get("SHAREPQTY").toString());

            totPqty=totPqty.add(sharePqty);

            String price="0";
            Map<String, Object> condiV= new HashMap<>();
            condiV.put("PLUNO",detail.get("PLUNO").toString());
            condiV.put("PUNIT",detail.get("PUNIT").toString());
            List<Map<String, Object>> priceList= MapDistinct.getWhereMap(getPluPrice, condiV, false);
            if(priceList!=null && priceList.size()>0 ) {
                price = priceList.get(0).get("PRICE").toString();
            }

            BigDecimal amt = new BigDecimal(price).multiply(sharePqty);
            totAmt=totAmt.add(amt);
            String featureNo = detail.get("FEATURENO").toString();
            if(Check.Null(featureNo)){
                featureNo=" ";
            }

            String costPriceSql="SELECT ACCOUNTID,COSTDOMAINID,PLUNO,FEATURENO,BATCH_NO,YEAR,PERIOD,CURAVGPRICE " +
                    "FROM (SELECT ACCOUNTID,COSTDOMAINID,PLUNO,FEATURENO,BATCH_NO,YEAR," +
                    "PERIOD,CURAVGPRICE,ROW_NUMBER() OVER (PARTITION BY ACCOUNTID, COSTDOMAINID, PLUNO, FEATURENO " +
                    "" +
                    "ORDER BY YEAR DESC, PERIOD DESC ) AS rn FROM DCP_CURINVCOSTSTAT where eid='"+eId+"' and accountid='"+accountId+"' and COSTDOMAINID='"+costDomainId+"' " +
                    " and pluno='"+detail.get("PLUNO").toString()+"' and featureno='"+featureNo+"'  ) WHERE rn = 1 ";
            List<Map<String, Object>> costPriceList = this.doQueryData(costPriceSql, null);
            BigDecimal distriPrice=new BigDecimal("0");
            if(costPriceList!=null && costPriceList.size()>0){
                distriPrice=new BigDecimal(costPriceList.get(0).get("CURAVGPRICE").toString());
            }

            BigDecimal distriAmt = distriPrice.multiply(sharePqty);
            totDistriAmt=totDistriAmt.add(distriAmt);



            ColumnDataValue detailColumns=new ColumnDataValue();
            detailColumns.add("EID", DataValues.newString(eId));
            detailColumns.add("SHOPID", DataValues.newString(req.getShopId()));
            detailColumns.add("MSTOCKOUTNO", DataValues.newString(mStockoutNo));
            detailColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
            detailColumns.add("WAREHOUSE", DataValues.newString(detail.get("TOWAREHOUSE").toString()));
            detailColumns.add("ITEM", DataValues.newString(item));
            detailColumns.add("PLUNO", DataValues.newString(detail.get("PLUNO").toString()));
            detailColumns.add("PUNIT", DataValues.newString(detail.get("PUNIT").toString()));
            detailColumns.add("PQTY", DataValues.newString(sharePqty.toString()));
            detailColumns.add("BASEUNIT", DataValues.newString(detail.get("BASEUNIT").toString()));
            detailColumns.add("BASEQTY", DataValues.newString(detail.get("BASEQTY").toString()));
            detailColumns.add("UNIT_RATIO", DataValues.newString(detail.get("UNITRATIO").toString()));
            detailColumns.add("PRICE", DataValues.newString(price));
            detailColumns.add("AMT", DataValues.newString(amt));
            detailColumns.add("DISTRIPRICE", DataValues.newString(distriPrice));
            detailColumns.add("DISTRIAMT", DataValues.newString(distriAmt));
            detailColumns.add("BATCHNO", DataValues.newString(detail.get("BATCH").toString()));
            detailColumns.add("PRODDATE", DataValues.newString(detail.get("PRODUCTDATE").toString()));
            detailColumns.add("EXPDATE", DataValues.newString(detail.get("LOSEDATE").toString()));
            detailColumns.add("ISBUCKLE", DataValues.newString("Y"));
            detailColumns.add("FEATURENO", DataValues.newString(featureNo));
            detailColumns.add("LOCATION", DataValues.newString(detail.get("LOCATION").toString()));
            detailColumns.add("OTYPE", DataValues.newString("2"));
            detailColumns.add("OFNO", DataValues.newString(batchNo));
            detailColumns.add("OITEM", DataValues.newString(detail.get("ITEM").toString()));
            detailColumns.add("OOTYPE", DataValues.newString(detail.get("OOTYPE").toString()));
            detailColumns.add("OOFNO", DataValues.newString(detail.get("OOFNO").toString()));
            detailColumns.add("OOITEM", DataValues.newString(detail.get("OOITEM").toString()));
            detailColumns.add("LOAD_DOCITEM", DataValues.newString(""));
            detailColumns.add("PITEM", DataValues.newString(detail.get("PITEM").toString()));
            detailColumns.add("PROCESSNO", DataValues.newString(detail.get("PROCESSNO").toString()));
            detailColumns.add("SITEM", DataValues.newString(detail.get("SITEM").toString()));
            detailColumns.add("ZITEM", DataValues.newString(detail.get("ZITEM").toString()));
            detailColumns.add("OPQTY", DataValues.newString(0));

            String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
            DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
            InsBean detailib=new InsBean("DCP_MSTOCKOUT_DETAIL",detailColumnNames);
            detailib.addValues(detailDataValues);
            this.addProcessData(new DataProcessBean(detailib));

        }

        String accountDate = batching.get("ACCOUNTDATE").toString();
        if(Check.Null(accountDate)){
            accountDate=createDate;
        }

        ColumnDataValue mainColumns=new ColumnDataValue();
        mainColumns.add("EID", DataValues.newString(eId));
        mainColumns.add("SHOPID", DataValues.newString(req.getShopId()));
        mainColumns.add("MSTOCKOUTNO", DataValues.newString(mStockoutNo));
        mainColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
        //mainColumns.add("WAREHOUSE", DataValues.newString(""));
        mainColumns.add("DOC_TYPE", DataValues.newString(batching.get("DOC_TYPE").toString()));
        mainColumns.add("ACCOUNT_DATE", DataValues.newString(accountDate));

        mainColumns.add("BDATE", DataValues.newString(createDate));
        mainColumns.add("SDATE", DataValues.newString(createDate));
        mainColumns.add("OTYPE", DataValues.newString("2"));
        mainColumns.add("OFNO", DataValues.newString(batchNo));
        mainColumns.add("OOTYPE", DataValues.newString(batching.get("OOTYPE").toString()));
        mainColumns.add("OOFNO", DataValues.newString(""));
        mainColumns.add("MEMO", DataValues.newString(""));
        mainColumns.add("STATUS", DataValues.newInteger(0));
        mainColumns.add("ADJUSTSTATUS", DataValues.newString("0"));
        mainColumns.add("OMSTOCKOUTNO", DataValues.newString(""));
        mainColumns.add("TOT_CQTY", DataValues.newDecimal(totCqty));
        mainColumns.add("TOT_PQTY", DataValues.newDecimal(totPqty));
        mainColumns.add("TOT_AMT", DataValues.newDecimal(totAmt));
        mainColumns.add("TOT_DISTRIAMT", DataValues.newDecimal(totDistriAmt));

        mainColumns.add("AUTOPROCESS", DataValues.newString("Y"));
        mainColumns.add("PROCESS_STATUS", DataValues.newString("N"));

        mainColumns.add("LOAD_DOCTYPE", DataValues.newString(""));
        mainColumns.add("LOAD_DOCNO", DataValues.newString(""));
        mainColumns.add("CREATEOPID", DataValues.newString(req.getOpNO()));
        mainColumns.add("CREATETIME", DataValues.newDate(createTime));
        mainColumns.add("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

        mainColumns.add("EMPLOYEEID", DataValues.newString(batching.get("EMPLOYEEID").toString()));
        mainColumns.add("DEPARTID", DataValues.newString(batching.get("DEPARTID").toString()));

        String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
        DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
        InsBean ib=new InsBean("DCP_MSTOCKOUT",mainColumnNames);
        ib.addValues(mainDataValues);
        this.addProcessData(new DataProcessBean(ib));


        this.doExecuteDataToDB();

        //单据生成以后调用审核
        ParseJson pj = new ParseJson();
        DCP_MStockOutProcessReq mstockReq=new DCP_MStockOutProcessReq();
        mstockReq.setServiceId("DCP_MStockOutProcess");
        mstockReq.setToken(req.getToken());
        DCP_MStockOutProcessReq.LevelRequest request = mstockReq.new LevelRequest();
        request.setMStockOutNo(mStockoutNo);
        request.setAccountDate(accountDate);
        mstockReq.setRequest(request);

        String jsontemp= pj.beanToJson(mstockReq);

        //直接调用CRegisterDCP服务
        DispatchService ds = DispatchService.getInstance();
        String resXml = ds.callService(jsontemp, StaticInfo.dao);
        DCP_MStockOutProcessRes resserver=pj.jsonToBean(resXml, new TypeToken<DCP_MStockOutProcessRes>(){});
        if(resserver.isSuccess()==false)
        {
            //删除单据
            DelBean db1 = new DelBean("DCP_MSTOCKOUT");
            db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db1.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
            db1.addCondition("MSTOCKOUTNO", new DataValue(mStockoutNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db1));

            DelBean db2 = new DelBean("DCP_MSTOCKOUT_DETAIL");
            db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db2.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
            db2.addCondition("MSTOCKOUTNO", new DataValue(mStockoutNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db2));

            this.doExecuteDataToDB();

            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "扣料单审核失败！");
        }
        else{
            //更新配料单数据  BUCKLESTATUS="Y"
            UptBean ub1 = new UptBean("MES_BATCHING");
            ub1.addUpdateValue("BUCKLESTATUS", new DataValue("Y", Types.VARCHAR));
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("ORGANIZATIONNO", new DataValue(req.getOrganizationNO(), Types.VARCHAR));
            ub1.addCondition("BATCHNO", new DataValue(batchNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));
            this.doExecuteDataToDB();
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_BatchToMStockOutReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_BatchToMStockOutReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_BatchToMStockOutReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_BatchToMStockOutReq req) throws Exception {
        boolean isFail = false;
        StringBuilder errMsg = new StringBuilder("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }


        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_BatchToMStockOutReq> getRequestType() {
        return new TypeToken<DCP_BatchToMStockOutReq>() {
        };
    }

    @Override
    protected DCP_BatchToMStockOutRes getResponseType() {
        return new DCP_BatchToMStockOutRes();
    }
}

