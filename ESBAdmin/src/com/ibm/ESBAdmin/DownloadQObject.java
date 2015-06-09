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

package com.ibm.ESBAdmin;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.csv.*;
import org.apache.commons.io.*;
import java.io.*;

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
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/plain");
		response.setHeader("Content-Disposition", "attachment;filename="
				+ request.getParameter("objName").toString() + ".mqsc");

		OutputStream outStream = response.getOutputStream();
		byte[] qMgrBytes;
		HttpSession session = request.getSession(true);

		String UserID = session.getAttribute("UserID").toString();
		File userQMFile = new File(System.getProperty("catalina.base")
				+ File.separator + "ESBAdmin" + File.separator + UserID
				+ File.separator + "QMEnv.txt");
		String qMgr = request.getParameter("qMgr");
		String qPort = null;
		String qHost = null;
		String qChannel = null;

		for (String line : FileUtils.readLines(userQMFile)) {
			if (line.indexOf(qMgr) > 0) {
				CSVParser parser = CSVParser.parse(line, CSVFormat.RFC4180);

				for (CSVRecord csvRecord : parser) {
					qHost = csvRecord.get(0);
					qPort = csvRecord.get(2);
					qChannel = csvRecord.get(3);
				}
			}
		}

		String objType = request.getParameter("objType").toString();
		String objName = request.getParameter("objName").toString();

		try {
			PCFCommons pcfCM = new PCFCommons();

			if (objType.equals("QUEUE")) {
				qMgrBytes = String.valueOf(
						pcfCM.createQScript(qHost, Integer.parseInt(qPort),
								objName, qChannel)).getBytes();
				outStream.write(qMgrBytes);
			}

			if (objType.equals("CHANNEL")) {
				qMgrBytes = String.valueOf(
						pcfCM.createChlScript(qHost, Integer.parseInt(qPort),
								objName, qChannel)).getBytes();
				outStream.write(qMgrBytes);
			}

			if (objType.equals("LISTENER")) {
				qMgrBytes = String.valueOf(
						pcfCM.createListScript(qHost, Integer.parseInt(qPort),
								objName, qChannel)).getBytes();
				outStream.write(qMgrBytes);
			}

			if (objType.equals("TOPIC")) {
				qMgrBytes = String.valueOf(
						pcfCM.createTopicScript(qHost, Integer.parseInt(qPort),
								objName, qChannel)).getBytes();
				outStream.write(qMgrBytes);
			}

			if (objType.equals("SUB")) {
				qMgrBytes = String.valueOf(
						pcfCM.createSubScript(qHost, Integer.parseInt(qPort),
								objName, qChannel)).getBytes();
				outStream.write(qMgrBytes);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		outStream.flush();
		outStream.close();
		System.gc();
	}

}
