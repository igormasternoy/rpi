package com.masternoy.gsm;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.masternoy.gsm.guice.SerialPortPacketHandlerFactory;
import com.masternoy.gsm.server.ChannelFactory;
import com.masternoy.gsm.server.GSMConnectionManager;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.rxtx.RxtxChannel;
import io.netty.channel.rxtx.RxtxDeviceAddress;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;

public class A7GSMCommunicator {
	private static final Logger log = Logger.getLogger(A7GSMCommunicator.class);
	private final static String PORT = "/dev/ttyS80";

	@Inject
	GSMConnectionManager connectionManager;

	@Inject
	SerialPortPacketHandlerFactory factory;

	public void start() throws InterruptedException {
		// GSM
		EventLoopGroup group = new OioEventLoopGroup();
		log.info("Starting GSM communicator");
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channelFactory(new ChannelFactory()).//
					handler(new ChannelInitializer<RxtxChannel>() {
						@Override
						public void initChannel(RxtxChannel ch) throws Exception {
							connectionManager.setGsmCtx(ch);
							ch.pipeline().addLast(//
									new DelimiterBasedFrameDecoder(258,
											Unpooled.wrappedBuffer(new byte[] { '\r', '\n' })),
									// new A7GSMMessageReader(),
									factory.getATResponseHandler()
							// factory.getPacketHandler()
							);
						}
					});

			ChannelFuture f = b.connect(new RxtxDeviceAddress(PORT)).sync();
			log.info("Connected successfuly to: " + PORT);
			f.channel().closeFuture().sync();
		} finally {
			group.shutdownGracefully();
		}
	}

}
