import java.util.Calendar;
import java.util.LinkedList;

import org.json.JSONObject;

import com.soffid.iam.addon.scim2.json.JSONBuilder;
import com.soffid.iam.addon.scim2.json.JSONParser;
import com.soffid.iam.api.Account;

import es.caib.seycon.ng.comu.AccountType;
import junit.framework.TestCase;

public class JSONTest extends TestCase {

	public void testJson() throws Exception {
		Account acc = new Account();
		acc.setName("admin");
		acc.setSystem("soffid");
		acc.setDisabled(false);
		acc.setLastLogin(Calendar.getInstance());
		acc.setType(AccountType.SHARED);
		acc.setOwnerUsers(new LinkedList<>());
		acc.getOwnerUsers().add("admin");
		
		JSONBuilder b = new JSONBuilder(null);
		JSONObject json = b.build(acc);
		
		System.out.println(json);
		
		
		JSONParser p = new JSONParser();
		Account acc2 = p.load(json, Account.class, new String[0]);
		System.out.println(acc2);
	}
}
