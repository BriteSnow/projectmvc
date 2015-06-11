package org.projectmvc.web;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.britesnow.snow.web.RequestContext;
import com.britesnow.snow.web.WebRequestType;
import com.britesnow.snow.web.auth.AuthRequest;
import com.britesnow.snow.web.auth.AuthToken;
import com.britesnow.snow.web.param.annotation.WebParam;
import com.britesnow.snow.web.param.annotation.WebUser;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.britesnow.snow.web.rest.annotation.WebPost;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.hash.Hashing;
import org.projectmvc.AppException;
import org.projectmvc.ErrorType;
import org.projectmvc.access.AccessManager;
import org.projectmvc.access.OrgRole;
import org.projectmvc.access.UserAccessContext;
import org.projectmvc.dao.IDao;
import org.projectmvc.dao.UserDao;
import org.projectmvc.entity.Org;
import org.projectmvc.entity.User;
import org.projectmvc.service.PasswordService;


/**
 * <pre>
 * Example of a Application Authentication service that will manage 2 things: 
 * 
 * 1) Login/Logoff REST service that will be bound via the @Web[REST] annotations.
 * 
 * 2) Authenticate any incoming Request with the AuthRequest.authRequest(RequestContext c)  
 * 
 * Note A) Note that the authRequest is bound to the application in the AppConfig, while the login/logoff are Snow @Web[REST] binding that 
 * could be moved to any Guice managed singleton classes. We just put them in the same class because of their related purpose. 
 * </pre>
 * 
 * @author jeremychone
 *
 */
@Singleton
public class AppAuthService implements AuthRequest {
	static private final String TOKEN_SALT = "maBfLDmbuxwvGkkhpm4WddKR9EUawThYWZTiNcbuuFHzfTXhW";

	static private final String CN_UTOKEN = "utoken";
	static private final String CN_USERNAME = "username";

	public enum Error implements ErrorType {
		NOT_LOGGEDIN("User not logged in."),
		WRONG_CREDENTIAL("User name or password invalid."),
		CANNOT_REGISTER_EMPTY_NAME("Username cannot be empty"),
		CANNOT_REGISTER_USERNAME_ALREADY_EXIST("Username already exist"),
		CANNOT_REGISTER_WITH_EMPTY_PASSWORD("Password cannot be empty."),
		REPEAT_PASSWORD_NO_MATCH("Repeat password did not match password field.");

		private String message;
		Error(String message){
			this.message = message;
		}
		public String getMessage(){
			return this.message;
		}
	}

	@Inject
	private UserDao userDao;

	@Inject
	private IDao<Org,Long> orgDao;

	@Inject
	private PasswordService passwordService;

	@Inject
	private AccessManager accessManager;

	// --------- AuthRequest Implementation --------- //
	/**
	 * <p>When Binding a AuthRequest implementation at the AppConfig Guice Module, all request will go
	 * through this authRequest() method. This is where we you get the user information from the cookie to authenticate
	 * or not the request (and the ability to get the @WebUser in your @Web[REST] and @Web[Handler] methods).</p>
	 *
	 * <p><strong>Note:</strong> In this application, we do not care to authenticate/secure static files request
	 * (e.g., .js, .css, and webbundle), therefore, we will use the WebRequestType to just do the auth logic for content related
	 * request (i.e., WEB_RESOURCE, WEB_REST, WEB_TEMPLATE)</p>
	 *
	 * @param rc
	 * @return
	 */
	@Override
	public AuthToken<User> authRequest(RequestContext rc) {
		WebRequestType wrt = rc.getWebRequestType();
		AuthToken authToken = null;
		switch(wrt){
			// All the dynamic resources, we need to auth
			case WEB_RESOURCE:
			case WEB_REST:
			case WEB_TEMPLATE:
				String username = rc.getCookie(CN_USERNAME);
				String utoken = rc.getCookie(CN_UTOKEN);
				if (!Strings.isNullOrEmpty(username) && !Strings.isNullOrEmpty(utoken)){
					User user = userDao.getByUsername(username);
					if (user != null){
						String expectedUToken = buildUToken(user);
						if (utoken.equals(expectedUToken)){
							authToken = new AuthToken<User>(user);
							accessManager.initUserAccessContext(user);
						}
					}
					// if the authToken is still null
					if (authToken == null){
						// the cookies where wrong, so, let's remove them.
						rc.removeCookie(CN_USERNAME);
						rc.removeCookie(CN_UTOKEN);
					}
				}
			break;

			// static files and generated files (.less, webbundle) we do not need to auth.
			case GENERATED_ASSET:
			case STATIC_FILE:
				// DO NOTHING.
		}
		return authToken;
	}
	// --------- /AuthRequest Implementation --------- //

