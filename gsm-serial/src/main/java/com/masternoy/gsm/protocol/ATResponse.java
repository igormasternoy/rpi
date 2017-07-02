package com.masternoy.gsm.protocol;

public class ATResponse {
	byte[] response;

	public ATResponse() {
	}
	
	public void setResponse(byte[] response) {
		this.response = response;
	}
	
	public byte[] getResponse() {
		return response;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (byte c : response) {
			sb.append(String.format("%02x", c).toUpperCase());
		}
		return sb.toString();
	}

}
