import org.json.JSONArray;
import org.json.JSONObject;

import com.soffid.iam.addon.scim2.json.ResourceTypeGenerator;

import junit.framework.TestCase;

public class ResourceTest extends TestCase {

	public void testResourceType() throws Exception {
		ResourceTypeGenerator generator = new ResourceTypeGenerator();
		JSONArray o = generator.generate("http://localhost:8080/webservice/scim2/v1/");
		System.out.println(o);
	}

}
