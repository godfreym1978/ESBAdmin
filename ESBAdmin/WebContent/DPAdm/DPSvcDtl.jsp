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
<%@ page import="com.ibm.MQAdmin.*"%>
<%@ page import="java.io.*"%>
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
		
		String dpHostName = request.getParameter("dpHostName");
		String domainName = request.getParameter("domainName");
		String svcName = request.getParameter("svcName");
	%>
			<center><h3> DataPower Environment</h3></center>

			<Table border=1 align=center class="gridtable">
				<tr>
					<th><b>Sr. No.</b></th>
					<th><b>DP Host Name</b></th>
					<th><b>Domain Name</b></th>
					<th><b>Service Name</b></th>
					
					<th><b>File List</b></th>
					<th><b>Config File List</b></th>
					<th><b>Admin Status</b></th>
				</tr>
		<%	
		String dmnName;
		DPUtil newDPUtil = new DPUtil();
		List<Map> domainSvcsListDtl = newDPUtil.getServices(dpHostName, domainName, svcName);
		List<String> fileListDtl = new ArrayList<String>();
		List<String> configListDtl = new ArrayList<String>();
		int SrNo = 0;
		for (int s=0;s<domainSvcsListDtl.size();s++){
			SrNo++;
			fileListDtl = (List<String>)domainSvcsListDtl.get(s).get("FileList");
			configListDtl = (List<String>)domainSvcsListDtl.get(s).get("configListDtl");
					
		%>			
			<tr>
				<td><%=SrNo%></td>
				<td><%=dpHostName%></td>
				<td><%=domainName%></td>
				<td><%=svcName%></td>
				<td>
				<ol>
				<%for(int i=0;i<fileListDtl.size();i++){%>
				<li><%=fileListDtl.get(i)%></li>
				<%}%>
				</ol>
				</td>
				<td>
				<ol>
				<%for(int i=0;i<configListDtl.size();i++){%>
				<li><%=configListDtl.get(i)%></li>
				<%}%>
				</ol>
				</td>
				<td><%=domainSvcsListDtl.get(s).get("AdminStatus")%></td>
			</tr>
		<%
		}
	}
%>
</table>

</body>
</html>