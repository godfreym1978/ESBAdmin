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

package com.ibm.esbadmin;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class DownloadMsg
 */
@WebServlet("/DownloadQMgr")
public class DownloadQMgr extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DownloadQMgr() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession httpSession = request.getSession(true);
		if (httpSession.getAttribute("UserID")!=null){
			// TODO Auto-generated method stub
			response.setContentType("text/plain");
			response.setHeader("Content-Disposition", "attachment;filename="
					+ request.getParameter("qMgr") + ".mqsc");

			OutputStream outStream = response.getOutputStream();
			byte[] qMgrBytes;

			String UserID = httpSession.getAttribute("UserID").toString();
			String qMgr = request.getParameter("qMgr");
			String qPort = null;
			String qHost = null;
			String qChannel = null;

			MQAdminUtil newMQAdUtil = new MQAdminUtil();
			List<Map> MQList = new ArrayList();

			try {
				MQList = newMQAdUtil.getQMEnv(UserID);

				for (int i=0; i<MQList.size(); i++) {
					if(MQList.get(i).get("QMName").toString().equals(qMgr)){
						qHost = MQList.get(i).get("QMHost").toString();
						qPort = MQList.get(i).get("QMPort").toString();
						qChannel = MQList.get(i).get("QMChannel").toString();
						break;
					}
				}
				PCFCommons pcfCM = new PCFCommons();

				List<Map> ListQueueNames = pcfCM.ListQueueNamesDtl(qHost,
						Integer.parseInt(qPort), qChannel);
				for (int i = 0; i < ListQueueNames.size(); i++) {
					qMgrBytes = String.valueOf(
							pcfCM.createQScript(qHost, Integer.parseInt(qPort),
									ListQueueNames.get(i).get("MQCA_Q_NAME")
											.toString().trim(), qChannel))
							.getBytes();
					outStream.write(qMgrBytes);
				}

				List<Map> listChannels = pcfCM.channelDetails(qHost,
						Integer.parseInt(qPort), qChannel);

				for (int i = 0; i < listChannels.size(); i++) {
					qMgrBytes = String.valueOf(
							pcfCM.createChlScript(qHost, Integer.parseInt(qPort),
									listChannels.get(i).get("MQCACH_CHANNEL_NAME")
											.toString().trim(), qChannel))
							.getBytes();
					outStream.write(qMgrBytes);
				}

				List<Map> listListener = pcfCM.listenerDetails(qHost,
						Integer.parseInt(qPort), qChannel);

				for (int i = 0; i < listListener.size(); i++) {
					qMgrBytes = String.valueOf(
							pcfCM.createListScript(qHost, Integer.parseInt(qPort),
									listListener.get(i).get("MQCACH_LISTENER_NAME")
											.toString().trim(), qChannel))
							.getBytes();
					outStream.write(qMgrBytes);
				}
				List<Map> listTopic = pcfCM.ListTopicNames(qHost,
						Integer.parseInt(qPort), qChannel);

				for (int i = 0; i < listTopic.size(); i++) {
					qMgrBytes = String.valueOf(
							pcfCM.createTopicScript(qHost, Integer.parseInt(qPort),
									listTopic.get(i).get("MQCA_TOPIC_NAME")
											.toString().trim(), qChannel))
							.getBytes();
					outStream.write(qMgrBytes);
				}
				List<Map> listSubs = pcfCM.ListSubNames(qHost,
						Integer.parseInt(qPort), qChannel);

				for (int i = 0; i < listSubs.size(); i++) {
					qMgrBytes = String.valueOf(
							pcfCM.createSubScript(qHost, Integer.parseInt(qPort),
									listSubs.get(i).get("MQCACF_SUB_NAME")
											.toString().trim(), qChannel))
							.getBytes();
					outStream.write(qMgrBytes);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			outStream.flush();
			outStream.close();

		}else{
			System.out.println("not logged in");
		}
		
		System.gc();
	}

}
