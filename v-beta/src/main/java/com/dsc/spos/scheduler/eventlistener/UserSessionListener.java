package com.dsc.spos.scheduler.eventlistener;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.dsc.spos.scheduler.job.StaticInfo;

public class UserSessionListener implements HttpSessionListener
{

	public UserSessionListener()
	{
		//System.out.println("UserSessionListener监听启动");		
	}

	@Override
	public void sessionCreated(HttpSessionEvent event) 
	{		
		//想DEBUG到这可以在INVOKE里面加上这句话
		//Object object=request.getSession().getServletContext().getAttribute("curUserNum");		
		
		//System.out.println("创建了");
		HttpSession session = event.getSession();// 获得Session对象
		// 通过Session获得servletcontext对象
		ServletContext servletContext = session.getServletContext();
		
		//新增
		servletContext.setAttribute(session.getId(), session);
		
		Object object = servletContext.getAttribute("curUserNum");
		if (object == null) 
		{
			servletContext.setAttribute("curUserNum", 1);
		} 
		else 
		{
			int num =(int)object;
			servletContext.setAttribute("curUserNum", num + 1);
			
			//赋值
			StaticInfo.lUsercount=num + 1;
		}
		
		//
		object=null;
		servletContext=null;
		session=null;
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) 
	{
		//System.out.println("销毁了");
		HttpSession session = event.getSession();// 获得Session对象
		ServletContext servletContext = session.getServletContext();

		//移除
		servletContext.removeAttribute(session.getId());
		
		Object object = servletContext.getAttribute("curUserNum");
		if (object != null)
		{
			int num = (int) object;
			
			if (num<=0) 
			{
				num=1;
			}
			
			servletContext.setAttribute("curUserNum", num - 1);
			
			//赋值
			StaticInfo.lUsercount=num - 1;
		}
		
		//
		object=null;
		servletContext=null;
		session=null;

	}


}
