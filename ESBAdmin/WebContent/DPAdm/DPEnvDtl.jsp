
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
	%>
			<center><h3> DataPower Environment</h3></center>

			<Table border=1 align=center class="gridtable">
				<tr>
					<th><b>Domain Name</b></th>
					<th><b>Object Name</b></th>
					<th><b>Object Type</b></th>
					<th><b>Object Class</b></th>
					
					<th><b>User Comments</b></th>
					<th><b>Domain Status</b></th>
					<th><b>Quiesce Status</b></th>
					<th><b>File List</b></th>
					<th><b>Config List Name</b></th>
				</tr>
			

	<%	
		DPUtil newDPUtil = new DPUtil();
		List<Map> deviceListDtl = newDPUtil.getServices();
	    List<String> fileListDtl ;
		List<String> configListDtl ;

		int i = 0;
		int fileCtr = 0;
		int configCtr = 0;
		String strDomain = new String();

		while(i < deviceListDtl.size()){
			strDomain = deviceListDtl.get(i).get("Domain").toString();
		%>
		<tr>
			<td><%=strDomain.substring(0, strDomain.indexOf(" in")) %></td>
	        <td><%=deviceListDtl.get(i).get("DomainName") %></td>
	        <td><%=deviceListDtl.get(i).get("ClassDisplayName") %></td>
	        <td><%=deviceListDtl.get(i).get("ClassName") %></td>
	        
	        <td><%=deviceListDtl.get(i).get("DomainUserComments") %></td>
	        <td><%=deviceListDtl.get(i).get("DomainStatus") %></td>
	        <td><%=deviceListDtl.get(i).get("QuiesceStatus") %></td>

	     <% 
	     	  
	        fileListDtl = (List)(deviceListDtl.get(i).get("FileList"));

	        %>
	        <td>
	        <%
	        try{
		        while(fileCtr <fileListDtl.size() ){
		        	%>
		        	<%=fileListDtl.get(fileCtr)%><br>
		        	<%
		        	fileCtr++;
		        }
	        }catch(Exception e){
	        %>
	        No Reference files.
	        <%

	        }
	        %>
	        </td>
	        <td>
	        <%
	        try{
				configListDtl = (List)(deviceListDtl.get(i).get("configListDtl"));
		        while(configCtr <configListDtl.size() ){
		        	%>
		        	<%=configListDtl.get(configCtr)%><br>
		        	<%
	
		        	configCtr++;
		        }
	        }catch(Exception e){
	        %>
	        No Config files.
	        <%
	        }
%>			</td>

	     </tr>   
<%			
			i++;
		}
		

	}
%>
</table>

</body>
</html>