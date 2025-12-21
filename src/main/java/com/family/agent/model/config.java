package com.family.agent.model;

public class config {
	private String serverUrl;
    private String agentKey;
    private String deviceId;

    

    public String getServerUrl() {
		return serverUrl;
	}



	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
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
        return "Config{serverUrl=" + serverUrl + ", deviceId=" + deviceId + "}";
    }
}
