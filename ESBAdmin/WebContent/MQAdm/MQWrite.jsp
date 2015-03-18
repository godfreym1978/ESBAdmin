<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="com.ibm.MQAdmin.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.net.*,java.io.*"%>  
<%@ page import="org.apache.commons.fileupload.*,org.apache.commons.io.*" %>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">
<%@ include file="../Style.css" %>
</style>
<title>Write Message to Queue</title>
</head>

<body>
		<center>
			<%
if(session.getAttribute("UserID")==null){
%>

		<center>
		Looks like you are not logged in.<br>
		
		Please login with a valid user id <a href='../Index.html'><b>Here</b> </a>
		</center>
<%}else{
	

				// Create a new file upload handler 
				DiskFileUpload upload = new DiskFileUpload();

				// parse request
				List items = upload.parseRequest(request);

				// get qMgr Name
				String qMgr = new String();
				qMgr = request.getParameter("QMgr").toString();

				//get qName
				String qName = new String();
				qName = request.getParameter("QName").toString();

				//get uploaded file 
				FileItem file = (FileItem) items.get(0);
				String source = file.getName();

				File outfile = new File(System.getProperty("catalina.base")+File.separator+"upload.txt");
				file.write(outfile);

				Util newUtil = new Util();
				String returnMsg = newUtil.writeMessageToQueue(qMgr, qName,
						FileUtils.readFileToString(outfile));
			%>
			<table border=1 align=center width=100% class="gridtable">
				<tr>
					<td>Queue Manager</td>
					<td><%=qMgr%></td>
				</tr>
				<tr>
					<td>Queue Name</td>
					<td><%=qName%></td>
				</tr>
				<tr>
					<td>Message</td>
					<td><c:out value="<%=FileUtils.readFileToString(outfile)%>" /></td>
				</tr>
			</table>
			<%
				if (returnMsg.equalsIgnoreCase("Success")) {
					%>Message written to the queue successfully<%
				} else {
			%>
					Following Error occurred while writing the message to the queue
					<%=returnMsg %>
			<%} 
			}%>
</body>
</html>