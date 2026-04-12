package com.dsc.spos.scheduler.job;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * JOB：Movedb_Credit
 * 说明：2.0升级3.0 赊销资料自动导入
 * @author jinzma
 * @since  2022-11-02
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class Movedb_Credit extends InitJob{
    static boolean bRun=false;//标记此服务是否正在执行中
    public String doExe() {
        //此服务是否正在执行中
        //返回信息
        String sReturnInfo="";
        
        if(bRun){
            loger.info("\r\n********* 赊销导入Movedb_Credit正在执行中,本次调用取消 ************\r\n");
            sReturnInfo="赊销导入Movedb_Credit正在执行中 ";
            return sReturnInfo;
        }
        bRun=true;
        
        loger.info("\r\n ********* 赊销导入Movedb_Credit定时调用Start ************\r\n");
        try{
            String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String sTime = new SimpleDateFormat("HHmmss").format(new Date());
            //先查 job 执行时间，然后再执行后续操作
            String sql = "select * from job_quartz_detail where job_name='Movedb_Credit' and status='100' ";
            List<Map<String, Object>> getTimeDatas=this.doQueryData(sql, null);
            if(getTimeDatas!= null && !getTimeDatas.isEmpty()) {
                for (Map<String, Object> map : getTimeDatas) {
                    String beginTime = map.get("BEGIN_TIME").toString();
                    String endTime = map.get("END_TIME").toString();
                    //执行时间范围内判断
                    if(sTime.compareTo(beginTime)>=0 && sTime.compareTo(endTime)<0) {
                        break;
                    }else{
                        loger.info("\r\n********* 赊销导入Movedb_Credit 执行时间未到,本次调用End ************\r\n");
                        return sReturnInfo;
                    }
                }
            }
            
            //查询导入基本配置
            sql = " select a.* from dcp_movedbsetting a where a.status='100' ";
            List<Map<String, Object>> getQDataBase=this.doQueryData(sql, null);
            if (CollectionUtil.isEmpty(getQDataBase)){
                loger.info("\r\n********* 赊销导入Movedb_Credit 导入配置资料为空,本次调用End ************\r\n");
                return sReturnInfo;
            }
            
            String newEId = getQDataBase.get(0).get("NEWEID").toString();
            String oldEId = getQDataBase.get(0).get("OLDEID").toString();
            
            sql = " select customerno from dcp_customer where eid='"+newEId+"' ";
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            if (CollectionUtil.isEmpty(getQData)){
                loger.info("\r\n********* 赊销导入Movedb_Credit 3.0客户资料为空,本次调用End ************\r\n");
                return sReturnInfo;
            }
            for (Map<String, Object>oneQData : getQData){
                List<DataProcessBean> old_data = new ArrayList<>();
                List<DataProcessBean> data = new ArrayList<>();
                
                String customerNo = oneQData.get("CUSTOMERNO").toString();
                //大客户赊销明细 导入
                if (!creditDetail(old_data, data, oldEId, newEId, customerNo)) {
                    continue;
                }
                //大客户预收 导入
                if (!creditPrepay(old_data, data, oldEId, newEId, customerNo)) {
                    continue;
                }
                //大客户回款 导入  小凤建议回款单不需要传回3.0
                if (!creditReturn(old_data, data, oldEId, newEId, customerNo)) {
                    continue;
                }
                
                try {
                    //事务提交
                    StaticInfo.dao.useTransactionProcessData(data);
                    StaticInfo.dao_pos2.useTransactionProcessData(old_data);
                    //成功后删除JOB异常日志    BY JZMA 20221102  此处门店编号被替换成客户编号
                    InsertWSLOG.delete_JOBLOG(newEId, customerNo, "Movedb_Credit");
                    
                } catch (Exception e) {
                    InsertWSLOG.insert_JOBLOG(newEId,customerNo, "Movedb_Credit", "2.0升级3.0 赊销资料自动导入", e.getMessage());
                }
            }
            
        }catch (Exception e) {
            try {
                StringWriter errors = new StringWriter();
                PrintWriter pw = new PrintWriter(errors);
                e.printStackTrace(pw);
                
                pw.flush();
                pw.close();
                
                errors.flush();
                errors.close();
                
                loger.error("\r\n********* 赊销导入Movedb_Credit 定时调用异常: " + e.getMessage() + "\r\n" + errors + "******\r\n");
                
            }catch (Exception ignored){
            }
        } finally {
            bRun=false;
            loger.info("\r\n*********赊销导入Movedb_Credit 定时调用End ************\r\n");
        }
        
        return sReturnInfo;
    }
    
    //大客户赊销明细 导入
    private boolean creditDetail(List<DataProcessBean> old_data,List<DataProcessBean> data,String oldEId,String newEId,String customerNo){
        String[] columns = {
                "EID","SHOPID","MACHNO","OPNO","BDATE","CUSTOMERNO","CREDITNAME",
                "SOURCENO","SOURCETYPE","CREDITAMT","RETURNAMT","LACKAMT"
        };
        
        try{
            //查询dcp2.0大客户赊销明细
            String sql = " select * from td_customer_credit_detail "
                    + " where companyno='"+oldEId+"' and customerno='"+customerNo+"' "
                    + " and (movedbstatus='N' or movedbstatus is null) ";
            List<Map<String, Object>>old_getQData=StaticInfo.dao_pos2.executeQuerySQL(sql, null);
            if (CollectionUtil.isEmpty(old_getQData)){
                //loger.info("\r\n********* 赊销导入Movedb_Credit 客户编号："+customerNo+" 在2.0大客户赊销明细表中不存在 ************\r\n");
                return true;  //无需导入处理
            }
            
            // 插入DCP_CUSTOMER_CREDIT_DETAIL
            for (Map<String, Object> old_oneQData:old_getQData){
                //增加判断逻辑，3.0是否存在判断
                sql = " select sourceno from dcp_customer_credit_detail"
                        + " where eid='"+newEId+"' "
                        + " and shopid='"+old_oneQData.get("SHOPNO").toString()+"' "
                        + " and sourceno='"+old_oneQData.get("SOURCENO").toString()+"' ";
                List<Map<String, Object>> getQData=this.doQueryData(sql, null);
                if (!CollectionUtil.isEmpty(getQData)){
                    //回写2.0大客户赊销明细标记
                    UptBean ub = new UptBean("TD_CUSTOMER_CREDIT_DETAIL");
                    ub.addUpdateValue("MOVEDBSTATUS", new DataValue("E", Types.VARCHAR));
                    //Condition
                    ub.addCondition("COMPANYNO", new DataValue(oldEId, Types.VARCHAR));
                    ub.addCondition("SHOPNO", new DataValue(old_oneQData.get("SHOPNO").toString(), Types.VARCHAR));
                    ub.addCondition("SOURCENO", new DataValue(old_oneQData.get("SOURCENO").toString(), Types.VARCHAR));
                    
                    old_data.add(new DataProcessBean(ub));
                    continue;  //3.0资料已存在无需导入处理
                }
                
                DataValue[] insValue = new DataValue[]{
                        new DataValue(newEId, Types.VARCHAR),
                        new DataValue(old_oneQData.get("SHOPNO").toString(), Types.VARCHAR),
                        new DataValue(old_oneQData.get("MACHNO").toString(), Types.VARCHAR),
                        new DataValue(old_oneQData.get("OPNO").toString(), Types.VARCHAR),
                        new DataValue(old_oneQData.get("BDATE").toString(), Types.VARCHAR),
                        new DataValue(old_oneQData.get("CUSTOMERNO").toString(), Types.VARCHAR),
                        new DataValue(old_oneQData.get("CREDITNAME").toString(), Types.VARCHAR),
                        new DataValue(old_oneQData.get("SOURCENO").toString(), Types.VARCHAR),
                        new DataValue(old_oneQData.get("SOURCETYPE").toString(), Types.VARCHAR),
                        new DataValue(old_oneQData.get("CREDITAMT").toString(), Types.VARCHAR),
                        new DataValue(old_oneQData.get("RETURNAMT").toString(), Types.VARCHAR),
                        new DataValue(old_oneQData.get("LACKAMT").toString(), Types.VARCHAR),
                };
                InsBean ib = new InsBean("DCP_CUSTOMER_CREDIT_DETAIL", columns);
                ib.addValues(insValue);
                data.add(new DataProcessBean(ib));
                
                //回写2.0大客户赊销明细标记
                UptBean ub = new UptBean("TD_CUSTOMER_CREDIT_DETAIL");
                ub.addUpdateValue("MOVEDBSTATUS", new DataValue("Y", Types.VARCHAR));
                //Condition
                ub.addCondition("COMPANYNO", new DataValue(oldEId, Types.VARCHAR));
                ub.addCondition("SHOPNO", new DataValue(old_oneQData.get("SHOPNO").toString(), Types.VARCHAR));
                ub.addCondition("SOURCENO", new DataValue(old_oneQData.get("SOURCENO").toString(), Types.VARCHAR));
                
                old_data.add(new DataProcessBean(ub));
                
            }
            
            
            return true;
            
        }catch (Exception e) {
            try {
                StringWriter errors = new StringWriter();
                PrintWriter pw = new PrintWriter(errors);
                e.printStackTrace(pw);
                
                pw.flush();
                pw.close();
                
                errors.flush();
                errors.close();
                
                loger.error("\r\n********* 赊销导入Movedb_Credit 定时调用异常: " + e.getMessage() + "\r\n" + errors + "******\r\n");
                InsertWSLOG.insert_JOBLOG(newEId,customerNo, "Movedb_Credit", "2.0升级3.0 赊销资料自动导入", ""+errors);
                
            } catch (Exception ignored) {
            }
            return false;
        }
        
    }
    //大客户预收 导入
    private boolean creditPrepay(List<DataProcessBean> old_data,List<DataProcessBean> data,String oldEId,String newEId,String customerNo){
        String[] columns = {
                "EID","SHOPID","MACHNO","OPNO","BDATE","CUSTOMERNO","PREPAYNO",
                "PREPAYAMT","USEDAMT","RESTAMT","PREPAYTYPE","RETURNNO"
        };
        
        try{
            //查询dcp2.0大客户预收
            String sql = " select * from td_customer_prepay "
                    + " where companyno='"+oldEId+"' and customerno='"+customerNo+"' "
                    + " and (movedbstatus='N' or movedbstatus is null) ";
            List<Map<String, Object>>old_getQData=StaticInfo.dao_pos2.executeQuerySQL(sql, null);
            if (CollectionUtil.isEmpty(old_getQData)){
                //loger.info("\r\n********* 赊销导入Movedb_Credit 客户编号："+customerNo+" 在2.0大客户预收表中不存在 ************\r\n");
                return true;  //无需导入处理
            }
            
            // 插入DCP_CUSTOMER_PREPAY
            for (Map<String, Object> old_oneQData:old_getQData){
                
                //增加判断逻辑，3.0是否存在判断
                sql = " select prepayno from dcp_customer_prepay"
                        + " where eid='"+newEId+"' "
                        + " and shopid='"+old_oneQData.get("SHOPNO").toString()+"' "
                        + " and prepayno='"+old_oneQData.get("PREPAYNO").toString()+"' ";
                
                List<Map<String, Object>> getQData=this.doQueryData(sql, null);
                if (!CollectionUtil.isEmpty(getQData)){
                    //回写2.0大客户赊销明细标记
                    UptBean ub = new UptBean("TD_CUSTOMER_PREPAY");
                    ub.addUpdateValue("MOVEDBSTATUS", new DataValue("E", Types.VARCHAR));
                    //Condition
                    ub.addCondition("COMPANYNO", new DataValue(oldEId, Types.VARCHAR));
                    ub.addCondition("SHOPNO", new DataValue(old_oneQData.get("SHOPNO").toString(), Types.VARCHAR));
                    ub.addCondition("PREPAYNO", new DataValue(old_oneQData.get("PREPAYNO").toString(), Types.VARCHAR));
                    
                    old_data.add(new DataProcessBean(ub));
                    continue;  //3.0资料已存在无需导入处理
                }
                
                
                DataValue[] insValue = new DataValue[]{
                        new DataValue(newEId, Types.VARCHAR),
                        new DataValue(old_oneQData.get("SHOPNO").toString(), Types.VARCHAR),
                        new DataValue(old_oneQData.get("MACHNO").toString(), Types.VARCHAR),
                        new DataValue(old_oneQData.get("OPNO").toString(), Types.VARCHAR),
                        new DataValue(old_oneQData.get("BDATE").toString(), Types.VARCHAR),
                        new DataValue(old_oneQData.get("CUSTOMERNO").toString(), Types.VARCHAR),
                        new DataValue(old_oneQData.get("PREPAYNO").toString(), Types.VARCHAR),
                        new DataValue(old_oneQData.get("PREPAYAMT").toString(), Types.VARCHAR),
                        new DataValue(old_oneQData.get("USEDAMT").toString(), Types.VARCHAR),
                        new DataValue(old_oneQData.get("RESTAMT").toString(), Types.VARCHAR),
                        new DataValue(old_oneQData.get("PREPAYTYPE").toString(), Types.VARCHAR),
                        new DataValue(old_oneQData.get("RETURNNO").toString(), Types.VARCHAR),
                };
                
                InsBean ib = new InsBean("DCP_CUSTOMER_PREPAY", columns);
                ib.addValues(insValue);
                data.add(new DataProcessBean(ib));
                
                //回写2.0大客户赊销明细标记
                UptBean ub = new UptBean("TD_CUSTOMER_PREPAY");
                ub.addUpdateValue("MOVEDBSTATUS", new DataValue("Y", Types.VARCHAR));
                //Condition
                ub.addCondition("COMPANYNO", new DataValue(oldEId, Types.VARCHAR));
                ub.addCondition("SHOPNO", new DataValue(old_oneQData.get("SHOPNO").toString(), Types.VARCHAR));
                ub.addCondition("PREPAYNO", new DataValue(old_oneQData.get("PREPAYNO").toString(), Types.VARCHAR));
                
                old_data.add(new DataProcessBean(ub));
            }
            
            return true;
            
        }catch (Exception e) {
            try {
                StringWriter errors = new StringWriter();
                PrintWriter pw = new PrintWriter(errors);
                e.printStackTrace(pw);
                
                pw.flush();
                pw.close();
                
                errors.flush();
                errors.close();
                
                loger.error("\r\n********* 赊销导入Movedb_Credit 定时调用异常: " + e.getMessage() + "\r\n" + errors + "******\r\n");
                InsertWSLOG.insert_JOBLOG(newEId,customerNo, "Movedb_Credit", "2.0升级3.0 赊销资料自动导入", ""+errors);
                
            } catch (Exception ignored) {
            }
            return false;
        }
        
        
    }
    //大客户回款 导入
    private boolean creditReturn(List<DataProcessBean> old_data,List<DataProcessBean> data,String oldEId,String newEId,String customerNo){
        String[] columns = {
                "EID","SHOPID","RETURNNO","BDATE","SDATE","STIME","RETURNAMT","OPNO","CUSTOMERNO","RETURNNAME",
                "MEMO","MACHINE","SQUADNO","RETURNTYPE","UPLOADTOCLOUD"
        };
        String[] columns_detail = {
                "EID","SHOPID","MACHNO","OPNO","BDATE","CUSTOMERNO","RETURNTYPE","RETURNNO","SOURCENO","SOURCETYPE",
                "SOURCESHOP","RETURNAMT","PRERETURNNO"
        };
        String[] columns_pay = {
                "EID","SHOPID","RETURNNO","ITEM","PAYCODE","PAYCODEERP","PAYNAME","CTTYPE","CARDNO","PAYSERNUM",
                "SERIALNO","REFNO","TERIMINALNO","DESCORE","PAY","EXTRA","CHANGED","SDATE","STIME","ISVERIFICATION",
                "ISORDERPAY","RETURNRATE","STATUS","PAYTYPE","FUNCNO","MERDISCOUNT","MERRECEIVE","THIRDDISCOUNT",
                "CUSTPAYREAL","COUPONMARKETPRICE","COUPONPRICE","CHARGEAMOUNT","PAYCHANNELCODE"
        };
        
        
        try{
            //查询dcp2.0大客户回款
            String sql = " select * from td_customer_return "
                    + " where companyno='"+oldEId+"' and customerno='"+customerNo+"' "
                    + " and (movedbstatus='N' or movedbstatus is null) ";
            List<Map<String, Object>>old_getQData=StaticInfo.dao_pos2.executeQuerySQL(sql, null);
            if (CollectionUtil.isEmpty(old_getQData)){
                //loger.info("\r\n********* 赊销导入Movedb_Credit 客户编号："+customerNo+" 在2.0大客户回款表中不存在 ************\r\n");
                return true;  //无需导入处理
            }
            
            for (Map<String, Object> old_oneQData:old_getQData){
                //增加判断逻辑，3.0是否存在判断
                sql = " select returnno from dcp_customer_return"
                        + " where eid='"+newEId+"' "
                        + " and shopid='"+old_oneQData.get("SHOP").toString()+"' "
                        + " and returnno='"+old_oneQData.get("RETURNNO").toString()+"' ";
                List<Map<String, Object>> getQData=this.doQueryData(sql, null);
                if (!CollectionUtil.isEmpty(getQData)){
                    //回写2.0大客户回款标记
                    UptBean ub = new UptBean("TD_CUSTOMER_RETURN");
                    ub.addUpdateValue("MOVEDBSTATUS", new DataValue("E", Types.VARCHAR));
                    //Condition
                    ub.addCondition("COMPANYNO", new DataValue(oldEId, Types.VARCHAR));
                    ub.addCondition("SHOP", new DataValue(old_oneQData.get("SHOP").toString(), Types.VARCHAR));
                    ub.addCondition("RETURNNO", new DataValue(old_oneQData.get("RETURNNO").toString(), Types.VARCHAR));
                    
                    old_data.add(new DataProcessBean(ub));
                    continue;  //3.0资料已存在无需导入处理
                }
                
                //判断回款单类型 小凤：如果支付档不为空，回款单类型=1，否则2   （回款单类型：1-客户回款 2-到款核销 ） 20221104
                String returnType="1";
                sql = " select paycode from td_customer_return_pay "
                        + " where companyno='"+oldEId+"' "
                        + " and shop='"+old_oneQData.get("SHOP").toString()+"' "
                        + " and returnno='"+old_oneQData.get("RETURNNO").toString()+"' ";
                List<Map<String, Object>>old_getQData1=StaticInfo.dao_pos2.executeQuerySQL(sql, null);
                if (CollectionUtil.isEmpty(old_getQData1)){
                    returnType="2";
                }
                
                
                // 插入DCP_CUSTOMER_RETURN
                DataValue[] insValue = new DataValue[]{
                        new DataValue(newEId, Types.VARCHAR),
                        new DataValue(old_oneQData.get("SHOP").toString(), Types.VARCHAR),
                        new DataValue(old_oneQData.get("RETURNNO").toString(), Types.VARCHAR),
                        new DataValue(old_oneQData.get("BDATE").toString(), Types.VARCHAR),
                        new DataValue(old_oneQData.get("SDATE").toString(), Types.VARCHAR),
                        new DataValue(old_oneQData.get("STIME").toString(), Types.VARCHAR),
                        new DataValue(old_oneQData.get("RETURNAMT").toString(), Types.VARCHAR),
                        new DataValue(old_oneQData.get("OPNO").toString(), Types.VARCHAR),
                        new DataValue(old_oneQData.get("CUSTOMERNO").toString(), Types.VARCHAR),
                        new DataValue(old_oneQData.get("RETURNNAME").toString(), Types.VARCHAR),
                        new DataValue("3.0备注:此笔单据为2.0自动导入  原2.0备注:"+old_oneQData.get("MEMO").toString(), Types.VARCHAR),
                        new DataValue(old_oneQData.get("MACHINE").toString(), Types.VARCHAR),
                        new DataValue(old_oneQData.get("SQUADNO").toString(), Types.VARCHAR),
                        new DataValue("Y", Types.VARCHAR),                                 //UPLOADTOCLOUD 是否已上传到云端,N-否【单店收银离线库专用字段】
                        new DataValue(returnType, Types.VARCHAR),            //RETURNTYPE 回款单类型：1-客户回款 2-到款核销
                };
                
                InsBean ib = new InsBean("DCP_CUSTOMER_RETURN", columns);
                ib.addValues(insValue);
                data.add(new DataProcessBean(ib));
                
                
                // 查询TD_CUSTOMER_RETURN_DETAIL
                sql = " select * from td_customer_return_detail "
                        + " where companyno='"+oldEId+"' and shopno='"+old_oneQData.get("SHOP").toString()+"' "
                        + " and returnno='"+old_oneQData.get("RETURNNO").toString()+"' ";
                List<Map<String, Object>>old_getQData_detail = StaticInfo.dao_pos2.executeQuerySQL(sql, null);
                if (!CollectionUtil.isEmpty(old_getQData_detail)){
                    for (Map<String, Object> old_oneQData_detail:old_getQData_detail){
                        // 插入DCP_CUSTOMER_RETURN_DETAIL
                        DataValue[] insValue_detail = new DataValue[]{
                                new DataValue(newEId, Types.VARCHAR),
                                new DataValue(old_oneQData_detail.get("SHOPNO").toString(), Types.VARCHAR),
                                new DataValue(old_oneQData_detail.get("MACHNO").toString(), Types.VARCHAR),
                                new DataValue(old_oneQData_detail.get("OPNO").toString(), Types.VARCHAR),
                                new DataValue(old_oneQData_detail.get("BDATE").toString(), Types.VARCHAR),
                                new DataValue(old_oneQData_detail.get("CUSTOMERNO").toString(), Types.VARCHAR),
                                //和小凤确认对接单头的回款单类型 20221104
                                //new DataValue(old_oneQData_detail.get("RETURNTYPE").toString(), Types.VARCHAR),
                                new DataValue(returnType, Types.VARCHAR),
                                new DataValue(old_oneQData_detail.get("RETURNNO").toString(), Types.VARCHAR),
                                new DataValue(old_oneQData_detail.get("SOURCENO").toString(), Types.VARCHAR),
                                new DataValue(old_oneQData_detail.get("SOURCETYPE").toString(), Types.VARCHAR),
                                new DataValue(old_oneQData_detail.get("SOURCESHOP").toString(), Types.VARCHAR),
                                new DataValue(old_oneQData_detail.get("RETURNAMT").toString(), Types.VARCHAR),
                                //和小凤确认给空 20221104
                                new DataValue("", Types.VARCHAR),   //PRERETURNNO
                        };
                        
                        InsBean ib_detail = new InsBean("DCP_CUSTOMER_RETURN_DETAIL", columns_detail);
                        ib_detail.addValues(insValue_detail);
                        data.add(new DataProcessBean(ib_detail));
                        
                    }
                }
                
                
                // 查询TD_CUSTOMER_RETURN_PAY
                sql = " select * from td_customer_return_pay "
                        + " where companyno='"+oldEId+"' and shop='"+old_oneQData.get("SHOP").toString()+"' "
                        + " and returnno='"+old_oneQData.get("RETURNNO").toString()+"' ";
                List<Map<String, Object>>old_getQData_pay = StaticInfo.dao_pos2.executeQuerySQL(sql, null);
                if (!CollectionUtil.isEmpty(old_getQData_pay)){
                    for (Map<String, Object> old_oneQData_pay:old_getQData_pay){
                        String status = old_oneQData_pay.get("CNFFLG").toString();
                        if ("N".equals(status)){
                            status="0";
                        }else {
                            status="100";
                        }
                        
                        //和小凤确认：根据PAYCODE匹配DCP_PAYTYPE第一条记录,DCP_PAYTYPE.PAYTYPE对应FUNCNO  20221104
                        String payType = "";
                        String funcNo = "";
                        sql = " select paytype,funcno from dcp_paytype "
                                + " where eid='"+newEId+"' "
                                + " and paycode='"+old_oneQData_pay.get("PAYCODE").toString()+"' "
                                + " order by createtime desc ";
                        List<Map<String, Object>>getQData1 = this.doQueryData(sql, null);
                        if (!CollectionUtil.isEmpty(getQData1)){
                            payType = getQData1.get(0).get("PAYTYPE").toString();
                            funcNo = getQData1.get(0).get("FUNCNO").toString();
                        }
                        
                        // PAY-CHANGED-EXTRA
                        BigDecimal pay_b = new BigDecimal(old_oneQData_pay.get("PAY").toString());
                        BigDecimal changed_b = new BigDecimal(old_oneQData_pay.get("CHANGED").toString());
                        BigDecimal extra_b = new BigDecimal(old_oneQData_pay.get("EXTRA").toString());
                        String merreceive = pay_b.subtract(changed_b).subtract(extra_b).toString();
                        
                        // 插入DCP_CUSTOMER_RETURN_PAY
                        DataValue[] insValue_pay = new DataValue[]{
                                new DataValue(newEId, Types.VARCHAR),
                                new DataValue(old_oneQData_pay.get("SHOP").toString(), Types.VARCHAR),
                                new DataValue(old_oneQData_pay.get("RETURNNO").toString(), Types.VARCHAR),
                                new DataValue(old_oneQData_pay.get("ITEM").toString(), Types.VARCHAR),
                                new DataValue(old_oneQData_pay.get("PAYCODE").toString(), Types.VARCHAR),
                                new DataValue(old_oneQData_pay.get("PAYCODEERP").toString(), Types.VARCHAR),
                                new DataValue(old_oneQData_pay.get("PAYNAME").toString(), Types.VARCHAR),
                                new DataValue(old_oneQData_pay.get("CTTYPE").toString(), Types.VARCHAR),
                                new DataValue(old_oneQData_pay.get("CARDNO").toString(), Types.VARCHAR),
                                new DataValue(old_oneQData_pay.get("PAYSERNUM").toString(), Types.VARCHAR),
                                new DataValue(old_oneQData_pay.get("SERIALNO").toString(), Types.VARCHAR),
                                new DataValue(old_oneQData_pay.get("REFNO").toString(), Types.VARCHAR),
                                new DataValue(old_oneQData_pay.get("TERIMINALNO").toString(), Types.VARCHAR),
                                new DataValue(old_oneQData_pay.get("DESCORE").toString(), Types.VARCHAR),
                                new DataValue(old_oneQData_pay.get("PAY").toString(), Types.VARCHAR),
                                new DataValue(old_oneQData_pay.get("EXTRA").toString(), Types.VARCHAR),
                                new DataValue(old_oneQData_pay.get("CHANGED").toString(), Types.VARCHAR),
                                new DataValue(old_oneQData_pay.get("SDATE").toString(), Types.VARCHAR),
                                new DataValue(old_oneQData_pay.get("STIME").toString(), Types.VARCHAR),
                                new DataValue(old_oneQData_pay.get("ISVERIFICATION").toString(), Types.VARCHAR),
                                new DataValue(old_oneQData_pay.get("ISORDERPAY").toString(), Types.VARCHAR),
                                new DataValue(old_oneQData_pay.get("RETURNRATE").toString(), Types.VARCHAR),
                                new DataValue(status, Types.VARCHAR),  //2.0字段为CNFFLG，2.0非N，3.0就是100
                                //以下和小凤确认对接关系  20221104 by jinzma
                                new DataValue(payType, Types.VARCHAR),     //PAYTYPE 根据PAYCODE匹配DCP_PAYTYPE第一条记录
                                new DataValue(funcNo, Types.VARCHAR),      //DCP_PAYTYPE.PAYTYPE对应FUNCNO
                                new DataValue("0", Types.VARCHAR),  //MERDISCOUNT  给0
                                new DataValue(merreceive, Types.VARCHAR),  //MERRECEIVE   PAY-CHANGED-EXTRA
                                new DataValue("0", Types.VARCHAR),  //THIRDDISCOUNT  给0
                                new DataValue(merreceive, Types.VARCHAR),  //CUSTPAYREAL    PAY-CHANGED-EXTRA
                                new DataValue("0", Types.VARCHAR),  //COUPONMARKETPRICE  给0
                                new DataValue("0", Types.VARCHAR),  //COUPONPRICE        给0
                                new DataValue("0", Types.VARCHAR),  //CHARGEAMOUNT       给0
                                new DataValue("", Types.VARCHAR),   //PAYCHANNELCODE     给空
                            
                        };
                        
                        InsBean ib_pay = new InsBean("DCP_CUSTOMER_RETURN_PAY", columns_pay);
                        ib_pay.addValues(insValue_pay);
                        data.add(new DataProcessBean(ib_pay));
                        
                    }
                }
                
                
                //回写2.0大客户回款标记
                UptBean ub = new UptBean("TD_CUSTOMER_RETURN");
                ub.addUpdateValue("MOVEDBSTATUS", new DataValue("Y", Types.VARCHAR));
                //Condition
                ub.addCondition("COMPANYNO", new DataValue(oldEId, Types.VARCHAR));
                ub.addCondition("SHOP", new DataValue(old_oneQData.get("SHOP").toString(), Types.VARCHAR));
                ub.addCondition("RETURNNO", new DataValue(old_oneQData.get("RETURNNO").toString(), Types.VARCHAR));
                
                old_data.add(new DataProcessBean(ub));
            }
            
            return true;
            
        }catch (Exception e) {
            try {
                StringWriter errors = new StringWriter();
                PrintWriter pw = new PrintWriter(errors);
                e.printStackTrace(pw);
                
                pw.flush();
                pw.close();
                
                errors.flush();
                errors.close();
                
                loger.error("\r\n********* 赊销导入Movedb_Credit 定时调用异常: " + e.getMessage() + "\r\n" + errors + "******\r\n");
                InsertWSLOG.insert_JOBLOG(newEId,customerNo, "Movedb_Credit", "2.0升级3.0 赊销资料自动导入", ""+errors);
                
            } catch (Exception ignored) {
            }
            return false;
        }
        
    }
}

