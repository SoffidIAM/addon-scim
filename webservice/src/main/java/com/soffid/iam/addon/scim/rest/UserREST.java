package com.soffid.iam.addon.scim.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.soffid.iam.addon.scim.json.SecondaryGroupJSON;
import com.soffid.iam.addon.scim.json.MetaJSON;
import com.soffid.iam.addon.scim.json.UserAccountJSON;
import com.soffid.iam.addon.scim.json.UserJSON;
import com.soffid.iam.addon.scim.response.SCIMResponseBuilder;
import com.soffid.iam.addon.scim.response.SCIMResponseList;
import com.soffid.iam.addon.scim.util.PATCHAnnotation;
import com.soffid.iam.addon.scim.util.PaginationUtil;
import com.soffid.iam.api.GroupUser;
import com.soffid.iam.api.PagedResult;
import com.soffid.iam.api.Password;
import com.soffid.iam.api.User;
import com.soffid.iam.api.UserAccount;
import com.soffid.iam.api.UserData;
import com.soffid.iam.service.ejb.AccountService;
import com.soffid.iam.service.ejb.AdditionalDataService;
import com.soffid.iam.service.ejb.ApplicationService;
import com.soffid.iam.service.ejb.DispatcherService;
import com.soffid.iam.service.ejb.GroupService;
import com.soffid.iam.service.ejb.UserService;

import es.caib.seycon.ng.exception.AccountAlreadyExistsException;
import es.caib.seycon.ng.exception.InternalErrorException;
import es.caib.seycon.ng.exception.NeedsAccountNameException;
import es.caib.seycon.util.Base64;

@Path("/scim/User")
@Produces({"application/scim+json", "application/json"})
@Consumes({"application/scim+json", "application/json"})
@ServletSecurity(@HttpConstraint(rolesAllowed = {"scim:invoke"}))
public class UserREST {
	Log log = LogFactory.getLog(getClass());
	
	static final String RESOURCE = "User";
	@EJB UserService userService;
	@EJB AccountService accountService;
	@EJB ApplicationService apllicationService;
	@EJB DispatcherService dispatcherService;
	@EJB AdditionalDataService dataService;
	@EJB GroupService groupService;

	@Path("")
	@GET
	public Response list(@QueryParam("filter") @DefaultValue("") String filter, @QueryParam("attributes") String atts,
			@QueryParam("startIndex") @DefaultValue("") String startIndex, @QueryParam("count") @DefaultValue("") String count)
			throws InternalErrorException {

		PaginationUtil p = new PaginationUtil(startIndex, count);
		if (p.getCount() <= 0 || p.getCount() > 1000) p.setCount(1000);
		PagedResult r = userService.findUserByJsonQuery(filter, p.getStartIndex(), p.getCount());
		return SCIMResponseBuilder.responseList(new SCIMResponseList(toExtendedUserList(r.getResources()), r));
	}

