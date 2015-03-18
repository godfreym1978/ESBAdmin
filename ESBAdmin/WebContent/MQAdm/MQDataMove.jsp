<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="com.ibm.MQAdmin.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.net.*,java.io.*"%>
<%@ page
	import="org.apache.commons.fileupload.*,org.apache.commons.io.*"%>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">
<%@include file="../Style.css"%>
</style>
<title>Write Message to Queue</title>
</head>

<body>
	<center>
		<%
if(session.getAttribute("UserID")==null){
%>

	<center>
		Looks like you are not logged in.<br> Please login with a valid
		user id <a href='../Index.html'><b>Here</b> </a>
	</center>
	<%}else{

		String qmDtls = session.getAttribute(request.getParameter("qMgr")).toString();				
		String qMgr = qmDtls.substring(qmDtls.indexOf('|') + 1,qmDtls.indexOf(':'));
		String qPort = qmDtls.substring(qmDtls.indexOf(':') + 1,qmDtls.length());
		String qHost = qmDtls.substring(0, qmDtls.indexOf('|'));
		String UserID = session.getAttribute("UserID").toString();
		Util newUtil = new Util();

		PCFCommons newPFCCM = new PCFCommons();
	
		List<Map> alQueueList = newPFCCM.ListQueueNamesDtl(
	 			qHost, Integer.parseInt(qPort));

			%>
			<h3> Move messages from Source Queue to Target Queue and the message count</h3>
		<form action='MQDataMoveRep.jsp'>	
		<table border=1 align=center width=50% class="gridtable">
			<tr>
				<td>Queue Mgr</td>
				<td><input type=hidden name=qMgr value=<%=qMgr%>><%=qMgr%></td>
			</tr>
			<tr>
				<td>Source Queue</td>
				<td><select name=srcQueueName>


					<%
					int inCrement = 0;
					int iCount = 0;
					int inMsgCtr = 0;
					iCount = alQueueList.size();

					while (inCrement < iCount) {
						%>
						<option value="<%=alQueueList.get(inCrement).get("MQCA_Q_NAME").toString()%>"><%=alQueueList.get(inCrement).get("MQCA_Q_NAME").toString()%></option>
						<%
						inCrement++;
					}
					%>

				</select></td>
			</tr>
			<tr>
				<td>Target Queue</td>
				<td><select name=tarQueueName>

					<%
					inCrement = 0;
					iCount = 0;
					inMsgCtr = 0;
					iCount = alQueueList.size();

					while (inCrement < iCount) {
					%>
						<option value="<%=alQueueList.get(inCrement).get("MQCA_Q_NAME").toString()%>"><%=alQueueList.get(inCrement).get("MQCA_Q_NAME").toString()%></option>
					<%
						inCrement++;
					}
}
					%>

				</select></td>
			</tr>
			<tr><td>Message Count</td><td><input type="text" name="msgCount"></td></tr>
			<tr><td  align=center colspan=2><input type="Submit" name="Submit" value="Submit"></td></tr>
			
		</table>
</body>
</html>