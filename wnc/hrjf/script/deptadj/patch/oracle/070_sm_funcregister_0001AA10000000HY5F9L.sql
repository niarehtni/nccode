
delete sm_menuitemreg where ( funcode in ('60050deptadj') and (dr=0 or dr is null) )
/

delete sm_paramregister where ( parentid in ('0001AA10000000HY5F9L') and (dr=0 or dr is null) )
/

delete sm_butnregister where ( parent_id in ('0001AA10000000HY5F9L') and (dr=0 or dr is null) )
/

delete sm_funcregister where ( cfunid='0001AA10000000HY5F9L' and (dr=0 or dr is null) )
/


insert into sm_funcregister(cfunid,class_name,dr,fun_desc,fun_name,fun_property,funcode,funtype,help_name,isbuttonpower,iscauserusable,isenable,isfunctype,mdid,orgtypecode,own_module,parent_id,pk_group,ts) values( '0001AA10000000HY5F9L','nc.ui.pubapp.uif2app.ToftPanelAdaptorEx',0,null,'部门调整申请',0,'60050deptadj',0,null,'N',null,'Y','N',null,'BUSINESSUNIT00000000','6005','1002Z710000000004I1E',null,'2018-11-03 16:46:22')
/


insert into sm_butnregister(btncode,btndesc,btnname,btnownertype,dr,isbuttonpower,isenable,iskeyfunc,parent_id,pk_btn,resid,ts) values( 'Edit','修改(Ctrl+E)','修改',0,0,'N','Y','N','0001AA10000000HY5F9L','1001AA100000002PMXT5',null,'2018-11-04 16:40:06')
/
insert into sm_butnregister(btncode,btndesc,btnname,btnownertype,dr,isbuttonpower,isenable,iskeyfunc,parent_id,pk_btn,resid,ts) values( 'Preview','預覽(Ctrl+W)','预览',0,0,'N','Y','N','0001AA10000000HY5F9L','1001AA100000002PMYKX',null,'2018-11-04 16:40:06')
/
insert into sm_butnregister(btncode,btndesc,btnname,btnownertype,dr,isbuttonpower,isenable,iskeyfunc,parent_id,pk_btn,resid,ts) values( 'Query','查詢(F3)','查詢',0,0,'N','Y','N','0001AA10000000HY5F9L','1001AA100000002PMZCP',null,'2018-11-04 16:40:06')
/
insert into sm_butnregister(btncode,btndesc,btnname,btnownertype,dr,isbuttonpower,isenable,iskeyfunc,parent_id,pk_btn,resid,ts) values( 'Cancel','取消(Ctrl+Q)','取消',0,0,'N','Y','N','0001AA10000000HY5F9L','1001AA100000002PN04H',null,'2018-11-04 16:40:06')
/
insert into sm_butnregister(btncode,btndesc,btnname,btnownertype,dr,isbuttonpower,isenable,iskeyfunc,parent_id,pk_btn,resid,ts) values( 'Refresh','刷新(F5)','刷新',0,0,'N','Y','N','0001AA10000000HY5F9L','1001AA100000002PN0W9',null,'2018-11-04 16:40:06')
/
insert into sm_butnregister(btncode,btndesc,btnname,btnownertype,dr,isbuttonpower,isenable,iskeyfunc,parent_id,pk_btn,resid,ts) values( 'Add','新增(Ctrl+N)','新增',0,0,'N','Y','N','0001AA10000000HY5F9L','1001AA100000002PN1O1',null,'2018-11-04 16:40:06')
/
insert into sm_butnregister(btncode,btndesc,btnname,btnownertype,dr,isbuttonpower,isenable,iskeyfunc,parent_id,pk_btn,resid,ts) values( 'Delete','刪除(Ctrl+Shift+Del)','刪除',0,0,'N','Y','N','0001AA10000000HY5F9L','1001AA100000002PN2FT',null,'2018-11-04 16:40:06')
/
insert into sm_butnregister(btncode,btndesc,btnname,btnownertype,dr,isbuttonpower,isenable,iskeyfunc,parent_id,pk_btn,resid,ts) values( 'output','輸出...','輸出...',0,0,'N','Y','N','0001AA10000000HY5F9L','1001AA100000002PN37L',null,'2018-11-04 16:40:06')
/
insert into sm_butnregister(btncode,btndesc,btnname,btnownertype,dr,isbuttonpower,isenable,iskeyfunc,parent_id,pk_btn,resid,ts) values( 'Save','保存(Ctrl+S)','保存',0,0,'N','Y','N','0001AA10000000HY5F9L','1001AA100000002PN3ZD',null,'2018-11-04 16:40:06')
/
insert into sm_butnregister(btncode,btndesc,btnname,btnownertype,dr,isbuttonpower,isenable,iskeyfunc,parent_id,pk_btn,resid,ts) values( 'Print','列印(Ctrl+P)','打印',0,0,'N','Y','N','0001AA10000000HY5F9L','1001AA100000002PN4R5',null,'2018-11-04 16:40:06')
/
insert into sm_butnregister(btncode,btndesc,btnname,btnownertype,dr,isbuttonpower,isenable,iskeyfunc,parent_id,pk_btn,resid,ts) values( 'Copy','複製(Ctrl+Alt+C)','複製',0,0,'N','Y','N','0001AA10000000HY5F9L','1001AA100000002PN5IX',null,'2018-11-04 16:40:06')
/


insert into sm_paramregister(dr,paramname,paramvalue,parentid,pk_param,ts) values( 0,'BeanConfigFilePath','nc/ui/hrjf/deptadj/ace/view/Deptadj_config.xml','0001AA10000000HY5F9L','0001AA10000000HY5G1D','2018-11-03 16:46:23')
/


insert into sm_menuitemreg(dr,funcode,iconpath,ismenutype,menudes,menuitemcode,menuitemname,nodeorder,pk_menu,pk_menuitem,resid,ts) values( 0,'60050deptadj',null,'N',null,'60050410','部门调整申请',null,'1004ZZ10000000000FFL','0001AA10000000HY5GT5','D60050410','2018-11-03 16:46:23')
/
