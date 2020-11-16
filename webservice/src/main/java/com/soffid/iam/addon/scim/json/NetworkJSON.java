package com.soffid.iam.addon.scim.json;

import java.util.ArrayList;
import java.util.Collection;

import com.soffid.iam.api.Network;
import com.soffid.iam.api.NetworkAuthorization;

public class NetworkJSON extends Network {

	private static final long serialVersionUID = 1L;
	MetaJSON meta = new MetaJSON();
	Collection<NetworkAuthorization> acls = new ArrayList<NetworkAuthorization>();

	public NetworkJSON() {
	}

	public NetworkJSON(Network network) {
		super (network);
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
}
