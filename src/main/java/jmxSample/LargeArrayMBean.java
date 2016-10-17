package jmxSample;
 
public interface LargeArrayMBean { 
 
    public Long getMaxHeapSize();
    public Long getFreeHeapSize();
    public Long getTotalHeapSize();
    
    public void fillDoubleArray(); 
    
} 