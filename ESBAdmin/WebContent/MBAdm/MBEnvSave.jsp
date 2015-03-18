
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
<title>Message Broker Environment</title>
</head>
<body>
	<%
	if(session.getAttribute("UserID")==null){
	%>
		<center>
		Looks like you are not logged in.<br>
		
		Please login with a valid user id <a href='../Index.html'><b>Here</b> </a>
		</center>
	
	<%	
	}else{
	%>
			<center><h3> Message Broker Environment</h3></center>
			<form action='MBEnvSave.jsp' method="post">

			<Table border=1 align=center class="gridtable">
				<tr>
					<th><b>Broker Environment</b></th>
					<th><b>Broker Name</b></th>
					<th><b>Broker IP Address/HostName</b></th>
					<th><b>Broker QM Port</b></th>
				</tr>
				<tr>
					<td><%=request.getParameter("brkEnv")%></td>
					<td><%=request.getParameter("brkName")%></td>
					<td><%=request.getParameter("brkHost")%></td>
					<td><%=request.getParameter("brkPort")%></td>
				</tr>
			</table>

	<%	
		
		String UserID = session.getAttribute("UserID").toString();
		Util newUtil = new Util();
		
		//MBCommons newMBCmn = new MBCommons();
		File userFile = new File(System.getProperty("catalina.base")+File.separator+"ESBAdmin"+
									File.separator+session.getAttribute("UserID").toString()+File.separator+"MBEnv.txt");
		if(!userFile.exists()){
			
			try{
				BrokerConnectionParameters bcp = new MQBrokerConnectionParameters(
							request.getParameter("brkHost"), Integer.parseInt(request.getParameter("brkPort")), "");
				BrokerProxy brkProxy = BrokerProxy.getInstance(bcp);
				brkProxy.disconnect();
				userFile.createNewFile();
				BufferedWriter output = new BufferedWriter(new FileWriter(userFile));
				output.write(request.getParameter("brkEnv")+";"+ 
				request.getParameter("brkName")+"|"+
				request.getParameter("brkHost")+":"+
				request.getParameter("brkPort"));
				output.close();
				%>
					<center>
    				The broker runtime with above details has been successfully registered.<br>
    				</center>
					<hr>
    			<%	
				
				}catch(Exception e){
				%>
					<center>
    				Cannot register with the following details.<br>
    				<%=e.getMessage()%>
    				</center>
					<hr>
    			<%	
				}
		}else{
			String newBrkEntry = new String(request.getParameter("brkEnv")+";"+ 
				request.getParameter("brkName")+"|"+
				request.getParameter("brkHost")+":"+
				request.getParameter("brkPort"));
			if(FileUtils.readFileToString(userFile).contains(newBrkEntry)){
			%>
					<center>
    				The broker runtime with above details has already been registered.<br>
    				</center>
					<hr>
			<%					
			}else{
				try{
					BrokerConnectionParameters bcp = new MQBrokerConnectionParameters(
								request.getParameter("brkHost"), Integer.parseInt(request.getParameter("brkPort")), "");
					BrokerProxy brkProxy = BrokerProxy.getInstance(bcp);
					brkProxy.disconnect();
					FileWriter fw = new FileWriter(userFile,true);
					fw.write("\n"+newBrkEntry);
					fw.close();
					%>
					<center>
    				The broker runtime with above details has been successfully registered.<br>
    				</center>
					<hr>
	    			<%	
				
				}catch(Exception e){
				%>
					<center>
    				Cannot register with the following details.<br>
    				<%=e.getMessage()%>
    				</center>
					<hr>
    			<%	
				}
			
			}
			
			

		}
		
		if (userFile.exists()){
			
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
			<br>
			<center><h3> Message Broker Environment</h3></center>
			
			<Table border=1 align=center class="gridtable">
				<tr>
					<th><b>Broker Details</b></th>
					<th><b>Broker EGs</b></th>
					<th><b>Broker EGs Action</b></th>
					<th><b>Broker Logs</b></th>
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
					<a href='MBLogProxy.jsp?brokerName=<%=brkProxy.getName()%>'>	<%=brkProxy.getName()%> Log </a>
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
						%><a href='MBGetEG.jsp?brokerName=<%=brkProxy.getName()%>&egName=<%=egProxy.get(egCtr).getName()%>'>
						<%=egProxy.get(egCtr).getName()%></a> - <%
						if(egProxy.get(egCtr).isRunning()){
							%>
							<font color=green>Running</font><br>
							<%						
						}else{
							 %>
							<font color=red>Stopped</font><br>
							<%
						}
					}
				%>
				</td>
				<td>
				<%if(UserID.equals("admin")){
					egCount = egProxy.size();
					for (egCtr = 0; egCount > egCtr; egCtr++) {
						%>	<%=egProxy.get(egCtr).getName()%> -> 
						<a
						href='../MBAdmin?action=EGstart&egName=<%=egProxy.get(egCtr).getName()%>&brkName=<%=brkProxy.getName()%>' >
							START </a> 
							| 
						<a
						href='../MBAdmin?action=EGstop&egName=<%=egProxy.get(egCtr).getName()%>&brkName=<%=brkProxy.getName()%>' >
							STOP </a>
							| 	 
						<a
						href='../MBAdmin?action=EGdelete&egName=<%=egProxy.get(egCtr).getName()%>&brkName=<%=brkProxy.getName()%>' >
							DELETE </a><br> 

					<%}
				}else if((env.equals("DEV")||env.equals("QA"))&&UserID.indexOf("dev-")>-1){
					egCount = egProxy.size();
					 
					for (egCtr = 0; egCount > egCtr; egCtr++) {
									
					%>	<%=egProxy.get(egCtr).getName()%> -> 
						<a
						href='../MBAdmin?action=EGstart&egName=<%=egProxy.get(egCtr).getName()%>&brkName=<%=brkProxy.getName()%>' >
							START </a> 
							| 
						<a
						href='../MBAdmin?action=EGstop&egName=<%=egProxy.get(egCtr).getName()%>&brkName=<%=brkProxy.getName()%>' >
							STOP </a>
							| 	 
						<a
						href='../MBAdmin?action=EGdelete&egName=<%=egProxy.get(egCtr).getName()%>&brkName=<%=brkProxy.getName()%>' >
							DELETE </a><br> 
					<%}
					
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
		egProxy.clear();
		
		System.gc();
			
		}else{
		
		}
	}
%>


</body>
</html>