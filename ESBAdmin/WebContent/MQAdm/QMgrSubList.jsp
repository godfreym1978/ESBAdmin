<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="com.ibm.MQAdmin.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page import="org.apache.commons.csv.*"%>
<%@ page
	import="org.apache.commons.fileupload.*,org.apache.commons.io.*,java.io.*"%>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">
<%@ include file="../Style.css" %>
</style>
<title>Get Topic List</title>
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

try{ 

					//session.setAttribute("UserID", UserID);
					
			String UserID = session.getAttribute("UserID").toString();
			File userQMFile = new File(
							System.getProperty("catalina.base")
									+ File.separator+"ESBAdmin"+File.separator+UserID+File.separator+"QMEnv.txt");
			String qMgr = request.getParameter("qMgr");
			String qPort = null;
			String qHost = null;
			String qChannel = null;

			for (String line : FileUtils.readLines(userQMFile)) {
				if (line.indexOf(qMgr)>0){
					CSVParser parser = CSVParser.parse(line, CSVFormat.RFC4180);
					
					for (CSVRecord csvRecord : parser) {
						qHost = csvRecord.get(0);
						qPort = csvRecord.get(2);
						qChannel = csvRecord.get(3);
						}							
				}
			}
					
					PCFCommons test = new PCFCommons();
					List<Map> topicDtls = test.ListSubNames(qHost, Integer.parseInt(qPort), qChannel);
					int listCtr =0;
					int listCount =topicDtls.size();
					%>
		<center><b><u>List of Subscriptions in Queue Manager - <%=qMgr %></u></b></center><br>
		<table border=1 align=center class="gridtable">
			<tr>
				<th><b>Sub Name</b></th>
				<th><b>Sub Topic Name</b></th>
				<th><b>Sub Topic String</b></th>
				<th><b>Destination</b></th>
				<th><b>Sub User ID</b></th>
				<th><b>Sub Creation Date</b></th>
				<th><b>Sub Creation Time</b></th>
				<th><b>Sub Alteration Date</b></th>
				<th><b>Sub Alteration Time</b></th>
				<th><b>Download MQSC Script</b></th>
			</tr>
			<%
					while(listCtr<listCount) {
			%>
			<tr>
				<td><%=topicDtls.get(listCtr).get("MQCACF_SUB_NAME")%></td>
				<td><%=topicDtls.get(listCtr).get("MQCA_TOPIC_NAME")%></td>
				<td><%=topicDtls.get(listCtr).get("MQCA_TOPIC_STRING")%></td>
				<td><%=topicDtls.get(listCtr).get("MQCACF_DESTINATION")%></td>
				<td><%=topicDtls.get(listCtr).get("MQCACF_SUB_USER_ID")%></td>
				<td><%=topicDtls.get(listCtr).get("MQCA_CREATION_DATE")%></td>
				<td><%=topicDtls.get(listCtr).get("MQCA_CREATION_TIME")%></td>
				<td><%=topicDtls.get(listCtr).get("MQCA_ALTERATION_DATE")%></td>
				<td><%=topicDtls.get(listCtr).get("MQCA_ALTERATION_TIME")%></td>
				<td><a
					href='../DownloadQObject?qMgr=<%=qMgr%>&objType=SUB&objName=<%=topicDtls.get(listCtr).get("MQCACF_SUB_NAME").toString()%>'
					> Download MQSC Script For This Subscription</a>
				</td>
				
				<%
				out.flush();
				listCtr++;
					}
					
				%>
		</table>
		<%
		}catch(Exception e){
			%>
			<center>
			We have encountered the following error<br>
			
			<font color=red><b><%=e%></b></font> 
			</center>
			<%
		}
}
System.gc();
%>
</body>
</html>
