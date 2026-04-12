package com.dsc.spos.utils.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.dao.DsmDAO;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.HelpTools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by WFX on 2018-02-26.
 */

public class SpringWebSocketHandler extends TextWebSocketHandler {

	private static Logger logger = LoggerFactory.getLogger(SpringWebSocketHandler.class);

	private static final Map<String, WebSocketSession> users;  //Map来存储WebSocketSession，key用USER_ID 即在线用户列表

	private static final List<OnLineUsers> wsUsers;  //Map来存储WebSocketSession，key用USER_ID 即在线用户列表

	protected DsmDAO dao;
	// 以下四个常量，记录当前连接用户的信息， 用于校验订单是否支付成功
	private static final String CompanyNo = "";
	private static final String Shop = "";
	private static final String TableNo = "";
	private static final String SaleNo = "";

	//用户标识
//    private static final String USER_ID = "WEBSOCKET_USERID";   //对应监听器中的key

	private static final String RANDOM_ID = "RANDOM_ID";

	WebSocketSession session ;

	static {
		users =  new HashMap<String, WebSocketSession>();
		wsUsers =  new ArrayList<OnLineUsers>();
	}

	public SpringWebSocketHandler() {
	}

	/**
	 * 连接成功时候，会触发页面上onopen方法
	 */
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		HelpTools.writelog_fileName("***************sessionId:"+session.getId()+"成功建立websocket连接 ","WebSocketLog");
		this.session = session;

		
		System.out.println("成功建立websocket连接!");

		String randomId =  (String) session.getAttributes().get(RANDOM_ID);
		users.put(randomId,session);
		HelpTools.writelog_fileName("***************randomId:"+randomId,"WebSocketLog");
		HelpTools.writelog_fileName("***************当前线上用户数量:"+users.size() + " list数量："+wsUsers.size(),"WebSocketLog");
		System.out.println("当前线上用户数量:"+users.size()  );

	}

	/**
	 * 关闭连接时触发
	 */
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		logger.debug("\r\n关闭websocket连接");
		String randomId = (String) session.getAttributes().get(RANDOM_ID);
		HelpTools.writelog_fileName("***************用户"+randomId+"已退出！","WebSocketLog");

//        CheckOrder(CompanyNo, Shop, TableNo ,SaleNo);
		Iterator<OnLineUsers> it_b = wsUsers.iterator();

		while(it_b.hasNext()){
			OnLineUsers ou = it_b.next();
			if (ou.getCliendId().equals(randomId)) {
				it_b.remove();
//            	// 2020-12-02 增加判断， 如果当前用户购物车为空,就移除当前用户
//            	if (ou.getShoppingCart() == null || ou.getShoppingCart().size() < 1 ){
//                    it_b.remove();
//            	}

			}
		}

		System.out.println("用户"+randomId+"已退出！");
		users.remove(randomId);
		System.out.println("剩余在线用户"+users.size()  + " list数量："+wsUsers.size());
	}

	/**
	 * js调用websocket.send时候，会调用该方法
	 */
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

		super.handleTextMessage(session, message);
		/**
		 * 收到消息，自定义处理机制，实现业务
		 */

