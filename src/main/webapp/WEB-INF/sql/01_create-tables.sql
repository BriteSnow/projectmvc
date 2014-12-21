-- quick EMPTY : truncate "user", project, ticket, team, teamuser RESTART IDENTITY cascade;
-- quick DROP  : drop table  "user", project, ticket, team, teamuser;

-- --------- user --------- --
CREATE TABLE "user"
(
	id bigserial NOT NULL,
	username character varying(128),
	"fullName" character varying(128),
	pwd character varying(256),
	CONSTRAINT user_pkey PRIMARY KEY (id)
);
create index on "user" (username);
-- --------- /user --------- --


-- --------- project --------- --
CREATE TABLE project
(
	id bigserial NOT NULL,
	name character varying(128),
	ticket_num_seq integer default 0,

	-- Timestamp data
	"creatorId" bigInt,
	"createTime" timestamp with time zone,
	"updateTime" timestamp  with time zone,

	CONSTRAINT project_pkey PRIMARY KEY (id)
);
-- --------- /project --------- --

-- --------- ticket --------- --
CREATE TABLE ticket
(
	id bigserial NOT NULL,
	title character varying(128),
	description text,
	"projectId" bigint,

	-- Timestamp data
	"creatorId" bigInt,
	"createTime" timestamp without time zone,
	"updateTime" timestamp without time zone,

	CONSTRAINT ticket_pkey PRIMARY KEY (id)
);
-- --------- /ticket --------- --

-- --------- team --------- --
CREATE TABLE team
(
	id bigserial NOT NULL,
	name character varying(128),
	"projectId" bigint,
	roles  character varying(128),

	CONSTRAINT team_pkey PRIMARY KEY (id)
);
-- ----;----- /team --------- --

-- --------- teamuser --------- --
CREATE TABLE teamuser
(
	"teamId" bigint NOT NULL,
	"userId" bigint NOT NULL,

	CONSTRAINT teamuser_pkey PRIMARY KEY ("teamId","userId")
);
-- --------- /teamuser --------- --

