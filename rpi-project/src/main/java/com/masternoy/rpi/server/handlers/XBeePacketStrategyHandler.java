package com.masternoy.rpi.server.handlers;

import java.util.Set;

import org.apache.log4j.Logger;

import com.digi.xbee.api.packet.common.IODataSampleRxIndicatorPacket;
import com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket;
import com.google.inject.Inject;
import com.masternoy.rpi.server.DeviceCommandQueuer;
import com.masternoy.rpi.server.business.Strategy;
import com.masternoy.rpi.server.protocol.XBeePacketHolder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class XBeePacketStrategyHandler extends SimpleChannelInboundHandler<XBeePacketHolder> {
	private static final Logger log = Logger.getLogger(XBeePacketStrategyHandler.class);

	@Inject
	Set<Strategy> strategies;
	@Inject
	DeviceCommandQueuer queuer;

	@Override
	public void channelRead0(ChannelHandlerContext ctx, XBeePacketHolder msg) throws Exception {
		com.digi.xbee.api.packet.XBeePacket packet = msg.getPacket();
		if (packet instanceof RemoteATCommandResponsePacket) {
			queuer.notifyCommandResponse((RemoteATCommandResponsePacket) packet);
		} else if (packet instanceof IODataSampleRxIndicatorPacket) {
			log.info("Received DATA packet: "+ msg.getPacket().toString());
			strategies.forEach(strat -> strat.process(packet));
		} else {
			log.info("Received unknown packet: " + msg.getPacket().toPrettyString());
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error("Exception caught", cause);
		super.exceptionCaught(ctx, cause);
	}

}
