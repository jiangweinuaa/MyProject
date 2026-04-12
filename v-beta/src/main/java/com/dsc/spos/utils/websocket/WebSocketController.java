package com.dsc.spos.utils.websocket;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.TextMessage;

import com.dsc.spos.waimai.HelpTools;


/**
 * 业务使用（暂未使用） Created by zhangwenchao on 2017/11/20.
 */
@Controller
@Component
public class WebSocketController {

    @Bean//这个注解会从Spring容器拿出Bean
    public SpringWebSocketHandler infoHandler() {
        return new SpringWebSocketHandler();
    }


    @RequestMapping("/websocket/loginPage")
    public String loginPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	HelpTools.writelog_fileName("*************** websocket loginPage","WebSocketLog");
    	return "/websocket/login";
    }


    @RequestMapping("/websocket/login")
    public String login(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String username = request.getSession().getId();
        System.out.println(username+"登录");
        HelpTools.writelog_fileName("***************"+username+"登录","WebSocketLog");
        HttpSession session = request.getSession(false);
        session.setAttribute("SESSION_USERNAME", username); //一般直接保存user实体
        return "/websocket/send";
    }

    @RequestMapping("/websocket/send")
    @ResponseBody
    public String send(HttpServletRequest request) {
        String username = request.getParameter("username");
        String message = request.getParameter("message");
        String result = "";
        result = infoHandler().sendMessageToUser(username, new TextMessage(message));
        System.out.println("走controller了："+ result);

        return result ;
    }

    @RequestMapping("/websocket/broad")
    @ResponseBody
    public  String broad(String id) {
        infoHandler().sendMessageToUsers(id,new TextMessage("发送一条小Broad"));
        System.out.println("群发成功");
        return "broad";
    }
}