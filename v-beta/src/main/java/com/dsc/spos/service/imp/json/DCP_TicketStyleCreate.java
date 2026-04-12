package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_TicketStyleCreateReq;
import com.dsc.spos.json.cust.req.DCP_TicketTypeQueryReq;
import com.dsc.spos.json.cust.req.DCP_TicketStyleCreateReq.level2Elm;
import com.dsc.spos.json.cust.req.DCP_TicketStyleCreateReq.level3Elm;
import com.dsc.spos.json.cust.res.DCP_TicketStyleCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;


/**
 * 服务函数：DCP_TicketStyleCreate
 * 服务说明：企业小票样式创建
 * @author wangzyc 
 * @since  2020-12-3
 */
public class DCP_TicketStyleCreate extends SPosAdvanceService<DCP_TicketStyleCreateReq, DCP_TicketStyleCreateRes>{

	@Override
	protected void processDUID(DCP_TicketStyleCreateReq req, DCP_TicketStyleCreateRes res) throws Exception {
		// TODO 自动生成的方法存根
		String eId = req.geteId();
		String styleName = req.getRequest().getStyleName();
		String ticketType = req.getRequest().getTicketType();
		String langType = req.getLangType();
		try{
			String content = getContent(req);
			String guid = getGUID(req);
			String[] columnsDcp_TicketStyle = { "EID", "STYLEID", "TICKETTYPE","STYLENAME_CN","STYLENAME_TW",
					"STYLENAME_EN","CONTENT","STATUS","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME"};
			DataValue[] insValue = null;
			//  DCP_TICKETSTYLE 企业小票 保存资料
			String styleNameCN = " ";
			String styleNameTW = " ";
			String styleNameEN = " ";
			if (langType.equals("zh_CN")) {
				styleNameCN = styleName;
			} else if (langType.equals("zh_TW")) {
				styleNameTW = styleName;
			} else if (langType.equals("en_US")) {
				styleNameEN = styleName;
			}
			
			String status = "100"; // 状态：-1未启用100已启用0-禁用 默认状态为100
			
			// 获取当前时间
			String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			insValue = new DataValue[] { 
					new DataValue(eId, Types.VARCHAR),
					new DataValue(guid, Types.VARCHAR),
					new DataValue(ticketType, Types.VARCHAR),
					new DataValue(styleNameCN, Types.VARCHAR),
					new DataValue(styleNameTW, Types.VARCHAR),
					new DataValue(styleNameEN, Types.VARCHAR),
					new DataValue(content, Types.VARCHAR), 
					new DataValue(status, Types.VARCHAR), 
					new DataValue(req.getOpNO(), Types.VARCHAR),
					new DataValue(req.getOpName(), Types.VARCHAR),
					new DataValue(lastmoditime, Types.DATE)
					};
			InsBean ib1 = new InsBean("DCP_TICKETSTYLE", columnsDcp_TicketStyle);
			ib1.addValues(insValue);
			this.addProcessData(new DataProcessBean(ib1));
			
			this.doExecuteDataToDB();
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		}
		catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败："+e.getMessage());
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_TicketStyleCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_TicketStyleCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_TicketStyleCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_TicketStyleCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		List<level2Elm> compoGroupList = req.getRequest().getCompoGroupList();
		
		
		if (Check.Null(req.getRequest().getStyleName())) 
		{
			errMsg.append("样式名称不可为空值, ");
			isFail = true;
		} 
		
		if (Check.Null(req.getRequest().getTicketType())) 
		{
			errMsg.append("小票类型编码不可为空值, ");
			isFail = true;
		} 
		
		if (compoGroupList.isEmpty() || compoGroupList.size()==0 ) 
		{
			errMsg.append("小票类型组件分组不可为空值, ");
			isFail = true;
		} 
		
		for (level2Elm level2Elm : compoGroupList) {
			if (Check.Null(level2Elm.getGroupId())) 
			{
				errMsg.append("组件分组编码不可为空值, ");
				isFail = true;
			} 
			List<level3Elm> compoList = level2Elm.getCompoList();
			if(compoList.isEmpty() || compoList.size()==0){
				errMsg.append("组件分组不可为空值, ");
				isFail = true;
			}
			
			for (level3Elm level3Elm : compoList) {
				if (Check.Null(level3Elm.getCompoId())) 
				{
					errMsg.append("组件编码不可为空值, ");
					isFail = true;
				} 
			}
		}
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}	
		return isFail;
	}
 
	@Override
	protected TypeToken<DCP_TicketStyleCreateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_TicketStyleCreateReq>(){};
	}

	@Override
	protected DCP_TicketStyleCreateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_TicketStyleCreateRes();
	}

	/**
	 * 查看将要保存的数据是否存在
	 * @param req
	 * @param GUID
	 * @return
	 * @throws Exception
	 */
	private boolean checkExist(DCP_TicketStyleCreateReq req,String GUID)  throws Exception {
		
		String sql = null;
		boolean exist = false;
		String eId = req.geteId();
		
		sql = " select * from DCP_TICKETSTYLE where EID='"+eId+"'  and STYLEID= '"+GUID+"'  " ;
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		if (getQData != null && getQData.isEmpty() == false) {
			exist = true;
		}
		return exist;
	}
	
	/**
	 * 参数组织成json 返回
	 * @param req
	 * @return
	 * @throws Exception
	 */
	private String getContent(DCP_TicketStyleCreateReq req) throws Exception{
		List<level2Elm> compoGroupList = req.getRequest().getCompoGroupList();
		JSONObject  content= new JSONObject();
		// 生成 content 
		for (level2Elm level2Elm : compoGroupList) {
			JSONObject  compo= new JSONObject();
			for (level3Elm level3Elm : level2Elm.getCompoList()) {
				compo.put(level3Elm.getCompoId(), level3Elm.getCurValue());
			}
			content.put(level2Elm.getGroupId(), compo);
		}
		return content.toString();
	}
	
	/**
	 * 生成一个不重复的GUID
	 * @param req
	 * @return
	 * @throws Exception
	 */
	private String getGUID(DCP_TicketStyleCreateReq req) throws Exception{
		// 生成GUID
		String GUID  = null;
		boolean existGUID = true;
		do{
			GUID = PosPub.getGUID(false);
			if(!checkExist(req, GUID)){
				existGUID = false;
			}
		}while(existGUID);
		return GUID;
	}
	
}
