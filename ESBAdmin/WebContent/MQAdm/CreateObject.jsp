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
<%@ page import="com.ibm.ESBAdmin.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
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
<title>Create MQ Objects</title>

</head>
<body>	 
<%
String UserID = session.getAttribute("UserID").toString();
if(UserID==null){%>
	<center>
		Looks like you are not logged in.<br> Please login with a valid
		user id <a href='../Index.html'><b>Here</b> </a>
	</center>
	<%	
}else{
	if(UserID.equals("admin")){
		try{ 
			String qMgr = request.getParameter("qMgr");
			String qPort = null;
			String qHost = null;
			String qChannel = null;

			MQAdminUtil newMQAdUtil = new MQAdminUtil();
			List<Map> MQList = newMQAdUtil.getQMEnv(UserID);

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
		
		 %>

						<a href="javascript:unhide('CreateQueue');"> 
													<b>Create Queue in <%=qMgr%> on <%=qHost%>   
						</b><br>
						</a>
						<div id="col2">
							<div id="CreateQueue" class="hidden">
								<form action='CreateObjectRep.jsp?qMgr=<%=qMgr%>' method="post">
								
								<table border=1 align=center class="gridtable">
										<tr><td>Queue Name</td><td><input type=text name=qName></td></tr>
										<tr><td>Backout Queue Name</td><td><input type=text name=backoutQName></td></tr>
										<tr><td>Queue Type</td>
											<td>
												<select name="qType">
													<option value="LOCAL">LOCAL</option>
													<option value="ALIAS">ALIAS</option>
													<option value="MODEL">MODEL</option>
													<option value="CLUSTER">CLUSTER</option>
													<option value="REMOTE">REMOTE</option>
												</select>
											</td></tr>
										<tr><td>Transmit Queue</td>
											<td>
												<select name="xmitType">
													<option value="true">YES</option>
													<option value="false">NO</option>
												</select>
											</td></tr>
										</tr>
										<tr><td colspan=2>
											<center>
												<input type=submit value=Submit>
											</center>
										</td></tr>
								</table>
								</form>
							</div>
						</div>
						<HR>

						<a href="javascript:unhide('CreateChannel');"> 
													<b>Create Channel in <%=qMgr%> on <%=qHost%>   
						</b><br>
						</a>
						<div id="col2">
							<div id="CreateChannel" class="hidden">
								<form action='CreateObjectRep.jsp?qMgr=<%=qMgr%>' method="post">
								
								<table border=1 align=center class="gridtable">
										<tr><td>Channel Name</td><td><input type=text name=chanName></td></tr>
										<tr><td>Channel Type</td>
											<td>
												<select name="chanType">
													<option value="SENDER">SENDER</option>
													<option value="RECEIVER">RECEIVER</option>
													<option value="REQUESTER">REQUESTER</option>
													<option value="CLUSSDR">CLUSSDR</option>
													<option value="CLUSREC">CLUSREC</option>
													<option value="SERVERCONN">SERVERCONN</option>
													<option value="SERVER">SERVER</option>
												</select>
											</td></tr>
										<tr><td>Connecting Qmgr IP(Required SENDER, REQUESTER, SERVER)</td><td><input type=text name=targetQmgrIP></td></tr>
										<tr><td>Connecting Qmgr Port(Required SENDER, REQUESTER, SERVER)</td><td><input type=text name=targetQmgrPort></td></tr>
										<tr><td>Transmit Queue(Required SENDER,SERVER)</td><td><input type=text name=xmitQueue></td></tr>
										<tr><td colspan=2>
											<center>
												<input type=submit value=Submit>
											</center>
										</td></tr>
								</table>
								</form>

							</div>
						</div>
						<HR>

						<a href="javascript:unhide('CreateListener');"> 
													<b>Create Listener in <%=qMgr%> on <%=qHost%>   
						</b><br>
						</a>
						<div id="col2">
							<div id="CreateListener" class="hidden">
								<form action='CreateObjectRep.jsp?qMgr=<%=qMgr%>' method="post">
								<table border=1 align=center class="gridtable">
										<tr><td>Listener Name</td><td><input type=text name=listName></td></tr>
										<tr><td>Listener Type</td>
											<td>
												<select name="listType">
													<option value="TCP">TCP</option>
													<option value="LU6.2">LU6.2</option>
													<option value="SPX">SPX</option>
													<option value="NetBIOS">NetBIOS</option>
												</select>
											</td></tr>
										<tr><td>TCP Port</td><td><input type=text name=portNum></td></tr>
										<tr><td colspan=2>
											<center>
												<input type=submit value=Submit>
											</center>
										</td></tr>
								</table>
								</form>

							</div>
						</div>
						<HR>
						
						<a href="javascript:unhide('CreateTopic');"> 
													<b>Create Topic in <%=qMgr%> on <%=qHost%>
						</b><br>
						</a>
						<div id="col2">
							<div id="CreateTopic" class="hidden">
								<form action='CreateObjectRep.jsp?qMgr=<%=qMgr%>' method="post">
								<table border=1 align=center class="gridtable">
										<tr><td>Topic Name</td><td><input type=text name=topicName></td></tr>
										<tr><td>Topic String</td><td><input type=text name=topicString></td></tr>
										<tr><td>Topic Description</td><td><input type=text name=topicDesc></td></tr>
										<tr><td colspan=2>
											<center>
												<input type=submit value=Submit>
											</center>
										</td></tr>
								</table>
								</form>

							</div>
						</div>
						<HR>
						
						<a href="javascript:unhide('CreateSubscription');"> 
													<b>Create Subscription in <%=qMgr%> on <%=qHost%>
						</b><br>
						</a>
						<div id="col2">
							<div id="CreateSubscription" class="hidden">
								<form action='CreateObjectRep.jsp?qMgr=<%=qMgr%>' method="post">
								
								<table border=1 align=center class="gridtable">
										<tr><td>Subscription Name</td><td><input type=text name=subName></td></tr>
										<tr><td>Topic String</td><td><input type=text name=topicString></td></tr>
										<tr><td>Topic Name</td><td><input type=text name=subTopicName></td></tr>
										<tr><td>Subscription Destination</td><td><input type=text name=subDest></td></tr>
										<tr><td>Subscription Destination Queue Manager</td><td><input type=text name=subDestQM></td></tr>
										<tr><td>Subscription User ID</td><td><input type=text name=subUsrID></td></tr>
										
										<tr><td colspan=2>
											<center>
												<input type=submit value=Submit>
											</center>
										</td></tr>
								</table>
								</form>

							</div>
						</div>
						<HR>


		<%
		}catch(Exception e){
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