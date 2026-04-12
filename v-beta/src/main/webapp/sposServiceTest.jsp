<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title>SPos Service Test</title>

<script type="text/javascript">
	
	function getXmlHttp() {
	   var xmlhttp;
	   if (window.XMLHttpRequest){// code for IE7+, Firefox, Chrome, Opera, Safari
	     xmlhttp=new XMLHttpRequest();
	   } else {// code for IE6, IE5
	     xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	   }
	   return xmlhttp;
	}
	
	function doOperate(){
	   var xmlhttp = getXmlHttp();
	   
	   xmlhttp.onreadystatechange = function() {
	     if (xmlhttp.readyState==4 && xmlhttp.status==200) { 
	    	document.getElementById("resContent").value = xmlhttp.responseText;
	     } else {
	   	    document.getElementById("resContent").value = xmlhttp.responseText;
	     }
	   }
	   
	   var xmlValue = encodeURIComponent(document.getElementById('xmlValue').value);
	   var by_post='json=' + xmlValue; //將變數放進字串
	   xmlhttp.open('POST', 'services/invoke', true);
	   xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');  //**重要一定要加上
	   xmlhttp.send(by_post);
  }

	function doProduceToken(){
		   var xmlhttp = getXmlHttp();
		   
		   xmlhttp.onreadystatechange = function() {
		     if (xmlhttp.readyState==4 && xmlhttp.status==200) { 
		    	document.getElementById("tokenProduce").innerHTML = xmlhttp.responseText;
		     } else {
		   	    document.getElementById("tokenProduce").innerHTML = xmlhttp.responseText;
		     }
		   }
		   var tokenXml = document.getElementById('tokenXmlValue').value;
		   var today = new Date();
		   var year = '' + today.getFullYear();
		   var month = (today.getMonth() + 1) < 10 ? '0' + (today.getMonth() + 1) : '' + (today.getMonth() + 1);
		   var day = today.getDate() < 10 ? '0' + today.getDate() : '' + today.getDate();
		   var hour = today.getHours() < 10 ? '0' + today.getHours() : '' + today.getHours();
		   var nowStr = year + month + day + hour; 
		   tokenXml = tokenXml.replace('tokenHashKeyChang', nowStr);
		   tokenXml = encodeURIComponent(tokenXml);
		   var by_post='json=' + tokenXml; //將變數放進字串
		   xmlhttp.open('POST', 'services/invoke', true);
		   xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');  //**重要一定要加上
		   xmlhttp.send(by_post);
  }
  
</script>

</head>
<body>
<h1>Token:<button type="button" onclick="doProduceToken()">Produce</button></h1>
<textarea id="tokenProduce" style="width:100%;height:150px" readOnly="true">
Produce [Universal Token] that is fake token, non use T100 WS Check..
Just for quickly test Service.
</textarea>

<!-- token start -->
<textarea id="tokenXmlValue" name="tonkenXml" style="visibility:hidden">
{
  "serviceId": "UserAuthCheck",
  "user" : "tokenHashKeyChang",
  "password" : "FAKE_PWD"
}
</textarea>
<!-- token end -->
<hr/>
<h1>Request:<button type="button" onclick="doOperate()">Submit</button></h1>
<textarea id="xmlValue" style="width:100%;height:200px" name="xml">
{
  "serviceId": "AAA",
  "token": "tokenHashKeyChang",
  "pageNumber": 1,
  "pageSize": 50,
  "invoice": "AAA",
  "shop": "AAA",
  "booth": "AAA",
  "checkDate": "2016/04/16",
  "inputDate": "2016/04/16",
  "inputUser": "TEST_USER",
  "confirmDate": "2016/04/16",
  "confirmUser": "TEST_USER",
  "confirmCode": "11111"
}
</textarea>
<hr/>
<h1>Response</h1>
<textarea id="resContent" style="width:100%;height:200px" name="xml"></textarea>
</body>
</html>