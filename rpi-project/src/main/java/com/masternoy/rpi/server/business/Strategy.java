package com.masternoy.rpi.server.business;

import java.util.Random;

import com.digi.xbee.api.packet.XBeePacket;

public interface Strategy<T extends XBeePacket> {

	public void process(T packet);
	
	default public int getFrameId() {
		byte rnd = (byte) new Random().nextInt();
		return rnd & 0xFF;
	}

}
