package com.masternoy.rpi.server.protocol;

import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.packet.XBeePacketParser;
import com.digi.xbee.api.packet.common.ATCommandResponsePacket;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ByteProcessor;

/**
 * @author imasternoy
 */
public class XBeeProtocolReader extends ByteToMessageDecoder {
	private static final Logger log = Logger.getLogger(XBeeProtocolReader.class);

	XBeePacketHolder packet;
	State currentState;

	private enum State {
		START, READ_BODY_LENGTH, READ_BODY, END
	}

	public XBeeProtocolReader() {
		currentState = State.START;
	}

	@Override
	protected final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		Object decoded = decode(ctx, in);
		if (decoded != null) {
			out.add(decoded);
		}
	}

	protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
		if (currentState == State.START) {
			final int eol = findPacketStart(buffer);
			if (eol < 0) {
				log.trace("Packet without signature found");
				return null;
			}
			log.trace("Reader index:" + buffer.readerIndex());
			buffer.readerIndex(eol + 1);
			log.trace("Reader index:" + buffer.readerIndex());
			currentState = State.READ_BODY_LENGTH;
		}

		if (currentState == State.READ_BODY_LENGTH) {
			if (buffer.readableBytes() < 2) {
				log.trace("Not enough bytes to construct packet length");
				return null;
			}
			packet = new XBeePacketHolder();
			// needed for test purposes
			byte[] low = new byte[1];
			byte[] high = new byte[1];
			low[0] = buffer.readByte();
			high[0] = buffer.readByte();
			log.trace(Hex.encodeHexString(low) + "" + Hex.encodeHexString(high));
			//Checksum is the always the last byte
			packet.setLength((short) (assemblyShort(low[0], high[0])));
			currentState = State.READ_BODY;
		}

		if (currentState == State.READ_BODY) {
			int length = packet.getLength() +1; //length + checkSumByte
			// LENGTH IS KNOWN
			if (buffer.readableBytes() < length) {
				return null;
			}
			ByteBuf buf = Unpooled.buffer(3+length); //1 byte - signature; 2 bytes - length
			buf.writeByte(Constants.Protocol.SIGNATURE);
			buf.writeShort(packet.getLength());
			buffer.readBytes(buf, buf.writerIndex(), length);
			XBeePacketParser parser = new XBeePacketParser();
			com.digi.xbee.api.packet.XBeePacket parsedPacket = parser.parsePacket(buf.array(), OperatingMode.API);
			packet.setPacket(parsedPacket);
			currentState = State.START;
			buffer.discardReadBytes();
			return packet;
		}
		return null;
	}

	private static short assemblyShort(int i2, int i1) {
		return (short) (i1 + (i2 << 8));
	}

	private static int findPacketStart(final ByteBuf buffer) {
		return buffer.forEachByte(new ByteProcessor.IndexOfProcessor(Constants.Protocol.SIGNATURE));
	}

}
