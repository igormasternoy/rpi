package com.masternoy.rpi.server.business;

import org.apache.log4j.Logger;

import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.packet.common.IODataSampleRxIndicatorPacket;
import com.google.inject.Singleton;
import com.masternoy.rpi.server.DeviceCommandQueuer;

@Singleton
public class TemperatureProcessingStrategy implements Strategy<IODataSampleRxIndicatorPacket> {
	private static final Logger log = Logger.getLogger(DeviceCommandQueuer.class);

	@Override
	public void process(IODataSampleRxIndicatorPacket packet) {
		if (!packet.getIOSample().hasAnalogValue(IOLine.DIO3_AD3)) {
			return;
		}
		Double tempAnalogData = packet.getIOSample().getAnalogValue(IOLine.DIO3_AD3).doubleValue();
		Double celcium = (tempAnalogData*1200.0)/1023/10;
		// 3.3)-0.6)*100.0
		log.info("Temperature as readed: " + tempAnalogData+" Converted value: "+celcium);
		

	}

}
