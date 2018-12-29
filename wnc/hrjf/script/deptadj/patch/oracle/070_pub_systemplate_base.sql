
delete pub_systemplate_base where ( PK_SYSTEMPLATE in ('0001ZZ10000000057F7F', '0001ZZ10000000057F09', '0001ZZ10000000057EYN') and (dr=0 or dr is null) )
/


insert into pub_systemplate_base(devorg,dr,funnode,layer,moduleid,nodekey,pk_country,pk_industry,pk_systemplate,templateid,tempstyle,ts) values( '00001',0,'60050deptadj',0,'6005','bt',null,null,'0001ZZ10000000057EYN','0001ZZ10000000057EX0',0,'2018-11-06 12:34:10')
/
insert into pub_systemplate_base(devorg,dr,funnode,layer,moduleid,nodekey,pk_country,pk_industry,pk_systemplate,templateid,tempstyle,ts) values( '00001',0,'60050deptadj',0,'6005','qt',null,null,'0001ZZ10000000057F09','0001ZZ10000000057EYO',1,'2018-11-06 12:34:10')
/
insert into pub_systemplate_base(devorg,dr,funnode,layer,moduleid,nodekey,pk_country,pk_industry,pk_systemplate,templateid,tempstyle,ts) values( '00001',0,'60050deptadj',0,'6005','ot',null,null,'0001ZZ10000000057F7F','0001ZZ10000000057F0A',3,'2018-11-06 12:34:16')
/
