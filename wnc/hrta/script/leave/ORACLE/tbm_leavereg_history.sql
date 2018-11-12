
-- Create table
create table tbm_leavereg_history
(
  backtime             CHAR(19),
  billsource           NUMBER(38) default 2 not null,
  creationtime         CHAR(19) not null,
  creator              CHAR(20) not null,
  dr                   NUMBER(10) default 0,
  freezedayorhour      NUMBER(16,4) default 0,
  islactation          CHAR(1) default 'N' not null,
  isleaveoff           CHAR(1) default 'N' not null,
  lactationholidaytype NUMBER(38),
  lactationhour        NUMBER(16,4),
  leavebegindate       CHAR(10) not null,
  leavebegintime       CHAR(19) not null,
  leaveenddate         CHAR(10) not null,
  leaveendtime         CHAR(19) not null,
  leavehour            NUMBER(16,4) default 0 not null,
  leaveindex           NUMBER(38) default 1 not null,
  leavemonth           CHAR(2),
  leaveremark          VARCHAR2(768),
  leaveyear            CHAR(4),
  modifiedtime         CHAR(19),
  modifier             VARCHAR2(20) default '~',
  pk_adminorg          CHAR(20),
  pk_agentpsn          VARCHAR2(20) default '~',
  pk_billsourceb       VARCHAR2(20) default '~',
  pk_billsourceh       VARCHAR2(20) default '~',
  pk_dept_v            CHAR(20),
  pk_group             CHAR(20) not null,
  pk_leavereg          CHAR(20) not null,
  pk_leavetype         CHAR(20) not null,
  pk_leavetypecopy     CHAR(20) not null,
  pk_org               CHAR(20) not null,
  pk_org_v             CHAR(20),
  pk_psndoc            CHAR(20) not null,
  pk_psnjob            CHAR(20) not null,
  pk_psnorg            CHAR(20) not null,
  realdayorhour        NUMBER(16,4) default 0,
  relatetel            VARCHAR2(50),
  restdayorhour        NUMBER(16,4) default 0,
  resteddayorhour      NUMBER(16,4),
  splitid              CHAR(20),
  ts                   CHAR(19) default to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),
  usefuldayorhour      NUMBER(16,4),
  workprocess          VARCHAR2(768),
  effectivedate        CHAR(10) not null,
  pk_leavereg_history  CHAR(20) not null,
  ischarge CHAR(1) default 'N' not null,
  charge NUMBER(16,4) default 0,
  actualchargedecimal NUMBER(16,4) default 0
)
-- Create/Recreate primary, unique and foreign key constraints 
alter table tbm_leavereg_history
  add constraint PK_TBM_LEAVEREG_HISTORY primary key (pk_leavereg_history)
  using index 
  tablespace NNC_INDEX01
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 128K
    next 128K
    minextents 1
    maxextents unlimited
    pctincrease 0
  );

