delete sm_menuitemreg where ( funcode in ('1305OBUINITDATA1') and (dr=0 or dr is null) );


delete sm_funcregister where ( cfunid='1001ZZ1000000000KAPP' and (dr=0 or dr is null) );



insert into sm_funcregister(cfunid,class_name,dr,fun_desc,fun_name,fun_property,funcode,funtype,help_name,isbuttonpower,iscauserusable,isenable,isfunctype,mdid,orgtypecode,own_module,parent_id,pk_group,ts) values( '1001ZZ1000000000KAPP','nc.ui.build.script.ConfigDataUI',0,null,'Init Data Exporter',0,'1305OBUINITDATA1',3,null,null,'N','Y','N',null,'GLOBLE00000000000000','1317','0001Z0FTSLZ600090RD0',null,'2018-07-28 15:59:41');



insert into sm_menuitemreg(dr,funcode,iconpath,ismenutype,menudes,menuitemcode,menuitemname,nodeorder,pk_menu,pk_menuitem,resid,ts) values( 0,'1305OBUINITDATA1',null,'N',null,'13170101','导出按钮',null,'1004ZZ10000000000FFL','1001ZZ1000000000KAPQ','D13170101','2018-07-28 16:01:23');
