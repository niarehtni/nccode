update pub_sysinittemp set initname='是否啟用月平均薪資同步級距' where initcode = 'TWHR06';
update pub_sysinit set initname='是否啟用月平均薪資同步級距' where initcode ='TWHR06';

DELETE FROM sm_paramregister WHERE parentid = (SELECT cfunid FROM sm_funcregister WHERE fun_name like '%员工信息维护%' );
INSERT INTO sm_paramregister (dr, paramname, paramvalue, parentid, pk_param) 
SELECT 0, 'PluginBeanConfigFilePath', '/nc/ui/hi/employee/uif2_ext_config.xml', cfunid, '1001JF100000PLUGIN01'
FROM sm_funcregister
WHERE fun_name like '%员工信息维护%';

