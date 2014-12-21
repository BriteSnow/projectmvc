package org.projectmvc.web;

import com.britesnow.snow.web.param.annotation.WebUser;
import com.britesnow.snow.web.rest.annotation.WebGet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.projectmvc.dao.IDao;
import org.projectmvc.dao.UserDao;
import org.projectmvc.entity.Ticket;
import org.projectmvc.entity.User;
import org.projectmvc.perf.annotation.ToMonitor;

import java.util.List;

/**
 * Created by jeremychone on 12/27/13.
 */
@Singleton
@ToMonitor
public class UserWebHandler {

    @Inject
    private UserDao userDao;

	@Inject
	private IDao<User,Long> udao;

	@Inject
	private IDao<Ticket,Long> ticketDao;

	@Inject
	private WebResponseBuilder webResponseBuilder;


    @WebGet("/das-list-user")
    public WebResponse listUser(@WebUser User user){
        List<User> users = userDao.list(user,null,0,100,"name");
        return webResponseBuilder.success(users);
    }

}
