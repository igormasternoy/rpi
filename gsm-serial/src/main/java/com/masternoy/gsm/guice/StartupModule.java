package com.masternoy.gsm.guice;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.masternoy.gsm.handler.GSMCommandResponseHandler;

public class StartupModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new FactoryModuleBuilder().implement(GSMCommandResponseHandler.class, GSMCommandResponseHandler.class)
				.build(SerialPortPacketHandlerFactory.class));
	}

}
