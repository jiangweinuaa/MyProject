package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_BillDateAlterReq;
import com.dsc.spos.json.cust.res.DCP_BillDateAlterRes;
import com.dsc.spos.ninetyone.util.ColumnDataValue;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.SUtil;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
 
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 结算日期
 * @date   2024-09-19
 * @author 01029 
 */
public class DCP_BillDateCalCulate extends SPosAdvanceService<DCP_BillDateAlterReq, DCP_BillDateAlterRes> {

    @Override
    protected void processDUID(DCP_BillDateAlterReq req, DCP_BillDateAlterRes res) throws Exception {
        
        try {
        	String oprType = req.getRequest().getOprType();//I insert U update

            if(oprType.equals("I")){
                processOnCreate(req,res);
            }else if(oprType.equals("U")) {
              
            }else {
            	res.setSuccess(false);
                res.setServiceStatus("200");
                res.setServiceDescription("服务执行失败:  操作类型 传值异常  ");
                return;
			}
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行异常:" + e.getMessage());

        }
    }
    
    private void processOnCreate(DCP_BillDateAlterReq req, DCP_BillDateAlterRes res) throws Exception{
    	String billDateNo = req.getRequest().getBillDateNo();
        String eId = req.geteId();
        StringBuffer errMsg = new StringBuffer("");
        String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String sql = null;
        sql = this.isRepeat(eId, billDateNo);
        List<Map<String, Object>> mDatas = this.doQueryData(sql, null);
        if (mDatas.isEmpty()) {
        	ColumnDataValue columns = new ColumnDataValue();
        	String[] columns1 =null;
        	DataValue[] insValue1 =null;
        	 
            // 新增多语言表
            List<DCP_BillDateAlterReq.NameLang> nameLang = req.getRequest().getName_Lang();
            String sName =null; 
            if (nameLang != null && nameLang.size() > 0) {
                for (DCP_BillDateAlterReq.NameLang par : nameLang) {
                    String langType = par.getLangType();
                    sName = par.getName();
                    columns.Columns.clear();
                    columns.DataValues.clear();
                    columns.Add("EID", eId, Types.VARCHAR);
            		columns.Add("BILLDATENO", billDateNo, Types.VARCHAR);
            		columns.Add("LANG_TYPE", langType, Types.VARCHAR);
            		columns.Add("NAME", sName, Types.VARCHAR);               	              

            		 columns1 = columns.Columns.toArray(new String[0]);
            		 insValue1 = columns.DataValues.toArray(new DataValue[0]);
                    // 添加多语言信息
                    InsBean ib2 = new InsBean("DCP_BILLDATE_LANG", columns1);
                    ib2.addValues(insValue1);
                    this.addProcessData(new DataProcessBean(ib2));
                }
            }
            columns.Columns.clear();
            columns.DataValues.clear();
            columns.Add("EID", eId, Types.VARCHAR);
    		columns.Add("BILLDATENO", billDateNo, Types.VARCHAR);
    		columns.Add("NAME", sName, Types.VARCHAR);
    		columns.Add("BILLTYPE", req.getRequest().getBillDateType(), Types.VARCHAR);
    		columns.Add("FDATE", req.getRequest().getFDate(), Types.VARCHAR);
    		columns.Add("ADDMONTHS", String.valueOf(req.getRequest().getAddMonths()), Types.VARCHAR);   
    		columns.Add("ADDDAYS", String.valueOf(req.getRequest().getAddDays()), Types.VARCHAR); 
    
    		columns.Add("STATUS", Integer.valueOf(req.getRequest().getStatus()), Types.INTEGER); 
    		columns.Add("CREATEOPID", req.getEmployeeNo(), Types.VARCHAR); 
    		columns.Add("CREATEDEPTID", req.getDepartmentNo() ,Types.VARCHAR); 
    		columns.Add("CREATETIME", lastmoditime, Types.DATE); 
    		 
            columns1 = columns.Columns.toArray(new String[0]);
   		    insValue1 = columns.DataValues.toArray(new DataValue[0]);
            InsBean ib1 = new InsBean("DCP_BILLDATE", columns1);
            ib1.addValues(insValue1);
            this.addProcessData(new DataProcessBean(ib1)); // 新增

            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } else {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败: 结算日期条件信息：" + billDateNo + "已存在 ");
            return;
        }
    }
    
