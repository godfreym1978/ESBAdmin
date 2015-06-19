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

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.csv.*;
import org.apache.commons.io.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Servlet implementation class DownloadMsg
 */
@WebServlet("/DownloadQObject")
public class DownloadQObject extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DownloadQObject() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException  {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession(true);

		if(session.getAttribute("UserID") != null){
			try {

				String UserID = session.getAttribute("UserID").toString();
				response.setContentType("text/plain");
				response.setHeader("Content-Disposition", "attachment;filename="
						+ request.getParameter("objName").toString() + ".mqsc");
	
				OutputStream outStream = response.getOutputStream();
				byte[] qMgrBytes;
				String qMgr = request.getParameter("qMgr");
				int qPort = 0;
				String qHost = null;
				String qChannel = null;
				MQAdminUtil newMQAdUtil = new MQAdminUtil();
	
				List<Map<String, Object>> MQList = newMQAdUtil.getQMEnv(UserID);

				for (int i=0; i< MQList.size(); i++) {
					if(MQList.get(i).get("QMName").toString().equals(qMgr)){
						qHost = MQList.get(i).get("QMHost").toString();
						qPort = 
								Integer.parseInt(MQList.get(i).get("QMPort").toString());
						qChannel = MQList.get(i).get("QMChannel").toString();
						break;
					}
				}
				String objType = request.getParameter("objType").toString();
				String objName = request.getParameter("objName").toString();

				PCFCommons pcfCM = new PCFCommons();

				if (objType.equals("QUEUE")) {
					qMgrBytes = String.valueOf(
							pcfCM.createQScript(qHost, qPort,
									objName, qChannel)).getBytes();
					outStream.write(qMgrBytes);
				}

				if (objType.equals("CHANNEL")) {
					qMgrBytes = String.valueOf(
							pcfCM.createChlScript(qHost, qPort,
									objName, qChannel)).getBytes();
					outStream.write(qMgrBytes);
				}

				if (objType.equals("LISTENER")) {
					qMgrBytes = String.valueOf(
							pcfCM.createListScript(qHost, qPort,
									objName, qChannel)).getBytes();
					outStream.write(qMgrBytes);
				}

				if (objType.equals("TOPIC")) {
					qMgrBytes = String.valueOf(
							pcfCM.createTopicScript(qHost, qPort,
									objName, qChannel)).getBytes();
					outStream.write(qMgrBytes);
				}

				if (objType.equals("SUB")) {
					qMgrBytes = String.valueOf(
							pcfCM.createSubScript(qHost, qPort, 
									objName, qChannel)).getBytes();
					outStream.write(qMgrBytes);
				}
				
				outStream.flush();
				outStream.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Not logged in");
		}
		System.gc();
	}
}
