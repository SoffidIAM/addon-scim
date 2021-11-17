package com.soffid.iam.addon.scim.json;

import java.util.LinkedList;
import java.util.List;

import com.soffid.iam.api.Host;

public class HostJSON {

	private static final long serialVersionUID = 1L;
	MetaJSON meta = new MetaJSON();

	/**
	 * Attribute id

	 */
	private java.lang.Long id;

	/**
	 * Attribute name

	 */
	private java.lang.String name;

	/**
	 * Attribute os

	 */
	private java.lang.String os;

	/**
	 * Attribute ip

	 */
	private java.lang.String ip;

	/**
	 * Attribute description

	 */
	private java.lang.String description;

	/**
	 * Attribute dhcp

	 */
	private java.lang.String dhcp;

	/**
	 * Attribute mail

	 */
	private java.lang.Boolean mail;

	/**
	 * Attribute office

	 */
	private java.lang.Boolean office;

	/**
	 * Attribute networkCode

	 */
	private java.lang.String networkCode;

	/**
	 * Attribute mac

	 */
	private java.lang.String mac;

	/**
	 * Attribute hostAlias

	 */
	private java.lang.String hostAlias;

	/**
	 * Attribute printersServer

	 */
	private java.lang.Boolean printersServer;

	/**
	 * Attribute serialNumber

	 */
	private java.lang.String serialNumber;

	/**
	 * Attribute dynamicIp

	 */
	private java.lang.Boolean dynamicIp;

	/**
	 * Attribute lastSeen

	 */
	private java.util.Calendar lastSeen;

	public HostJSON() {
	}

	public HostJSON(Host host) {
		this.description = host.getDescription();
		this.dhcp = host.getDhcp();
		this.dynamicIp = host.getDynamicIp();
		this.hostAlias = join (host.getHostAlias());
		this.id = host.getId();
		this.lastSeen = host.getLastSeen();
		this.mac = host.getMac();
		this.mail = host.getMail();
		this.name = host.getName();
		this.networkCode = host.getNetworkCode();
		this.office = host.getOffice();
		this.os = host.getOs();
		this.printersServer = host.getPrintersServer();
		this.serialNumber = host.getSerialNumber();
	}

	public Host toHost() {
		Host h = new Host();
		h.setDescription(getDescription());
		h.setDhcp(getDhcp());
		h.setDynamicIp(getDynamicIp());
		h.setHostAlias(split (getHostAlias()));
		h.setId(getId());
		h.setLastSeen(getLastSeen());
		h.setMac(getMac());
		h.setMail(getMail());
		h.setName(getName());
		h.setNetworkCode(getNetworkCode());
		h.setOffice(getOffice());
		h.setOs(getOs());
		h.setPrintersServer(getPrintersServer());
		h.setSerialNumber(getSerialNumber());
		return h;
	}

	private List<String> split(String hostAlias2) {
		if (hostAlias2 == null)
			return null;
		List<String> l = new LinkedList<>();
		for (String alias: hostAlias2.split(" +"))
			l.add(alias);
		return l ;
	}

	private String join(List<String> hostAlias2) {
		if (hostAlias2 == null) return null;
		String s = "";
		for (String alias: hostAlias2) {
			if (! s.isEmpty()) s += " ";
			s += alias;
		}
		return s;
	}

	public MetaJSON getMeta() {
		return meta;
	}

	public void setMeta(MetaJSON meta) {
		this.meta = meta;
	}

	public java.lang.Long getId() {
		return id;
	}

	public void setId(java.lang.Long id) {
		this.id = id;
	}

	public java.lang.String getName() {
		return name;
	}

	public void setName(java.lang.String name) {
		this.name = name;
	}

	public java.lang.String getDescription() {
		return description;
	}

	public void setDescription(java.lang.String description) {
		this.description = description;
	}

	public java.lang.String getNetworkCode() {
		return networkCode;
	}

	public void setNetworkCode(java.lang.String networkCode) {
		this.networkCode = networkCode;
	}

	public java.lang.String getDhcp() {
		return dhcp;
	}

	public void setDhcp(java.lang.String dhcp) {
		this.dhcp = dhcp;
	}

	public java.lang.String getIp() {
		return ip;
	}

	public void setIp(java.lang.String ip) {
		this.ip = ip;
	}

	public java.lang.String getOs() {
		return os;
	}

	public void setOs(java.lang.String os) {
		this.os = os;
	}

	public java.lang.Boolean getMail() {
		return mail;
	}

	public void setMail(java.lang.Boolean mail) {
		this.mail = mail;
	}

	public java.lang.Boolean getOffice() {
		return office;
	}

	public void setOffice(java.lang.Boolean office) {
		this.office = office;
	}

	public java.lang.String getMac() {
		return mac;
	}

	public void setMac(java.lang.String mac) {
		this.mac = mac;
	}

	public String getHostAlias() {
		return hostAlias;
	}

	public void setHostAlias(String hostAlias) {
		this.hostAlias = hostAlias;
	}

	public java.lang.Boolean getPrintersServer() {
		return printersServer;
	}

	public void setPrintersServer(java.lang.Boolean printersServer) {
		this.printersServer = printersServer;
	}

	public java.lang.Boolean getDynamicIp() {
		return dynamicIp;
	}

	public void setDynamicIp(java.lang.Boolean dynamicIp) {
		this.dynamicIp = dynamicIp;
	}

	public java.lang.String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(java.lang.String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public java.util.Calendar getLastSeen() {
		return lastSeen;
	}

	public void setLastSeen(java.util.Calendar lastSeen) {
		this.lastSeen = lastSeen;
	}
}
