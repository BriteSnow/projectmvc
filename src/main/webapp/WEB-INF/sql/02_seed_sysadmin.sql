-- We insert sysadmin-org
insert into "org" (id, name) values (1, 'sysadmin-org');
ALTER SEQUENCE org_id_seq restart 2; -- since we set id val manual, make sure the sequence get updated

-- We insert the sysadmin user
insert into "user" (id, username, pwd, "orgId" ) values (1, 'sysadmin', 'welcome', 1);
ALTER SEQUENCE user_id_seq restart 2; -- since we set id val manual, make sure the sequence get updated

-- We added the role "full" to the sysadmin (which will give him full sysadmin role)
insert into "orguser" ("orgId", "userId", roles ) values (1, 1, 'full');