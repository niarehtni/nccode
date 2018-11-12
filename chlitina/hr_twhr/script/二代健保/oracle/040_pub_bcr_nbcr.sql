
delete pub_bcr_nbcr where ( pk_nbcr in ('0001ZZ10000000051GBA') and (dr=0 or dr is null) )
;


insert into pub_bcr_nbcr(code,codelenth,codescope,codestyle,comp,dr,metaid,name,name2,name3,name4,name5,name6,orgtype,pk_nbcr,ts) values( 'NHI1',40,'global','after',null,0,'80a16e9a-9cfc-48ca-a1cb-718050e3f125','NHI1',null,null,null,null,null,'GLOBLE00000000000000','0001ZZ10000000051GBA','2018-09-26 10:47:49')
;
