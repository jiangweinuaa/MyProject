package com.dsc.spos.service.imp.json;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import org.apache.commons.codec.binary.Base64;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_UploadFileReq;
import com.dsc.spos.json.cust.res.DCP_UploadFileRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：UploadFileDCPReq
 * 服务说明：文件上传
 * @author jinzma 
 * @since  2019-11-19
 */
public class DCP_UploadFile extends SPosAdvanceService<DCP_UploadFileReq,DCP_UploadFileRes>{

	@Override
	protected void processDUID(DCP_UploadFileReq req, DCP_UploadFileRes res) throws Exception {
		// TODO 自动生成的方法存根
		try 
		{
			//fileData数据
			String base64=req.getFileData();
			//有附件再保存					
			base64=base64.substring(base64.indexOf("base64,")+7);
			//http://blog.csdn.net/jsjwbxzy/article/details/45970231
			//http请求中传输base64出现加号变空格的解决办法
			base64=base64.replace(" ", "+");//替换空格/*TMD接收进来有空格，发现本来应该是+号的*/
			//根据RFC822规定，BASE64Encoder编码每76个字符，还需要加上一个回车换行
			//http://blog.csdn.net/u010953266/article/details/52590570
			int iLEN=base64.length()/76+1;
			String sNewBase64="";
			for (int i = 0; i < iLEN; i++) 
			{
				if (i*76+76>base64.length()) 
				{
					sNewBase64=sNewBase64+base64.substring(i*76, base64.length());
				}
				else
				{
					sNewBase64=sNewBase64+base64.substring(i*76, i*76+76) +"\r\n";					
				}				
			}	
			//Base64解码  
			//@SuppressWarnings("restriction")
			byte[] b = Base64.decodeBase64(base64);
			String dirpath= System.getProperty("catalina.home")+"\\webapps\\uploadfile";
			File file =new File(dirpath);    
			//如果文件夹不存在则创建   \\webapps\\uploadfile 
			if(!file.exists() && !file.isDirectory())      
			{
				boolean b1=file.mkdir();
				if(b1==false)
				{
					////System.out.println("webapps已经存在或失败");
				}			
			}
			file=null;

			//生成文件
			String imgFilePath = dirpath +"\\" +req.getFileName();
			OutputStream out = new FileOutputStream(imgFilePath);      
			out.write(b);  
			out.flush();  
			out.close();  

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");			

		} 
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}




	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_UploadFileReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_UploadFileReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_UploadFileReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_UploadFileReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if (Check.Null(req.getFileName()) ) 
		{
			errMsg.append("文件名称不可为空值, ");
			isFail = true;
		}
		if (Check.Null(req.getFileData()) ) 
		{
			errMsg.append("文件编码不可为空值, ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_UploadFileReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_UploadFileReq>(){};
	}

	@Override
	protected DCP_UploadFileRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_UploadFileRes();
	}

}
