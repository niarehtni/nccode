create table tbm_daystat_cacu (
pk_daystat_cacu CHAR(20) NOT NULL primary key,
pk_org CHAR(20),
cuserid CHAR(20) NULL, -- 用户id
pk_daystat CHAR(20) NULL,
pk_psndoc CHAR(20) NULL, -- 员工
pk_timeitem CHAR(20) NULL,  
hourcount numeric(18,8) NULL, -- 时数
calendar char(10), -- 日期
calType char(1), -- 计算标志 0为加班数据 1为加班转调休数据
ts char(19) -- ts
);