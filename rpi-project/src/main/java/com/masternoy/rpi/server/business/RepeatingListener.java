package com.masternoy.rpi.server.business;

import org.apache.log4j.Logger;

import com.digi.xbee.api.packet.common.RemoteATCommandPacket;
import com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket;
import com.google.inject.Inject;
import com.masternoy.rpi.server.DeviceCommandQueuer;
import com.masternoy.rpi.server.protocol.XBeeCommandRequestListener;

public class RepeatingListener extends XBeeCommandRequestListener {
	private static final Logger log = Logger.getLogger(RepeatingListener.class);
	private static final int REPEAT_MESSAGE_TIMES = 3;

	int repeatCounter = 0;

	@Inject
	DeviceCommandQueuer commandQueuer;

	public RepeatingListener(RemoteATCommandPacket request) {
		super(request);
	}

	@Override
	public void onSuccess(RemoteATCommandResponsePacket packet) {
		log.debug("Received response" + packet.toPrettyString() + " for command" + command.toString());
	}

	@Override
	public void onFailed(boolean wasEvicted) {
		if (wasEvicted) {
			log.warn("TIMEOUT waiting for response try Num " + repeatCounter + " command" + command);
			repeatCounter++;
			if (repeatCounter < REPEAT_MESSAGE_TIMES) {
				commandQueuer.put(this);
			}
		}
	}

}
