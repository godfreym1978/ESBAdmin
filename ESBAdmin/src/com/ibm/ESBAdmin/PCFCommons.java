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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.headers.MQDataException;
import com.ibm.mq.headers.pcf.PCFException;
import com.ibm.mq.headers.pcf.PCFMessage;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.headers.pcf.PCFMessageAgent;

public class PCFCommons {

	/**
	 * Object containing the instance of the PCFMessageAgent.
	 */
	public PCFMessageAgent agent = null;

	/**
	 * Create an agent. This method is used by all PCF samples to create the
	 * PCFAgent object using the queue manager name (local server bindings) or
	 * the host and port (client bindings). Note, when connecting using client
	 * bindings, the queue manager name is not used (it assumes the queue
	 * manager required will be the default).
	 * 
	 * @param numOfArgs
	 *            Used to determine if the agent should be bound to a local
	 *            server or to a client.
	 * @throws MQDataException
	 */
	public void CreateAgent(String qmgrHost, int qmgrPort, String qChannel) {
		try {
			// Connect to the client and define the queue manager host, port and
			// channel.
			// Notice that the method does not take a queue manager name. It is
			// assuming that the
			// default QM will be used.
			agent = new PCFMessageAgent(qmgrHost, qmgrPort,
					qChannel);
			// System.out.print("Agent Created");
		} catch (MQDataException mqde) {
			if (mqde.reasonCode == CMQC.MQRC_Q_MGR_NAME_ERROR) {
				System.out.print("Either could not find the ");
				System.out.print("default queue manager at \"" + qmgrHost
						+ "\", port \"" + qmgrPort + "\"");
			}

		}
		return;
	}

	/*
	 * /** Destroy the agent. This method is used by all PCF samples to destroy
	 * the PCFAgent.
	 * 
	 * @throws MQDataException
	 * 
	 * @throws MQException
	 */
	/*
	 * public void DestroyAgent() throws MQDataException{ // Disconnect the
	 * agent. agent.disconnect(); //System.out.print("Agent Destroyed"); return;
	 * }
	 */

	/**
	 * ListQueueNames uses the PCF command 'MQCMD_INQUIRE_Q_NAMES' to gather
	 * information about all Queues (using the '*' wildcard to denote 'all
	 * queues') contained within the given Queue Manager. The information is
	 * displayed in a tabular form on the console.<br>
	 * For more information on the Inquire Queue Names command, please read the
	 * "Programmable Command Formats and Administration Interface" section
	 * within the Websphere MQ documentation.
	 * 
	 * @param pcfCM
	 *            Object used to hold common objects used by the PCF samples.
	 * @return
	 * @throws PCFException
	 * @throws IOException
	 * @throws MQDataException
	 */

	public ArrayList<String> ListQueueNames(String qmgrHost, int qmgrPort, String qChannel)
			throws PCFException, MQDataException, IOException {

		PCFMessageAgent agent = new PCFMessageAgent(qmgrHost, qmgrPort,
				qChannel);

		// Create the PCF message type for the inquire.
		PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_INQUIRE_Q_NAMES);

		// Add the inquire rules.
		// Queue name = wildcard.
		pcfCmd.addParameter(MQConstants.MQCA_Q_NAME, "*");

		// Queue type = ALL.
		pcfCmd.addParameter(MQConstants.MQIA_Q_TYPE, MQConstants.MQQT_ALL);

		// Execute the command. The returned object is an array of PCF messages.
		PCFMessage[] pcfResponse = agent.send(pcfCmd);

		// For each returned message, extract the message from the array and
		// display the
		// required information.
		ArrayList<String> queueList = new ArrayList<String>();

		String[] names = (String[]) pcfResponse[0]
				.getParameterValue(MQConstants.MQCACF_Q_NAMES);

		for (int index = 0; index < names.length; index++) {
			if (!names[index].startsWith("AMQ")
					&& !names[index].startsWith("SYSTEM")) {
				queueList.add(names[index]);
			}

		}

		agent.disconnect();

