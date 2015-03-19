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
<%@ page import="com.ibm.MQAdmin.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page import="com.ibm.mq.constants.MQConstants" %>

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
	
try{ 

	String qmDtls = session.getAttribute(request.getParameter("qMgr")).toString();				
	String qMgr = qmDtls.substring(qmDtls.indexOf('|') + 1,qmDtls.indexOf(':'));
	String qPort = qmDtls.substring(qmDtls.indexOf(':') + 1,qmDtls.length());
	String qHost = qmDtls.substring(0, qmDtls.indexOf('|'));
	String UserID = session.getAttribute("UserID").toString();
	String chlName = request.getParameter("chlName").toString();
	Util newUtil = new Util();

	PCFCommons newPFCCM = new PCFCommons();

 	List<Map> chanelStat  = newPFCCM.channelStatus(qHost, Integer.parseInt(qPort),chlName );
 %>

				<table border=1 align=center class="gridtable">
				
					<tr>
						<th><b>Channel Name</b></th>
	    				<th><b>Channel Type</b></th>
	    				
						<th><b>Channel Status</b></th>
						<th><b>Alteration Date</b></th>
						<th><b>Alteration TIme</b></th>
						<th><b>Batch Size</b></th>
						<th><b>Channel Description</b></th>
						<th><b>Heartbeat Interval</b></th>
						<th><b>Keap Alive Interval</b></th>
						<th><b>Max Message Length</b></th>
						<th><b>MCA User ID</b></th>
						<th><b>Monitoring Channel</b></th>
						<th><b>Message Retry Count</b></th>
						<th><b>Message Retry Interval</b></th>
						<th><b>NPM Speed</b></th>
						<th><b>Put Athority</b></th>
						<th><b>Channel Reset Requested?</b></th>
						<th><b>Sequence Number Wrap</b></th>
						<th><b>SSL Client Auth</b></th>
						<th><b>Channel Statistics</b></th>
						<th><b>Transmit Protocol Type</b></th>
						<th><b>Channel Heartbeat Interval</b></th>
						<th><b>Indoubt Status</b></th>
						<th><b>Keep Alive Interval</b></th>
						<th><b>Last LUWID</b></th>
						<th><b>Last Message Date</b></th>
						<th><b>Last Message Time</b></th>
						<th><b>Last Sequence</b></th>
						<th><b>Long Retries Left</b></th>
						<th><b>Max Msg Length</b></th>
						<th><b>Max Sharing Conv</b></th>
						<th><b>MCA Job name</b></th>
						<th><b>MCA Status</b></th>
						<th><b>MCA User ID</b></th>
						<th><b>Message Compression</b></th>
						<th><b>Messages</b></th>
						<th><b>Transmit Msgs Available</b></th>
						<th><b>N/w Time Indicator</b></th>
						<th><b>NPM Speed</b></th>
						<th><b>Remote Appln Tag</b></th>
						<th><b>Remote Q Mgr Name</b></th>
						<th><b>Short Retries Left</b></th>
						<th><b>SSL Cert User ID</b></th>
						<th><b>SSL Key Reset Date</b></th>
						<th><b>SSL Key Resets</b></th>
						<th><b>SSL Key Reset Time</b></th>
						<th><b>SSL Short Peer Name</b></th>
						<th><b>Stop Requested</b></th>
						<th><b>Channel SubState</b></th>
						<th><b>Transmit Queue Name</b></th>
						<th><b>Transmit Queue Indicator</b></th>
	    				
	    				
					</tr>        

			<%
					for (int index=0;index < chanelStat.size();index++){
						%>
						<tr>
						
						<td><%=chanelStat.get(index).get("MQIACH_BATCH_SIZE_INDICATOR")%></td>
						<td><%=chanelStat.get(index).get("MQIACH_BUFFERS_RCVD")%></td>
						<td><%=chanelStat.get(index).get("MQIACH_BUFFERS_SENT")%></td>
						<td><%=chanelStat.get(index).get("MQIACH_BYTES_RCVD")%></td>
						<td><%=chanelStat.get(index).get("MQIACH_BYTES_SENT")%></td>
						<td><%=chanelStat.get(index).get("MQIACH_CHANNEL_DISP")%></td>
						<td><%=chanelStat.get(index).get("MQIACH_CHANNEL_INSTANCE_TYPE")%></td>
						<td><%=chanelStat.get(index).get("MQIA_MONITORING_CHANNEL")%></td>
						<td><%=chanelStat.get(index).get("MQCACH_CHANNEL_START_DATE")%></td>
						<td><%=chanelStat.get(index).get("MQCACH_CHANNEL_START_TIME")%></td>
						<td><%=chanelStat.get(index).get("MQIACH_CHANNEL_STATUS")%></td>
						<td><%=chanelStat.get(index).get("MQIACH_CHANNEL_TYPE")%></td>
						<td><%=chanelStat.get(index).get("MQIACH_COMPRESSION_RATE")%></td>
						<td><%=chanelStat.get(index).get("MQIACH_COMPRESSION_TIME")%></td>
						<td><%=chanelStat.get(index).get("MQCACH_CONNECTION_NAME")%></td>
						<td><%=chanelStat.get(index).get("MQCACH_CURRENT_LUWID")%></td>
						<td><%=chanelStat.get(index).get("MQIACH_CURRENT_MSGS")%></td>
						<td><%=chanelStat.get(index).get("MQIACH_CURRENT_SEQ_NUMBER")%></td>
						<td><%=chanelStat.get(index).get("MQIACH_CURRENT_SHARING_CONVS")%></td>
						<td><%=chanelStat.get(index).get("MQIACH_EXIT_TIME_INDICATOR")%></td>
						<td><%=chanelStat.get(index).get("MQIACH_HDR_COMPRESSION")%></td>
						<td><%=chanelStat.get(index).get("MQIACH_HB_INTERVAL")%></td>
						<td><%=chanelStat.get(index).get("MQIACH_INDOUBT_STATUS")%></td>
						<td><%=chanelStat.get(index).get("MQIACH_KEEP_ALIVE_INTERVAL")%></td>
						<td><%=chanelStat.get(index).get("MQCACH_LAST_LUWID")%></td>
						<td><%=chanelStat.get(index).get("MQCACH_LAST_MSG_DATE")%></td>
						<td><%=chanelStat.get(index).get("MQCACH_LAST_MSG_TIME")%></td>
						<td><%=chanelStat.get(index).get("MQIACH_LAST_SEQ_NUMBER")%></td>
						<td><%=chanelStat.get(index).get("MQIACH_LONG_RETRIES_LEFT")%></td>
						<td><%=chanelStat.get(index).get("MQIACH_MAX_MSG_LENGTH")%></td>
						<td><%=chanelStat.get(index).get("MQIACH_MAX_SHARING_CONVS")%></td>
						<td><%=chanelStat.get(index).get("MQCACH_MCA_JOB_NAME")%></td>
						<td><%=chanelStat.get(index).get("MQIACH_MCA_STATUS")%></td>
						<td><%=chanelStat.get(index).get("MQCACH_MCA_USER_ID")%></td>
						<td><%=chanelStat.get(index).get("MQIACH_MSG_COMPRESSION")%></td>
						<td><%=chanelStat.get(index).get("MQIACH_MSGS")%></td>
						<td><%=chanelStat.get(index).get("MQIACH_XMITQ_MSGS_AVAILABLE")%></td>
						<td><%=chanelStat.get(index).get("MQIACH_NETWORK_TIME_INDICATOR")%></td>
						<td><%=chanelStat.get(index).get("MQIACH_NPM_SPEED")%></td>
						<td><%=chanelStat.get(index).get("MQCACH_REMOTE_APPL_TAG")%></td>
						<td><%=chanelStat.get(index).get("MQCA_REMOTE_Q_MGR_NAME")%></td>
						<td><%=chanelStat.get(index).get("MQIACH_SHORT_RETRIES_LEFT")%></td>
						<td><%=chanelStat.get(index).get("MQCACH_SSL_CERT_USER_ID")%></td>
						<td><%=chanelStat.get(index).get("MQCACH_SSL_KEY_RESET_DATE")%></td>
						<td><%=chanelStat.get(index).get("MQIACH_SSL_KEY_RESETS")%></td>
						<td><%=chanelStat.get(index).get("MQCACH_SSL_KEY_RESET_TIME")%></td>
						<td><%=chanelStat.get(index).get("MQCACH_SSL_SHORT_PEER_NAME")%></td>
						<td><%=chanelStat.get(index).get("MQIACH_STOP_REQUESTED")%></td>
						<td><%=chanelStat.get(index).get("MQIACH_CHANNEL_SUBSTATE")%></td>
						<td><%=chanelStat.get(index).get("MQCACH_XMIT_Q_NAME")%></td>
						<td><%=chanelStat.get(index).get("MQIACH_XMITQ_TIME_INDICATOR")%></td>
					</tr>
				
						


						<%
					}
			%>
				</table>

		<%
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

System.gc();
	 %>
</body>
</html>