	@Path("")
	@POST
	public Response create(UserJSON user, @Context HttpServletRequest request) throws URISyntaxException {
		try {
			User newUser = userService.create(user);
			if (newUser != null) {
				updateAccounts(user, newUser);
				updateAttributes(user, newUser, true);
				updateSecondaryGroups(user, newUser);
				if (user.getPassword() != null) 
					userService.setTemporaryPassword(user.getUserName(), "DEFAULT", new Password(user.getPassword())); //$NON-NLS-1$
				UserJSON eu = toExtendedUser(newUser);
				return SCIMResponseBuilder.responseOk(eu, new URI(eu.getMeta().getLocation()));
			} else
				return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
		} catch (EJBException e) {
			LogFactory.getLog(getClass()).warn("Error creating user", e);
			return SCIMResponseBuilder.errorCustom(Status.CONFLICT, e);
		} catch (RuntimeException e) {
			e.printStackTrace();
			return SCIMResponseBuilder.errorGeneric(e);
		} catch (Exception e) {
			e.printStackTrace();
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	private void updateSecondaryGroups(UserJSON src, User target) throws InternalErrorException {
		Collection<GroupUser> groups = groupService.findUsersGroupByUserName(target.getUserName());
		for (GroupUser ug : groups) {
			boolean found = false;
			for (SecondaryGroupJSON ug2 : src.getSecondaryGroups()) {
				if (ug2.getGroup() != null && ug2.getGroup().equals(ug.getGroup()))
				{
					found = true;
					break;
				}
			}
			if (!found) {
				groupService.delete(ug);
			}
		}
		for (SecondaryGroupJSON ug2 : src.getSecondaryGroups()) {
			boolean found = false;
			{
				for (GroupUser ug : groups)
				{
					
					if (ug2.getGroup() != null && ug2.getGroup().equals(ug.getGroup()))
					{
						found = true;
						break;
					}
				}
			}
			if (!found) {
				groupService.addGroupToUser(target.getUserName(), ug2.getGroup());
			}
		}
	}

	@Path("/{id}")
	@GET
	public Response show(@PathParam("id") long id) {
		User user;
		try {
			user = userService.findUserByUserId(id);
			if (user != null)
				return SCIMResponseBuilder.responseOk(toExtendedUser(user));
			else
				return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
		} catch (Exception e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	@Path("/{id}")
	@DELETE
	public Response delete(@PathParam("id") long id) {
		User user;
		try {
			user = userService.findUserByUserId(id);
			if (user != null) {
				userService.delete(user);
				return SCIMResponseBuilder.responseOnlyHTTP(Status.NO_CONTENT);
			} else {
				return SCIMResponseBuilder.errorCustom(Status.NOT_FOUND, "UserSvc.userNotFound", id); //$NON-NLS-1$
			}
		} catch (Exception e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	@Path("/{id}")
	@PUT
	public Response update(@PathParam("id") long id, UserJSON user) {
		User user2;
		try {
			user2 = userService.findUserByUserId(id);
			if (user2 == null) return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
			if (null != user.getId() && id != user.getId())
				return SCIMResponseBuilder.errorCustom(Status.NOT_FOUND, "UserSvc.userNotEquals", id, user.getId()); //$NON-NLS-1$

			user2.setActive(user.getActive());
			user2.setComments(user.getComments());
			user2.setCreatedByUser(user.getCreatedByUser());
			user2.setCreatedDate(user.getCreatedDate());
			user2.setFirstName(user.getFirstName());
			user2.setFullName(user.getFullName());
			user2.setHomeServer(user.getHomeServer());
			user2.setId(user.getId());
			user2.setLastName(user.getLastName());
			user2.setMailAlias(user.getMailAlias());
			user2.setMailDomain(user.getMailDomain());
			user2.setMailServer(user.getMailServer());
			user2.setMailDomain(user.getMailDomain());
			user2.setMailServer(user.getMailServer());
			user2.setMiddleName(user.getMiddleName());
			user2.setModifiedByUser(user.getModifiedByUser());
			user2.setModifiedDate(user.getModifiedDate());
			user2.setMultiSession(user.getMultiSession());
			user2.setPrimaryGroup(user.getPrimaryGroup());
			user2.setPrimaryGroupDescription(user.getPrimaryGroupDescription());
			user2.setProfileServer(user.getProfileServer());
			user2.setShortName(user.getShortName());
			user2.setUserName(user.getUserName());
			user2.setUserType(user.getUserType());
			userService.update(user2);
			if (user.getPassword() != null) userService.setTemporaryPassword(user.getUserName(), "DEFAULT", new Password(user.getPassword())); //$NON-NLS-1$
			updateAttributes(user, user2, true);
			updateAccounts(user, user2);
			updateSecondaryGroups(user, user2);
			return SCIMResponseBuilder.responseOk(toExtendedUser(user2));
		} catch (Exception e) {
			log.warn("Error updating user", e);
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	@Path("/{id}")
	@PATCHAnnotation
	public Response patch(@PathParam("id") long id, UserJSON user) {
		User user2;
		try {
			user2 = userService.findUserByUserId(id);
			if (user2 == null) return SCIMResponseBuilder.responseOnlyHTTP(Status.NOT_FOUND);
			if (null != user.getId() && id != user.getId())
				return SCIMResponseBuilder.errorCustom(Status.NOT_FOUND, "UserSvc.userNotEquals", id, user.getId()); //$NON-NLS-1$

			if (user.getActive() != null) user2.setActive(user.getActive());
			if (user.getComments() != null) user2.setComments(user.getComments());
			if (user.getCreatedByUser() != null) user2.setCreatedByUser(user.getCreatedByUser());
			if (user.getCreatedDate() != null) user2.setCreatedDate(user.getCreatedDate());
			if (user.getFirstName() != null) user2.setFirstName(user.getFirstName());
			if (user.getFullName() != null) user2.setFullName(user.getFullName());
			if (user.getHomeServer() != null) user2.setHomeServer(user.getHomeServer());
			if (user.getLastName() != null) user2.setLastName(user.getLastName());
			if (user.getMailAlias() != null) user2.setMailAlias(user.getMailAlias());
			if (user.getMailDomain() != null) user2.setMailDomain(user.getMailDomain());
			if (user.getMailServer() != null) user2.setMailServer(user.getMailServer());
			if (user.getMailDomain() != null) user2.setMailDomain(user.getMailDomain());
			if (user.getMailServer() != null) user2.setMailServer(user.getMailServer());
			if (user.getMiddleName() != null) user2.setMiddleName(user.getMiddleName());
			if (user.getModifiedByUser() != null) user2.setModifiedByUser(user.getModifiedByUser());
			if (user.getModifiedDate() != null) user2.setModifiedDate(user.getModifiedDate());
			if (user.getMultiSession() != null) user2.setMultiSession(user.getMultiSession());
			if (user.getPrimaryGroup() != null) user2.setPrimaryGroup(user.getPrimaryGroup());
			if (user.getPrimaryGroupDescription() != null) user2.setPrimaryGroupDescription(user.getPrimaryGroupDescription());
			if (user.getProfileServer() != null) user2.setProfileServer(user.getProfileServer());
			if (user.getShortName() != null) user2.setShortName(user.getShortName());
			if (user.getUserName() != null) user2.setUserName(user.getUserName());
			if (user.getUserType() != null) user2.setUserType(user.getUserType());
			userService.update(user2);

			if (user.getPassword() != null) userService.setTemporaryPassword(user2.getUserName(), "DEFAULT", new Password(user.getPassword())); //$NON-NLS-1$
			if (!user.getAccounts().isEmpty()) updateAccounts(user, user2);
			if (!user.getAttributes().isEmpty()) updateAttributes(user, user2, false);
			if (user.getSecondaryGroups() != null && ! user.getSecondaryGroups().isEmpty())
				updateSecondaryGroups(user, user2);

			return SCIMResponseBuilder.responseOk(toExtendedUser(user2));
		} catch (Exception e) {
			return SCIMResponseBuilder.errorGeneric(e);
		}
	}

	private Collection<Object> toExtendedUserList(Collection<User> userList) throws InternalErrorException {
		LinkedList<Object> extendedUserList = new LinkedList<Object>();
		if (null != userList && !userList.isEmpty()) {
			for (User user : userList) {
				extendedUserList.add(toExtendedUser(user));
			}
		}
		return extendedUserList;
	}

	private UserJSON toExtendedUser(User u) throws InternalErrorException {

		// Final user to include in the response
		UserJSON eu = new UserJSON(u);

		// Include user attributes
		for (UserData data : userService.findUserDataByUserName(u.getUserName())) {
			if (data.getDateValue() != null)
				eu.getAttributes().put(data.getAttribute(), data.getDateValue().getTime());
			else if (data.getBlobDataValue() != null)
				eu.getAttributes().put(data.getAttribute(), Base64.encodeBytes(data.getBlobDataValue(), Base64.DONT_BREAK_LINES));
			else
				eu.getAttributes().put(data.getAttribute(), data.getValue());
		}

		// Include user accounts
		for (UserAccount acc : accountService.getUserAccounts(u)) {
			UserAccountJSON js = new UserAccountJSON();
			js.setName(acc.getName());
			js.setId(acc.getId());
			js.setSystem(acc.getSystem());
			eu.getAccounts().add(js);
		}

		// Include user accounts
		MetaJSON meta = eu.getMeta();
		if (u.getId()!=null) meta.setLocation(getClass(), u.getId().toString());
		if (u.getCreatedDate()!=null) meta.setCreated(u.getCreatedDate().getTime());
		if (u.getModifiedDate()!=null) meta.setLastModified(u.getModifiedDate().getTime());
		meta.setResourceType(RESOURCE);

		// Include secondary groups
		List<SecondaryGroupJSON> secondaryGroupsList = new LinkedList<SecondaryGroupJSON>();
		for (GroupUser secondaryGroup : groupService.findUsersGroupByUserName(eu.getUserName())) {
			secondaryGroupsList.add(new SecondaryGroupJSON(secondaryGroup));
		}
		eu.setSecondaryGroups(secondaryGroupsList);

		return eu;
	}

	private void updateAccounts(UserJSON src, User target)
			throws InternalErrorException, NeedsAccountNameException, AccountAlreadyExistsException {
		Collection<com.soffid.iam.api.UserAccount> accounts = accountService.getUserAccounts(target);
		for (UserAccount ua : accounts) {
			boolean found = false;
			for (UserAccountJSON ua2 : src.getAccounts()) {
				if (ua2.getId() == ua.getId().longValue()) {
					found = true;
					break;
				}
			}
			if (!found) {
				accountService.removeAccount(ua);
			}
		}
		for (UserAccountJSON ua2 : src.getAccounts()) {
			boolean found = false;
			{
				for (UserAccount ua : accounts)
					if (ua2.getId() == ua.getId().longValue()) {
						found = true;
						break;
					}
			}
			if (!found) {
				accountService.createAccount(target, dispatcherService.findDispatcherByName(ua2.getSystem()), ua2.getName());
			}
		}
	}

	private void updateAttributes(UserJSON src, User target, boolean delete)
			throws InternalErrorException, NeedsAccountNameException, AccountAlreadyExistsException {
		Collection<UserData> atts = userService.findUserDataByUserName(target.getUserName());
		for (UserData ua : atts) {
			Object value = null;
			if (src.getAttributes()!=null && !src.getAttributes().isEmpty())
				value = src.getAttributes().get(ua.getAttribute());
			if (value == null) {
				if (ua.getId()!=null && (delete || src.getAttributes().containsKey(ua.getAttribute())))
					dataService.delete(ua);
			} else {
				Date d = null;
				try {
					//2020-08-05T00:00:00+02:00
					//yyyy-MM-dd'T'HH:mm:ss.SSS'Z
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
					format.setTimeZone(TimeZone.getTimeZone("UTC"));
					d = format.parse(value.toString());
				} catch(Exception e) {}
				if (d!=null) {
					Calendar c = Calendar.getInstance();
					c.setTime(d);
					ua.setDateValue(c);
				} else
					ua.setValue(value.toString());
				dataService.update(ua);
			}
		}
		for (String key : src.getAttributes().keySet()) {
			boolean found = false;
			for (UserData ua : atts) {
				if (ua.getAttribute().equals(key)) {
					found = true;
					break;
				}
			}
			if (!found) {
				UserData data = new UserData();
				data.setUser(target.getUserName());
				data.setAttribute(key);
				Object value = src.getAttributes().get(key);
				if (value instanceof Date) {
					Calendar c = Calendar.getInstance();
					c.setTime((Date) value);
					data.setDateValue(c);
				} else
					data.setValue(value.toString());
				dataService.create(data);
			}
		}
	}
}
