create table wa_daysalary (
pk_daysalary char(20) NOT NULL,
pk_hrorg varchar(20) default '~' NULL,
pk_wa_class varchar(50) NULL,
pk_wa_item varchar(20) NULL,
pk_psndoc_sub varchar(20) NULL,
wadocts char(19) NULL,
pk_psndoc varchar(20) default '~' NULL,
pk_psnjob varchar(20) default '~' NULL,
salarydate char(10) NULL,
cyear int NULL,
cperiod int NULL,
daysalary decimal(28,8) NULL,
hoursalary decimal(28,8) NULL,
CONSTRAINT PK_WA_DAYSALARY PRIMARY KEY (pk_daysalary),
ts char(19) NULL,
dr smallint default 0 
);


