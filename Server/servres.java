package mpp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class servres {

	byte Type = 83;

	byte[] Version = {(byte)0,(byte)0,(byte)0,(byte)1,(byte)2}; 
    byte[] ClientId;
    //Options SequenceNum;
    byte[] MulticastGroup;
    //Options ServerInfo;
    byte[] MulticastPrefix4 = {(byte)0,(byte)10,(byte)0,(byte)4,(byte)0,(byte)1,(byte)-120,(byte)106};
    byte[] MulticastPrefix6 = {(byte)0,(byte)10,(byte)0,(byte)5,(byte)0,(byte)2,(byte)-112,(byte)127,(byte)30};
    byte[] SessionId = {(byte)0,(byte)11,(byte)0,(byte)4,(byte)0,(byte)0,(byte)0,(byte)MPP.ran};    

    public byte[] toBytes() throws IOException
    {
    	ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
    	outputStream.write( Type );
    	outputStream.write( Version );
    	outputStream.write( ClientId );
    	if(!MPP.mprefix)
    		outputStream.write( MulticastGroup );
    	else if(!MPP.ipversion6)
    	{
    		outputStream.write( MulticastPrefix4 );
    	}
    	else
    		outputStream.write( MulticastPrefix6 );
    	outputStream.write( SessionId );
    	
    	return outputStream.toByteArray( );
    }
}
