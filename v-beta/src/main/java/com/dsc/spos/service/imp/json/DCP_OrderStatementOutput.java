package com.dsc.spos.service.imp.json;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;

import com.dsc.spos.json.cust.req.DCP_OrderStatementOutputReq;
import com.dsc.spos.json.cust.res.DCP_OrderStatementOutputRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * 服务OrderStatementOutput ,对账导出
 * @author 08546
 */
public class DCP_OrderStatementOutput extends SPosBasicService<DCP_OrderStatementOutputReq, DCP_OrderStatementOutputRes> {

	Logger logger = LogManager.getLogger(DCP_OrderStatementOutput.class.getName());

	@Override
	protected boolean isVerifyFail(DCP_OrderStatementOutputReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false; 
		StringBuffer errMsg = new StringBuffer("");

		String accountStatus=req.getRequest().getAccountStatus();
		if(accountStatus!=null&&accountStatus.trim().equals("2")){

		}else{
			errMsg.append("未对账数据不得导出");
			//			isFail = true;
		}


		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderStatementOutputReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderStatementOutputReq>(){};
	}

	@Override
	protected DCP_OrderStatementOutputRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderStatementOutputRes();
	}

	@Override
	protected DCP_OrderStatementOutputRes processJson(DCP_OrderStatementOutputReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_OrderStatementOutputRes res = this.getResponse();
		try {
			String countSql =getCountSql(req);
			List<Map<String, Object>> countList=this.doQueryData(countSql, null);
			Number num = (Number) countList.get(0).get("COUNTNO");
			int count = num.intValue();
			if(count>0){
				res=saveExcel(req, count);
			}
			else{ //当查询导出格式列为空的时候
				res.setSuccess(false);
				res.setServiceStatus("100");
				res.setServiceDescription("查无导出资料");
			}

		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"对账导出执行失败",e);
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("导出执行失败：" + e.getMessage());
		}

		return res;
	}

	public DCP_OrderStatementOutputRes saveExcel (DCP_OrderStatementOutputReq req,int subCount) throws Exception {
		DCP_OrderStatementOutputRes res = this.getResponse();
		int subSize=1000;
		int subPageTotal = (subCount / subSize) + ((subCount % subSize > 0) ? 1 : 0);
		WritableWorkbook wwb = null;
		try{
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			String ecPlatformName="";
			String thirdType=req.getRequest().getThirdType();
			switch(thirdType){
			case "1":ecPlatformName="饿了么";break;
			case "2":ecPlatformName="美团";break;
			case "3":ecPlatformName="京东到家";break;
			case "10":ecPlatformName="微信";break;
			case "11":ecPlatformName="支付宝";break;
			default:ecPlatformName="未知来源";break;
			}
			String fileName = ecPlatformName+df.format(new Date())+".xls";
			// 传进来的文件名 + 绝对路径 ， 组合成文件完成路径

			String httpPath ="dualplay\\" + thirdType + "\\export\\" ;
			String filePath = System.getProperty("catalina.home")+"\\webapps\\"+httpPath;
			//临时文件名
			String tempFilePath=System.getProperty("catalina.home")+"\\webapps\\dualplay\\temp";
			// 暂且设置为 固定 EC + 平台名 + 文件名， 如果后期规划需要变更 平台编码，修改服务器文件夹名即可
			File file = new File(filePath);
			//如果文件夹不存在则创建 
			if(!file.exists()&& !file.isDirectory())      
			{				
				file.mkdirs();
			}			
			file=null;

			File tempFile = new File(tempFilePath);
			//如果文件夹不存在则创建 
			if(!tempFile.exists()&& !tempFile.isDirectory())      
			{				
				tempFile.mkdirs();
			}			
			tempFile=null;


			//1、创建工作簿
			wwb = Workbook.createWorkbook(new File(filePath+fileName));//采用临时文件形式生成数据

			WritableSheet wSheet = wwb.createSheet("Test",0);
			wSheet.setName(ecPlatformName);
			wSheet.getSettings().setDefaultColumnWidth(20);//列宽
			wSheet.setRowView(0, 700);
			WritableFont font1 = new WritableFont(WritableFont.createFont("宋体"),
					14,//字号
					WritableFont.BOLD,//粗体
					false,//斜体
					UnderlineStyle.NO_UNDERLINE// 下划线
					);  
			WritableCellFormat cellFormat1 = new WritableCellFormat(font1);  
			cellFormat1.setAlignment(Alignment.CENTRE);// 设置对齐方式 左对齐、右对齐、居中
			cellFormat1.setVerticalAlignment(VerticalAlignment.CENTRE);// 设置对齐方式 顶端、底端、垂直居中
			cellFormat1.setBackground(Colour.GRAY_25);// 设置单元格的背景颜色
			cellFormat1.setBorder(Border.ALL, BorderLineStyle.THIN);// 添加边框
			cellFormat1.setWrap(true);

			List<String> headNameList=new ArrayList<String>();
			headNameList.add("门店编号");
			headNameList.add("门店名称");
			headNameList.add("订单号");
			headNameList.add("订单类型");
			headNameList.add("平台应结金额");

			headNameList.add("订单中心应结金额");
			headNameList.add("POS应结金额");
			headNameList.add("用户支付金额");
			headNameList.add("订单原价");
			headNameList.add("退款金额");

			headNameList.add("对账结果");
			headNameList.add("差异类型1");
			headNameList.add("差异金额1");
			headNameList.add("差异类型2");
			headNameList.add("差异金额2");

			headNameList.add("账单日期");
			headNameList.add("差异原因");
			headNameList.add("POS单号");

			List<String> columnNameList=new ArrayList<String>();
			columnNameList.add("SHOPID");
			columnNameList.add("SHOPNAME");
			columnNameList.add("ORDERNO");
			columnNameList.add("ORDERTYPE");
			columnNameList.add("THIRDSETTLEMENTAMT");

			columnNameList.add("ORDERSETTLEMENTAMT");
			columnNameList.add("POSSETTLEMENTAMT");
			columnNameList.add("THIRDPAIDAMT");
			columnNameList.add("THIRDORDERAMT");
			columnNameList.add("THIRDREFUNDAMT");

			columnNameList.add("ACCOUNTSTATUS");
			columnNameList.add("DIVERSITYTYPE1");
			columnNameList.add("DIVERSITYAMT1");
			columnNameList.add("DIVERSITYTYPE2");
			columnNameList.add("DIVERSITYAMT2");

			columnNameList.add("THIRDSTATEMENTDATE");
			columnNameList.add("DIVERSITYREASON");
			columnNameList.add("POSSALENO");

			for(int i=0;i<headNameList.size();i++){
				Label headColumn = new Label(i,0,headNameList.get(i),cellFormat1);
				wSheet.addCell(headColumn);
			}
			wwb.write();
			wwb.close();

			if(headNameList.size()!=columnNameList.size()){
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "程序异常,excel头与excel数据列长度不匹配");
			}

			int rowNo=0;
			for(int i=1;i<=subPageTotal;i++){
				List<Map<String, Object>> returnList=getDatas(req, i, subSize);
				if(returnList!=null&&returnList.size()>0){
					WorkbookSettings wbSetting =new WorkbookSettings ();
					wbSetting.setGCDisabled(true);
					wbSetting.setUseTemporaryFileDuringWrite(true);
					wbSetting.setTemporaryFileDuringWriteDirectory(new File(tempFilePath));

					//读取已经生成的excel文件
					FileInputStream in = new FileInputStream(new File(filePath+fileName));
					Workbook wb= Workbook.getWorkbook(in); // 解析input流为excel对象
					//采用临时文件形式创建新对象，并且新对象会包含之前生成的excel对象
					wwb= Workbook.createWorkbook(new File(filePath+fileName), wb, wbSetting);

					WritableSheet wSheetNew=wwb.getSheet(0);
					for(Map<String, Object> returnMap:returnList){
						rowNo++;

						for (int j = 0; j < headNameList.size(); j++) {
							String columnName=columnNameList.get(j);
							String value=returnMap.get(columnName)==null?"":returnMap.get(columnName).toString();
							//订单类型	Y	ORDERTYPE	NVARCHAR2(10)	NOT NULL	1-正向；2-负向
							if("ORDERTYPE".equals(columnName)){
								switch(value){
								case "1":value="正向";break;
								case "2":value="负向";break;
								default:value="未知状态";break;
								}
							}
							//对账结果		ACCOUNTSTATUS	NVARCHAR2(10)		1-未对账；2-已对账
							if("ACCOUNTSTATUS".equals(columnName)){
								switch(value){
								case "1":value="未对账";break;
								case "2":value="已对账";break;
								default:value="未知状态";break;
								}
							}
							//差异类型 DIVERSITYTYPE1 外卖和订单中心 1-无差异 2-订单中心无 3-金额不匹配
							if("DIVERSITYTYPE1".equals(columnName)){
								switch(value){
								case "1":value="无差异";break;
								case "2":value="订单中心无";break;
								case "3":value="金额不匹配";break;
								default:value="未知状态";break;
								}
							}
							//差异类型 DIVERSITYTYPE2 订单中心和POS 1-无差异 2-全部无 3-订单中心无 4-POS无 5-金额不匹配
							if("DIVERSITYTYPE2".equals(columnName)){
								switch(value){
								case "1":value="无差异";break;
								case "2":value="全部无";break;
								case "3":value="订单中心无";break;
								case "4":value="POS无";break;
								case "5":value="金额不匹配";break;
								default:value="未知状态";break;
								}
							}
							Label column = new Label(j,rowNo,value);
							wSheetNew.addCell(column);
						}
					}
					wwb.write();
					wwb.close();
				}

			}


			//			System.gc();
			res.setExcelFileName(fileName);
			res.setFilePath(httpPath);
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("导出成功");
		}catch(Exception e){
			// TODO Auto-generated catch block
			throw e;
		}finally{
			try{
				wwb.close();
				//				System.gc();
			}catch(Exception e){

			}
		}
		return res;
	}

	public List<Map<String, Object>> getDatas(DCP_OrderStatementOutputReq req,int startPage,int pgSize) throws Exception {
		//計算起啟位置
		int startRow = ((startPage - 1) * pgSize);
		startRow = ((startPage - 1) == 0) ? startRow-- : startRow; //是否為第一頁第一筆
		startRow = (startRow == -1) ? 0 : startRow; //表示從第一筆開始取資料
		int endRow=startRow+pgSize;
		String mainSql=getMainSql(req, startRow, endRow);
		List<Map<String, Object>> returnList=null;
		try{
			returnList = this.doQueryData(mainSql.toString(), null);
		}catch(Exception e){

		}
		return returnList;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_OrderStatementOutputReq req) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append(" AND T.EID ='" + req.geteId() + "' ");
		//对账类型
		String thirdType = req.getRequest().getThirdType();
		if(thirdType!=null&&!thirdType.trim().isEmpty()&&!thirdType.equals("ALL")){
			sqlbuf.append(" AND T.THIRDTYPE ='" + thirdType + "' ");
		}
		//开始日期
		String startDate=req.getRequest().getStartDate();
		if(startDate!=null&&!startDate.trim().isEmpty()){
			sqlbuf.append(" AND T.THIRDSTATEMENTDATE >='" + startDate + "' ");
		}
		//结束日期
		String endDate=req.getRequest().getEndDate();
		if(endDate!=null&&!endDate.trim().isEmpty()){
			sqlbuf.append(" AND T.THIRDSTATEMENTDATE <='" + endDate + "' ");
		}
		//门店
		String shopId=req.getShopId();
		if(shopId!=null&&!shopId.trim().isEmpty()){
			sqlbuf.append(" AND T.SHOPID ='" + shopId + "' ");
		}
		//关键字
		String keyTxt=req.getRequest().getKeyTxt();
		if(keyTxt!=null&&!keyTxt.trim().isEmpty()){
			sqlbuf.append(" AND (UPPER(T.ORDERNO) like UPPER('%%"+keyTxt+"%%'))");
		}
		//差异类型1
		String diversityType1 = req.getRequest().getDiversityType1();
		if(diversityType1!=null&&!diversityType1.trim().isEmpty()&&!diversityType1.equals("ALL")){
			sqlbuf.append(" AND T.DIVERSITYTYPE1 ='" + diversityType1 + "' ");
		}
		//差异类型2
		String diversityType2 = req.getRequest().getDiversityType2();
		if(diversityType2!=null&&!diversityType2.trim().isEmpty()&&!diversityType2.equals("ALL")){
			sqlbuf.append(" AND T.DIVERSITYTYPE2 ='" + diversityType2 + "' ");
		}
		//对账结果
		String accountStatus=req.getRequest().getAccountStatus();
		if(accountStatus!=null&&!accountStatus.trim().isEmpty()&&!accountStatus.equals("ALL")){
			sqlbuf.append(" AND T.ACCOUNTSTATUS ='" + accountStatus + "' ");
		}

		return sqlbuf.toString();
	}

	public String getCountSql(DCP_OrderStatementOutputReq req)throws Exception {
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("SELECT count(1) as COUNTNO FROM OC_STATEMENT T WHERE 1=1 ");
		sqlbuf.append(getQuerySql(req));
		return sqlbuf.toString();

	}

	public String getMainSql(DCP_OrderStatementOutputReq req,int startRow,int endRow)throws Exception {
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("SELECT * FROM ");
		sqlbuf.append(" ( ");
		sqlbuf.append(" SELECT ROWNUM AS RN, T.* FROM OC_STATEMENT T ");
		sqlbuf.append(" WHERE 1=1 ");
		sqlbuf.append(getQuerySql(req));
		sqlbuf.append(" AND ROWNUM <="+endRow+" ");
		sqlbuf.append(" ) TA ");
		sqlbuf.append(" WHERE TA.RN > "+startRow);
		return sqlbuf.toString();
	}

	public static <T> T map2Bean(Map<String, Object> source, Class<T> instance) {
		try {
			T object = instance.newInstance();
			Field[] fields = object.getClass().getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				if(field.getType() == List.class){
					field.set(object, null);
				}else{
					field.set(object, source.get(field.getName().toUpperCase())==null?null:source.get(field.getName().toUpperCase()).toString());
				}
			}
			return object;
		} catch (InstantiationException | IllegalAccessException e) {

		}
		return null;
	}



}
