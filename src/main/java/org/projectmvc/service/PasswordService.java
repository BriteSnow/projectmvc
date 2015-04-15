package org.projectmvc.service;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

import javax.inject.Singleton;

/**
 * <p>A simple singleton that manage the password encryption and validation for WebREST handles and type of requests.</p>
 *
 * <p>At first, this class will support only one encryption scheme, but eventually this service can support multiple encryption schemes
 * allowing to seamlessly update password security with minimum end-user disturbance.</p>
 */
@Singleton
public class PasswordService {

	Scheme clearScheme = new ClearScheme();
	Scheme defaultScheme = new Scheme01();

	/**
	 * Encrypt the password with the latest security scheme.
	 *
	 * @param clearPwd
	 * @return
	 */
	public String encrypt(String clearPwd){
		return defaultScheme.encrypt(clearPwd);
	}

	public boolean check(String clearPwd, String referencePwd) {
		if (referencePwd == null || clearPwd == null) {
			return false;
		}

		// the pwdToCompare is the encrypted pwd given the referencePwd scheme (which could be the clearScheme or another)
		String pwdToCompare = getScheme(referencePwd).encrypt(clearPwd) ;

		return (pwdToCompare.equals(referencePwd));
	}

	private Scheme getScheme(String pwd){
		// For now a simplistic check to return the default or clear scheme.
		// Note: yes, this assume that clear password cannot start with "#E", but remember, that
		//       all passwords entered by the end users will be encrypted. This is more to allow an admin override.
		return pwd.startsWith("#E")?defaultScheme:clearScheme;
	}


	static final String schemeIt(String schemeName, String encryptedPwd){
		return new StringBuilder().append("#E").append(schemeName).append("#").append(encryptedPwd).toString();
	}
}

interface Scheme{

	String encrypt(String clearPwd);

}


class ClearScheme implements Scheme{

	@Override
	public String encrypt(String clearPwd) {
		return clearPwd;
	}
}

/**
 * This is the first encryption scheme.
 */
class Scheme01 implements Scheme{
	static final String name = "01";

	static private final String SALT = "qRkVBkLwJWy2fAmXNCVDDJxxR3tuNMJiKXvcTGJQGeszN";


	@Override
	public String encrypt(String clearPwd) {
		// for this example, we will use sha256, but can be made much more secure if needed.
		String encryptedPwd = Hashing.sha256().hashString(SALT + clearPwd, Charsets.UTF_8).toString();
		return PasswordService.schemeIt(name, encryptedPwd);
	}
}