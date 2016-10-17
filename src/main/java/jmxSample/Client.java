package jmxSample;
/*
 * Client.java - JMX client that interacts with the JMX agent. It gets
 * attributes and performs operations on the LargeArray MBean. 
 * It also listens for MBean notifications.
 */

import java.io.IOException;
import java.lang.management.MemoryMXBean;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import javax.management.AttributeChangeNotification;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.JMX;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class Client {

	/**
	 * Inner class that will handle the notifications.
	 */
	public static class ClientListener implements NotificationListener {
		public void handleNotification(Notification notification, Object handback) {
			echo("\nReceived notification:");
			echo("\tClassName: " + notification.getClass().getName());
			echo("\tSource: " + notification.getSource());
			echo("\tType: " + notification.getType());
			echo("\tMessage: " + notification.getMessage());
			if (notification instanceof AttributeChangeNotification) {
				AttributeChangeNotification acn = (AttributeChangeNotification) notification;
				echo("\tAttributeName: " + acn.getAttributeName());
				echo("\tAttributeType: " + acn.getAttributeType());
				echo("\tNewValue: " + acn.getNewValue());
				echo("\tOldValue: " + acn.getOldValue());
			}
		}
	}

	public static void main(String[] args) {

		String url = JmxSampleConstants.DEFAULT_URL;
		if (args != null && args.length > 0) {
			url = args[0];
		}

		// Create an RMI connector client and
		// connect it to the RMI connector server
		//
		echo("\nClient started...connection to the RMI connector server on " + url);
		JMXServiceURL remoteUrl;
		try {
			remoteUrl = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + url + "/jmxrmi");
		} catch (MalformedURLException e) {

			echo(JmxSampleConstants.BAD_FORMATTED_URL_ERR + url);
			echo(e.getMessage());
			return;
		}

		JMXConnector jmxc = null;
		try {
			jmxc = JMXConnectorFactory.connect(remoteUrl, null);

			// Create listener
			//
			ClientListener listener = new ClientListener();

			// Get an MBeanServerConnection
			//
			echo("\nGet an MBeanServerConnection");
			MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
			waitForEnterPressed();

			// Get domains from MBeanServer
			//
			echo("Domains:");
			String domains[] = mbsc.getDomains();
			Arrays.sort(domains);
			for (String domain : domains) {
				echo("\tDomain = " + domain);
			}
			waitForEnterPressed();

			// Query MBean names
			//
			echo("\nQuery MBeanServer MBeans:");
			Set<ObjectName> names = new TreeSet<ObjectName>(mbsc.queryNames(null, null));
			for (ObjectName name : names) {
				echo("\tObjectName = " + name);
			}
			waitForEnterPressed();

			// ----------------------
			// Manage the Memory
			// ----------------------
			ObjectName memoryMbean;
			memoryMbean = new ObjectName("java.lang:type=Memory");
			ObjectName mbeanName = new ObjectName("jmxSample:type=LargeArray");
			LargeArrayMBean mbeanProxy = JMX.newMBeanProxy(mbsc, mbeanName, LargeArrayMBean.class, true);
			// Add notification listener on MBean
			mbsc.addNotificationListener(mbeanName, listener, null, null);
			
			
			boolean quitMenu = false;
			Scanner keyboard = new Scanner(System.in);

			while (!quitMenu) {
				printMainMenu();
				int item = keyboard.nextInt();
				switch (item) {
				case 1:
					printHeapMemoryUsage_MemoryMBean(mbsc.getAttribute(memoryMbean, "HeapMemoryUsage"));
					waitForEnterPressed();
					break;
				case 2:
					printHeapMemoryUsage_LargeArrayMBean(mbeanProxy);
					waitForEnterPressed();
					break;
				case 3:
					mbeanProxy.fillDoubleArray();
					echo("fillDoubleArray invoked.");
					waitForEnterPressed();
					break;
				case 4:
					quitMenu = true;
					break;
				default:
					echo("unknown entry");
					break;
				}
			}

			keyboard.close();

		} catch (IOException e) {
			echo(JmxSampleConstants.CONNECTION_OPENING_ERR + url);
			echo(e.getMessage());
			return;
		} catch (MalformedObjectNameException e) {
			echo(JmxSampleConstants.BAD_FORMATTED_NAME_ERR);
			echo(e.getMessage());
		} catch (Exception e) {
			echo(JmxSampleConstants.JMX_ERROR);
			echo(e.getMessage());
		}

		// Close MBeanServer connection
		//
		echo("\nClose the connection to the server");
		try {
			jmxc.close();
		} catch (IOException e) {
			echo(JmxSampleConstants.CONNECTION_CLOSING_ERR);

			echo(e.getMessage());
		}
		echo("\nBye! Bye!");

	}

	private static void printHeapMemoryUsage_MemoryMBean(Object obj) {
		if (obj != null && obj instanceof CompositeData) {
			CompositeData cd = (CompositeData) obj;
			echo(">>> HeapMemoryUsage (MemoryMBean) <<<");
			System.out.println("init : " + humanReadableByteCount(cd.get("init")));
			System.out.println("used : " + humanReadableByteCount(cd.get("used")));
			System.out.println("committed : " + humanReadableByteCount(cd.get("committed")));
			System.out.println("max : " + humanReadableByteCount(cd.get("max")));
		}
	}

	private static void printHeapMemoryUsage_LargeArrayMBean(LargeArrayMBean mbeanProxy) {
		if (mbeanProxy != null) {
			echo(">>> HeapMemoryUsage (LargeArrayMBean) <<<");
			echo("getFreeHeapSize = " + humanReadableByteCount(mbeanProxy.getFreeHeapSize()));
			echo("getTotalHeapSize = " + humanReadableByteCount(mbeanProxy.getTotalHeapSize()));
			echo("getMaxHeapSize = " + humanReadableByteCount(mbeanProxy.getMaxHeapSize()));
		}
	}

	private static void printMainMenu() {
		echo("1. Show HeapMemoryUsage (MemoryMBean) ");
		echo("2. Show HeapMemoryUsage (LargeArrayMBean) ");
		echo("3. Invoke fillDoubleArray ");
		echo("4. Quit");
	}

	private static void echo(String msg) {
		System.out.println(msg);
	}

	private static void waitForEnterPressed() {
		try {
			echo("\nPress <Enter> to continue...");
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Method to convert byte size into human-readable format
	// Ex. 1024bytes is converted in "1 Kb", 1024*1024 in "1 Mb"
	public static String humanReadableByteCount(Object obj) {
		if (obj instanceof Long) {
			Long bytes = (Long) obj;
			int unit = 1000;
			if (bytes < unit)
				return bytes + " B";

			int exp = (int) (Math.log(bytes) / Math.log(unit));
			String pre = ("kMGTPE").charAt(exp - 1) + "";
			return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
		} else {
			return obj.toString();
		}
	}
}
