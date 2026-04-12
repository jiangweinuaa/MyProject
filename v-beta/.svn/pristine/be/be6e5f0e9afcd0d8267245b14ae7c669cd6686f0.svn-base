package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.ProcedureBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_AdjustCreateReq;
import com.dsc.spos.json.cust.res.DCP_AdjustCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.webhook.WebHookService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.bc.BcReq;
import com.dsc.spos.utils.bc.BcRes;
import com.google.gson.reflect.TypeToken;

public class DCP_AdjustCreate extends SPosAdvanceService<DCP_AdjustCreateReq, DCP_AdjustCreateRes> {
    @Override
    protected void processDUID(DCP_AdjustCreateReq req, DCP_AdjustCreateRes res) throws Exception {
        
        try {
            String eId = req.geteId();
            String shopId = req.getShopId();
            String loadDocNO = req.getloadDocNO();
            String organizationno = shopId;
            String adjustno = "";//库存调整单单号
            String ofno = req.getofno();
            String differenceStatus = req.getDifferenceStatus();
            String adjust_warehouse="";
            
            String sysDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String sysTime = new SimpleDateFormat("HHmmss").format(new Date());
            String accountDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
            
            MyCommon mc = new MyCommon();
            String sJoinPluNo = "";
            String sJoinPunit = "";
            String sJoinBaseUnit = "";
            boolean isNeedStockSync = false;//是否需要同步到第三方
            
            //JSON的开源转换会将数值型转为浮点型，这TMD有问题，(1变成1.0;)
            //我先在这处理一下,因为这个字符的长度为1
            String doctype = req.getdocType();
            String oType = req.getotype();    // 0.差异申诉  1.配送收货
            if (Check.Null(oType)){
                oType = "0";
            }
            
            
            //来源单号判断，不能重复
            String sql = " select ADJUSTNO,OFNO from DCP_ADJUST "
                    + " WHERE eId='"+eId+"' "
                    + " AND SHOPID='"+shopId+"' "
                    + " AND LOAD_DOCNO='"+loadDocNO+"' AND DOC_TYPE='"+doctype+"'";
            List<Map<String, Object>> getQData = this.doQueryData(sql,null);
            if (getQData == null || getQData.isEmpty()){
                //doctype: 1-期初调整  3-配送收货差异调整
                //3.配送收货差异处理结果(拒绝)  4.配送收货差异处理(接受)
                if (doctype.equals("1") || (oType.equals("0") && differenceStatus.equals("4")) || oType.equals("1")) {
                    adjustno = this.getAdjustNo(req,accountDate);
                    //DCP_ADJUST
                    String[] columnsAdjust = {
                            "SHOPID","ADJUSTNO","BDATE","MEMO","DOC_TYPE","OTYPE","OFNO","STATUS",
                            "CREATEBY","CREATE_DATE","CREATE_TIME","MODIFYBY","MODIFY_DATE","MODIFY_TIME",
                            "CONFIRMBY","CONFIRM_DATE","CONFIRM_TIME","ACCOUNTBY","ACCOUNT_DATE","ACCOUNT_TIME",
                            "CANCELBY","CANCEL_DATE","CANCEL_TIME","SUBMITBY","SUBMIT_DATE","SUBMIT_TIME",
                            "TOT_PQTY","TOT_AMT","TOT_CQTY","LOAD_DOCTYPE","LOAD_DOCNO",
                            "EID","ORGANIZATIONNO","WAREHOUSE","TOT_DISTRIAMT","UPDATE_TIME","TRAN_TIME"
                    };
                    DataValue[] insValue1;
                    
                    BigDecimal TOT_PQTY=new BigDecimal("0");
                    BigDecimal TOT_AMT=new BigDecimal("0");
                    BigDecimal totDistriAmt=new BigDecimal("0");
                    int TOT_CQTY=0;
                    
                    String bdate = req.getbDate();
                    
                    // IF 参数是否启用批号==N ，库存流水的批号和日期字段不给值 BY JZMA 20191024
                    String isBatchPara = PosPub.getPARA_SMS(dao, eId, "", "Is_BatchNO");
                    if (Check.Null(isBatchPara) || !isBatchPara.equals("Y")){
                        isBatchPara="N";
                    }
                    
                    //单身明细信息
                    List<Map<String, String>> jsonDatas = req.getDatas();
                    if(jsonDatas != null && !jsonDatas.isEmpty()) {
                        for (Map<String, String> par : jsonDatas) {
                            int insColCt = 0;
                            String[] columnsAdjustDetail ={
                                    "SHOPID","ADJUSTNO","ITEM","OITEM","PLUNO","PLU_BARCODE","PUNIT","PQTY",
                                    "BASEUNIT","BASEQTY","UNIT_RATIO","PRICE","AMT","EID","ORGANIZATIONNO","WAREHOUSE",
                                    "BATCH_NO","PROD_DATE","DISTRIPRICE","DISTRIAMT","BDATE","FEATURENO"
                            };
                            
                            DataValue[] columnsVal = new DataValue[columnsAdjustDetail.length];
                            for (int i = 0; i < columnsVal.length; i++) {
                                String keyVal = null;
                                switch (i) {
                                    case 0:
                                        keyVal=shopId;
                                        break;
                                    case 1:
                                        keyVal=adjustno;
                                        TOT_CQTY+=1;
                                        break;
                                    case 2:
                                        keyVal=par.get("item");
                                        break;
                                    case 3:
                                        keyVal=par.get("oitem");
                                        break;
                                    case 4:
                                        keyVal=par.get("pluNO");
                                        sJoinPluNo += keyVal+",";
                                        break;
                                    case 5:
                                        keyVal="";
                                        break;
                                    case 6:
                                        keyVal=par.get("punit");
                                        sJoinPunit += keyVal+",";
                                        break;
                                    case 7:
                                        keyVal=par.get("pqty");
                                        TOT_PQTY = TOT_PQTY.add(new BigDecimal(keyVal));
                                        break;
                                    case 8:
                                        keyVal=par.get("baseUnit");
                                        sJoinBaseUnit += keyVal+",";
                                        break;
                                    case 9:
                                        keyVal=par.get("baseQty");
                                        break;
                                    case 10:
                                        keyVal=par.get("unitRatio");
                                        break;
                                    case 11:
                                        keyVal=par.get("price");
                                        if(Check.Null(keyVal))
                                            keyVal = "0";
                                        break;
                                    case 12:
                                        keyVal=par.get("amt");
                                        if(Check.Null(keyVal))
                                            keyVal = "0";
                                        TOT_AMT = TOT_AMT.add(new BigDecimal(keyVal));
                                        break;
                                    case 13:
                                        keyVal=eId;
                                        break;
                                    case 14:
                                        keyVal=organizationno;
                                        break;
                                    case 15:
                                        keyVal=par.get("WAREHOUSE");
                                        adjust_warehouse= keyVal;//单头给仓库值
                                        break;
                                    case 16:
                                        //【ID1027165】【冠生园】 门店退货输入数量提示 服务执行异常（开启了中台批号管理） by jinzma 20220706
                                        if (isBatchPara.equals("N")){
                                            //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "ERP下传了商品批号但门店未启用批号管理");
                                            keyVal="";
                                        }else{
                                            keyVal=par.getOrDefault("batchNO", "");
                                        }
                                        break;
                                    case 17:
                                        if (isBatchPara.equals("N")){
                                            keyVal="";
                                        }else{
                                            keyVal=par.getOrDefault("prodDate", "");
                                        }
                                        break;
                                    case 18:
                                        keyVal=par.getOrDefault("distriPrice", "");
                                        break;
                                    case 19:
                                        keyVal=par.getOrDefault("distriAmt", "");
                                        totDistriAmt =totDistriAmt.add(new BigDecimal(keyVal));
                                        break;
                                    case 20:
                                        keyVal = bdate;
                                        break;
                                    case 21:
                                        keyVal = par.getOrDefault("featureNO", " ");
                                        if (Check.Null(keyVal))
                                            keyVal=" ";
                                        break;
                                    default:
                                        break;
                                }
                                
                                if (keyVal != null) {
                                    insColCt++;
                                    if (i == 2 || i == 3) {
                                        columnsVal[i] = new DataValue(keyVal, Types.DECIMAL);
                                    } else if (i == 7 || i ==9 || i == 10 || i == 11 || i == 12) {
                                        columnsVal[i] = new DataValue(keyVal, Types.FLOAT);
                                    } else {
                                        columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
                                    }
                                } else {
                                    columnsVal[i] = null;
                                }
                            }
                            
                            String[] columns2 = new String[insColCt];
                            DataValue[] insValue2 = new DataValue[insColCt];
                            
                            insColCt = 0;
                            
                            for (int i = 0; i < columnsVal.length; i++) {
                                if (columnsVal[i] != null) {
                                    columns2[insColCt] = columnsAdjustDetail[i];
                                    insValue2[insColCt] = columnsVal[i];
                                    insColCt++;
                                    if (insColCt >= insValue2.length) {
                                        break;
                                    }
                                }
                            }
                            InsBean ib2 = new InsBean("DCP_ADJUST_DETAIL", columns2);
                            ib2.addValues(insValue2);
                            this.addProcessData(new DataProcessBean(ib2));
                            
                            //加入库存流水账信息
                            String stockDocType="00";
                            if (doctype.equals("3")||doctype.equals("4")) {
                                stockDocType = "09";
                            }
                            
                            String featureNo=par.get("featureNO");
                            if (Check.Null(featureNo)) {
                                featureNo = " ";
                            }
                            
                            //批号管控 by jinzma 20220614  上面代码已经有拦截，此处可以不判断
                            String batchNo = par.getOrDefault("batchNO", "");
                            String prodDate = par.getOrDefault("prodDate", "");
                            if (isBatchPara.equals("N")){
                                batchNo = "";
                                prodDate = "";
                            }

                            BcReq bcReq=new BcReq();
                            bcReq.setServiceType("StockAdjProcess");
                            BcRes bcRes = PosPub.getBTypeAndCostCode(bcReq);
                            String bType = bcRes.getBType();
                            String costCode = bcRes.getCostCode();

                            if(Check.Null(bType)||Check.Null(costCode)){
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "请先设置业务类型和成本码！");
                            }
                            
                            String procedure="SP_DCP_STOCKCHANGE_VX";
                            Map<Integer,Object> inputParameter = new HashMap<>();
                            inputParameter.put(1,eId);//--企业ID
                            inputParameter.put(2,null);
                            inputParameter.put(3,organizationno);                    //--组织
                            inputParameter.put(4, bType);
                            inputParameter.put(5, costCode);
                            inputParameter.put(6,stockDocType);                      //--单据类型
                            inputParameter.put(7,adjustno);	                         //--单据号
                            inputParameter.put(8,par.get("item"));                   //--单据行号
                            inputParameter.put(9,"0");
                            inputParameter.put(10,"1");                               //--异动方向 1=加库存 -1=减库存
                            inputParameter.put(11,bdate);                             //--营业日期 yyyy-MM-dd
                            inputParameter.put(12,par.get("pluNO"));                  //--品号
                            inputParameter.put(13,featureNo);                         //--特征码
                            inputParameter.put(14,adjust_warehouse);                 //--仓库
                            inputParameter.put(15,batchNo);                          //--批号
                            inputParameter.put(16," ");
                            inputParameter.put(17,par.get("punit"));                 //--交易单位
                            inputParameter.put(18,par.get("pqty"));                  //--交易数量
                            inputParameter.put(19,par.get("baseUnit"));              //--基准单位
                            inputParameter.put(20,par.get("baseQty"));               //--基准数量
                            inputParameter.put(21,par.get("unitRatio"));             //--换算比例
                            inputParameter.put(22,par.get("price"));                 //--零售价
                            inputParameter.put(23,par.get("amt"));                   //--零售金额
                            inputParameter.put(24,par.get("distriPrice"));           //--进货价
                            inputParameter.put(25,par.get("distriAmt"));             //--进货金额
                            inputParameter.put(26,accountDate);                      //--入账日期 yyyy-MM-dd
                            inputParameter.put(27,prodDate);                         //--批号的生产日期 yyyy-MM-dd
                            inputParameter.put(28,par.get(bdate));                   //--单据日期
                            inputParameter.put(29,"");                               //--异动原因
                            inputParameter.put(30,"");                               //--异动描述
                            inputParameter.put(31,req.getcreateBy());                //--操作员
                            
                            ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                            this.addProcessData(new DataProcessBean(pdb));
                            isNeedStockSync = true;
                            
                        }
                    }
                    
                    ///【ID1023564】【北京法美味3.0】差异申诉确认返回报错： error executing work
                    // 单身商品有效性检核  by jzma 20220629
                    Map<String,String> map = new HashMap<>();
                    map.put("PLUNO", sJoinPluNo);
                    map.put("PUNIT", sJoinPunit);
                    map.put("BASEUNIT", sJoinBaseUnit);
                    String withPluNo = mc.getFormatSourceMultiColWith(map);
                    sql = " with plu as ("+withPluNo+")"
                            + " select plu.pluno,a.baseunit,b.ounit from plu"
                            + " left join dcp_goods a on a.eid='"+eId+"' and plu.pluno=a.pluno and plu.baseunit=a.baseunit"
                            + " left join dcp_goods_unit b on b.eid='"+eId+"' and plu.pluno=b.pluno and plu.punit=b.ounit"
                            + " where a.pluno is null or b.pluno is null ";
                    List<Map<String, Object>> CheckPlu = this.doQueryData(sql,null);
                    if (CheckPlu!=null && !CheckPlu.isEmpty()){
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"商品资料检核失败,商品编号:"+CheckPlu.get(0).get("PLUNO")+"的base_unit或packing_unit给值错误");
                    }
                    
                    //单身为0，不要插入单头记录
                    if(jsonDatas != null && !jsonDatas.isEmpty()){
                        insValue1 = new DataValue[] {
                                new DataValue(shopId, Types.VARCHAR),
                                new DataValue(adjustno, Types.VARCHAR),
                                new DataValue(bdate, Types.VARCHAR),
                                new DataValue(req.getmemo(), Types.VARCHAR),
                                new DataValue(doctype, Types.VARCHAR),
                                new DataValue(oType, Types.VARCHAR),
                                new DataValue(ofno, Types.VARCHAR),
                                new DataValue("2", Types.VARCHAR),//STATUS
                                new DataValue(req.getcreateBy(), Types.VARCHAR),  //CREATEBY
                                new DataValue(sysDate, Types.VARCHAR),
                                new DataValue(sysTime, Types.VARCHAR),
                                new DataValue("", Types.VARCHAR),                 //MODIFYBY
                                new DataValue("", Types.VARCHAR),
                                new DataValue("", Types.VARCHAR),
                                new DataValue(req.getconfirmBy(), Types.VARCHAR), //CONFIRMBY
                                new DataValue(sysDate, Types.VARCHAR),
                                new DataValue(sysTime, Types.VARCHAR),
                                new DataValue(req.getaccountBy(), Types.VARCHAR), //ACCOUNTBY
                                new DataValue(accountDate, Types.VARCHAR),
                                new DataValue(sysTime, Types.VARCHAR),
                                new DataValue("", Types.VARCHAR),                 //CANCELBY
                                new DataValue("", Types.VARCHAR),
                                new DataValue("", Types.VARCHAR),
                                new DataValue(req.getaccountBy(), Types.VARCHAR), //SUBMITBY
                                new DataValue(sysDate, Types.VARCHAR),
                                new DataValue(sysTime, Types.VARCHAR),
                                new DataValue(TOT_PQTY.toString(), Types.VARCHAR), //TOT_PQTY
                                new DataValue(TOT_AMT.toString(), Types.VARCHAR), //TOT_AMT
                                new DataValue(TOT_CQTY, Types.VARCHAR), //TOT_CQTY 明细条数
                                new DataValue(req.getloadDocType(), Types.VARCHAR),
                                new DataValue(req.getloadDocNO(), Types.VARCHAR),
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(organizationno, Types.VARCHAR), //organizationno
                                new DataValue(adjust_warehouse, Types.VARCHAR),
                                new DataValue(totDistriAmt.toString(), Types.VARCHAR),
								new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
								new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                        };
                        
                        InsBean ib1 = new InsBean("DCP_ADJUST", columnsAdjust);
                        ib1.addValues(insValue1);
                        this.addProcessData(new DataProcessBean(ib1)); // 新增單頭
                    }
                    
                }
                
                if (doctype.equals("3")) {
                    //0.差异申诉
                    if (oType.equals("0")) {
                        sql = " select * from dcp_difference where eid='" + eId + "' and shopid='" + shopId + "' and differenceno='" + ofno + "' ";
                        List<Map<String, Object>> CheckOfno = this.doQueryData(sql, null);
                        if (CheckOfno == null || CheckOfno.isEmpty()) {
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "来源单号(source_no):" + ofno + " 找不到对应的差异申诉单");
                        }
                        
                        //更新差异单DCP_difference
                        UptBean ub1 = new UptBean("DCP_DIFFERENCE");
                        //更新值
                        ub1.addUpdateValue("STATUS", new DataValue(differenceStatus, Types.VARCHAR));
                        ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                        ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                        //where 条件
                        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                        ub1.addCondition("DIFFERENCENO", new DataValue(ofno, Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(ub1));
                    }
                    //1.配送收货
                    if (oType.equals("1")) {
                        sql = " select * from dcp_stockin where eid='"+eId+"' and shopid='"+shopId+"' and stockinno='"+ofno+"' and doc_type='0' and status='2' ";
                        List<Map<String, Object>> CheckOfno = this.doQueryData(sql, null);
                        if (CheckOfno == null || CheckOfno.isEmpty()) {
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "来源单号(source_no):" + ofno + " 找不到对应的配送收货单");
                        }
                    }
                }
                
            }else{
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"ERP转入来源单号重复,load_doc_no:"+loadDocNO+"已经存在!");
            }
            
            this.doExecuteDataToDB();
            
            res.setDoc_no(adjustno);
            res.setOrg_no(shopId);
            
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

            //***********调用库存同步给三方，这是个异步，不会影响效能*****************
            try
            {
                if (!Check.Null(adjustno)&& isNeedStockSync)
                {
                    WebHookService.stockSync(eId,shopId,adjustno);
                }

            }
            catch (Exception e)
            {

            }
            
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500,e.getMessage());
        }
        
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_AdjustCreateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_AdjustCreateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_AdjustCreateReq req) throws Exception {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_AdjustCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        List<Map<String, String>> jsonDatas = req.getDatas();
        //3.配送收货差异处理结果(拒绝)  4.配送收货差异处理(接受)
        String differenceStatus = req.getDifferenceStatus();
        
        if (Check.Null(req.geteId())) {
            errMsg.append("公司编码不可为空值, ");
            isFail = true;
        }
        if (Check.Null(req.getShopId())) {
            errMsg.append("门店编号不可为空值, ");
            isFail = true;
        }
        if (Check.Null(req.getdocType())) {
            errMsg.append("单据类型不可为空值, ");
            isFail = true;
        } else {
            // 1-期初调整  3-配送收货差异调整  BY JZMA 20190120  调整接口只支持此二种类型
            if(!req.getdocType().equals("1") && !req.getdocType().equals("3")) {
                errMsg.append("库存调整单单据类型doc_type:"+ req.getdocType() +"错误,非系统定义值 ");
                isFail = true;
            }else{
                if (req.getdocType().equals("3")) {
                    if (Check.Null(differenceStatus)) {
                        errMsg.append("库存调整单差异状态difference_status未给值 ");
                        isFail = true;
                    }else{
                        if (!(differenceStatus.equals("3") || differenceStatus.equals("4"))){
                            errMsg.append("配送收货差异调整时,节点difference_status 只能是3或4 ");
                            isFail = true;
                        }
                        if (differenceStatus.equals("4") && (jsonDatas == null || jsonDatas.isEmpty()) ){
                            errMsg.append("单身明细不可为空值, ");
                            isFail = true;
                        }
                    }
                    if (Check.Null(req.getofno())){
                        errMsg.append("来源单号(source_no)不可为空值,");
                        isFail = true;
                    }
                }
            }
        }
        if (Check.Null(req.getbDate())) {
            errMsg.append("调整日期不可为空值, ");
            isFail = true;
        }
        if (Check.Null(req.getloadDocNO())) {
            errMsg.append("资料来源单号不可为空值, ");
            isFail = true;
        }
        
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        if (jsonDatas != null && !jsonDatas.isEmpty()) {
            for (Map<String, String> par : jsonDatas) {
                if (Check.Null(par.get("item"))) {
                    errMsg.append("项次不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.get("pluNO"))) {
                    errMsg.append("商品编号不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.get("punit"))) {
                    errMsg.append("包装单位不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.get("pqty"))) {
                    errMsg.append("包装数量不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.get("price"))) {
                    errMsg.append("单价不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.get("amt"))) {
                    errMsg.append("金额不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.get("WAREHOUSE"))||par.get("WAREHOUSE").equals(" ")) {
                    errMsg.append("仓库不可为空值, ");
                    isFail = true;
                }
                //新增日期格式判断 BY JZMA 20191025
                String prodDate =par.getOrDefault("prodDate", "");
                if (!Check.Null(prodDate.trim())) {
                    if (!PosPub.isNumeric(prodDate)) {
                        errMsg.append("生产日期(prod_date)格式错误, ");
                        isFail = true;
                    }
                }
                if (Check.Null(par.get("distriPrice"))) {
                    errMsg.append("进货价不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.get("distriAmt"))) {
                    errMsg.append("进货金额不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.get("baseUnit"))) {
                    errMsg.append("基准单位不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.get("baseQty"))) {
                    errMsg.append("基准数量不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.get("unitRatio"))) {
                    errMsg.append("单位转换率不可为空值, ");
                    isFail = true;
                }
                
                if (isFail) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                }
            }
        }
        
        
        return false;
    }
    
    @Override
    protected TypeToken<DCP_AdjustCreateReq> getRequestType() {
        return new TypeToken<DCP_AdjustCreateReq>(){};
    }
    
    @Override
    protected DCP_AdjustCreateRes getResponseType() {
        return new DCP_AdjustCreateRes();
    }
    
    private String getAdjustNo(DCP_AdjustCreateReq req,String accountDate) throws Exception {
		/*
		  库存调整单生成规则：
		  固定编码KCTZ+年月日+5位流水号
		  KCTZ2016120900001
		 */
        String adjustno;
        String shopId = req.getShopId();
        String eId = req.geteId();
        StringBuffer sqlbuf=new StringBuffer();
        String ajustnoHead="KCTZ" + accountDate;
        sqlbuf.append(" select MAX(ADJUSTNO) ADJUSTNO from DCP_adjust "
                + " where SHOPID='"+shopId+"' "
                + " and eId='"+eId+"' "
                + " and organizationno='"+shopId+"' "
                + " and adjustno like '"+ ajustnoHead+"%%' ");
        List<Map<String, Object>> getQData = this.doQueryData(sqlbuf.toString(), null);
        if (getQData != null && !getQData.isEmpty()) {
            adjustno = (String) getQData.get(0).get("ADJUSTNO");
            if (adjustno != null && adjustno.length() > 0) {
                long i;
                adjustno = adjustno.substring(4);
                i = Long.parseLong(adjustno) + 1;
                adjustno = i + "";
                adjustno = "KCTZ" + adjustno;
            } else {
                //当天无单，从00001开始
                adjustno = "KCTZ" + accountDate + "00001";
            }
        } else {
            //当天无单，从00001开始
            adjustno = "KCTZ" +accountDate+ "00001";
        }
        return adjustno;
    }
    
}
