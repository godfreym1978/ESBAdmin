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
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.io.*"%>
<%@ page import="com.ibm.ESBAdmin.*"%>
<%@ page import="com.ibm.broker.config.proxy.*"%>
<%@ page import="java.util.*"%>
<%@ page import="org.apache.commons.csv.*"%>

<%@ page
	import="org.apache.commons.fileupload.*,org.apache.commons.io.*,java.io.*"%>


<html>
<head>
<meta http-equiv="Content-Style-Type" content="text/css">
<style type="text/css">
<%@ include file="../Style.css" %>
</style>
<title>Get Execution Group Details</title>

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
		String UserID = session.getAttribute("UserID").toString();
		Util newUtil = new Util();
		MBCommons newMBCmn = new MBCommons();

		File userFile = new File(System.getProperty("catalina.base")+File.separator+"ESBAdmin"+File.separator+session.getAttribute("UserID").toString()+File.separator+"MBEnv.txt");
		String hostName = new String();
		String env = null;
		String egName = request.getParameter("egName");
		String brokerName = request.getParameter("brokerName");
		int portNum=0;
		BrokerProxy brkProxy  = null;
		String qMgr = null;
		for (String line : FileUtils.readLines(userFile)) {
			CSVParser parser = CSVParser.parse(line, CSVFormat.RFC4180);
			for (CSVRecord csvRecord : parser) {
				if(csvRecord.get(1).equals(brokerName)){
					env = csvRecord.get(0);
					hostName = csvRecord.get(2);
					portNum = Integer.parseInt(csvRecord.get(3));
				}
				BrokerConnectionParameters bcp = new MQBrokerConnectionParameters(
						hostName, portNum, "");
				brkProxy = BrokerProxy.getInstance(bcp);
				qMgr = brkProxy.getQueueManagerName(); 
			}							
		}
		ArrayList<ApplicationProxy> appProxy = newMBCmn.getExecutionGroupDetails(brkProxy, egName);
		ArrayList<MessageFlowProxy> egMFProxy = newMBCmn.getMFDetails(brkProxy,egName);
		ExecutionGroupProxy egProxy =
				brkProxy.getExecutionGroupByName(egName);
		ArrayList<LibraryProxy> egLibraries = Collections.list(egProxy.getLibraries(null));
				
		int egLibCtr;
		int egLibCount = egLibraries.size();
		int egMFCount = egMFProxy.size();
		int egMFCtr;
		int appCount = appProxy.size();
		ArrayList<MessageFlowProxy> appMFNames = null;
		int mfCount;
		int mfCtr;
		ArrayList<LibraryProxy> libNames = null;
		int libCount;
		int libCtr;
		String[] queueNames = null;
		int qCtr;
		int qCount;
		 
	%>
	
	<center><h3>Broker Name - <%=brkProxy.getName()%>
 / Execution Group - <a href="EGAuditRep.jsp?brokerName=<%=brokerName%>&egName=<%=egName%>"><%=egName%></a></h3></center>
 
 <%if((UserID.indexOf("dev-") ==0 && !env.equals("STG")&& !env.equals("PROD"))||UserID.equals("admin") ){%>
		<Table border=1 align=center class="gridtable">
			<tr>
				<td><h3>Deploy BAR file to EG <%=egName%> </h3></td>
					<td>
					<form action='MBEGDeploy.jsp?brokerName=<%=brokerName%>&egName=<%=egName%>'
						enctype="multipart/form-data" method="post">
						<input type="file" name="message"> <input type="submit"
							value="Submit" />
					</form>
					
					</td>
					</tr>
					</Table>
					<hr>
 <%}%>
		<Table border=1 align=center class="gridtable">
			<tr>
				<th><b>Artifact Name</b></th>
				<th><b>Application Flows</b></th>
				<th><b>Queues Used in Flows</b></th>
				<th><b>Application Libraries</b></th>
				<th><b>Is It Running?</b></th>
				<th><b>Stop, Start or Delete?</b></th>
				<th><b>Deployment Time</b></th>
				<th><b>Bar File Name</b></th>
			</tr>
			<%
				for (int appCtr = 0; appCount > appCtr; appCtr++) {
					appMFNames = Collections.list(appProxy.get(appCtr).getMessageFlows(null));
					libNames = Collections.list(appProxy.get(appCtr).getLibraries(null));
			%>
			<tr>
				<td><%=appProxy.get(appCtr).getName()%></td>
				<td colspan=2>
					<table width="100%" border=1 align=center>
						<col width="50%">
						<%
							mfCount = appMFNames.size();
								for (mfCtr = 0; mfCount > mfCtr; mfCtr++) {
									%><tr>
									<td> <a href="MFAuditRep.jsp?brokerName=<%=brokerName%>&mfName='<%=appMFNames.get(mfCtr).getName()%>'">
									<%=appMFNames.get(mfCtr).getName()%></a></td>
									<td> 
									<%
									queueNames = appMFNames.get(mfCtr).getQueues();
									qCount = queueNames.length;
									for (qCtr=0;qCount>qCtr;qCtr++){
										%>
										<a href="../MQAdm/QueueDtl.jsp?qMgr=<%=qMgr%>&qName=<%=queueNames[qCtr]%>"><%=queueNames[qCtr]%><br>
										<%
									}
									%>
							</td>
							<%}%>
						</tr>						
					</table>
				</td>
				<td>
					<%	libCount = libNames.size();
						for (libCtr = 0; libCount > libCtr; libCtr++) {
							%> <%=libNames.get(libCtr).getName()%><br> 
						<%}
						
						%>
				</td>
				<td>
				<%
				if(appProxy.get(appCtr).isRunning()){
					%>
					<font color=green>Running</font><br>
					<%						
					}else{
					%>
					<font color=red>Stopped</font><br>
				<%}%>
				</td>

			<td>
				<%if(UserID.equals("admin")){%>
					<a
					href='../MBAdmin?action=APPLstart&egName=<%=egName%>&brkName=<%=brkProxy.getName()%>&applName=<%=appProxy.get(appCtr).getName()%>' >
						START</a>
						| 	 
					<a
					href='../MBAdmin?action=APPLstop&egName=<%=egName%>&brkName=<%=brkProxy.getName()%>&applName=<%=appProxy.get(appCtr).getName()%>' >
						STOP</a>
						| 	 
					<a
					href='../MBAdmin?action=APPLdelete&egName=<%=egName%>&brkName=<%=brkProxy.getName()%>&applName=<%=appProxy.get(appCtr).getName()%>' >
						DELETE</a><br> 
					
				<%}else if((env.equals("DEV")||env.equals("QA"))&&session.getAttribute("UserID").toString().indexOf("dev-")>-1){%>
					<a
					href='../MBAdmin?action=APPLstart&egName=<%=egName%>&brkName=<%=brkProxy.getName()%>&applName=<%=appProxy.get(appCtr).getName()%>' >
						START</a>
						| 	 
					<a
					href='../MBAdmin?action=APPLstop&egName=<%=egName%>&brkName=<%=brkProxy.getName()%>&applName=<%=appProxy.get(appCtr).getName()%>' >
						STOP</a>
						| 	 
					<a
					href='../MBAdmin?action=APPLdelete&egName=<%=egName%>&brkName=<%=brkProxy.getName()%>&applName=<%=appProxy.get(appCtr).getName()%>' >
						DELETE</a><br> 
					
				<%}%>

			</td>

				
				<td><%=appProxy.get(appCtr).getDeployTime()%></td>
				<td><%=appProxy.get(appCtr).getBARFileName()%></td>
			</tr>
			<%}%>
			<%
				for (egMFCtr = 0; egMFCount > egMFCtr; egMFCtr++) {
			%>
			<tr>
				<td></td>
				<td>
				<a href="MFAuditRep.jsp?brokerName=<%=brokerName%>&mfName='<%=egMFProxy.get(egMFCtr).getName()%>'">
				<%=egMFProxy.get(egMFCtr).getName()%></a></td>
				<td><%
							queueNames = egMFProxy.get(egMFCtr).getQueues();
							qCount = queueNames.length;
							for (qCtr=0;qCount>qCtr;qCtr++){
								%>
								<a href="../MQAdm/QueueDtl.jsp?qMgr=<%=qMgr%>&qName=<%=queueNames[qCtr]%>"><%=queueNames[qCtr]%><br>
								<%
							}
				
							 %>
				</td>
				<td></td>
				<td>
				<%
				if(egMFProxy.get(egMFCtr).isRunning()){
					%>
					<font color=green>Running</font><br>
					<%						
					}else{
					%>
					<font color=red>Stopped</font><br>
				<%}%>
				</td>
				
				<td>
					<%if(UserID.equals("admin")){
						%>
						<a
						href='../MBAdmin?action=MFstart&egName=<%=egName%>&brkName=<%=brkProxy.getName()%>&mfName=<%=egMFProxy.get(egMFCtr).getName()%>' >
							START </a> 
						<a
						href='../MBAdmin?action=MFstop&egName=<%=egName%>&brkName=<%=brkProxy.getName()%>&mfName=<%=egMFProxy.get(egMFCtr).getName()%>' >
							| STOP </a> 
						<a
						href='../MBAdmin?action=MFdelete&egName=<%=egName%>&brkName=<%=brkProxy.getName()%>&mfName=<%=egMFProxy.get(egMFCtr).getName()%>' >
							| DELETE </a><br>
						<%
					}else if((env.equals("DEV")||env.equals("QA"))&&UserID.indexOf("dev-")>-1){
						%>
						<a
						href='../MBAdmin?action=MFstart&egName=<%=egName%>&brkName=<%=brkProxy.getName()%>&mfName=<%=egMFProxy.get(egMFCtr).getName()%>' >
							START </a> 
						<a
						href='../MBAdmin?action=MFstop&egName=<%=egName%>&brkName=<%=brkProxy.getName()%>&mfName=<%=egMFProxy.get(egMFCtr).getName()%>' >
							| STOP </a> 
						<a
						href='../MBAdmin?action=MFdelete&egName=<%=egName%>&brkName=<%=brkProxy.getName()%>&mfName=<%=egMFProxy.get(egMFCtr).getName()%>' >
							| DELETE </a><br>
						<%
					}%> 

				</td>
				
				<td><%=egMFProxy.get(egMFCtr).getDeployTime()%></td>
				<td><%=egMFProxy.get(egMFCtr).getBARFileName()%></td>
			</tr>
			
			<%}
			%>

			<%
				
				for (egLibCtr = 0; egLibCount > egLibCtr; egLibCtr++) {
					LibraryProxy libProxyDtl = egProxy.getLibraryByName(egLibraries.get(egLibCtr).getName());
					ArrayList<DeployedObject> libDetails =  Collections.list(libProxyDtl.getDeployedObjects());
			%>
			<tr>

				<td><%=egLibraries.get(egLibCtr).getFullName()%></td>
				<td><%
				
					for (int libDtlCtr = 0; libDetails.size()>libDtlCtr;libDtlCtr++){
						if(libDetails.get(libDtlCtr).getFullName().indexOf("subflow") >0){
						%>
						<%=libDetails.get(libDtlCtr).getFullName() %><br>
						<%	
						}
						System.out.println();
					}
					%></td>
				<td></td>
				<td><%
				
					for (int libDtlCtr = 0; libDetails.size()>libDtlCtr;libDtlCtr++){
						if(libDetails.get(libDtlCtr).getFullName().indexOf("xsd") >0){
						%>
						<%=libDetails.get(libDtlCtr).getFullName() %> - <%=libDetails.get(libDtlCtr).getModifyTime()%><br>
						<%	
						}
						System.out.println();
					}
					%></td>

				<td></td>
				<td>
					<a
						href='../MBAdmin?action=LIBdelete&egName=<%=egName%>&brkName=<%=brkProxy.getName()%>&libName=<%=egLibraries.get(egLibCtr).getName()%>' >
							DELETE </a><br>
				</td>
				<td><%=egLibraries.get(egLibCtr).getDeployTime()%></td>
				<td><%=egLibraries.get(egLibCtr).getBARFileName()%></td>
			</tr>
				
			<%
				}
			%>
			
			
			<%
			brkProxy.disconnect();
			System.gc();
			
}

%>		
		</Table>

</body>
</html>