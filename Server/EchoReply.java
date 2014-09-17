package mpp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EchoReply {
    
	long time = System.nanoTime()/1000;
	int sec = (int)time/1000000,msec = (int)(time%1000000);
	byte[] t = {
	        (byte) (sec >> 24),
	        (byte) (sec >> 16),
	        (byte) (sec >> 8),
	        (byte) sec};
	byte[] t1 = {
	        (byte) (msec >> 24),
	        (byte) (msec >> 16),
	        (byte) (msec >> 8),
	        (byte) msec};
    byte Type = 65;
    
    byte[] Version = {(byte)0,(byte)0,(byte)0,(byte)1,(byte)2}; 
    byte[] ClientId;
    byte[] SequenceNum;
    byte[] ClientTimeStamp;
    byte[] MulticastGroup;
    byte[] TTL = {(byte)0,(byte)9,(byte)0,(byte)1,(byte)64};
    //OptionRequest;
    byte[] ServerTimeStamp = {(byte)0,(byte)12,(byte)0,(byte)8};
    
    public byte[] toBytes() throws IOException
    {
    	ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
    	outputStream.write( Type );
    	outputStream.write( Version );
    	outputStream.write( ClientId );
    	outputStream.write( ClientTimeStamp );
    	outputStream.write( MulticastGroup );
    	outputStream.write( SequenceNum );
    	if(MPP.stamp){
    		outputStream.write( ServerTimeStamp );
    		outputStream.write(t);
    		outputStream.write(t1);
    	}
    	outputStream.write( TTL );
    	
    	return outputStream.toByteArray( );
    }
}
