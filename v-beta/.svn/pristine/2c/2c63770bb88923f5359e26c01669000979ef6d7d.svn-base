package com.dsc.spos.service.imp.json;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_TemplateShopUpdateReq;
import com.dsc.spos.json.cust.req.DCP_TemplateShopUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_TemplateShopUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：ParaDefineUpdate
 *   說明：参数定义修改
 * 服务说明：参数定义修改
 * @author Jinzma 
 * @since  2017-03-03
 */
public class DCP_TemplateShopUpdate extends SPosAdvanceService<DCP_TemplateShopUpdateReq, DCP_TemplateShopUpdateRes> 
{
	@Override
	protected void processDUID(DCP_TemplateShopUpdateReq req, DCP_TemplateShopUpdateRes res) throws Exception {
		// TODO 自动生成的方法存根
		try 
		{
			String sql=getdataExist_SQL(req);	
			String[] conditionValues = null ; //查詢條件
			List<Map<String, Object>> getQData_checkNO = this.doQueryData(sql,conditionValues);
			if(getQData_checkNO!=null && !getQData_checkNO.isEmpty())
			{
				String eId=req.geteId();
				String pTemplateNO=req.getRequest().getTemplateNo();
				String docType =req.getRequest().getDocType();

				//删除原有单身
				DelBean db1 = new DelBean("DCP_PTEMPLATE_SHOP");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("PTEMPLATENO", new DataValue(pTemplateNO, Types.VARCHAR));
				db1.addCondition("DOC_TYPE", new DataValue(docType, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));


				if (req.getRequest().getDatas()!=null)
				{
					//新增新的单身（DCP_PTEMPLATE_SHOP）
					List<level1Elm> jsonDatas = req.getRequest().getDatas();
					for (level1Elm par : jsonDatas) {
						int insColCt = 0;
						String[] columnsName = {"EID","PTEMPLATENO","DOC_TYPE","SHOPID","GOODSWAREHOUSE","MATERIALWAREHOUSE","STATUS"};
						DataValue[] columnsVal = new DataValue[columnsName.length];
						for (int i = 0; i < columnsVal.length; i++) {
							String keyVal = null;
							switch (i) {
							case 0:
								keyVal = eId;
								break;
							case 1:
								keyVal = pTemplateNO;
								break;
							case 2:
								keyVal = docType;
								break;	
							case 3:
								keyVal = par.getShopId() ;
								break;
							case 4:
								keyVal = par.getGoodsWarehouseNo() ;
								break;
							case 5:
								keyVal = par.getMaterialWarehouseNo();
								break;
							case 6:
								keyVal = "100";
								break;
							default:
								break;
							}
							insColCt++;
							columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
						}

						String[] columns  = new String[insColCt];
						DataValue[] insValue = new DataValue[insColCt];
						insColCt = 0;					
						for (int i = 0; i < columnsVal.length; i++){
							if (columnsVal[i] != null){
								columns[insColCt] = columnsName[i];
								insValue[insColCt] = columnsVal[i];
								insColCt ++;
								if (insColCt >= insValue.length) break;
							}
						}		
						InsBean ib1 = new InsBean("DCP_PTEMPLATE_SHOP", columns);
						ib1.addValues(insValue);
						this.addProcessData(new DataProcessBean(ib1));
					}

				}

				//更新单头
				String modifyBy = req.getOpNO();
				Calendar cal = Calendar.getInstance();//获得当前时间
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
				String modifyDate = df.format(cal.getTime());
				df = new SimpleDateFormat("HHmmss");
				String modifyTime = df.format(cal.getTime());
				UptBean ub1 = null;	
				ub1 = new UptBean("DCP_PTEMPLATE");
				ub1.addUpdateValue("MODIFYBY", new DataValue(modifyBy, Types.VARCHAR));
				ub1.addUpdateValue("MODIFY_DATE", new DataValue(modifyDate, Types.VARCHAR));
				ub1.addUpdateValue("MODIFY_TIME", new DataValue(modifyTime, Types.VARCHAR));
				ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

				// condition
				ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				ub1.addCondition("PTEMPLATENO", new DataValue(pTemplateNO, Types.VARCHAR));
				ub1.addCondition("DOC_TYPE", new DataValue(docType, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(ub1));

				this.doExecuteDataToDB();

				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
			}
			else
			{
				res.setSuccess(false);
				res.setServiceDescription("模板不存在！");
			}
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());	
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_TemplateShopUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_TemplateShopUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_TemplateShopUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_TemplateShopUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
		}
		
		if (Check.Null(req.getRequest().getTemplateNo())) 
		{
			errCt++;
			errMsg.append("模板编号不可为空值, ");
			isFail = true;
		}
		if (Check.Null(req.getRequest().getDocType())) 
		{
			errCt++;
			errMsg.append("模板类型不可为空值, ");
			isFail = true;
		}	

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;	
	}

	@Override
	protected TypeToken<DCP_TemplateShopUpdateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_TemplateShopUpdateReq>(){};
	}

	@Override
	protected DCP_TemplateShopUpdateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_TemplateShopUpdateRes();
	}

	protected String getdataExist_SQL(DCP_TemplateShopUpdateReq req)
	{
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		String eId=req.geteId();
		String pTemplateNO=req.getRequest().getTemplateNo();
		String docType =req.getRequest().getDocType();

		sqlbuf.append("select PTEMPLATENO from DCP_ptemplate "
				+ "WHERE EID='"+eId +"'  "
				+ "AND pTemplateNO='"+pTemplateNO +"' AND doc_Type='"+docType +"'  ");		

		sql = sqlbuf.toString(); 	
		return sql;	
	}



}
