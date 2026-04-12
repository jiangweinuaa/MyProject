package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_MarketAccessUpdateReq;
import com.dsc.spos.json.cust.res.DCP_MarketAccessUpdateRes;
import com.dsc.spos.ninetyone.util.ColumnDataValue;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 商场接入修改
 * 规格：http://183.233.190.204:10004/project/79/interface/api/2902
 */
public class DCP_MarketAccessUpdate extends SPosAdvanceService<DCP_MarketAccessUpdateReq, DCP_MarketAccessUpdateRes>
{

	@Override
	protected void processDUID(DCP_MarketAccessUpdateReq req, DCP_MarketAccessUpdateRes res) throws Exception {
	// TODO Auto-generated method stub
		String eId=req.geteId();
		String modifBy = req.getOpNO();
		String modifByName = req.getOpName();
		
		DCP_MarketAccessUpdateReq.Level1Elm lev1=req.getRequest();
		
		Date dt = new Date();
		SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String createTime =  matter.format(dt);
    
		String shopId=lev1.getShopId();
		DelBean db1 = new DelBean("DCP_MARKETACCESS");
		db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1));
		
		ColumnDataValue columns = new ColumnDataValue();
		columns.Add("EID", eId, Types.VARCHAR);
		columns.Add("SHOPID", shopId, Types.VARCHAR);
		columns.Add("STATUS", Integer.valueOf(lev1.getStatus()), Types.INTEGER);
		columns.Add("MARKETTYPE", lev1.getMarketType(), Types.VARCHAR);
		String uploadScale=lev1.getUploadScale();
		if(uploadScale==null|| Check.Null(uploadScale)){
			uploadScale="100";
		}
		columns.Add("UPLOADSCALE", Integer.valueOf(uploadScale), Types.VARCHAR);//上传比例
		columns.Add("SERVICEURL", lev1.getServiceUrl(), Types.VARCHAR);//商场：服务地址
		columns.Add("VALIKEY", lev1.getValiKey(), Types.VARCHAR);//商场：接入密钥
		columns.Add("USERCODE", lev1.getUserCode(), Types.VARCHAR);//商场：用户名
		columns.Add("PASSWORD", lev1.getPassword(), Types.VARCHAR);//商场：密码
		columns.Add("MARKETNO", lev1.getMarketNo(), Types.VARCHAR);//商场：商场编码
		columns.Add("MARKETNAME", lev1.getMarketName(), Types.VARCHAR);//商场：商场名称
		columns.Add("STORENO", lev1.getStoreNo(), Types.VARCHAR);//商场：店铺编码
		columns.Add("STORENAME", lev1.getStoreName(), Types.VARCHAR);//商场：店铺名称
		columns.Add("CORPNO", lev1.getCorpNo(), Types.VARCHAR);//商场：公司编码
		columns.Add("CORPNAME", lev1.getCorpName(), Types.VARCHAR);//商场：公司名称
		
		columns.Add("MACHINENO", lev1.getMachineNo(), Types.VARCHAR);//商场：店铺统一收银机号
		columns.Add("GOODSNO", lev1.getGoodsNo(), Types.VARCHAR);//商场：店铺统一货号
		columns.Add("PAYCODE", lev1.getPayCode(), Types.VARCHAR);//商场：店铺统一支付方式
        columns.Add("PARAMS", lev1.getParams(), Types.VARCHAR);//商场：华润置地-万象城参数信息


        columns.Add("CREATEOPID", modifBy, Types.VARCHAR);//创建人ID
		columns.Add("CREATEOPNAME", modifByName, Types.VARCHAR);//创建人名称
		columns.Add("CREATETIME", createTime, Types.DATE);//创建时间
		
		columns.Add("LASTMODIOPID", modifBy, Types.VARCHAR);//最后修改人ID
		columns.Add("LASTMODIOPNAME", modifByName, Types.VARCHAR);//最后修改人名称
		columns.Add("LASTMODITIME", createTime, Types.DATE);//最后修改时间
//		columns.Add("TRAN_TIME", createTime, Types.VARCHAR);//最后传输时间
		
		String[] columns1 = columns.Columns.toArray(new String[0]);
		DataValue[] insValue1 = columns.DataValues.toArray(new DataValue[0]);
		InsBean ib1 = new InsBean("DCP_MARKETACCESS", columns1);
		ib1.addValues(insValue1);
		this.addProcessData(new DataProcessBean(ib1));
        this.doExecuteDataToDB();

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_MarketAccessUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_MarketAccessUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_MarketAccessUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_MarketAccessUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		DCP_MarketAccessUpdateReq.Level1Elm lev1=req.getRequest();
		
		if(lev1==null){
			errMsg.append("request不可为空值, ");
			isFail = true;
		}else{
			String shopId=lev1.getShopId();
			if(shopId==null||Check.Null(shopId)){
				errMsg.append("门店编码不可为空值, ");
				isFail = true;
			}
			String status=lev1.getStatus();
			if(status==null||Check.Null(status)){
				errMsg.append("是否开启商场对接不可为空值, ");
				isFail = true;
			}else{
				if("0".equals(status)||"1".equals(status)){
					
				}else{
					errMsg.append("是否开启商场对接值异常[0,1], ");
					isFail = true;
				}
			}
			String uploadscale=lev1.getUploadScale();
			if(uploadscale!=null&&!Check.Null(uploadscale)){
				try{
					int uploadscaleInt=Integer.valueOf(uploadscale);
					if(uploadscaleInt<0||uploadscaleInt>100){
						errMsg.append("上传比例值异常[0~100],当前值:"+uploadscale+", ");
						isFail = true;
					}
				}catch(Exception e){
					errMsg.append("上传比例值异常[0~100],当前值:"+uploadscale+", ");
					isFail = true;
				}
			}
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.substring(0, errMsg.length() - 2));
		}
	  return isFail;
	}

	@Override
	protected TypeToken<DCP_MarketAccessUpdateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_MarketAccessUpdateReq>(){};
	}

	@Override
	protected DCP_MarketAccessUpdateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_MarketAccessUpdateRes();
	}

}
