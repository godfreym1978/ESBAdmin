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
<title>Insert title here</title>
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

	Util newUtil = new Util();
	MBCommons newMBCmn = new MBCommons();

	String UserID = session.getAttribute("UserID").toString();
	List<Map> MBList = newMBCmn.getMBEnv(UserID);

	String hostName = new String();
	int portNum=0;
	String brokerName = request.getParameter("brokerName").toString();
	String egName = request.getParameter("egName").toString();

	BrokerProxy brkProxy  = null;

	for (int i=0; i<MBList.size(); i++) {
		if(MBList.get(i).get("MBName").toString().equals(brokerName)){
			hostName = MBList.get(i).get("MBHost").toString();
			portNum = Integer.parseInt(MBList.get(i).get("MBPort").toString());
			brkProxy = newMBCmn.getBrokerProxy(hostName, portNum);
			break;
		}
	}

	ExecutionGroupProxy egProxy	= brkProxy.getExecutionGroupByName(egName);
    Properties rms = new Properties();
    rms.setProperty(AttributeConstants.ACTIVITYLOG_SUPPORTED_PROPERTY, AttributeConstants.TRUE);

    Enumeration <ResourceManagerProxy> enumResManProxy = egProxy.getResourceManagers(rms);
    while (enumResManProxy.hasMoreElements()) {
    	ResourceManagerProxy rmProxy = enumResManProxy.nextElement();
    	String name = rmProxy.getName();
%>
		<a href="javascript:unhide('<%=name%>');"> 
		<b>Resource Manager Name - <%=name%>
</b><br>
</a>
						<div id="col2">
							<div id="<%=name%>" class="hidden">
<%
        ActivityLogProxy actLogProxy = egProxy.getResourceManagerByName(name).getActivityLog();

        if (actLogProxy != null) {
        	%>
        	<table border=1 align=center class="gridtable">
	        	<tr>
		        	<th><b>Message Number</b></th>
		        	<th><b>Message</b></th>
		        	<th><b>Detail</b></th>
		        	<th><b>Source</b></th>
		        	<th><b>Timestamp</b></th>
		        	<th><b>Thread Identifier</b></th>
		        	<th><b>Inserts</b></th>
	        	</tr>
	        	<%
	        	List<String> list = null;
	        	for (int i = actLogProxy.getSize(); i > 0 ; i--) {	
	                ActivityLogEntry actLogEntProxy = actLogProxy.getLogEntry(i);
	                list = Collections.list(actLogEntProxy.getTagNames());
	        	%>
	            <tr>
		            <td><%=actLogEntProxy.getMessageNumber()%></td>
		            <td><%=actLogEntProxy.getMessage()%></td>
		            <td><%=actLogEntProxy.getDetail()%></td>
		            <td><%=actLogEntProxy.getSource()%></td>
		            <td><%=actLogEntProxy.getTimestamp()%></td>
		            <td><%=actLogEntProxy.getThreadIdentifier()%></td>
					<td>
					<%
					for(int j=0;j<list.size();j++){
		            %>
		            	<%=list.get(j)+"  -  "+actLogEntProxy.getTagValue(list.get(j))%><br>
		            	<%
		            }
					%>
					</td>
				</tr>
			<%
			
			out.flush();
	          }
    	}
        %>
        </table>
        </div>
	</div>
<HR>
        <%
    }
    
	brkProxy.disconnect();
    System.gc();
}
 %>

</body>
</html>