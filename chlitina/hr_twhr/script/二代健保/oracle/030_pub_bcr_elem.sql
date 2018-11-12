
delete pub_bcr_elem where ( pk_billcodeelem in ('0001ZZ10000000051GBC', '0001ZZ10000000051GBD', '0001ZZ10000000051GBE') and (dr=0 or dr is null) )
;


insert into pub_bcr_elem(dataoriginflag,dr,elemlenth,elemtype,elemvalue,eorder,fillsign,fillstyle,isrefer,pk_billcodebase,pk_billcodeelem,pk_billcodeentity,ts) values( null,0,4,0,'NHI1',0,null,0,0,'0001ZZ10000000051GBB','0001ZZ10000000051GBC','~','2018-09-26 10:47:49')
;
insert into pub_bcr_elem(dataoriginflag,dr,elemlenth,elemtype,elemvalue,eorder,fillsign,fillstyle,isrefer,pk_billcodebase,pk_billcodeelem,pk_billcodeentity,ts) values( null,0,8,2,'2018-09-26 10:47:49',1,null,0,0,'0001ZZ10000000051GBB','0001ZZ10000000051GBD','~','2018-09-26 10:47:49')
;
insert into pub_bcr_elem(dataoriginflag,dr,elemlenth,elemtype,elemvalue,eorder,fillsign,fillstyle,isrefer,pk_billcodebase,pk_billcodeelem,pk_billcodeentity,ts) values( null,0,8,3,'NHI1',2,null,0,0,'0001ZZ10000000051GBB','0001ZZ10000000051GBE','~','2018-09-26 10:47:49')
;
