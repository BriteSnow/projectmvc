-- quick EMPTY : truncate "user", org, project, ticket, team, teamuser RESTART IDENTITY cascade;
-- quick DROP  : drop table  "user", org, project, ticket, team, teamuser;

-- --------- org --------- --
CREATE TABLE org
(
	id bigserial NOT NULL,
	name character varying(128),
	personal boolean default 'f' NOT NULL,

	-- Timestamp data
	"cid" bigInt,
	"ctime" timestamp with time zone,
	"mid" bigInt,
	"mtime" timestamp with time zone,

	CONSTRAINT org_pkey PRIMARY KEY (id)
);
-- --------- /org --------- --

-- --------- user --------- --
CREATE TABLE "user"
(
	id bigserial NOT NULL,
	username character varying(128) NOT NULL,
	"fullName" character varying(128),
	pwd character varying(256) NOT NULL,
	"orgId" bigint NOT NULL, -- main organization --

	-- Timestamp data
	"cid" bigInt,
	"ctime" timestamp with time zone,
	"mid" bigInt,
	"mtime" timestamp with time zone,

	CONSTRAINT user_pkey PRIMARY KEY (id)
);
create index on "user" (username);
-- --------- /user --------- --

-- --------- orguser --------- --
CREATE TABLE orguser
(
	"orgId" bigint NOT NULL,
	"userId" bigint NOT NULL,
	"orgRoles" character varying(128),

	-- Timestamp data
	"cid" bigInt,
	"ctime" timestamp with time zone,
	"mid" bigInt,
	"mtime" timestamp with time zone,	
	
	CONSTRAINT orguser_pkey PRIMARY KEY ("orgId","userId")
);
-- --------- /orguser --------- --

-- --------- project --------- --
CREATE TABLE project
(
	id bigserial NOT NULL,
	"orgId" bigint NOT NULL,
	name character varying(128),
	ticket_num_seq integer default 0,

	-- Timestamp data
	"cid" bigInt,
	"ctime" timestamp with time zone,
	"mid" bigInt,
	"mtime" timestamp with time zone,

	CONSTRAINT project_pkey PRIMARY KEY (id)
);
-- --------- /project --------- --

-- --------- ticket --------- --
CREATE TABLE ticket
(
	id bigserial NOT NULL,
	"orgId" bigint NOT NULL,
	title character varying(128),
	description text,
	"projectId" bigint,

	-- Timestamp data
	"cid" bigInt,
	"ctime" timestamp with time zone,
	"mid" bigInt,
	"mtime" timestamp with time zone,

	CONSTRAINT ticket_pkey PRIMARY KEY (id)
);
-- --------- /ticket --------- --

-- --------- team --------- --
CREATE TABLE team
(
	id bigserial NOT NULL,
	"orgId" bigint NOT NULL,
	name character varying(128),
	"projectId" bigint,
	roles  character varying(128),

	-- Timestamp data
	"cid" bigInt,
	"ctime" timestamp with time zone,
	"mid" bigInt,
	"mtime" timestamp with time zone,

	CONSTRAINT team_pkey PRIMARY KEY (id)
);
-- --------- /team --------- --

-- --------- teamuser --------- --
CREATE TABLE teamuser
(
	"teamId" bigint NOT NULL,
	"userId" bigint NOT NULL,
	
	-- Timestamp data
	"cid" bigInt,
	"ctime" timestamp with time zone,
	"mid" bigInt,
	"mtime" timestamp with time zone,

	CONSTRAINT teamuser_pkey PRIMARY KEY ("teamId","userId")
);
-- --------- /teamuser --------- --

