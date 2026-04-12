package com.dsc.spos.service.imp.json;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.imageio.ImageIO;

//import org.bouncycastle.util.encoders.Base64;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_NoticeUpdateReq;
import com.dsc.spos.json.cust.req.DCP_NoticeUpdateReq.level2Elm;
import com.dsc.spos.json.cust.res.DCP_NoticeUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.EncryptUtils;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.codec.*;
import org.apache.commons.codec.binary.Base64;

public class DCP_NoticeUpdate extends SPosAdvanceService<DCP_NoticeUpdateReq,DCP_NoticeUpdateRes>
{
	@Override
	protected void processDUID(DCP_NoticeUpdateReq req, DCP_NoticeUpdateRes res) throws Exception 
	{
		List<DataProcessBean> lstIns=new ArrayList<DataProcessBean>();

		List<level2Elm> datas=req.getRequest().getDatas();
		for (level2Elm level2Elm : datas) 
		{
			/**测试案例
			EncryptUtils eu = EncryptUtils.getInstance();

			ByteArrayOutputStream outputStream = null;  
			File f=new File("c:\\1.jpg");
			BufferedImage bufferedImage = ImageIO.read(f);  
			outputStream = new ByteArrayOutputStream();  
			ImageIO.write(bufferedImage, "jpg", outputStream); 
			byte[] old=outputStream.toByteArray();

			//将图片文件转化为字节数组字符串，并对其进行Base64编码处理  
        String imgFile = "c:\\2.jpg";//待处理的图片  
        InputStream in = null;  
        byte[] data = null;  
        //读取图片字节数组  
        try   
        {  
            in = new FileInputStream(imgFile);          
            data = new byte[in.available()];  
            in.read(data);  
            in.close();  
        }   
        catch (IOException e)   
        {  

        }  

			 */


			try 
			{		
				//fileData附件数据
				String base64=level2Elm.getFileData();	
				//有附件再保存
				if (Check.Null(base64)==false) 
				{
					/**测试案例
				  //创建文件，追加记录
					File fs=new File("c:\\base64.txt");
					FileOutputStream fop=new FileOutputStream(fs);
					OutputStreamWriter writer=new OutputStreamWriter(fop);
					writer.append(base64);
					writer.close();
					fop.close();		
					 */

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

					/**测试案例
					fs=new File("c:\\sNewBase64.txt");
					fop=new FileOutputStream(fs);
					writer=new OutputStreamWriter(fop);
					writer.append(sNewBase64);
					writer.close();
					fop.close();	
					 */

					//Base64解码  
					@SuppressWarnings("restriction")
					byte[] b = Base64.decodeBase64(base64);


					File file =new File("webapps");    

					//如果文件夹不存在则创建   \\webapps\\ivy\\notice_file 
					if(!file.exists()&& !file.isDirectory())      
					{
						boolean b1=file.mkdir();
						if(b1==false)
						{
							////System.out.println("webapps已经存在或失败");
						}
						else
						{
							file =new File("webapps\\ivy"); 
							b1=file.mkdir();
							if(b1==false)
							{
								////System.out.println("webapps\\ivy已经存在或失败");
							}
							else
							{
								file =new File("webapps\\ivy\\notice_file"); 
								b1=file.mkdir();
								if(b1==false)
								{
									////System.out.println("webapps\\ivy\\notice_file已经存在或失败");
								}
							}
						}
					}

					//生成文件
					String imgFilePath = "webapps\\ivy\\notice_file\\" +level2Elm.getFilePath();
					OutputStream out = new FileOutputStream(imgFilePath);      
					out.write(b);  
					out.flush();  
					out.close();  

				}	 			

			} 
			catch (Exception e) 
			{		

			}

			int insColCt = 0;
			String[] columnsNOTICE_FILE ={"EID","NOTICE_ID","FILE_NAME","FILE_PATH"};
			DataValue[] columnsVal = new DataValue[columnsNOTICE_FILE.length];
			for (int i = 0; i < columnsVal.length; i++)
			{
				String keyVal = null;
				switch (i) 
				{
				case 0:
					keyVal=req.geteId();
					break;
				case 1:
					keyVal=req.getRequest().getNoticeID();
					break;
				case 2:
					keyVal=level2Elm.getFileName();
					break;
				case 3:
					keyVal=level2Elm.getFilePath();
					break;

				default:

					break;

				}

				if (keyVal != null) 
				{
					insColCt++;
					columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
				} 
				else 
				{
					columnsVal[i] = null;
				}

			}

			String[] columns2 = new String[insColCt];
			DataValue[] insValue2 = new DataValue[insColCt];

			insColCt = 0;

			for (int i = 0; i < columnsVal.length; i++) 
			{
				if (columnsVal[i] != null) 
				{
					columns2[insColCt] = columnsNOTICE_FILE[i];
					insValue2[insColCt] = columnsVal[i];
					insColCt++;
					if (insColCt >= insValue2.length)
						break;
				}
			}

			InsBean ib2 = new InsBean("DCP_NOTICE_FILE", columns2);
			ib2.addValues(insValue2);
			lstIns.add(new DataProcessBean(ib2));
		}


		//更新单头
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
		String MODIFY_DATE = df.format(cal.getTime());
		df=new SimpleDateFormat("HHmmss");
		String MODIFY_TIME = df.format(cal.getTime());

		UptBean ub1 = new UptBean("DCP_NOTICE");			
		ub1.addCondition("EID",new DataValue(req.geteId(), Types.VARCHAR));
		ub1.addCondition("NOTICE_ID",new DataValue(req.getRequest().getNoticeID().toUpperCase(), Types.VARCHAR));
		ub1.addUpdateValue("MODIFYBY",new DataValue(req.getOpNO(), Types.VARCHAR)); 
		ub1.addUpdateValue("MODIFY_DATE",new DataValue(MODIFY_DATE, Types.VARCHAR));
		ub1.addUpdateValue("MODIFY_TIME",new DataValue(MODIFY_TIME, Types.VARCHAR));
		ub1.addUpdateValue("TYPE",new DataValue(req.getRequest().getType(), Types.VARCHAR));
		ub1.addUpdateValue("TITLE",new DataValue(req.getRequest().getTitle(), Types.VARCHAR));
		ub1.addUpdateValue("CONTENT",new DataValue(req.getRequest().getContent(), Types.BLOB));

		this.addProcessData(new DataProcessBean(ub1));

		//先删除原来单身
		DelBean db1 = new DelBean("DCP_NOTICE_FILE");
		db1.addCondition("NOTICE_ID", new DataValue(req.getRequest().getNoticeID().toUpperCase(), Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1));

		//再加入新增SQL
		for (DataProcessBean ins : lstIns) 
		{				
			this.addProcessData(ins);
		}

		this.doExecuteDataToDB();
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");



	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_NoticeUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_NoticeUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_NoticeUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_NoticeUpdateReq req) throws Exception 
	{	
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

		List<level2Elm> datas=req.getRequest().getDatas();

		if (Check.Null(req.getRequest().getType())) 
		{
			errCt++;
			errMsg.append("公告类型不可为空值, ");
			isFail = true;
		} 

		if (Check.Null(req.getRequest().getTitle())) 
		{
			errCt++;
			errMsg.append("公告标题不可为空值, ");
			isFail = true;
		} 

		if (Check.Null(req.getRequest().getContent())) 
		{
			errCt++;
			errMsg.append("公告内容不可为空值, ");
			isFail = true;
		} 

		if (Check.Null(req.getRequest().getNoticeID())) 
		{
			errCt++;
			errMsg.append("公告GUID不可为空值, ");
			isFail = true;
		} 

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		for (level2Elm level2Elm : datas) 
		{
			if (Check.Null(level2Elm.getFileName())) 
			{
				errCt++;
				errMsg.append("附件名称不可为空值, ");
				isFail = true;
			} 

			if (Check.Null(level2Elm.getFilePath())) 
			{
				errCt++;
				errMsg.append("附件路径不可为空值, ");
				isFail = true;
			} 

			if (isFail)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}

		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_NoticeUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_NoticeUpdateReq>(){};
	}

	@Override
	protected DCP_NoticeUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_NoticeUpdateRes();
	}

}
