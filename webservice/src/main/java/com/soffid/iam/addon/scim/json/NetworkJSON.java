package com.soffid.iam.addon.scim.json;

import java.util.ArrayList;
import java.util.Collection;

import com.soffid.iam.api.Network;
import com.soffid.iam.api.NetworkAuthorization;

public class NetworkJSON {

	private static final long serialVersionUID = 1L;
	MetaJSON meta = new MetaJSON();
	/**
	 * Attribute code

	 */
	private java.lang.String code;

	/**
	 * Attribute ip

	 */
	private java.lang.String ip;

	/**
	 * Attribute description

	 */
	private java.lang.String description;

	/**
	 * Attribute mask

	 */
	private java.lang.String mask;

	/**
	 * Attribute lanAccess

	 */
	private java.lang.Boolean lanAccess;

	/**
	 * Attribute dhcp

	 */
	private java.lang.String dhcp;

	/**
	 * Attribute id

	 */
	private java.lang.Long id;

	/**
	 * Attribute dhcpSupport

	 */
	private boolean dhcpSupport;

	Collection<NetworkAuthorization> acls = new ArrayList<NetworkAuthorization>();

	public NetworkJSON() {
	}

	public NetworkJSON(Network network) {
		code = network.getCode();
		description = network.getDescription();
		dhcp = network.getDhcp();
		dhcpSupport = network.isDhcpSupport();
		id = network.getId();
		ip = network.getIp();
		lanAccess = network.getLanAccess();
		mask = network.getMask();
	}

	
	public Network toNetwork() {
		Network n = new Network();
		n.setCode(code);
		n.setDescription(description);
		n.setDhcp(dhcp);
		n.setDhcpSupport(dhcpSupport);
		n.setId(id);
		n.setIp(ip);
		n.setLanAccess(lanAccess);
		n.setMask(mask);
		return n;
	}
	
	public MetaJSON getMeta() {
		return meta;
	}

	public void setMeta(MetaJSON meta) {
		this.meta = meta;
	}

	public Collection<NetworkAuthorization> getAcls() {
		return acls;
	}

	public void setAcls(Collection<NetworkAuthorization> acls) {
		this.acls = acls;
	}

	public java.lang.String getCode() {
		return code;
	}

	public void setCode(java.lang.String code) {
		this.code = code;
	}

	public java.lang.String getIp() {
		return ip;
	}

	public void setIp(java.lang.String ip) {
		this.ip = ip;
	}

	public java.lang.String getDescription() {
		return description;
	}

	public void setDescription(java.lang.String description) {
		this.description = description;
	}

	public java.lang.String getMask() {
		return mask;
	}

	public void setMask(java.lang.String mask) {
		this.mask = mask;
	}

	public java.lang.Boolean getLanAccess() {
		return lanAccess;
	}

	public void setLanAccess(java.lang.Boolean lanAccess) {
		this.lanAccess = lanAccess;
	}

	public java.lang.String getDhcp() {
		return dhcp;
	}

	public void setDhcp(java.lang.String dhcp) {
		this.dhcp = dhcp;
	}

	public java.lang.Long getId() {
		return id;
	}

	public void setId(java.lang.Long id) {
		this.id = id;
	}

	public boolean isDhcpSupport() {
		return dhcpSupport;
	}

	public void setDhcpSupport(boolean dhcpSupport) {
		this.dhcpSupport = dhcpSupport;
	}
}
