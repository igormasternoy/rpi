package com.masternoy.rpi.server;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.masternoy.rpi.server.guice.StartupModule;

public class Startup {
	
	public static void main(String[] args) throws InterruptedException {
		//GUICE FIRST
		Injector injector = Guice.createInjector(new StartupModule());
		injector.getInstance(XBeeSerialCommunicator.class).start();
	
	}

}
