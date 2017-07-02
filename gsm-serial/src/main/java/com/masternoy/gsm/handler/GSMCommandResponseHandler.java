package com.masternoy.gsm.handler;

import java.util.Arrays;

import org.apache.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class GSMCommandResponseHandler extends SimpleChannelInboundHandler<ByteBuf> {
	private static final Logger log = Logger.getLogger(GSMCommandResponseHandler.class);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
		byte[] bytes = new byte[msg.readableBytes()];
		msg.readBytes(bytes);
		
		StringBuilder sb = new StringBuilder();
		for (byte c : bytes) {
			sb.append(String.format("%02x", c).toUpperCase());
		}
		
		log.info("Received by handler: " + Arrays.toString(bytes) + "\n text: " + sb.toString()) ;
	}

}
