package com.masternoy.rpi.server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.digi.xbee.api.packet.XBeeAPIPacket;
import com.digi.xbee.api.packet.common.RemoteATCommandPacket;
import com.digi.xbee.api.packet.common.RemoteATCommandResponsePacket;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.masternoy.rpi.server.protocol.XBeeCommandRequestListener;
import com.masternoy.rpi.server.protocol.XBeePacketHolder;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

@Singleton
public class DeviceCommandQueuer {
	private static final Logger log = Logger.getLogger(DeviceCommandQueuer.class);

	@Inject
	ConnectionManager connectionManager;

	private Cache<Integer, XBeeCommandRequestListener> commandListeners;

	public DeviceCommandQueuer() {
		commandListeners = CacheBuilder.newBuilder() //
				.expireAfterWrite(10, TimeUnit.SECONDS) //
				.removalListener(new RemovalListener<Integer, XBeeCommandRequestListener>() {
					@Override
					public void onRemoval(RemovalNotification<Integer, XBeeCommandRequestListener> notification) {
						notification.getValue().onFailed(notification.wasEvicted());
					}
				}).build();

		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				commandListeners.cleanUp();
			}
		}, 10, 10, TimeUnit.SECONDS);
	}

	public void put(final XBeeCommandRequestListener commandListener) {
		Integer frameId = commandListener.getRequestID();
		assert frameId != XBeeAPIPacket.NO_FRAME_ID;
		
		commandListeners.put(frameId, commandListener);
		connectionManager.writeToXBeeChannel((RemoteATCommandPacket) commandListener.getRequest()).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (!future.isSuccess()) {
					log.error("FAILED to send command " + commandListener.getRequestID() + " to the device  " + frameId);
					commandListeners.invalidate(frameId);
					//TODO: [imasternoy] Repeat then?
				}
			}
		});
	}

	public void notifyCommandResponse(RemoteATCommandResponsePacket packet) {
		XBeeCommandRequestListener listener = commandListeners.getIfPresent(packet.getFrameID());
		if (listener != null) {
			listener.onSuccess(packet);
			commandListeners.invalidate(packet.getFrameID());
		} else {
			log.warn("WARNING received response, but no listeners found for packet: " + packet);
		}
	}

}
