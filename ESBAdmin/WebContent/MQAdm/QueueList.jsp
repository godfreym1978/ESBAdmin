<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="com.ibm.MQAdmin.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="com.ibm.mq.constants.MQConstants"%>
<%@ page import="org.apache.commons.csv.*"%>
<%@ page
	import="org.apache.commons.fileupload.*,org.apache.commons.io.*,java.io.*"%>

<html>
<head>
<meta http-equiv="Content-Style-Type" content="text/css">
<style type="text/css">
<%@
include
 
file
="../Style.css"
 
%>
</style>
<title>Get Topic List</title>
</head>
<body>
	<title>Browse Messages</title>
</head>
<body>
	<%if(session.getAttribute("UserID")==null){
%>

	<center>
		Looks like you are not logged in.<br> Please login with a valid
		user id <a href='../Index.html'><b>Here</b> </a>
	</center>

	<%	
}else{
	try{ 
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
										
			Util newUtil = new Util();
		
			PCFCommons newPFCCM = new PCFCommons();
			
			List<Map> alQueueList = newPFCCM.ListQueueNamesDtl(
		 			qHost, Integer.parseInt(qPort), qChannel);
		 			
			System.out.println("ehllo");
 %>

	<table border=1 align=center class="gridtable">
		<tr>
			<th><b>Queue Name</b></th>
			<th><b>Setup for Admin</b></th>
		</tr>
		<form action='AddQueueAdmin.jsp'>
			<input type=text name=qMgr value='<%=qMgr%>' hidden>
			<%
					int inCrement = 0;
					int iCount = 0;
					int inMsgCtr = 0;
					iCount = alQueueList.size();

					while (inCrement < iCount) {
						%>
				<tr>
					<td><a
						href="QueueDtl.jsp?qName=<%=alQueueList.get(inCrement).get("MQCA_Q_NAME")%>&qMgr=<%=qMgr%>">
						<%=alQueueList.get(inCrement).get("MQCA_Q_NAME")%></a></td>
					<%if(alQueueList.get(inCrement).get("MQIA_Q_TYPE").equals(MQConstants.MQQT_LOCAL)){%>
					<td><input type="checkbox" name="Queue"
						value="<%=alQueueList.get(inCrement).get("MQCA_Q_NAME")%>"></td>
					<%}%>
				
				</tr>
				<%
							inCrement++;
					}
				%>
				<tr>
					<td align=center colspan=3><input type="Submit" name="Submit"
						value="Submit"></td>
				</tr>
	</table>
	</form>
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

//System.gc();
	 %>
</body>
</html>