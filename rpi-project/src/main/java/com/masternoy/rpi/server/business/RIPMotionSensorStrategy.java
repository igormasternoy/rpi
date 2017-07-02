package com.masternoy.rpi.server.business;

import org.apache.log4j.Logger;

import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOValue;
import com.digi.xbee.api.models.RemoteATCommandOptions;
import com.digi.xbee.api.packet.common.IODataSampleRxIndicatorPacket;
import com.digi.xbee.api.packet.common.RemoteATCommandPacket;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.masternoy.rpi.server.DeviceCommandQueuer;
import com.masternoy.rpi.server.protocol.Constants;

@Singleton
public class RIPMotionSensorStrategy implements Strategy<IODataSampleRxIndicatorPacket>, AlarmStrategy {
	private static final Logger log = Logger.getLogger(DeviceCommandQueuer.class);
	@Inject
	DeviceCommandQueuer commandQueuer;

	boolean detected = false;

	@Override
	public void process(IODataSampleRxIndicatorPacket packet) {
		if (!packet.getIOSample().hasDigitalValue(IOLine.DIO3_AD3)) {
			return;
		}
		IOValue value = packet.getIOSample().getDigitalValue(IOLine.DIO3_AD3);
		if (value == IOValue.HIGH) {
			if (detected == false) {
				log.info("!!!Motion detected!!!");
				RemoteATCommandPacket command = new RemoteATCommandPacket(getFrameId(), //
						packet.get64bitSourceAddress(), //
						packet.get16bitSourceAddress(), //
						RemoteATCommandOptions.OPTION_APPLY_CHANGES, "SM", //
						new byte[] { Constants.DISABLE }); //
				commandQueuer.put(new RepeatingListener(command));
				detected = true;
			}
		} else if (value == IOValue.LOW) {
			if (detected == true) {
				detected = false;
				RemoteATCommandPacket command = new RemoteATCommandPacket(getFrameId(), //
						packet.get64bitSourceAddress(), //
						packet.get16bitSourceAddress(), //
						RemoteATCommandOptions.OPTION_APPLY_CHANGES, "SM", //
						new byte[] { Constants.HIGH });
				commandQueuer.put(new RepeatingListener(command));
				log.info("!!!Motion finisihed!!!");
			}
		}

	}

}
