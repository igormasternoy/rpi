package com.masternoy.rpi.server.business;

import org.apache.log4j.Logger;

import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOValue;
import com.digi.xbee.api.packet.common.IODataSampleRxIndicatorPacket;
import com.digi.xbee.api.packet.common.RemoteATCommandPacket;
import com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.masternoy.rpi.server.DeviceCommandQueuer;
import com.masternoy.rpi.server.protocol.Constants;
import com.masternoy.rpi.server.protocol.XBeeCommandRequestListener;

/**
 * If motion sensor triggered switch sleeping mode to pin wake up; Switch back
 * when it finished.
 * 
 * @author imasternoy
 *
 */
@Singleton
public class AlertMotionStrategy implements Strategy<IODataSampleRxIndicatorPacket> {
	private static final int REPEAT_MESSAGE_TIMES = 3;

	private static final Logger log = Logger.getLogger(DeviceCommandQueuer.class);

	@Inject
	DeviceCommandQueuer commandQueuer;

	/**
	 * Currently for each packet send on/off DIO3
	 * 
	 * @param packet
	 */
	@Override
	public void process(IODataSampleRxIndicatorPacket packet) {
		RemoteATCommandPacket command = new RemoteATCommandPacket(getFrameId(), packet.get64bitSourceAddress(),
				packet.get16bitSourceAddress(), 2, "D3", new byte[] { Constants.HIGH });
		if (packet.getIOSample().hasDigitalValue(IOLine.DIO3_AD3)) {
			IOValue value = packet.getIOSample().getDigitalValue(IOLine.DIO3_AD3);
			if (value == IOValue.HIGH) {
				command.setParameter(new byte[] { Constants.LOW });
			} else {
				command.setParameter(new byte[] { Constants.HIGH });
			}
		}
		commandQueuer.put(new RepeatingListener(command));
	}


}
