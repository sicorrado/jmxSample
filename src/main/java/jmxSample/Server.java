package jmxSample;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public class Server {
	public static void main(String[] args) {

		try {
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			ObjectName name;
			name = new ObjectName("jmxSample:type=LargeArray");
			
			LargeArray mbean = new LargeArray();
			mbs.registerMBean(mbean, name);

			System.out.println("Server running...");
			Thread.sleep(Long.MAX_VALUE);
			
			
		} catch (MalformedObjectNameException e) {
			echo(JmxSampleConstants.BAD_FORMATTED_NAME_ERR);
			echo (e.getMessage());
		} catch (Exception e) {
			echo(JmxSampleConstants.JMX_ERROR);
			echo (e.getMessage());
		}
	}
	
	private static void echo(String msg) {
		System.out.println(msg);
	}
}
