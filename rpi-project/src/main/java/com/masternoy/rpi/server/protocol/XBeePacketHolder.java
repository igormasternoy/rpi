package com.masternoy.rpi.server.protocol;

import com.digi.xbee.api.packet.XBeePacket;

public class XBeePacketHolder {
	private short length = -1;
	private XBeePacket packet = null;

	public void setPacket(com.digi.xbee.api.packet.XBeePacket packet) {
		this.packet = packet;
	}

	public com.digi.xbee.api.packet.XBeePacket getPacket() {
		return packet;
	}

	public void setLength(short length) {
		this.length = length;
	}

	public short getLength() {
		return length;
	}

	public byte[] getPayload() {
		return packet.generateByteArray();
	}

	@Override
	public String toString() {
		return packet.toPrettyString();
	}

}
