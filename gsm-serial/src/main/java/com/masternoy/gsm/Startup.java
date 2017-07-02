package com.masternoy.gsm;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.masternoy.gsm.guice.StartupModule;

public class Startup {
	
	public static void main(String[] args) throws InterruptedException {
		//GUICE FIRST
		Injector injector = Guice.createInjector(new StartupModule());
		injector.getInstance(A7GSMCommunicator.class).start();
	}

}