//        String userId= (String) session.getAttributes().get(USER_ID);
		String randomId= (String) session.getAttributes().get(RANDOM_ID);

		String reqMsg = message.getPayload();
		HelpTools.writelog_fileName("*************** 服务器收到消息（"+randomId+"）："+reqMsg,"WebSocketLog");

		JSONObject json = new JSONObject();
		json = JSONObject.parseObject(reqMsg);

		JSONArray shoppingCart = new JSONArray();

		String company = "";
		String shop = "";
		String tableNo = "";
		String saleNo = "";
		String operationType = "";

		String scanType = ""; //0：点餐模式 0.正餐(后结) 1.快餐/街饮(先结)

		if(json.containsKey("operationType")){

			if(Check.Null(json.get("operationType").toString())){
//	    		operationType = "UPDATE"; //UPDATE 是服务端自己写的枚举， 区别于 9, CLEARCARTPAYSUCCESS（清空购物车）
			}else{
				operationType = json.get("operationType").toString();
			}

		}
		if (json.containsKey("ScanType")){
			scanType = json.get("ScanType").toString();
		}

		if (json.containsKey("Company")){
			company = json.get("Company").toString();
		}

		if (json.containsKey("Shop")){
			shop = json.get("Shop").toString();
		}

		if (json.containsKey("TableNo")){
			tableNo = json.get("TableNo").toString();
		}

		if (json.containsKey("SaleNo")){
			saleNo = json.get("SaleNo").toString();
		}

		if (json.containsKey("shoppingCart")){
			shoppingCart = json.getJSONArray("shoppingCart");
		}else{
			shoppingCart = null;
		}

		OnLineUsers oUser = new OnLineUsers();

		// 2020-12-14 增加以下操作： 若客户端发出支付锁定指令
		if(operationType.toUpperCase().equals("7")){

			for (OnLineUsers onLineUsers : wsUsers) {
				if(randomId.equals(onLineUsers.getCliendId())){
					onLineUsers.setIsPayer("Y");
					onLineUsers.setSaleNo(saleNo);
					oUser = onLineUsers;
					HelpTools.writelog_fileName(" "+randomId+"已存在,是否支付人："+onLineUsers.getIsPayer()+"  购物车："+onLineUsers.getShoppingCart(),"WebSocketLog");
				}
				else{

					if( !randomId.equals(onLineUsers.getCliendId()) && company.equals(onLineUsers.getCompany()) && shop.equals(onLineUsers.getShop()) && tableNo.equals(onLineUsers.getTableNo())){
						if(scanType.equals("0")){
							// 判断ScanType==0(正餐-后结) 增加单号限制
							if(saleNo.equals(onLineUsers.getSaleNo())){
								// 同桌其他人改为 非付款人
								onLineUsers.setIsPayer("N");
								// 如果有人锁定订单了， 就把付款人提交时的单号同步给其他人
								onLineUsers.setSaleNo(saleNo);
							}
						}else {
							// 同桌其他人改为 非付款人
							onLineUsers.setIsPayer("N");
							// 如果有人锁定订单了， 就把付款人提交时的单号同步给其他人
							onLineUsers.setSaleNo(saleNo);
						}

					}

				}

			}

			TextMessage returnMessage = new TextMessage(json.toString());
			sendMessageToUser(oUser,returnMessage);

		}

		// operationType ==8  取消锁单，需要把付款人的购物车更新，（ 这期间可能有其他人点菜 ）
		else if(operationType.toUpperCase().equals("8")){

			// 把其他人的购物车给 付款的这个人， 因为付款人的购物车不是最新的， 在他付款期间，可能有其他人点餐。
			for (OnLineUsers onLineUsers : wsUsers) {
				// 获取其他已点菜用户的购物车。
				if( !randomId.equals(onLineUsers.getCliendId()) && company.equals(onLineUsers.getCompany()) && shop.equals(onLineUsers.getShop()) && tableNo.equals(onLineUsers.getTableNo()) ){
					if(scanType.equals("0")) {
						// 判断ScanType==0(正餐-后结) 增加单号限制
						if(saleNo.equals(onLineUsers.getSaleNo())){
							if(onLineUsers.getShoppingCart() == null || onLineUsers.getShoppingCart().size() < 1){
								shoppingCart = new JSONArray();
								break;
							}
							else{
								shoppingCart = onLineUsers.getShoppingCart();
								break;
							}
						}

					}else{
						if(onLineUsers.getShoppingCart() == null || onLineUsers.getShoppingCart().size() < 1){
							shoppingCart = new JSONArray();
							break;
						}
						else{
							shoppingCart = onLineUsers.getShoppingCart();
							break;
						}
					}


				}
			}

			for (OnLineUsers onLineUsers : wsUsers) {
				if(randomId.equals(onLineUsers.getCliendId())){
					onLineUsers.setIsPayer("N");
					onLineUsers.setShoppingCart(shoppingCart);
					oUser = onLineUsers;
				}
			}

			json.put("shoppingCart", shoppingCart);

//	        TextMessage returnMessage = new TextMessage(json.toString());
//	        users.get(randomId).sendMessage(returnMessage);

			TextMessage returnMessage = new TextMessage(json.toString());
			sendMessageToUser(oUser,returnMessage);

		}

		// 2020-11-30 增加以下操作： 若客户端发出清空购物车指令， 服务端需清空当前桌台的单号saleNo， 购物车 shoppingCart
		else if(operationType.toUpperCase().equals("9")){

			boolean isExit = false;

			for (OnLineUsers onLineUsers : wsUsers) {
				if(randomId.equals(onLineUsers.getCliendId())){
					isExit = true;
					oUser = onLineUsers;
					HelpTools.writelog_fileName(" "+randomId+"已存在,购物车："+onLineUsers.getShoppingCart(),"WebSocketLog");
				}

			}
			HelpTools.writelog_fileName(" "+randomId+"日志:1","WebSocketLog");
			if(isExit == false ) {
				// 当前在线用户不存在的时候，添加进去
				HelpTools.writelog_fileName(" "+randomId+"日志:1-0","WebSocketLog");
				oUser.setCliendId(randomId);
				HelpTools.writelog_fileName(" "+randomId+"日志:1-0企业编号:"+company,"WebSocketLog");
				oUser.setCompany(company);
				HelpTools.writelog_fileName(" "+randomId+"日志:1-0门店:"+shop,"WebSocketLog");
				oUser.setShop(shop);
				HelpTools.writelog_fileName(" "+randomId+"日志:1-0桌台:"+tableNo,"WebSocketLog");
				oUser.setTableNo(tableNo);
				HelpTools.writelog_fileName(" "+randomId+"日志:1-0销售单号:"+saleNo,"WebSocketLog");
				oUser.setSaleNo(saleNo);
				HelpTools.writelog_fileName(" "+randomId+"日志:1-0扫码类型:"+scanType,"WebSocketLog");
				oUser.setScanType(scanType);
				oUser.setIsPayer("N");//新用户， 所传json中 operationType 不是7
				oUser.setUsers(users);
				HelpTools.writelog_fileName(" "+randomId+"日志:2","WebSocketLog");
				wsUsers.add(oUser);
			}


			HelpTools.writelog_fileName(" "+randomId+"日志:3","WebSocketLog");

			// 给客户端发送信息，清空购物车
			JSONObject clearJson = new JSONObject();

			clearJson = JSONObject.parseObject(reqMsg);
			clearJson.put("shoppingCart", new JSONArray());
			clearJson.put("operationType", 9);
			HelpTools.writelog_fileName(" "+randomId+"日志:4","WebSocketLog");
			for (OnLineUsers onLineUsers : wsUsers) {
				if( company.equals(onLineUsers.getCompany()) && shop.equals(onLineUsers.getShop()) && tableNo.equals(onLineUsers.getTableNo()) ){
					if(scanType.equals("0")) {
						HelpTools.writelog_fileName(" "+randomId+"日志:5","WebSocketLog");
						// 判断ScanType==0(正餐-后结) 增加单号限制
						if (saleNo.equals(onLineUsers.getSaleNo())) {
							if(randomId.equals(onLineUsers.getCliendId()) || onLineUsers.getIsPayer().equals("Y")){
								clearJson.put("SaleNo", saleNo);
							}else{
								clearJson.put("SaleNo", "");
							}
							HelpTools.writelog_fileName(" "+randomId+"日志:6","WebSocketLog");
							onLineUsers.setShoppingCart(new JSONArray());
							onLineUsers.setSaleNo("");
							onLineUsers.setIsPayer("N");
							HelpTools.writelog_fileName(" "+randomId+"日志:7","WebSocketLog");
							TextMessage returnMessage = new TextMessage(clearJson.toString());
							// 同桌其他人要清空单号
							users.get(onLineUsers.getCliendId()).sendMessage(returnMessage);
							HelpTools.writelog_fileName("向"+onLineUsers.getCliendId()+"发送的信息是："+clearJson.toString(),"WebSocketLog");
							HelpTools.writelog_fileName(" "+randomId+"日志:8","WebSocketLog");
							oUser = onLineUsers;
						}
					}else{
						if(randomId.equals(onLineUsers.getCliendId()) || onLineUsers.getIsPayer().equals("Y")){
							clearJson.put("SaleNo", saleNo);
						}else{
							clearJson.put("SaleNo", "");
						}
						HelpTools.writelog_fileName(" "+randomId+"日志:9","WebSocketLog");
						onLineUsers.setShoppingCart(new JSONArray());
						onLineUsers.setSaleNo("");
						onLineUsers.setIsPayer("N");

						TextMessage returnMessage = new TextMessage(clearJson.toString());
						// 同桌其他人要清空单号
						users.get(onLineUsers.getCliendId()).sendMessage(returnMessage);
						HelpTools.writelog_fileName(" "+randomId+"日志:10","WebSocketLog");
						HelpTools.writelog_fileName("向"+onLineUsers.getCliendId()+"发送的信息是："+clearJson.toString(),"WebSocketLog");

						oUser = onLineUsers;
					}

				}

			}

			//付款人要返回单号，用于查订单详情
//	    	clearJson.put("SaleNo", saleNo);
//	    	TextMessage returnMessage = new TextMessage(clearJson.toString());
//	    	users.get(randomId).sendMessage(returnMessage);

//	    	HelpTools.writelog_fileName(" "+randomId+"支付成功给同桌人发送的信息是："+clearJson.toString() ,"WebSocketLog");
//	    	sendMessageToUser(oUser,returnMessage);

		}
		else {
			boolean isExit = false;
			HelpTools.writelog_fileName(" "+randomId+"日志:11","WebSocketLog");
			for (OnLineUsers onLineUsers : wsUsers) {
				if(randomId.equals(onLineUsers.getCliendId())){
					isExit = true;
					oUser = onLineUsers;
					HelpTools.writelog_fileName(" "+randomId+"日志:12","WebSocketLog");
					HelpTools.writelog_fileName(" "+randomId+"已存在,购物车："+onLineUsers.getShoppingCart(),"WebSocketLog");
				}

			}

			if(isExit == false ) {
				// 当前在线用户不存在的时候，添加进去
				oUser.setCliendId(randomId);
				HelpTools.writelog_fileName(" "+randomId+"日志:13-0公司:"+company,"WebSocketLog");
				HelpTools.writelog_fileName(" "+randomId+"日志:13-0门店:"+shop,"WebSocketLog");
				HelpTools.writelog_fileName(" "+randomId+"日志:13-0桌台:"+tableNo,"WebSocketLog");
				HelpTools.writelog_fileName(" "+randomId+"日志:13-0单号:"+saleNo,"WebSocketLog");
				oUser.setCompany(company);
				oUser.setShop(shop);
				oUser.setTableNo(tableNo);
				oUser.setSaleNo(saleNo);
				oUser.setIsPayer("N");//新用户， 所传json中 operationType 不是7
				oUser.setScanType(scanType);
				oUser.setUsers(users);
				HelpTools.writelog_fileName(" "+randomId+"日志:13","WebSocketLog");
				//如果当前用户购物车为空， 可能是刚进来的用户， 判断其他用户购物车有没有
				if(shoppingCart == null || shoppingCart.size() < 1){
					HelpTools.writelog_fileName(" "+randomId+"日志:14","WebSocketLog");
					for (OnLineUsers onLineUsers : wsUsers) {
						HelpTools.writelog_fileName(" "+randomId+"日志:15","WebSocketLog");
						// 获取已点菜用户的购物车。
						if(!randomId.equals(onLineUsers.getCliendId()) && company.equals(onLineUsers.getCompany()) && shop.equals(onLineUsers.getShop()) && tableNo.equals(onLineUsers.getTableNo()) ){
							if(scanType.equals("0")) {
								// 判断ScanType==0(正餐-后结) 增加单号限制
								HelpTools.writelog_fileName(" "+randomId+"日志:16","WebSocketLog");
								if (saleNo.equals(onLineUsers.getSaleNo())) {
									saleNo = onLineUsers.getSaleNo();
									HelpTools.writelog_fileName(" "+randomId+"日志:17","WebSocketLog");
									if(onLineUsers.getShoppingCart() == null || onLineUsers.getShoppingCart().size() < 1){
										shoppingCart = new JSONArray();
										HelpTools.writelog_fileName(" "+randomId+"日志:18","WebSocketLog");
										break;
									}
									else{
										HelpTools.writelog_fileName(" "+randomId+"日志:19","WebSocketLog");
										shoppingCart = onLineUsers.getShoppingCart();
										break;
									}
								}

							}else{
								HelpTools.writelog_fileName(" "+randomId+"日志:20","WebSocketLog");
								saleNo = onLineUsers.getSaleNo();
								if(onLineUsers.getShoppingCart() == null || onLineUsers.getShoppingCart().size() < 1){
									shoppingCart = new JSONArray();
									HelpTools.writelog_fileName(" "+randomId+"日志:21","WebSocketLog");
									break;
								}
								else{
									HelpTools.writelog_fileName(" "+randomId+"日志:22","WebSocketLog");
									shoppingCart = onLineUsers.getShoppingCart();
									break;
								}
							}

						}
					}
					HelpTools.writelog_fileName(" "+randomId+"日志:23","WebSocketLog");
					oUser.setSaleNo(saleNo);
					oUser.setShoppingCart(shoppingCart);
//			        if(shoppingCart != null && shoppingCart.size() > 0 ){
					wsUsers.add(oUser);
//					}
					json.put("SaleNo", saleNo);
					json.put("shoppingCart", shoppingCart);
					TextMessage returnMessage = new TextMessage(json.toString());
					users.get(randomId).sendMessage(returnMessage);
					HelpTools.writelog_fileName(" "+randomId+"日志:24","WebSocketLog");
					HelpTools.writelog_fileName("新用户进来没点菜，最终给到信息是："+returnMessage.getPayload()+"结束","WebSocketLog");
				}

				else{

					for (OnLineUsers onLineUsers : wsUsers) {
						HelpTools.writelog_fileName(" "+randomId+"日志:25","WebSocketLog");
						// 获取已点菜用户的购物车。
						if(!randomId.equals(onLineUsers.getCliendId()) && company.equals(onLineUsers.getCompany()) && shop.equals(onLineUsers.getShop()) && tableNo.equals(onLineUsers.getTableNo()) ){
							if(scanType.equals("0")) {
								HelpTools.writelog_fileName(" "+randomId+"日志:26","WebSocketLog");
								// 判断ScanType==0(正餐-后结) 增加单号限制
								if (saleNo.equals(onLineUsers.getSaleNo())) {
									saleNo = onLineUsers.getSaleNo();
									break;
								}
							}else{
								HelpTools.writelog_fileName(" "+randomId+"日志:27","WebSocketLog");
								saleNo = onLineUsers.getSaleNo();
								break;
							}

						}
					}

					oUser.setSaleNo(saleNo);
					oUser.setShoppingCart(shoppingCart);
					wsUsers.add(oUser);
					json.put("shoppingCart", shoppingCart);
					json.put("SaleNo", saleNo);
					HelpTools.writelog_fileName(" "+randomId+"日志:28","WebSocketLog");
					TextMessage returnMessage = new TextMessage(json.toString());
					sendMessageToUser(oUser,returnMessage);
					HelpTools.writelog_fileName("第二（用户不存在，但购物车不是空）： 最终给到信息是："+returnMessage.getPayload()+"结束","WebSocketLog");
				}

			}
			else{

				for (OnLineUsers onLineUsers : wsUsers) {
					// 获取已点菜用户的购物车。
					HelpTools.writelog_fileName(" "+randomId+"日志:30","WebSocketLog");
					if(company.equals(onLineUsers.getCompany()) && shop.equals(onLineUsers.getShop()) && tableNo.equals(onLineUsers.getTableNo()) ){
						if(scanType.equals("0")) {
							// 判断ScanType==0(正餐-后结) 增加单号限制
							if (saleNo.equals(onLineUsers.getSaleNo())) {
								onLineUsers.setShoppingCart(shoppingCart);
								HelpTools.writelog_fileName(" "+randomId+"日志:31","WebSocketLog");
								if(!randomId.equals(onLineUsers.getCliendId()) && !Check.Null(onLineUsers.getSaleNo() )  ){
									saleNo = onLineUsers.getSaleNo();
									HelpTools.writelog_fileName(" "+randomId+"日志:32","WebSocketLog");
								}
							}
						}else{
							onLineUsers.setShoppingCart(shoppingCart);
							HelpTools.writelog_fileName(" "+randomId+"日志:33","WebSocketLog");
							if(!randomId.equals(onLineUsers.getCliendId()) && !Check.Null(onLineUsers.getSaleNo() )  ){
								saleNo = onLineUsers.getSaleNo();
								HelpTools.writelog_fileName(" "+randomId+"日志:34","WebSocketLog");
							}
						}

					}
				}

				oUser.setSaleNo(saleNo);
				oUser.setShoppingCart(shoppingCart);
				json.put("shoppingCart", shoppingCart);
				json.put("SaleNo", saleNo);
				HelpTools.writelog_fileName(" "+randomId+"日志:35","WebSocketLog");
				TextMessage returnMessage = new TextMessage(json.toString());
				sendMessageToUser(oUser,returnMessage);
				HelpTools.writelog_fileName("第三（当前用户已存在）： 最终给到信息是："+returnMessage.getPayload()+"结束","WebSocketLog");
			}
		}

	}

	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

		HelpTools.writelog_fileName("*************** 传输出现异常，关闭websocket连接...  ","WebSocketLog");

