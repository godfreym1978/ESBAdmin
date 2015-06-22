package com.ibm.esbadmin;

/* Project: ESBAdmin */
/* Author: Godfrey Peter Menezes */
/* 
Copyright © 2015 Godfrey P Menezes
All rights reserved. This code or any portion thereof
may not be reproduced or used in any manner whatsoever
without the express written permission of Godfrey P Menezes(godfreym@gmail.com).

*/


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.stream.XMLStreamException;


/**
 * Servlet implementation class DownloadMsg
 */
@WebServlet("/DeleteMQObject")
public class DeleteMQObject extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteMQObject() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//String qMgr = request.getParameter("qMgr");
		//String qName = request.getParameter("qName");

		PCFCommons newPCFCommons = new PCFCommons();

		HttpSession httpSession = request.getSession(true);
		
		String UserID = httpSession.getAttribute("UserID").toString();
		String qMgr = request.getParameter("qMgr");
		String qPort = null;
		String qHost = null;
		String qChannel = null;

		MQAdminUtil newMQAdUtil = new MQAdminUtil();
		List<Map<String, String>> MQList = new ArrayList<Map<String, String>>();
		try {
			MQList = newMQAdUtil.getQMEnv(UserID);
		} catch (XMLStreamException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		for (int i=0; i < MQList.size(); i++) {
			if (MQList.get(i).get("QMName").toString().equals(qMgr)) {
				qHost = MQList.get(i).get("QMHost").toString();
				qPort = MQList.get(i).get("QMPort").toString();
				qChannel = MQList.get(i).get("QMChannel").toString();
				break;
			}
		}
		if (httpSession.getAttribute("UserID").toString().indexOf("admin") > -1) {
			try {
				if (!request.getParameter("qName").isEmpty()) {
					newPCFCommons.deleteQueue(qHost, Integer.parseInt(qPort), 
							request.getParameter("qName"), qChannel);
				}
				if (!request.getParameter("qChannel").isEmpty()) {
					newPCFCommons.deleteChannel(qHost, Integer.parseInt(qPort), 
							request.getParameter("qChannel"), qChannel);
				}
				if (!request.getParameter("qListener").isEmpty()) {
					newPCFCommons.deleteListener(qHost, Integer.parseInt(qPort), 
							request.getParameter("qListener"), qChannel);
				}
				if (!request.getParameter("qTopic").isEmpty() || !request.getParameter("qTopicString").isEmpty()) {
					newPCFCommons.deleteTopic(qHost, Integer.parseInt(qPort), 
							request.getParameter("qTopic"), request.getParameter("qTopicString"), qChannel);
				}
				if (!request.getParameter("qSubscription").isEmpty()) {
					newPCFCommons.deleteSub(qHost, Integer.parseInt(qPort), 
							request.getParameter("qSubscription"), qChannel);
				}
			}catch(Exception e) {
				System.out.println("Error in deleting object");
			}
		}
		response.sendRedirect(request.getHeader("referer"));
	}

}