	// --------- Register & Login WebREST APIs --------- //
	@WebPost("/register")
	public WebResponse register(@WebParam("username") String username, @WebParam("pwd")String clearPwd,
								@WebParam("pwdRepeat")String pwdRepeat,
								RequestContext rc){
		// we double check the password rules.
		if (Strings.isNullOrEmpty(username)) {
			throw new AppException(Error.CANNOT_REGISTER_EMPTY_NAME);
		}
		if (Strings.isNullOrEmpty(clearPwd)){
			throw new AppException(Error.CANNOT_REGISTER_WITH_EMPTY_PASSWORD);
		}
		if (!clearPwd.equals(pwdRepeat)) {
			throw new AppException(Error.REPEAT_PASSWORD_NO_MATCH);
		}
		if (userDao.getByUsername(username) != null){
			throw new AppException(Error.CANNOT_REGISTER_USERNAME_ALREADY_EXIST);
		}

		String encryptedPwd = passwordService.encrypt(clearPwd);

		//// all new user have their own personal organization
		// create the user (and its new personal org)
		userDao.createUser(username,encryptedPwd);

		return login(username,clearPwd,rc);
	}

	@WebPost("/login")
	public WebResponse login(@WebParam("username")String username, @WebParam("pwd")String clearPwd, RequestContext rc){
		User user = userDao.getByUsername(username);

		// check if user exist
		if (user == null){
			throw new AppException(Error.WRONG_CREDENTIAL);
		}

		// get the password stored in the database
		String pwd = user.getPwd();

		// the clearPwd given by the end user, or the pwd on the database cannot be null or empty.
		if (Strings.isNullOrEmpty(clearPwd) || Strings.isNullOrEmpty(pwd)){
			throw new AppException(Error.WRONG_CREDENTIAL);
		}

		// now check the clearPwd against the pwd stored in the db with the passwordService.
		if (!passwordService.check(clearPwd, pwd)){
			throw new AppException(Error.WRONG_CREDENTIAL);
		}

		// if nothing fail, build utoken, and set cookies
		String utoken = buildUToken(user);
		rc.setCookie(CN_USERNAME,username);
		rc.setCookie(CN_UTOKEN,utoken);

		return WebResponse.success(user);
	}

	@WebGet("/logoff")
	public WebResponse logoff(RequestContext rc){
		String username = rc.getCookie(CN_USERNAME);
		String usertoken = rc.getCookie(CN_UTOKEN);
		if (username != null || usertoken != null){
			rc.removeCookie(CN_USERNAME);
			rc.removeCookie(CN_UTOKEN);
			return WebResponse.success();
		}else{
			return WebResponse.fail(new AppException(Error.NOT_LOGGEDIN));
		}
	}

	@WebGet("/auth/get-user")
	public WebResponse getUser(@WebUser User user) {
		if (user != null) {
			return WebResponse.success(user);
		}else{
			return WebResponse.fail("Request not authenticated");
		}
	}
	// --------- /Register & Login WebREST APIs --------- //

	static String buildUToken(User user){
		return Hashing.sha1().hashString(user.getUsername() + TOKEN_SALT + user.getPwd(), Charsets.UTF_8).toString();
	}
}
