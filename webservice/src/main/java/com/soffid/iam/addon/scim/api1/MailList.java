package com.soffid.iam.addon.scim.api1;

import java.util.LinkedList;
import java.util.List;

/**
* ValueObject MailList
**/
public class MailList implements java.io.Serializable
{

	/**
	 + The serial version UID of this class. Needed for serialization.
	 */
	private static final long serialVersionUID = 1;
	/**
	 * Attribute name
* Mail list name

	 */
	private java.lang.String name;

	/**
	 * Attribute description
* Mail description

	 */
	private java.lang.String description;

	/**
	 * Attribute domainCode
* Mail domain

	 */
	private java.lang.String domainCode;

	/**
	 * Attribute id

	 */
	private java.lang.Long id;

	/**
	 * Attribute explodedUsersList
* Contains the exploded users list, resolving any group or role membership.

	 */
	private java.lang.String explodedUsersList;

	/**
	 * Attribute lists
* Embeded mail lists

	 */
	private java.lang.String lists;

	/**
	 * Attribute externalList
* External (unmanaged) mail lists that are subscribed to this one

	 */
	private java.lang.String externalList;

	/**
	 * Attribute roleMembers
* Role whose gramtee should be subscribed to this list

	 */
	private java.lang.String roleMembers;

	/**
	 * Attribute groupMembers
* Business units whose membes should be subscribed to this list

	 */
	private java.lang.String groupMembers;

	/**
	 * Attribute usersList
* Contains the users that are directly subscribed to this mail list

	 */
	private java.lang.String usersList;

	/**
	 * Attribute listsBelong
* Mail lists that this one is subscribed to

	 */
	private java.lang.String listsBelong;

	/**
	 * Attribute attributes
* Mail list custom attributes

	 */
	private java.util.Map<java.lang.String,java.lang.Object> attributes = new java.util.HashMap<String,Object>();

	public MailList()
	{
	}

	public MailList(java.lang.String name, java.lang.String description, java.lang.String domainCode, java.lang.Long id, java.lang.String explodedUsersList, java.lang.String lists, java.lang.String externalList, java.lang.String roleMembers, java.lang.String groupMembers, java.lang.String usersList, java.lang.String listsBelong, java.util.Map<java.lang.String,java.lang.Object> attributes)
	{
		super();
		this.name = name;
		this.description = description;
		this.domainCode = domainCode;
		this.id = id;
		this.explodedUsersList = explodedUsersList;
		this.lists = lists;
		this.externalList = externalList;
		this.roleMembers = roleMembers;
		this.groupMembers = groupMembers;
		this.usersList = usersList;
		this.listsBelong = listsBelong;
		this.attributes = attributes;
	}

	public MailList(java.lang.String name)
	{
		super();
		this.name = name;
	}

	public MailList(MailList otherBean)
	{
		this(otherBean.name, otherBean.description, otherBean.domainCode, otherBean.id, otherBean.explodedUsersList, otherBean.lists, otherBean.externalList, otherBean.roleMembers, otherBean.groupMembers, otherBean.usersList, otherBean.listsBelong, otherBean.attributes);
	}

	/**
	 * Gets value for attribute name
	 */
	public java.lang.String getName() {
		return this.name;
	}

	/**
	 * Sets value for attribute name
	 */
	public void setName(java.lang.String name) {
		this.name = name;
	}

	/**
	 * Gets value for attribute description
	 */
	public java.lang.String getDescription() {
		return this.description;
	}

	/**
	 * Sets value for attribute description
	 */
	public void setDescription(java.lang.String description) {
		this.description = description;
	}

	/**
	 * Gets value for attribute domainCode
	 */
	public java.lang.String getDomainCode() {
		return this.domainCode;
	}

	/**
	 * Sets value for attribute domainCode
	 */
	public void setDomainCode(java.lang.String domainCode) {
		this.domainCode = domainCode;
	}

	/**
	 * Gets value for attribute id
	 */
	public java.lang.Long getId() {
		return this.id;
	}

	/**
	 * Sets value for attribute id
	 */
	public void setId(java.lang.Long id) {
		this.id = id;
	}

	/**
	 * Gets value for attribute explodedUsersList
	 */
	public java.lang.String getExplodedUsersList() {
		return this.explodedUsersList;
	}

	/**
	 * Sets value for attribute explodedUsersList
	 */
	public void setExplodedUsersList(java.lang.String explodedUsersList) {
		this.explodedUsersList = explodedUsersList;
	}

	/**
	 * Gets value for attribute lists
	 */
	public java.lang.String getLists() {
		return this.lists;
	}

	/**
	 * Sets value for attribute lists
	 */
	public void setLists(java.lang.String lists) {
		this.lists = lists;
	}

	/**
	 * Gets value for attribute externalList
	 */
	public java.lang.String getExternalList() {
		return this.externalList;
	}

	/**
	 * Sets value for attribute externalList
	 */
	public void setExternalList(java.lang.String externalList) {
		this.externalList = externalList;
	}

	/**
	 * Gets value for attribute roleMembers
	 */
	public java.lang.String getRoleMembers() {
		return this.roleMembers;
	}