  //根据 开始结束日期 去掉不符合条件的 账期 ，不在开始结束日期里面的 。
  	public static  List<String> oprList(String xBDate,String xEDate,List<String> listDay )   {
  		if (SUtil.EmptyList(listDay)){
  			return null;
  		}
  		List<String> listRet = new ArrayList<String>();
  		String bDate=null;
  		String eDate=null;
  		String day=null;
  		String[] day1=null; //20241107 -20241226
           for (int i = 0; i < listDay.size(); i++) {
  			day1= listDay.get(i).split(",");
  			bDate =day1[0];
  			eDate =day1[1];
  			//如果日期 在 合同的 开始和结束时间内部  那么 这是合法时间 直接取出
  			if (xBDate.compareTo(bDate)<=0 && xEDate.compareTo(eDate)>=0){
  				listRet.add(listDay.get(i));
  				continue;
  			}
  			//如果  合同的 开始 时间在 结账日期的 区间内部 那么 开始时间要替换掉 结账日期的开始时间
  			if (xBDate.compareTo(bDate)>=0 && xBDate.compareTo(eDate)<=0  ) {
  				bDate = xBDate;
  				//如果  合同的 结束 时间在 结账日期的 区间内部 那么 结束时间要替换掉 结账日期的结束时间
  				if (xEDate.compareTo(bDate)>=0 && xEDate.compareTo(eDate)<=0  ) {
  					listRet.add(bDate+","+xEDate);
  					continue;
  				}else if ( xEDate.compareTo(eDate)>0  ) { { //结束时间在外部则不替换结束时间 只替换开始时间
  					listRet.add(bDate+","+eDate);
  					continue;
  				} 
  				}	
  			} 
  			
  			//如果  合同的 结束时间在 结账日期的 区间内部 那么 结束时间要替换掉 结账日期的结束时间
  			if (xEDate.compareTo(bDate)>=0 && xEDate.compareTo(eDate)<=0  ) {
  				eDate = xEDate;
  				//如果  合同的 开始 时间在 结账日期的 区间内部 那么 开始时间要替换掉 结账日期的开始时间 和上面有判断重复 ，先这么写
  				if (xBDate.compareTo(bDate)>=0 && xBDate.compareTo(eDate)<=0  ) {
  					listRet.add(xBDate+","+xEDate);
  					continue;
  				}else if ( xBDate.compareTo(bDate)<0  ) { { //结束开始时间在外部则不替换开始时间 只替换结束时间
  					listRet.add(bDate+","+xEDate);
  					continue;
  				} 
  				}	
  			} 
  			
  			//System.out.println(listDay.get(i)+ "--" + listDay.get(i+1));	
  		}
  		return listRet;
  		
  	}
  	public static boolean checkCond(String xBType,String xBDate,String xEDate,String xFDate) {
		LocalDate day=null;
		String[] day1=null; //20241107 -20241226
		if  ("1".equals(xBType)){ //固定结算日
			try {
				day = LocalDate.parse(xBDate);
				day = LocalDate.parse(xEDate);	
				
				day1= xFDate.split(",");
				int dBefore=0;
				for (String d : day1) {
					if (!"ee".equals(d)){
						int d1= Integer.valueOf(d);
						if (d1<=dBefore) {
							return false;
						}
						dBefore =d1;
					   if ((d1<1 || d1>31 )) {
						   return false;
					}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				return false;
			}
			
		}
		return true;
		
	}
    public static List<String> getDay(String xBType,String xBDate,String xEDate,String xFDate) {
		//记得增加判断  里面的每个日期是否在 1-31 里面 ，是否是日期格式是否 是升序
		//开始结束日期是否符合条件
    	String billType =xBType;
    	String fDate =xFDate;
    	String bDate =null;
    	String eDate =null;
    	String hbDate =xBDate;       //合同开始日期
    	String heDate =xEDate;
    
    	LocalDate day = null;
    	List<String> listInsDay = new ArrayList<String>();
    	if (!checkCond(xBDate,xBDate,xEDate,xFDate)) return listInsDay ; 
    	LocalDate endDay= null;
    	LocalDate xTemp= null;
    	if ("1".equals(billType)){ //固定结算日  （可多选）
    		bDate=hbDate;
    		eDate = heDate;
    	 
    		day = LocalDate.parse(bDate);
    		endDay = LocalDate.parse(heDate);
     
    		int iDay=0;
    		while (!day.isAfter(endDay) ) { //合同结束日期
    		 
    			List<String> listDay = Arrays.asList(fDate.split(","));
    			for (int i = 0; i < listDay.size(); i++) {//2024-11-01 6,21,31
    				if ("ee".equals(listDay.get(i))){
    					xTemp = LocalDate.parse(day.toString().substring(0,8)+"01"); 
    					xTemp = xTemp.with(TemporalAdjusters.lastDayOfMonth());	
    					iDay= Integer.valueOf( xTemp.toString().substring(8,10));
    				}else {
    					iDay= Integer.valueOf( listDay.get(i));
    					//这里判断下日期 如果超过30号 就要看大月小月
    					iDay = gDay(iDay,Integer.valueOf(day.toString().substring(5,7)));
					}
    				
    				if (i==0){  //第一次执行 开始就是第一天
    					bDate = day.toString().substring(0,8)+"01";	    						
    				}else {   
						bDate = LocalDate.parse(eDate).plusDays(1).toString();
					}
    				eDate = day.toString().substring(0,8)+String.format("%02d", iDay);
    				//只有日期 还在一个月里面才可以计算
    				if (!bDate.substring(0,7).equals(eDate.substring(0,7))) continue;
    				  listInsDay.add(bDate+","+eDate);
    				 
    				
    				if (i==listDay.size()-1){ //最后一次执行结束还得把剩下日期算 最后一个账期
    					bDate = LocalDate.parse(eDate).plusDays(1).toString();
    					//只有日期 还在一个月里面才可以计算
    					if (bDate.substring(0,7).equals(eDate.substring(0,7))) {    						    					
    					eDate = LocalDate.parse(bDate).with(TemporalAdjusters.lastDayOfMonth()).toString();
    					//只有日期 还在一个月里面才可以计算
    					if (bDate.substring(0,7).equals(eDate.substring(0,7)))
    					if (eDate.compareTo(bDate)>=0){
    						listInsDay.add(bDate+","+eDate);
    					}
    					}
    				}
    				
    			}

    			day = day.plusMonths(1);
    		}
    		
    	}
    
		return listInsDay;
    	 
	}
	
	public static int gDay(int iDay,int imonth) {
		Integer[] month = {4,6,9,11};
		List<Integer> li= Arrays.asList(month);
		if (2==imonth) {
			if (iDay>28) iDay=28;
		}else if (li.contains(imonth)) {
			if (iDay>30) iDay=30;
		}else {
			if (iDay>31) iDay=31;
		}
		return iDay;
		
	}

	public static List<String> getDay(String xBDate,String xEDate,String xFDate,int xMonth)   {
		if (xMonth<=0 || xMonth>=12){
			return null;
		}
    	String billType ="2";
    	//记得增加判断  里面的每个日期是否在 1-31 里面 ，是否是日期格式是否 是升序
    	String fDate =xFDate;
    	String bDate =null;
    	String eDate =null;
    	String hbDate =xBDate;       //合同开始日期
    	String heDate =xEDate;
   
    	LocalDate day = null;
    	List<String> listInsDay = new ArrayList<String>();
    	 
    	LocalDate endDay= null;
    	LocalDate xTemp= null;
    	if ("2".equals(billType)){ //固定结算日加月数
    		bDate=hbDate;
    		eDate = heDate;
    	 
    		day = LocalDate.parse(bDate);
    		endDay = LocalDate.parse(heDate);
    		boolean bFirst= false;
     
    		int iDay=0;
    		int incMonth=xMonth;
    		while (!day.isAfter(endDay) ) { //合同结束日期
    	 
    			if ("ee".equals(fDate)){
					xTemp = LocalDate.parse(day.toString().substring(0,8)+"01"); 
					xTemp = xTemp.with(TemporalAdjusters.lastDayOfMonth());	
					iDay= Integer.valueOf( xTemp.toString().substring(8,10));
				}else {
					iDay= Integer.valueOf( fDate);
					//这里判断下日期 如果超过30号 就要看大月小月
					iDay = gDay(iDay,Integer.valueOf(day.toString().substring(5,7)));
				}
    			if (!bFirst) { //第一次执行 就是当月第一天 ，第二次以后执行 就行 截止日期+1
    				bDate = day.toString().substring(0,8)+"01";	 
    				bFirst = true;
				}
    			  
    			eDate = bDate.toString().substring(0,8)+String.format("%02d", iDay);
    			xTemp = LocalDate.parse(eDate);
    			xTemp = xTemp.plusMonths(incMonth);
    			eDate = xTemp.toString(); 
				listInsDay.add(bDate+","+eDate);  
				bDate = LocalDate.parse(eDate).plusDays(1).toString();
				day = day.plusMonths(incMonth);
    		}
    		
    		//最后再判断一次 如果最后一次的结束日期 到合同结束日期 还有时间 ，就再放一次数据
    		eDate =LocalDate.parse(eDate).plusDays(1).toString();
    		if (eDate.compareTo(endDay.toString())<=0){
    			listInsDay.add(bDate+","+endDay.toString()); 
    		}
    		
    	}
    	
		return listInsDay;
    	 
	}
	
	public static List<String> getDay(String xBDate,String xEDate,int incDay )   {
		incDay = incDay-1;
		if (incDay<=0 || incDay>=365){
			return null;
		}
    	String billType ="3";
    	//记得增加判断  里面的每个日期是否在 1-31 里面 ，是否是日期格式是否 是升序
    	String fDate = String.valueOf(incDay);
    	String bDate =null;
    	String eDate =null;
    	String hbDate =xBDate;       //合同开始日期
    	String heDate =xEDate;
     
    	LocalDate day = null;
    	List<String> listInsDay = new ArrayList<String>();
    	 
    	LocalDate endDay= null;
    	LocalDate xTemp= null;
    	if ("3".equals(billType)){ //自定义天数
    		bDate=hbDate;
    		eDate = heDate;
    	 
    		day = LocalDate.parse(bDate);
    		endDay = LocalDate.parse(heDate);
    		boolean bFirst= false;
    		boolean bFind= false;
     
    		while (!day.isAfter(endDay) ) { //合同结束日期
    	     	 	 
    			if (!bFirst) { //第一次执行 就是当月第一天 ，第二次以后执行 就行 截止日期+1
    				bDate = day.toString().substring(0,8)+"01";	 
    				bFirst = true;
				}
    			  
    			eDate = bDate ;
    			xTemp = LocalDate.parse(eDate);
    			xTemp = xTemp.plusDays(incDay);
    			eDate = xTemp.toString(); 
				listInsDay.add(bDate+","+eDate);  
				bDate = LocalDate.parse(eDate).plusDays(1).toString();
				day = day.plusDays(incDay);
    		}
    		//最后再判断一次 如果最后一次的结束日期 到合同结束日期 还有时间 ，就再放一次数据
    		eDate =LocalDate.parse(eDate).plusDays(1).toString();
    		if (eDate.compareTo(endDay.toString())<=0){
    			listInsDay.add(bDate+","+endDay.toString());  
    		}
    		
    	}
    	//for (int i = 0; i < listInsDay.size(); i++) {
			//System.out.println(listInsDay.get(i) );	
		//}
		return listInsDay;
    	 
	}
	
	public static List<String> getDay(String xBDate,String xEDate )   {
		int incDay =1;
		if (incDay<=0 || incDay>=365){
			return null;
		}
    	String billType ="4";
    	//记得增加判断  里面的每个日期是否在 1-31 里面 ，是否是日期格式是否 是升序
    	String bDate =null;
    	String eDate =null;
    	String hbDate =xBDate;       //合同开始日期
    	String heDate =xEDate;
    	String curDate =null;
    	LocalDate day = null;
    	List<String> listInsDay = new ArrayList<String>();
    	 
    	LocalDate endDay= null;
    	LocalDate xTemp= null;
    	if ("4".equals(billType)){ //现结：按日结算，每日作为一个结算账期
    		bDate=hbDate;
    		eDate = heDate;
    	 
    		day = LocalDate.parse(bDate);
    		endDay = LocalDate.parse(heDate);
    		boolean bFirst= false;
    	 
    		while (!day.isAfter(endDay) ) { //合同结束日期
 
    			if (!bFirst) { //第一次执行 就是当月第一天 ，第二次以后执行 就行 截止日期+1
    				bDate = day.toString().substring(0,8)+"01";	 
    				bFirst = true;
				}
    			  
    			eDate = bDate ;
    		 
				listInsDay.add(bDate+","+eDate);  
				bDate = LocalDate.parse(eDate).plusDays(1).toString();
				day = day.plusDays(incDay);
    		}    	     		
    	}
		return listInsDay;         	 
	}

    @Override
    protected List<InsBean> prepareInsertData(DCP_BillDateAlterReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_BillDateAlterReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_BillDateAlterReq req) throws Exception {
        return null;
    }


    @Override
    protected boolean isVerifyFail(DCP_BillDateAlterReq req) throws Exception {
        // TODO Auto-generated method stub
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_BillDateAlterReq> getRequestType() {
        return new TypeToken<DCP_BillDateAlterReq>() {
        };
    }

    @Override
    protected DCP_BillDateAlterRes getResponseType() {
        return new DCP_BillDateAlterRes();
    }


    /**
     * 判断 信息时候已存在或重复
     */
    private String isRepeat(String... key) {
        String sql = null;
        sql = " SELECT * FROM DCP_BillDate WHERE EID='%s' AND BILLDATENO='%s'  ";
        sql = String.format(sql, key);
        return sql;
    }

}
	
