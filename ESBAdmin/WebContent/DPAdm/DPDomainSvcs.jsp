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
<%@ page import="com.ibm.esbadmin.*"%>
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
	%>
			<center><h3> DataPower Environment</h3></center>

			<Table border=1 align=center class="gridtable">
				<tr>
					<th><b>Sr. No.</b></th>
					<th><b>Domain Name</b></th>
					<th><b>Class Display Name</b></th>
					<th><b>Service Name</b></th>
					<th><b>Domain User Comments</b></th>
					<th><b>Operational Status</b></th>
					<th><b>Quiesce Status</b></th>
					<th><b>Admin Status</b></th>
				</tr>
	<%	
		String dmnName;
		DPUtil newDPUtil = new DPUtil();
		List<Map<String, Object>> domainSvcsListDtl = newDPUtil.getDomainServices(dpHostName, domainName);
		int SrNo = 0;
		for (int s=0;s<domainSvcsListDtl.size();s++){
			SrNo++;
			dmnName = domainSvcsListDtl.get(s).get("Domain").toString();
			
		%>			
			<tr>
				<td><%=SrNo%></td>
				<td><%=dmnName.substring(0, dmnName.indexOf("in Managed Set:"))%></td>
				<td><%=domainSvcsListDtl.get(s).get("ClassDisplayName")%></td>
				<td><a
						href="DPSvcDtl.jsp?dpHostName=<%=dpHostName%>
									&domainName=<%=domainName%>
									&svcName=<%=domainSvcsListDtl.get(s).get("DomainName")%>">
						<%=domainSvcsListDtl.get(s).get("DomainName")%></a></td>
				<td><%=domainSvcsListDtl.get(s).get("DomainUserComments")%></td>
				<td><%=domainSvcsListDtl.get(s).get("OpStatus")%></td>
				<td><%=domainSvcsListDtl.get(s).get("QuiesceStatus")%></td>
				<td><%=domainSvcsListDtl.get(s).get("AdminStatus")%></td>
			</tr>
		<%
		}
	}
%>
</table>

</body>
</html>