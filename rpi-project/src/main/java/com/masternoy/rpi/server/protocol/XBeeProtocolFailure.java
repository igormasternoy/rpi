package com.masternoy.rpi.server.protocol;

public class XBeeProtocolFailure extends Exception {
	private static final long serialVersionUID = 1L;

	public XBeeProtocolFailure(String message) {
		super(message);
	}
}
