package dev.voras.common.openstack.manager.internal;

import dev.voras.common.ipnetwork.IIpHost;

public class OpenstackIpHost implements IIpHost {
	
	private final String hostname;
	
	public OpenstackIpHost(String hostname) {
		this.hostname = hostname;
	}

	@Override
	public String getHostname() {
		return this.hostname;
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
