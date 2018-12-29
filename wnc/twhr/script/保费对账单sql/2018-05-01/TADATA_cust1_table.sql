create table train_Custamain (
pk_cust char(20) NOT NULL,
pk_group varchar(20) default '~' NULL,
pk_org varchar(20) default '~' NULL,
pk_org_v varchar(20) default '~' NULL,
code varchar(50) NULL,
name varchar(50) NULL,
pk_supclass varchar(20) default '~' NULL,
industry char(20) NULL,
creator char(20) NULL,
creationtime char(19) NULL,
modifier char(20) NULL,
modifiedtime char(19) NULL,
vmemo varchar(128) NULL,
def1 varchar(128) NULL,
def2 varchar(128) NULL,
def3 varchar(128) NULL,
def4 varchar(128) NULL,
def5 varchar(128) NULL,
CONSTRAINT PK_TRAIN_CUSTAMAIN PRIMARY KEY (pk_cust),
ts char(19) NULL,
dr smallint default 0 
)


create table train_Custbody (
pk_cust_b char(20) NOT NULL,
pk_group char(20) NULL,
pk_org char(20) NULL,
pk_org_v char(20) NULL,
linkman varchar(50) NULL,
sex varchar(50) NULL,
phone varchar(50) NULL,
mobile varchar(50) NULL,
isdefault char(1) NULL,
vmemo varchar(128) NULL,
def1 varchar(128) NULL,
def2 varchar(128) NULL,
def3 varchar(128) NULL,
def4 varchar(128) NULL,
def5 varchar(128) NULL,
pk_cust char(20) NOT NULL,
CONSTRAINT PK_TRAIN_CUSTBODY PRIMARY KEY (pk_cust_b),
ts char(19) NULL,
dr smallint default 0 
)


