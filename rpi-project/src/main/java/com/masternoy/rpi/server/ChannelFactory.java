package com.masternoy.rpi.server;

import io.netty.channel.rxtx.RxtxChannel;
import io.netty.channel.rxtx.RxtxChannelConfig.Databits;
import io.netty.channel.rxtx.RxtxChannelConfig.Paritybit;
import io.netty.channel.rxtx.RxtxChannelConfig.Stopbits;

public class ChannelFactory implements io.netty.channel.ChannelFactory<RxtxChannel> {

	public RxtxChannel newChannel() {
		return createChannel();
	}

	private static RxtxChannel createChannel() {
		RxtxChannel channel = new RxtxChannel();
		channel.config().setBaudrate(9600);
		channel.config().setDatabits(Databits.DATABITS_8);
		channel.config().setStopbits(Stopbits.STOPBITS_1);
		channel.config().setParitybit(Paritybit.NONE);
		return channel;
	}

}
