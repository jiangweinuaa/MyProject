package com.dsc.spos.scheduler.job;

import cn.hutool.core.collection.CollectionUtil;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * JOB：Movedb_Stock
 * 说明：2.0升级3.0库存导入
 * @author jinzma
 * @since  2022-10-25
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class Movedb_Stock extends InitJob{
    
    static boolean bRun=false;//标记此服务是否正在执行中
    public String doExe() {
        //此服务是否正在执行中
        //返回信息
        String sReturnInfo="";
        
        if(bRun ){
            loger.info("\r\n********* 库存导入Movedb_Stock正在执行中,本次调用取消 ************\r\n");
            sReturnInfo="库存导入Movedb_Stock正在执行中 ";
            return sReturnInfo;
        }
        
        bRun=true;
        
        loger.info("\r\n ********* 库存导入Movedb_Stock定时调用Start ************\r\n");
        try {
            String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String sTime = new SimpleDateFormat("HHmmss").format(new Date());
            //先查 job 执行时间，然后再执行后续操作
            String sql = "select * from job_quartz_detail where job_name='Movedb_Stock' and status='100' ";
            List<Map<String, Object>> getTimeDatas=this.doQueryData(sql, null);
            if(getTimeDatas!= null && !getTimeDatas.isEmpty()) {
                for (Map<String, Object> map : getTimeDatas) {
                    String beginTime = map.get("BEGIN_TIME").toString();
                    String endTime = map.get("END_TIME").toString();
                    // 如果当前时间在 执行时间范围内，  就执行自动收货
                    if(sTime.compareTo(beginTime)>=0 && sTime.compareTo(endTime)<0) {
                        break;
                    }else{
                        loger.info("\r\n********* 库存导入Movedb_Stock 执行时间未到,本次调用End ************\r\n");
                        return sReturnInfo;
                    }
                }
            }
            
            
            //查询导入基本配置
            sql = " select a.*,b.newshopid from dcp_movedbsetting a"
                    + " left join dcp_movedbsetting_shop b on a.neweid=b.neweid"
                    + " where a.status='100' ";
            List<Map<String, Object>> getQDataBase=this.doQueryData(sql, null);
            if (CollectionUtil.isEmpty(getQDataBase)){
                loger.info("\r\n********* 库存导入Movedb_Stock 导入配置资料为空,本次调用End ************\r\n");
                return sReturnInfo;
            }
            
            String newEId = getQDataBase.get(0).get("NEWEID").toString();
            String oldEId = getQDataBase.get(0).get("OLDEID").toString();
            
            //3.0门店资料查询 (指定门店或全部门店)
            if (!Check.Null(getQDataBase.get(0).get("NEWSHOPID").toString())){
                sql = " select a.organizationno,a.in_cost_warehouse from dcp_org a"
                        + " inner join dcp_movedbsetting_shop b on a.eid=b.neweid and a.organizationno=b.newshopid"
                        + " inner join dcp_warehouse c on a.eid=c.eid and a.organizationno=c.organizationno and a.in_cost_warehouse=c.warehouse"
                        + " where a.eid='"+newEId+"' and a.status='100' and a.org_form='2'";
            }else{
                sql = " select a.organizationno,a.in_cost_warehouse from dcp_org a"
                        + " inner join dcp_warehouse c on a.eid=c.eid and a.organizationno=c.organizationno and a.in_cost_warehouse=c.warehouse"
                        + " where a.eid='"+newEId+"' and a.status='100' and a.org_form='2'";
            }
            List<Map<String, Object>> getQData=this.doQueryData(sql, null);
            if (CollectionUtil.isEmpty(getQData)){
                loger.info("\r\n********* 库存导入Movedb_Stock 3.0生效的门店资料为空,本次调用End ************\r\n");
                return sReturnInfo;
            }
            
            outer:
            for (Map<String, Object>oneQData : getQData){
                String shopId = oneQData.get("ORGANIZATIONNO").toString();
                String newWarehouse = oneQData.get("IN_COST_WAREHOUSE").toString();
                if (Check.Null(newWarehouse)){
                    loger.info("\r\n********* 库存导入Movedb_Stock 门店："+shopId+" 对应的默认收货成本仓为空,自动导入失败 ************\r\n");
                    InsertWSLOG.insert_JOBLOG(newEId,shopId, "Movedb_Stock", "2.0升级3.0门店库存自动导入", "门店："+shopId+" 对应的默认收货成本仓为空,自动导入失败");
                    continue;
                }
                //判断3.0是否存在日结资料或者库存流水，存在就报错
                sql = " select a.organizationno from dcp_stock_day a where a.eid='"+newEId+"' and a.organizationno='"+shopId+"'"
                        + " union all"
                        + " select a.organizationno from dcp_stock_detail a where a.eid='"+newEId+"' and a.organizationno='"+shopId+"'";
                List<Map<String, Object>>getQData1=this.doQueryData(sql, null);
                if (!CollectionUtil.isEmpty(getQData1)){
                    loger.info("\r\n********* 库存导入Movedb_Stock 门店："+shopId+" 已产生库存流水或库存日结,自动导入失败 ************\r\n");
                    //已产生就不再提示了，避免过多的报错提示  by jinzma 20221117
                    //InsertWSLOG.insert_JOBLOG(newEId,shopId, "Movedb_Stock", "2.0升级3.0门店库存自动导入", "门店："+shopId+" 已产生库存流水或库存日结,自动导入失败");
                    continue;
                }
                
                //判断2.0是否存在库存流水，存在就报错
                sql = " select a.organizationno from tw_stock_detail a"
                        + " where a.companyno='"+oldEId+"' and a.organizationno='"+shopId+"'"
                        + " group by a.organizationno";
                List<Map<String, Object>>old_getQData2=StaticInfo.dao_pos2.executeQuerySQL(sql, null);
                if (!CollectionUtil.isEmpty(old_getQData2)){
                    loger.info("\r\n********* 库存导入Movedb_Stock 门店："+shopId+" 在2.0库存流水表中存在资料,自动导入失败 ************\r\n");
                    InsertWSLOG.insert_JOBLOG(newEId,shopId, "Movedb_Stock", "2.0升级3.0门店库存自动导入", "门店："+shopId+" 在2.0库存流水表中存在资料,自动导入失败");
                    continue;
                }
                
                //判断2.0是否日结完成，未完成就报错
                sql = " select a.warehouse,a.edate from tw_stock_day a"
                        + " where a.companyno='"+oldEId+"' and a.organizationno='"+shopId+"'"
                        + " group by a.warehouse,a.edate";
                List<Map<String, Object>>old_getQData3=StaticInfo.dao_pos2.executeQuerySQL(sql, null);
                if (CollectionUtil.isEmpty(old_getQData3)){
                    loger.info("\r\n********* 库存导入Movedb_Stock 门店："+shopId+" 在2.0日结档中不存在,自动导入失败 ************\r\n");
                    InsertWSLOG.insert_JOBLOG(newEId,shopId, "Movedb_Stock", "2.0升级3.0门店库存自动导入", "门店："+shopId+" 在2.0日结档中不存在,自动导入失败");
                    continue;
                }
                
                //可能存在多仓或者多个日结日期
                String eDate="";
                for (Map<String, Object> old_oneQData1:old_getQData3){
                    if (Check.Null(eDate)){
                        eDate = old_oneQData1.get("EDATE").toString();
                    }else{
                        if (!eDate.equals(old_oneQData1.get("EDATE").toString())){
                            loger.info("\r\n********* 库存导入Movedb_Stock 门店："+shopId+" 在2.0未完成日结,自动导入失败 ************\r\n");
                            InsertWSLOG.insert_JOBLOG(newEId,shopId, "Movedb_Stock", "2.0升级3.0门店库存自动导入", "门店："+shopId+" 在2.0未完成日结,自动导入失败");
                            continue outer;
                        }
                    }
                    if (PosPub.compare_date(PosPub.GetStringDate(eDate,1),sDate)==-1) {   //日结日期+1天 和当前系统日期判断
                        loger.info("\r\n********* 库存导入Movedb_Stock 门店："+shopId+" 在2.0未完成日结,自动导入失败 ************\r\n");
                        InsertWSLOG.insert_JOBLOG(newEId,shopId, "Movedb_Stock", "2.0升级3.0门店库存自动导入", "门店："+shopId+" 在2.0未完成日结,自动导入失败");
                        continue outer;
                    }
                    
                    String oldWarehouse = old_oneQData1.get("WAREHOUSE").toString();
                    
                    //此处优化，如果2.0门店只有一个仓库，转入3.0的时候，不再校验仓库有效性
                    if (old_oneQData1.size()==1){
                        oldWarehouse = newWarehouse;
                    }
                    
                    //旧仓库在3.0中是否存在判断
                    if (!oldWarehouse.equals(newWarehouse)){
                        sql = " select warehouse from dcp_warehouse a"
                                + " where a.eid='"+newEId+"' and a.organizationno='"+shopId+"' and a.warehouse='"+oldWarehouse+"' and a.warehouse_type='2'";
                        List<Map<String, Object>>getQData4=this.doQueryData(sql, null);
                        if (CollectionUtil.isEmpty(getQData4)){
                            loger.info("\r\n********* 库存导入Movedb_Stock 门店: "+shopId+" 日结表对应的仓库: "+oldWarehouse+"在3.0仓库表中不存在,自动导入失败 ************\r\n");
                            InsertWSLOG.insert_JOBLOG(newEId,shopId, "Movedb_Stock", "2.0升级3.0门店库存自动导入", "门店: "+shopId+" 日结表对应的仓库: "+oldWarehouse+"在3.0仓库表中不存在,自动导入失败");
                            continue outer;
                        }
                    }
                    
                    //此处存在多仓
                    if (MoveStock(oldEId,newEId,shopId,oldWarehouse,eDate)){
                        //成功后删除JOB异常日志    BY JZMA 20221102
                        InsertWSLOG.delete_JOBLOG(newEId,shopId,"Movedb_Stock");
                    }
                    
                }
            }
        } catch (Exception e) {
            try {
                StringWriter errors = new StringWriter();
                PrintWriter pw = new PrintWriter(errors);
                e.printStackTrace(pw);
                
                pw.flush();
                pw.close();
                
                errors.flush();
                errors.close();
                
                loger.error("\r\n********* 库存导入Movedb_Stock 定时调用异常: " + e.getMessage() + "\r\n" + errors + "******\r\n");
                
                
            }catch (Exception ignored){
            }
            
        }
        finally {
            bRun=false;
            loger.info("\r\n*********库存导入Movedb_Stock 定时调用End ************\r\n");
        }
        
        return sReturnInfo;
    }
    
    private boolean MoveStock(String oldEId,String newEId,String shopId,String oldWarehouse,String eDate) {
        String[] columns_stock = {
                "EID","ORGANIZATIONNO","WAREHOUSE","PLUNO","FEATURENO","BASEUNIT",
                "QTY","LOCKQTY","ONLINEQTY",
                "CREATEOPID","CREATEOPNAME","CREATETIME"
        };
        String[] columns_stock_day = {
                "EID","ORGANIZATIONNO","WAREHOUSE","EDATE","PLUNO","FEATURENO","BATCHNO","PRODDATE",
                "BASEUNIT","QTY", "PRICE","DISTRIPRICE","AMT"
        };
        
        try{
            List<DataProcessBean> data = new ArrayList<>();
            List<DataProcessBean> old_data = new ArrayList<>();
            //查询dcp2.0库存日结资料
            String sql = " select a.pluno,sum(a.qty) as qty,max(a.unit) as unit,max(a.price) as price,max(a.distriprice) as distriprice"
                    + " from tw_stock_day a"
                    // + " inner join tb_goods b on a.companyno=b.companyno and a.pluno=b.pluno"
                    + " where a.companyno='"+oldEId+"' and a.organizationno='"+shopId+"' and a.warehouse='"+oldWarehouse+"' and a.edate='"+eDate+"'"
                    + " group by a.pluno";
            List<Map<String, Object>>old_getQData=StaticInfo.dao_pos2.executeQuerySQL(sql, null);
            for (Map<String, Object> old_oneQData:old_getQData){
                String pluNo = old_oneQData.get("PLUNO").toString();
                String old_qty = old_oneQData.get("QTY").toString();
                String old_price = old_oneQData.get("PRICE").toString();
                String old_distriprice = old_oneQData.get("DISTRIPRICE").toString();
                String old_unit = old_oneQData.get("UNIT").toString();
                
                //此处做了优化，2.0日结数量为空默认给0
                if (Check.Null(old_qty)){
                    old_qty="0";
                }
                if (!PosPub.isNumericType(old_price)){
                    old_price="0";
                }
                if (!PosPub.isNumericType(old_distriprice)){
                    old_distriprice="0";
                }
                //此处做了优化，2.0日结单位为空默认给3.0基准单位
                if (!Check.Null(old_unit)){
                    //2.0商品资料校验 商品编号和单位
                    sql = " select a.baseunit,b.unitratio from dcp_goods a"
                            + " inner join dcp_goods_unit b on a.eid=b.eid and a.pluno=b.pluno and a.baseunit=b.unit"
                            + " where a.eid='"+newEId+"' and a.pluno='"+pluNo+"' and b.ounit='"+old_unit+"' and a.plutype<>'FEATURE' ";
                }else{
                    sql = " select a.baseunit,b.unitratio from dcp_goods a"
                            + " inner join dcp_goods_unit b on a.eid=b.eid and a.pluno=b.pluno and a.baseunit=b.ounit and a.plutype<>'FEATURE'"
                            + " where a.eid='"+newEId+"' and a.pluno='"+pluNo+"' ";
                }
                List<Map<String, Object>>getQData1= this.doQueryData(sql, null);
                if (CollectionUtil.isEmpty(getQData1)){
                    //此处导入优化，如果2.0商品基本资料表已经失效或者不存在的，则跳过这个商品继续导入剩余商品
                    sql = " select pluno from tb_goods where companyno='"+oldEId+"' and pluno='"+pluNo+"' and cnfflg='Y' ";
                    List<Map<String, Object>>old_getQData1=StaticInfo.dao_pos2.executeQuerySQL(sql, null);
                    if (CollectionUtil.isEmpty(old_getQData1)){
                        //回写2.0日结档标记 记录为失败，失败原因是3.0不存在这个商品并且2.0商品基本资料表里面也不存在或者已经失效
                        UptBean ub = new UptBean("TW_STOCK_DAY");
                        ub.addUpdateValue("MOVEDBSTATUS", new DataValue("E", Types.VARCHAR));
                        //Condition
                        ub.addCondition("COMPANYNO", new DataValue(oldEId, Types.VARCHAR));
                        ub.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
                        ub.addCondition("WAREHOUSE", new DataValue(oldWarehouse, Types.VARCHAR));
                        ub.addCondition("EDATE", new DataValue(eDate, Types.VARCHAR));
                        ub.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
                        old_data.add(new DataProcessBean(ub));
                        
                        continue;
                    }
                    loger.info("\r\n********* 库存导入Movedb_Stock 商品编号："+pluNo+" 在3.0商品资料表或商品单位表中不存在,自动导入失败 ************\r\n");
                    InsertWSLOG.insert_JOBLOG(newEId,shopId, "Movedb_Stock", "2.0升级3.0门店库存自动导入", "商品编号："+pluNo+" 在3.0商品资料表或商品单位表中不存在,自动导入失败");
                    return false;
                }
                
                String new_baseUnit = getQData1.get(0).get("BASEUNIT").toString();
                if (Check.Null(new_baseUnit)){
                    loger.info("\r\n********* 库存导入Movedb_Stock 商品编号："+pluNo+" 对应的基准单位(3.0)为空,自动导入失败 ************\r\n");
                    InsertWSLOG.insert_JOBLOG(newEId,shopId, "Movedb_Stock", "2.0升级3.0门店库存自动导入", "商品编号："+pluNo+" 对应的基准单位(3.0)为空,自动导入失败");
                    return false;
                }
                
                String new_unitRatio = getQData1.get(0).get("UNITRATIO").toString();
                if (!PosPub.isNumericType(new_unitRatio)){
                    loger.info("\r\n********* 库存导入Movedb_Stock 商品编号："+pluNo+" 对应的单位转换率(3.0)为空,自动导入失败 ************\r\n");
                    InsertWSLOG.insert_JOBLOG(newEId,shopId, "Movedb_Stock", "2.0升级3.0门店库存自动导入", "商品编号："+pluNo+" 对应的单位转换率(3.0)为空,自动导入失败");
                    return false;
                }
                
                BigDecimal new_unitRatio_b = new BigDecimal(new_unitRatio);
                
                String new_qty = new_unitRatio_b.multiply(new BigDecimal(old_qty)).setScale(6,RoundingMode.HALF_UP).toString();
                String new_price = new_unitRatio_b.multiply(new BigDecimal(old_price)).setScale(6,RoundingMode.HALF_UP).toString();
                String new_distriprice = new_unitRatio_b.multiply(new BigDecimal(old_distriprice)).setScale(6,RoundingMode.HALF_UP).toString();
                String new_amt = new BigDecimal(new_qty).multiply(new BigDecimal(new_price)).setScale(6,RoundingMode.HALF_UP).toString();
                
                // 插入DCP_STOCK
                DataValue[] insValue_stock = new DataValue[]{
                        new DataValue(newEId, Types.VARCHAR),
                        new DataValue(shopId, Types.VARCHAR),
                        new DataValue(oldWarehouse, Types.VARCHAR),  //2.0仓库已经检核过是否存在3.0中
                        new DataValue(pluNo, Types.VARCHAR),
                        new DataValue(" ", Types.VARCHAR),
                        new DataValue(new_baseUnit, Types.VARCHAR),
                        new DataValue(new_qty, Types.VARCHAR),
                        new DataValue("0", Types.VARCHAR),
                        new DataValue("0", Types.VARCHAR),
                        new DataValue("admin", Types.VARCHAR),
                        new DataValue("admin", Types.VARCHAR),
                        new DataValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), Types.DATE),
                };
                InsBean ib_stock = new InsBean("DCP_STOCK", columns_stock);
                ib_stock.addValues(insValue_stock);
                data.add(new DataProcessBean(ib_stock));
                
                // 插入DCP_STOCK_DAY
                DataValue[] insValue_stock_day = new DataValue[]{
                        new DataValue(newEId, Types.VARCHAR),
                        new DataValue(shopId, Types.VARCHAR),
                        new DataValue(oldWarehouse, Types.VARCHAR),
                        new DataValue(eDate, Types.VARCHAR),
                        new DataValue(pluNo, Types.VARCHAR),
                        new DataValue(" ", Types.VARCHAR),
                        new DataValue(" ", Types.VARCHAR),
                        new DataValue("", Types.VARCHAR),
                        new DataValue(new_baseUnit, Types.VARCHAR),
                        new DataValue(new_qty, Types.VARCHAR),
                        new DataValue(new_price, Types.VARCHAR),
                        new DataValue(new_distriprice, Types.VARCHAR),
                        new DataValue(new_amt, Types.VARCHAR),
                };
                
                InsBean ib_stock_day = new InsBean("DCP_STOCK_DAY", columns_stock_day);
                ib_stock_day.addValues(insValue_stock_day);
                data.add(new DataProcessBean(ib_stock_day));
                
                // 插入DCP_STOCK_DAY_STATIC
                InsBean ib_stock_day_static = new InsBean("DCP_STOCK_DAY_STATIC", columns_stock_day);
                ib_stock_day_static.addValues(insValue_stock_day);
                data.add(new DataProcessBean(ib_stock_day_static));
                
                //回写2.0日结档标记
                UptBean ub = new UptBean("TW_STOCK_DAY");
                ub.addUpdateValue("MOVEDBSTATUS", new DataValue("Y", Types.VARCHAR));
                //Condition
                ub.addCondition("COMPANYNO", new DataValue(oldEId, Types.VARCHAR));
                ub.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
                ub.addCondition("WAREHOUSE", new DataValue(oldWarehouse, Types.VARCHAR));
                ub.addCondition("EDATE", new DataValue(eDate, Types.VARCHAR));
                ub.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
                old_data.add(new DataProcessBean(ub));
            }
            
            StaticInfo.dao.useTransactionProcessData(data);
            StaticInfo.dao_pos2.useTransactionProcessData(old_data);
            
        }catch (Exception e){
            try {
                StringWriter errors = new StringWriter();
                PrintWriter pw = new PrintWriter(errors);
                e.printStackTrace(pw);
                
                pw.flush();
                pw.close();
                
                errors.flush();
                errors.close();
                
                loger.error("\r\n********* 库存导入Movedb_Stock 定时调用异常: " + e.getMessage() + "\r\n" + errors + "******\r\n");
                
                return false;
                
            }catch (Exception ignored){
            }
        }
        return true;
    }
}
