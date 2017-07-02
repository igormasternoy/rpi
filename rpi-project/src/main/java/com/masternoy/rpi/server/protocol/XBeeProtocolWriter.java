package com.masternoy.rpi.server.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import static com.masternoy.rpi.server.protocol.Constants.Protocol.*;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

import com.digi.xbee.api.packet.common.RemoteATCommandPacket;

/**
 * Writing messages @see XBeeCommandRequest back to serial port
 * 
 * 
 * @author imasternoy
 *
 */
public class XBeeProtocolWriter extends MessageToByteEncoder<RemoteATCommandPacket> {
	private static final Logger log = Logger.getLogger(XBeeProtocolWriter.class);

	@Override
	protected void encode(ChannelHandlerContext ctx, RemoteATCommandPacket msg, ByteBuf out) throws Exception {
		out.writeBytes(msg.generateByteArray());
		log.debug("Message sent with payload : " + msg.toPrettyString());
	}

}
