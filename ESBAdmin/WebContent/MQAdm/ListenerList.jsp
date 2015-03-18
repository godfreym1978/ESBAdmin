<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="com.ibm.MQAdmin.*" %>
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

		String UserID = session.getAttribute("UserID").toString();
		File userQMFile = new File(
							System.getProperty("catalina.base")
									+ File.separator+"ESBAdmin"+File.separator+UserID+File.separator+"QMEnv.txt");
		String qMgr = request.getParameter("qMgr");
		String qPort = null;
		String qHost = null;
		String qChannel = null;

		for (String line : FileUtils.readLines(userQMFile)) {
			if (line.indexOf(qMgr)>0){
				CSVParser parser = CSVParser.parse(line, CSVFormat.RFC4180);
				
				for (CSVRecord csvRecord : parser) {
					qHost = csvRecord.get(0);
					qPort = csvRecord.get(2);
					qChannel = csvRecord.get(3);
				}							
			}
		}

		Util newUtil = new Util();
	
		PCFCommons newPFCCM = new PCFCommons();
	
	 	List<Map> listenerDtl  = newPFCCM.listenerDetails(qHost, Integer.parseInt(qPort),qChannel);
 %>

			<%
					for (int index=0;index < listenerDtl.size();index++){
						%>
						<a href="javascript:unhide('<%=listenerDtl.get(index).get("MQCACH_LISTENER_NAME")%>');"> 
													<b>ListenerDtl Name - <%=listenerDtl.get(index).get("MQCACH_LISTENER_NAME")%>
						</b><br>
						</a>

						<div id="col2">
							<div id="<%=listenerDtl.get(index).get("MQCACH_LISTENER_NAME")%>" class="hidden">
					 	<center>
			 	<a
					href='../DownloadQObject?qMgr=<%=qMgr%>&objType=LISTENER&objName=<%=listenerDtl.get(index).get("MQCACH_LISTENER_NAME").toString()%>'
					> Download MQSC Script For This Listener</a>
				</center><br>
				

				<table border=1 align=center class="gridtable">
					<tr>
						<th><b>Property</b></th>
	    				<th><b>Property Value</b></th>
					</tr>        
				
						<tr><td>Listener Name</td><td><%=listenerDtl.get(index).get("MQCACH_LISTENER_NAME")%></td></tr>
						<tr><td>Listener Control</td><td><%=listenerDtl.get(index).get("MQIACH_LISTENER_CONTROL")%></td></tr>
						<tr><td>Transmit Protocol Type</td><td><%=listenerDtl.get(index).get("MQIACH_XMIT_PROTOCOL_TYPE")%></td></tr>
 						<tr><td>Listener Description</td><td><%=listenerDtl.get(index).get("MQCACH_LISTENER_DESC")%></td></tr> 
 						<tr><td>TP Name</td><td><%=listenerDtl.get(index).get("MQCACH_TP_NAME")%></td></tr>
						<tr><td>Alteration Date</td><td><%=listenerDtl.get(index).get("MQCA_ALTERATION_DATE")%></td></tr>
						<tr><td>Alteration TIme</td><td><%=listenerDtl.get(index).get("MQCA_ALTERATION_TIME")%></td></tr>

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