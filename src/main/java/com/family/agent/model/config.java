package com.family.agent.model;

public class config {
	private String serverHost;
    private String agentKey;
    private String deviceId;

	public String getServerHost() {
		return serverHost;
	}
	
	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}



	public String getAgentKey() {
		return agentKey;
	}



	public void setAgentKey(String agentKey) {
		this.agentKey = agentKey;
	}



	public String getDeviceId() {
		return deviceId;
	}



	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}



	@Override public String toString() {
        return "Config{serverHost=" + serverHost + ", deviceId=" + deviceId + "}";
    }
}
