<%@ page import="com.ibm.MQAdmin.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
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
		
		Please login with a valid user id <a href='Index.html'><b>Here</b> </a>
		</center>

<%	
}else{

	String []newStr = request.getParameterValues("Queue");
	String qMgr = request.getParameter("qMgr").toString();
	List<String> setupQueue = new ArrayList<String>();
	
	Util newUtil = new Util();
	File queueFile = new File(System.getProperty("catalina.base")+ File.separator+"ESBAdmin"
								+File.separator+session.getAttribute("UserID").toString()+File.separator
								+qMgr);
	
	if (!queueFile.exists()){
		queueFile.createNewFile();
	}

	int lineCtr = 0;
	
	for (String line : FileUtils.readLines(queueFile)) {
		setupQueue.add(lineCtr, line);
		lineCtr++;
	}

	if(setupQueue.size()==0){
		int newQCtr;
		for (newQCtr=0;newQCtr<newStr.length;newQCtr++){
				if(newQCtr==(newStr.length-1)){
					FileUtils.writeStringToFile(queueFile, newStr[newQCtr].toString().trim(), true);	
				}else{
					FileUtils.writeStringToFile(queueFile, newStr[newQCtr].toString().trim()+"\n", true);
				}
				
				%>
				<center>Queue Name - <%=newStr[newQCtr].toString() %> - Set up for Admin<br></center>
				<%
		}
	}else{
	boolean checkFlag = true;
	for (int newQCtr=0;newQCtr<newStr.length;newQCtr++){
		for (int oldQCtr=0;oldQCtr<setupQueue.size();oldQCtr++){
			if (setupQueue.get(oldQCtr).toString().equals(newStr[newQCtr].toString().trim())){
				checkFlag = false;
				oldQCtr = setupQueue.size();
			}
		}
		if (checkFlag){
			FileUtils.writeStringToFile(queueFile, "\n"+newStr[newQCtr].toString().trim(), true);
			
		}
		checkFlag = true;
	%>
	<center>Queue Name - <%=newStr[newQCtr].toString() %> - Set up for Admin<br></center>
	<%
	}
	}
	
}
System.gc();
%>

</body>
</html>