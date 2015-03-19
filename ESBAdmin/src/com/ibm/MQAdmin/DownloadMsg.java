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

package com.ibm.MQAdmin;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;

import sun.misc.BASE64Decoder;

/**
 * Servlet implementation class DownloadMsg
 */
@WebServlet("/DownloadMsg")
public class DownloadMsg extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DownloadMsg() {
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
		String wmbKey = request.getParameter("WMB_MSGKEY");
		String mbName = request.getParameter("brkName");

		response.setContentType("text/plain");
		response.setHeader("Content-Disposition", "attachment;filename="
				+ mbName + "-" + wmbKey);
		// ServletContext ctx = getServletContext();

		BASE64Decoder decoder = new BASE64Decoder();

		File userFile = new File(System.getProperty("catalina.base")
				+ File.separator + "ESBAdmin" + File.separator + "MBAud.txt");

		String userName = new String();
		String password = new String();
		String url = new String();
		String schema = new String();
		String query = new String();

		for (String line : FileUtils.readLines(userFile)) {
			if (line.substring(0, line.indexOf("|")).equals(mbName)) {
				userName = line.substring(line.indexOf("|") + 1,
						line.indexOf(";"));
				password = line.substring(line.indexOf(";") + 1,
						line.indexOf(":"));
				url = line.substring(line.indexOf(":") + 1, line.indexOf("/"));
				schema = line.substring(line.indexOf("/") + 1, line.length());
				query = "select /*+ FIRST_ROWS */Data from " + schema
						+ ".wmb_binary_data where WMB_MSGKEY ='" + wmbKey + "'";
				break;
			}
		}

		// String query =
		// "select /*+ FIRST_ROWS */Data from wmb_binary_data where WMB_MSGKEY ='"+wmbKey+"'";
		Connection conn = null;
		ResultSet rs = null;
		Statement st = null;
		String data = new String();

		try {
			System.out.println("In DownloadMsg Servlet - in try");
			Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
			conn = DriverManager.getConnection(url, userName, password);
			st = conn.createStatement();
			rs = st.executeQuery(query);
			// STEP 5: Extract data from result set
			while (rs.next()) {
				data = rs.getString("DATA");
				// Retrieve by column name
			}

		} catch (Exception e) {
			System.err.println("Cannot connect to database server");
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					rs.close();
					conn.close();
					System.out.println("Database connection terminated");
				} catch (Exception e) { /* ignore close errors */
				}
			}
		}

		byte[] decodedBytes = decoder.decodeBuffer(data);

		OutputStream os = response.getOutputStream();
		os.write(decodedBytes);
		os.flush();
		os.close();
		System.gc();
	}

}
