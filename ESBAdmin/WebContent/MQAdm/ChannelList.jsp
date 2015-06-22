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
<%@ page import="com.ibm.esbadmin.*"%>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page import="com.ibm.mq.constants.MQConstants" %>
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
<title>Get Topic List</title>
</head>
<body>
<title>Channel List</title>

</head>
<body>	 
<%
if(session.getAttribute("UserID")==null){%>
<center>
	Looks like you are not logged in.<br> Please login with a valid
	user id <a href='../Index.html'><b>Here</b> </a>
</center>
<%	
}else{
	String UserID = session.getAttribute("UserID").toString();
	try{
		String qMgr = request.getParameter("qMgr");
		String qPort = null;
		String qHost = null;
		String qChannel = null;

		MQAdminUtil newMQAdUtil = new MQAdminUtil();
		List<Map<String, String>> MQList = newMQAdUtil.getQMEnv(UserID);

		for (int i=0; i<MQList.size(); i++) {
			if(MQList.get(i).get("QMName").toString().equals(qMgr)){
				qHost = MQList.get(i).get("QMHost").toString();
				qPort = MQList.get(i).get("QMPort").toString();
				qChannel = MQList.get(i).get("QMChannel").toString();
				break;
			}
		}
			
		Util newUtil = new Util();
		PCFCommons newPFCCM = new PCFCommons();

		List<Map<String, Object>> chanelDtl  = newPFCCM.channelDetails(qHost, Integer.parseInt(qPort), qChannel);
 %>

			<%
					for (int index=0;index < chanelDtl.size();index++){
						%>
						<a href="javascript:unhide('<%=chanelDtl.get(index).get("MQCACH_CHANNEL_NAME")%>');"> 
													<b>Channel Name - <%=chanelDtl.get(index).get("MQCACH_CHANNEL_NAME")%>
						</b><br>
						</a>

						<div id="col2">
							<div id="<%=chanelDtl.get(index).get("MQCACH_CHANNEL_NAME")%>" class="hidden">
			 	<center>
			 	<a
					href='../DownloadQObject?qMgr=<%=qMgr%>&objType=CHANNEL&objName=<%=chanelDtl.get(index).get("MQCACH_CHANNEL_NAME").toString()%>'
					> Download MQSC Script For This Channel</a>
				<center><br>
			 	<a
					href='ChannelStatus.jsp?qMgr=<%=qMgr%>&chlName=<%=chanelDtl.get(index).get("MQCACH_CHANNEL_NAME").toString()%>'
					> Channel Status</a><br>
				
				<table border=1 align=center class="gridtable">
				
					<tr>
						<th><b>Property</b></th>
	    				<th><b>Property Value</b></th>
					</tr>        
				
						<tr><td>Channel Name</td><td><%=chanelDtl.get(index).get("MQCACH_CHANNEL_NAME")%></td></tr>
						<tr><td>Channel Type</td><td><%=chanelDtl.get(index).get("MQIACH_CHANNEL_TYPE")%></td></tr>
						
						<tr><td>Channel Status</td><td><%=chanelDtl.get(index).get("MQIACH_CHANNEL_STATUS")%></td></tr>
						<tr><td>Alteration Date</td><td><%=chanelDtl.get(index).get("MQCA_ALTERATION_DATE")%></td></tr>
						<tr><td>Alteration TIme</td><td><%=chanelDtl.get(index).get("MQCA_ALTERATION_TIME")%></td></tr>
						<tr><td>Batch Size</td><td><%=chanelDtl.get(index).get("MQIACH_BATCH_SIZE")%></td></tr>
						<tr><td>Channel Description</td><td><%=chanelDtl.get(index).get("MQCACH_DESC")%></td></tr>
						<tr><td>Heartbeat Interval</td><td><%=chanelDtl.get(index).get("MQIACH_HB_INTERVAL")%></td></tr>
						<tr><td>Keap Alive Interval</td><td><%=chanelDtl.get(index).get("MQIACH_KEEP_ALIVE_INTERVAL")%></td></tr>
						<tr><td>Max Message Length</td><td><%=chanelDtl.get(index).get("MQIACH_MAX_MSG_LENGTH")%></td></tr>
						<tr><td>MCA User ID</td><td><%=chanelDtl.get(index).get("MQCACH_MCA_USER_ID")%></td></tr>
						<tr><td>Monitoring Channel</td><td><%=chanelDtl.get(index).get("MQIA_MONITORING_CHANNEL")%></td></tr>
						<tr><td>Message Retry Count</td><td><%=chanelDtl.get(index).get("MQIACH_MR_COUNT")%></td></tr>
						<tr><td>Message Retry Interval</td><td><%=chanelDtl.get(index).get("MQIACH_MR_INTERVAL")%></td></tr>
						<tr><td>NPM Speed</td><td><%=chanelDtl.get(index).get("MQIACH_NPM_SPEED")%></td></tr>
						<tr><td>Put Athority</td><td><%=chanelDtl.get(index).get("MQIACH_PUT_AUTHORITY")%></td></tr>
						<tr><td>Channel Reset Requested?</td><td><%=chanelDtl.get(index).get("MQIACH_RESET_REQUESTED")%></td></tr>
						<tr><td>Sequence Number Wrap</td><td><%=chanelDtl.get(index).get("MQIACH_SEQUENCE_NUMBER_WRAP")%></td></tr>
						<tr><td>SSL Client Auth</td><td><%=chanelDtl.get(index).get("MQIACH_SSL_CLIENT_AUTH")%></td></tr>
						<tr><td>Channel Statistics</td><td><%=chanelDtl.get(index).get("MQIA_STATISTICS_CHANNEL")%></td></tr>
						<tr><td>Transmit Protocol Type</td><td><%=chanelDtl.get(index).get("MQIACH_XMIT_PROTOCOL_TYPE")%></td></tr>
						<tr><td>Use Dead Letter Queue</td><td><%=chanelDtl.get(index).get("MQIA_USE_DEAD_LETTER_Q")%></td></tr>

						
				</table>
							</div>
						</div>
									<HR>


						<%
					}
			%>
		<%
		}catch(Exception e){
			%>
			<center>
			We have encountered the following error<br>
			
			<font color=red><b><%=e%></b></font> 
			</center>
			<%
		}
}

System.gc();
	 %>
</body>
</html>