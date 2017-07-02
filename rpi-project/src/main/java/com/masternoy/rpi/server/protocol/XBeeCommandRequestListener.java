package com.masternoy.rpi.server.protocol;

import com.digi.xbee.api.packet.common.RemoteATCommandPacket;
import com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket;

/**
 * Callback listener on successful command execution
 * 
 * @author imasternoy
 *
 */
public abstract class XBeeCommandRequestListener {

	protected RemoteATCommandPacket command;

	public XBeeCommandRequestListener(RemoteATCommandPacket remoteCommand) {
		this.command = remoteCommand;
	}

	/**
	 * Method with a result of successful command
	 * 
	 * @param packet
	 */
	public abstract void onSuccess(RemoteATCommandResponsePacket packet);

	/**
	 * Method with a result of failed command
	 * 
	 * @param wasEvicted
	 */
	public abstract void onFailed(boolean wasEvicted);
	
	public RemoteATCommandPacket getRequest() {
		return command;
	}

	public Integer getRequestID() {
		return command.getFrameID();
	}

}
