package com.masternoy.rpi.server;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.masternoy.rpi.server.guice.SerialPortPacketHandlerFactory;
import com.masternoy.rpi.server.protocol.XBeeProtocolReader;
import com.masternoy.rpi.server.protocol.XBeeProtocolWriter;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.rxtx.RxtxChannel;
import io.netty.channel.rxtx.RxtxDeviceAddress;

@Singleton
public class XBeeSerialCommunicator {
	private final static String PORT = "/dev/cu.usbserial-A50285BI";
	private static final Logger log = Logger.getLogger(XBeeSerialCommunicator.class);

	@Inject
	ConnectionManager connectionManager;
	@Inject
	SerialPortPacketHandlerFactory factory;

	public void start() throws InterruptedException {
		// NETTY
		EventLoopGroup group = new OioEventLoopGroup();
		log.info("Starting XBee communication");
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channelFactory(new ChannelFactory()).//
					handler(new ChannelInitializer<RxtxChannel>() {
						@Override
						public void initChannel(RxtxChannel ch) throws Exception {
							connectionManager.setXBeeCtx(ch);
							ch.pipeline().addLast(//
									new XBeeProtocolReader(), //
									new XBeeProtocolWriter(),
									// factory.getResponseHandler(),
									factory.getPacketHandler());
						}
					});

			ChannelFuture f = b.connect(new RxtxDeviceAddress(PORT)).sync();
			log.info("Connected successfuly to: " + PORT);
			f.channel().closeFuture().sync();
		} finally {
			log.info("Shuting down XBee comunication");
			group.shutdownGracefully();
		}
	}

}
