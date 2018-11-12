drop table tbm_monthstat_cacu;
create table TBM_MONTHSTAT_CACU
(
    pk_org      char(20),
    pk_psndoc       char(20) ,
    pk_monthstat char(20)  ,
    datedaytype     number ,
		otSettletype    number,
		otsumhours      number,
		cyear         varchar2(10),
		cperiod       varchar2(10),
		pk_monthstat_cacu char(20),
		ts           char(19)
		
)