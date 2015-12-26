DROP DATABASE IF EXISTS "projectmvc_db";
DROP USER IF EXISTS "projectmvc_user";
CREATE USER "projectmvc_user" PASSWORD 'welcome';
CREATE DATABASE "projectmvc_db" owner projectmvc_user ENCODING = 'UTF-8';
