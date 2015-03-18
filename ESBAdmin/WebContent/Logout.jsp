<!-- 
/********************************************************************************/
/* */
/* Project: ESBAdmin */
/* Author: Godfrey Peter Menezes */
/* 
Copyright © 2010 by 2015 Godfrey P Menezes
All rights reserved. This book or any portion thereof
may not be reproduced or used in any manner whatsoever
without the express written permission of Godfrey P Menezes.
*/
/********************************************************************************/
 -->
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="com.ibm.MQAdmin.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.sql.*"%>
<%@ page
	import="org.apache.commons.fileupload.*,org.apache.commons.io.*,java.io.*"%>
<html>
<head>
<meta http-equiv="Content-Style-Type" content="text/css">
<style type="text/css">
<%@ include file ="Style.css" %>
</style>
<title>Login Page</title>

</head>
<body>
<%
if(session != null)  
{  
         session.invalidate();
         
         response.sendRedirect("Index.html");
} 
%>

</body>
</html>