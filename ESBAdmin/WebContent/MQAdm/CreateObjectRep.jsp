<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="com.ibm.MQAdmin.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>

<html>
<script type="text/javascript">
  function unhide(divID) {
    var item = document.getElementById(divID);
    if (item) {
      item.className=(item.className=='hidden')?'unhidden':'hidden';
    }
  }
</script>
<head>
<meta http-equiv="Content-Style-Type" content="text/css">
<style type="text/css">
<%@ include file="../Style.css" %>
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
		Looks like you are not logged in.<br>
		
		Please login with a valid user id <a href='../Index.html'><b>Here</b> </a>
		</center>

<%	
}else{
	
	if(session.getAttribute("UserID").equals("admin")){
try{
	String qmDtls = session.getAttribute(request.getParameter("qMgr")).toString();
	String qMgr = qmDtls.substring(qmDtls.indexOf('|') + 1,qmDtls.indexOf(':'));
	String qPort = qmDtls.substring(qmDtls.indexOf(':') + 1,qmDtls.length());
	String qHost = qmDtls.substring(0, qmDtls.indexOf('|'));
	
	String UserID = session.getAttribute("UserID").toString();
	Util newUtil = new Util();
	
	PCFCommons newPFCCM = new PCFCommons();
	System.out.println("qname");
	if (request.getParameter("qName") != null){
		String resOutput = newPFCCM.createQueue(qHost, 
				Integer.parseInt(qPort), 
				request.getParameter("qType").toString(), 
				request.getParameter("qName").toString(), 
				Boolean.parseBoolean(request.getParameter("xmitType")),
				request.getParameter("backoutQName").toString());
		%>
								<table border=1 align=center class="gridtable">
										<tr><td>Queue Name</td><td><%=request.getParameter("qName")%></td></tr>
										<tr><td>Queue Type</td><td><%=request.getParameter("qType")%></td></tr>
										<tr><td>Backout Queue Name</td><td><%=request.getParameter("backoutQName")%></td></tr>
								</table>
		
								<center><b><%=resOutput%></b></center>
		<%
		
		
	}

	System.out.println("channel");
	if (request.getParameter("chanName")!= null){
		newPFCCM.createChannel(qHost, 
				Integer.parseInt(qPort), 
				request.getParameter("chanType").toString(), 
				request.getParameter("chanName").toString(), 
				request.getParameter("xmitQueue"));
		%>
		<table border=1 align=center >
				<tr><td>Channel Name</td><td><%=request.getParameter("chanName")%></td></tr>
				<tr><td>Channel Type</td><td><%=request.getParameter("chanType")%></td></tr>
				<tr><td>Connecting Qmgr IP</td><td><%=request.getParameter("targetQmgrIP")%></td></tr>
				<tr><td>Connecting Qmgr Port</td><td><%=request.getParameter("targetQmgrPort")%></td></tr>
				<tr><td>Transmit Queue</td><td><%=request.getParameter("xmitQueue")%></td></tr>

		</table>
		<%
	}
	System.out.println("listener");
	if (request.getParameter("listName")!= null){
		int portNum;
		System.out.println(request.getParameter("portNum"));
		if(request.getParameter("portNum").toString().equals("")){
			portNum = 0; 	
		}else{
			portNum = Integer.parseInt(request.getParameter("portNum").toString());
		}
		
		newPFCCM.createListener(qHost, 
				Integer.parseInt(qPort), 
				request.getParameter("listType").toString(), 
				request.getParameter("listName").toString(), 
				portNum);
		%>
		<table border=1 align=center >
				<tr><td>Listener Name</td><td><%=request.getParameter("listName")%></td></tr>
				<tr><td>Listener Type</td><td><%=request.getParameter("listType")%></td></tr>
				<tr><td>Port Number</td><td><%=request.getParameter("portNum")%></td></tr>
		</table>
		<%
	}

	
	if (request.getParameter("topicName")!= null){
		newPFCCM.createTopic(qHost, 
				Integer.parseInt(qPort), 
				request.getParameter("topicName").toString(), 
				request.getParameter("topicString").toString(), 
				request.getParameter("topicDesc").toString());

		%>
		<table border=1 align=center >
				<tr><td>Topic Name</td><td><%=request.getParameter("topicName")%></td></tr>
				<tr><td>Topic String</td><td><%=request.getParameter("topicString")%></td></tr>
				<tr><td>Topic Description</td><td><%=request.getParameter("topicDesc")%></td></tr>
		</table>
		<%
	}
	

	if (request.getParameter("subName")!= null){
		newPFCCM.createSub(qHost, 
				Integer.parseInt(qPort), 
				request.getParameter("subName").toString(), 
				request.getParameter("topicString").toString(), 
				request.getParameter("subTopicName").toString(),
				request.getParameter("subDest").toString(),
				request.getParameter("subDestQM").toString(),
				request.getParameter("subUsrID").toString());
		%>
		<table border=1 align=center >
		
				<tr><td>Subscription Name</td><td><%=request.getParameter("subName")%></td></tr>
				<tr><td>Topic String</td><td><%=request.getParameter("topicString")%></td></tr>
				<tr><td>Topic Name</td><td><%=request.getParameter("subTopicName")%></td></tr>
				<tr><td>Subscription Destination</td><td><%=request.getParameter("subDest")%></td></tr>
				<tr><td>Subscription Destination Queue Manager</td><td><%=request.getParameter("subDestQM")%></td></tr>
				<tr><td>Subscription User ID</td><td><%=request.getParameter("subUsrID")%></td></tr>
		</table>
		<%
	}
	
	
		}catch(Exception e){
			e.printStackTrace();
			%>
			<center> <b>Experienced the following error  - </b></center><br>
			<%
		    for (StackTraceElement element : e.getStackTrace()) {
		    	%>
		        <%=element.toString()%><br>
		        <%
		    }
		}
	}
}

System.gc();
	 %>
</body>
</html>