		return queueList;
	}

	public int purgeQueue(String qmgrHost, int qmgrPort, String qName, String qChannel)
			throws MQDataException, IOException {

		PCFMessageAgent agent = new PCFMessageAgent(qmgrHost, qmgrPort,
				qChannel);

		PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_CLEAR_Q);
		pcfCmd.addParameter(MQConstants.MQCA_Q_NAME, qName);
		PCFMessage[] pcfResponse = agent.send(pcfCmd);

		return pcfResponse[0].getCompCode();

	}

	public List<Map> ListQueueNamesDtl(String qmgrHost, int qmgrPort, String qChannel)
			throws PCFException, MQDataException, IOException {

		List<Map> queueListDtl = new ArrayList<Map>();
		Map iMap = new HashMap();

		PCFMessageAgent agent = new PCFMessageAgent(qmgrHost, qmgrPort,
				qChannel);

		// Create the PCF message type for the inquire.
		PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_INQUIRE_Q);

		// Add the inquire rules.
		// Queue name = wildcard.
		pcfCmd.addParameter(MQConstants.MQCA_Q_NAME, "*");

		// Queue type = ALL.
		pcfCmd.addParameter(MQConstants.MQIA_Q_TYPE, MQConstants.MQQT_ALL);
		// Execute the command. The returned object is an array of PCF
		// messages.
		PCFMessage[] pcfResponse = agent.send(pcfCmd);

		for (int index = 0; index < pcfResponse.length; index++) {
			if (!pcfResponse[index].getParameterValue(MQConstants.MQCA_Q_NAME)
					.toString().startsWith("AMQ")
					&& !pcfResponse[index]
							.getParameterValue(MQConstants.MQCA_Q_NAME)
							.toString().startsWith("SYSTEM")) {
				iMap = new HashMap();
				iMap.put("MQCA_Q_NAME", pcfResponse[index]
						.getParameterValue(MQConstants.MQCA_Q_NAME));
				iMap.put("MQIA_Q_TYPE", pcfResponse[index]
						.getParameterValue(MQConstants.MQIA_Q_TYPE));

				queueListDtl.add(iMap);
			}
		}

		agent.disconnect();

		return queueListDtl;
	}

	public List<Map> ListTopicNames(String qmgrHost, int qmgrPort, String qChannel)
			throws PCFException, MQDataException, IOException {

		List<Map> topicListDtl = new ArrayList<Map>();
		Map iMap = new HashMap();

		PCFMessageAgent agent = new PCFMessageAgent(qmgrHost, qmgrPort,
				qChannel);

		// Create the PCF message type for the inquire.
		PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_INQUIRE_TOPIC);

		// Add the inquire rules.
		// Topic name = wildcard.
		pcfCmd.addParameter(MQConstants.MQCA_TOPIC_NAME, "*");
		// Execute the command. The returned object is an array of PCF messages.
		PCFMessage[] pcfResponse = agent.send(pcfCmd);

		for (int index = 0; index < pcfResponse.length; index++) {
			if (!pcfResponse[index].toString().contains("SYSTEM")) {
				iMap = new HashMap<>();
				iMap.put("MQCA_TOPIC_NAME", pcfResponse[index]
						.getParameterValue(MQConstants.MQCA_TOPIC_NAME));
				iMap.put("MQCA_TOPIC_STRING", pcfResponse[index]
						.getParameterValue(MQConstants.MQCA_TOPIC_STRING));
				iMap.put("MQCA_TOPIC_DESC", pcfResponse[index]
						.getParameterValue(MQConstants.MQCA_TOPIC_DESC));
				iMap.put("MQCA_ALTERATION_DATE", pcfResponse[index]
						.getParameterValue(MQConstants.MQCA_ALTERATION_DATE));
				iMap.put("MQCA_ALTERATION_TIME", pcfResponse[index]
						.getParameterValue(MQConstants.MQCA_ALTERATION_TIME));

				topicListDtl.add(iMap);
			}
		}

		agent.disconnect();

		return topicListDtl;
	}

	public List<Map> ListTopicStatus(String qmgrHost, int qmgrPort,
			String topicString, String qChannel) throws PCFException, MQDataException, IOException {

		List<Map> topicListDtl = new ArrayList<Map>();
		Map iMap = new HashMap();

		PCFMessageAgent agent = new PCFMessageAgent(qmgrHost, qmgrPort,
				qChannel);

		// Create the PCF message type for the inquire.
		PCFMessage pcfCmd = new PCFMessage(
				MQConstants.MQCMD_INQUIRE_TOPIC_STATUS);
		
		// Add the inquire rules.
		// Topic name = wildcard.
		pcfCmd.addParameter(MQConstants.MQCA_TOPIC_STRING, "TEST/TOPIC");
		// Execute the command. The returned object is an array of PCF messages.
		PCFMessage[] pcfResponse = agent.send(pcfCmd);
		String strValue = new String();
		for (int index = 0; index < pcfResponse.length; index++) {
				iMap = new HashMap<>();
				iMap.put("MQCA_CLUSTER_NAME", pcfResponse[index]
						.getParameterValue(MQConstants.MQCA_CLUSTER_NAME));
				
				switch (Integer.parseInt(pcfResponse[index].
						getParameterValue(MQConstants.MQIA_TOPIC_DEF_PERSISTENCE).toString())) {
				case MQConstants.MQPER_PERSISTENT:
					iMap.put("MQIA_TOPIC_DEF_PERSISTENCE", "PERSISTENT");
					break;
				case MQConstants.MQPER_NOT_PERSISTENT:
					iMap.put("MQIA_TOPIC_DEF_PERSISTENCE", "NOT_PERSISTENT");
					break;
				default:
					iMap.put("MQIA_TOPIC_DEF_PERSISTENCE", "NULL");
					break;
				}

				switch (Integer.parseInt(pcfResponse[index].
						getParameterValue(MQConstants.MQIA_DEF_PUT_RESPONSE_TYPE).toString())) {
				case MQConstants.MQPRT_SYNC_RESPONSE:
					iMap.put("MQIA_DEF_PUT_RESPONSE_TYPE", "SYNC RESPONSE");
					break;
				case MQConstants.MQPRT_ASYNC_RESPONSE:
					iMap.put("MQIA_DEF_PUT_RESPONSE_TYPE", "ASYNC RESPONSE");
					break;
				default:
					iMap.put("MQIA_DEF_PUT_RESPONSE_TYPE", "NULL");
					break;
				}

				iMap.put("MQIA_DEF_PRIORITY", pcfResponse[index]
						.getParameterValue(MQConstants.MQIA_DEF_PRIORITY));

				switch (Integer.parseInt(pcfResponse[index].
						getParameterValue(MQConstants.MQIA_DURABLE_SUB).toString())) {
				case MQConstants.MQSUB_DURABLE_ALLOWED:
					iMap.put("MQIA_DURABLE_SUB", "DURABLE ALLOWED");
					break;
				case MQConstants.MQSUB_DURABLE_INHIBITED:
					iMap.put("MQIA_DURABLE_SUB", "DURABLE INHIBITED");
					break;
				default:
					iMap.put("MQIA_DURABLE_SUB", "NULL");
					break;
				}

				switch (Integer.parseInt(pcfResponse[index].
						getParameterValue(MQConstants.MQIA_INHIBIT_PUB).toString())) {
				case MQConstants.MQTA_PUB_ALLOWED:
					iMap.put("MQIA_INHIBIT_PUB", "PUB ALLOWED");
					break;
				case MQConstants.MQTA_PUB_INHIBITED:
					iMap.put("MQIA_INHIBIT_PUB", "PUB INHIBITED");
					break;
				default:
					iMap.put("MQIA_INHIBIT_PUB", "NULL");
					break;
				}

				switch (Integer.parseInt(pcfResponse[index].
						getParameterValue(MQConstants.MQIA_INHIBIT_SUB).toString())) {
				case MQConstants.MQTA_SUB_ALLOWED:
					iMap.put("MQIA_INHIBIT_SUB", "SUB ALLOWED");
					break;
				case MQConstants.MQTA_SUB_INHIBITED:
					iMap.put("MQIA_INHIBIT_SUB", "SUB INHIBITED");
					break;
				default:
					iMap.put("MQIA_INHIBIT_SUB", "NULL");
					break;
				}

				iMap.put("MQCA_ADMIN_TOPIC_NAME", pcfResponse[index]
						.getParameterValue(MQConstants.MQCA_ADMIN_TOPIC_NAME));
				iMap.put("MQCA_MODEL_DURABLE_Q", pcfResponse[index]
						.getParameterValue(MQConstants.MQCA_MODEL_DURABLE_Q));
				iMap.put(
						"MQCA_MODEL_NON_DURABLE_Q",
						pcfResponse[index]
								.getParameterValue(MQConstants.MQCA_MODEL_NON_DURABLE_Q));
				
				switch (Integer.parseInt(pcfResponse[index].
						getParameterValue(MQConstants.MQIA_PM_DELIVERY).toString())) {
				case MQConstants.MQDLV_ALL:
					iMap.put("MQIA_PM_DELIVERY", "ALL");
					break;
				case MQConstants.MQDLV_ALL_DUR:
					iMap.put("MQIA_PM_DELIVERY", "ALL DURABLE");
					break;
				case MQConstants.MQDLV_ALL_AVAIL:
					iMap.put("MQIA_PM_DELIVERY", "ALL AVAILABLE");
					break;
				default:
					iMap.put("MQIA_PM_DELIVERY", "NULL");
					break;
				}

				switch (Integer.parseInt(pcfResponse[index].
						getParameterValue(MQConstants.MQIA_NPM_DELIVERY).toString())) {
				case MQConstants.MQDLV_ALL:
					iMap.put("MQIA_NPM_DELIVERY", "ALL");
					break;
				case MQConstants.MQDLV_ALL_DUR:
					iMap.put("MQIA_NPM_DELIVERY", "ALL DURABLE");
					break;
				case MQConstants.MQDLV_ALL_AVAIL:
					iMap.put("MQIA_NPM_DELIVERY", "ALL AVAILABLE");
					break;
				default:
					iMap.put("MQIA_NPM_DELIVERY", "NULL");
					break;
				}
				
				switch (Integer.parseInt(pcfResponse[index].
						getParameterValue(MQConstants.MQIACF_RETAINED_PUBLICATION).toString())) {
				case MQConstants.MQQSO_YES:
					iMap.put("MQIACF_RETAINED_PUBLICATION", "RETAINED PUBLICATION");
					break;
				case MQConstants.MQQSO_NO:
					iMap.put("MQIACF_RETAINED_PUBLICATION", "NOT RETAINED PUBLICATION");
					break;
				default:
					iMap.put("MQIACF_RETAINED_PUBLICATION", "NULL");
					break;
				}
				
				iMap.put("MQIA_PUB_COUNT", pcfResponse[index]
						.getParameterValue(MQConstants.MQIA_PUB_COUNT));
				iMap.put("MQIA_SUB_COUNT", pcfResponse[index]
						.getParameterValue(MQConstants.MQIA_SUB_COUNT));
				
				switch (Integer.parseInt(pcfResponse[index].
						getParameterValue(MQConstants.MQIA_SUB_SCOPE).toString())) {
				case MQConstants.MQSCOPE_QMGR:
					iMap.put("MQIA_SUB_SCOPE", "SUB SCOPE ONLY FOR THIS QUEUE MANAGER");
					break;
				case MQConstants.MQSCOPE_ALL:
					iMap.put("MQIA_SUB_SCOPE", "SUB SCOPE FOR ALL QUEUE MANAGERS");
					break;
				default:
					iMap.put("MQIA_SUB_SCOPE", "NULL");
					break;
				}
				
				switch (Integer.parseInt(pcfResponse[index].
						getParameterValue(MQConstants.MQIA_PUB_SCOPE).toString())) {
				case MQConstants.MQSCOPE_QMGR:
					iMap.put("MQIA_PUB_SCOPE", "PUB SCOPE ONLY FOR THIS QUEUE MANAGER");
					break;
				case MQConstants.MQSCOPE_ALL:
					iMap.put("MQIA_PUB_SCOPE", "PUB SCOPE FOR ALL QUEUE MANAGERS");
					break;
				default:
					iMap.put("MQIA_PUB_SCOPE", "NULL");
					break;
				}
				
				switch (Integer.parseInt(pcfResponse[index].
						getParameterValue(MQConstants.MQIA_USE_DEAD_LETTER_Q).toString())) {
				case MQConstants.MQUSEDLQ_NO:
					iMap.put("MQIA_USE_DEAD_LETTER_Q", "NO");
					break;
				case MQConstants.MQUSEDLQ_YES:
					iMap.put("MQIA_USE_DEAD_LETTER_Q", "YES");
					break;
				default:
					iMap.put("MQIA_USE_DEAD_LETTER_Q", "NULL");
					break;
				}
				
				
				iMap.put("MQBACF_SUB_ID", pcfResponse[index]
						.getParameterValue(MQConstants.MQBACF_SUB_ID));
				iMap.put("MQCACF_SUB_USER_ID", pcfResponse[index]
						.getParameterValue(MQConstants.MQCACF_SUB_USER_ID));
				/*
				switch (Integer.parseInt(pcfResponse[index].
						getParameterValue(MQConstants.MQIACF_DURABLE_SUBSCRIPTION).toString())) {
				case MQConstants.MQSUB_DURABLE_NO:
					iMap.put("MQIACF_DURABLE_SUBSCRIPTION", "NO");
					break;
				case MQConstants.MQSUB_DURABLE_YES:
					iMap.put("MQIACF_DURABLE_SUBSCRIPTION", "YES");
					break;
				default:
					iMap.put("MQIACF_DURABLE_SUBSCRIPTION", "NULL");
					break;
				}
				*/
				
				iMap.put("MQIACF_DURABLE_SUBSCRIPTION", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACF_DURABLE_SUBSCRIPTION));
				
				iMap.put("MQIACF_SUB_TYPE", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACF_SUB_TYPE));
				iMap.put("MQCA_RESUME_DATE", pcfResponse[index]
						.getParameterValue(MQConstants.MQCA_RESUME_DATE));
				iMap.put("MQCA_RESUME_TIME", pcfResponse[index]
						.getParameterValue(MQConstants.MQCA_RESUME_TIME));
				iMap.put("MQCACF_LAST_MSG_DATE", pcfResponse[index]
						.getParameterValue(MQConstants.MQCACF_LAST_MSG_DATE));
				iMap.put("MQCACF_LAST_MSG_TIME", pcfResponse[index]
						.getParameterValue(MQConstants.MQCACF_LAST_MSG_TIME));
				iMap.put("MQIACF_MESSAGE_COUNT", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACF_MESSAGE_COUNT));
				iMap.put("MQBACF_CONNECTION_ID", pcfResponse[index]
						.getParameterValue(MQConstants.MQBACF_CONNECTION_ID));
				iMap.put("MQCACF_LAST_PUB_DATE", pcfResponse[index]
						.getParameterValue(MQConstants.MQCACF_LAST_PUB_DATE));
				iMap.put("MQCACF_LAST_PUB_TIME", pcfResponse[index]
						.getParameterValue(MQConstants.MQCACF_LAST_PUB_TIME));
				iMap.put("MQIACF_PUBLISH_COUNT", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACF_PUBLISH_COUNT));
				iMap.put("MQBACF_CONNECTION_ID", pcfResponse[index]
						.getParameterValue(MQConstants.MQBACF_CONNECTION_ID));

				topicListDtl.add(iMap);
		}

		// pcfCM.DestroyAgent();
		agent.disconnect();

		return topicListDtl;
	}

	public List<Map> ListSubNames(String qmgrHost, int qmgrPort, String qChannel)
			throws PCFException, MQDataException, IOException {
		List<Map> subListDtl = new ArrayList<Map>();
		Map iMap = new HashMap();

		PCFMessageAgent agent = new PCFMessageAgent(qmgrHost, qmgrPort,
				qChannel);

		// Create the PCF message type for the inquire.
		PCFMessage pcfCmd = new PCFMessage(
				MQConstants.MQCMD_INQUIRE_SUBSCRIPTION);

		// Add the inquire rules.
		// Topic name = wildcard.
		pcfCmd.addParameter(MQConstants.MQCACF_SUB_NAME, "*");

		// Execute the command. The returned object is an array of PCF messages.
		PCFMessage[] pcfResponse = agent.send(pcfCmd);

		for (int index = 0; index < pcfResponse.length; index++) {
			if (!pcfResponse[index].toString().contains("SYSTEM")) {
				iMap = new HashMap();
				iMap.put("MQCACF_SUB_NAME", pcfResponse[index]
						.getParameterValue(MQConstants.MQCACF_SUB_NAME));
				iMap.put("MQCA_TOPIC_STRING", pcfResponse[index]
						.getParameterValue(MQConstants.MQCA_TOPIC_STRING));
				iMap.put("MQCA_TOPIC_NAME", pcfResponse[index]
						.getParameterValue(MQConstants.MQCA_TOPIC_NAME));
				iMap.put("MQCACF_DESTINATION", pcfResponse[index]
						.getParameterValue(MQConstants.MQCACF_DESTINATION));
				iMap.put(
						"MQCACF_DESTINATION_Q_MGR",
						pcfResponse[index]
								.getParameterValue(MQConstants.MQCACF_DESTINATION_Q_MGR));
				iMap.put(
						"MQBACF_DESTINATION_CORREL_ID",
						pcfResponse[index]
								.getParameterValue(MQConstants.MQBACF_DESTINATION_CORREL_ID));
				iMap.put("MQCACF_SUB_USER_ID", pcfResponse[index]
						.getParameterValue(MQConstants.MQCACF_SUB_USER_ID));
				iMap.put("MQCA_CREATION_DATE", pcfResponse[index]
						.getParameterValue(MQConstants.MQCA_CREATION_DATE));
				iMap.put("MQCA_CREATION_TIME", pcfResponse[index]
						.getParameterValue(MQConstants.MQCA_CREATION_TIME));
				iMap.put("MQCA_ALTERATION_DATE", pcfResponse[index]
						.getParameterValue(MQConstants.MQCA_ALTERATION_DATE));
				iMap.put("MQCA_ALTERATION_TIME", pcfResponse[index]
						.getParameterValue(MQConstants.MQCA_ALTERATION_TIME));

				subListDtl.add(iMap);
			}
		}

		agent.disconnect();

		return subListDtl;
	}

	public List<Map> ListSubStatus(String qmgrHost, int qmgrPort, String subName, String qChannel)
			throws PCFException, MQDataException, IOException {
		List<Map> subListDtl = new ArrayList<Map>();
		Map iMap = new HashMap();

		PCFMessageAgent agent = new PCFMessageAgent(qmgrHost, qmgrPort,
				qChannel);

		// Create the PCF message type for the inquire.
		PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_INQUIRE_SUB_STATUS);

		// Add the inquire rules.
		// Topic name = wildcard.
		pcfCmd.addParameter(MQConstants.MQCACF_SUB_NAME, subName);

		// Execute the command. The returned object is an array of PCF messages.
		PCFMessage[] pcfResponse = agent.send(pcfCmd);

		for (int index = 0; index < pcfResponse.length; index++) {
			if (!pcfResponse[index].toString().contains("SYSTEM")) {
				iMap = new HashMap();
				iMap.put("MQBACF_CONNECTION_ID", pcfResponse[index]
						.getParameterValue(MQConstants.MQBACF_CONNECTION_ID));
				
				switch (Integer.parseInt(pcfResponse[index].
						getParameterValue(MQConstants.MQIACF_DURABLE_SUBSCRIPTION).toString())) {
				case MQConstants.MQSUB_DURABLE_NO:
					iMap.put("MQIACF_DURABLE_SUBSCRIPTION", "NO");
					break;
				case MQConstants.MQSUB_DURABLE_YES:
					iMap.put("MQIACF_DURABLE_SUBSCRIPTION", "YES");
					break;
				default:
					iMap.put("MQIACF_DURABLE_SUBSCRIPTION", "NULL");
					break;
				}

				
				iMap.put("MQCACF_LAST_MSG_DATE", pcfResponse[index]
						.getParameterValue(MQConstants.MQCACF_LAST_MSG_DATE));
				iMap.put("MQCACF_LAST_MSG_TIME", pcfResponse[index]
						.getParameterValue(MQConstants.MQCACF_LAST_MSG_TIME));
				iMap.put("MQIACF_PUBLISH_COUNT", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACF_PUBLISH_COUNT));
				iMap.put("MQCA_RESUME_DATE", pcfResponse[index]
						.getParameterValue(MQConstants.MQCA_RESUME_DATE));
				iMap.put("MQCA_RESUME_TIME", pcfResponse[index]
						.getParameterValue(MQConstants.MQCA_RESUME_TIME));
				iMap.put("MQCACF_SUB_USER_ID", pcfResponse[index]
						.getParameterValue(MQConstants.MQCACF_SUB_USER_ID));
				iMap.put("MQBACF_SUB_ID", pcfResponse[index]
						.getParameterValue(MQConstants.MQBACF_SUB_ID));
				
				subListDtl.add(iMap);
			}
		}

		agent.disconnect();

		return subListDtl;
	}

	public void createTopic(String qmgrHost, int qmgrPort, String topicName,
			String topicString, String topicDesc, String qChannel) throws PCFException,
			MQDataException, IOException {

		PCFMessageAgent agent = new PCFMessageAgent(qmgrHost, qmgrPort,
				qChannel);

		// Create the PCF message type for the inquire.
		PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_CREATE_TOPIC);

		// Add the inquire rules.
		// Topic name = wildcard.
		pcfCmd.addParameter(MQConstants.MQCA_TOPIC_NAME, topicName);
		pcfCmd.addParameter(MQConstants.MQCA_TOPIC_STRING, topicString);
		pcfCmd.addParameter(MQConstants.MQCA_TOPIC_DESC, topicDesc);
		// Execute the command. The returned object is an array of PCF messages.
		agent.send(pcfCmd);

		agent.disconnect();

	}

	public void createSub(String qmgrHost, int qmgrPort, String subName,
			String topicString, String topicName, String subDest,
			String subDestQM, String subUsrID, String qChannel) throws PCFException,
			MQDataException, IOException {

		PCFMessageAgent agent = new PCFMessageAgent(qmgrHost, qmgrPort,
				qChannel);

		// Create the PCF message type for the inquire.
		PCFMessage pcfCmd = new PCFMessage(
				MQConstants.MQCMD_CREATE_SUBSCRIPTION);

		// Add the inquire rules.
		// Topic name = wildcard.
		pcfCmd.addParameter(MQConstants.MQCACF_SUB_NAME, subName);
		pcfCmd.addParameter(MQConstants.MQCA_TOPIC_STRING, topicString);
		pcfCmd.addParameter(MQConstants.MQCA_TOPIC_NAME, topicName);
		pcfCmd.addParameter(MQConstants.MQCACF_DESTINATION, subDest);
		pcfCmd.addParameter(MQConstants.MQCACF_DESTINATION_Q_MGR, subDestQM);
		pcfCmd.addParameter(MQConstants.MQCACF_SUB_USER_ID, subUsrID);

		// Execute the command. The returned object is an array of PCF messages.
		agent.send(pcfCmd);

		agent.disconnect();
	}

	public List<Map> queueDetails(String qmgrHost, int qmgrPort,
			String queueName, String qChannel) throws PCFException, MQDataException, IOException {
		List<Map> queueDtl = new ArrayList<Map>();
		Map iMap = new HashMap();

		PCFMessageAgent agent = new PCFMessageAgent(qmgrHost, qmgrPort,
				qChannel);

		PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_INQUIRE_Q);
		pcfCmd.addParameter(MQConstants.MQCA_Q_NAME, queueName);

		// Execute the command. The returned object is an array of PCF messages.
		PCFMessage[] pcfResponse = agent.send(pcfCmd);

		for (int index = 0; index < pcfResponse.length; index++) {
			iMap = new HashMap();
			iMap.put("MQCA_Q_NAME", pcfResponse[index]
					.getParameterValue(MQConstants.MQCA_Q_NAME));
			iMap.put("MQCA_ALTERATION_DATE", pcfResponse[index]
					.getParameterValue(MQConstants.MQCA_ALTERATION_DATE));
			iMap.put("MQCA_ALTERATION_TIME", pcfResponse[index]
					.getParameterValue(MQConstants.MQCA_ALTERATION_TIME));
			iMap.put("MQCA_BACKOUT_REQ_Q_NAME", pcfResponse[index]
					.getParameterValue(MQConstants.MQCA_BACKOUT_REQ_Q_NAME));
			iMap.put("MQIA_BACKOUT_THRESHOLD", pcfResponse[index]
					.getParameterValue(MQConstants.MQIA_BACKOUT_THRESHOLD));
			iMap.put("MQCA_CLUSTER_NAMELIST", pcfResponse[index]
					.getParameterValue(MQConstants.MQCA_CLUSTER_NAMELIST));
			iMap.put("MQCA_CLUSTER_NAME", pcfResponse[index]
					.getParameterValue(MQConstants.MQCA_CLUSTER_NAME));
			iMap.put("MQCA_CREATION_DATE", pcfResponse[index]
					.getParameterValue(MQConstants.MQCA_CREATION_DATE));
			iMap.put("MQCA_CREATION_TIME", pcfResponse[index]
					.getParameterValue(MQConstants.MQCA_CREATION_TIME));
			iMap.put("MQIA_CURRENT_Q_DEPTH", pcfResponse[index]
					.getParameterValue(MQConstants.MQIA_CURRENT_Q_DEPTH));
			iMap.put("MQIA_DEF_PERSISTENCE", pcfResponse[index]
					.getParameterValue(MQConstants.MQIA_DEF_PERSISTENCE));
			iMap.put("MQIA_OPEN_INPUT_COUNT", pcfResponse[index]
					.getParameterValue(MQConstants.MQIA_OPEN_INPUT_COUNT));
			iMap.put("MQIA_MAX_Q_DEPTH", pcfResponse[index]
					.getParameterValue(MQConstants.MQIA_MAX_Q_DEPTH));
			iMap.put("MQIA_MAX_MSG_LENGTH", pcfResponse[index]
					.getParameterValue(MQConstants.MQIA_MAX_MSG_LENGTH));
			iMap.put("MQIA_OPEN_OUTPUT_COUNT", pcfResponse[index]
					.getParameterValue(MQConstants.MQIA_OPEN_OUTPUT_COUNT));
			iMap.put("MQIA_INHIBIT_PUT", pcfResponse[index]
					.getParameterValue(MQConstants.MQIA_INHIBIT_PUT));
			iMap.put("MQIA_Q_DEPTH_HIGH_LIMIT", pcfResponse[index]
					.getParameterValue(MQConstants.MQIA_Q_DEPTH_HIGH_LIMIT));
			iMap.put("MQIA_Q_DEPTH_LOW_LIMIT", pcfResponse[index]
					.getParameterValue(MQConstants.MQIA_Q_DEPTH_LOW_LIMIT));
			iMap.put("MQIA_Q_DEPTH_HIGH_EVENT", pcfResponse[index]
					.getParameterValue(MQConstants.MQIA_Q_DEPTH_HIGH_EVENT));
			iMap.put("MQIA_Q_DEPTH_LOW_EVENT", pcfResponse[index]
					.getParameterValue(MQConstants.MQIA_Q_DEPTH_LOW_EVENT));
			iMap.put("MQIA_Q_DEPTH_MAX_EVENT", pcfResponse[index]
					.getParameterValue(MQConstants.MQIA_Q_DEPTH_MAX_EVENT));
			iMap.put("MQIA_RETENTION_INTERVAL", pcfResponse[index]
					.getParameterValue(MQConstants.MQIA_RETENTION_INTERVAL));
			iMap.put("MQIA_TRIGGER_DEPTH", pcfResponse[index]
					.getParameterValue(MQConstants.MQIA_TRIGGER_DEPTH));

			queueDtl.add(iMap);
		}

		agent.disconnect();
		return queueDtl;
	}

	public List<Map> queueStatus(String qmgrHost, int qmgrPort, String queueName, String qChannel)
			throws PCFException, MQDataException, IOException {
		List<Map> queueDtl = new ArrayList<Map>();
		Map iMap = new HashMap();

		PCFMessageAgent agent = new PCFMessageAgent(qmgrHost, qmgrPort,
				qChannel);

		PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_INQUIRE_Q_STATUS);
		pcfCmd.addParameter(MQConstants.MQCA_Q_NAME, queueName);

		// Execute the command. The returned object is an array of PCF messages.
		PCFMessage[] pcfResponse = agent.send(pcfCmd);

		for (int index = 0; index < pcfResponse.length; index++) {
			iMap = new HashMap();
			iMap.put("MQCACF_LAST_GET_DATE", pcfResponse[index]
					.getParameterValue(MQConstants.MQCACF_LAST_GET_DATE));
			iMap.put("MQCACF_LAST_GET_TIME", pcfResponse[index]
					.getParameterValue(MQConstants.MQCACF_LAST_GET_TIME));
			iMap.put("MQCACF_LAST_PUT_DATE", pcfResponse[index]
					.getParameterValue(MQConstants.MQCACF_LAST_PUT_DATE));
			iMap.put("MQCACF_LAST_PUT_TIME", pcfResponse[index]
					.getParameterValue(MQConstants.MQCACF_LAST_PUT_TIME));
			iMap.put("MQIACF_OLDEST_MSG_AGE", pcfResponse[index]
					.getParameterValue(MQConstants.MQIACF_OLDEST_MSG_AGE));
			iMap.put("MQIACF_Q_TIME_INDICATOR", pcfResponse[index]
					.getParameterValue(MQConstants.MQIACF_Q_TIME_INDICATOR));
			iMap.put("MQIACF_UNCOMMITTED_MSGS", pcfResponse[index]
					.getParameterValue(MQConstants.MQIACF_UNCOMMITTED_MSGS));

			queueDtl.add(iMap);
		}

		agent.disconnect();
		return queueDtl;
	}

	public List<Map> channelDetails(String qmgrHost, int qmgrPort, String qChannel)
			throws PCFException, MQDataException, IOException {
		List<Map> channelDtl = new ArrayList<Map>();
		Map iMap = new HashMap();

		PCFMessageAgent agent = new PCFMessageAgent(qmgrHost, qmgrPort,
				qChannel);

		PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_INQUIRE_CHANNEL);
		pcfCmd.addParameter(MQConstants.MQCACH_CHANNEL_NAME, "*");

		// Execute the command. The returned object is an array of PCF messages.
		PCFMessage[] pcfResponse = agent.send(pcfCmd);

		try {
			for (int index = 0; index < pcfResponse.length; index++) {
				iMap = new HashMap();
				iMap.put("MQCACH_CHANNEL_NAME", pcfResponse[index]
						.getParameterValue(MQConstants.MQCACH_CHANNEL_NAME));

				switch (Integer.parseInt(pcfResponse[index].getParameterValue(
						MQConstants.MQIACH_CHANNEL_TYPE).toString())) {
				case MQConstants.MQCHT_SENDER:
					iMap.put("MQIACH_CHANNEL_TYPE", "SENDER");
					break;
				case MQConstants.MQCHT_RECEIVER:
					iMap.put("MQIACH_CHANNEL_TYPE", "RECEIVER");
					break;
				case MQConstants.MQCHT_REQUESTER:
					iMap.put("MQIACH_CHANNEL_TYPE", "REQUESTER");
					break;
				case MQConstants.MQCHT_CLUSSDR:
					iMap.put("MQIACH_CHANNEL_TYPE", "CLUSSDR");
					break;
				case MQConstants.MQCHT_CLUSRCVR:
					iMap.put("MQIACH_CHANNEL_TYPE", "CLUSREC");
					break;
				case MQConstants.MQCHT_SVRCONN:
					iMap.put("MQIACH_CHANNEL_TYPE", "SERVERCONN");
					break;
				case MQConstants.MQCHT_SERVER:
					iMap.put("MQIACH_CHANNEL_TYPE", "SERVER");
					break;
				default:
					;
					break;
				}
				iMap.put("MQCA_ALTERATION_DATE", pcfResponse[index]
						.getParameterValue(MQConstants.MQCA_ALTERATION_DATE));
				iMap.put("MQCA_ALTERATION_TIME", pcfResponse[index]
						.getParameterValue(MQConstants.MQCA_ALTERATION_TIME));
				iMap.put("MQIACH_BATCH_SIZE", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_BATCH_SIZE));
				iMap.put("MQIACH_HDR_COMPRESSION", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_HDR_COMPRESSION));
				/*
				 * System.out.println(Integer.parseInt(pcfResponse[index].
				 * getParameterValue
				 * (MQConstants.MQIACH_HDR_COMPRESSION).toString()));
				 * 
				 * switch
				 * (Integer.parseInt(pcfResponse[index].getParameterValue(
				 * MQConstants.MQIACH_HDR_COMPRESSION).toString())){ case
				 * MQConstants.MQCOMPRESS_NONE:
				 * iMap.put("MQIACH_HDR_COMPRESSION","NONE"); break; case
				 * MQConstants.MQCOMPRESS_SYSTEM:
				 * iMap.put("MQIACH_HDR_COMPRESSION","SYSTEM"); break; }
				 */
				iMap.put("MQIACH_MSG_COMPRESSION", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_MSG_COMPRESSION));
				iMap.put("MQCACH_DESC", pcfResponse[index]
						.getParameterValue(MQConstants.MQCACH_DESC));
				iMap.put("MQIACH_HB_INTERVAL", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_HB_INTERVAL));
				iMap.put(
						"MQIACH_KEEP_ALIVE_INTERVAL",
						pcfResponse[index]
								.getParameterValue(MQConstants.MQIACH_KEEP_ALIVE_INTERVAL));
				iMap.put("MQIACH_MAX_MSG_LENGTH", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_MAX_MSG_LENGTH));
				iMap.put("MQCACH_MCA_USER_ID", pcfResponse[index]
						.getParameterValue(MQConstants.MQCACH_MCA_USER_ID));
				iMap.put("MQIA_MONITORING_CHANNEL", pcfResponse[index]
						.getParameterValue(MQConstants.MQIA_MONITORING_CHANNEL));
				iMap.put(
						"MQCACH_MR_EXIT_USER_DATA",
						pcfResponse[index]
								.getParameterValue(MQConstants.MQCACH_MR_EXIT_USER_DATA));
				iMap.put("MQCACH_MR_EXIT_NAME", pcfResponse[index]
						.getParameterValue(MQConstants.MQCACH_MR_EXIT_NAME));
				iMap.put("MQIACH_MR_COUNT", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_MR_COUNT));
				iMap.put("MQIACH_MR_INTERVAL", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_MR_INTERVAL));
				iMap.put(
						"MQCACH_MSG_EXIT_USER_DATA",
						pcfResponse[index]
								.getParameterValue(MQConstants.MQCACH_MSG_EXIT_USER_DATA));
				iMap.put("MQCACH_MSG_EXIT_NAME", pcfResponse[index]
						.getParameterValue(MQConstants.MQCACH_MSG_EXIT_NAME));
				iMap.put("MQIACH_NPM_SPEED", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_NPM_SPEED));
				iMap.put("MQIACH_PUT_AUTHORITY", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_PUT_AUTHORITY));
				iMap.put(
						"MQCACH_RCV_EXIT_USER_DATA",
						pcfResponse[index]
								.getParameterValue(MQConstants.MQCACH_RCV_EXIT_USER_DATA));
				iMap.put("MQCACH_RCV_EXIT_NAME", pcfResponse[index]
						.getParameterValue(MQConstants.MQCACH_RCV_EXIT_NAME));
				iMap.put("MQIACH_RESET_REQUESTED", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_RESET_REQUESTED));
				iMap.put(
						"MQCACH_SEC_EXIT_USER_DATA",
						pcfResponse[index]
								.getParameterValue(MQConstants.MQCACH_SEC_EXIT_USER_DATA));
				iMap.put("MQCACH_SEC_EXIT_NAME", pcfResponse[index]
						.getParameterValue(MQConstants.MQCACH_SEC_EXIT_NAME));
				iMap.put(
						"MQCACH_SEND_EXIT_USER_DATA",
						pcfResponse[index]
								.getParameterValue(MQConstants.MQCACH_SEND_EXIT_USER_DATA));
				iMap.put("MQCACH_SEND_EXIT_NAME", pcfResponse[index]
						.getParameterValue(MQConstants.MQCACH_SEND_EXIT_NAME));
				iMap.put(
						"MQIACH_SEQUENCE_NUMBER_WRAP",
						pcfResponse[index]
								.getParameterValue(MQConstants.MQIACH_SEQUENCE_NUMBER_WRAP));
				iMap.put("MQIACH_SSL_CLIENT_AUTH", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_SSL_CLIENT_AUTH));
				iMap.put("MQCACH_SSL_CIPHER_SPEC", pcfResponse[index]
						.getParameterValue(MQConstants.MQCACH_SSL_CIPHER_SPEC));
				iMap.put("MQCACH_SSL_PEER_NAME", pcfResponse[index]
						.getParameterValue(MQConstants.MQCACH_SSL_PEER_NAME));
				iMap.put("MQIA_STATISTICS_CHANNEL", pcfResponse[index]
						.getParameterValue(MQConstants.MQIA_STATISTICS_CHANNEL));
				/*
				 * iMap.put( "MQIACH_XMIT_PROTOCOL_TYPE", pcfResponse[index]
				 * .getParameterValue(MQConstants.MQIACH_XMIT_PROTOCOL_TYPE));
				 */
				int intXmitProt = (int) pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_XMIT_PROTOCOL_TYPE);

				switch (intXmitProt) {
				case MQConstants.MQXPT_LU62:
					iMap.put("MQIACH_XMIT_PROTOCOL_TYPE", "LU62");
					break;
				case MQConstants.MQXPT_TCP:
					iMap.put("MQIACH_XMIT_PROTOCOL_TYPE", "TCP");
					break;
				case MQConstants.MQXPT_NETBIOS:
					iMap.put("MQIACH_XMIT_PROTOCOL_TYPE", "NetBios");
					break;
				case MQConstants.MQXPT_SPX:
					iMap.put("MQIACH_XMIT_PROTOCOL_TYPE", "SPX");
					break;
				case MQConstants.MQXPT_DECNET:
					iMap.put("MQIACH_XMIT_PROTOCOL_TYPE", "DECnet");
					break;
				}

				try {
					int intUseDLQ = (int) pcfResponse[index]
							.getParameterValue(MQConstants.MQIA_USE_DEAD_LETTER_Q);
					switch (intUseDLQ) {
					case MQConstants.MQUSEDLQ_NO:
						iMap.put("MQIA_USE_DEAD_LETTER_Q", "NO");
						break;
					case MQConstants.MQUSEDLQ_YES:
						iMap.put("MQIA_USE_DEAD_LETTER_Q", "YES");
						break;
					default:
						iMap.put("MQIA_USE_DEAD_LETTER_Q", "UNKNOWN");
						break;
					}

				} catch (NullPointerException ne) {
					iMap.put("MQIA_USE_DEAD_LETTER_Q", "UNKNOWN");
				}

				String strChlStat = (String) pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_CHANNEL_STATUS);

				if (strChlStat == null) {
					iMap.put("MQIACH_CHANNEL_STATUS", "UNKNOWN");
				} else {
					switch (Integer.parseInt(pcfResponse[index]
							.getParameterValue(
									MQConstants.MQIACH_CHANNEL_STATUS)
							.toString())) {
					case MQConstants.MQCHS_DISCONNECTED:
						iMap.put("MQIACH_CHANNEL_STATUS", "DISCONNECTED");
						break;
					case MQConstants.MQCHS_RUNNING:
						iMap.put("MQIACH_CHANNEL_TYPE", "RUNNING");
						break;
					default:
						iMap.put("MQIACH_CHANNEL_TYPE", "UNKNOWN");
						break;
					}

				}

				channelDtl.add(iMap);
			}

			agent.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return channelDtl;
	}

	public List<Map> channelStatus(String qmgrHost, int qmgrPort, String chlName)
			throws PCFException, MQDataException, IOException {
		List<Map> channelStatus = new ArrayList<Map>();
		Map iMap = new HashMap();

		PCFMessageAgent agent = new PCFMessageAgent(qmgrHost, qmgrPort, chlName);

		PCFMessage pcfCmd = new PCFMessage(
				MQConstants.MQCMD_INQUIRE_CHANNEL_STATUS);
		pcfCmd.addParameter(MQConstants.MQCACH_CHANNEL_NAME, chlName);
		// pcfCmd.addParameter(MQConstants.MQIACH_CHANNEL_INSTANCE_ATTRS,MQConstants.MQCACH_CONNECTION_NAME);

		// Execute the command. The returned object is an array of PCF messages.
		PCFMessage[] pcfResponse = agent.send(pcfCmd);

		try {
			for (int index = 0; index < pcfResponse.length; index++) {
				iMap = new HashMap();
				iMap.put("MQIACH_BATCHES", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_BATCHES));
				iMap.put("MQIACH_BATCH_SIZE", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_BATCH_SIZE));
				iMap.put(
						"MQIACH_BATCH_SIZE_INDICATOR",
						pcfResponse[index]
								.getParameterValue(MQConstants.MQIACH_BATCH_SIZE_INDICATOR));
				iMap.put("MQIACH_BUFFERS_RCVD", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_BUFFERS_RCVD));
				iMap.put("MQIACH_BUFFERS_SENT", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_BUFFERS_SENT));
				iMap.put("MQIACH_BYTES_RCVD", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_BYTES_RCVD));
				iMap.put("MQIACH_BYTES_SENT", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_BYTES_SENT));
				iMap.put("MQIACH_CHANNEL_DISP", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_CHANNEL_DISP));
				// iMap.put("MQIACH_CHANNEL_INSTANCE_TYPE",pcfResponse[index].getParameterValue(MQConstants.MQIACH_CHANNEL_INSTANCE_TYPE));
				iMap.put("MQIA_MONITORING_CHANNEL", pcfResponse[index]
						.getParameterValue(MQConstants.MQIA_MONITORING_CHANNEL));
				iMap.put(
						"MQCACH_CHANNEL_START_DATE",
						pcfResponse[index]
								.getParameterValue(MQConstants.MQCACH_CHANNEL_START_DATE));
				iMap.put(
						"MQCACH_CHANNEL_START_TIME",
						pcfResponse[index]
								.getParameterValue(MQConstants.MQCACH_CHANNEL_START_TIME));
				iMap.put("MQIACH_CHANNEL_STATUS", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_CHANNEL_STATUS));
				iMap.put("MQIACH_CHANNEL_TYPE", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_CHANNEL_TYPE));
				iMap.put("MQIACH_COMPRESSION_RATE", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_COMPRESSION_RATE));
				iMap.put("MQIACH_COMPRESSION_TIME", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_COMPRESSION_TIME));
				iMap.put("MQCACH_CONNECTION_NAME", pcfResponse[index]
						.getParameterValue(MQConstants.MQCACH_CONNECTION_NAME));
				iMap.put("MQCACH_CURRENT_LUWID", pcfResponse[index]
						.getParameterValue(MQConstants.MQCACH_CURRENT_LUWID));
				iMap.put("MQIACH_CURRENT_MSGS", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_CURRENT_MSGS));
				iMap.put(
						"MQIACH_CURRENT_SEQ_NUMBER",
						pcfResponse[index]
								.getParameterValue(MQConstants.MQIACH_CURRENT_SEQ_NUMBER));
				iMap.put(
						"MQIACH_CURRENT_SHARING_CONVS",
						pcfResponse[index]
								.getParameterValue(MQConstants.MQIACH_CURRENT_SHARING_CONVS));
				iMap.put(
						"MQIACH_EXIT_TIME_INDICATOR",
						pcfResponse[index]
								.getParameterValue(MQConstants.MQIACH_EXIT_TIME_INDICATOR));
				iMap.put("MQIACH_HDR_COMPRESSION", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_HDR_COMPRESSION));
				iMap.put("MQIACH_HB_INTERVAL", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_HB_INTERVAL));
				iMap.put("MQIACH_INDOUBT_STATUS", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_INDOUBT_STATUS));
				iMap.put(
						"MQIACH_KEEP_ALIVE_INTERVAL",
						pcfResponse[index]
								.getParameterValue(MQConstants.MQIACH_KEEP_ALIVE_INTERVAL));
				iMap.put("MQCACH_LAST_LUWID", pcfResponse[index]
						.getParameterValue(MQConstants.MQCACH_LAST_LUWID));
				iMap.put("MQCACH_LAST_MSG_DATE", pcfResponse[index]
						.getParameterValue(MQConstants.MQCACH_LAST_MSG_DATE));
				iMap.put("MQCACH_LAST_MSG_TIME", pcfResponse[index]
						.getParameterValue(MQConstants.MQCACH_LAST_MSG_TIME));
				iMap.put("MQIACH_LAST_SEQ_NUMBER", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_LAST_SEQ_NUMBER));
				iMap.put("MQCACH_LOCAL_ADDRESS", pcfResponse[index]
						.getParameterValue(MQConstants.MQCACH_LOCAL_ADDRESS));
				iMap.put(
						"MQIACH_LONG_RETRIES_LEFT",
						pcfResponse[index]
								.getParameterValue(MQConstants.MQIACH_LONG_RETRIES_LEFT));
				iMap.put("MQIACH_MAX_MSG_LENGTH", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_MAX_MSG_LENGTH));
				iMap.put(
						"MQIACH_MAX_SHARING_CONVS",
						pcfResponse[index]
								.getParameterValue(MQConstants.MQIACH_MAX_SHARING_CONVS));
				iMap.put("MQCACH_MCA_JOB_NAME", pcfResponse[index]
						.getParameterValue(MQConstants.MQCACH_MCA_JOB_NAME));
				iMap.put("MQIACH_MCA_STATUS", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_MCA_STATUS));
				iMap.put("MQCACH_MCA_USER_ID", pcfResponse[index]
						.getParameterValue(MQConstants.MQCACH_MCA_USER_ID));
				iMap.put("MQIACH_MSG_COMPRESSION", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_MSG_COMPRESSION));
				iMap.put("MQIACH_MSGS", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_MSGS));
				iMap.put(
						"MQIACH_XMITQ_MSGS_AVAILABLE",
						pcfResponse[index]
								.getParameterValue(MQConstants.MQIACH_XMITQ_MSGS_AVAILABLE));
				iMap.put(
						"MQIACH_NETWORK_TIME_INDICATOR",
						pcfResponse[index]
								.getParameterValue(MQConstants.MQIACH_NETWORK_TIME_INDICATOR));
				iMap.put("MQIACH_NPM_SPEED", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_NPM_SPEED));
				iMap.put("MQCACH_REMOTE_APPL_TAG", pcfResponse[index]
						.getParameterValue(MQConstants.MQCACH_REMOTE_APPL_TAG));
				iMap.put("MQCA_REMOTE_Q_MGR_NAME", pcfResponse[index]
						.getParameterValue(MQConstants.MQCA_REMOTE_Q_MGR_NAME));
				iMap.put(
						"MQIACH_SHORT_RETRIES_LEFT",
						pcfResponse[index]
								.getParameterValue(MQConstants.MQIACH_SHORT_RETRIES_LEFT));
				iMap.put("MQCACH_SSL_CERT_USER_ID", pcfResponse[index]
						.getParameterValue(MQConstants.MQCACH_SSL_CERT_USER_ID));
				iMap.put(
						"MQCACH_SSL_KEY_RESET_DATE",
						pcfResponse[index]
								.getParameterValue(MQConstants.MQCACH_SSL_KEY_RESET_DATE));
				iMap.put("MQIACH_SSL_KEY_RESETS", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_SSL_KEY_RESETS));
				iMap.put(
						"MQCACH_SSL_KEY_RESET_TIME",
						pcfResponse[index]
								.getParameterValue(MQConstants.MQCACH_SSL_KEY_RESET_TIME));
				iMap.put(
						"MQCACH_SSL_SHORT_PEER_NAME",
						pcfResponse[index]
								.getParameterValue(MQConstants.MQCACH_SSL_SHORT_PEER_NAME));
				iMap.put("MQIACH_STOP_REQUESTED", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_STOP_REQUESTED));
				iMap.put("MQIACH_CHANNEL_SUBSTATE", pcfResponse[index]
						.getParameterValue(MQConstants.MQIACH_CHANNEL_SUBSTATE));
				iMap.put("MQCACH_XMIT_Q_NAME", pcfResponse[index]
						.getParameterValue(MQConstants.MQCACH_XMIT_Q_NAME));
				iMap.put(
						"MQIACH_XMITQ_TIME_INDICATOR",
						pcfResponse[index]
								.getParameterValue(MQConstants.MQIACH_XMITQ_TIME_INDICATOR));

				channelStatus.add(iMap);
			}
			agent.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return channelStatus;
	}

	public List<Map> listenerDetails(String qmgrHost, int qmgrPort,
			String qChannel) throws PCFException, MQDataException, IOException {
		List<Map> listenerDtl = new ArrayList<Map>();
		Map iMap = new HashMap();

		PCFMessageAgent agent = new PCFMessageAgent(qmgrHost, qmgrPort,
				qChannel);

		PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_INQUIRE_LISTENER);
		pcfCmd.addParameter(MQConstants.MQCACH_LISTENER_NAME, "*");

		// Execute the command. The returned object is an array of PCF messages.
		PCFMessage[] pcfResponse = agent.send(pcfCmd);

		for (int index = 0; index < pcfResponse.length; index++) {
			iMap = new HashMap();

			iMap.put("MQCACH_LISTENER_NAME", pcfResponse[index]
					.getParameterValue(MQConstants.MQCACH_LISTENER_NAME));

			iMap.put("MQIACH_LISTENER_CONTROL", pcfResponse[index]
					.getParameterValue(MQConstants.MQIACH_LISTENER_CONTROL));

			int intListCtrl = (int) pcfResponse[index]
					.getParameterValue(MQConstants.MQIACH_LISTENER_CONTROL);

			switch (intListCtrl) {
			case MQConstants.MQSVC_CONTROL_MANUAL:
				iMap.put("MQIACH_LISTENER_CONTROL", "Manual");
				break;
			case MQConstants.MQSVC_CONTROL_Q_MGR:
				iMap.put("MQIACH_LISTENER_CONTROL", "Queue Manager");
				break;
			case MQConstants.MQSVC_CONTROL_Q_MGR_START:
				iMap.put("MQIACH_LISTENER_CONTROL", "Queue Manager Start");
				break;
			}

			iMap.put("MQCA_ALTERATION_DATE", pcfResponse[index]
					.getParameterValue(MQConstants.MQCA_ALTERATION_DATE));
			iMap.put("MQCA_ALTERATION_TIME", pcfResponse[index]
					.getParameterValue(MQConstants.MQCA_ALTERATION_TIME));
			int intXmitProt = (int) pcfResponse[index]
					.getParameterValue(MQConstants.MQIACH_XMIT_PROTOCOL_TYPE);

			switch (intXmitProt) {
			case MQConstants.MQXPT_LU62:
				iMap.put("MQIACH_XMIT_PROTOCOL_TYPE", "LU62");
				break;
			case MQConstants.MQXPT_TCP:
				iMap.put("MQIACH_XMIT_PROTOCOL_TYPE", "TCP");
				break;
			case MQConstants.MQXPT_NETBIOS:
				iMap.put("MQIACH_XMIT_PROTOCOL_TYPE", "NetBios");
				break;
			case MQConstants.MQXPT_SPX:
				iMap.put("MQIACH_XMIT_PROTOCOL_TYPE", "SPX");
				break;
			case MQConstants.MQXPT_DECNET:
				iMap.put("MQIACH_XMIT_PROTOCOL_TYPE", "DECnet");
				break;
			}

			iMap.put("MQCACH_TP_NAME", pcfResponse[index]
					.getParameterValue(MQConstants.MQCACH_TP_NAME));
			iMap.put("MQCACH_LISTENER_DESC", pcfResponse[index]
					.getParameterValue(MQConstants.MQCACH_LISTENER_DESC));

			listenerDtl.add(iMap);
		}

		agent.disconnect();

		return listenerDtl;
	}

	public String createQueue(String qmgrHost, int qmgrPort, String qType,
			String qName, boolean xmitType, String backoutQName, String qChannel)
			throws PCFException, MQDataException, IOException {
		String resOutput = new String();

		PCFMessageAgent agent = new PCFMessageAgent(qmgrHost, qmgrPort,
				qChannel);

		int queueType = 0;
		switch (qType) {
		case "LOCAL":
			queueType = MQConstants.MQQT_LOCAL;
			break;
		case "ALIAS":
			queueType = MQConstants.MQQT_ALIAS;
			break;
		case "MODEL":
			queueType = MQConstants.MQQT_MODEL;
			break;
		case "CLUSTER":
			queueType = MQConstants.MQQT_CLUSTER;
			break;
		case "REMOTE":
			queueType = MQConstants.MQQT_REMOTE;
			break;
		default:
			queueType = MQConstants.MQQT_LOCAL;
			break;
		}

		// Create the PCF message type for the create queue.
		// NB: The parameters must be added in a specific order or an exception
		// (3015)
		// will be thrown.
		PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_CREATE_Q);

		// Queue name - Mandatory.
		pcfCmd.addParameter(MQConstants.MQCA_Q_NAME, qName);

		// Queue Type - Optional.
		pcfCmd.addParameter(MQConstants.MQIA_Q_TYPE, queueType);

		if (xmitType) {
			// Queue Type - This must be the second parameter!
			pcfCmd.addParameter(MQConstants.MQIA_USAGE,
					MQConstants.MQUS_TRANSMISSION);
		}

		pcfCmd.addParameter(MQConstants.MQIA_DEF_PERSISTENCE,
				MQConstants.MQPER_PERSISTENT);
		if (!backoutQName.equals("")) {
			pcfCmd.addParameter(MQConstants.MQCA_BACKOUT_REQ_Q_NAME,
					backoutQName);
		}

		try {
			// Execute the command. The returned object is an array of PCF
			// messages.
			// If the Queue already exists, then catch the exception, otherwise
			// rethrow.
			/* PCFMessage[] pcfResponse = */// We ignore the returned result
			agent.send(pcfCmd);
		} catch (PCFException pcfe) {
			if (pcfe.reasonCode == MQConstants.MQRCCF_OBJECT_ALREADY_EXISTS) {

				resOutput = "The queue \"" + qName
						+ "\" already exists in the queue manager.";
			} else {
				resOutput = "The queue \"" + qName
						+ "\" could not be created on the queue manager.";
				throw pcfe;
			}
		}

		agent.disconnect();
		return resOutput;
	}

	public void createChannel(String qmgrHost, int qmgrPort, String chanType,
			String chanName, String xmitQueue, String qChannel) throws MQDataException,
			IOException {

		PCFCommons pcfCM = new PCFCommons();
		PCFMessageAgent agent = new PCFMessageAgent(qmgrHost, qmgrPort,
				qChannel);

		// Create the PCF message type for the create channel.
		PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_CREATE_CHANNEL);

		// Add the create channel mandatory parameters.
		// Channel name.
		pcfCmd.addParameter(MQConstants.MQCACH_CHANNEL_NAME, chanName);

		switch (chanType) {
		case "SENDER":
			// Create the transmission Queue.
			pcfCM.createQueue(qmgrHost, qmgrPort, "local", xmitQueue, true, "", qChannel);
			// Channel type.
			pcfCmd.addParameter(MQConstants.MQIACH_CHANNEL_TYPE,
					MQConstants.MQCHT_SENDER);
			// Channel Connection.
			pcfCmd.addParameter(MQConstants.MQCACH_CONNECTION_NAME, qmgrHost
					+ "(" + qmgrPort + ")");
			// Channel Transmit Queue name.
			pcfCmd.addParameter(MQConstants.MQCACH_XMIT_Q_NAME, xmitQueue);
			break;
		case "RECEIVER":
			// Channel type.
			pcfCmd.addParameter(MQConstants.MQIACH_CHANNEL_TYPE,
					MQConstants.MQCHT_RECEIVER);
			break;
		case "REQUESTER":
			// Channel type.
			pcfCmd.addParameter(MQConstants.MQIACH_CHANNEL_TYPE,
					MQConstants.MQCHT_REQUESTER);
			// Channel Connection.
			pcfCmd.addParameter(MQConstants.MQCACH_CONNECTION_NAME, qmgrHost
					+ "(" + qmgrPort + ")");
			break;
		case "CLUSSDR":
			// Channel type.
			pcfCmd.addParameter(MQConstants.MQIACH_CHANNEL_TYPE,
					MQConstants.MQCHT_CLUSSDR);
			// Channel Connection.
			pcfCmd.addParameter(MQConstants.MQCACH_CONNECTION_NAME, qmgrHost
					+ "(" + qmgrPort + ")");
			break;
		case "CLUSREC":
			// Channel type.
			pcfCmd.addParameter(MQConstants.MQIACH_CHANNEL_TYPE,
					MQConstants.MQCHT_CLUSRCVR);
			break;
		case "SERVERCONN":
			// Channel type.
			pcfCmd.addParameter(MQConstants.MQIACH_CHANNEL_TYPE,
					MQConstants.MQCHT_SVRCONN);
			break;
		case "SERVER":
			// Create the transmission Queue.
			pcfCM.createQueue(qmgrHost, qmgrPort, "local", xmitQueue, true, "", qChannel);
			// Channel type.
			pcfCmd.addParameter(MQConstants.MQIACH_CHANNEL_TYPE,
					MQConstants.MQCHT_SERVER);
			// Channel Transmit Queue name.
			pcfCmd.addParameter(MQConstants.MQCACH_XMIT_Q_NAME, xmitQueue);
			break;
		default:
			;
			break;
		}

		// Execute the command. If the command causes the
		// 'MQRCCF_OBJECT_ALREADY_EXISTS' exception
		// to be thrown, catch it here as this is ok.
		// If successful, the returned object is an array of PCF messages.
		try {
			/* PCFMessage[] pcfResponse = */// We ignore the returned result
			agent.send(pcfCmd);
		} catch (PCFException pcfe) {
			if (pcfe.reasonCode != MQConstants.MQRCCF_OBJECT_ALREADY_EXISTS) {
				throw pcfe;
			}
		}

		agent.disconnect();

		return;
	}

	public void createListener(String qmgrHost, int qmgrPort, String listType,
			String listName, int portNum, String qChannel) throws MQDataException, IOException {

		PCFCommons pcfCM = new PCFCommons();
		PCFMessageAgent agent = new PCFMessageAgent(qmgrHost, qmgrPort,
				qChannel);

		// Create the PCF message type for the create channel.
		PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_CREATE_LISTENER);

		// Add the create channel mandatory parameters.
		// Channel name.
		pcfCmd.addParameter(MQConstants.MQCACH_LISTENER_NAME, listName);

		switch (listType) {
		case "TCP":
			// Listener type.
			pcfCmd.addParameter(MQConstants.MQIACH_XMIT_PROTOCOL_TYPE,
					MQConstants.MQXPT_TCP);
			// Port Number.
			pcfCmd.addParameter(MQConstants.MQIACH_PORT, portNum);
			// Listener Control.
			pcfCmd.addParameter(MQConstants.MQIACH_LISTENER_CONTROL,
					MQConstants.MQSVC_CONTROL_Q_MGR_START);
			break;
		case "LU6.2":
			// Listener type.
			pcfCmd.addParameter(MQConstants.MQIACH_XMIT_PROTOCOL_TYPE,
					MQConstants.MQXPT_LU62);
			// Listener Control.
			pcfCmd.addParameter(MQConstants.MQIACH_LISTENER_CONTROL,
					MQConstants.MQSVC_CONTROL_Q_MGR_START);
			break;
		case "SPX":
			// Listener type.
			pcfCmd.addParameter(MQConstants.MQIACH_XMIT_PROTOCOL_TYPE,
					MQConstants.MQXPT_SPX);
			// Listener Control.
			pcfCmd.addParameter(MQConstants.MQIACH_LISTENER_CONTROL,
					MQConstants.MQSVC_CONTROL_Q_MGR_START);
			break;
		case "NetBIOS":
			// Listener type.
			pcfCmd.addParameter(MQConstants.MQIACH_XMIT_PROTOCOL_TYPE,
					MQConstants.MQXPT_NETBIOS);
			// Listener Control.
			pcfCmd.addParameter(MQConstants.MQIACH_LISTENER_CONTROL,
					MQConstants.MQSVC_CONTROL_Q_MGR_START);
			break;
		}

		// Execute the command. If the command causes the
		// 'MQRCCF_OBJECT_ALREADY_EXISTS' exception
		// to be thrown, catch it here as this is ok.
		// If successful, the returned object is an array of PCF messages.
		try {
			/* PCFMessage[] pcfResponse = */// We ignore the returned result
			agent.send(pcfCmd);
		} catch (PCFException pcfe) {
			if (pcfe.reasonCode != MQConstants.MQRCCF_OBJECT_ALREADY_EXISTS) {
				throw pcfe;
			}
		}
		agent.disconnect();

		return;
	}

	public StringBuffer createQScript(String qmgrHost, int qmgrPort,
			String qName, String qChannel) throws MQDataException, IOException {

		PCFCommons pcfCM = new PCFCommons();
		PCFMessageAgent agent = new PCFMessageAgent(qmgrHost, qmgrPort,
				qChannel);

		PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_INQUIRE_Q);
		pcfCmd.addParameter(MQConstants.MQCA_Q_NAME, qName);

		// Execute the command. The returned object is an array of PCF messages.
		PCFMessage[] pcfResponse = agent.send(pcfCmd);

		StringBuffer sb = new StringBuffer();
		if (pcfResponse[0].getParameterValue(MQConstants.MQIA_Q_TYPE)
				.toString().indexOf("SYSTEM") != 0) {
			switch (Integer.parseInt(pcfResponse[0].getParameterValue(
					MQConstants.MQIA_Q_TYPE).toString())) {
			case MQConstants.MQQT_LOCAL:
				sb.append("DEFINE QLOCAL("
						+ pcfResponse[0]
								.getParameterValue(MQConstants.MQCA_Q_NAME)
								.toString().trim() + ") + \n");
				break;
			case MQConstants.MQQT_ALIAS:
				sb.append("DEFINE QALIAS("
						+ pcfResponse[0]
								.getParameterValue(MQConstants.MQCA_Q_NAME)
								.toString().trim() + ") + \n");
				sb.append("TARGET('"
						+ pcfResponse[0]
								.getParameterValue(MQConstants.MQCA_BASE_Q_NAME)
								.toString().trim() + "') + \n");

				switch (Integer.parseInt(pcfResponse[0].getParameterValue(
						MQConstants.MQIA_BASE_TYPE).toString())) {
				case MQConstants.MQOT_Q:
					sb.append("TARGTYPE(QUEUE) + \n");
					break;
				case MQConstants.MQOT_TOPIC:
					sb.append("TARGTYPE(TOPIC) + \n");
					break;
				}

				break;
			case MQConstants.MQQT_REMOTE:
				sb.append("DEFINE QREMOTE("
						+ pcfResponse[0]
								.getParameterValue(MQConstants.MQCA_Q_NAME)
								.toString().trim() + ") + \n");
				sb.append("RNAME('"
						+ pcfResponse[0]
								.getParameterValue(
										MQConstants.MQCA_REMOTE_Q_NAME)
								.toString().trim() + "') + \n");
				sb.append("RQMNAME('"
						+ pcfResponse[0]
								.getParameterValue(
										MQConstants.MQCA_REMOTE_Q_MGR_NAME)
								.toString().trim() + "') + \n");
				sb.append("XMITQ'"
						+ pcfResponse[0]
								.getParameterValue(MQConstants.MQCA_XMIT_Q_NAME)
								.toString().trim() + "') + \n");
				break;
			case MQConstants.MQQT_CLUSTER:
				sb.append("DEFINE QLOCAL("
						+ pcfResponse[0]
								.getParameterValue(MQConstants.MQCA_Q_NAME)
								.toString().trim() + ") + \n");
				break;
			case MQConstants.MQQT_MODEL:
				sb.append("DEFINE QMODEL("
						+ pcfResponse[0]
								.getParameterValue(MQConstants.MQCA_Q_NAME)
								.toString().trim() + ") + \n");
				break;
			}

			if (Integer.parseInt(pcfResponse[0].getParameterValue(
					MQConstants.MQIA_Q_TYPE).toString()) == MQConstants.MQQT_LOCAL
					|| Integer.parseInt(pcfResponse[0].getParameterValue(
							MQConstants.MQIA_Q_TYPE).toString()) == MQConstants.MQQT_CLUSTER
					|| Integer.parseInt(pcfResponse[0].getParameterValue(
							MQConstants.MQIA_Q_TYPE).toString()) == MQConstants.MQQT_MODEL) {
				switch (Integer.parseInt(pcfResponse[0].getParameterValue(
						MQConstants.MQIA_ACCOUNTING_Q).toString())) {
				case MQConstants.MQMON_NONE:
					sb.append("ACCTQ('QMGR') + \n");
					break;
				case MQConstants.MQMON_OFF:
					sb.append("ACCTQ('OFF') + \n");
					break;
				case MQConstants.MQMON_ON:
					sb.append("ACCTQ('ON') + \n");
					break;
				}
				sb.append("BOQNAME('"
						+ pcfResponse[0]
								.getParameterValue(
										MQConstants.MQCA_BACKOUT_REQ_Q_NAME)
								.toString().trim() + "') + \n");
				sb.append("BOTHRESH("
						+ pcfResponse[0]
								.getParameterValue(
										MQConstants.MQIA_BACKOUT_THRESHOLD)
								.toString().trim() + ") + \n");
				sb.append("CLCHNAME('"
						+ pcfResponse[0]
								.getParameterValue(
										MQConstants.MQCA_CLUS_CHL_NAME)
								.toString().trim() + "') + \n");

				switch (Integer.parseInt(pcfResponse[0].getParameterValue(
						MQConstants.MQIA_DEF_INPUT_OPEN_OPTION).toString())) {
				case MQConstants.MQOO_INPUT_EXCLUSIVE:
					sb.append("DEFSOPT(EXCL) + \n");
					break;
				case MQConstants.MQOO_INPUT_SHARED:
					sb.append("DEFSOPT(SHARED) + \n");
					break;
				}

				switch (Integer.parseInt(pcfResponse[0].getParameterValue(
						MQConstants.MQIA_DIST_LISTS).toString())) {
				case MQConstants.MQDL_SUPPORTED:
					sb.append("DISTL(YES) + \n");
					break;
				case MQConstants.MQDL_NOT_SUPPORTED:
					sb.append("DISTL(NO) + \n");
					break;
				}

				switch (Integer.parseInt(pcfResponse[0].getParameterValue(
						MQConstants.MQIA_HARDEN_GET_BACKOUT).toString())) {
				case MQConstants.MQQA_BACKOUT_HARDENED:
					sb.append("HARDENBO + \n");
					break;
				case MQConstants.MQQA_BACKOUT_NOT_HARDENED:
					sb.append("NOHARDENBO + \n");
					break;
				}

				sb.append("INITQ('"
						+ pcfResponse[0]
								.getParameterValue(
										MQConstants.MQCA_INITIATION_Q_NAME)
								.toString().trim() + "') + \n");

				switch (Integer.parseInt(pcfResponse[0].getParameterValue(
						MQConstants.MQIA_MONITORING_Q).toString())) {
				case MQConstants.MQMON_Q_MGR:
					sb.append("MONQ(QMGR) + \n");
					break;
				case MQConstants.MQMON_OFF:
					sb.append("MONQ(OFF) + \n");
					break;
				case MQConstants.MQMON_LOW:
					sb.append("MONQ(LOW) + \n");
					break;
				case MQConstants.MQMON_MEDIUM:
					sb.append("MONQ(MEDIUM) + \n");
					break;
				}

				switch (Integer.parseInt(pcfResponse[0].getParameterValue(
						MQConstants.MQIA_MSG_DELIVERY_SEQUENCE).toString())) {
				case MQConstants.MQMDS_FIFO:
					sb.append("MSGDLVSQ(FIFO) + \n");
					break;
				case MQConstants.MQMDS_PRIORITY:
					sb.append("MSGDLVSQ(PRIORITY) + \n");
					break;
				}

				sb.append("MAXDEPTH("
						+ pcfResponse[0]
								.getParameterValue(MQConstants.MQIA_MAX_Q_DEPTH)
								.toString().trim() + ") + \n");
				sb.append("MAXMSGL("
						+ pcfResponse[0]
								.getParameterValue(
										MQConstants.MQIA_MAX_MSG_LENGTH)
								.toString().trim() + ") + \n");
				sb.append("PROCESS('"
						+ pcfResponse[0]
								.getParameterValue(
										MQConstants.MQCA_PROCESS_NAME)
								.toString().trim() + "') + \n");

				switch (Integer.parseInt(pcfResponse[0].getParameterValue(
						MQConstants.MQIA_Q_DEPTH_LOW_EVENT).toString())) {
				case MQConstants.MQEVR_DISABLED:
					sb.append("QDPLOEV(DISABLED) + \n");
					break;
				case MQConstants.MQEVR_ENABLED:
					sb.append("QDPLOEV(ENABLED) + \n");
					break;
				}

				switch (Integer.parseInt(pcfResponse[0].getParameterValue(
						MQConstants.MQIA_Q_DEPTH_HIGH_EVENT).toString())) {
				case MQConstants.MQEVR_DISABLED:
					sb.append("QDPHIEV(DISABLED) + \n");
					break;
				case MQConstants.MQEVR_ENABLED:
					sb.append("QDPHIEV(ENABLED) + \n");
					break;
				}

				switch (Integer.parseInt(pcfResponse[0].getParameterValue(
						MQConstants.MQIA_Q_DEPTH_MAX_EVENT).toString())) {
				case MQConstants.MQEVR_DISABLED:
					sb.append("QDPMAXEV(DISABLED) + \n");
					break;
				case MQConstants.MQEVR_ENABLED:
					sb.append("QDPMAXEV(ENABLED) + \n");
					break;
				}

				switch (Integer.parseInt(pcfResponse[0].getParameterValue(
						MQConstants.MQIA_Q_SERVICE_INTERVAL_EVENT).toString())) {
				case MQConstants.MQQSIE_NONE:
					sb.append("QSVCIEV(NONE) + \n");
					break;
				case MQConstants.MQQSIE_HIGH:
					sb.append("QSVCIEV(HIGH) + \n");
					break;
				case MQConstants.MQQSIE_OK:
					sb.append("QSVCIEV(OK) + \n");
					break;
				}

				switch (Integer.parseInt(pcfResponse[0].getParameterValue(
						MQConstants.MQIA_TRIGGER_TYPE).toString())) {
				case MQConstants.MQMON_NONE:
					sb.append("STATQ(QMGR) + \n");
					break;
				case MQConstants.MQMON_OFF:
					sb.append("STATQ(OFF) + \n");
					break;
				case MQConstants.MQMON_ON:
					sb.append("STATQ(ON) + \n");
					break;
				}

				switch (Integer.parseInt(pcfResponse[0].getParameterValue(
						MQConstants.MQIA_TRIGGER_TYPE).toString())) {
				case MQConstants.MQTT_NONE:
					sb.append("TRIGTYPE(NONE) + \n");
					break;
				case MQConstants.MQTT_FIRST:
					sb.append("TRIGTYPE(FIRST) + \n");
					break;
				case MQConstants.MQTT_EVERY:
					sb.append("TRIGTYPE(EVERY) + \n");
					break;
				case MQConstants.MQTT_DEPTH:
					sb.append("TRIGTYPE(DEPTH) + \n");
					break;
				}

				sb.append("TRIGDPTH("
						+ pcfResponse[0]
								.getParameterValue(
										MQConstants.MQIA_TRIGGER_DEPTH)
								.toString().trim() + ") + \n");
				sb.append("TRIGMPRI("
						+ pcfResponse[0]
								.getParameterValue(
										MQConstants.MQIA_TRIGGER_MSG_PRIORITY)
								.toString().trim() + ") + \n");
				sb.append("TRIGDATA('"
						+ pcfResponse[0]
								.getParameterValue(
										MQConstants.MQCA_TRIGGER_DATA)
								.toString().trim() + "') + \n");

				switch (Integer.parseInt(pcfResponse[0].getParameterValue(
						MQConstants.MQIA_USAGE).toString())) {
				case MQConstants.MQUS_NORMAL:
					sb.append("USAGE(NORMAL) + \n");
					break;
				case MQConstants.MQUS_TRANSMISSION:
					sb.append("USAGE(XMITQ) + \n");
					break;
				}

				sb.append("QSVCINT("
						+ pcfResponse[0]
								.getParameterValue(
										MQConstants.MQIA_Q_SERVICE_INTERVAL)
								.toString().trim() + ") + \n");
				sb.append("RETINTVL("
						+ pcfResponse[0]
								.getParameterValue(
										MQConstants.MQIA_RETENTION_INTERVAL)
								.toString().trim() + ") + \n");
				sb.append("QDEPTHHI("
						+ pcfResponse[0]
								.getParameterValue(
										MQConstants.MQIA_Q_DEPTH_HIGH_LIMIT)
								.toString().trim() + ") + \n");
				sb.append("QDEPTHLO("
						+ pcfResponse[0]
								.getParameterValue(
										MQConstants.MQIA_Q_DEPTH_LOW_LIMIT)
								.toString().trim() + ") + \n");

				switch (Integer.parseInt(pcfResponse[0].getParameterValue(
						MQConstants.MQIA_NPM_CLASS).toString())) {
				case MQConstants.MQNPM_CLASS_NORMAL:
					sb.append("NPMCLASS(NORMAL) + \n");
					break;
				case MQConstants.MQNPM_CLASS_HIGH:
					sb.append("NPMCLASS(HIGH) + \n");
					break;
				}

				switch (Integer.parseInt(pcfResponse[0].getParameterValue(
						MQConstants.MQIA_TRIGGER_CONTROL).toString())) {
				case MQConstants.MQTC_ON:
					sb.append("TRIGGER + \n");
					break;
				case MQConstants.MQTC_OFF:
					sb.append("NOTRIGGER + \n");
					break;
				}

				switch (Integer.parseInt(pcfResponse[0].getParameterValue(
						MQConstants.MQIA_DEFINITION_TYPE).toString())) {
				case MQConstants.MQQDT_PERMANENT_DYNAMIC:
					sb.append("DEFTYPE(PERMDYN) + \n");
					break;
				case MQConstants.MQQDT_TEMPORARY_DYNAMIC:
					sb.append("DEFTYPE(TEMPDYN) + \n");
					break;
				case MQConstants.MQQDT_SHARED_DYNAMIC:
					sb.append("DEFTYPE(SHAREDYN) + \n");
					break;
				}

				switch (Integer.parseInt(pcfResponse[0].getParameterValue(
						MQConstants.MQIA_SHAREABILITY).toString())) {
				case MQConstants.MQQA_SHAREABLE:
					sb.append("SHARE + \n");
					break;
				case MQConstants.MQQA_NOT_SHAREABLE:
					sb.append("NOSHARE + \n");
					break;
				}

			}

			if (!(Integer.parseInt(pcfResponse[0].getParameterValue(
					MQConstants.MQIA_Q_TYPE).toString()) == MQConstants.MQQT_MODEL)) {
				switch (Integer.parseInt(pcfResponse[0].getParameterValue(
						MQConstants.MQIA_SCOPE).toString())) {
				case MQConstants.MQSCO_Q_MGR:
					sb.append("SCOPE(QMGR) + \n");
					break;
				case MQConstants.MQSCO_CELL:
					sb.append("SCOPE(CELL) + \n");
					break;
				}
			}
			if (Integer.parseInt(pcfResponse[0].getParameterValue(
					MQConstants.MQIA_Q_TYPE).toString()) == MQConstants.MQQT_LOCAL
					|| Integer.parseInt(pcfResponse[0].getParameterValue(
							MQConstants.MQIA_Q_TYPE).toString()) == MQConstants.MQQT_CLUSTER
					|| Integer.parseInt(pcfResponse[0].getParameterValue(
							MQConstants.MQIA_Q_TYPE).toString()) == MQConstants.MQQT_ALIAS
					|| Integer.parseInt(pcfResponse[0].getParameterValue(
							MQConstants.MQIA_Q_TYPE).toString()) == MQConstants.MQQT_REMOTE) {
				sb.append("CLUSNL('"
						+ pcfResponse[0]
								.getParameterValue(
										MQConstants.MQCA_CLUSTER_NAMELIST)
								.toString().trim() + "') + \n");
				sb.append("CLUSTER('"
						+ pcfResponse[0]
								.getParameterValue(
										MQConstants.MQCA_CLUSTER_NAME)
								.toString().trim() + "') + \n");
				sb.append("CLWLPRTY("
						+ pcfResponse[0]
								.getParameterValue(
										MQConstants.MQIA_CLWL_Q_PRIORITY)
								.toString().trim() + ") + \n");
				sb.append("CLWLRANK("
						+ pcfResponse[0]
								.getParameterValue(MQConstants.MQIA_CLWL_Q_RANK)
								.toString().trim() + ") + \n");

				switch (Integer.parseInt(pcfResponse[0].getParameterValue(
						MQConstants.MQIA_DEF_BIND).toString())) {
				case MQConstants.MQBND_BIND_ON_OPEN:
					sb.append("DEFBIND(OPEN) + \n");
					break;
				case MQConstants.MQBND_BIND_NOT_FIXED:
					sb.append("DEFBIND(NOTFIXED) + \n");
					break;
				case MQConstants.MQBND_BIND_ON_GROUP:
					sb.append("DEFBIND(GROUP) + \n");
					break;
				}

			}

			if (Integer.parseInt(pcfResponse[0].getParameterValue(
					MQConstants.MQIA_Q_TYPE).toString()) == MQConstants.MQQT_LOCAL
					|| Integer.parseInt(pcfResponse[0].getParameterValue(
							MQConstants.MQIA_Q_TYPE).toString()) == MQConstants.MQQT_CLUSTER) {

				switch (Integer.parseInt(pcfResponse[0].getParameterValue(
						MQConstants.MQIA_CLWL_USEQ).toString())) {
				case MQConstants.MQCLWL_USEQ_ANY:
					sb.append("CLWLUSEQ(ANY) + \n");
					break;
				case MQConstants.MQCLWL_USEQ_LOCAL:
					sb.append("CLWLUSEQ(LOCAL) + \n");
					break;
				}
			}
			sb.append("CUSTOM('"
					+ pcfResponse[0].getParameterValue(MQConstants.MQCA_CUSTOM)
							.toString().trim() + "') + \n");

			switch (Integer.parseInt(pcfResponse[0].getParameterValue(
					MQConstants.MQIA_DEF_PUT_RESPONSE_TYPE).toString())) {
			case MQConstants.MQPRT_SYNC_RESPONSE:
				sb.append("DEFPRESP(SYNC) + \n");
				break;
			case MQConstants.MQPRT_ASYNC_RESPONSE:
				sb.append("DEFPRESP(ASYNC) + \n");
				break;
			}

			sb.append("DEFPRTY("
					+ pcfResponse[0]
							.getParameterValue(MQConstants.MQIA_DEF_PRIORITY)
							.toString().trim() + ") + \n");

			switch (Integer.parseInt(pcfResponse[0].getParameterValue(
					MQConstants.MQIA_DEF_PERSISTENCE).toString())) {
			case MQConstants.MQPER_PERSISTENT:
				sb.append("DEFPSIST(YES) + \n");
				break;
			case MQConstants.MQPER_NOT_PERSISTENT:
				sb.append("DEFPSIST(NO) + \n");
				break;
			}

			if ((Integer.parseInt(pcfResponse[0].getParameterValue(
					MQConstants.MQIA_Q_TYPE).toString()) != MQConstants.MQQT_REMOTE)) {
				switch (Integer.parseInt(pcfResponse[0].getParameterValue(
						MQConstants.MQIA_DEF_READ_AHEAD).toString())) {
				case MQConstants.MQREADA_NO:
					sb.append("DEFREADA(NO) + \n");
					break;
				case MQConstants.MQREADA_YES:
					sb.append("DEFREADA(YES) + \n");
					break;
				case MQConstants.MQREADA_DISABLED:
					sb.append("DEFREADA(DISABLED) + \n");
					break;
				}

				switch (Integer.parseInt(pcfResponse[0].getParameterValue(
						MQConstants.MQIA_INHIBIT_GET).toString())) {
				case MQConstants.MQQA_GET_INHIBITED:
					sb.append("GET(DISABLED) + \n");
					break;
				case MQConstants.MQQA_GET_ALLOWED:
					sb.append("GET(ENABLED) + \n");
					break;
				}

				switch (Integer.parseInt(pcfResponse[0].getParameterValue(
						MQConstants.MQIA_PROPERTY_CONTROL).toString())) {
				case MQConstants.MQPROP_ALL:
					sb.append("PROPCTL(ALL) + \n");
					break;
				case MQConstants.MQPROP_COMPATIBILITY:
					sb.append("PROPCTL(COMPAT) + \n");
					break;
				case MQConstants.MQPROP_FORCE_MQRFH2:
					sb.append("PROPCTL(FORCE) + \n");
					break;
				case MQConstants.MQPROP_NONE:
					sb.append("PROPCTL(NONE) + \n");
					break;
				}
			}

			switch (Integer.parseInt(pcfResponse[0].getParameterValue(
					MQConstants.MQIA_INHIBIT_PUT).toString())) {
			case MQConstants.MQQA_PUT_INHIBITED:
				sb.append("PUT(DISABLED) + \n");
				break;
			case MQConstants.MQQA_PUT_ALLOWED:
				sb.append("PUT(DISABLED) + \n");
				break;
			}

			sb.append("DESCR('"
					+ pcfResponse[0].getParameterValue(MQConstants.MQCA_Q_DESC)
							.toString().trim() + "')  \n");
			agent.disconnect();

			sb.append(" \n");
		}
		return sb;
	}

	public StringBuffer createChlScript(String qmgrHost, int qmgrPort,
			String chlName, String qChannel) throws MQDataException, IOException {

		PCFCommons pcfCM = new PCFCommons();
		PCFMessageAgent agent = new PCFMessageAgent(qmgrHost, qmgrPort,
				qChannel);

		PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_INQUIRE_CHANNEL);
		pcfCmd.addParameter(MQConstants.MQCACH_CHANNEL_NAME, chlName);

		// Execute the command. The returned object is an array of PCF messages.
		PCFMessage[] pcfResponse = agent.send(pcfCmd);

		StringBuffer sb = new StringBuffer();

		if (pcfResponse[0].getParameterValue(MQConstants.MQCACH_CHANNEL_NAME)
				.toString().indexOf("SYSTEM") != 0) {
			sb.append("DEFINE CHANNEL('"
					+ pcfResponse[0]
							.getParameterValue(MQConstants.MQCACH_CHANNEL_NAME)
							.toString().trim() + "') + \n");
			switch (Integer.parseInt(pcfResponse[0].getParameterValue(
					MQConstants.MQIACH_CHANNEL_TYPE).toString())) {
			case MQConstants.MQCHT_SENDER:
				sb.append("CHLTYPE (SDR) + \n");
				sb.append("CONNAME('"
						+ pcfResponse[0]
								.getParameterValue(
										MQConstants.MQCACH_CONNECTION_NAME)
								.toString().trim() + "') + \n");
				sb.append("XMITQ('"
						+ pcfResponse[0]
								.getParameterValue(
										MQConstants.MQCACH_XMIT_Q_NAME)
								.toString().trim() + "') + \n");
				break;
			case MQConstants.MQCHT_SERVER:
				sb.append("CHLTYPE (SVR) + \n");
				sb.append("CONNAME('"
						+ pcfResponse[0]
								.getParameterValue(
										MQConstants.MQCACH_CONNECTION_NAME)
								.toString().trim() + "') + \n");
				sb.append("XMITQ('"
						+ pcfResponse[0]
								.getParameterValue(
										MQConstants.MQCACH_XMIT_Q_NAME)
								.toString().trim() + "') + \n");
				break;
			case MQConstants.MQCHT_RECEIVER:
				sb.append("CHLTYPE (RCVR) + \n");
				break;
			case MQConstants.MQCHT_REQUESTER:
				sb.append("CHLTYPE (RQSTR) + \n");
				sb.append("CONNAME('"
						+ pcfResponse[0]
								.getParameterValue(
										MQConstants.MQCACH_CONNECTION_NAME)
								.toString().trim() + "') + \n");
				break;
			case MQConstants.MQCHT_SVRCONN:
				sb.append("CHLTYPE (SVRCONN) + \n");
				if (pcfResponse[0]
						.getParameterValue(MQConstants.MQIACH_MAX_INSTANCES) != null) {
					sb.append("MAXINST("
							+ pcfResponse[0]
									.getParameterValue(MQConstants.MQIACH_MAX_INSTANCES)
							+ ") + \n");
				}
				if (pcfResponse[0]
						.getParameterValue(MQConstants.MQIACH_MAX_INSTS_PER_CLIENT) != null) {
					sb.append("MAXINSTC("
							+ pcfResponse[0]
									.getParameterValue(MQConstants.MQIACH_MAX_INSTS_PER_CLIENT)
							+ ") + \n");
				}
				break;
			case MQConstants.MQCHT_CLNTCONN:
				sb.append("CHLTYPE (CLNTCONN) + \n");
				break;
			case MQConstants.MQCHT_CLUSRCVR:
				sb.append("CHLTYPE (CLUSRCVR) + \n");
				sb.append("CONNAME('"
						+ pcfResponse[0]
								.getParameterValue(
										MQConstants.MQCACH_CONNECTION_NAME)
								.toString().trim() + "') + \n");
				break;
			case MQConstants.MQCHT_CLUSSDR:
				sb.append("CHLTYPE (CLUSSDR) + \n");
				sb.append("CONNAME('"
						+ pcfResponse[0]
								.getParameterValue(
										MQConstants.MQCACH_CONNECTION_NAME)
								.toString().trim() + "') + \n");
				break;
			}

			switch (Integer.parseInt(pcfResponse[0].getParameterValue(
					MQConstants.MQIACH_KEEP_ALIVE_INTERVAL).toString())) {
			case MQConstants.MQKAI_AUTO:
				sb.append("KAINT(AUTO) + \n");
				break;
			}

			if (pcfResponse[0].getParameterValue(MQConstants.MQIACH_BATCH_HB) != null) {
				sb.append("BATCHHB("
						+ pcfResponse[0]
								.getParameterValue(MQConstants.MQIACH_BATCH_HB)
						+ ") + \n");
			}
			if (pcfResponse[0]
					.getParameterValue(MQConstants.MQIACH_BATCH_INTERVAL) != null) {
				sb.append("BATCHINT("
						+ pcfResponse[0]
								.getParameterValue(MQConstants.MQIACH_BATCH_INTERVAL)
						+ ") + \n");
			}
			if (pcfResponse[0]
					.getParameterValue(MQConstants.MQIACH_BATCH_DATA_LIMIT) != null) {
				sb.append("BATCHLIM("
						+ pcfResponse[0]
								.getParameterValue(MQConstants.MQIACH_BATCH_DATA_LIMIT)
						+ ") + \n");
			}
			if (pcfResponse[0].getParameterValue(MQConstants.MQIACH_BATCH_SIZE) != null) {
				sb.append("BATCHSZ("
						+ pcfResponse[0]
								.getParameterValue(MQConstants.MQIACH_BATCH_SIZE)
						+ ") + \n");
			}

			int lenHDRComp = Array.getLength(pcfResponse[0]
					.getParameterValue(MQConstants.MQIACH_HDR_COMPRESSION));
			for (int hdrCtr = 0; hdrCtr < lenHDRComp; hdrCtr++) {
				switch (Integer
						.parseInt(Array
								.get(pcfResponse[0]
										.getParameterValue(MQConstants.MQIACH_HDR_COMPRESSION),
										hdrCtr).toString())) {
				case MQConstants.MQCOMPRESS_NONE:
					sb.append("COMPHDR (NONE) + \n");
					break;
				case MQConstants.MQCOMPRESS_SYSTEM:
					sb.append("COMPHDR (SYSTEM) + \n");
					break;
				}
			}

			int lenMSGComp = Array.getLength(pcfResponse[0]
					.getParameterValue(MQConstants.MQIACH_MSG_COMPRESSION));
			for (int msgCtr = 0; msgCtr < lenMSGComp; msgCtr++) {
				switch (Integer
						.parseInt(Array
								.get(pcfResponse[0]
										.getParameterValue(MQConstants.MQIACH_MSG_COMPRESSION),
										msgCtr).toString())) {
				case MQConstants.MQCOMPRESS_NONE:
					sb.append("COMPMSG (NONE) + \n");
					break;
				case MQConstants.MQCOMPRESS_RLE:
					sb.append("COMPMSG (RLE) + \n");
					break;
				case MQConstants.MQCOMPRESS_ZLIBFAST:
					sb.append("COMPMSG (ZLIBFAST) + \n");
					break;
				case MQConstants.MQCOMPRESS_ZLIBHIGH:
					sb.append("COMPMSG (ZLIBHIGH) + \n");
					break;
				}
			}

			if (pcfResponse[0]
					.getParameterValue(MQConstants.MQIACH_DATA_CONVERSION) != null) {
				switch (Integer.parseInt(pcfResponse[0].getParameterValue(
						MQConstants.MQIACH_DATA_CONVERSION).toString())) {
				case MQConstants.MQCDC_SENDER_CONVERSION:
					sb.append("CONVERT(YES) + \n");
					break;
				case MQConstants.MQCDC_NO_SENDER_CONVERSION:
					sb.append("CONVERT(NO) + \n");
					break;
				}
			}

			sb.append("DESCR('"
					+ pcfResponse[0].getParameterValue(MQConstants.MQCACH_DESC)
							.toString().trim() + "') + \n");

			if (pcfResponse[0]
					.getParameterValue(MQConstants.MQIACH_DISC_INTERVAL) != null) {
				sb.append("DISCINT("
						+ Integer.parseInt(pcfResponse[0].getParameterValue(
								MQConstants.MQIACH_DISC_INTERVAL).toString())
						+ ") + \n");
			}

			sb.append("HBINT("
					+ pcfResponse[0]
							.getParameterValue(MQConstants.MQIACH_HB_INTERVAL)
					+ ") + \n");

			if (pcfResponse[0]
					.getParameterValue(MQConstants.MQCACH_LOCAL_ADDRESS) != null) {
				sb.append("LOCLADDR('"
						+ pcfResponse[0]
								.getParameterValue(
										MQConstants.MQCACH_LOCAL_ADDRESS)
								.toString().trim() + "') + \n");
			}
			if (pcfResponse[0].getParameterValue(MQConstants.MQIACH_LONG_RETRY) != null) {
				sb.append("LONGRTY("
						+ pcfResponse[0]
								.getParameterValue(MQConstants.MQIACH_LONG_RETRY)
						+ ") + \n");
			}
			if (pcfResponse[0].getParameterValue(MQConstants.MQIACH_LONG_TIMER) != null) {
				sb.append("LONGTMR("
						+ pcfResponse[0]
								.getParameterValue(MQConstants.MQIACH_LONG_TIMER)
						+ ") + \n");
			}

			if (pcfResponse[0].getParameterValue(MQConstants.MQIACH_MCA_TYPE) != null) {
				switch (Integer.parseInt(pcfResponse[0].getParameterValue(
						MQConstants.MQIACH_MCA_TYPE).toString())) {
				case MQConstants.MQMCAT_PROCESS:
					sb.append("MCATYPE(PROCESS) + \n");
					break;
				case MQConstants.MQMCAT_THREAD:
					sb.append("MCATYPE(THREAD) + \n");
					break;
				}
			}

			if (pcfResponse[0].getParameterValue(MQConstants.MQCACH_MODE_NAME) != null) {
				sb.append("MODENAME('"
						+ pcfResponse[0]
								.getParameterValue(MQConstants.MQCACH_MODE_NAME)
								.toString().trim() + "') + \n");
			}

			sb.append("MAXMSGL("
					+ pcfResponse[0]
							.getParameterValue(MQConstants.MQIACH_MAX_MSG_LENGTH)
					+ ") + \n");

			if (!(Integer.parseInt(pcfResponse[0].getParameterValue(
					MQConstants.MQIACH_CHANNEL_TYPE).toString()) == MQConstants.MQCHT_CLNTCONN)) {
				sb.append("MCAUSER('"
						+ pcfResponse[0]
								.getParameterValue(
										MQConstants.MQCACH_MCA_USER_ID)
								.toString().trim() + "') + \n");

				switch (Integer.parseInt(pcfResponse[0].getParameterValue(
						MQConstants.MQIA_MONITORING_CHANNEL).toString())) {
				case MQConstants.MQMON_NONE:
					sb.append("MONCHL(NONE) + \n");
					break;
				case MQConstants.MQMON_OFF:
					sb.append("MONCHL(OFF) + \n");
					break;
				case MQConstants.MQMON_LOW:
					sb.append("MONCHL(LOW) + \n");
					break;
				case MQConstants.MQMON_MEDIUM:
					sb.append("MONCHL(MEDIUM) + \n");
					break;
				case MQConstants.MQMON_HIGH:
					sb.append("MONCHL(HIGH) + \n");
					break;
				}
			}

			if (pcfResponse[0]
					.getParameterValue(MQConstants.MQCACH_MSG_EXIT_USER_DATA) != null) {
				sb.append("MSGDATA('"
						+ pcfResponse[0]
								.getParameterValue(
										MQConstants.MQCACH_MSG_EXIT_USER_DATA)
								.toString().trim() + "') + \n");
				sb.append("MSGEXIT('"
						+ pcfResponse[0]
								.getParameterValue(
										MQConstants.MQCACH_MSG_EXIT_NAME)
								.toString().trim() + "') + \n");

				switch (Integer.parseInt(pcfResponse[0].getParameterValue(
						MQConstants.MQIACH_NPM_SPEED).toString())) {
				case MQConstants.MQNPMS_NORMAL:
					sb.append("NPMSPEED(NORMAL) + \n");
					break;
				case MQConstants.MQNPMS_FAST:
					sb.append("NPMSPEED(FAST) + \n");
					break;
				}

			}

			if (pcfResponse[0].getParameterValue(MQConstants.MQCACH_PASSWORD) != null) {
				sb.append("PASSWORD('"
						+ pcfResponse[0]
								.getParameterValue(MQConstants.MQCACH_PASSWORD)
								.toString().trim() + "') + \n");
			}

			if (pcfResponse[0]
					.getParameterValue(MQConstants.MQIA_PROPERTY_CONTROL) != null) {
				switch (Integer.parseInt(pcfResponse[0].getParameterValue(
						MQConstants.MQIA_PROPERTY_CONTROL).toString())) {
				case MQConstants.MQPROP_ALL:
					sb.append("PROPCTL(ALL) + \n");
					break;
				case MQConstants.MQPROP_COMPATIBILITY:
					sb.append("PROPCTL(COMPAT) + \n");
					break;
				case MQConstants.MQPROP_NONE:
					sb.append("PROPCTL(NONE) + \n");
					break;
				}
			}

			sb.append("RCVDATA('"
					+ pcfResponse[0]
							.getParameterValue(
									MQConstants.MQCACH_RCV_EXIT_USER_DATA)
							.toString().trim() + "') + \n");
			sb.append("RCVEXIT('"
					+ pcfResponse[0]
							.getParameterValue(MQConstants.MQCACH_RCV_EXIT_NAME)
							.toString().trim() + "') + \n");
			sb.append("SCYDATA('"
					+ pcfResponse[0]
							.getParameterValue(
									MQConstants.MQCACH_SEC_EXIT_USER_DATA)
							.toString().trim() + "') + \n");
			sb.append("SCYEXIT('"
					+ pcfResponse[0]
							.getParameterValue(MQConstants.MQCACH_SEC_EXIT_NAME)
							.toString().trim() + "') + \n");
			sb.append("SENDDATA('"
					+ pcfResponse[0]
							.getParameterValue(
									MQConstants.MQCACH_SEND_EXIT_USER_DATA)
							.toString().trim() + "') + \n");
			sb.append("SENDEXIT('"
					+ pcfResponse[0]
							.getParameterValue(
									MQConstants.MQCACH_SEND_EXIT_NAME)
							.toString().trim() + "') + \n");

			if (pcfResponse[0]
					.getParameterValue(MQConstants.MQIACH_SEQUENCE_NUMBER_WRAP) != null) {
				sb.append("SEQWRAP("
						+ pcfResponse[0]
								.getParameterValue(
										MQConstants.MQIACH_SEQUENCE_NUMBER_WRAP)
								.toString().trim() + ") + \n");
			}
			if (pcfResponse[0]
					.getParameterValue(MQConstants.MQIACH_SHORT_RETRY) != null) {
				sb.append("SHORTRTY("
						+ pcfResponse[0]
								.getParameterValue(
										MQConstants.MQIACH_SHORT_RETRY)
								.toString().trim() + ") + \n");
			}
			if (pcfResponse[0]
					.getParameterValue(MQConstants.MQIACH_SHORT_TIMER) != null) {
				sb.append("SHORTTMR("
						+ pcfResponse[0]
								.getParameterValue(
										MQConstants.MQIACH_SHORT_TIMER)
								.toString().trim() + ") + \n");
			}

			if (pcfResponse[0]
					.getParameterValue(MQConstants.MQIACH_SHARING_CONVERSATIONS) != null) {
				sb.append("SHARECNV("
						+ pcfResponse[0]
								.getParameterValue(MQConstants.MQIACH_SHARING_CONVERSATIONS)
						+ ") + \n");
			}

			if (pcfResponse[0]
					.getParameterValue(MQConstants.MQIACH_SSL_CLIENT_AUTH) != null) {

				switch (Integer.parseInt(pcfResponse[0].getParameterValue(
						MQConstants.MQIACH_SSL_CLIENT_AUTH).toString())) {
				case MQConstants.MQSCA_REQUIRED:
					sb.append("SSLCAUTH(REQUIRED) + \n");
					break;
				case MQConstants.MQSCA_OPTIONAL:
					sb.append("SSLCAUTH(OPTIONAL) + \n");
					break;
				}
			}

			if (pcfResponse[0]
					.getParameterValue(MQConstants.MQIA_STATISTICS_CHANNEL) != null) {
				switch (Integer.parseInt(pcfResponse[0].getParameterValue(
						MQConstants.MQIA_STATISTICS_CHANNEL).toString())) {
				case MQConstants.MQMON_NONE:
					sb.append("TRPTYPE(QMGR)  \n");
					break;
				case MQConstants.MQMON_OFF:
					sb.append("TRPTYPE(OFF)  \n");
					break;
				case MQConstants.MQMON_LOW:
					sb.append("TRPTYPE(LOW)  \n");
					break;
				case MQConstants.MQMON_MEDIUM:
					sb.append("TRPTYPE(MEDIUM)  \n");
					break;
				case MQConstants.MQMON_HIGH:
					sb.append("TRPTYPE(HIGH)  \n");
					break;
				}

			}

			if (pcfResponse[0].getParameterValue(MQConstants.MQCACH_TP_NAME) != null) {
				sb.append("TPNAME('"
						+ pcfResponse[0]
								.getParameterValue(MQConstants.MQCACH_TP_NAME)
								.toString().trim() + "') + \n");
			}

			switch (Integer.parseInt(pcfResponse[0].getParameterValue(
					MQConstants.MQIACH_XMIT_PROTOCOL_TYPE).toString())) {
			case MQConstants.MQXPT_LU62:
				sb.append("TRPTYPE(LU62) + \n");
				break;
			case MQConstants.MQXPT_TCP:
				sb.append("TRPTYPE(TCP) + \n");
				break;
			case MQConstants.MQXPT_NETBIOS:
				sb.append("TRPTYPE(NETBIOS) + \n");
				break;
			case MQConstants.MQXPT_SPX:
				sb.append("TRPTYPE(SPX) + \n");
				break;
			}

			if (pcfResponse[0]
					.getParameterValue(MQConstants.MQIA_USE_DEAD_LETTER_Q) != null) {
				switch (Integer.parseInt(pcfResponse[0].getParameterValue(
						MQConstants.MQIA_USE_DEAD_LETTER_Q).toString())) {
				case MQConstants.MQUSEDLQ_YES:
					sb.append("USEDLQ(YES) + \n");
					break;
				case MQConstants.MQUSEDLQ_NO:
					sb.append("USEDLQ(NO) +  \n");
					break;
				}

			}

			if (pcfResponse[0].getParameterValue(MQConstants.MQCACH_USER_ID) != null) {

				sb.append("USERID('"
						+ pcfResponse[0]
								.getParameterValue(MQConstants.MQCACH_USER_ID)
								.toString().trim() + "') + \n");
			}

			sb.append("SSLCIPH('"
					+ pcfResponse[0]
							.getParameterValue(
									MQConstants.MQCACH_SSL_CIPHER_SPEC)
							.toString().trim() + "') + \n");
			sb.append("SSLPEER('"
					+ pcfResponse[0]
							.getParameterValue(MQConstants.MQCACH_SSL_PEER_NAME)
							.toString().trim() + "') \n");

			agent.disconnect();

			sb.append(" \n");
		}
		return sb;
	}

	public StringBuffer createListScript(String qmgrHost, int qmgrPort,
			String listName, String qChannel) throws MQDataException, IOException {
		PCFCommons pcfCM = new PCFCommons();

		PCFMessageAgent agent = new PCFMessageAgent(qmgrHost, qmgrPort,
				qChannel);

		PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_INQUIRE_LISTENER);
		pcfCmd.addParameter(MQConstants.MQCACH_LISTENER_NAME, listName);

		// Execute the command. The returned object is an array of PCF messages.
		PCFMessage[] pcfResponse = agent.send(pcfCmd);

		StringBuffer sb = new StringBuffer();

		if (pcfResponse[0].getParameterValue(MQConstants.MQCACH_LISTENER_NAME)
				.toString().indexOf("SYSTEM") != 0) {
			sb.append("DEFINE LISTENER('"
					+ pcfResponse[0]
							.getParameterValue(MQConstants.MQCACH_LISTENER_NAME)
							.toString().trim() + "') + \n");
			switch (Integer.parseInt(pcfResponse[0].getParameterValue(
					MQConstants.MQIACH_XMIT_PROTOCOL_TYPE).toString())) {
			case MQConstants.MQXPT_TCP:
				sb.append("TRPTYPE(TCP) + \n");
				sb.append("PORT("
						+ pcfResponse[0]
								.getParameterValue(MQConstants.MQIACH_PORT)
						+ ") + \n");
				sb.append("IPADDR('"
						+ pcfResponse[0]
								.getParameterValue(
										MQConstants.MQCACH_IP_ADDRESS)
								.toString().trim() + "') + \n");
				break;
			case MQConstants.MQXPT_LU62:
				sb.append("TRPTYPE(LU62) + \n");
				break;
			case MQConstants.MQXPT_NETBIOS:
				sb.append("TRPTYPE(NETBIOS) + \n");
				sb.append("LOCLNAME('"
						+ pcfResponse[0]
								.getParameterValue(
										MQConstants.MQCACH_LOCAL_NAME)
								.toString().trim() + "') + \n");
				sb.append("ADAPTER("
						+ pcfResponse[0].getParameterValue(
								MQConstants.MQIACH_ADAPTER).toString()
						+ ") + \n");
				break;
			case MQConstants.MQXPT_SPX:
				sb.append("TRPTYPE(SPX) + \n");
				break;
			}
			switch (Integer.parseInt(pcfResponse[0].getParameterValue(
					MQConstants.MQIACH_LISTENER_CONTROL).toString())) {
			case MQConstants.MQSVC_CONTROL_MANUAL:
				sb.append("CONTROL(MANUAL) + \n");
				break;
			case MQConstants.MQSVC_CONTROL_Q_MGR:
				sb.append("CONTROL(QMGR) + \n");
				break;
			case MQConstants.MQSVC_CONTROL_Q_MGR_START:
				sb.append("CONTROL(STARTONLY) + \n");
				break;
			}
			if (pcfResponse[0].getParameterValue(MQConstants.MQIACH_BACKLOG) != null) {
				sb.append("BACKLOG("
						+ pcfResponse[0]
								.getParameterValue(MQConstants.MQIACH_BACKLOG)
						+ ") + \n");
			}
			if (pcfResponse[0]
					.getParameterValue(MQConstants.MQCACH_LISTENER_DESC) != null) {
				sb.append("DESCR('"
						+ pcfResponse[0]
								.getParameterValue(
										MQConstants.MQCACH_LISTENER_DESC)
								.toString().trim() + "')  \n");
			}

			agent.disconnect();

			sb.append(" \n");

		}

		return sb;

	}

	public StringBuffer createTopicScript(String qmgrHost, int qmgrPort,
			String topicName, String qChannel) throws MQDataException, IOException {
		PCFCommons pcfCM = new PCFCommons();

		PCFMessageAgent agent = new PCFMessageAgent(qmgrHost, qmgrPort,
				qChannel);

		PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_INQUIRE_TOPIC);
		pcfCmd.addParameter(MQConstants.MQCA_TOPIC_NAME, topicName);

		// Execute the command. The returned object is an array of PCF messages.
		PCFMessage[] pcfResponse = agent.send(pcfCmd);

		StringBuffer sb = new StringBuffer();

		if (pcfResponse[0].getParameterValue(MQConstants.MQCA_TOPIC_NAME)
				.toString().indexOf("SYSTEM") != 0) {
			sb.append("DEFINE TOPIC('"
					+ pcfResponse[0]
							.getParameterValue(MQConstants.MQCA_TOPIC_NAME)
							.toString().trim() + "') + \n");
			sb.append("TOPICSTR('"
					+ pcfResponse[0]
							.getParameterValue(MQConstants.MQCA_TOPIC_STRING)
							.toString().trim() + "') + \n");
			sb.append("CLUSTER('"
					+ pcfResponse[0]
							.getParameterValue(MQConstants.MQCA_CLUSTER_NAME)
							.toString().trim() + "') + \n");
			sb.append("COMMINFO('"
					+ pcfResponse[0]
							.getParameterValue(MQConstants.MQCA_COMM_INFO_NAME)
							.toString().trim() + "') + \n");
			sb.append("CUSTOM('"
					+ pcfResponse[0].getParameterValue(MQConstants.MQCA_CUSTOM)
							.toString().trim() + "') + \n");

			switch (Integer.parseInt(pcfResponse[0].getParameterValue(
					MQConstants.MQIA_TOPIC_DEF_PERSISTENCE).toString())) {
			case MQConstants.MQPER_PERSISTENCE_AS_PARENT:
				sb.append("DEFPRESP(ASPARENT) + \n");
				break;
			case MQConstants.MQPER_PERSISTENT:
				sb.append("DEFPRESP(SYNC) + \n");
				break;
			case MQConstants.MQPER_NOT_PERSISTENT:
				sb.append("DEFPRESP(ASYNC) + \n");
				break;
			}
			switch (Integer.parseInt(pcfResponse[0].getParameterValue(
					MQConstants.MQIA_DEF_PRIORITY).toString())) {
			case MQConstants.MQPRI_PRIORITY_AS_PARENT:
				sb.append("DEFPRTY(ASPARENT) + \n");
				break;
			default:
				sb.append("DEFPRTY() + \n");
				break;
			}

			switch (Integer.parseInt(pcfResponse[0].getParameterValue(
					MQConstants.MQIA_TOPIC_DEF_PERSISTENCE).toString())) {
			case MQConstants.MQPER_PERSISTENCE_AS_PARENT:
				sb.append("DEFPSIST(ASPARENT) + \n");
				break;
			case MQConstants.MQPER_PERSISTENT:
				sb.append("DEFPSIST(YES) + \n");
				break;
			case MQConstants.MQPER_NOT_PERSISTENT:
				sb.append("DEFPSIST(NO) + \n");
				break;
			}
			switch (Integer.parseInt(pcfResponse[0].getParameterValue(
					MQConstants.MQIA_DURABLE_SUB).toString())) {
			case MQConstants.MQSUB_DURABLE_AS_PARENT:
				sb.append("DURSUB(ASPARENT) + \n");
				break;
			case MQConstants.MQSUB_DURABLE_ALLOWED:
				sb.append("DURSUB(YES) + \n");
				break;
			case MQConstants.MQSUB_DURABLE_INHIBITED:
				sb.append("DURSUB(NO) + \n");
				break;
			}
			sb.append(" MDURMDL('"
					+ pcfResponse[0]
							.getParameterValue(MQConstants.MQCA_MODEL_DURABLE_Q)
							.toString().trim() + "') + \n");
			sb.append(" MNDURMDL('"
					+ pcfResponse[0]
							.getParameterValue(
									MQConstants.MQCA_MODEL_NON_DURABLE_Q)
							.toString().trim() + "') + \n");

			switch (Integer.parseInt(pcfResponse[0].getParameterValue(
					MQConstants.MQIA_NPM_DELIVERY).toString())) {
			case MQConstants.MQDLV_AS_PARENT:
				sb.append("NPMSGDLV(ASPARENT) + \n");
				break;
			case MQConstants.MQDLV_ALL:
				sb.append("NPMSGDLV(ALL) + \n");
				break;
			case MQConstants.MQDLV_ALL_DUR:
				sb.append("NPMSGDLV(ALLDUR) + \n");
				break;
			case MQConstants.MQDLV_ALL_AVAIL:
				sb.append("NPMSGDLV(ALLAVAIL) + \n");
				break;
			}
			switch (Integer.parseInt(pcfResponse[0].getParameterValue(
					MQConstants.MQIA_PM_DELIVERY).toString())) {
			case MQConstants.MQDLV_AS_PARENT:
				sb.append("PMSGDLV(ASPARENT) + \n");
				break;
			case MQConstants.MQDLV_ALL:
				sb.append("PMSGDLV(ALL) + \n");
				break;
			case MQConstants.MQDLV_ALL_DUR:
				sb.append("PMSGDLV(ALLDUR) + \n");
				break;
			case MQConstants.MQDLV_ALL_AVAIL:
				sb.append("PMSGDLV(ALLAVAIL) + \n");
				break;
			}

			switch (Integer.parseInt(pcfResponse[0].getParameterValue(
					MQConstants.MQIA_PROXY_SUB).toString())) {
			case MQConstants.MQTA_PROXY_SUB_FIRSTUSE:
				sb.append("PROXYSUB(FIRSTUSE) + \n");
				break;
			case MQConstants.MQTA_PROXY_SUB_FORCE:
				sb.append("PROXYSUB(FORCE) + \n");
				break;
			}
			switch (Integer.parseInt(pcfResponse[0].getParameterValue(
					MQConstants.MQIA_INHIBIT_PUB).toString())) {
			case MQConstants.MQTA_PUB_AS_PARENT:
				sb.append("PUB(ASPARENT) + \n");
				break;
			case MQConstants.MQTA_PUB_ALLOWED:
				sb.append("PUB(ENABLED) + \n");
				break;
			case MQConstants.MQTA_PUB_INHIBITED:
				sb.append("PUB(DISABLED) + \n");
				break;
			}

			switch (Integer.parseInt(pcfResponse[0].getParameterValue(
					MQConstants.MQIA_PUB_SCOPE).toString())) {
			case MQConstants.MQSCOPE_ALL:
				sb.append("PUBSCOPE(ALL) + \n");
				break;
			case MQConstants.MQSCOPE_AS_PARENT:
				sb.append("PUBSCOPE(ASPARENT) + \n");
				break;
			case MQConstants.MQSCOPE_QMGR:
				sb.append("PUBSCOPE(QMGR) + \n");
				break;
			}
			switch (Integer.parseInt(pcfResponse[0].getParameterValue(
					MQConstants.MQIA_INHIBIT_SUB).toString())) {
			case MQConstants.MQTA_SUB_AS_PARENT:
				sb.append("SUB(ASPARENT) + \n");
				break;
			case MQConstants.MQTA_SUB_ALLOWED:
				sb.append("SUB(ENABLED) + \n");
				break;
			case MQConstants.MQTA_SUB_INHIBITED:
				sb.append("SUB(DISABLED) + \n");
				break;
			}

			switch (Integer.parseInt(pcfResponse[0].getParameterValue(
					MQConstants.MQIA_SUB_SCOPE).toString())) {
			case MQConstants.MQSCOPE_AS_PARENT:
				sb.append("SUBSCOPE(ASPARENT) + \n");
				break;
			case MQConstants.MQSCOPE_QMGR:
				sb.append("SUBSCOPE(QMGR) + \n");
				break;
			case MQConstants.MQSCOPE_ALL:
				sb.append("SUBSCOPE(ALL) + \n");
				break;
			}
			switch (Integer.parseInt(pcfResponse[0].getParameterValue(
					MQConstants.MQIA_TOPIC_TYPE).toString())) {
			case MQConstants.MQTOPT_ALL:
				sb.append("TYPE(ALL) + \n");
				break;
			case MQConstants.MQTOPT_CLUSTER:
				sb.append("TYPE(CLUSTER) + \n");
				break;
			case MQConstants.MQTOPT_LOCAL:
				sb.append("TYPE(LOCAL) + \n");
				break;
			}

			switch (Integer.parseInt(pcfResponse[0].getParameterValue(
					MQConstants.MQIA_USE_DEAD_LETTER_Q).toString())) {
			case MQConstants.MQUSEDLQ_AS_PARENT:
				sb.append("USEDLQ(ASPARENT) + \n");
				break;
			case MQConstants.MQUSEDLQ_NO:
				sb.append("USEDLQ(NO) + \n");
				break;
			case MQConstants.MQUSEDLQ_YES:
				sb.append("USEDLQ(YES) + \n");
				break;
			}
			/*
			 * 
			 * switch
			 * (Integer.parseInt(pcfResponse[0].getParameterValue(MQConstants
			 * .MQIA_WILDCARD_OPERATION).toString())){ case
			 * MQConstants.MQTA_PASSTHRU: sb.append("WILDCARD(PASSTHRU) + \n");
			 * break; case MQConstants.MQTA_BLOCK:
			 * sb.append("WILDCARD(BLOCK) + \n"); break; }
			 * 
			 * switch
			 * (Integer.parseInt(pcfResponse[0].getParameterValue(MQConstants
			 * .MQIACF_REPLACE).toString())){ case MQConstants.MQRP_YES:
			 * sb.append("REPLACE + \n"); break; case MQConstants.MQRP_NO:
			 * sb.append("NOREPLACE + \n"); break; }
			 */
			sb.append(" DESCR('"
					+ pcfResponse[0]
							.getParameterValue(MQConstants.MQCA_TOPIC_DESC)
							.toString().trim() + "') \n");

			agent.disconnect();

			sb.append(" \n");

		}

		return sb;
	}

	public StringBuffer createSubScript(String qmgrHost, int qmgrPort,
			String subName, String qChannel) throws MQDataException, IOException {
		PCFCommons pcfCM = new PCFCommons();

		PCFMessageAgent agent = new PCFMessageAgent(qmgrHost, qmgrPort,
				qChannel);

		PCFMessage pcfCmd = new PCFMessage(
				MQConstants.MQCMD_INQUIRE_SUBSCRIPTION);
		pcfCmd.addParameter(MQConstants.MQCACF_SUB_NAME, subName);

		// Execute the command. The returned object is an array of PCF messages.
		PCFMessage[] pcfResponse = agent.send(pcfCmd);

		StringBuffer sb = new StringBuffer();

		if (pcfResponse[0].getParameterValue(MQConstants.MQCACF_SUB_NAME)
				.toString().indexOf("SYSTEM") != 0
				|| pcfResponse[0]
						.getParameterValue(MQConstants.MQCA_TOPIC_STRING)
						.toString().indexOf("SYSTEM") != 0) {

			sb.append("DEFINE SUB('"
					+ pcfResponse[0]
							.getParameterValue(MQConstants.MQCACF_SUB_NAME)
							.toString().trim() + "') + \n");
			sb.append("TOPICSTR("
					+ pcfResponse[0]
							.getParameterValue(MQConstants.MQCA_TOPIC_STRING)
							.toString().trim() + ") + \n");
			sb.append("TOPICOBJ('"
					+ pcfResponse[0]
							.getParameterValue(MQConstants.MQCA_TOPIC_NAME)
							.toString().trim() + "') + \n");
			sb.append("DEST("
					+ pcfResponse[0]
							.getParameterValue(MQConstants.MQCACF_DESTINATION)
							.toString().trim() + ") + \n");
			sb.append("DESTQMGR('"
					+ pcfResponse[0]
							.getParameterValue(
									MQConstants.MQCACF_DESTINATION_Q_MGR)
							.toString().trim() + "') + \n");

			/*
			 * int lenMSGComp =
			 * Array.getLength(pcfResponse[0].getParameterValue(
			 * MQConstants.MQBACF_DESTINATION_CORREL_ID)); for(int msgCtr = 0;
			 * msgCtr < lenMSGComp; msgCtr++){
			 * System.out.println(Array.get(pcfResponse
			 * [0].getParameterValue(MQConstants.MQBACF_DESTINATION_CORREL_ID),
			 * msgCtr)); switch
			 * (Integer.parseInt(Array.get(pcfResponse[0].getParameterValue
			 * (MQConstants.MQBACF_DESTINATION_CORREL_ID), msgCtr).toString())){
			 * case MQConstants.MQCOMPRESS_NONE:
			 * sb.append("COMPMSG (NONE) + \n"); break; case
			 * MQConstants.MQCOMPRESS_RLE: sb.append("COMPMSG (RLE) + \n");
			 * break; case MQConstants.MQCOMPRESS_ZLIBFAST:
			 * sb.append("COMPMSG (ZLIBFAST) + \n"); break; case
			 * MQConstants.MQCOMPRESS_ZLIBHIGH:
			 * sb.append("COMPMSG (ZLIBHIGH) + \n"); break; } }
			 */
			// sb.append(" PUBACCT("+pcfResponse[0].getParameterValue(MQConstants.MQBACF_ACCOUNTING_TOKEN)+") + \n");
			sb.append("PUBAPPID('"
					+ pcfResponse[0]
							.getParameterValue(
									MQConstants.MQCACF_APPL_IDENTITY_DATA)
							.toString().trim() + "') + \n");
			sb.append("SELECTOR('"
					+ pcfResponse[0]
							.getParameterValue(MQConstants.MQCACF_SUB_SELECTOR)
							.toString().trim() + "') + \n");
			sb.append("USERDATA('"
					+ pcfResponse[0]
							.getParameterValue(MQConstants.MQCACF_SUB_USER_DATA)
							.toString().trim() + "') + \n");
			sb.append("SUBLEVEL("
					+ pcfResponse[0]
							.getParameterValue(MQConstants.MQIACF_SUB_LEVEL)
							.toString().trim() + ") + \n");

			switch (Integer.parseInt(pcfResponse[0].getParameterValue(
					MQConstants.MQIACF_VARIABLE_USER_ID).toString())) {
			case MQConstants.MQVU_ANY_USER:
				sb.append("VARUSER(ANY) + \n");
				break;
			case MQConstants.MQVU_FIXED_USER:
				sb.append("VARUSER(FIXED) + \n");
				break;
			}

			switch (Integer.parseInt(pcfResponse[0].getParameterValue(
					MQConstants.MQIACF_PUB_PRIORITY).toString())) {
			case MQConstants.MQPRI_PRIORITY_AS_PUBLISHED:
				sb.append("PUBPRTY(ASPUB) + \n");
				break;
			default:
				sb.append("PUBPRTY(Integer.parseInt(pcfResponse[0].getParameterValue(MQConstants.MQIACF_PUB_PRIORITY).toString())) + \n");
				break;
			}

			switch (Integer.parseInt(pcfResponse[0].getParameterValue(
					MQConstants.MQIACF_EXPIRY).toString())) {
			default:
				sb.append("EXPIRY(UNLIMITED) + \n");
				break;
			}

			switch (Integer.parseInt(pcfResponse[0].getParameterValue(
					MQConstants.MQIACF_WILDCARD_SCHEMA).toString())) {
			case MQConstants.MQWS_CHAR:
				sb.append("WSCHEMA(CHAR) + \n");
				break;
			case MQConstants.MQWS_TOPIC:
				sb.append("WSCHEMA(TOPIC) + \n");
				break;
			}

			switch (Integer.parseInt(pcfResponse[0].getParameterValue(
					MQConstants.MQIACF_SUBSCRIPTION_SCOPE).toString())) {
			case MQConstants.MQTSCOPE_ALL:
				sb.append("SUBSCOPE(ALL) + \n");
				break;
			case MQConstants.MQTSCOPE_QMGR:
				sb.append("SUBSCOPE(QMGR) + \n");
				break;
			}

			switch (Integer.parseInt(pcfResponse[0].getParameterValue(
					MQConstants.MQIACF_DESTINATION_CLASS).toString())) {
			case MQConstants.MQDC_MANAGED:
				sb.append("DESTCLAS(MANAGED) + \n");
				break;
			case MQConstants.MQDC_PROVIDED:
				sb.append("DESTCLAS(PROVIDED) + \n");
				break;
			}

			switch (Integer.parseInt(pcfResponse[0].getParameterValue(
					MQConstants.MQIACF_PUBSUB_PROPERTIES).toString())) {
			case MQConstants.MQPSPROP_COMPAT:
				sb.append("PSPROP(COMPAT) + \n");
				break;
			case MQConstants.MQPSPROP_NONE:
				sb.append("PSPROP(NONE) + \n");
				break;
			case MQConstants.MQPSPROP_RFH2:
				sb.append("PSPROP(RFH2) + \n");
				break;
			}

			sb.append("SUBUSER('"
					+ pcfResponse[0]
							.getParameterValue(MQConstants.MQCACF_SUB_USER_ID)
							.toString().trim() + "') \n");

			agent.disconnect();

			sb.append(" \n");
		}
		return sb;
	}

	public List<Map> qmgrDetails(String qmgrHost, int qmgrPort, String qmgrName, String qChannel)
			throws PCFException, MQDataException, IOException {
		List<Map> queueDtl = new ArrayList<Map>();
		Map iMap = new HashMap();

		/*
		 * int[] pcfParmAttrs = {MQConstants.MQIACF_ALL}; PCFParameter[]
		 * pcfParameters = {new MQCFIL(MQConstants.MQIACF_Q_MGR_ATTRS,
		 * pcfParmAttrs)}; MQMessage[] mqResponse =
		 * agent.send(MQConstants.MQCMD_INQUIRE_Q_MGR, pcfParameters); MQCFH
		 * mqCFH = new MQCFH(mqResponse[0]);
		 */

		PCFMessageAgent agent = new PCFMessageAgent(qmgrHost, qmgrPort,
				qChannel);

		PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_INQUIRE_Q_MGR);
		// pcfCmd.addParameter(MQConstants.MQCA_Q_MGR_NAME, qmgrName);
		// pcfCmd.addParameter(MQConstants.MQIACF_ALL);

		// Execute the command. The returned object is an array of PCF messages.
		PCFMessage[] pcfResponse = agent.send(pcfCmd);

		for (int index = 0; index < pcfResponse.length; index++) {
			iMap = new HashMap();
			System.out.println("MQCA_Q_MGR_NAME - "
					+ pcfResponse[index]
							.getParameterValue(MQConstants.MQCA_Q_MGR_NAME));
			System.out
					.println(" - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQIA_ACCOUNTING_CONN_OVERRIDE));
			switch (Integer.parseInt(pcfResponse[index].getParameterValue(
					MQConstants.MQIA_ACCOUNTING_CONN_OVERRIDE).toString())) {
			case MQConstants.MQMON_DISABLED:
				System.out
						.println("MQIA_ACCOUNTING_CONN_OVERRIDE - MQMON_DISABLED");
				break;
			case MQConstants.MQMON_ENABLED:
				System.out
						.println("MQIA_ACCOUNTING_CONN_OVERRIDE - MQMON_ENABLED");
				break;
			}

			System.out
					.println("MQIA_ACCOUNTING_INTERVAL - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQIA_ACCOUNTING_INTERVAL));
			System.out
					.println("MQIA_ACCOUNTING_MQI - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQIA_ACCOUNTING_MQI));

			switch (Integer.parseInt(pcfResponse[index].getParameterValue(
					MQConstants.MQIA_ACCOUNTING_Q).toString())) {
			case MQConstants.MQMON_ON:
				System.out.println("MQIA_ACCOUNTING_Q - MQMON_ON");
				break;
			case MQConstants.MQMON_OFF:
				System.out.println("MQIA_ACCOUNTING_Q - MQMON_OFF");
				break;
			case MQConstants.MQMON_NONE:
				System.out.println("MQIA_ACCOUNTING_Q - MQMON_NONE");
				break;
			}

			switch (Integer.parseInt(pcfResponse[index].getParameterValue(
					MQConstants.MQIA_ACTIVITY_RECORDING).toString())) {
			case MQConstants.MQMON_DISABLED:
				System.out.println("MQIA_ACTIVITY_RECORDING - MQMON_DISABLED");
				break;
			case MQConstants.MQMON_ENABLED:
				System.out.println("MQIA_ACTIVITY_RECORDING - MQMON_ENABLED");
				break;
			}

			switch (Integer.parseInt(pcfResponse[index].getParameterValue(
					MQConstants.MQIA_ACTIVITY_CONN_OVERRIDE).toString())) {
			case MQConstants.MQRECORDING_DISABLED:
				System.out
						.println("MQIA_ACTIVITY_CONN_OVERRIDE - MQRECORDING_DISABLED");
				break;
			case MQConstants.MQRECORDING_MSG:
				System.out
						.println("MQIA_ACTIVITY_CONN_OVERRIDE - MQRECORDING_MSG");
				break;
			case MQConstants.MQRECORDING_Q:
				System.out
						.println("MQIA_ACTIVITY_CONN_OVERRIDE - MQRECORDING_Q");
				break;
			}

			switch (Integer.parseInt(pcfResponse[index].getParameterValue(
					MQConstants.MQIA_ACTIVITY_TRACE).toString())) {
			case MQConstants.MQMON_OFF:
				System.out.println("MQIA_ACTIVITY_TRACE - MQMON_OFF");
				break;
			case MQConstants.MQMON_ON:
				System.out.println("MQIA_ACTIVITY_TRACE - MQMON_ON");
				break;
			}

			System.out
					.println("MQCA_ALTERATION_DATE - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQCA_ALTERATION_DATE));
			System.out
					.println("MQCA_ALTERATION_TIME - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQCA_ALTERATION_TIME));

			switch (Integer.parseInt(pcfResponse[index].getParameterValue(
					MQConstants.MQIA_AUTHORITY_EVENT).toString())) {
			case MQConstants.MQEVR_DISABLED:
				System.out.println("MQIA_AUTHORITY_EVENT - MQEVR_DISABLED");
				break;
			case MQConstants.MQEVR_ENABLED:
				System.out.println("MQIA_AUTHORITY_EVENT - MQEVR_ENABLED");
				break;
			}

			System.out
					.println("MQIA_CODED_CHAR_SET_ID - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQIA_CODED_CHAR_SET_ID));

			switch (Integer.parseInt(pcfResponse[index].getParameterValue(
					MQConstants.MQIA_CERT_VAL_POLICY).toString())) {
			case MQConstants.MQ_CERT_VAL_POLICY_RFC5280:
				System.out
						.println("MQIA_CERT_VAL_POLICY - MQ_CERT_VAL_POLICY_RFC5280");
				break;
			case MQConstants.MQ_CERT_VAL_POLICY_ANY:
				System.out
						.println("MQIA_CERT_VAL_POLICY - MQ_CERT_VAL_POLICY_ANY");
				break;
			}

			switch (Integer.parseInt(pcfResponse[index].getParameterValue(
					MQConstants.MQIA_CHANNEL_AUTO_DEF).toString())) {
			case MQConstants.MQCHAD_DISABLED:
				System.out.println("MQIA_CHANNEL_AUTO_DEF - MQCHAD_DISABLED");
				break;
			case MQConstants.MQCHAD_ENABLED:
				System.out.println("MQIA_CHANNEL_AUTO_DEF - MQCHAD_ENABLED");
				break;
			}

			switch (Integer.parseInt(pcfResponse[index].getParameterValue(
					MQConstants.MQIA_CHANNEL_AUTO_DEF_EVENT).toString())) {
			case MQConstants.MQEVR_DISABLED:
				System.out
						.println("MQIA_CHANNEL_AUTO_DEF_EVENT - MQEVR_DISABLED");
				break;
			case MQConstants.MQEVR_ENABLED:
				System.out
						.println("MQIA_CHANNEL_AUTO_DEF_EVENT - MQEVR_ENABLED");
				break;
			}

			System.out
					.println("MQCA_CHANNEL_AUTO_DEF_EXIT - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQCA_CHANNEL_AUTO_DEF_EXIT));

			System.out.println("MQIA_CHANNEL_EVENT - "
					+ pcfResponse[index]
							.getParameterValue(MQConstants.MQIA_CHANNEL_EVENT));

			switch (Integer.parseInt(pcfResponse[index].getParameterValue(
					MQConstants.MQIA_CHLAUTH_RECORDS).toString())) {
			case MQConstants.MQCHLA_DISABLED:
				System.out.println("MQIA_CHLAUTH_RECORDS - MQCHLA_DISABLED");
				break;
			case MQConstants.MQCHLA_ENABLED:
				System.out.println("MQIA_CHLAUTH_RECORDS - MQCHLA_ENABLED");
				break;
			}

			System.out
					.println("MQCA_CLUSTER_WORKLOAD_DATA - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQCA_CLUSTER_WORKLOAD_DATA));
			System.out
					.println("MQCA_CLUSTER_WORKLOAD_EXIT - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQCA_CLUSTER_WORKLOAD_EXIT));
			System.out
					.println("MQIA_CLUSTER_WORKLOAD_LENGTH - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQIA_CLUSTER_WORKLOAD_LENGTH));
			System.out
					.println("MQIA_CLWL_MRU_CHANNELS - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQIA_CLWL_MRU_CHANNELS));
			System.out.println("MQIA_CLWL_USEQ - "
					+ pcfResponse[index]
							.getParameterValue(MQConstants.MQIA_CLWL_USEQ));

			switch (Integer.parseInt(pcfResponse[index].getParameterValue(
					MQConstants.MQIA_COMMAND_EVENT).toString())) {
			case MQConstants.MQEVR_DISABLED:
				System.out.println("MQIA_COMMAND_EVENT - MQEVR_DISABLED");
				break;
			case MQConstants.MQEVR_ENABLED:
				System.out.println("MQIA_COMMAND_EVENT - MQEVR_ENABLED");
				break;
			}

			System.out.println("MQIA_COMMAND_LEVEL - "
					+ pcfResponse[index]
							.getParameterValue(MQConstants.MQIA_COMMAND_LEVEL));
			System.out
					.println("MQCA_COMMAND_INPUT_Q_NAME - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQCA_COMMAND_INPUT_Q_NAME));
			System.out
					.println("MQIA_CONFIGURATION_EVENT - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQIA_CONFIGURATION_EVENT));
			System.out.println("MQCA_CREATION_DATE - "
					+ pcfResponse[index]
							.getParameterValue(MQConstants.MQCA_CREATION_DATE));
			System.out.println("MQCA_CREATION_TIME - "
					+ pcfResponse[index]
							.getParameterValue(MQConstants.MQCA_CREATION_TIME));
			System.out.println("MQCA_CUSTOM - "
					+ pcfResponse[index]
							.getParameterValue(MQConstants.MQCA_CUSTOM));
			System.out
					.println("MQCA_DEAD_LETTER_Q_NAME - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQCA_DEAD_LETTER_Q_NAME));
			System.out
					.println("MQIA_DEF_CLUSTER_XMIT_Q_TYPE - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQIA_DEF_CLUSTER_XMIT_Q_TYPE));
			System.out
					.println("MQCA_DEF_XMIT_Q_NAME - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQCA_DEF_XMIT_Q_NAME));
			System.out.println("MQCA_Q_MGR_DESC - "
					+ pcfResponse[index]
							.getParameterValue(MQConstants.MQCA_Q_MGR_DESC));
			System.out.println("MQIA_DIST_LISTS - "
					+ pcfResponse[index]
							.getParameterValue(MQConstants.MQIA_DIST_LISTS));
			System.out.println("MQIA_INHIBIT_EVENT - "
					+ pcfResponse[index]
							.getParameterValue(MQConstants.MQIA_INHIBIT_EVENT));
			System.out
					.println("MQIA_IP_ADDRESS_VERSION - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQIA_IP_ADDRESS_VERSION));
			System.out.println("MQIA_LOCAL_EVENT - "
					+ pcfResponse[index]
							.getParameterValue(MQConstants.MQIA_LOCAL_EVENT));
			System.out.println("MQIA_LOGGER_EVENT - "
					+ pcfResponse[index]
							.getParameterValue(MQConstants.MQIA_LOGGER_EVENT));
			System.out
					.println("MQIA_MSG_MARK_BROWSE_INTERVAL - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQIA_MSG_MARK_BROWSE_INTERVAL));
			System.out.println("MQIA_MAX_HANDLES - "
					+ pcfResponse[index]
							.getParameterValue(MQConstants.MQIA_MAX_HANDLES));
			System.out
					.println("MQIA_MAX_MSG_LENGTH - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQIA_MAX_MSG_LENGTH));
			System.out
					.println("MQIA_MAX_PROPERTIES_LENGTH - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQIA_MAX_PROPERTIES_LENGTH));
			System.out.println("MQIA_MAX_PRIORITY - "
					+ pcfResponse[index]
							.getParameterValue(MQConstants.MQIA_MAX_PRIORITY));
			System.out
					.println("MQIA_MAX_UNCOMMITTED_MSGS - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQIA_MAX_UNCOMMITTED_MSGS));

			switch (Integer.parseInt(pcfResponse[index].getParameterValue(
					MQConstants.MQIA_MONITORING_AUTO_CLUSSDR).toString())) {
			case MQConstants.MQMON_OFF:
				System.out.println("MQIA_MONITORING_AUTO_CLUSSDR - MQMON_OFF");
				break;
			case MQConstants.MQMON_NONE:
				System.out.println("MQIA_MONITORING_AUTO_CLUSSDR - MQMON_NONE");
				break;
			case MQConstants.MQMON_LOW:
				System.out.println("MQIA_MONITORING_AUTO_CLUSSDR - MQMON_LOW");
				break;
			case MQConstants.MQMON_MEDIUM:
				System.out
						.println("MQIA_MONITORING_AUTO_CLUSSDR - MQMON_MEDIUM");
				break;
			case MQConstants.MQMON_HIGH:
				System.out.println("MQIA_MONITORING_AUTO_CLUSSDR - MQMON_HIGH");
				break;
			}

			System.out
					.println("MQIA_MONITORING_CHANNEL - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQIA_MONITORING_CHANNEL));

			switch (Integer.parseInt(pcfResponse[index].getParameterValue(
					MQConstants.MQIA_MONITORING_Q).toString())) {
			case MQConstants.MQMON_NONE:
				System.out.println("MQIA_MONITORING_Q - MQMON_NONE");
				break;
			case MQConstants.MQMON_OFF:
				System.out.println("MQIA_MONITORING_Q - MQMON_OFF");
				break;
			case MQConstants.MQMON_LOW:
				System.out.println("MQIA_MONITORING_Q - MQMON_LOW");
				break;
			case MQConstants.MQMON_MEDIUM:
				System.out.println("MQIA_MONITORING_Q - MQMON_MEDIUM");
				break;
			case MQConstants.MQMON_HIGH:
				System.out.println("MQIA_MONITORING_Q - MQMON_HIGH");
				break;
			}

			System.out.println("MQCA_PARENT - "
					+ pcfResponse[index]
							.getParameterValue(MQConstants.MQCA_PARENT));
			System.out
					.println("MQIA_PERFORMANCE_EVENT - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQIA_PERFORMANCE_EVENT));
			System.out.println("MQIA_PLATFORM - "
					+ pcfResponse[index]
							.getParameterValue(MQConstants.MQIA_PLATFORM));
			System.out.println("MQIA_PUBSUB_MODE - "
					+ pcfResponse[index]
							.getParameterValue(MQConstants.MQIA_PUBSUB_MODE));
			System.out
					.println("MQIA_PUBSUB_CLUSTER - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQIA_PUBSUB_CLUSTER));
			System.out.println("MQIA_PUBSUB_NP_MSG - "
					+ pcfResponse[index]
							.getParameterValue(MQConstants.MQIA_PUBSUB_NP_MSG));
			System.out
					.println("MQIA_PUBSUB_NP_RESP - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQIA_PUBSUB_NP_RESP));
			System.out
					.println("MQIA_PUBSUB_MAXMSG_RETRY_COUNT - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQIA_PUBSUB_MAXMSG_RETRY_COUNT));
			System.out
					.println("MQIA_PUBSUB_SYNC_PT - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQIA_PUBSUB_SYNC_PT));
			System.out
					.println("MQCA_Q_MGR_IDENTIFIER - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQCA_Q_MGR_IDENTIFIER));

			switch (Integer.parseInt(pcfResponse[index].getParameterValue(
					MQConstants.MQIA_REMOTE_EVENT).toString())) {
			case MQConstants.MQEVR_ENABLED:
				System.out.println("MQIA_REMOTE_EVENT - MQEVR_ENABLED");
				break;
			case MQConstants.MQEVR_DISABLED:
				System.out.println("MQIA_REMOTE_EVENT - MQEVR_DISABLED");
				break;
			}

			System.out
					.println("MQCA_REPOSITORY_NAME - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQCA_REPOSITORY_NAME));
			System.out
					.println("MQCA_REPOSITORY_NAMELIST - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQCA_REPOSITORY_NAMELIST));

			switch (Integer.parseInt(pcfResponse[index].getParameterValue(
					MQConstants.MQIA_TRACE_ROUTE_RECORDING).toString())) {
			case MQConstants.MQRECORDING_DISABLED:
				System.out
						.println("MQIA_TRACE_ROUTE_RECORDING - MQRECORDING_DISABLED");
				break;
			case MQConstants.MQRECORDING_Q:
				System.out
						.println("MQIA_TRACE_ROUTE_RECORDING - MQRECORDING_Q");
				break;
			case MQConstants.MQRECORDING_MSG:
				System.out
						.println("MQIA_TRACE_ROUTE_RECORDING - MQRECORDING_MSG");
				break;
			}

			System.out
					.println("MQIA_CHINIT_CONTROL - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQIA_CHINIT_CONTROL));
			System.out
					.println("MQIA_CMD_SERVER_CONTROL - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQIA_CMD_SERVER_CONTROL));
			System.out
					.println("MQIA_PROT_POLICY_CAPABILITY - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQIA_PROT_POLICY_CAPABILITY));
			System.out
					.println("MQCA_SSL_CRL_NAMELIST - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQCA_SSL_CRL_NAMELIST));
			System.out
					.println("MQCA_SSL_CRYPTO_HARDWARE - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQCA_SSL_CRYPTO_HARDWARE));

			switch (Integer.parseInt(pcfResponse[index].getParameterValue(
					MQConstants.MQIA_SSL_EVENT).toString())) {
			case MQConstants.MQEVR_DISABLED:
				System.out.println("MQIA_SSL_EVENT - MQEVR_DISABLED");
				break;
			case MQConstants.MQEVR_ENABLED:
				System.out.println("MQIA_SSL_EVENT - MQEVR_ENABLED");
				break;
			}

			System.out
					.println("MQIA_SSL_FIPS_REQUIRED - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQIA_SSL_FIPS_REQUIRED));
			System.out
					.println("MQCA_SSL_KEY_REPOSITORY - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQCA_SSL_KEY_REPOSITORY));
			System.out
					.println("MQIA_SSL_RESET_COUNT - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQIA_SSL_RESET_COUNT));
			switch (Integer.parseInt(pcfResponse[index].getParameterValue(
					MQConstants.MQIA_STATISTICS_AUTO_CLUSSDR).toString())) {
			case MQConstants.MQMON_Q_MGR:
				System.out
						.println("MQIA_STATISTICS_AUTO_CLUSSDR - MQMON_Q_MGR");
				break;
			case MQConstants.MQMON_OFF:
				System.out.println("MQIA_STATISTICS_AUTO_CLUSSDR - MQMON_OFF");
				break;
			case MQConstants.MQMON_LOW:
				System.out.println("MQIA_STATISTICS_AUTO_CLUSSDR - MQMON_LOW");
				break;
			case MQConstants.MQMON_MEDIUM:
				System.out
						.println("MQIA_STATISTICS_AUTO_CLUSSDR - MQMON_MEDIUM");
				break;
			case MQConstants.MQMON_HIGH:
				System.out.println("MQIA_STATISTICS_AUTO_CLUSSDR - MQMON_HIGH");
				break;
			}

			switch (Integer.parseInt(pcfResponse[index].getParameterValue(
					MQConstants.MQIA_STATISTICS_CHANNEL).toString())) {
			case MQConstants.MQMON_NONE:
				System.out.println("MQIA_STATISTICS_CHANNEL - MQMON_NONE");
				break;
			case MQConstants.MQMON_OFF:
				System.out.println("MQIA_STATISTICS_CHANNEL - MQMON_OFF");
				break;
			case MQConstants.MQMON_LOW:
				System.out.println("MQIA_STATISTICS_CHANNEL - MQMON_LOW");
				break;
			case MQConstants.MQMON_MEDIUM:
				System.out.println("MQIA_STATISTICS_CHANNEL - MQMON_MEDIUM");
				break;
			case MQConstants.MQMON_HIGH:
				System.out.println("MQIA_STATISTICS_CHANNEL - MQMON_HIGH");
				break;
			}

			System.out
					.println("MQIA_STATISTICS_INTERVAL - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQIA_STATISTICS_INTERVAL));
			System.out
					.println("MQIA_STATISTICS_MQI - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQIA_STATISTICS_MQI));
			System.out.println("MQIA_STATISTICS_Q - "
					+ pcfResponse[index]
							.getParameterValue(MQConstants.MQIA_STATISTICS_Q));

			switch (Integer.parseInt(pcfResponse[index].getParameterValue(
					MQConstants.MQIA_START_STOP_EVENT).toString())) {
			case MQConstants.MQEVR_DISABLED:
				System.out.println("MQIA_START_STOP_EVENT - MQEVR_DISABLED");
				break;
			case MQConstants.MQEVR_ENABLED:
				System.out.println("MQIA_START_STOP_EVENT - MQEVR_ENABLED");
				break;
			}

			System.out
					.println("MQIA_SUITE_B_STRENGTH - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQIA_SUITE_B_STRENGTH));
			System.out.println("MQIA_SYNCPOINT - "
					+ pcfResponse[index]
							.getParameterValue(MQConstants.MQIA_SYNCPOINT));
			System.out
					.println("MQIA_TREE_LIFE_TIME - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQIA_TREE_LIFE_TIME));
			System.out
					.println("MQIA_TRIGGER_INTERVAL - "
							+ pcfResponse[index]
									.getParameterValue(MQConstants.MQIA_TRIGGER_INTERVAL));
			System.out.println("MQCA_VERSION - "
					+ pcfResponse[index]
							.getParameterValue(MQConstants.MQCA_VERSION));
			System.out.println("MQIA_XR_CAPABILITY - "
					+ pcfResponse[index]
							.getParameterValue(MQConstants.MQIA_XR_CAPABILITY));

			queueDtl.add(iMap);
		}

		agent.disconnect();
		return queueDtl;
	}

	public void moveQueue(String qmgrHost, int qmgrPort, String sourceQueue,
			String targetQueue, String qChannel) throws PCFException, MQDataException,
			IOException {
		List<Map> subListDtl = new ArrayList<Map>();
		Map iMap = new HashMap();

		PCFMessageAgent agent = new PCFMessageAgent(qmgrHost, qmgrPort,
				qChannel);

		// Create the PCF message type for the inquire.
		PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_MOVE_Q);

		// Add the inquire rules.
		// Topic name = wildcard.
		pcfCmd.addParameter(MQConstants.MQCACF_FROM_Q_NAME, sourceQueue);
		pcfCmd.addParameter(MQConstants.MQCACF_TO_Q_NAME, targetQueue);
		pcfCmd.addParameter(MQConstants.MQIA_QSG_DISP,
				MQConstants.MQIACF_MOVE_TYPE_MOVE);

		// Execute the command. The returned object is an array of PCF messages.
		PCFMessage[] pcfResponse = agent.send(pcfCmd);
		System.out.println(pcfResponse);
		/*
		 * for (int index = 0; index < pcfResponse.length; index++) {
		 * System.out.println("MQCA_Q_MGR_NAME - " + pcfResponse[index]
		 * .getParameterValue(MQConstants.MQCA_Q_MGR_NAME)); }
		 */

		agent.disconnect();

		// return subListDtl;
	}

	public String deleteQueue(String qmgrHost, int qmgrPort, String qName, String qChannel)
			throws PCFException, MQDataException, IOException {
		String resOutput = new String();

		PCFMessageAgent agent = new PCFMessageAgent(qmgrHost, qmgrPort,
				qChannel);

		// Create the PCF message type for the create queue.
		// NB: The parameters must be added in a specific order or an exception
		// (3015)
		// will be thrown.
		PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_DELETE_Q);

		// Queue name - Mandatory.
		pcfCmd.addParameter(MQConstants.MQCA_Q_NAME, qName);

		try {
			// Execute the command. The returned object is an array of PCF
			// messages.
			// If the Queue already exists, then catch the exception, otherwise
			// rethrow.
			/* PCFMessage[] pcfResponse = */// We ignore the returned result
			agent.send(pcfCmd);
		} catch (PCFException pcfe) {
			if (pcfe.reasonCode == MQConstants.MQRCCF_OBJECT_OPEN) {

				resOutput = "The queue \"" + qName + "\" is open.";
			} else {
				resOutput = "The queue \"" + qName
						+ "\" could not be deleted on the queue manager.";
				throw pcfe;
			}
		}

		agent.disconnect();
		return resOutput;
	}

	public void deleteChannel(String qmgrHost, int qmgrPort, String chanName, String qChannel)
			throws MQDataException, IOException {

		PCFCommons pcfCM = new PCFCommons();
		PCFMessageAgent agent = new PCFMessageAgent(qmgrHost, qmgrPort,
				qChannel);

		// Create the PCF message type for the create channel.
		PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_DELETE_CHANNEL);

		// Add the create channel mandatory parameters.
		// Channel name.
		pcfCmd.addParameter(MQConstants.MQCACH_CHANNEL_NAME, chanName);

		// Execute the command. If the command causes the
		// 'MQRCCF_OBJECT_ALREADY_EXISTS' exception
		// to be thrown, catch it here as this is ok.
		// If successful, the returned object is an array of PCF messages.
		try {
			/* PCFMessage[] pcfResponse = */// We ignore the returned result
			agent.send(pcfCmd);
		} catch (PCFException pcfe) {
			if (pcfe.reasonCode != MQConstants.MQRCCF_OBJECT_ALREADY_EXISTS) {
				throw pcfe;
			}
		}

		agent.disconnect();

		return;
	}

	public void deleteListener(String qmgrHost, int qmgrPort, String listName, String qChannel)
			throws MQDataException, IOException {

		PCFCommons pcfCM = new PCFCommons();
		PCFMessageAgent agent = new PCFMessageAgent(qmgrHost, qmgrPort,
				qChannel);

		// Create the PCF message type for the create channel.
		PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_DELETE_LISTENER);

		// Add the create channel mandatory parameters.
		// Channel name.
		pcfCmd.addParameter(MQConstants.MQCACH_LISTENER_NAME, listName);

		// Execute the command. If the command causes the
		// 'MQRCCF_OBJECT_ALREADY_EXISTS' exception
		// to be thrown, catch it here as this is ok.
		// If successful, the returned object is an array of PCF messages.
		try {
			/* PCFMessage[] pcfResponse = */// We ignore the returned result
			agent.send(pcfCmd);
		} catch (PCFException pcfe) {
			pcfe.printStackTrace();
		}
		agent.disconnect();

		return;
	}

	public void deleteTopic(String qmgrHost, int qmgrPort, String topicName,
			String topicString, String qChannel) throws PCFException, MQDataException,
			IOException {

		PCFMessageAgent agent = new PCFMessageAgent(qmgrHost, qmgrPort,
				qChannel);

		// Create the PCF message type for the inquire.
		PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_DELETE_TOPIC);

		// Add the inquire rules.
		// Topic name = wildcard.
		if (topicName.isEmpty()) {
			pcfCmd.addParameter(MQConstants.MQCA_TOPIC_STRING, topicString);
		} else {
			pcfCmd.addParameter(MQConstants.MQCA_TOPIC_NAME, topicName);
		}

		// Execute the command. The returned object is an array of PCF messages.
		agent.send(pcfCmd);

		agent.disconnect();

	}

	public void deleteSub(String qmgrHost, int qmgrPort, String subName, String qChannel)
			throws PCFException, MQDataException, IOException {

		PCFMessageAgent agent = new PCFMessageAgent(qmgrHost, qmgrPort,
				qChannel);

		// Create the PCF message type for the inquire.
		PCFMessage pcfCmd = new PCFMessage(
				MQConstants.MQCMD_DELETE_SUBSCRIPTION);

		// Add the inquire rules.
		// Topic name = wildcard.
		pcfCmd.addParameter(MQConstants.MQCACF_SUB_NAME, subName);

		// Execute the command. The returned object is an array of PCF messages.
		agent.send(pcfCmd);

		agent.disconnect();
	}

}
