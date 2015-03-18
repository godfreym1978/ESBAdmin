<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.io.*"%>
<%@ page import="com.ibm.MQAdmin.*"%>
<%@ page import="com.ibm.broker.config.proxy.*"%>
<%@ page import="java.util.*"%>
<%@ page
	import="org.apache.commons.fileupload.*,org.apache.commons.io.*,java.io.*"%>

<html>
<head>
<meta http-equiv="Content-Style-Type" content="text/css">
<style type="text/css">
<%@ include file="../Style.css" %>
</style>
<title>Insert title here</title>
</head>
<body>

<%if(session.getAttribute("UserID")==null){
%>

		<center>
		Looks like you are not logged in.<br>
		
		Please login with a valid user id <a href='../Index.html'><b>Here</b> </a>
		</center>

<%	
}else{
%>
<center><button type="button" onClick="window.location.reload();">Refresh</button></center>
<%

	Util newUtil = new Util();
	MBCommons newMBCmn = new MBCommons();

	File userFile = new File(System.getProperty("catalina.base")+File.separator+"ESBAdmin"+File.separator+session.getAttribute("UserID").toString()+File.separator+"MBEnv.txt");
	String hostName = new String();
	int portNum=0;
	String brokerName = request.getParameter("brokerName").toString();

	BrokerProxy brkProxy  = null;
	
	for (String line : FileUtils.readLines(userFile)) {
		if(line.substring(line.indexOf(";")+1,line.indexOf("|")).equals(brokerName)){
		hostName = line.substring(line.indexOf("|")+1, line.indexOf(":"));
		portNum = Integer.parseInt(line.substring(line.indexOf(":")+1,line.length()));
		brkProxy = newMBCmn.getBrokerProxy(hostName, portNum);
		}
	}

	LogProxy lp = brkProxy.getLog();
	int logCount = lp.getSize(); 
	String logMsg = new String();
%>
		<Table border=1 align=center width="100%" class="gridtable">
		<tr>
		<th width="10"><b>Message</b></th>
		<th width="60"><b>Detail</b></th>
		<th width="15"><b>Source</b></th>
		<th width="15"><b>Timestamp</b></th>
		</tr>
<%	
	while(logCount>0) {
		logMsg = lp.getLogEntry(logCount).getDetail();
		%>
		<tr>
		<td>
		<%=lp.getLogEntry(logCount).getMessage()%>
		</td>
		<td>
		<%=logMsg.substring(logMsg.indexOf(":")+2, logMsg.length()) %>
		</td>
		<td>
		<%=lp.getLogEntry(logCount).getSource()%>
		</td>
		<td>
		<%=lp.getLogEntry(logCount).getTimestamp() %>
		</td>
		</tr>
		<%
		out.flush();
		logCount--;
	}
System.gc();
lp.clear();
brkProxy.disconnect();
}
	
 %>
 </table>
</body>
</html>