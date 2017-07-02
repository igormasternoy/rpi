package com.masternoy.gsm.guice;

import com.masternoy.gsm.handler.GSMCommandResponseHandler;

public interface SerialPortPacketHandlerFactory {

	GSMCommandResponseHandler getATResponseHandler();

}
