DROP DATABASE IF EXISTS pmvc_db;
DROP USER IF EXISTS pmvc_user;
CREATE USER pmvc_user PASSWORD 'welcome';
CREATE DATABASE pmvc_db owner pmvc_user ENCODING = 'UTF-8';


