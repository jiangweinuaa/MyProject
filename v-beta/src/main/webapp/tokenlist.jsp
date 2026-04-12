<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
    import="java.util.*, java.text.*"
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Token List</title>
</head>
<body>

<table style="width:100%" border=1>
  <tr>
  	<td>No</td>
    <td>Token Key</td>
    <td>Login Time</td>
    <td>Login User</td>
    <td>Is Can Use</td>
  </tr>
<%
Map<String, com.dsc.spos.service.utils.TokenManager.TokeBean>  data = com.dsc.spos.service.utils.TokenManager.getInstance().getTokenData();
String[] keys = data.keySet().toArray(new String[0]);
 java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
for (int i = 0; i < keys.length; i++) {
   String key = keys[i];
   com.dsc.spos.service.utils.TokenManager.TokeBean value = data.get(key);
%>

  <tr>
  	<td><%=i%></td>
    <td><%=key%></td>
    <td><%=sdf.format(value.getLoginTime())%></td>
    <td><%=value.getRes().getDatas().get(0).getOpNO()%></td>
    <td><%=com.dsc.spos.service.utils.TokenManager.getInstance().verify(key)%></td>
  </tr>
<%}%>
</table>
</body>
</html>