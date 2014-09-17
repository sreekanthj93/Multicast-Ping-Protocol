package mpp;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MembershipKey;
import java.util.*;
public class MPP {
	static Random rand = new Random();
	public static int ran = rand.nextInt(1000);
	public static boolean stamp = false,mprefix = false,ipversion6 = false;
    public static void main(String[] args) throws SocketException, UnknownHostException, IOException {
        byte[] b = new byte[100];      
        InetAddress ClientAddr;
        int ClientPort,pktlength=0,i=0;
        InetAddress GroupAddr,GroupAddr6;
        GroupAddr = InetAddress.getByName("234.0.0.1");
        GroupAddr6 = InetAddress.getByName("ff1e:0000:0000:0000:0000:0000:0000:101");
        byte[] ClientId = {0},mgroup = {0},ClientTimeStamp = {0},SequenceNum={0},ServerStamp={0};
        int port = 9903;
        DatagramSocket serverSocket = new DatagramSocket(9902);
        //MulticastSocket serverMCSocket = new MulticastSocket(9903);
        while(true){
	DatagramPacket pkt = new DatagramPacket(b,b.length);
	try {
            boolean Init = false;
            boolean Echo_Request = false;
            boolean mc = false;
            stamp = false;
            ipversion6 = false;
            boolean Sid = true;
            serverSocket.receive(pkt);
            ClientAddr = pkt.getAddress();
            
            
            ClientPort = pkt.getPort();
            pktlength = pkt.getLength();
            
            if((int)b[0]==73) {
                Init = true;
                Echo_Request = false;
                System.out.println("Init Received");
            }
            else if((int)b[0]==81) {
                Init = false;
                Echo_Request = true;
                System.out.println("Echo_Request Received");
            }
            System.out.println("Request Received from "+ClientAddr.getHostAddress());
            for(int ij=0;ij<pkt.getLength();ij++)
            	System.out.print(b[ij]+" ");
            System.out.println("");
            i=1;
            ipversion6 = checkip(ClientAddr);
            while(i<pktlength)
            {
            	switch((int)b[i+1])
            	{
            	    case 0:
            	    	i=i+4+b[i+3];
            	    	break;
            		case 1:
            			ClientId = new byte[b[i+3]+4];
            			ClientId[0] = b[i];
            			ClientId[1] = b[i+1];
            			ClientId[2] = b[i+2];
            			ClientId[3] = b[i+3];
            			for(int a = 0 ; a < (int)b[i+3];a++)
            				ClientId[a+4] = b[a+4+i];
            			i = i+4+b[i+3];
            			break;
            			
            		case 2:
            			SequenceNum = new byte[b[i+3]+4];
            			SequenceNum[0] = b[i];
            			SequenceNum[1] = b[i+1];
            			SequenceNum[2] = b[i+2];
            			SequenceNum[3] = b[i+3];
            			for(int a = 0 ; a < (int)b[i+3];a++)
            				SequenceNum[a+4] = b[a+4+i];
            			i = i+4+b[i+3];
            			break;
            			
            		case 3:
            			ClientTimeStamp = new byte[b[i+3]+4];
            			ClientTimeStamp[0] = b[i];
            			ClientTimeStamp[1] = b[i+1];
            			ClientTimeStamp[2] = b[i+2];
            			ClientTimeStamp[3] = b[i+3];
            			
            			for(int a = 0 ; a < (int)b[i+3];a++)
            				ClientTimeStamp[a+4] = b[a+4+i];
            			i = i+4+b[i+3];
            			break;
            		case 4:
            			mc = true;
            			mgroup = new byte[b[i+3]+4];
            			mgroup[0] = b[i];
            			mgroup[1] = b[i+1];
            			mgroup[2] = b[i+2];
            			mgroup[3] = b[i+3];
            			for(int a = 0 ; a < (int)b[i+3];a++)
            				mgroup[a+4] = b[a+4+i];
            			String gaddr="";
            			int k=0;
            			if(!ipversion6)
            			{
            				for(;k < (int)b[i+3]-3;k++)
            					gaddr = gaddr + Integer.toString((int)mgroup[6+k]+128)+".";
            				gaddr = gaddr + Integer.toString((int)mgroup[6+k]+128);
            				System.out.println(gaddr);
            			}else{
            				for(;k < (int)b[i+3]-3;k=k+2)
            					gaddr = gaddr + Integer.toHexString((int)mgroup[6+k])+Integer.toHexString((int)mgroup[6+k+1])+":";
            				gaddr = gaddr + Integer.toString((int)mgroup[6+k])+Integer.toHexString((int)mgroup[6+k+1]);
            			}
            			GroupAddr = InetAddress.getByName(gaddr);
            			i = i+4+b[i+3];
            			break;
            		case 5:
            			if((int)b[i+5] == 12)
            				stamp = true;
            			i=i+4+b[i+3];
            			break;
            		case 10:
            			mc = true;
            			if(!ipversion6)
            				mgroup = new byte[6+4];
            			else
            				mgroup = new byte[6+16];
            			mgroup[0] = 0;
            			mgroup[1] = 4;
            			mgroup[2] = 0;
            			if(!ipversion6)
            				mgroup[3] = 6;
            			else
            				mgroup[3] = 18;
            			mgroup[4] = b[i+4];
            			mgroup[5] = b[i+5];
            			int aa;
            			if(!ipversion6)
            			{
	            			if(((int)b[i+6]+128)%8==0)
	            				aa = (int)((b[i+6]+128)/8);
	            			else
	            				aa = (int)((b[i+6]+128)/8) + 1;
	            			if((int)b[i+7]!=106)
	            			{
	            				mprefix=true;
	            				mgroup[6]=106;mgroup[7]=-128;mgroup[8]=-128;mgroup[9]=-127;
	
	            			}else{
		            			int a = 0 ;
		            			for(; a < aa;a++)
		            					mgroup[a+6] = b[a+7+i];
		            			if(aa==1)
		            			{
		            				mgroup[7]=-128;mgroup[8]=-128;mgroup[9]=-127;
		            			}
		        				if(aa==2)
		        				{
		        					mgroup[8]=-128;mgroup[9]=-127;
		        				}
		            			if(aa==3)
		            			{
		            				mgroup[9]=-127;
		            			}
		            			}
            			}else{
            				if((int)b[i+6]%8==0)
	            				aa = (int)b[i+6]/8;
	            			else
	            				aa = (int)b[i+6]/8 + 1;
            				if((int)b[i+7]!=127||(int)b[i+8]!=-98)
	            			{
            					System.out.println("sending option 10");
            					int a =0;
	            				mprefix=true;
	            				mgroup[6]=127;mgroup[7]=-98;
	            				for(a=8;a<21;a++)
	            					mgroup[a] = -128;
	            				mgroup[a]=-27;
	            			}else{
		            			int a = 0 ;
		            			for(; a < aa;a++)
		            					mgroup[a+6] = b[a+7+i];
		            			if(aa!=16)
		            				mgroup[22] = -27;
		            			for(;aa<22;aa++)
		            				mgroup[6+aa] = -128;
		            			}
            			}
            			i = i+4+b[i+3];
            			break;
            		case 11:
            			i=i+8;
            			break;
            			
            				
            	}	
            }
            if(!mc){
            	if(!ipversion6)
            	{
            		mgroup = new byte[10];
	            	mgroup[0]=0;mgroup[1]=4;
	            	mgroup[2]=0;mgroup[3]=6;
					mgroup[4]=0;mgroup[5]=1;
	            	mgroup[6]=106;mgroup[7]=-128;
					mgroup[8]=-128;mgroup[9]=-127;
            	}else{
            		mgroup = new byte[22];
            		mgroup[0]=0;mgroup[1]=4;
	            	mgroup[2]=0;mgroup[3]=6;
					mgroup[4]=0;mgroup[5]=2;
					mgroup[6]=127;
					mgroup[7] = -98;
					for(int p = 8;p<21;p++)
						mgroup[p] = -128;
					mgroup[21] = -27;
            	}
            }
            if(Init){
                servres SR = new servres();
                SR.ClientId = ClientId;
                SR.MulticastGroup = mgroup;
                byte[] message = SR.toBytes();
                DatagramPacket packet= new DatagramPacket(message,message.length,ClientAddr,ClientPort);
                serverSocket.send(packet);
                System.out.println("Server_Response Sent");
                for(int ij=0;ij<message.length;ij++)
                	System.out.print(message[ij]+" ");
                System.out.println();
            }
            if(Echo_Request && Sid){
                EchoReply ER = new EchoReply();
                ER.ClientId = ClientId;
                ER.SequenceNum = SequenceNum;
                ER.ClientTimeStamp = ClientTimeStamp;
                ER.MulticastGroup = mgroup;
                if(stamp)
                	ER.ServerTimeStamp = ServerStamp;
                byte[] message = ER.toBytes();
                
                DatagramPacket packet= new DatagramPacket(message,message.length,ClientAddr,ClientPort);
                serverSocket.send(packet);
                System.out.println("Unicast Echo_Reply Sent");
                for(int ij=0;ij<message.length;ij++)
                	System.out.print(message[ij]+" ");
                System.out.println();
                NetworkInterface inter = NetworkInterface.getByName("eth0");
                InetAddress group;
                if(!ipversion6)
                	group = GroupAddr;
                else
                	group = GroupAddr6;
                DatagramChannel channel;
                if(!ipversion6)
                {
	                channel = DatagramChannel.open (StandardProtocolFamily.INET)
	                		.setOption (StandardSocketOptions.SO_REUSEADDR, true)
	                		.bind (new InetSocketAddress(port));
                }else{
                	channel = DatagramChannel.open (StandardProtocolFamily.INET6)
	                		.setOption (StandardSocketOptions.SO_REUSEADDR, true)
	                		.bind (new InetSocketAddress(port));
                }
                System.out.println("addr is "+group);
                MembershipKey key = channel.join(group, inter);
                ByteBuffer buf = ByteBuffer.allocate(message.length);
                buf.clear();
                buf.put(message);
                buf.flip();
                int bytesSent;
                if(!ipversion6)
                	bytesSent = channel.send(buf, new InetSocketAddress(GroupAddr, port));
                else
                	bytesSent = channel.send(buf, new InetSocketAddress(GroupAddr6, port));
                //DatagramPacket mpacket= new DatagramPacket(message,message.length,GroupAddr,9903);
                //serverMCSocket.send(mpacket);
                //System.out.println("addr is : "+GroupAddr.getHostAddress());
                System.out.println("Multicast Echo_Reply Sent");
            }
	} catch (IOException e) {
            System.err.println("Not Recieved");
	}
    }
        //serverSocket.close();
    }
    
    private static boolean checkip(InetAddress ClientAddr)
    {
    	byte[] ipaddr = ClientAddr.toString().getBytes();
    	System.out.println(Integer.toString(ipaddr.length));
    	if(ipaddr.length==13 )
    	{
    		System.out.println("false");
    		return false;
    	}
    	
    	return true;
    }
}
