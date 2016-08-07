package com.rodriguemouadeu.jms.sender;

import java.util.Date;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.QueueSender;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.QueueSession;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;

public class Sender {

	static Properties env;
	static {
		env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		env.put(Context.PROVIDER_URL, "tcp://localhost:61616");
		env.put("queue.queueSampleQueue", "MyNewQueue");

	}

	public static void main(String[] args) throws Exception {

		// get the initial context
		final InitialContext ctx = new InitialContext(env);

		Thread senderThread = new Thread(new Runnable() {

			public void run() {

				while (true) {
					Queue queue;
					QueueConnection queueConn = null;
					try {
						// lookup the queue object
						queue = (Queue) ctx.lookup("queueSampleQueue");
						// lookup the queue connection factory
						QueueConnectionFactory connFactory = (QueueConnectionFactory) ctx
								.lookup("QueueConnectionFactory");
						// create a queue connection
						queueConn = connFactory.createQueueConnection();

						// create a queue session
						QueueSession queueSession = queueConn.createQueueSession(false, Session.DUPS_OK_ACKNOWLEDGE);

						// create a queue sender
						QueueSender queueSender = queueSession.createSender(queue);
						queueSender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

						// create a simple message to say "Hello"
						TextMessage message = queueSession.createTextMessage("Hello my name is Bob" + " ("+new Date()+")");

						// send the message
						queueSender.send(message);

						System.out.println("sent: " + message.getText());

						queueConn.close();

					} catch (NamingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						if (queueConn != null) {
							try {
								queueConn.close();
							} catch (JMSException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

					try {
						Thread.sleep(3000L);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		senderThread.start();

	}
}
