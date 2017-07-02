package com.masternoy.gsm.server;

import com.google.inject.Singleton;

import io.netty.channel.rxtx.RxtxChannel;

@Singleton
public class GSMConnectionManager {
	RxtxChannel gsmCtx;
	
	public RxtxChannel getGsmCtx() {
		return gsmCtx;
	}

	public void setGsmCtx(RxtxChannel ctx) {
		this.gsmCtx = ctx;
	}
}
