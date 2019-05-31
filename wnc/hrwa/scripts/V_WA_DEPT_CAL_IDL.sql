CREATE OR REPLACE VIEW "V_WA_DEPT_CAL_IDL" ( "PSNCLCODE", "PK_FATHERORG", "CYEAR", "CPERIOD", "WORKDEPT", "NUM" )
AS
select 'IDL' psnclCode,dept.PK_FATHERORG,wa_dept_num.CYEAR,wa_dept_num.CPERIOD,wa_dept_num.workdept,wa_dept_num.num from org_dept dept
inner join 
(
select   wa.cyear,wa.cperiod,wa.workdept,count(wa.pk_psnjob)  num    from wa_data wa 
inner join bd_psnjob psnjob1 on psnjob1.pk_psnjob = wa.pk_psnjob 
inner join bd_psncl psncl on psncl.pk_psncl = psnjob1.pk_psncl 
where
psnjob1.pk_psncl in ('1001A1100000000001RD',  '1001A110000000000GFP')
and 
wa.pk_wa_class ='1001X11000000000APCA'
group by wa.cyear,wa.cperiod,wa.WORKDEPT
ORDER BY cyear,cperiod,num asc
) wa_dept_num
on
wa_dept_num.workdept = dept.pk_dept
order by num asc