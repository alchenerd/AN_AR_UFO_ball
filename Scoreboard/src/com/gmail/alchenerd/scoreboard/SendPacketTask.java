package com.gmail.alchenerd.scoreboard;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

public class SendPacketTask extends AsyncTask<String, Void, Void> {
	
	int dstPort;
	WifiManager wifiStatus;
	SendPacketTask(int port, WifiManager wifi){
		dstPort = port;
		wifiStatus = wifi;
		}

	@Override
	protected Void doInBackground(String... params) {
		// TODO Auto-generated method stub
		try {
			DatagramSocket sds = new DatagramSocket(0);
			sds.setBroadcast(true);
			String packedMessage = ""+(wifiStatus.getConnectionInfo().getIpAddress()>>24&0xff)+" 255 06 "+params[0]+" 0 0 0 0 0";
			DatagramPacket dp = new DatagramPacket(packedMessage.getBytes(), packedMessage.getBytes().length, getBroadcastAddress(), dstPort);
			sds.send(dp);//something was wrong here
			sds.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
		return null;
		
	}

	private InetAddress getBroadcastAddress() throws IOException {
		// TODO Auto-generated method stub
		DhcpInfo dhcp = wifiStatus.getDhcpInfo();
	    // handle null somehow

	    int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
	    byte[] quads = new byte[4];
	    for (int k = 0; k < 4; k++)
	      quads[3-k] = (byte) ((broadcast >> (k * 8)) & 0xFF);
	    //return InetAddress.getByAddress(quads);
	    return InetAddress.getByName("255.255.255.255");
	}

}
