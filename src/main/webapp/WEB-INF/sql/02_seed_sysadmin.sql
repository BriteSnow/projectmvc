insert into "org" (id, name) values (1, 'sysadmin-org');
ALTER SEQUENCE org_id_seq restart 2; -- since we set id val manual, make sure the sequence get updated

insert into "user" (id, username, pwd, "orgId" ) values (1, 'sysadmin', 'welcome', 1);
ALTER SEQUENCE user_id_seq restart 2; -- since we set id val manual, make sure the sequence get updated