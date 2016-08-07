package com.rodriguemouadeu.jms.receiver;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.QueueSession;
import javax.jms.QueueReceiver;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;

public class Receiver {

	static Properties env;
	static {
		env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		env.put(Context.PROVIDER_URL, "tcp://localhost:61616");
		env.put("queue.queueSampleQueue", "MyNewQueue");
	}

	public static void main(String args[]) throws Exception {

		Thread receiverThread = new Thread(new Runnable() {

			public void run() {
				while (true) {
					// TODO Auto-generated method stub
					// get the initial context
					InitialContext ctx;
					QueueConnection queueConn = null;

					try {
						ctx = new InitialContext(env);

						// lookup the queue object
						Queue queue = (Queue) ctx.lookup("queueSampleQueue");

						// lookup the queue connection factory
						QueueConnectionFactory connFactory = (QueueConnectionFactory) ctx
								.lookup("QueueConnectionFactory");

						// create a queue connection
						queueConn = connFactory.createQueueConnection();

						// create a queue session
						QueueSession queueSession = queueConn.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

						// create a queue receiver
						QueueReceiver queueReceiver = queueSession.createReceiver(queue);

						// start the connection
						queueConn.start();

						// receive a message
						TextMessage message = (TextMessage) queueReceiver.receive();

						// print the message
						System.out.println("received: " + message.getText());
					} catch (NamingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {

						// close the queue connection
						try {
							queueConn.close();
						} catch (JMSException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		});
		receiverThread.start();
	}
}