//        CheckOrder(CompanyNo, Shop, TableNo, SaleNo);

		logger.debug("\r\n传输出现异常，关闭websocket连接... ");
//        String userId= (String) session.getAttributes().get(USER_ID);
//        users.remove(userId);
		String randomId= (String) session.getAttributes().get(RANDOM_ID);
		users.remove(randomId);

		Iterator<OnLineUsers> it_b = wsUsers.iterator();

		while(it_b.hasNext()){
			OnLineUsers ou = it_b.next();
			if (ou.getCliendId().equals(randomId)) {
				it_b.remove();
			}
		}

		if(session.isOpen()){
			session.close();
		}

	}

	public boolean supportsPartialMessages() {

		return false;
	}


	/**
	 * 给某个用户发送消息
	 *
	 * @param userId
	 * @param message
	 */
	public String sendMessageToUser(String userId, TextMessage message) {
		String result = "";
		for (String id : users.keySet()) {
			if (id.equals(userId)) {
				try {
					if (users.get(id).isOpen()) {
						users.get(id).sendMessage(message);
						result = "消息发送成功了："+ message.toString();

						System.out.println("单发："+result);

					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
		}
		return result;
	}

	/**
	 * 给所有在线用户发送消息
	 *
	 * @param message
	 */
	public String sendMessageToUsers(String userId,TextMessage message) {
		String result = "";
		for (String id : users.keySet()) {
			if (id.equals(userId)){break;}
			try {
				if (users.get(id).isOpen()) {
					users.get(id).sendMessage(message);
					result = "消息发送成功了："+ message.toString();

					System.out.println("群发："+result);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return result;
	}



	public String sendMessageToUser(OnLineUsers oUser,TextMessage message) throws IOException {
		String allResult = "";
		try {
			String company = oUser.getCompany();
			String shop = oUser.getShop();
			String tableNo = oUser.getTableNo();

			String scanType = oUser.getScanType();
			String saleNo = oUser.getSaleNo();

			for (OnLineUsers ou : wsUsers) {
				String clientId = ou.getCliendId();

				if(ou.getCompany().equals(company) && ou.getShop().equals(shop)
						&& ou.getTableNo().equals(tableNo) && (Check.Null(ou.getIsPayer()) || ou.getIsPayer().equals("N"))
//						&& ou.getSaleNo().equals(saleNo)
				){
					if(scanType.equals("0")) {
						// 判断ScanType==0(正餐-后结) 增加单号限制
						if (saleNo.equals(ou.getSaleNo())){
							for (String id : users.keySet()) {
								HelpTools.writelog_fileName("*************** 需要发送信息 的 clientId ： "+clientId  ,"WebSocketLog");

								if (id.equals(clientId)){
									try {
										if (users.get(id).isOpen()) {
											TextMessage returnMessage = new TextMessage(message.getPayload());
											users.get(id).sendMessage(returnMessage);
											HelpTools.writelog_fileName("*************** 已发送的 clientId ： "+clientId  ,"WebSocketLog");

											System.out.println("已发送："+ id);
										}
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}
						}

					}else{
						for (String id : users.keySet()) {
							HelpTools.writelog_fileName("*************** 需要发送信息 的 clientId ： "+clientId  ,"WebSocketLog");

							if (id.equals(clientId)){
								try {
									if (users.get(id).isOpen()) {
										TextMessage returnMessage = new TextMessage(message.getPayload());
										users.get(id).sendMessage(returnMessage);
										HelpTools.writelog_fileName("*************** 已发送的 clientId ： "+clientId  ,"WebSocketLog");

										System.out.println("已发送："+ id);
									}
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}



				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return allResult;
	}


	private boolean CheckOrder(String companyNo , String shopId , String tableNo , String saleNo) throws Exception{

		boolean result = false;

		String sql = " select * from DCP_SALE where eId = '"+companyNo+"' and shopId = '"+shopId+"' and saleNo = '"+saleNo+"' " ;
		List<Map<String , Object>> orderDatas = dao.executeQuerySQL(sql,null);

		HelpTools.writelog_fileName("*****校验订单是否支付："+saleNo,"WebSocketLog");
		System.out.println("*****校验订单是否支付："+saleNo );

		// 如果当前传入单号已经支付成功， 同桌其他人清空购物车， 清空单号。
		// 作用：当用户线下支付的时候，无法由前端主动发起线上socket 通知， 需要服务端查询该笔订单是否支付成功（是否有销售单），若成功，则同桌其他人清空购物车， 清空单号。
		if(orderDatas != null && orderDatas.size() > 0){
			result = true;
			OnLineUsers oUser = new OnLineUsers();

			for (OnLineUsers onLineUsers : wsUsers) {
				if(companyNo.equals(onLineUsers.getCompany()) && shopId.equals(onLineUsers.getShop()) && tableNo.equals(onLineUsers.getTableNo()) ){
					onLineUsers.setShoppingCart(new JSONArray());
					onLineUsers.setSaleNo("");
					onLineUsers.setIsPayer("N");
					oUser = onLineUsers;
				}
			}

			oUser.setCliendId(RANDOM_ID);
			oUser.setCompany(companyNo);
			oUser.setShop(shopId);
			oUser.setTableNo(tableNo);
			oUser.setSaleNo(saleNo);
			oUser.setIsPayer("N");
			oUser.setUsers(users);

			// 给客户端发送信息，清空购物车
			JSONObject clearJson = new JSONObject();

			clearJson.put("Company", companyNo);
			clearJson.put("TableNo", tableNo);
			clearJson.put("Shop", shopId);
			clearJson.put("SaleNo", "");
			clearJson.put("shoppingCart", new JSONArray());
			clearJson.put("operationType", 9);
			TextMessage returnMessage = new TextMessage(clearJson.toString());

			HelpTools.writelog_fileName(" "+RANDOM_ID+" 检测到支付成功给同桌人发送的信息是："+clearJson.toString() ,"WebSocketLog");

			sendMessageToUser(oUser,returnMessage);

		}
		else{
			result = false;
		}

		HelpTools.writelog_fileName("*****订单是否支付："+result,"WebSocketLog");
		System.out.println("*****订单是否支付："+result );

		return result;

	}

}