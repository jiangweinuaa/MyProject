package com.dsc.spos.utils.websocket;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import com.dsc.spos.waimai.HelpTools;

/**
 * Created by WFX on 2018-02-26.
 */
public class SpringWebSocketHandlerInterceptor extends HttpSessionHandshakeInterceptor {
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {

		HelpTools.writelog_fileName("*************** websocket 握手开始","WebSocketLog");
		
		System.out.println("Before Handshake");
		
		if (request instanceof ServletServerHttpRequest) {
			ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
//			HttpSession session = servletRequest.getServletRequest().getSession(false);
			HttpSession session = servletRequest.getServletRequest().getSession();
			
			HelpTools.writelog_fileName("*************** servletRequest："+servletRequest,"WebSocketLog");
			HelpTools.writelog_fileName("*************** session："+session,"WebSocketLog");
			
			InputStream inputStream = request.getBody();
			
			String result = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining(System.lineSeparator()));
			System.out.println("结果："+result);
			HelpTools.writelog_fileName("*************** 结果:"+result,"WebSocketLog");
			
			if (session != null) {
				// 使用userName区分WebSocketHandler，以便定向发送消息
				String userName = (String) session.getId(); // 一般直接保存user实体 //
															// 实际开发改为session.getAttribute("user");(唯一标识)
				HelpTools.writelog_fileName("*************** sessionId：" + userName,"WebSocketLog");
				UUID uuid = UUID.randomUUID();
				String randomId = uuid.toString().replace("-", "");
				System.out.println("当前会话随机码："+randomId);
				HelpTools.writelog_fileName("*************** 当前会话随机码：" + randomId,"WebSocketLog");
				
				if (userName != null) {
					HelpTools.writelog_fileName("*************** RANDOM_ID：" + randomId,"WebSocketLog");
					attributes.put("RANDOM_ID", randomId);
					HelpTools.writelog_fileName("*************** WEBSOCKET_USERID：" + userName,"WebSocketLog");
					attributes.put("WEBSOCKET_USERID", userName);
				}else
				{
					HelpTools.writelog_fileName("*************** RANDOM_ID空：" + randomId,"WebSocketLog");
					HelpTools.writelog_fileName("*************** WEBSOCKET_USERID空：" + userName,"WebSocketLog");
				}

			}
		}
		return super.beforeHandshake(request, response, wsHandler, attributes);

	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception ex) {
		super.afterHandshake(request, response, wsHandler, ex);
	}
}
