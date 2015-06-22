<!-- 
/********************************************************************************/
/* */
/* Project: ESBAdmin */
/* Author: Godfrey Peter Menezes */
/* 
Copyright © 2015 Godfrey P Menezes
All rights reserved. This code or any portion thereof
may not be reproduced or used in any manner whatsoever
without the express written permission of Godfrey P Menezes(godfreym@gmail.com).

*/
/********************************************************************************/
 -->
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="com.ibm.esbadmin.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page
	import="org.apache.commons.fileupload.*,org.apache.commons.io.*,java.io.*"%>
<%@ page import="org.apache.commons.csv.*"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">
<%@ include file="../Style.css"%>
</style>
<title>Get Queue List</title>
</head>
<%
if(session.getAttribute("UserID")==null){%>
<center>
	Looks like you are not logged in.<br> Please login with a valid
	user id <a href='../Index.html'><b>Here</b> </a>
</center>
<%	
}else{
	String UserID = session.getAttribute("UserID").toString();
	try{
		String qMgr = request.getParameter("qMgr");
		String qPort = null;
		String qHost = null;
		String qChannel = null;

		MQAdminUtil newMQAdUtil = new MQAdminUtil();
		List<Map<String, String>> MQList = newMQAdUtil.getQMEnv(UserID);

		for (int i=0; i<MQList.size(); i++) {
			if(MQList.get(i).get("QMName").toString().equals(qMgr)){
				qHost = MQList.get(i).get("QMHost").toString();
				qPort = MQList.get(i).get("QMPort").toString();
				qChannel = MQList.get(i).get("QMChannel").toString();
				break;
			}
		}
		Util newUtil = new Util();
						
		File userFile = new File(System.getProperty("catalina.base")+ File.separator+"ESBAdmin"
								+File.separator+session.getAttribute("UserID").toString()+File.separator
								+qMgr);
					
		if(userFile.exists()){
			ArrayList qList = new ArrayList();
							
			for (String line : FileUtils.readLines(userFile)) {
				qList.add(line);
			}
			List<Map<String, Object>> qDepthList = new ArrayList<Map<String, Object>>();
			qDepthList = newUtil.getDepthAll(qList, Integer.parseInt(qPort), qHost, qMgr, qChannel);
			%>
			<center>This Page gets the list of queues for the mentioned
					queue manager.</center>
			
			<table border=1 align=center class="gridtable">
				<tr>
					<th><b>Queue Name</b></th>
					<th><b>Queue Depth</b></th>
					<th><b>Browse Queue?</b></th>
					<th><b>Save Queue Messages?</b></th>
					<th><b>Purge Queue?</b></th>
					<th><b>Write File Data To Queue?</b></th>
					<th><b>Load Queue Messages?</b></th>
				</tr>
		
				<%
				int qCtr = 0;
				for (String line : FileUtils.readLines(userFile)) {
				%>
				<tr>
					<td><a href='QueueDtl.jsp?qName=<%=line%>&qMgr=<%=qMgr%>'><%=line%></a></td>
					<td><%=qDepthList.get(qCtr).get(line) %></td>
					<td><a href='MQBrowse.jsp?QName=<%=line%>&QMgr=<%=qMgr%>'> <b>YES</b>
					</a></td>
					<td><a
							href='../DownloadMsgsFromQueue?QName=<%=line%>&QMgr=<%=qMgr%>'>
								<b>YES</b>
					</a></td>

				<%if(UserID.indexOf("ba-") !=0){
					if((UserID.indexOf("dev-") ==0 && !qMgr.equals("QMBRKPRD01"))||UserID.equals("admin")){
				%>
					<td><a href='PurgeQueue.jsp?QName=<%=line%>&QMgr=<%=qMgr%>'>
								<b>YES</b>
						</a></td>
					<td><form action='MQWrite.jsp?QName=<%=line%>&QMgr=<%=qMgr%>'
								enctype="multipart/form-data" method="post">
								<input type="file" name="message"> <input type="submit"
									value="Submit" />
							</form></td>
					<td><form
								action='../LoadMsgsToQueue?QName=<%=line%>&QMgr=<%=qMgr%>'
								enctype="multipart/form-data" method="post">
								<input type="file" name="message"> <input type="submit"
									value="Submit" />
							</form></td>

					<%}else{%>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<%}
					}else{
					%>
					<td></td>
					<td></td>
					<td></td>
					<td></td>
					<%}
					qCtr++;
					}
				}else{
					%>
			<center>
				<b>No queues have been setup for access.</b><br> Click on 'Qs
				in QMgr' beneath the link you clicked to know which queues you can
				set up for access.
			</center>
			<%	
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
