package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PayMappingUpdateReq;
import com.dsc.spos.json.cust.res.DCP_PayMappingUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_PayMappingUpdate extends SPosAdvanceService<DCP_PayMappingUpdateReq,DCP_PayMappingUpdateRes>
{
	@Override
	protected void processDUID(DCP_PayMappingUpdateReq req, DCP_PayMappingUpdateRes res) throws Exception 
	{
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String mySysTime = df.format(cal.getTime());
        String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String eId = req.geteId();
        String docType = req.getRequest().getChannelType();
        String channelId = req.getRequest().getChannelId();
        String payType = req.getRequest().getPayType();
        String payName = req.getRequest().getPayName();
        String order_paycode = req.getRequest().getOrder_paycode();
        String order_payname = req.getRequest().getOrder_payname();

        String sql = null;
        sql = this.isRepeat(eId, docType, channelId, order_paycode);
        List<Map<String, Object>> scDatas = this.doQueryData(sql, null);
        if (scDatas==null||scDatas.isEmpty())
        {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("该第三方平台支付方式不存在，无法修改！");
            return;

        }

        UptBean up1 = new UptBean("DCP_PAYMENTMAPPING");
        up1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        up1.addCondition("CHANNELTYPE", new DataValue(docType, Types.VARCHAR));
        up1.addCondition("CHANNELID", new DataValue(channelId, Types.VARCHAR));
        up1.addCondition("ORDER_PAYCODE", new DataValue(order_paycode, Types.VARCHAR));

        up1.addUpdateValue("PAYTYPE", new DataValue(payType, Types.VARCHAR));
        up1.addUpdateValue("PAYNAME", new DataValue(payName, Types.VARCHAR));
        if(order_payname!=null&&order_payname.trim().length()>0)
        {
            up1.addUpdateValue("ORDER_PAYNAME", new DataValue(order_payname, Types.VARCHAR));
        }
        up1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
        up1.addUpdateValue("LASTMODIOPNAME", new DataValue(req.getOpName(), Types.VARCHAR));
        up1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));

        this.addProcessData(new DataProcessBean(up1));
        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");



    }

	@Override
	protected List<InsBean> prepareInsertData(DCP_PayMappingUpdateReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PayMappingUpdateReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PayMappingUpdateReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PayMappingUpdateReq req) throws Exception 
	{

        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");

        if (req.getRequest() == null)
        {
            errMsg.append("request不能为空值！");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(req.getRequest().getChannelType()))
        {
            errMsg.append("渠道类型不能为空值，");
            isFail = true;

        }
        if (Check.Null(req.getRequest().getChannelId()))
        {
            errMsg.append("渠道编码不能为空值，");
            isFail = true;

        }
        if (Check.Null(req.getRequest().getOrder_paycode()))
        {
            errMsg.append("第三方平台支付编码不能为空值，");
            isFail = true;

        }
        if (Check.Null(req.getRequest().getOrder_payname()))
        {
            errMsg.append("第三方平台支付名称不能为空值，");
            isFail = true;

        }

        if (Check.Null(req.getRequest().getPayType()))
        {
            errMsg.append("新零售支付编码不能为空值，");
            isFail = true;

        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
	}

	@Override
	protected TypeToken<DCP_PayMappingUpdateReq> getRequestType() 
	{
		return new TypeToken<DCP_PayMappingUpdateReq>(){};
	}

	@Override
	protected DCP_PayMappingUpdateRes getResponseType() 
	{
		return new DCP_PayMappingUpdateRes();
	}

    /**
     * 验证映射信息是否已存在
     * @param eId
     * @param docType
     * @param channelId
     * @param order_payCode
     * @return
     */
    private String isRepeat( String eId , String docType, String channelId, String order_payCode ){
        String sql = null;
        sql = "select * from DCP_PAYMENTMAPPING "
                + " where  EID = '"+eId +"' "
                + " and CHANNELTYPE = '"+docType+"' "
                + " and CHANNELID = '"+channelId+"' "
                + " and ORDER_PAYCODE = '"+order_payCode+"'";
        return sql;
    }

	
}
