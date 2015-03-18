
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="com.ibm.MQAdmin.*"%>
<%@ page import="java.io.*"%>
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
<title>Carter's Message Broker Environment</title>
</head>
<%if(session.getAttribute("UserID")==null){
%>

		<center>
		Looks like you are not logged in.<br>
		
		Please login with a valid user id <a href='../Index.html'><b>Here</b> </a>
		</center>

<%	
}else{
%>
<body>
<%
		String UserID = session.getAttribute("UserID").toString();
		Util newUtil = new Util();
		
		//MBCommons newMBCmn = new MBCommons();
		File userFile = new File(System.getProperty("catalina.base")+File.separator+"ESBAdmin"+File.separator+session.getAttribute("UserID").toString()+File.separator+"MBEnv.txt");
		String env = null;		
		String hostName = null;
		int portNum ;
		BrokerProxy brkProxy  = null;
		ArrayList<ExecutionGroupProxy> egProxy = null;
		int egCtr;
		int egCount;
		boolean chkFlag = false;
		String notRunning = new String("");
		%>
		<center><h3> Message Broker Environment</h3></center>
		<Table border=1 align=center class="gridtable">
			<tr>
				<th><b>Broker Details</b></th>
				<th><b>Broker EGs</b></th>
				<th><b>Broker Logs</b></th>
				<th><b>Broker EGs Running?</b></th>			
				<th><b>Broker EGs Action</b></th>
				
			</tr>

		<%
			for (String line : FileUtils.readLines(userFile)) {
				env = line.substring(0,line.indexOf(";"));
				hostName = line.substring(line.indexOf("|")+1, line.indexOf(":"));
				portNum = Integer.parseInt(line.substring(line.indexOf(":")+1,line.length()));
				try{
					//brkProxy = newMBCmn.getBrokerProxy(hostName, portNum);
					BrokerConnectionParameters bcp = new MQBrokerConnectionParameters(
							hostName, portNum, "");

					brkProxy = BrokerProxy.getInstance(bcp);

		%>
			<tr>
				<td><%=brkProxy.getName()%>
				<br>
				<a
					href='MBLogProxy.jsp?brokerName=<%=brkProxy.getName()%>'>
						<%=brkProxy.getName()%> Log </a>
				<br><font color=blue>Broker QM</font> - <%=brkProxy.getQueueManagerName()%> 
				<br><font color=blue>Broker OS</font> - <%=brkProxy.getBrokerOSName()%> 
				<br><font color=blue>Broker OS Version</font> - <%=brkProxy.getBrokerOSVersion()%> 
				<br><font color=blue>Broker Version</font> - <%=brkProxy.getBrokerVersion()%> 
				</td>
				<td>
					<%
					egProxy = Collections.list(brkProxy.getExecutionGroups(null));
					egCount = egProxy.size();
					for (egCtr=0;egCount>egCtr;egCtr++){
						%><a
					href='MBGetEG.jsp?brokerName=<%=brkProxy.getName()%>&egName=<%=egProxy.get(egCtr).getName()%>'>
						<%=egProxy.get(egCtr).getName()%></a><br> <%
					}
					%>
					</td>
					
				<td>					

					<%
					egProxy = Collections.list(brkProxy.getExecutionGroups(null));
					egCount = egProxy.size();
					for (egCtr=0;egCount>egCtr;egCtr++){
						%>
						<a href='MBActLogProxy.jsp?brokerName=<%=brkProxy.getName()%>&egName=<%=egProxy.get(egCtr).getName()%>'>
						<%=egProxy.get(egCtr).getName()%> Activity Log </a>
						<br> <%
					}
					%>

				</td>
				<td>
				<%
					egProxy = Collections.list(brkProxy
										.getExecutionGroups(null));
								egCount = egProxy.size();
								for (egCtr = 0; egCount > egCtr; egCtr++) {
									if(egProxy.get(egCtr).isRunning()){
										%>
										<font color=green>Running</font><br>
										<%						
									}else{
										 %>
										<font color=red>Stopped</font><br>
										<%
									}
			
								}%>
				</td>

				<td>
				
				<%if(UserID.equals("admin")){
					egCount = egProxy.size();
					for (egCtr = 0; egCount > egCtr; egCtr++) {
									
					%>
						<a
						href='../MBAdmin?action=EGstart&egName=<%=egProxy.get(egCtr).getName()%>&brkName=<%=brkProxy.getName()%>' >
							START - <%=egProxy.get(egCtr).getName()%></a> 
							| 
						<a
						href='../MBAdmin?action=EGstop&egName=<%=egProxy.get(egCtr).getName()%>&brkName=<%=brkProxy.getName()%>' >
							STOP - <%=egProxy.get(egCtr).getName()%></a>
							| 	 
						<a
						href='../MBAdmin?action=EGdelete&egName=<%=egProxy.get(egCtr).getName()%>&brkName=<%=brkProxy.getName()%>' >
							DELETE - <%=egProxy.get(egCtr).getName()%></a><br> 
					<%}
				}else if((env.equals("DEV")||env.equals("QA"))&&UserID.indexOf("dev-")>-1){
					egCount = egProxy.size();
					 
					for (egCtr = 0; egCount > egCtr; egCtr++) {
									
					%>
						<a
						href='../MBAdmin?action=EGstart&egName=<%=egProxy.get(egCtr).getName()%>&brkName=<%=brkProxy.getName()%>' >
							START - <%=egProxy.get(egCtr).getName()%></a> 
							| 
						<a
						href='../MBAdmin?action=EGstop&egName=<%=egProxy.get(egCtr).getName()%>&brkName=<%=brkProxy.getName()%>' >
							STOP - <%=egProxy.get(egCtr).getName()%></a>
							| 	 
						<a
						href='../MBAdmin?action=EGdelete&egName=<%=egProxy.get(egCtr).getName()%>&brkName=<%=brkProxy.getName()%>' >
							DELETE - <%=egProxy.get(egCtr).getName()%></a><br> 
					<%}
					
				}
				%>
				</td>
				
			</tr>
					<%
					brkProxy.disconnect();
					
					}catch(ConfigManagerProxyLoggedMQException e){
						e.printStackTrace();
						chkFlag = true;
						notRunning = notRunning+" "+hostName;
						}
					}
					%>
		</Table>
		<%
		if(chkFlag){
			%>
			<center><b>The following host dont have broker queue managers up and running <%=notRunning%></b></center>
			<%
		}
		egProxy.clear();
		
		System.gc();
	} %>
</body>
</html>