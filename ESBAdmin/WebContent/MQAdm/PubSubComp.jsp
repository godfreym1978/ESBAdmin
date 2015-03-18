<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="com.ibm.MQAdmin.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page
	import="org.apache.commons.fileupload.*,org.apache.commons.io.*,java.io.*"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<style type="text/css">
<%@ include file="../Style.css" %>
</style>
<title>Get Topic List</title>
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
					//session.setAttribute("UserID", UserID);
					
					String qmDtls = session.getAttribute(request.getParameter("qMgr")).toString();				
					String qMgr = qmDtls.substring(qmDtls.indexOf('|') + 1,qmDtls.indexOf(':'));
					String qPort = qmDtls.substring(qmDtls.indexOf(':') + 1,qmDtls.length());
					String qHost = qmDtls.substring(0, qmDtls.indexOf('|'));
					String UserID = session.getAttribute("UserID").toString();
					Util newUtil = new Util();
					
					File userFile = new File(System.getProperty("catalina.base")
							+ File.separator+"ESBAdmin"+File.separator+request.getParameter("qMgr").toString()+"."
							+ session.getAttribute("UserID").toString());

					PCFCommons pcfCmd = new PCFCommons();
					
					List<Map> ListSubNames = pcfCmd.ListSubNames(qHost, Integer.parseInt(qPort));
					List<Map> ListTopicNames = pcfCmd.ListTopicNames(qHost, Integer.parseInt(qPort));
					List<Map> ListOrpTopicNames = new ArrayList<Map>();
					
					boolean flag = true;
					%>
					<center><b><u>List of Topics in Queue Manager - <%=qMgr %></u></b></center><br>
					<table border=1 align=center class="gridtable">
						<tr>
							<th><b>Topic Name</b></th>
							<th><b>Topic String</b></th>
							<th><b>Topic Description</b></th>
							<th><b>Topic Alter Date</b></th>
							<th><b>Topic Alter Time</b></th>

							<th><b>Sub Name</b></th>
							<th><b>Destination</b></th>
							<th><b>Destination Q Mgr</b></th>
							<th><b>Sub User ID</b></th>
							<th><b>Sub Creation Date</b></th>
							<th><b>Sub Creation Time</b></th>
							<th><b>Sub Alteration Date</b></th>
							<th><b>Sub Alteration Time</b></th>
						</tr>
						<%
					for (int t=0;t<ListTopicNames.size();t++){
						for (int s=0;s<ListSubNames.size();s++){
							if(ListSubNames.get(s).get("MQCA_TOPIC_NAME").toString().equalsIgnoreCase(ListTopicNames.get(t).get("MQCA_TOPIC_NAME").toString())||
									ListSubNames.get(s).get("MQCA_TOPIC_STRING").toString().equalsIgnoreCase(ListTopicNames.get(t).get("MQCA_TOPIC_STRING").toString())){
								//System.out.println(ListTopicNames.get(t).get("MQCA_TOPIC_NAME").toString()+" - "+ListSubNames.get(s).get("MQCACF_SUB_NAME").toString());
								%>
								<tr>
									<td><%=ListTopicNames.get(t).get("MQCA_TOPIC_NAME")%></td>
									<td><%=ListTopicNames.get(t).get("MQCA_TOPIC_STRING")%></td>
									<td><%=ListTopicNames.get(t).get("MQCA_TOPIC_DESC")%></td>
									<td><%=ListTopicNames.get(t).get("MQCA_ALTERATION_DATE")%></td>
									<td><%=ListTopicNames.get(t).get("MQCA_ALTERATION_TIME")%></td>

									<td><%=ListSubNames.get(s).get("MQCACF_SUB_NAME")%></td>
									<td><%=ListSubNames.get(s).get("MQCACF_DESTINATION")%></td>
									<td><%=ListSubNames.get(s).get("MQCACF_DESTINATION_Q_MGR")%></td>
									<td><%=ListSubNames.get(s).get("MQCACF_SUB_USER_ID")%></td>
									<td><%=ListSubNames.get(s).get("MQCA_CREATION_DATE")%></td>
									<td><%=ListSubNames.get(s).get("MQCA_CREATION_TIME")%></td>
									<td><%=ListSubNames.get(s).get("MQCA_ALTERATION_DATE")%></td>
									<td><%=ListSubNames.get(s).get("MQCA_ALTERATION_TIME")%></td>
									<%
									out.flush();
									ListSubNames.remove(s);
									flag = false;
							}
						}
						if(flag){
							ListOrpTopicNames.add(ListTopicNames.get(t));
						}
						flag = true;
					}
				%>
		</table>
		<HR>
		<center><b><u>List of Subscriptions in Queue Manager - <%=qMgr %> with no Topics being Published</u></b></center><br>
					<table border=1 align=center>
						<tr>
							<td><b>Sub Name</b></td>
							<td><b>Topic String</b></td>
							<td><b>Destination</b></td>
							<td><b>Destination Q Mgr</b></td>
							<td><b>Sub User ID</b></td>
							<td><b>Sub Creation Date</b></td>
							<td><b>Sub Creation Time</b></td>
							<td><b>Sub Alteration Date</b></td>
							<td><b>Sub Alteration Time</b></td>
						</tr>
		<%
		for (int s=0;s<ListSubNames.size();s++){
		%>			
			<tr>
				<td><%=ListSubNames.get(s).get("MQCACF_SUB_NAME")%></td>
				<td><%=ListSubNames.get(s).get("MQCA_TOPIC_STRING")%></td>
				<td><%=ListSubNames.get(s).get("MQCACF_DESTINATION")%></td>
				<td><%=ListSubNames.get(s).get("MQCACF_DESTINATION_Q_MGR")%></td>
				<td><%=ListSubNames.get(s).get("MQCACF_SUB_USER_ID")%></td>
				<td><%=ListSubNames.get(s).get("MQCA_CREATION_DATE")%></td>
				<td><%=ListSubNames.get(s).get("MQCA_CREATION_TIME")%></td>
				<td><%=ListSubNames.get(s).get("MQCA_ALTERATION_DATE")%></td>
				<td><%=ListSubNames.get(s).get("MQCA_ALTERATION_TIME")%></td>
			</tr>
		<%
		}
		%>
</table>

<HR>
<center><b><u>List of Topics in Queue Manager - <%=qMgr %> with no Subscriptions </u></b></center><br>

			<table border=1 align=center>
				<tr>
							<td><b>Topic Name</b></td>
							<td><b>Topic String</b></td>
							<td><b>Topic Description</b></td>
							<td><b>Topic Alter Date</b></td>
							<td><b>Topic Alter Time</b></td>
				</tr>
				<%
				for (int t=0;t<ListOrpTopicNames.size();t++){
				%>			
					<tr>
						<td><%=ListOrpTopicNames.get(t).get("MQCA_TOPIC_NAME")%></td>
						<td><%=ListOrpTopicNames.get(t).get("MQCA_TOPIC_STRING")%></td>
						<td><%=ListOrpTopicNames.get(t).get("MQCA_TOPIC_DESC")%></td>
						<td><%=ListOrpTopicNames.get(t).get("MQCA_ALTERATION_DATE")%></td>
						<td><%=ListOrpTopicNames.get(t).get("MQCA_ALTERATION_TIME")%></td>
					</tr>
				<%
				}
				
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
}//end if block
System.gc();
%>
</table>	 

</body>
</html>
