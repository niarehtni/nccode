
delete sm_menuitemreg where ( pk_menuitem in ('1001ZZ1000000000K3RQ') and (dr=0 or dr is null) );
INSERT INTO SM_MENUITEMREG (DR, FUNCODE, ICONPATH, ISMENUTYPE, MENUDES, MENUITEMCODE, MENUITEMNAME, NODEORDER, PK_MENU, PK_MENUITEM, RESID, TS) VALUES (0, '68J61006', null, 'N', null, '60401003', '参数设定-全局', null, (select pk_menu from sm_menuregister where isenable = 'Y'), '1001ZZ1000000000K3RQ', 'D60401001', '2018-09-30 20:32:28');
