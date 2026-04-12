package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_TicketStyleUpdateReq;
import com.dsc.spos.json.cust.req.DCP_TicketStyleUpdateReq.level2Elm;
import com.dsc.spos.json.cust.req.DCP_TicketStyleUpdateReq.level3Elm;
import com.dsc.spos.json.cust.res.DCP_TicketStyleUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DCP_TicketStyleUpdate
 * 服务说明：企业小票样式修改
 * @author wangzyc 
 * @since  2020-12-3
 */
public class DCP_TicketStyleUpdate extends SPosAdvanceService<DCP_TicketStyleUpdateReq, DCP_TicketStyleUpdateRes>{

	@Override
	protected void processDUID(DCP_TicketStyleUpdateReq req, DCP_TicketStyleUpdateRes res) throws Exception {
		// TODO 自动生成的方法存根
		
		String eId = req.geteId();
		String styleName = req.getRequest().getStyleName();
		String ticketType = req.getRequest().getTicketType();
		String styleId = req.getRequest().getStyleId();
		String langType = req.getLangType();
		try{
			if(checkExist(req)){
				String content = getContent(req);
				// 获取当前时间
				String lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				
				UptBean ub1 = null;	
				ub1 = new UptBean("DCP_TICKETSTYLE");
				// 根据Token 中的语言别 判断修改的哪个名称
				if (langType.equals("zh_CN")) {
					ub1.addUpdateValue("STYLENAME_CN", new DataValue(styleName, Types.VARCHAR));
				} else if (langType.equals("zh_TW")) {
					ub1.addUpdateValue("STYLENAME_TW", new DataValue(styleName, Types.VARCHAR));
				} else if (langType.equals("en_US")) {
					ub1.addUpdateValue("STYLENAME_EN", new DataValue(styleName, Types.VARCHAR));
				}
				ub1.addUpdateValue("CONTENT", new DataValue(content, Types.VARCHAR));
				ub1.addUpdateValue("TICKETTYPE", new DataValue(ticketType, Types.VARCHAR));
				ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
				ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(req.getOpName(), Types.VARCHAR));
				ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
			
				// condition
				ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				ub1.addCondition("STYLEID", new DataValue(styleId, Types.VARCHAR));

				this.addProcessData(new DataProcessBean(ub1));
				
				this.doExecuteDataToDB();
			}
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		}
		catch (Exception e) 
		{
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败："+e.getMessage());	
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_TicketStyleUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_TicketStyleUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_TicketStyleUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_TicketStyleUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		  List<level2Elm> compoGroupList = req.getRequest().getCompoGroupList();
		
		if (Check.Null(req.getRequest().getStyleId())) 
		{
			errMsg.append("样式编号不可为空值, ");
			isFail = true;
		} 
		
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
	protected TypeToken<DCP_TicketStyleUpdateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_TicketStyleUpdateReq>(){};
	}

	@Override
	protected DCP_TicketStyleUpdateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_TicketStyleUpdateRes();
	}
	
	/**
	 * 查看修改的数据是否存在
	 * @param req
	 * @param GUID
	 * @return
	 * @throws Exception
	 */
	private boolean checkExist(DCP_TicketStyleUpdateReq req)  throws Exception {
		
		String sql = null;
		boolean exist = false;
		String eId = req.geteId();
		
		sql = " select * from DCP_TICKETSTYLE where EID='"+eId+"'  and STYLEID= '"+req.getRequest().getStyleId()+"'  " ;
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
	private String getContent(DCP_TicketStyleUpdateReq req) throws Exception{
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

}
