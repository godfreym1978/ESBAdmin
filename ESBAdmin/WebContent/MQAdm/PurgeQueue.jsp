<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="com.ibm.MQAdmin.*"%>
<%@ page import="java.util.*"%>
<%@ page import="org.apache.commons.csv.*"%>
<%@ page
	import="org.apache.commons.fileupload.*,org.apache.commons.io.*,java.io.*"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">
<%@ include file="../Style.css" %>
</style>
<title>Insert title here</title>
</head>

<body>
	
		<center>
<%if(session.getAttribute("UserID")==null){
%>

		<center>
		Looks like you are not logged in.<br>
		
		Please login with a valid user id <a href='../Index.html'><b>Here</b> </a>
		</center>

<%	
}else{
			String UserID = session.getAttribute("UserID").toString();
			File userQMFile = new File(
							System.getProperty("catalina.base")
									+ File.separator+"ESBAdmin"+File.separator+UserID+File.separator+"QMEnv.txt");
			String qMgr = request.getParameter("QMgr");
			
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
			
				String qName = request.getParameter("QName");
				try {
						//String returnMsg = 
						int msgPurged = newPFCCM.purgeQueue(qHost,Integer.parseInt(qPort), qName, qChannel);
						
						if (msgPurged<0){
							msgPurged = newUtil.purgeQueue(qMgr, qName);
							%>
							<b><%=msgPurged%></b> Messages Purged from the Queue - <b><%=qName%></b>
							- Queue Manager <b><%=qMgr%></b>
							<%
						}else{
							%>
							Messages Cleared from the Queue - <b><%=qName%></b>
							- Queue Manager <b><%=qMgr%></b>
							<%
						}
				} catch (NullPointerException e) {
					System.out.println(session.getAttribute("UserID"));
			%>Are you logged in to system? If not do so in <a
				href='Index.html'>here </a>
			<%
				}
}
			%>
</body>
</html>