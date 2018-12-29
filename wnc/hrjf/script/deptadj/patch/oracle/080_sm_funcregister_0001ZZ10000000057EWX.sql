
delete sm_menuitemreg where ( funcode in ('60050deptadj') and (dr=0 or dr is null) )
/

delete sm_paramregister where ( parentid in ('0001ZZ10000000057EWX') and (dr=0 or dr is null) )
/

delete sm_funcregister where ( CFUNID='0001ZZ10000000057EWX' and (dr=0 or dr is null) )
/


insert into sm_funcregister(cfunid,class_name,dr,fun_desc,fun_name,fun_property,funcode,funtype,help_name,isbuttonpower,iscauserusable,isenable,isfunctype,mdid,orgtypecode,own_module,parent_id,pk_group,ts) values( '0001ZZ10000000057EWX','nc.ui.pubapp.uif2app.ToftPanelAdaptorEx',0,null,'部门调整申请',0,'60050deptadj',0,null,'N',null,'Y','N',null,'HRORGTYPE00000000000','6005','1002Z710000000004I1E',null,'2018-11-06 12:34:00')
/


insert into sm_paramregister(dr,paramname,paramvalue,parentid,pk_param,ts) values( 0,'BeanConfigFilePath','nc/ui/hrjf/deptadj/ace/view/Deptadj_config.xml','0001ZZ10000000057EWX','0001ZZ10000000057EWY','2018-11-06 12:34:00')
/


insert into sm_menuitemreg(dr,funcode,iconpath,ismenutype,menudes,menuitemcode,menuitemname,nodeorder,pk_menu,pk_menuitem,resid,ts) values( 0,'60050deptadj',null,'N',null,'60050410','部门调整申请',null,'1004ZZ10000000000FFL','0001ZZ10000000057EWZ','D60050410','2018-11-06 12:34:00')
/
