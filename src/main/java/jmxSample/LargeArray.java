package jmxSample;

import java.util.ArrayList;
import java.util.Random;

import javax.management.AttributeChangeNotification;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;

public class LargeArray  extends NotificationBroadcasterSupport
implements LargeArrayMBean{
	
	public static long count = 0;
	private long sequenceNumber = 1;
    private ArrayList <Double> bigList = new ArrayList<Double>();
    
	public Long getMaxHeapSize() {
		return Runtime.getRuntime().maxMemory();
	}

	public Long getFreeHeapSize() {
		return Runtime.getRuntime().freeMemory();
	}

	public Long getTotalHeapSize() {
		return Runtime.getRuntime().totalMemory();		
	}

	public void fillDoubleArray() {
		
		Long oldSize = getFreeHeapSize();
		Random rand = new Random();
		for(int i = 0; i < JmxSampleConstants.DEFAULT_ARRAY_SIZE; i++)
			bigList.add(rand.nextDouble());
		
		if(getFreeHeapSize() < JmxSampleConstants.DEFAULT_MIN_HEAP_SIZE){
			Notification n =
				    new AttributeChangeNotification(this,
								    sequenceNumber++,
								    System.currentTimeMillis(),
								    "FreeHeapSize < min size",
								    "FreeHeapSize",
								    "long",
								    oldSize,
								    this.getFreeHeapSize());
			sendNotification(n);
			
		}
	}
}