	/**
	 * Sets value for attribute roleMembers
	 */
	public void setRoleMembers(java.lang.String roleMembers) {
		this.roleMembers = roleMembers;
	}

	/**
	 * Gets value for attribute groupMembers
	 */
	public java.lang.String getGroupMembers() {
		return this.groupMembers;
	}

	/**
	 * Sets value for attribute groupMembers
	 */
	public void setGroupMembers(java.lang.String groupMembers) {
		this.groupMembers = groupMembers;
	}

	/**
	 * Gets value for attribute usersList
	 */
	public java.lang.String getUsersList() {
		return this.usersList;
	}

	/**
	 * Sets value for attribute usersList
	 */
	public void setUsersList(java.lang.String usersList) {
		this.usersList = usersList;
	}

	/**
	 * Gets value for attribute listsBelong
	 */
	public java.lang.String getListsBelong() {
		return this.listsBelong;
	}

	/**
	 * Sets value for attribute listsBelong
	 */
	public void setListsBelong(java.lang.String listsBelong) {
		this.listsBelong = listsBelong;
	}

	/**
	 * Gets value for attribute attributes
	 */
	public java.util.Map<java.lang.String,java.lang.Object> getAttributes() {
		return this.attributes;
	}

	/**
	 * Sets value for attribute attributes
	 */
	public void setAttributes(java.util.Map<java.lang.String,java.lang.Object> attributes) {
		this.attributes = attributes;
	}

	/**
	 * Returns a string representation of the value object.
	 */
	public String toString()
	{
		StringBuffer b = new StringBuffer();
		b.append (getClass().getName());
		b.append ("[name: ");
		b.append (this.name);
		b.append (", description: ");
		b.append (this.description);
		b.append (", domainCode: ");
		b.append (this.domainCode);
		b.append (", id: ");
		b.append (this.id);
		b.append (", explodedUsersList: ");
		b.append (this.explodedUsersList);
		b.append (", lists: ");
		b.append (this.lists);
		b.append (", externalList: ");
		b.append (this.externalList);
		b.append (", roleMembers: ");
		b.append (this.roleMembers);
		b.append (", groupMembers: ");
		b.append (this.groupMembers);
		b.append (", usersList: ");
		b.append (this.usersList);
		b.append (", listsBelong: ");
		b.append (this.listsBelong);
		b.append (", attributes: ");
		b.append (this.attributes);
		b.append ("]");
		return b.toString();
	}

	private List<String> split(String s) {
		LinkedList<String> l = null;
		if (s != null) {
			l = new LinkedList<String>();
			for (String u: s.split("[ ,]+"))
				l.add(u);
		}
		return l;
	}
	
	public com.soffid.iam.api.MailList toSoffid3Api() {
		com.soffid.iam.api.MailList ml = new com.soffid.iam.api.MailList();
		toSoffid3Api(ml);
		return ml;
	}
	
	public com.soffid.iam.api.MailList toSoffid3Api(com.soffid.iam.api.MailList ml) {
		ml.setAttributes(getAttributes());
		ml.setDescription(description);
		ml.setDomainCode(domainCode);
		ml.setExplodedUsersList( split(explodedUsersList) );
		ml.setExternalList( split( externalList ));
		ml.setGroupMembers(split(groupMembers));
		ml.setId(id);
		ml.setLists(split(lists));
		ml.setListsBelong(listsBelong);
		ml.setName(name);
		ml.setRoleMembers(split(roleMembers));
		ml.setUsersList(split(usersList));
		return ml;
	}

	String merge (List<String> l) {
		String s = null;
		if (l != null) {
			s = "";
			for ( String ll: l) {
				if (!s.isEmpty()) s += " ";
				s += ll;
			}
		}
		return s;
	}
	
	
	public MailList(com.soffid.iam.api.MailList ml3) {
		setAttributes(ml3.getAttributes());
		setDescription(ml3.getDescription());
		setDomainCode(ml3.getDomainCode());
		setExplodedUsersList( merge(ml3.getExplodedUsersList()) );
		setExternalList( merge( ml3.getExternalList() ));
		setGroupMembers(merge(ml3.getGroupMembers()));
		setId(ml3.getId());
		setLists(merge(ml3.getLists()));
		setListsBelong(ml3.getListsBelong());
		setName(ml3.getName());
		setRoleMembers(merge(ml3.getRoleMembers()));
		setUsersList(merge(ml3.getUsersList()));
	}
	
	public MailList fromSoffid3Api(com.soffid.iam.api.MailList ml3) {
		MailList ml = new MailList();
		ml.setAttributes(ml3.getAttributes());
		ml.setDescription(ml3.getDescription());
		ml.setDomainCode(ml3.getDomainCode());
		ml.setExplodedUsersList( merge(ml3.getExplodedUsersList()) );
		ml.setExternalList( merge( ml3.getExternalList() ));
		ml.setGroupMembers(merge(ml3.getGroupMembers()));
		ml.setId(ml3.getId());
		ml.setLists(merge(ml3.getLists()));
		ml.setListsBelong(ml3.getListsBelong());
		ml.setName(ml3.getName());
		ml.setRoleMembers(merge(ml3.getRoleMembers()));
		ml.setUsersList(merge(ml3.getUsersList()));
		return ml;
	}

}
