package com.dsc.spos.service.imp.json;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_WarningMonitoringExportReq;
import com.dsc.spos.json.cust.req.DCP_WarningMonitoringExportReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_WarningMonitoringExportRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
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

public class DCP_WarningMonitoringExport extends SPosBasicService<DCP_WarningMonitoringExportReq,DCP_WarningMonitoringExportRes> {


	private int MaxExportSize = 20000;//导出excel最多记录数
	private int subSize=2000;//每2000条数据，写入一次excel，防止一次数据过多，造成内存溢出

	@Override
	protected boolean isVerifyFail(DCP_WarningMonitoringExportReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；  
		level1Elm requestModel =	req.getRequest();
		if(requestModel==null)
		{
			errCt++;
			errMsg.append("请求节点request不存在, ");
			isFail = true;
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		if(Check.Null(requestModel.getWarningType()))
		{
			errCt++;
			errMsg.append("监控类型warningType不能为空, ");
			isFail = true;

		}


		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}    
		return isFail;
	}

	@Override
	protected TypeToken<DCP_WarningMonitoringExportReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_WarningMonitoringExportReq>(){};
	}

	@Override
	protected DCP_WarningMonitoringExportRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_WarningMonitoringExportRes();
	}

	@Override
	protected DCP_WarningMonitoringExportRes processJson(DCP_WarningMonitoringExportReq req) throws Exception {
		// TODO Auto-generated method stub

		DCP_WarningMonitoringExportRes res = this.getResponse();
		res.setDatas(res.new level1Elm());

		level1Elm requestModel =	req.getRequest();		
		String warningType = requestModel.getWarningType();//枚举: null：全部,order：零售单,point：会员积分,card：储值卡
		String warningTypeName= getWarningTypeName(warningType);
		String sql ="";
		sql = this.getCountSql(req);
		int totalRecords = 0;	//明细记录总数	

		List<Map<String, Object>> getQData_Count = this.doQueryData(sql, null);
		if (getQData_Count != null && getQData_Count.isEmpty() == false) 
		{ 			
			Map<String, Object> oneData_Count = getQData_Count.get(0);
			String num = oneData_Count.get("NUM").toString();
			totalRecords=Integer.parseInt(num);
		}

		if(totalRecords ==0)
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("没有数据,无需导出！");
			return res;
		}
		if(totalRecords > MaxExportSize)
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("数据量过大！建议重新选择查询条件分批导出！");
			return res;
		}

		String excelName="监控导出";		
		excelName = warningTypeName + excelName;	

		try 
		{
			WritableWorkbook wwb = null;
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			//如修改文件名，需要注意删除历史文件的代码部分，即调用delFilesByPath方法
			String fileName = df.format(new Date())+excelName+".xls";

			// 传进来的文件名 + 绝对路径 ， 组合成文件完成路径
			String excelFilePath = "\\excelExport\\";
			String filePath = System.getProperty("catalina.home")+"\\webapps\\goodsimages"+excelFilePath;//放到图片的目录下
			//临时文件名
			String tempFilePath=System.getProperty("catalina.home")+"\\webapps\\goodsimages\\excelTemp\\";

			//删除历史导出文件
			delFilesByPath(filePath,df.format(new Date()),excelName+".xls");

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
			wwb = Workbook.createWorkbook(new File(filePath+fileName));
			//完整路径
			String fullPath=filePath+fileName;


			WritableSheet wSheet2 = wwb.createSheet("Test",0);
			wSheet2.setName("TEST1");
			wSheet2.getSettings().setDefaultColumnWidth(20);//列宽
			wSheet2.setRowView(0, 700);
			WritableFont font1 = new WritableFont(WritableFont.createFont("宋体"),
					12,//字号
					WritableFont.BOLD,//粗体
					false,//斜体
					UnderlineStyle.NO_UNDERLINE// 下划线
					);  
			WritableCellFormat cellFormat1 = new WritableCellFormat(font1);  
			cellFormat1.setAlignment(Alignment.CENTRE);// 设置对齐方式 左对齐、右对齐、居中
			cellFormat1.setVerticalAlignment(VerticalAlignment.CENTRE);// 设置对齐方式 顶端、底端、垂直居中
			cellFormat1.setBackground(Colour.WHITE);// 设置单元格的背景颜色
			cellFormat1.setBorder(Border.ALL, BorderLineStyle.THIN);// 添加边框
			cellFormat1.setWrap(true);

			wwb.write();
			wwb.close();
			saveHeadInformation(req, cellFormat1, fullPath, tempFilePath, 0);
			saveDetailInformation(req, cellFormat1, fullPath, tempFilePath, 1, subSize,totalRecords);

			DCP_WarningMonitoringExportRes.level1Elm oneLv1 = res.new level1Elm(); 
			oneLv1.setExcelFileName(fileName);
			oneLv1.setFilePath(excelFilePath.replace("\\", "//")+fileName);

			res.setDatas(oneLv1);
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("导出成功");


		} 
		catch (Exception e) 
		{	
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("导出失败！异常:"+e.getMessage());	
		}





		return res;
	}

	/**
	 * 
	 * @param req 请求参数
	 * @param cellFormat1 excel格式
	 * @param fullPath 导出execl完整路径
	 * @param tempFilePath 临时导出excel路径
	 * @param sheetNo sheet索引
	 * @throws Exception
	 */
	private void saveHeadInformation(DCP_WarningMonitoringExportReq req,WritableCellFormat cellFormat1, String fullPath, String tempFilePath,int sheetNo) throws Exception
	{
		level1Elm requestModel =	req.getRequest();
		String warningNo = requestModel.getWarningNo()==null?"":requestModel.getWarningNo();//监控编号
		String orderNo = requestModel.getOrderNo()==null?"":requestModel.getOrderNo();//销售单号
		String shopId = requestModel.getShopId()==null?"":requestModel.getShopId();
		String memberId = requestModel.getMemberId();
		String cardTypeId = requestModel.getCardTypeId();
		String cardNo = requestModel.getCardNo();
		String beginDate = requestModel.getBeginDate()==null?"":requestModel.getBeginDate();
		String endDate = requestModel.getEndDate()==null?"":requestModel.getEndDate();
		String warningType = requestModel.getWarningType();//枚举: null：全部,order：零售单,point：会员积分,card：储值卡
		String warningTypeName= getWarningTypeName(warningType);
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sheetName=warningTypeName+"-汇总";
		//1、创建工作簿
		WritableWorkbook wwb = null;
		try 
		{

			String sql = getHeadSql(req);

			List<Map<String, Object>> result = this.doQueryData(sql, null);

			// no data found
			if (result == null || result.isEmpty()) {
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, "未查询到传入资料！");
			}


			WorkbookSettings wbSetting =new WorkbookSettings ();
			wbSetting.setGCDisabled(true);
			wbSetting.setUseTemporaryFileDuringWrite(true);
			wbSetting.setTemporaryFileDuringWriteDirectory(new File(tempFilePath));

			//读取已经生成的excel文件
			FileInputStream in = new FileInputStream(new File(fullPath));
			Workbook wb= Workbook.getWorkbook(in); // 解析input流为excel对象
			//采用临时文件形式创建新对象，并且新对象会包含之前生成的excel对象
			wwb= Workbook.createWorkbook(new File(fullPath), wb, wbSetting);
			WritableSheet wSheet=wwb.getSheet("TEST1");
			wSheet.setName(sheetName);
			wSheet.getSettings().setDefaultColumnWidth(20);//列宽
			wSheet.setRowView(0, 700);


			//固定信息
			//第1行 监控类型  "";监控编号 "";门店 "";
			List<String> List1=new ArrayList<String>();
			List1.add("监控类型:");
			List1.add(warningTypeName);
			List1.add("监控编号:");
			List1.add(warningNo);
			if(warningType.toLowerCase().equals("order"))
			{
				List1.add("门店:");
				List1.add(shopId);				
			}

			for(int i=0;i<List1.size();i++){
				Label headColumn = new Label(i,0,List1.get(i),cellFormat1);
				wSheet.addCell(headColumn);
			}
			//第2行 时间范围 
			List<String> List2=new ArrayList<String>();
			List2.add("时间范围:");
			List2.add(beginDate);
			List2.add(endDate);
			for(int i=0;i<List2.size();i++){
				Label headColumn = new Label(i,1,List2.get(i),cellFormat1);
				wSheet.addCell(headColumn);
			}
			//第3行 列头 
			List<String> List3=new ArrayList<String>();
			if(warningType.toLowerCase().equals("order"))
			{
				List3.add("序号");
				List3.add("日期");
				List3.add("门店");
				List3.add("异常订单数");
				List3.add("金额");
				List3.add("监控编号");
				List3.add("监控名称");
				List3.add("监控项");			
			}
			else if(warningType.toLowerCase().equals("point"))
			{
				List3.add("序号");
				List3.add("日期");
				List3.add("会员");
				List3.add("订单数");
				List3.add("积分次数");
				List3.add("积分");
				List3.add("监控编号");
				List3.add("监控名称");
				List3.add("监控项");			
			}
			else if(warningType.toLowerCase().equals("card"))
			{
				List3.add("序号");
				List3.add("日期");
				List3.add("卡类型");
				List3.add("卡号");
				List3.add("所属会员号");
				List3.add("消费次数");
				List3.add("消费金额");
				List3.add("监控编号");
				List3.add("监控名称");
				List3.add("监控项");				
			}


			for(int i=0;i<List3.size();i++){
				Label headColumn = new Label(i,2,List3.get(i),cellFormat1);
				wSheet.addCell(headColumn);
			}

			//第4行 开始数据
			int row = 3;
			int seriNo = 1;//序号
			for (Map<String, Object> mapHead : result) 
			{
				String billId_head = mapHead.get("ID").toString();//日志单号
				String billNo_head = mapHead.get("BILLNO").toString();										
				String billName =  mapHead.get("BILLNAME").toString();//值在明细有
				String warningItemDescription = mapHead.get("WARNINGITEMDESCRIPTION").toString();//值在明细有
				String warningDate = mapHead.get("PUSHDATE").toString();//值在明细有

				List<String> List4=new ArrayList<String>();

				if(warningType.toLowerCase().equals("order"))
				{
					String tot_amt = mapHead.get("TOT_AMT").toString()==null?"0":mapHead.get("TOT_AMT").toString();
					String tot_count = mapHead.get("TOT_COUNT").toString()==null?"0":mapHead.get("TOT_COUNT").toString();							
					String	shopId_head = mapHead.get("SHOPID").toString();
					String  shopName = mapHead.get("SHOPNAME").toString();				
					/**
					 * List3.add("序号");
						List3.add("日期");
						List3.add("门店");
						List3.add("异常订单数");
						List3.add("金额");
						List3.add("监控编号");
						List3.add("监控名称");
						List3.add("监控项");
					 */
					List4.add(seriNo+"");
					List4.add(warningDate);
					List4.add(shopId_head+"("+shopName+")");
					List4.add(tot_count+"");
					List4.add(tot_amt+"");
					List4.add(billNo_head);
					List4.add(billName);
					List4.add(warningItemDescription);			
				}
				else if(warningType.toLowerCase().equals("point"))
				{
					String memberId_head = mapHead.get("MEMBERID").toString();		
					String tot_count = mapHead.get("TOT_COUNT").toString()==null?"0":mapHead.get("TOT_COUNT").toString();		
					String tot_pointQty = mapHead.get("TOT_POINTQTY").toString()==null?"0":mapHead.get("TOT_POINTQTY").toString();

					/**
					 * List3.add("序号");
						List3.add("日期");
						List3.add("会员");
						List3.add("订单数");
						List3.add("积分次数");
						List3.add("积分");
						List3.add("监控编号");
						List3.add("监控名称");
						List3.add("监控项");		
					 */
					List4.add(seriNo+"");
					List4.add(warningDate);
					List4.add(memberId_head);
					List4.add(tot_count+"");
					List4.add(tot_count+"");
					List4.add(tot_pointQty+"");
					List4.add(billNo_head);
					List4.add(billName);
					List4.add(warningItemDescription);			
				}
				else if(warningType.toLowerCase().equals("card"))
				{
					String cardNo_head = mapHead.get("CARDNO").toString();				
					String tot_cardPayAmt = mapHead.get("TOT_CARDPAYAMT").toString()==null?"0":mapHead.get("TOT_CARDPAYAMT").toString();
					String tot_count = mapHead.get("TOT_COUNT").toString()==null?"0":mapHead.get("TOT_COUNT").toString();					
					String cardTypeId_db = mapHead.get("CARDTYPEID").toString()==null?"":mapHead.get("CARDTYPEID").toString();
					String cardTypeName = mapHead.get("CARDTYPENAME").toString()==null?"":mapHead.get("CARDTYPENAME").toString();
					String memberId_detail = mapHead.get("MEMBERID").toString();
					//String memberName_detail =  mapDetail.get("MEMBERNAME").toString();
					/**
					 *List3.add("序号");
					List3.add("日期");
					List3.add("卡类型");
					List3.add("卡号");
					List3.add("所属会员号");
					List3.add("消费次数");
					List3.add("消费金额");
					List3.add("监控编号");
					List3.add("监控名称");
					List3.add("监控项");	
					 */
					List4.add(seriNo+"");
					List4.add(warningDate);
					List4.add(cardTypeId_db);
					List4.add(cardNo_head);
					List4.add(memberId_detail);
					List4.add(tot_count+"");
					List4.add(tot_cardPayAmt+"");
					List4.add(billNo_head);
					List4.add(billName);
					List4.add(warningItemDescription);			
				}





				for(int i=0;i<List4.size();i++){
					Label headColumn = new Label(i,row,List4.get(i),cellFormat1);
					wSheet.addCell(headColumn);
				}

				row++;
				seriNo++;
			}

			wwb.write();
			wwb.close();
		} 
		catch (Exception e) 
		{
			throw e;

		}
		finally 
		{
			try{
				wwb.close();
			}catch(Exception e){

			}


		}

	}

	/**
	 * 写明细
	 * @param req
	 * @param cellFormat1
	 * @param fullPath
	 * @param tempFilePath
	 * @param sheetNo
	 * @param subSize
	 * @param totalRecords
	 * @throws Exception
	 */
	private void saveDetailInformation(DCP_WarningMonitoringExportReq req, WritableCellFormat cellFormat1, String fullPath,
			String tempFilePath, int sheetNo, int subSize,int totalRecords) throws Exception 
	{
		level1Elm requestModel =	req.getRequest();
		String warningNo = requestModel.getWarningNo()==null?"":requestModel.getWarningNo();//监控编号
		String orderNo = requestModel.getOrderNo()==null?"":requestModel.getOrderNo();//销售单号
		String shopId = requestModel.getShopId()==null?"":requestModel.getShopId();
		String memberId = requestModel.getMemberId();
		String cardTypeId = requestModel.getCardTypeId();
		String cardNo = requestModel.getCardNo();
		String beginDate = requestModel.getBeginDate()==null?"":requestModel.getBeginDate();
		String endDate = requestModel.getEndDate()==null?"":requestModel.getEndDate();
		String warningType = requestModel.getWarningType();//枚举: null：全部,order：零售单,point：会员积分,card：储值卡
		String warningTypeName= getWarningTypeName(warningType);
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sheetName=warningTypeName+"-明细";
		//1、创建工作簿
		WritableWorkbook wwb = null;
		try 
		{		
			WorkbookSettings wbSetting =new WorkbookSettings ();
			wbSetting.setGCDisabled(true);
			wbSetting.setUseTemporaryFileDuringWrite(true);
			wbSetting.setTemporaryFileDuringWriteDirectory(new File(tempFilePath));

			//读取已经生成的excel文件
			FileInputStream in = new FileInputStream(new File(fullPath));
			Workbook wb= Workbook.getWorkbook(in); // 解析input流为excel对象
			//采用临时文件形式创建新对象，并且新对象会包含之前生成的excel对象
			wwb= Workbook.createWorkbook(new File(fullPath), wb, wbSetting);
			WritableSheet wSheet=wwb.createSheet("Test",sheetNo);
			wSheet.setName(sheetName);
			wSheet.getSettings().setDefaultColumnWidth(20);//列宽
			wSheet.setRowView(0, 700);

			//固定信息
			//第1行 监控类型  "";监控编号 "";门店 "";
			List<String> List1=new ArrayList<String>();
			List1.add("监控类型:");
			List1.add(warningTypeName);
			List1.add("监控编号:");
			List1.add(warningNo);
			if(warningType.toLowerCase().equals("order"))
			{
				List1.add("门店:");
				List1.add(shopId);				
			}

			for(int i=0;i<List1.size();i++){
				Label headColumn = new Label(i,0,List1.get(i),cellFormat1);
				wSheet.addCell(headColumn);
			}
			//第2行 时间范围 
			List<String> List2=new ArrayList<String>();
			List2.add("时间范围:");
			List2.add(beginDate);
			List2.add(endDate);
			for(int i=0;i<List2.size();i++){
				Label headColumn = new Label(i,1,List2.get(i),cellFormat1);
				wSheet.addCell(headColumn);
			}
			//第3行 列头 
			List<String> List3=new ArrayList<String>();
			if(warningType.toLowerCase().equals("order"))
			{
				List3.add("序号");
				List3.add("单号");
				List3.add("营业日期");
				List3.add("联系人");
				List3.add("会员号");
				List3.add("会员卡号");
				List3.add("下订门店");
				List3.add("支付金额");
				List3.add("下订时间");
				List3.add("监控编号");
				List3.add("监控名称");
				List3.add("监控项");			
			}
			else if(warningType.toLowerCase().equals("point"))
			{
				List3.add("序号");
				List3.add("单号");
				List3.add("营业日期");
				List3.add("联系人");
				List3.add("会员号");
				List3.add("会员卡号");
				List3.add("下订门店");
				List3.add("支付金额");
				List3.add("积分");
				List3.add("下订时间");
				List3.add("监控编号");
				List3.add("监控名称");
				List3.add("监控项");				
			}
			else if(warningType.toLowerCase().equals("card"))
			{
				List3.add("序号");
				List3.add("单号");
				List3.add("营业日期");
				List3.add("联系人");
				List3.add("会员号");
				List3.add("下订门店");
				List3.add("支付金额");
				List3.add("支付卡类型");
				List3.add("支付卡号");
				List3.add("下订时间");
				List3.add("监控编号");
				List3.add("监控名称");
				List3.add("监控项");						
			}


			for(int i=0;i<List3.size();i++){
				Label headColumn = new Label(i,2,List3.get(i),cellFormat1);
				wSheet.addCell(headColumn);
			}

			wwb.write();
			wwb.close();

			//第4行 开始数据
			int row = 3;
			int seriNo = 1;//序号
			//导入的时候，防止一次加载内存数据过大，分页查下查询，
			int totalPages = 0;
			totalPages = totalRecords / subSize;
			totalPages = (totalRecords % subSize > 0) ? totalPages + 1 : totalPages;

			for(int page=1;page<=totalPages;page++)
			{
				int startRow = (page-1)*subSize;
				int endRow = startRow+subSize;

				String sql_page = this.getDetailSql(req, startRow, endRow);
				List<Map<String, Object>> getQDataDetail=this.doQueryData(sql_page,null);
				if(getQDataDetail==null||getQDataDetail.isEmpty())
				{
					continue;
				}

				WorkbookSettings wbSettingNew =new WorkbookSettings ();
				wbSettingNew.setGCDisabled(true);
				wbSettingNew.setUseTemporaryFileDuringWrite(true);
				wbSettingNew.setTemporaryFileDuringWriteDirectory(new File(tempFilePath));

				//读取已经生成的excel文件
				FileInputStream newIn = new FileInputStream(new File(fullPath));
				Workbook wbNew= Workbook.getWorkbook(newIn); // 解析input流为excel对象
				//采用临时文件形式创建新对象，并且新对象会包含之前生成的excel对象
				wwb= Workbook.createWorkbook(new File(fullPath), wbNew, wbSettingNew);

				WritableSheet wSheetNew=wwb.getSheet(sheetName);


				for (Map<String, Object> mapDetail : getQDataDetail) 
				{
					String billId = mapDetail.get("ID").toString();//日志单号
					String billNo = mapDetail.get("BILLNO").toString();										
					String billName =  mapDetail.get("BILLNAME").toString();//值在明细有
					String warningItemDescription = mapDetail.get("WARNINGITEMDESCRIPTION").toString();//值在明细有
					String tot_amt = mapDetail.get("TOT_AMT").toString()==null?"0":mapDetail.get("TOT_AMT").toString();
					//String tot_count = mapDetail.get("TOT_COUNT").toString()==null?"0":mapDetail.get("TOT_COUNT").toString();
					String pointQty = mapDetail.get("POINTQTY").toString()==null?"0":mapDetail.get("POINTQTY").toString();
					String cardPayAmt = mapDetail.get("CARDPAYAMT").toString()==null?"0":mapDetail.get("CARDPAYAMT").toString();
					String shopId_detail = mapDetail.get("SHOPID").toString();
					String shopName =  mapDetail.get("SHOPNAME").toString();
					String saleNo = mapDetail.get("ORDERNO").toString();
					String contMan = mapDetail.get("CONTMAN").toString(); 
					String memberId_detail = mapDetail.get("MEMBERID").toString();		
					String cardNo_detail= mapDetail.get("CARDNO").toString();																		
					String cardTypeId_detail = mapDetail.get("CARDTYPEID").toString()==null?"":mapDetail.get("CARDTYPEID").toString();
					String cardTypeName = mapDetail.get("CARDTYPENAME").toString()==null?"":mapDetail.get("CARDTYPENAME").toString();
					String payAmt = mapDetail.get("PAYAMT").toString()==null?"0":mapDetail.get("PAYAMT").toString();

					String create_datetime = mapDetail.get("CREATE_DATETIME").toString();
					if(create_datetime!=null&&create_datetime.length()==14)
					{
						try 
						{
							String year = create_datetime.substring(0, 4);
							String month = create_datetime.substring(4, 6);
							String day = create_datetime.substring(6, 8);
							String hour = create_datetime.substring(8, 10);
							String minute = create_datetime.substring(10, 12);
							String second = create_datetime.substring(12, 14);									
							create_datetime = year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second;								
						} 
						catch (Exception e) 
						{

						}																
					}

					List<String> List4=new ArrayList<String>();

					if(warningType.toLowerCase().equals("order"))
					{
						/*List3.add("序号");
						List3.add("单号");
						List3.add("营业日期");
						List3.add("联系人");
						List3.add("会员号");
						List3.add("会员卡号");
						List3.add("下订门店");
						List3.add("支付金额");
						List3.add("下订时间");
						List3.add("监控编号");
						List3.add("监控名称");
						List3.add("监控项");		*/	
						List4.add(seriNo+"");			
						List4.add(saleNo);
						List4.add(mapDetail.get("BDATE").toString());
						List4.add(contMan);
						List4.add(memberId_detail);
						List4.add(cardNo_detail);
						List4.add(shopId_detail+"("+shopName+")");
						List4.add(payAmt+"");
						List4.add(create_datetime);
						List4.add(billNo);
						List4.add(billName);
						List4.add(warningItemDescription);		

					}
					else if(warningType.toLowerCase().equals("point"))
					{
						/*List3.add("序号");
						List3.add("单号");
						List3.add("营业日期");
						List3.add("联系人");
						List3.add("会员号");
						List3.add("会员卡号");
						List3.add("下订门店");
						List3.add("支付金额");
						List3.add("积分");
						List3.add("下订时间");
						List3.add("监控编号");
						List3.add("监控名称");
						List3.add("监控项");	*/			
						List4.add(seriNo+"");			
						List4.add(saleNo);
						List4.add(mapDetail.get("BDATE").toString());
						List4.add(contMan);
						List4.add(memberId_detail);
						List4.add(cardNo_detail);
						List4.add(shopId_detail+"("+shopName+")");
						List4.add(payAmt+"");
						List4.add(pointQty+"");
						List4.add(create_datetime);
						List4.add(billNo);
						List4.add(billName);
						List4.add(warningItemDescription);	
					}
					else if(warningType.toLowerCase().equals("card"))
					{
						/*List3.add("序号");
						List3.add("单号");
						List3.add("营业日期");
						List3.add("联系人");
						List3.add("会员号");				
						List3.add("下订门店");
						List3.add("支付金额");
						List3.add("支付卡类型");
						List3.add("支付卡号");
						List3.add("下订时间");
						List3.add("监控编号");
						List3.add("监控名称");
						List3.add("监控项");	*/	
						List4.add(seriNo+"");			
						List4.add(saleNo);
						List4.add(mapDetail.get("BDATE").toString());
						List4.add(contMan);
						List4.add(memberId_detail);
						List4.add(shopId_detail+"("+shopName+")");
						List4.add(payAmt+"");
						if(cardTypeName==null||cardTypeName.trim().isEmpty())
						{
							List4.add(cardTypeId_detail);							
						}
						else
						{
							List4.add(cardTypeId_detail+"("+cardTypeName+")");							
						}
						List4.add(cardNo_detail);												
						List4.add(create_datetime);
						List4.add(billNo);
						List4.add(billName);
						List4.add(warningItemDescription);
					}

					for(int i=0;i<List4.size();i++){
						Label headColumn = new Label(i,row,List4.get(i),cellFormat1);
						wSheetNew.addCell(headColumn);
					}

					row++;
					seriNo++;				
				}

				wwb.write();
				wwb.close();



			}		

		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			throw e;
		}
		finally 
		{
			try{
				wwb.close();
			}catch(Exception e){

			}


		}


	}



	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_WarningMonitoringExportReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	private String getCountSql(DCP_WarningMonitoringExportReq req) throws Exception {

		String eId = req.geteId();
		String langType = req.getLangType();
		level1Elm requestModel =	req.getRequest();		
		String warningType = requestModel.getWarningType();//枚举: null：全部,order：零售单,point：会员积分,card：储值卡
		String warningNo = requestModel.getWarningNo();//监控编号
		String orderNo = requestModel.getOrderNo();//销售单号
		String shopId = requestModel.getShopId();
		String memberId = requestModel.getMemberId();
		String cardTypeId = requestModel.getCardTypeId();
		String cardNo = requestModel.getCardNo();
		String beginDate = requestModel.getBeginDate();
		String endDate = requestModel.getEndDate();
		String sql= "";
		StringBuffer sqlbuf=new StringBuffer("");

		sqlbuf.append("select * from (");
		sqlbuf.append(" select count(*)  as num from dcp_warninglog_detail  a "
				+ " inner join dcp_warninglog b  on a.eid=b.eid and a.id=b.id ");
		sqlbuf.append("  where a.eid='"+eId+"'");

		sqlbuf.append(" and b.warningtype='"+warningType+"'");

		if (beginDate != null && beginDate.trim().isEmpty() == false)
		{
			String beginDate_format = beginDate+" 00:00:00";		
			sqlbuf.append(" and b.pushtime>='"+beginDate_format+"'");

		}
		if (endDate != null && endDate.trim().isEmpty() == false)
		{
			String endDate_format = endDate+" 23:59:59";		
			sqlbuf.append(" and b.pushtime<='"+endDate_format+"'");

		}	
		if (warningNo != null && warningNo.trim().isEmpty() == false)
		{
			sqlbuf.append(" and a.billno='"+warningNo+"'");			
		}
		if (orderNo != null && orderNo.trim().isEmpty() == false)
		{
			sqlbuf.append(" and a.orderNo='"+orderNo+"'");

		}			
		if (shopId != null && shopId.trim().isEmpty() == false)
		{
			sqlbuf.append(" and a.shopId='"+shopId+"'");			
		}
		if (memberId != null && memberId.trim().isEmpty() == false)
		{
			sqlbuf.append(" and a.memberId='"+memberId+"'");			
		}
		if (cardTypeId != null && cardTypeId.trim().isEmpty() == false)
		{
			sqlbuf.append(" and a.cardTypeId='"+cardTypeId+"'");			
		}
		if (cardNo != null && cardNo.trim().isEmpty() == false)
		{
			sqlbuf.append(" and a.cardNo='"+cardNo+"'");			
		}
		sqlbuf.append(")");

		return sqlbuf.toString();
	}


	private String getHeadSql(DCP_WarningMonitoringExportReq req) throws Exception {

		String eId = req.geteId();
		String langType = req.getLangType();
		level1Elm requestModel =	req.getRequest();		
		String warningType = requestModel.getWarningType();//枚举: null：全部,order：零售单,point：会员积分,card：储值卡
		String warningNo = requestModel.getWarningNo();//监控编号
		String orderNo = requestModel.getOrderNo();//销售单号
		String shopId = requestModel.getShopId();
		String memberId = requestModel.getMemberId();
		String cardTypeId = requestModel.getCardTypeId();
		String cardNo = requestModel.getCardNo();
		String beginDate = requestModel.getBeginDate();
		String endDate = requestModel.getEndDate();
		String sql= "";
		StringBuffer sqlbuf=new StringBuffer("");

		sqlbuf.append("select * from (");	
		sqlbuf.append(" select  aa.* from (");	
		if(warningType.toLowerCase().equals("order"))
		{
			//不同类型分组不同，后面group by 也要修改
			sqlbuf.append(" select id,billno,warningtype,shopid,SHOPNAME,pushdate,BILLNAME,Warningitemdescription,sum(tot_amt) as tot_amt ,count(*) as tot_count  from (");

		}
		else if (warningType.toLowerCase().equals("point")) 
		{
			sqlbuf.append(" select id,billno,warningtype,memberId,pushdate,BILLNAME,Warningitemdescription,sum(pointqty) as tot_pointqty ,count(*) as tot_count  from (");

		}
		else if (warningType.toLowerCase().equals("card")) 
		{
			sqlbuf.append(" select id,billno,warningtype,cardno,cardtypeid,cardtypename,memberid,pushdate,BILLNAME,Warningitemdescription,sum(cardpayamt) as tot_cardpayamt ,count(*) as tot_count  from (");
		}
		else 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "监控类型warningType("+warningType+")无效,请检查！");	
		}

		sqlbuf.append(" select a.*,b.warningtype,b.pushtime, substr(b.pushtime,0,10) as pushdate,B.BILLNAME,B.Warningitemdescription from dcp_warninglog_detail a");
		sqlbuf.append(" inner join dcp_warninglog b  on a.eid=b.eid and a.id=b.id ");
		sqlbuf.append("  where a.eid='"+eId+"'");

		sqlbuf.append(" and b.warningtype='"+warningType+"'");

		if (beginDate != null && beginDate.trim().isEmpty() == false)
		{
			String beginDate_format = beginDate+" 00:00:00";		
			sqlbuf.append(" and b.pushtime>='"+beginDate_format+"'");

		}
		if (endDate != null && endDate.trim().isEmpty() == false)
		{
			String endDate_format = endDate+" 23:59:59";		
			sqlbuf.append(" and b.pushtime<='"+endDate_format+"'");

		}	
		if (warningNo != null && warningNo.trim().isEmpty() == false)
		{
			sqlbuf.append(" and a.billno='"+warningNo+"'");			
		}
		if (orderNo != null && orderNo.trim().isEmpty() == false)
		{
			sqlbuf.append(" and a.orderNo='"+orderNo+"'");

		}			
		if (shopId != null && shopId.trim().isEmpty() == false)
		{
			sqlbuf.append(" and a.shopId='"+shopId+"'");			
		}
		if (memberId != null && memberId.trim().isEmpty() == false)
		{
			sqlbuf.append(" and a.memberId='"+memberId+"'");			
		}
		if (cardTypeId != null && cardTypeId.trim().isEmpty() == false)
		{
			sqlbuf.append(" and a.cardTypeId='"+cardTypeId+"'");			
		}
		if (cardNo != null && cardNo.trim().isEmpty() == false)
		{
			sqlbuf.append(" and a.cardNo='"+cardNo+"'");			
		}

		sqlbuf.append(" order by b.warningtype,b.pushtime desc");


		if(warningType.toLowerCase().equals("order"))
		{
			//不同类型分组不同，后面group by 也要修改
			sqlbuf.append(" ) group by id,billno,warningtype,shopid,SHOPNAME,pushdate,BILLNAME,Warningitemdescription ");			
		}
		else if (warningType.toLowerCase().equals("point")) 
		{
			//不同类型分组不同，后面group by 也要修改
			sqlbuf.append(" ) group by id,billno,warningtype,memberid,pushdate,BILLNAME,Warningitemdescription ");		

		}
		else if (warningType.toLowerCase().equals("card")) 
		{	
			//不同类型分组不同，后面group by 也要修改
			sqlbuf.append(" ) group by id,billno,warningtype,cardno,cardtypeid,cardtypename,memberid,pushdate,BILLNAME,Warningitemdescription ");		
		}

		sqlbuf.append(" order by id  desc   ) aa");
		sqlbuf.append(" )");


		return sqlbuf.toString();


	}

	private String getDetailSql(DCP_WarningMonitoringExportReq req,int startRow,int endRow) throws Exception 
	{
		String eId = req.geteId();
		String langType = req.getLangType();
		level1Elm requestModel =	req.getRequest();		
		String warningType = requestModel.getWarningType();//枚举: null：全部,order：零售单,point：会员积分,card：储值卡
		String warningNo = requestModel.getWarningNo();//监控编号
		String orderNo = requestModel.getOrderNo();//销售单号
		String shopId = requestModel.getShopId();
		String memberId = requestModel.getMemberId();
		String cardTypeId = requestModel.getCardTypeId();
		String cardNo = requestModel.getCardNo();
		String beginDate = requestModel.getBeginDate();
		String endDate = requestModel.getEndDate();

		StringBuffer sqlbuf=new StringBuffer("");

		sqlbuf.append("select * from (");	
		sqlbuf.append("select rownum as rn,a.* from (");
		sqlbuf.append(" select a.*,b.warningtype,B.BILLNAME,B.Warningitemdescription from dcp_warninglog_detail a");
		sqlbuf.append(" inner join dcp_warninglog b  on a.eid=b.eid and a.id=b.id ");
		sqlbuf.append("  where a.eid='"+eId+"'");

		sqlbuf.append(" and b.warningtype='"+warningType+"'");

		if (beginDate != null && beginDate.trim().isEmpty() == false)
		{
			String beginDate_format = beginDate+" 00:00:00";		
			sqlbuf.append(" and b.pushtime>='"+beginDate_format+"'");

		}
		if (endDate != null && endDate.trim().isEmpty() == false)
		{
			String endDate_format = endDate+" 23:59:59";		
			sqlbuf.append(" and b.pushtime<='"+endDate_format+"'");

		}	
		if (warningNo != null && warningNo.trim().isEmpty() == false)
		{
			sqlbuf.append(" and a.billno='"+warningNo+"'");			
		}
		if (orderNo != null && orderNo.trim().isEmpty() == false)
		{
			sqlbuf.append(" and a.orderNo='"+orderNo+"'");

		}			
		if (shopId != null && shopId.trim().isEmpty() == false)
		{
			sqlbuf.append(" and a.shopId='"+shopId+"'");			
		}
		if (memberId != null && memberId.trim().isEmpty() == false)
		{
			sqlbuf.append(" and a.memberId='"+memberId+"'");			
		}
		if (cardTypeId != null && cardTypeId.trim().isEmpty() == false)
		{
			sqlbuf.append(" and a.cardTypeId='"+cardTypeId+"'");			
		}
		if (cardNo != null && cardNo.trim().isEmpty() == false)
		{
			sqlbuf.append(" and a.cardNo='"+cardNo+"'");			
		}

		sqlbuf.append(" order by b.warningtype,b.pushtime desc");
		sqlbuf.append(") a");
		sqlbuf.append(") where rn>"+startRow+" and rn<="+endRow);


		return sqlbuf.toString();
	}

	public boolean delFilesByPath(String path, String ignoreStartName, String str) throws Exception {
		// 参数说明---------path:要删除的文件的文件夹的路径
		boolean b = false;
		try {
			// 参数说明---------path:要删除的文件的文件夹的路径
			File file = new File(path);
			if (file.exists() && file.isDirectory()) {
				File[] tempFile = file.listFiles();

				for (int i = 0; i < tempFile.length; i++) {
					// 删除，文件名开头非当前日期，且以"送券促销汇总导出.xls"结尾的文件
					if (!tempFile[i].getName().startsWith(ignoreStartName) && tempFile[i].getName().endsWith(str)) {
						boolean del = deleteFile(path + tempFile[i].getName());
						if (del) {
							// logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"文件"+tempFile[i].getName()+"删除成功");
							b = true;
						} else {
							// logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"文件"+tempFile[i].getName()+"删除失败");
						}
					}
				}
			}
		} catch (Exception e) {
			// logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"盘点信息excel导出,删除文件异常,参数:path"+path+"name"+ignoreStartName+"str"+str);
			// logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"盘点信息excel导出,删除文件异常",e);
		}
		return b;
	}

	public boolean deleteFile(String path) {
		// logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"开始删除文件："+path);
		boolean del = false;
		File file = new File(path);
		if (file.isFile()) {
			file.delete();
			del = true;
		}
		return del;
	}

	private String getWarningTypeName(String warningType)
	{
		String warningTypeName = "";
		if(warningType.toLowerCase().equals("order"))
		{
			warningTypeName = "异常零售单";								
		}
		else if (warningType.toLowerCase().equals("point")) 
		{
			warningTypeName = "积分消费";		

		}
		else if (warningType.toLowerCase().equals("card")) 
		{
			warningTypeName = "储值消费";					
		}

		return warningTypeName;
	}

}
