package com.ibm.MQAdmin;




import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
		String qmDtls = httpSession.getAttribute(request.getParameter("qMgr"))
				.toString();
		int qmgrPort = Integer.parseInt(qmDtls.substring(qmDtls.indexOf(':') + 1,
				qmDtls.length()));
		String qmgrHost = qmDtls.substring(0, qmDtls.indexOf('|'));

		if (httpSession.getAttribute("UserID").toString().indexOf("admin") > -1){
			try{
				if(!request.getParameter("qName").isEmpty())
					newPCFCommons.deleteQueue(qmgrHost, qmgrPort, request.getParameter("qName"));
				if(!request.getParameter("qChannel").isEmpty())
					newPCFCommons.deleteChannel(qmgrHost, qmgrPort, request.getParameter("qChannel"));
				if(!request.getParameter("qListener").isEmpty())
					newPCFCommons.deleteListener(qmgrHost, qmgrPort, request.getParameter("qListener"));
				if(!request.getParameter("qTopic").isEmpty()||!request.getParameter("qTopicString").isEmpty())
					newPCFCommons.deleteTopic(qmgrHost, qmgrPort, request.getParameter("qTopic"),request.getParameter("qTopicString"));
				if(!request.getParameter("qSubscription").isEmpty())
					newPCFCommons.deleteSub(qmgrHost, qmgrPort, request.getParameter("qSubscription"));
				
			}catch(Exception e){
				System.out.println("Error in deleting object");
			}
		}


		System.gc();
		response.sendRedirect(request.getHeader("referer"));
	}

}
