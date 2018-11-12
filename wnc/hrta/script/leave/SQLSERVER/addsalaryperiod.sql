CREATE TABLE tbm_daystatb_notcurrmonth(	
	approve_date nchar(10) NOT NULL,
	calendar nchar(10) NOT NULL,
	dr smallint NULL,
	hournum decimal(16, 4) NOT NULL,
	pk_daystat nchar(20) NOT NULL,
	pk_daystatb_notcurrmonth nchar(20) NOT NULL,
	pk_group nchar(20) NOT NULL,
	pk_org nchar(20) NOT NULL,
	pk_timeitem nchar(20) NOT NULL,
	pk_billsource nvarchar(20) NULL,
	timeitemunit smallint NOT NULL,
	toresthour decimal(16, 4) NULL,
	ts nchar(19) NULL,
	type smallint NOT NULL)

alter table tbm_overtimereg add waperiod nchar(10)
alter table tbm_overtimereg add approve_time nchar(19)
alter table tbm_leavereg add waperiod nchar(10)
alter table tbm_leavereg add approve_time nchar(19)
