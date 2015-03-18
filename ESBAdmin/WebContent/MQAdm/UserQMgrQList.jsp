<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="com.ibm.MQAdmin.*"%>
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
<body>
	<%if(session.getAttribute("UserID")==null){%>

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
						
			File userFile = new File(System.getProperty("catalina.base")+ File.separator+"ESBAdmin"
								+File.separator+session.getAttribute("UserID").toString()+File.separator
								+qMgr);
					
			if(userFile.exists()){
				ArrayList qList = new ArrayList();
							
				for (String line : FileUtils.readLines(userFile)) {
					qList.add(line);
				}
				List<Map> qDepthList = new ArrayList<Map>();
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
