create table tbm_extrarest (
pk_extrarest char(20) NOT NULL,
pk_psndoc varchar(20) default '~' NULL,
pk_org_v varchar(20) default '~' NULL,
pk_dept_v varchar(20) default '~' NULL,
pk_group varchar(20) default '~' NULL,
pk_org varchar(20) default '~' NULL,
creator varchar(20) default '~' NULL,
billdate char(19) NULL,
creationtime char(19) NULL,
modifier varchar(20) default '~' NULL,
modifiedtime char(19) NULL,
beforechange varchar(50) NULL,
afterchange varchar(50) NULL,
changetype varchar(50) NULL,
changedayorhour decimal(28,8) NULL,
CONSTRAINT PK_TBM_EXTRAREST PRIMARY KEY (pk_extrarest),
ts char(19) NULL,
dr smallint default 0 
)


