--DELETE
DELETE FROM pub_sysinittemp WHERE groupcode = 'TWHR';
DELETE FROM pub_sysinit WHERE initcode LIKE 'TWHR%';
DELETE FROM twhr_rangeline;
DELETE FROM twhr_rangetable;

--sysinittemp
INSERT INTO pub_sysinittemp (afterclass, checkclass, dataclass, dataoriginflag, defaultvalue, domainflag, dr, editcomponentctrlclass, groupcode, groupname, initcode, initname, mainflag, mutexflag, orgtypeconvertmode, paratype, pk_orgtype, pk_refinfo, pk_sysinittemp, remark, showflag, stateflag, sysflag, sysindex, ts, valuelist, valuetype) VALUES (null, null, null, 0, 'N', '68J6', null, null, 'TWHR', '台灣人力資源', 'TWHR01', '是否啟用台灣人力資源', 'N', 0, 'HRORGTYPE00000000000', 'business', 'HRORGTYPE00000000000', '~', '0000TW10000001ORGREF', '只有該值為是時，才啟用所有台灣人力資源特性。', 'Y', 2, 'N', 0, '2014-11-06 00:31:18', 'Y/N', 1);
INSERT INTO pub_sysinittemp (afterclass, checkclass, dataclass, dataoriginflag, defaultvalue, domainflag, dr, editcomponentctrlclass, groupcode, groupname, initcode, initname, mainflag, mutexflag, orgtypeconvertmode, paratype, pk_orgtype, pk_refinfo, pk_sysinittemp, remark, showflag, stateflag, sysflag, sysindex, ts, valuelist, valuetype) VALUES (null, null, null, 0, 'N', '68J6', null, null, 'TWHR', '台灣人力資源', 'TWHR02', '人員入職時是否自動增加勞保記錄', 'N', 0, 'HRORGTYPE00000000000', 'business', 'HRORGTYPE00000000000', '~', '0000TW10000002ORGREF', '該值為是時，人員入職時會在勞保信息表中自動創建一行，起始時間為入職日期。', 'Y', 2, 'N', 0, '2014-11-06 00:38:54', 'Y/N', 1);
INSERT INTO pub_sysinittemp (afterclass, checkclass, dataclass, dataoriginflag, defaultvalue, domainflag, dr, editcomponentctrlclass, groupcode, groupname, initcode, initname, mainflag, mutexflag, orgtypeconvertmode, paratype, pk_orgtype, pk_refinfo, pk_sysinittemp, remark, showflag, stateflag, sysflag, sysindex, ts, valuelist, valuetype) VALUES (null, null, null, 0, '', '68J6', null, null, 'TWHR', '台灣人力資源', 'TWHR03', '默認勞保身份', 'N', 0, 'HRORGTYPE00000000000', 'business', 'HRORGTYPE00000000000', '1001ZZ10000000001J2H', '0000TW10000003ORGREF', '自動創建勞保信息行時使用的勞保身份。', 'Y', 5, 'N', 0, '2014-11-06 00:37:05', '', 2);
INSERT INTO pub_sysinittemp (afterclass, checkclass, dataclass, dataoriginflag, defaultvalue, domainflag, dr, editcomponentctrlclass, groupcode, groupname, initcode, initname, mainflag, mutexflag, orgtypeconvertmode, paratype, pk_orgtype, pk_refinfo, pk_sysinittemp, remark, showflag, stateflag, sysflag, sysindex, ts, valuelist, valuetype) VALUES (null, null, null, 0, 'N', '68J6', null, null, 'TWHR', '台灣人力資源', 'TWHR04', '人員入職時是否自動增加健保記錄', 'N', 0, 'HRORGTYPE00000000000', 'business', 'HRORGTYPE00000000000', '~', '0000TW10000004ORGREF', '該值為是時，人員入職時會在健保信息表中自動創建一行，起始時間為入職日期。', 'Y', 2, 'N', 0, '2014-11-06 00:38:43', 'Y/N', 1);
INSERT INTO pub_sysinittemp (afterclass, checkclass, dataclass, dataoriginflag, defaultvalue, domainflag, dr, editcomponentctrlclass, groupcode, groupname, initcode, initname, mainflag, mutexflag, orgtypeconvertmode, paratype, pk_orgtype, pk_refinfo, pk_sysinittemp, remark, showflag, stateflag, sysflag, sysindex, ts, valuelist, valuetype) VALUES (null, null, null, 0, '', '68J6', null, null, 'TWHR', '台灣人力資源', 'TWHR05', '默認健保身份', 'N', 0, 'HRORGTYPE00000000000', 'business', 'HRORGTYPE00000000000', '1001ZZ10000000001J4H', '0000TW10000005ORGREF', '自動創建健保信息行時使用的健保身份。', 'Y', 5, 'N', 0, '2014-11-06 00:40:17', '', 2);
INSERT INTO pub_sysinittemp (afterclass, checkclass, dataclass, dataoriginflag, defaultvalue, domainflag, dr, editcomponentctrlclass, groupcode, groupname, initcode, initname, mainflag, mutexflag, orgtypeconvertmode, paratype, pk_orgtype, pk_refinfo, pk_sysinittemp, remark, showflag, stateflag, sysflag, sysindex, ts, valuelist, valuetype) VALUES (null, null, null, 0, 'N', '68J6', null, null, 'TWHR', '台灣人力資源', 'TWHR06', '是否啟用定調薪級距同步', 'N', 0, 'HRORGTYPE00000000000', 'business', 'HRORGTYPE00000000000', '~', '0000TW10000006ORGREF', '是否啟用的同步投保級距功能，功能按鈕位置：人力資本-人員資訊管理-資訊維護-員工資訊維護。', 'Y', 2, 'N', 0, '2015-06-03 00:59:11', '是/否', 1);
INSERT INTO pub_sysinittemp (afterclass, checkclass, dataclass, dataoriginflag, defaultvalue, domainflag, dr, editcomponentctrlclass, groupcode, groupname, initcode, initname, mainflag, mutexflag, orgtypeconvertmode, paratype, pk_orgtype, pk_refinfo, pk_sysinittemp, remark, showflag, stateflag, sysflag, sysindex, ts, valuelist, valuetype) VALUES (null, null, null, 0, '發放日期', '68J6', null, null, 'TWHR', '台灣人力資源', 'TWHR07', '綜所稅申報薪資發放依據', 'N', 0, 'HRORGTYPE00000000000', 'business', 'HRORGTYPE00000000000', '~', '1001ZZ100000000270WT', '發放日期：薪資實際發放日期所在期間，薪資期間：薪資在系統中所屬的薪資期間。', 'Y', 1, 'N', 0, '2015-12-01 17:39:39', '發放日期,薪資期間', 2);


insert into twhr_rangetable (pk_rangetable, tabletype, doctime, docno, startdate, enddate, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, ts, dr, enabled)
select replace(pk_org, '000000000', 'RANGETBL3'), 3, '2017-01-04 17:02:17', 'TWRTINIT201701010003', '2017-01-01 00:00:00', '2017-12-31 23:59:59', pk_group, pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2017-01-04 17:02:57', '~', null, '2017-01-04 17:02:57', 0, 'N'
from org_orgs
where isbusinessunit='Y';


insert into twhr_rangetable (pk_rangetable, tabletype, doctime, docno, startdate, enddate, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, ts, dr, enabled)
select replace(pk_org, '000000000', 'RANGETBL1'),  1, '2017-01-04 17:11:38', 'TWRTINIT201701010001', '2017-01-01 00:00:00', '2017-12-31 23:59:59', pk_group, pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2017-01-04 17:12:58', '~', null, '2017-01-04 17:12:58', 0, 'N'
from org_orgs
where isbusinessunit='Y';

insert into twhr_rangetable (pk_rangetable, tabletype, doctime, docno, startdate, enddate, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, ts, dr, enabled)
select replace(pk_org, '000000000', 'RANGETBL2'),  2, '2017-01-04 17:14:01', 'TWRTINIT201701010002', '2017-01-01 00:00:00', '2017-12-31 23:59:59', pk_group, pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2017-01-04 17:15:07', '~', null, '2017-01-04 17:15:07', 0, 'N'
from org_orgs
where isbusinessunit='Y';


INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '1', '~', null, '~', null, null, '1', 0, 20008.00000000, null, null, 282.00000000, 906.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '2', '~', null, '~', null, null, '2', 20009.00000000, 21009.00000000, null, null, 296.00000000, 952.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '3', '~', null, '~', null, null, '3', 21010.00000000, 21900.00000000, null, null, 308.00000000, 992.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '4', '~', null, '~', null, null, '4', 21901.00000000, 22800.00000000, null, null, 321.00000000, 1033.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '5', '~', null, '~', null, null, '5', 22801.00000000, 24000.00000000, null, null, 338.00000000, 1087.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '6', '~', null, '~', null, null, '6', 24001.00000000, 25200.00000000, null, null, 355.00000000, 1142.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '7', '~', null, '~', null, null, '7', 25201.00000000, 26400.00000000, null, null, 371.00000000, 1196.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '8', '~', null, '~', null, null, '8', 26401.00000000, 27600.00000000, null, null, 388.00000000, 1250.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '9', '~', null, '~', null, null, '9', 27601.00000000, 28800.00000000, null, null, 405.00000000, 1305.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '10', '~', null, '~', null, null, '10', 28801.00000000, 30300.00000000, null, null, 426.00000000, 1373.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '11', '~', null, '~', null, null, '11', 30301.00000000, 31800.00000000, null, null, 447.00000000, 1441.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '12', '~', null, '~', null, null, '12', 31801.00000000, 33300.00000000, null, null, 469.00000000, 1509.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '13', '~', null, '~', null, null, '13', 33301.00000000, 34800.00000000, null, null, 490.00000000, 1577.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '14', '~', null, '~', null, null, '14', 34801.00000000, 36300.00000000, null, null, 511.00000000, 1645.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '15', '~', null, '~', null, null, '15', 36301.00000000, 38200.00000000, null, null, 537.00000000, 1731.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '16', '~', null, '~', null, null, '16', 38201.00000000, 40100.00000000, null, null, 564.00000000, 1817.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '17', '~', null, '~', null, null, '17', 40101.00000000, 42000.00000000, null, null, 591.00000000, 1903.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '18', '~', null, '~', null, null, '18', 42001.00000000, 43900.00000000, null, null, 618.00000000, 1989.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '19', '~', null, '~', null, null, '19', 43901.00000000, 45800.00000000, null, null, 644.00000000, 2075.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '20', '~', null, '~', null, null, '20', 45801.00000000, 48200.00000000, null, null, 678.00000000, 2184.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '21', '~', null, '~', null, null, '21', 48201.00000000, 50600.00000000, null, null, 712.00000000, 2292.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '22', '~', null, '~', null, null, '22', 50601.00000000, 53000.00000000, null, null, 746.00000000, 2401.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '23', '~', null, '~', null, null, '23', 53001.00000000, 55400.00000000, null, null, 779.00000000, 2510.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '24', '~', null, '~', null, null, '24', 55401.00000000, 57800.00000000, null, null, 813.00000000, 2619.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '25', '~', null, '~', null, null, '25', 57801.00000000, 60800.00000000, null, null, 855.00000000, 2755.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '26', '~', null, '~', null, null, '26', 60801.00000000, 63800.00000000, null, null, 898.00000000, 2890.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '27', '~', null, '~', null, null, '27', 63801.00000000, 66800.00000000, null, null, 940.00000000, 3026.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '28', '~', null, '~', null, null, '28', 66801.00000000, 69800.00000000, null, null, 982.00000000, 3162.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '29', '~', null, '~', null, null, '29', 69801.00000000, 72800.00000000, null, null, 1024.00000000, 3298.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '30', '~', null, '~', null, null, '30', 72801.00000000, 76500.00000000, null, null, 1076.00000000, 3466.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '31', '~', null, '~', null, null, '31', 76501.00000000, 80200.00000000, null, null, 1128.00000000, 3633.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '32', '~', null, '~', null, null, '32', 80201.00000000, 83900.00000000, null, null, 1180.00000000, 3801.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '33', '~', null, '~', null, null, '33', 83901.00000000, 87600.00000000, null, null, 1233.00000000, 3969.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '34', '~', null, '~', null, null, '34', 87601.00000000, 92100.00000000, null, null, 1296.00000000, 4173.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '35', '~', null, '~', null, null, '35', 92101.00000000, 96600.00000000, null, null, 1359.00000000, 4377.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '36', '~', null, '~', null, null, '36', 96601.00000000, 101100.00000000, null, null, 1422.00000000, 4580.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '37', '~', null, '~', null, null, '37', 101101.00000000, 105600.00000000, null, null, 1486.00000000, 4784.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '38', '~', null, '~', null, null, '38', 105601.00000000, 110100.00000000, null, null, 1549.00000000, 4988.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '39', '~', null, '~', null, null, '39', 110101.00000000, 115500.00000000, null, null, 1625.00000000, 5233.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '40', '~', null, '~', null, null, '40', 115501.00000000, 120900.00000000, null, null, 1701.00000000, 5477.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '41', '~', null, '~', null, null, '41', 120901.00000000, 126300.00000000, null, null, 1777.00000000, 5722.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '42', '~', null, '~', null, null, '42', 126301.00000000, 131700.00000000, null, null, 1853.00000000, 5967.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '43', '~', null, '~', null, null, '43', 131701.00000000, 137100.00000000, null, null, 1929.00000000, 6211.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '44', '~', null, '~', null, null, '44', 137101.00000000, 142500.00000000, null, null, 2005.00000000, 6456.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '45', '~', null, '~', null, null, '45', 142501.00000000, 147900.00000000, null, null, 2081.00000000, 6701.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '46', '~', null, '~', null, null, '46', 147901.00000000, 150000.00000000, null, null, 2111.00000000, 6796.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '47', '~', null, '~', null, null, '47', 150001.00000000, 156400.00000000, null, null, 2201.00000000, 7086.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '48', '~', null, '~', null, null, '48', 156401.00000000, 162800.00000000, null, null, 2291.00000000, 7376.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '49', '~', null, '~', null, null, '49', 162801.00000000, 169200.00000000, null, null, 2381.00000000, 7666.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '50', '~', null, '~', null, null, '50', 169201.00000000, 175600.00000000, null, null, 2471.00000000, 7956.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '51', '~', null, '~', null, null, '51', 175601.00000000, 182000.00000000, null, null, 2561.00000000, 8246.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL3'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '52', '~', null, '~', null, null, '52', 182001.00000000, 0, null, null, 2561.00000000, 8246.00000000, null, '2017-01-04 17:02:57', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '1', '~', null, '~', null, '1', '1', 0, 11100.00000000, null, null, 8.00000000, 28.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '2', '~', null, '~', null, '1', '2', 0, 11100.00000000, null, null, 15.00000000, 54.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '3', '~', null, '~', null, '1', '3', 0, 11100.00000000, null, null, 23.00000000, 82.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '4', '~', null, '~', null, '1', '4', 0, 11100.00000000, null, null, 31.00000000, 108.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '5', '~', null, '~', null, '1', '5', 0, 11100.00000000, null, null, 39.00000000, 136.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '6', '~', null, '~', null, '1', '6', 0, 11100.00000000, null, null, 46.00000000, 164.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '7', '~', null, '~', null, '1', '7', 0, 11100.00000000, null, null, 54.00000000, 190.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '8', '~', null, '~', null, '1', '8', 0, 11100.00000000, null, null, 62.00000000, 218.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '9', '~', null, '~', null, '1', '9', 0, 11100.00000000, null, null, 70.00000000, 244.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '10', '~', null, '~', null, '1', '10', 0, 11100.00000000, null, null, 77.00000000, 272.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '11', '~', null, '~', null, '1', '11', 0, 11100.00000000, null, null, 85.00000000, 299.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '12', '~', null, '~', null, '1', '12', 0, 11100.00000000, null, null, 93.00000000, 326.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '13', '~', null, '~', null, '1', '13', 0, 11100.00000000, null, null, 101.00000000, 354.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '14', '~', null, '~', null, '1', '14', 0, 11100.00000000, null, null, 108.00000000, 380.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '15', '~', null, '~', null, '1', '15', 0, 11100.00000000, null, null, 116.00000000, 408.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '16', '~', null, '~', null, '1', '16', 0, 11100.00000000, null, null, 124.00000000, 435.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '17', '~', null, '~', null, '1', '17', 0, 11100.00000000, null, null, 133.00000000, 462.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '18', '~', null, '~', null, '1', '18', 0, 11100.00000000, null, null, 140.00000000, 490.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '19', '~', null, '~', null, '1', '19', 0, 11100.00000000, null, null, 148.00000000, 516.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '20', '~', null, '~', null, '1', '20', 0, 11100.00000000, null, null, 156.00000000, 544.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '21', '~', null, '~', null, '1', '21', 0, 11100.00000000, null, null, 164.00000000, 571.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '22', '~', null, '~', null, '1', '22', 0, 11100.00000000, null, null, 171.00000000, 598.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '23', '~', null, '~', null, '1', '23', 0, 11100.00000000, null, null, 179.00000000, 626.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '24', '~', null, '~', null, '1', '24', 0, 11100.00000000, null, null, 187.00000000, 653.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '25', '~', null, '~', null, '1', '25', 0, 11100.00000000, null, null, 195.00000000, 680.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '26', '~', null, '~', null, '1', '26', 0, 11100.00000000, null, null, 202.00000000, 707.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '27', '~', null, '~', null, '1', '27', 0, 11100.00000000, null, null, 210.00000000, 734.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '28', '~', null, '~', null, '1', '28', 0, 11100.00000000, null, null, 218.00000000, 762.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '29', '~', null, '~', null, '1', '29', 0, 11100.00000000, null, null, 225.00000000, 789.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '30', '~', null, '~', null, '1', '30', 0, 11100.00000000, null, null, 233.00000000, 816.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '31', '~', null, '~', null, '2', '1', 11101.00000000, 12540.00000000, null, null, 9.00000000, 31.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '32', '~', null, '~', null, '2', '2', 11101.00000000, 12540.00000000, null, null, 18.00000000, 62.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '33', '~', null, '~', null, '2', '3', 11101.00000000, 12540.00000000, null, null, 27.00000000, 92.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '34', '~', null, '~', null, '2', '4', 11101.00000000, 12540.00000000, null, null, 35.00000000, 123.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '35', '~', null, '~', null, '2', '5', 11101.00000000, 12540.00000000, null, null, 44.00000000, 154.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '36', '~', null, '~', null, '2', '6', 11101.00000000, 12540.00000000, null, null, 53.00000000, 185.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '37', '~', null, '~', null, '2', '7', 11101.00000000, 12540.00000000, null, null, 62.00000000, 215.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '38', '~', null, '~', null, '2', '8', 11101.00000000, 12540.00000000, null, null, 71.00000000, 245.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '39', '~', null, '~', null, '2', '9', 11101.00000000, 12540.00000000, null, null, 79.00000000, 276.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '40', '~', null, '~', null, '2', '10', 11101.00000000, 12540.00000000, null, null, 87.00000000, 307.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '41', '~', null, '~', null, '2', '11', 11101.00000000, 12540.00000000, null, null, 96.00000000, 338.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '42', '~', null, '~', null, '2', '12', 11101.00000000, 12540.00000000, null, null, 105.00000000, 369.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '43', '~', null, '~', null, '2', '13', 11101.00000000, 12540.00000000, null, null, 114.00000000, 399.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '44', '~', null, '~', null, '2', '14', 11101.00000000, 12540.00000000, null, null, 123.00000000, 430.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '45', '~', null, '~', null, '2', '15', 11101.00000000, 12540.00000000, null, null, 132.00000000, 461.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '46', '~', null, '~', null, '2', '16', 11101.00000000, 12540.00000000, null, null, 140.00000000, 492.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '47', '~', null, '~', null, '2', '17', 11101.00000000, 12540.00000000, null, null, 149.00000000, 523.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '48', '~', null, '~', null, '2', '18', 11101.00000000, 12540.00000000, null, null, 158.00000000, 553.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '49', '~', null, '~', null, '2', '19', 11101.00000000, 12540.00000000, null, null, 167.00000000, 584.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '50', '~', null, '~', null, '2', '20', 11101.00000000, 12540.00000000, null, null, 176.00000000, 615.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '51', '~', null, '~', null, '2', '21', 11101.00000000, 12540.00000000, null, null, 185.00000000, 645.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '52', '~', null, '~', null, '2', '22', 11101.00000000, 12540.00000000, null, null, 193.00000000, 676.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '53', '~', null, '~', null, '2', '23', 11101.00000000, 12540.00000000, null, null, 202.00000000, 706.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '54', '~', null, '~', null, '2', '24', 11101.00000000, 12540.00000000, null, null, 211.00000000, 737.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '55', '~', null, '~', null, '2', '25', 11101.00000000, 12540.00000000, null, null, 220.00000000, 768.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '56', '~', null, '~', null, '2', '26', 11101.00000000, 12540.00000000, null, null, 228.00000000, 799.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '57', '~', null, '~', null, '2', '27', 11101.00000000, 12540.00000000, null, null, 237.00000000, 830.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '58', '~', null, '~', null, '2', '28', 11101.00000000, 12540.00000000, null, null, 245.00000000, 860.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '59', '~', null, '~', null, '2', '29', 11101.00000000, 12540.00000000, null, null, 254.00000000, 891.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '60', '~', null, '~', null, '2', '30', 11101.00000000, 12540.00000000, null, null, 263.00000000, 922.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '61', '~', null, '~', null, '3', '1', 12541.00000000, 13500.00000000, null, null, 10.00000000, 33.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '62', '~', null, '~', null, '3', '2', 12541.00000000, 13500.00000000, null, null, 19.00000000, 66.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '63', '~', null, '~', null, '3', '3', 12541.00000000, 13500.00000000, null, null, 29.00000000, 99.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '64', '~', null, '~', null, '3', '4', 12541.00000000, 13500.00000000, null, null, 38.00000000, 133.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '65', '~', null, '~', null, '3', '5', 12541.00000000, 13500.00000000, null, null, 48.00000000, 166.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '66', '~', null, '~', null, '3', '6', 12541.00000000, 13500.00000000, null, null, 56.00000000, 199.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '67', '~', null, '~', null, '3', '7', 12541.00000000, 13500.00000000, null, null, 66.00000000, 231.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '68', '~', null, '~', null, '3', '8', 12541.00000000, 13500.00000000, null, null, 75.00000000, 264.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '69', '~', null, '~', null, '3', '9', 12541.00000000, 13500.00000000, null, null, 85.00000000, 297.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '70', '~', null, '~', null, '3', '10', 12541.00000000, 13500.00000000, null, null, 95.00000000, 331.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '71', '~', null, '~', null, '3', '11', 12541.00000000, 13500.00000000, null, null, 104.00000000, 364.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '72', '~', null, '~', null, '3', '12', 12541.00000000, 13500.00000000, null, null, 114.00000000, 397.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '73', '~', null, '~', null, '3', '13', 12541.00000000, 13500.00000000, null, null, 123.00000000, 430.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '74', '~', null, '~', null, '3', '14', 12541.00000000, 13500.00000000, null, null, 133.00000000, 463.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '75', '~', null, '~', null, '3', '15', 12541.00000000, 13500.00000000, null, null, 142.00000000, 496.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '76', '~', null, '~', null, '3', '16', 12541.00000000, 13500.00000000, null, null, 151.00000000, 529.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '77', '~', null, '~', null, '3', '17', 12541.00000000, 13500.00000000, null, null, 160.00000000, 563.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '78', '~', null, '~', null, '3', '18', 12541.00000000, 13500.00000000, null, null, 170.00000000, 596.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '79', '~', null, '~', null, '3', '19', 12541.00000000, 13500.00000000, null, null, 179.00000000, 629.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '80', '~', null, '~', null, '3', '20', 12541.00000000, 13500.00000000, null, null, 189.00000000, 662.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '81', '~', null, '~', null, '3', '21', 12541.00000000, 13500.00000000, null, null, 199.00000000, 694.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '82', '~', null, '~', null, '3', '22', 12541.00000000, 13500.00000000, null, null, 208.00000000, 727.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '83', '~', null, '~', null, '3', '23', 12541.00000000, 13500.00000000, null, null, 218.00000000, 760.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '84', '~', null, '~', null, '3', '24', 12541.00000000, 13500.00000000, null, null, 227.00000000, 794.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '85', '~', null, '~', null, '3', '25', 12541.00000000, 13500.00000000, null, null, 237.00000000, 827.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '86', '~', null, '~', null, '3', '26', 12541.00000000, 13500.00000000, null, null, 245.00000000, 860.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '87', '~', null, '~', null, '3', '27', 12541.00000000, 13500.00000000, null, null, 255.00000000, 893.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '88', '~', null, '~', null, '3', '28', 12541.00000000, 13500.00000000, null, null, 264.00000000, 926.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '89', '~', null, '~', null, '3', '29', 12541.00000000, 13500.00000000, null, null, 274.00000000, 959.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '90', '~', null, '~', null, '3', '30', 12541.00000000, 13500.00000000, null, null, 284.00000000, 993.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '91', '~', null, '~', null, '4', '1', 13501.00000000, 15840.00000000, null, null, 11.00000000, 39.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '92', '~', null, '~', null, '4', '2', 13501.00000000, 15840.00000000, null, null, 22.00000000, 77.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '93', '~', null, '~', null, '4', '3', 13501.00000000, 15840.00000000, null, null, 33.00000000, 116.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '94', '~', null, '~', null, '4', '4', 13501.00000000, 15840.00000000, null, null, 44.00000000, 155.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '95', '~', null, '~', null, '4', '5', 13501.00000000, 15840.00000000, null, null, 55.00000000, 194.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '96', '~', null, '~', null, '4', '6', 13501.00000000, 15840.00000000, null, null, 66.00000000, 233.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '97', '~', null, '~', null, '4', '7', 13501.00000000, 15840.00000000, null, null, 77.00000000, 272.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '98', '~', null, '~', null, '4', '8', 13501.00000000, 15840.00000000, null, null, 88.00000000, 311.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '99', '~', null, '~', null, '4', '9', 13501.00000000, 15840.00000000, null, null, 100.00000000, 349.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '100', '~', null, '~', null, '4', '10', 13501.00000000, 15840.00000000, null, null, 111.00000000, 388.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '101', '~', null, '~', null, '4', '11', 13501.00000000, 15840.00000000, null, null, 122.00000000, 427.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '102', '~', null, '~', null, '4', '12', 13501.00000000, 15840.00000000, null, null, 133.00000000, 465.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '103', '~', null, '~', null, '4', '13', 13501.00000000, 15840.00000000, null, null, 144.00000000, 504.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '104', '~', null, '~', null, '4', '14', 13501.00000000, 15840.00000000, null, null, 155.00000000, 544.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '105', '~', null, '~', null, '4', '15', 13501.00000000, 15840.00000000, null, null, 166.00000000, 582.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '106', '~', null, '~', null, '4', '16', 13501.00000000, 15840.00000000, null, null, 178.00000000, 621.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '107', '~', null, '~', null, '4', '17', 13501.00000000, 15840.00000000, null, null, 189.00000000, 660.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '108', '~', null, '~', null, '4', '18', 13501.00000000, 15840.00000000, null, null, 200.00000000, 699.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '109', '~', null, '~', null, '4', '19', 13501.00000000, 15840.00000000, null, null, 211.00000000, 737.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '110', '~', null, '~', null, '4', '20', 13501.00000000, 15840.00000000, null, null, 222.00000000, 776.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '111', '~', null, '~', null, '4', '21', 13501.00000000, 15840.00000000, null, null, 233.00000000, 815.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '112', '~', null, '~', null, '4', '22', 13501.00000000, 15840.00000000, null, null, 244.00000000, 853.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '113', '~', null, '~', null, '4', '23', 13501.00000000, 15840.00000000, null, null, 255.00000000, 893.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '114', '~', null, '~', null, '4', '24', 13501.00000000, 15840.00000000, null, null, 266.00000000, 932.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '115', '~', null, '~', null, '4', '25', 13501.00000000, 15840.00000000, null, null, 277.00000000, 970.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '116', '~', null, '~', null, '4', '26', 13501.00000000, 15840.00000000, null, null, 288.00000000, 1009.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '117', '~', null, '~', null, '4', '27', 13501.00000000, 15840.00000000, null, null, 300.00000000, 1048.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '118', '~', null, '~', null, '4', '28', 13501.00000000, 15840.00000000, null, null, 311.00000000, 1086.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '119', '~', null, '~', null, '4', '29', 13501.00000000, 15840.00000000, null, null, 322.00000000, 1125.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '120', '~', null, '~', null, '4', '30', 13501.00000000, 15840.00000000, null, null, 333.00000000, 1164.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '121', '~', null, '~', null, '5', '1', 15841.00000000, 16500.00000000, null, null, 11.00000000, 41.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '122', '~', null, '~', null, '5', '2', 15841.00000000, 16500.00000000, null, null, 23.00000000, 81.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '123', '~', null, '~', null, '5', '3', 15841.00000000, 16500.00000000, null, null, 34.00000000, 122.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '124', '~', null, '~', null, '5', '4', 15841.00000000, 16500.00000000, null, null, 46.00000000, 161.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '125', '~', null, '~', null, '5', '5', 15841.00000000, 16500.00000000, null, null, 58.00000000, 202.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '126', '~', null, '~', null, '5', '6', 15841.00000000, 16500.00000000, null, null, 70.00000000, 242.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '127', '~', null, '~', null, '5', '7', 15841.00000000, 16500.00000000, null, null, 81.00000000, 283.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '128', '~', null, '~', null, '5', '8', 15841.00000000, 16500.00000000, null, null, 93.00000000, 324.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '129', '~', null, '~', null, '5', '9', 15841.00000000, 16500.00000000, null, null, 104.00000000, 364.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '130', '~', null, '~', null, '5', '10', 15841.00000000, 16500.00000000, null, null, 116.00000000, 405.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '131', '~', null, '~', null, '5', '11', 15841.00000000, 16500.00000000, null, null, 127.00000000, 444.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '132', '~', null, '~', null, '5', '12', 15841.00000000, 16500.00000000, null, null, 138.00000000, 485.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '133', '~', null, '~', null, '5', '13', 15841.00000000, 16500.00000000, null, null, 150.00000000, 525.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '134', '~', null, '~', null, '5', '14', 15841.00000000, 16500.00000000, null, null, 161.00000000, 566.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '135', '~', null, '~', null, '5', '15', 15841.00000000, 16500.00000000, null, null, 174.00000000, 607.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '136', '~', null, '~', null, '5', '16', 15841.00000000, 16500.00000000, null, null, 185.00000000, 647.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '137', '~', null, '~', null, '5', '17', 15841.00000000, 16500.00000000, null, null, 197.00000000, 687.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '138', '~', null, '~', null, '5', '18', 15841.00000000, 16500.00000000, null, null, 208.00000000, 727.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '139', '~', null, '~', null, '5', '19', 15841.00000000, 16500.00000000, null, null, 220.00000000, 768.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '140', '~', null, '~', null, '5', '20', 15841.00000000, 16500.00000000, null, null, 231.00000000, 809.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '141', '~', null, '~', null, '5', '21', 15841.00000000, 16500.00000000, null, null, 242.00000000, 849.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '142', '~', null, '~', null, '5', '22', 15841.00000000, 16500.00000000, null, null, 254.00000000, 890.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '143', '~', null, '~', null, '5', '23', 15841.00000000, 16500.00000000, null, null, 265.00000000, 930.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '144', '~', null, '~', null, '5', '24', 15841.00000000, 16500.00000000, null, null, 277.00000000, 970.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '145', '~', null, '~', null, '5', '25', 15841.00000000, 16500.00000000, null, null, 289.00000000, 1010.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '146', '~', null, '~', null, '5', '26', 15841.00000000, 16500.00000000, null, null, 301.00000000, 1051.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '147', '~', null, '~', null, '5', '27', 15841.00000000, 16500.00000000, null, null, 312.00000000, 1092.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '148', '~', null, '~', null, '5', '28', 15841.00000000, 16500.00000000, null, null, 324.00000000, 1132.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '149', '~', null, '~', null, '5', '29', 15841.00000000, 16500.00000000, null, null, 335.00000000, 1173.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '150', '~', null, '~', null, '5', '30', 15841.00000000, 16500.00000000, null, null, 347.00000000, 1213.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '151', '~', null, '~', null, '6', '1', 16501.00000000, 17280.00000000, null, null, 12.00000000, 42.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '152', '~', null, '~', null, '6', '2', 16501.00000000, 17280.00000000, null, null, 24.00000000, 85.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '153', '~', null, '~', null, '6', '3', 16501.00000000, 17280.00000000, null, null, 36.00000000, 127.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '154', '~', null, '~', null, '6', '4', 16501.00000000, 17280.00000000, null, null, 49.00000000, 169.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '155', '~', null, '~', null, '6', '5', 16501.00000000, 17280.00000000, null, null, 61.00000000, 212.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '156', '~', null, '~', null, '6', '6', 16501.00000000, 17280.00000000, null, null, 73.00000000, 254.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '157', '~', null, '~', null, '6', '7', 16501.00000000, 17280.00000000, null, null, 85.00000000, 296.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '158', '~', null, '~', null, '6', '8', 16501.00000000, 17280.00000000, null, null, 97.00000000, 338.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '159', '~', null, '~', null, '6', '9', 16501.00000000, 17280.00000000, null, null, 108.00000000, 381.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '160', '~', null, '~', null, '6', '10', 16501.00000000, 17280.00000000, null, null, 121.00000000, 423.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '161', '~', null, '~', null, '6', '11', 16501.00000000, 17280.00000000, null, null, 133.00000000, 465.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '162', '~', null, '~', null, '6', '12', 16501.00000000, 17280.00000000, null, null, 145.00000000, 508.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '163', '~', null, '~', null, '6', '13', 16501.00000000, 17280.00000000, null, null, 157.00000000, 550.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '164', '~', null, '~', null, '6', '14', 16501.00000000, 17280.00000000, null, null, 169.00000000, 592.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '165', '~', null, '~', null, '6', '15', 16501.00000000, 17280.00000000, null, null, 181.00000000, 635.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '166', '~', null, '~', null, '6', '16', 16501.00000000, 17280.00000000, null, null, 193.00000000, 678.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '167', '~', null, '~', null, '6', '17', 16501.00000000, 17280.00000000, null, null, 206.00000000, 720.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '168', '~', null, '~', null, '6', '18', 16501.00000000, 17280.00000000, null, null, 218.00000000, 762.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '169', '~', null, '~', null, '6', '19', 16501.00000000, 17280.00000000, null, null, 230.00000000, 805.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '170', '~', null, '~', null, '6', '20', 16501.00000000, 17280.00000000, null, null, 242.00000000, 847.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '171', '~', null, '~', null, '6', '21', 16501.00000000, 17280.00000000, null, null, 254.00000000, 889.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '172', '~', null, '~', null, '6', '22', 16501.00000000, 17280.00000000, null, null, 266.00000000, 932.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '173', '~', null, '~', null, '6', '23', 16501.00000000, 17280.00000000, null, null, 278.00000000, 974.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '174', '~', null, '~', null, '6', '24', 16501.00000000, 17280.00000000, null, null, 291.00000000, 1016.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '175', '~', null, '~', null, '6', '25', 16501.00000000, 17280.00000000, null, null, 303.00000000, 1059.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '176', '~', null, '~', null, '6', '26', 16501.00000000, 17280.00000000, null, null, 315.00000000, 1101.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '177', '~', null, '~', null, '6', '27', 16501.00000000, 17280.00000000, null, null, 326.00000000, 1143.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '178', '~', null, '~', null, '6', '28', 16501.00000000, 17280.00000000, null, null, 338.00000000, 1186.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '179', '~', null, '~', null, '6', '29', 16501.00000000, 17280.00000000, null, null, 350.00000000, 1228.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '180', '~', null, '~', null, '6', '30', 16501.00000000, 17280.00000000, null, null, 363.00000000, 1270.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '181', '~', null, '~', null, '7', '1', 17281.00000000, 17880.00000000, null, null, 12.00000000, 44.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '182', '~', null, '~', null, '7', '2', 17281.00000000, 17880.00000000, null, null, 25.00000000, 87.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '183', '~', null, '~', null, '7', '3', 17281.00000000, 17880.00000000, null, null, 38.00000000, 132.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '184', '~', null, '~', null, '7', '4', 17281.00000000, 17880.00000000, null, null, 50.00000000, 176.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '185', '~', null, '~', null, '7', '5', 17281.00000000, 17880.00000000, null, null, 63.00000000, 219.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '186', '~', null, '~', null, '7', '6', 17281.00000000, 17880.00000000, null, null, 75.00000000, 263.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '187', '~', null, '~', null, '7', '7', 17281.00000000, 17880.00000000, null, null, 87.00000000, 306.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '188', '~', null, '~', null, '7', '8', 17281.00000000, 17880.00000000, null, null, 101.00000000, 350.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '189', '~', null, '~', null, '7', '9', 17281.00000000, 17880.00000000, null, null, 113.00000000, 395.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '190', '~', null, '~', null, '7', '10', 17281.00000000, 17880.00000000, null, null, 125.00000000, 438.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '191', '~', null, '~', null, '7', '11', 17281.00000000, 17880.00000000, null, null, 138.00000000, 482.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '192', '~', null, '~', null, '7', '12', 17281.00000000, 17880.00000000, null, null, 150.00000000, 526.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '193', '~', null, '~', null, '7', '13', 17281.00000000, 17880.00000000, null, null, 162.00000000, 569.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '194', '~', null, '~', null, '7', '14', 17281.00000000, 17880.00000000, null, null, 176.00000000, 613.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '195', '~', null, '~', null, '7', '15', 17281.00000000, 17880.00000000, null, null, 188.00000000, 658.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '196', '~', null, '~', null, '7', '16', 17281.00000000, 17880.00000000, null, null, 200.00000000, 701.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '197', '~', null, '~', null, '7', '17', 17281.00000000, 17880.00000000, null, null, 213.00000000, 745.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '198', '~', null, '~', null, '7', '18', 17281.00000000, 17880.00000000, null, null, 225.00000000, 788.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '199', '~', null, '~', null, '7', '19', 17281.00000000, 17880.00000000, null, null, 238.00000000, 832.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '200', '~', null, '~', null, '7', '20', 17281.00000000, 17880.00000000, null, null, 250.00000000, 876.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '201', '~', null, '~', null, '7', '21', 17281.00000000, 17880.00000000, null, null, 263.00000000, 920.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '202', '~', null, '~', null, '7', '22', 17281.00000000, 17880.00000000, null, null, 275.00000000, 964.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '203', '~', null, '~', null, '7', '23', 17281.00000000, 17880.00000000, null, null, 287.00000000, 1008.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '204', '~', null, '~', null, '7', '24', 17281.00000000, 17880.00000000, null, null, 301.00000000, 1051.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '205', '~', null, '~', null, '7', '25', 17281.00000000, 17880.00000000, null, null, 313.00000000, 1095.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '206', '~', null, '~', null, '7', '26', 17281.00000000, 17880.00000000, null, null, 325.00000000, 1138.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '207', '~', null, '~', null, '7', '27', 17281.00000000, 17880.00000000, null, null, 338.00000000, 1183.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '208', '~', null, '~', null, '7', '28', 17281.00000000, 17880.00000000, null, null, 350.00000000, 1227.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '209', '~', null, '~', null, '7', '29', 17281.00000000, 17880.00000000, null, null, 363.00000000, 1270.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '210', '~', null, '~', null, '7', '30', 17281.00000000, 17880.00000000, null, null, 376.00000000, 1314.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '211', '~', null, '~', null, '8', '1', 17881.00000000, 19047.00000000, null, null, 13.00000000, 46.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '212', '~', null, '~', null, '8', '2', 17881.00000000, 19047.00000000, null, null, 27.00000000, 93.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '213', '~', null, '~', null, '8', '3', 17881.00000000, 19047.00000000, null, null, 40.00000000, 140.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '214', '~', null, '~', null, '8', '4', 17881.00000000, 19047.00000000, null, null, 53.00000000, 187.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '215', '~', null, '~', null, '8', '5', 17881.00000000, 19047.00000000, null, null, 66.00000000, 233.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '216', '~', null, '~', null, '8', '6', 17881.00000000, 19047.00000000, null, null, 80.00000000, 280.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '217', '~', null, '~', null, '8', '7', 17881.00000000, 19047.00000000, null, null, 93.00000000, 327.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '218', '~', null, '~', null, '8', '8', 17881.00000000, 19047.00000000, null, null, 107.00000000, 374.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '219', '~', null, '~', null, '8', '9', 17881.00000000, 19047.00000000, null, null, 120.00000000, 420.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '220', '~', null, '~', null, '8', '10', 17881.00000000, 19047.00000000, null, null, 134.00000000, 466.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '221', '~', null, '~', null, '8', '11', 17881.00000000, 19047.00000000, null, null, 147.00000000, 513.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '222', '~', null, '~', null, '8', '12', 17881.00000000, 19047.00000000, null, null, 160.00000000, 560.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '223', '~', null, '~', null, '8', '13', 17881.00000000, 19047.00000000, null, null, 174.00000000, 607.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '224', '~', null, '~', null, '8', '14', 17881.00000000, 19047.00000000, null, null, 187.00000000, 653.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '225', '~', null, '~', null, '8', '15', 17881.00000000, 19047.00000000, null, null, 200.00000000, 700.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '226', '~', null, '~', null, '8', '16', 17881.00000000, 19047.00000000, null, null, 213.00000000, 747.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '227', '~', null, '~', null, '8', '17', 17881.00000000, 19047.00000000, null, null, 227.00000000, 794.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '228', '~', null, '~', null, '8', '18', 17881.00000000, 19047.00000000, null, null, 240.00000000, 840.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '229', '~', null, '~', null, '8', '19', 17881.00000000, 19047.00000000, null, null, 253.00000000, 886.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '230', '~', null, '~', null, '8', '20', 17881.00000000, 19047.00000000, null, null, 266.00000000, 933.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '231', '~', null, '~', null, '8', '21', 17881.00000000, 19047.00000000, null, null, 280.00000000, 980.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '232', '~', null, '~', null, '8', '22', 17881.00000000, 19047.00000000, null, null, 293.00000000, 1027.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '233', '~', null, '~', null, '8', '23', 17881.00000000, 19047.00000000, null, null, 306.00000000, 1073.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '234', '~', null, '~', null, '8', '24', 17881.00000000, 19047.00000000, null, null, 320.00000000, 1120.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '235', '~', null, '~', null, '8', '25', 17881.00000000, 19047.00000000, null, null, 334.00000000, 1167.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '236', '~', null, '~', null, '8', '26', 17881.00000000, 19047.00000000, null, null, 347.00000000, 1214.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '237', '~', null, '~', null, '8', '27', 17881.00000000, 19047.00000000, null, null, 360.00000000, 1260.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '238', '~', null, '~', null, '8', '28', 17881.00000000, 19047.00000000, null, null, 374.00000000, 1306.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '239', '~', null, '~', null, '8', '29', 17881.00000000, 19047.00000000, null, null, 387.00000000, 1353.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '240', '~', null, '~', null, '8', '30', 17881.00000000, 19047.00000000, null, null, 400.00000000, 1400.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '241', '~', null, '~', null, '9', '1', 19048.00000000, 20008.00000000, null, null, 14.00000000, 49.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '242', '~', null, '~', null, '9', '2', 19048.00000000, 20008.00000000, null, null, 28.00000000, 98.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '243', '~', null, '~', null, '9', '3', 19048.00000000, 20008.00000000, null, null, 42.00000000, 147.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '244', '~', null, '~', null, '9', '4', 19048.00000000, 20008.00000000, null, null, 56.00000000, 196.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '245', '~', null, '~', null, '9', '5', 19048.00000000, 20008.00000000, null, null, 70.00000000, 245.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '246', '~', null, '~', null, '9', '6', 19048.00000000, 20008.00000000, null, null, 84.00000000, 294.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '247', '~', null, '~', null, '9', '7', 19048.00000000, 20008.00000000, null, null, 98.00000000, 343.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '248', '~', null, '~', null, '9', '8', 19048.00000000, 20008.00000000, null, null, 112.00000000, 392.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '249', '~', null, '~', null, '9', '9', 19048.00000000, 20008.00000000, null, null, 126.00000000, 441.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '250', '~', null, '~', null, '9', '10', 19048.00000000, 20008.00000000, null, null, 140.00000000, 491.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '251', '~', null, '~', null, '9', '11', 19048.00000000, 20008.00000000, null, null, 154.00000000, 539.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '252', '~', null, '~', null, '9', '12', 19048.00000000, 20008.00000000, null, null, 168.00000000, 588.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '253', '~', null, '~', null, '9', '13', 19048.00000000, 20008.00000000, null, null, 182.00000000, 638.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '254', '~', null, '~', null, '9', '14', 19048.00000000, 20008.00000000, null, null, 196.00000000, 686.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '255', '~', null, '~', null, '9', '15', 19048.00000000, 20008.00000000, null, null, 210.00000000, 735.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '256', '~', null, '~', null, '9', '16', 19048.00000000, 20008.00000000, null, null, 224.00000000, 785.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '257', '~', null, '~', null, '9', '17', 19048.00000000, 20008.00000000, null, null, 238.00000000, 833.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '258', '~', null, '~', null, '9', '18', 19048.00000000, 20008.00000000, null, null, 252.00000000, 882.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '259', '~', null, '~', null, '9', '19', 19048.00000000, 20008.00000000, null, null, 266.00000000, 932.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '260', '~', null, '~', null, '9', '20', 19048.00000000, 20008.00000000, null, null, 280.00000000, 980.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '261', '~', null, '~', null, '9', '21', 19048.00000000, 20008.00000000, null, null, 294.00000000, 1029.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '262', '~', null, '~', null, '9', '22', 19048.00000000, 20008.00000000, null, null, 308.00000000, 1079.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '263', '~', null, '~', null, '9', '23', 19048.00000000, 20008.00000000, null, null, 322.00000000, 1127.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '264', '~', null, '~', null, '9', '24', 19048.00000000, 20008.00000000, null, null, 336.00000000, 1176.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '265', '~', null, '~', null, '9', '25', 19048.00000000, 20008.00000000, null, null, 350.00000000, 1226.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '266', '~', null, '~', null, '9', '26', 19048.00000000, 20008.00000000, null, null, 364.00000000, 1274.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '267', '~', null, '~', null, '9', '27', 19048.00000000, 20008.00000000, null, null, 378.00000000, 1323.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '268', '~', null, '~', null, '9', '28', 19048.00000000, 20008.00000000, null, null, 392.00000000, 1373.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '269', '~', null, '~', null, '9', '29', 19048.00000000, 20008.00000000, null, null, 406.00000000, 1421.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '270', '~', null, '~', null, '9', '30', 19048.00000000, 20008.00000000, null, null, 420.00000000, 1471.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '271', '~', null, '~', null, '10', '1', 20009.00000000, 21009.00000000, null, null, 14.00000000, 52.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '272', '~', null, '~', null, '10', '2', 20009.00000000, 21009.00000000, null, null, 30.00000000, 103.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '273', '~', null, '~', null, '10', '3', 20009.00000000, 21009.00000000, null, null, 44.00000000, 155.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '274', '~', null, '~', null, '10', '4', 20009.00000000, 21009.00000000, null, null, 59.00000000, 206.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '275', '~', null, '~', null, '10', '5', 20009.00000000, 21009.00000000, null, null, 74.00000000, 258.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '276', '~', null, '~', null, '10', '6', 20009.00000000, 21009.00000000, null, null, 88.00000000, 308.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '277', '~', null, '~', null, '10', '7', 20009.00000000, 21009.00000000, null, null, 103.00000000, 360.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '278', '~', null, '~', null, '10', '8', 20009.00000000, 21009.00000000, null, null, 117.00000000, 412.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '279', '~', null, '~', null, '10', '9', 20009.00000000, 21009.00000000, null, null, 133.00000000, 463.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '280', '~', null, '~', null, '10', '10', 20009.00000000, 21009.00000000, null, null, 147.00000000, 515.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '281', '~', null, '~', null, '10', '11', 20009.00000000, 21009.00000000, null, null, 161.00000000, 566.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '282', '~', null, '~', null, '10', '12', 20009.00000000, 21009.00000000, null, null, 177.00000000, 618.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '283', '~', null, '~', null, '10', '13', 20009.00000000, 21009.00000000, null, null, 191.00000000, 669.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '284', '~', null, '~', null, '10', '14', 20009.00000000, 21009.00000000, null, null, 206.00000000, 721.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '285', '~', null, '~', null, '10', '15', 20009.00000000, 21009.00000000, null, null, 221.00000000, 773.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '286', '~', null, '~', null, '10', '16', 20009.00000000, 21009.00000000, null, null, 235.00000000, 823.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '287', '~', null, '~', null, '10', '17', 20009.00000000, 21009.00000000, null, null, 250.00000000, 875.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '288', '~', null, '~', null, '10', '18', 20009.00000000, 21009.00000000, null, null, 265.00000000, 926.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '289', '~', null, '~', null, '10', '19', 20009.00000000, 21009.00000000, null, null, 280.00000000, 978.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '290', '~', null, '~', null, '10', '20', 20009.00000000, 21009.00000000, null, null, 294.00000000, 1029.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '291', '~', null, '~', null, '10', '21', 20009.00000000, 21009.00000000, null, null, 308.00000000, 1081.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '292', '~', null, '~', null, '10', '22', 20009.00000000, 21009.00000000, null, null, 324.00000000, 1133.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '293', '~', null, '~', null, '10', '23', 20009.00000000, 21009.00000000, null, null, 338.00000000, 1184.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '294', '~', null, '~', null, '10', '24', 20009.00000000, 21009.00000000, null, null, 353.00000000, 1236.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '295', '~', null, '~', null, '10', '25', 20009.00000000, 21009.00000000, null, null, 368.00000000, 1287.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '296', '~', null, '~', null, '10', '26', 20009.00000000, 21009.00000000, null, null, 382.00000000, 1338.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '297', '~', null, '~', null, '10', '27', 20009.00000000, 21009.00000000, null, null, 397.00000000, 1389.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '298', '~', null, '~', null, '10', '28', 20009.00000000, 21009.00000000, null, null, 412.00000000, 1441.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '299', '~', null, '~', null, '10', '29', 20009.00000000, 21009.00000000, null, null, 427.00000000, 1493.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '300', '~', null, '~', null, '10', '30', 20009.00000000, 21009.00000000, null, null, 441.00000000, 1544.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '301', '~', null, '~', null, '11', '1', 21010.00000000, 21900.00000000, null, null, 15.00000000, 54.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '302', '~', null, '~', null, '11', '2', 21010.00000000, 21900.00000000, null, null, 31.00000000, 107.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '303', '~', null, '~', null, '11', '3', 21010.00000000, 21900.00000000, null, null, 46.00000000, 161.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '304', '~', null, '~', null, '11', '4', 21010.00000000, 21900.00000000, null, null, 61.00000000, 214.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '305', '~', null, '~', null, '11', '5', 21010.00000000, 21900.00000000, null, null, 76.00000000, 269.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '306', '~', null, '~', null, '11', '6', 21010.00000000, 21900.00000000, null, null, 92.00000000, 322.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '307', '~', null, '~', null, '11', '7', 21010.00000000, 21900.00000000, null, null, 107.00000000, 376.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '308', '~', null, '~', null, '11', '8', 21010.00000000, 21900.00000000, null, null, 123.00000000, 429.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '309', '~', null, '~', null, '11', '9', 21010.00000000, 21900.00000000, null, null, 138.00000000, 483.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '310', '~', null, '~', null, '11', '10', 21010.00000000, 21900.00000000, null, null, 154.00000000, 536.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '311', '~', null, '~', null, '11', '11', 21010.00000000, 21900.00000000, null, null, 169.00000000, 590.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '312', '~', null, '~', null, '11', '12', 21010.00000000, 21900.00000000, null, null, 184.00000000, 644.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '313', '~', null, '~', null, '11', '13', 21010.00000000, 21900.00000000, null, null, 199.00000000, 697.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '314', '~', null, '~', null, '11', '14', 21010.00000000, 21900.00000000, null, null, 214.00000000, 752.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '315', '~', null, '~', null, '11', '15', 21010.00000000, 21900.00000000, null, null, 230.00000000, 805.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '316', '~', null, '~', null, '11', '16', 21010.00000000, 21900.00000000, null, null, 245.00000000, 859.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '317', '~', null, '~', null, '11', '17', 21010.00000000, 21900.00000000, null, null, 261.00000000, 912.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '318', '~', null, '~', null, '11', '18', 21010.00000000, 21900.00000000, null, null, 276.00000000, 966.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '319', '~', null, '~', null, '11', '19', 21010.00000000, 21900.00000000, null, null, 292.00000000, 1019.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '320', '~', null, '~', null, '11', '20', 21010.00000000, 21900.00000000, null, null, 306.00000000, 1073.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '321', '~', null, '~', null, '11', '21', 21010.00000000, 21900.00000000, null, null, 322.00000000, 1126.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '322', '~', null, '~', null, '11', '22', 21010.00000000, 21900.00000000, null, null, 337.00000000, 1180.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '323', '~', null, '~', null, '11', '23', 21010.00000000, 21900.00000000, null, null, 353.00000000, 1235.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '324', '~', null, '~', null, '11', '24', 21010.00000000, 21900.00000000, null, null, 368.00000000, 1288.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '325', '~', null, '~', null, '11', '25', 21010.00000000, 21900.00000000, null, null, 384.00000000, 1342.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '326', '~', null, '~', null, '11', '26', 21010.00000000, 21900.00000000, null, null, 399.00000000, 1395.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '327', '~', null, '~', null, '11', '27', 21010.00000000, 21900.00000000, null, null, 413.00000000, 1449.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '328', '~', null, '~', null, '11', '28', 21010.00000000, 21900.00000000, null, null, 429.00000000, 1502.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '329', '~', null, '~', null, '11', '29', 21010.00000000, 21900.00000000, null, null, 444.00000000, 1556.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '330', '~', null, '~', null, '11', '30', 21010.00000000, 21900.00000000, null, null, 460.00000000, 1609.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '331', '~', null, '~', null, '12', '1', 21901.00000000, 22800.00000000, null, null, 16.00000000, 56.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '332', '~', null, '~', null, '12', '2', 21901.00000000, 22800.00000000, null, null, 32.00000000, 112.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '333', '~', null, '~', null, '12', '3', 21901.00000000, 22800.00000000, null, null, 48.00000000, 168.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '334', '~', null, '~', null, '12', '4', 21901.00000000, 22800.00000000, null, null, 64.00000000, 223.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '335', '~', null, '~', null, '12', '5', 21901.00000000, 22800.00000000, null, null, 80.00000000, 280.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '336', '~', null, '~', null, '12', '6', 21901.00000000, 22800.00000000, null, null, 96.00000000, 335.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '337', '~', null, '~', null, '12', '7', 21901.00000000, 22800.00000000, null, null, 112.00000000, 391.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '338', '~', null, '~', null, '12', '8', 21901.00000000, 22800.00000000, null, null, 128.00000000, 447.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '339', '~', null, '~', null, '12', '9', 21901.00000000, 22800.00000000, null, null, 144.00000000, 503.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '340', '~', null, '~', null, '12', '10', 21901.00000000, 22800.00000000, null, null, 159.00000000, 558.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '341', '~', null, '~', null, '12', '11', 21901.00000000, 22800.00000000, null, null, 176.00000000, 615.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '342', '~', null, '~', null, '12', '12', 21901.00000000, 22800.00000000, null, null, 191.00000000, 670.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '343', '~', null, '~', null, '12', '13', 21901.00000000, 22800.00000000, null, null, 208.00000000, 726.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '344', '~', null, '~', null, '12', '14', 21901.00000000, 22800.00000000, null, null, 223.00000000, 782.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '345', '~', null, '~', null, '12', '15', 21901.00000000, 22800.00000000, null, null, 240.00000000, 838.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '346', '~', null, '~', null, '12', '16', 21901.00000000, 22800.00000000, null, null, 255.00000000, 894.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '347', '~', null, '~', null, '12', '17', 21901.00000000, 22800.00000000, null, null, 271.00000000, 949.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '348', '~', null, '~', null, '12', '18', 21901.00000000, 22800.00000000, null, null, 287.00000000, 1006.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '349', '~', null, '~', null, '12', '19', 21901.00000000, 22800.00000000, null, null, 303.00000000, 1061.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '350', '~', null, '~', null, '12', '20', 21901.00000000, 22800.00000000, null, null, 319.00000000, 1117.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '351', '~', null, '~', null, '12', '21', 21901.00000000, 22800.00000000, null, null, 335.00000000, 1173.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '352', '~', null, '~', null, '12', '22', 21901.00000000, 22800.00000000, null, null, 351.00000000, 1229.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '353', '~', null, '~', null, '12', '23', 21901.00000000, 22800.00000000, null, null, 367.00000000, 1284.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '354', '~', null, '~', null, '12', '24', 21901.00000000, 22800.00000000, null, null, 383.00000000, 1341.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '355', '~', null, '~', null, '12', '25', 21901.00000000, 22800.00000000, null, null, 399.00000000, 1397.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '356', '~', null, '~', null, '12', '26', 21901.00000000, 22800.00000000, null, null, 415.00000000, 1452.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '357', '~', null, '~', null, '12', '27', 21901.00000000, 22800.00000000, null, null, 431.00000000, 1509.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '358', '~', null, '~', null, '12', '28', 21901.00000000, 22800.00000000, null, null, 447.00000000, 1564.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '359', '~', null, '~', null, '12', '29', 21901.00000000, 22800.00000000, null, null, 463.00000000, 1620.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '360', '~', null, '~', null, '12', '30', 21901.00000000, 22800.00000000, null, null, 479.00000000, 1676.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '361', '~', null, '~', null, '13', '1', 22801.00000000, 24000.00000000, null, null, 17.00000000, 59.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '362', '~', null, '~', null, '13', '2', 22801.00000000, 24000.00000000, null, null, 33.00000000, 117.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '363', '~', null, '~', null, '13', '3', 22801.00000000, 24000.00000000, null, null, 51.00000000, 177.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '364', '~', null, '~', null, '13', '4', 22801.00000000, 24000.00000000, null, null, 67.00000000, 235.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '365', '~', null, '~', null, '13', '5', 22801.00000000, 24000.00000000, null, null, 84.00000000, 294.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '366', '~', null, '~', null, '13', '6', 22801.00000000, 24000.00000000, null, null, 101.00000000, 353.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '367', '~', null, '~', null, '13', '7', 22801.00000000, 24000.00000000, null, null, 117.00000000, 411.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '368', '~', null, '~', null, '13', '8', 22801.00000000, 24000.00000000, null, null, 135.00000000, 471.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '369', '~', null, '~', null, '13', '9', 22801.00000000, 24000.00000000, null, null, 151.00000000, 529.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '370', '~', null, '~', null, '13', '10', 22801.00000000, 24000.00000000, null, null, 168.00000000, 588.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '371', '~', null, '~', null, '13', '11', 22801.00000000, 24000.00000000, null, null, 185.00000000, 647.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '372', '~', null, '~', null, '13', '12', 22801.00000000, 24000.00000000, null, null, 201.00000000, 705.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '373', '~', null, '~', null, '13', '13', 22801.00000000, 24000.00000000, null, null, 219.00000000, 765.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '374', '~', null, '~', null, '13', '14', 22801.00000000, 24000.00000000, null, null, 235.00000000, 823.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '375', '~', null, '~', null, '13', '15', 22801.00000000, 24000.00000000, null, null, 252.00000000, 882.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '376', '~', null, '~', null, '13', '16', 22801.00000000, 24000.00000000, null, null, 269.00000000, 941.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '377', '~', null, '~', null, '13', '17', 22801.00000000, 24000.00000000, null, null, 285.00000000, 999.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '378', '~', null, '~', null, '13', '18', 22801.00000000, 24000.00000000, null, null, 303.00000000, 1059.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '379', '~', null, '~', null, '13', '19', 22801.00000000, 24000.00000000, null, null, 319.00000000, 1117.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '380', '~', null, '~', null, '13', '20', 22801.00000000, 24000.00000000, null, null, 336.00000000, 1176.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '381', '~', null, '~', null, '13', '21', 22801.00000000, 24000.00000000, null, null, 353.00000000, 1235.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '382', '~', null, '~', null, '13', '22', 22801.00000000, 24000.00000000, null, null, 369.00000000, 1293.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '383', '~', null, '~', null, '13', '23', 22801.00000000, 24000.00000000, null, null, 387.00000000, 1353.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '384', '~', null, '~', null, '13', '24', 22801.00000000, 24000.00000000, null, null, 403.00000000, 1411.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '385', '~', null, '~', null, '13', '25', 22801.00000000, 24000.00000000, null, null, 420.00000000, 1470.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '386', '~', null, '~', null, '13', '26', 22801.00000000, 24000.00000000, null, null, 437.00000000, 1529.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '387', '~', null, '~', null, '13', '27', 22801.00000000, 24000.00000000, null, null, 453.00000000, 1587.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '388', '~', null, '~', null, '13', '28', 22801.00000000, 24000.00000000, null, null, 471.00000000, 1647.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '389', '~', null, '~', null, '13', '29', 22801.00000000, 24000.00000000, null, null, 487.00000000, 1705.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '390', '~', null, '~', null, '13', '30', 22801.00000000, 24000.00000000, null, null, 504.00000000, 1764.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '391', '~', null, '~', null, '14', '1', 24001.00000000, 25200.00000000, null, null, 18.00000000, 62.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '392', '~', null, '~', null, '14', '2', 24001.00000000, 25200.00000000, null, null, 35.00000000, 124.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '393', '~', null, '~', null, '14', '3', 24001.00000000, 25200.00000000, null, null, 53.00000000, 186.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '394', '~', null, '~', null, '14', '4', 24001.00000000, 25200.00000000, null, null, 71.00000000, 247.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '395', '~', null, '~', null, '14', '5', 24001.00000000, 25200.00000000, null, null, 88.00000000, 308.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '396', '~', null, '~', null, '14', '6', 24001.00000000, 25200.00000000, null, null, 106.00000000, 370.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '397', '~', null, '~', null, '14', '7', 24001.00000000, 25200.00000000, null, null, 124.00000000, 432.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '398', '~', null, '~', null, '14', '8', 24001.00000000, 25200.00000000, null, null, 141.00000000, 494.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '399', '~', null, '~', null, '14', '9', 24001.00000000, 25200.00000000, null, null, 159.00000000, 556.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '400', '~', null, '~', null, '14', '10', 24001.00000000, 25200.00000000, null, null, 177.00000000, 618.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '401', '~', null, '~', null, '14', '11', 24001.00000000, 25200.00000000, null, null, 194.00000000, 679.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '402', '~', null, '~', null, '14', '12', 24001.00000000, 25200.00000000, null, null, 212.00000000, 741.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '403', '~', null, '~', null, '14', '13', 24001.00000000, 25200.00000000, null, null, 229.00000000, 802.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '404', '~', null, '~', null, '14', '14', 24001.00000000, 25200.00000000, null, null, 247.00000000, 864.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '405', '~', null, '~', null, '14', '15', 24001.00000000, 25200.00000000, null, null, 264.00000000, 926.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '406', '~', null, '~', null, '14', '16', 24001.00000000, 25200.00000000, null, null, 282.00000000, 988.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '407', '~', null, '~', null, '14', '17', 24001.00000000, 25200.00000000, null, null, 300.00000000, 1050.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '408', '~', null, '~', null, '14', '18', 24001.00000000, 25200.00000000, null, null, 317.00000000, 1111.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '409', '~', null, '~', null, '14', '19', 24001.00000000, 25200.00000000, null, null, 335.00000000, 1173.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '410', '~', null, '~', null, '14', '20', 24001.00000000, 25200.00000000, null, null, 353.00000000, 1235.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '411', '~', null, '~', null, '14', '21', 24001.00000000, 25200.00000000, null, null, 370.00000000, 1296.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '412', '~', null, '~', null, '14', '22', 24001.00000000, 25200.00000000, null, null, 388.00000000, 1358.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '413', '~', null, '~', null, '14', '23', 24001.00000000, 25200.00000000, null, null, 406.00000000, 1420.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '414', '~', null, '~', null, '14', '24', 24001.00000000, 25200.00000000, null, null, 423.00000000, 1482.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '415', '~', null, '~', null, '14', '25', 24001.00000000, 25200.00000000, null, null, 441.00000000, 1544.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '416', '~', null, '~', null, '14', '26', 24001.00000000, 25200.00000000, null, null, 459.00000000, 1605.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '417', '~', null, '~', null, '14', '27', 24001.00000000, 25200.00000000, null, null, 476.00000000, 1667.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '418', '~', null, '~', null, '14', '28', 24001.00000000, 25200.00000000, null, null, 494.00000000, 1729.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '419', '~', null, '~', null, '14', '29', 24001.00000000, 25200.00000000, null, null, 512.00000000, 1791.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '420', '~', null, '~', null, '14', '30', 24001.00000000, 25200.00000000, null, null, 529.00000000, 1852.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '421', '~', null, '~', null, '15', '1', 25201.00000000, 26400.00000000, null, null, 19.00000000, 65.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '422', '~', null, '~', null, '15', '2', 25201.00000000, 26400.00000000, null, null, 37.00000000, 129.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '423', '~', null, '~', null, '15', '3', 25201.00000000, 26400.00000000, null, null, 55.00000000, 194.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '424', '~', null, '~', null, '15', '4', 25201.00000000, 26400.00000000, null, null, 74.00000000, 259.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '425', '~', null, '~', null, '15', '5', 25201.00000000, 26400.00000000, null, null, 93.00000000, 324.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '426', '~', null, '~', null, '15', '6', 25201.00000000, 26400.00000000, null, null, 111.00000000, 388.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '427', '~', null, '~', null, '15', '7', 25201.00000000, 26400.00000000, null, null, 129.00000000, 453.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '428', '~', null, '~', null, '15', '8', 25201.00000000, 26400.00000000, null, null, 148.00000000, 517.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '429', '~', null, '~', null, '15', '9', 25201.00000000, 26400.00000000, null, null, 166.00000000, 582.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '430', '~', null, '~', null, '15', '10', 25201.00000000, 26400.00000000, null, null, 185.00000000, 647.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '431', '~', null, '~', null, '15', '11', 25201.00000000, 26400.00000000, null, null, 203.00000000, 712.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '432', '~', null, '~', null, '15', '12', 25201.00000000, 26400.00000000, null, null, 222.00000000, 776.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '433', '~', null, '~', null, '15', '13', 25201.00000000, 26400.00000000, null, null, 240.00000000, 841.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '434', '~', null, '~', null, '15', '14', 25201.00000000, 26400.00000000, null, null, 259.00000000, 905.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '435', '~', null, '~', null, '15', '15', 25201.00000000, 26400.00000000, null, null, 277.00000000, 970.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '436', '~', null, '~', null, '15', '16', 25201.00000000, 26400.00000000, null, null, 296.00000000, 1035.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '437', '~', null, '~', null, '15', '17', 25201.00000000, 26400.00000000, null, null, 314.00000000, 1100.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '438', '~', null, '~', null, '15', '18', 25201.00000000, 26400.00000000, null, null, 333.00000000, 1164.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '439', '~', null, '~', null, '15', '19', 25201.00000000, 26400.00000000, null, null, 351.00000000, 1229.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '440', '~', null, '~', null, '15', '20', 25201.00000000, 26400.00000000, null, null, 369.00000000, 1293.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '441', '~', null, '~', null, '15', '21', 25201.00000000, 26400.00000000, null, null, 388.00000000, 1358.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '442', '~', null, '~', null, '15', '22', 25201.00000000, 26400.00000000, null, null, 407.00000000, 1423.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '443', '~', null, '~', null, '15', '23', 25201.00000000, 26400.00000000, null, null, 425.00000000, 1488.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '444', '~', null, '~', null, '15', '24', 25201.00000000, 26400.00000000, null, null, 443.00000000, 1552.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '445', '~', null, '~', null, '15', '25', 25201.00000000, 26400.00000000, null, null, 462.00000000, 1617.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '446', '~', null, '~', null, '15', '26', 25201.00000000, 26400.00000000, null, null, 481.00000000, 1682.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '447', '~', null, '~', null, '15', '27', 25201.00000000, 26400.00000000, null, null, 499.00000000, 1746.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '448', '~', null, '~', null, '15', '28', 25201.00000000, 26400.00000000, null, null, 517.00000000, 1811.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '449', '~', null, '~', null, '15', '29', 25201.00000000, 26400.00000000, null, null, 536.00000000, 1876.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '450', '~', null, '~', null, '15', '30', 25201.00000000, 26400.00000000, null, null, 555.00000000, 1941.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '451', '~', null, '~', null, '16', '1', 26401.00000000, 27600.00000000, null, null, 19.00000000, 67.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '452', '~', null, '~', null, '16', '2', 26401.00000000, 27600.00000000, null, null, 39.00000000, 135.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '453', '~', null, '~', null, '16', '3', 26401.00000000, 27600.00000000, null, null, 58.00000000, 203.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '454', '~', null, '~', null, '16', '4', 26401.00000000, 27600.00000000, null, null, 77.00000000, 271.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '455', '~', null, '~', null, '16', '5', 26401.00000000, 27600.00000000, null, null, 96.00000000, 338.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '456', '~', null, '~', null, '16', '6', 26401.00000000, 27600.00000000, null, null, 116.00000000, 406.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '457', '~', null, '~', null, '16', '7', 26401.00000000, 27600.00000000, null, null, 135.00000000, 473.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '458', '~', null, '~', null, '16', '8', 26401.00000000, 27600.00000000, null, null, 155.00000000, 541.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '459', '~', null, '~', null, '16', '9', 26401.00000000, 27600.00000000, null, null, 174.00000000, 609.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '460', '~', null, '~', null, '16', '10', 26401.00000000, 27600.00000000, null, null, 193.00000000, 676.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '461', '~', null, '~', null, '16', '11', 26401.00000000, 27600.00000000, null, null, 212.00000000, 744.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '462', '~', null, '~', null, '16', '12', 26401.00000000, 27600.00000000, null, null, 232.00000000, 811.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '463', '~', null, '~', null, '16', '13', 26401.00000000, 27600.00000000, null, null, 251.00000000, 879.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '464', '~', null, '~', null, '16', '14', 26401.00000000, 27600.00000000, null, null, 271.00000000, 947.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '465', '~', null, '~', null, '16', '15', 26401.00000000, 27600.00000000, null, null, 290.00000000, 1015.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '466', '~', null, '~', null, '16', '16', 26401.00000000, 27600.00000000, null, null, 309.00000000, 1082.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '467', '~', null, '~', null, '16', '17', 26401.00000000, 27600.00000000, null, null, 328.00000000, 1149.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '468', '~', null, '~', null, '16', '18', 26401.00000000, 27600.00000000, null, null, 348.00000000, 1217.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '469', '~', null, '~', null, '16', '19', 26401.00000000, 27600.00000000, null, null, 367.00000000, 1284.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '470', '~', null, '~', null, '16', '20', 26401.00000000, 27600.00000000, null, null, 387.00000000, 1353.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '471', '~', null, '~', null, '16', '21', 26401.00000000, 27600.00000000, null, null, 406.00000000, 1420.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '472', '~', null, '~', null, '16', '22', 26401.00000000, 27600.00000000, null, null, 425.00000000, 1488.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '473', '~', null, '~', null, '16', '23', 26401.00000000, 27600.00000000, null, null, 444.00000000, 1555.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '474', '~', null, '~', null, '16', '24', 26401.00000000, 27600.00000000, null, null, 464.00000000, 1623.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '475', '~', null, '~', null, '16', '25', 26401.00000000, 27600.00000000, null, null, 483.00000000, 1691.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '476', '~', null, '~', null, '16', '26', 26401.00000000, 27600.00000000, null, null, 502.00000000, 1758.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '477', '~', null, '~', null, '16', '27', 26401.00000000, 27600.00000000, null, null, 522.00000000, 1826.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '478', '~', null, '~', null, '16', '28', 26401.00000000, 27600.00000000, null, null, 541.00000000, 1893.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '479', '~', null, '~', null, '16', '29', 26401.00000000, 27600.00000000, null, null, 560.00000000, 1961.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '480', '~', null, '~', null, '16', '30', 26401.00000000, 27600.00000000, null, null, 579.00000000, 2028.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '481', '~', null, '~', null, '17', '1', 27601.00000000, 28800.00000000, null, null, 20.00000000, 71.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '482', '~', null, '~', null, '17', '2', 27601.00000000, 28800.00000000, null, null, 40.00000000, 141.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '483', '~', null, '~', null, '17', '3', 27601.00000000, 28800.00000000, null, null, 61.00000000, 212.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '484', '~', null, '~', null, '17', '4', 27601.00000000, 28800.00000000, null, null, 81.00000000, 282.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '485', '~', null, '~', null, '17', '5', 27601.00000000, 28800.00000000, null, null, 101.00000000, 353.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '486', '~', null, '~', null, '17', '6', 27601.00000000, 28800.00000000, null, null, 121.00000000, 423.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '487', '~', null, '~', null, '17', '7', 27601.00000000, 28800.00000000, null, null, 141.00000000, 494.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '488', '~', null, '~', null, '17', '8', 27601.00000000, 28800.00000000, null, null, 161.00000000, 565.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '489', '~', null, '~', null, '17', '9', 27601.00000000, 28800.00000000, null, null, 181.00000000, 635.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '490', '~', null, '~', null, '17', '10', 27601.00000000, 28800.00000000, null, null, 201.00000000, 705.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '491', '~', null, '~', null, '17', '11', 27601.00000000, 28800.00000000, null, null, 222.00000000, 776.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '492', '~', null, '~', null, '17', '12', 27601.00000000, 28800.00000000, null, null, 242.00000000, 847.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '493', '~', null, '~', null, '17', '13', 27601.00000000, 28800.00000000, null, null, 262.00000000, 917.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '494', '~', null, '~', null, '17', '14', 27601.00000000, 28800.00000000, null, null, 282.00000000, 988.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '495', '~', null, '~', null, '17', '15', 27601.00000000, 28800.00000000, null, null, 303.00000000, 1059.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '496', '~', null, '~', null, '17', '16', 27601.00000000, 28800.00000000, null, null, 323.00000000, 1129.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '497', '~', null, '~', null, '17', '17', 27601.00000000, 28800.00000000, null, null, 343.00000000, 1199.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '498', '~', null, '~', null, '17', '18', 27601.00000000, 28800.00000000, null, null, 363.00000000, 1270.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '499', '~', null, '~', null, '17', '19', 27601.00000000, 28800.00000000, null, null, 383.00000000, 1341.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '500', '~', null, '~', null, '17', '20', 27601.00000000, 28800.00000000, null, null, 403.00000000, 1411.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '501', '~', null, '~', null, '17', '21', 27601.00000000, 28800.00000000, null, null, 423.00000000, 1482.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '502', '~', null, '~', null, '17', '22', 27601.00000000, 28800.00000000, null, null, 443.00000000, 1552.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '503', '~', null, '~', null, '17', '23', 27601.00000000, 28800.00000000, null, null, 464.00000000, 1623.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '504', '~', null, '~', null, '17', '24', 27601.00000000, 28800.00000000, null, null, 484.00000000, 1693.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '505', '~', null, '~', null, '17', '25', 27601.00000000, 28800.00000000, null, null, 504.00000000, 1764.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '506', '~', null, '~', null, '17', '26', 27601.00000000, 28800.00000000, null, null, 524.00000000, 1835.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '507', '~', null, '~', null, '17', '27', 27601.00000000, 28800.00000000, null, null, 544.00000000, 1905.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '508', '~', null, '~', null, '17', '28', 27601.00000000, 28800.00000000, null, null, 565.00000000, 1976.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '509', '~', null, '~', null, '17', '29', 27601.00000000, 28800.00000000, null, null, 585.00000000, 2046.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '510', '~', null, '~', null, '17', '30', 27601.00000000, 28800.00000000, null, null, 605.00000000, 2117.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '511', '~', null, '~', null, '18', '1', 28801.00000000, 30300.00000000, null, null, 21.00000000, 74.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '512', '~', null, '~', null, '18', '2', 28801.00000000, 30300.00000000, null, null, 42.00000000, 148.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '513', '~', null, '~', null, '18', '3', 28801.00000000, 30300.00000000, null, null, 64.00000000, 222.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '514', '~', null, '~', null, '18', '4', 28801.00000000, 30300.00000000, null, null, 85.00000000, 297.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '515', '~', null, '~', null, '18', '5', 28801.00000000, 30300.00000000, null, null, 106.00000000, 371.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '516', '~', null, '~', null, '18', '6', 28801.00000000, 30300.00000000, null, null, 127.00000000, 445.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '517', '~', null, '~', null, '18', '7', 28801.00000000, 30300.00000000, null, null, 148.00000000, 519.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '518', '~', null, '~', null, '18', '8', 28801.00000000, 30300.00000000, null, null, 170.00000000, 594.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '519', '~', null, '~', null, '18', '9', 28801.00000000, 30300.00000000, null, null, 191.00000000, 668.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '520', '~', null, '~', null, '18', '10', 28801.00000000, 30300.00000000, null, null, 212.00000000, 743.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '521', '~', null, '~', null, '18', '11', 28801.00000000, 30300.00000000, null, null, 233.00000000, 817.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '522', '~', null, '~', null, '18', '12', 28801.00000000, 30300.00000000, null, null, 254.00000000, 891.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '523', '~', null, '~', null, '18', '13', 28801.00000000, 30300.00000000, null, null, 275.00000000, 965.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '524', '~', null, '~', null, '18', '14', 28801.00000000, 30300.00000000, null, null, 297.00000000, 1039.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '525', '~', null, '~', null, '18', '15', 28801.00000000, 30300.00000000, null, null, 318.00000000, 1113.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '526', '~', null, '~', null, '18', '16', 28801.00000000, 30300.00000000, null, null, 339.00000000, 1188.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '527', '~', null, '~', null, '18', '17', 28801.00000000, 30300.00000000, null, null, 360.00000000, 1262.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '528', '~', null, '~', null, '18', '18', 28801.00000000, 30300.00000000, null, null, 381.00000000, 1336.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '529', '~', null, '~', null, '18', '19', 28801.00000000, 30300.00000000, null, null, 403.00000000, 1410.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '530', '~', null, '~', null, '18', '20', 28801.00000000, 30300.00000000, null, null, 424.00000000, 1484.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '531', '~', null, '~', null, '18', '21', 28801.00000000, 30300.00000000, null, null, 445.00000000, 1558.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '532', '~', null, '~', null, '18', '22', 28801.00000000, 30300.00000000, null, null, 466.00000000, 1634.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '533', '~', null, '~', null, '18', '23', 28801.00000000, 30300.00000000, null, null, 487.00000000, 1708.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '534', '~', null, '~', null, '18', '24', 28801.00000000, 30300.00000000, null, null, 509.00000000, 1782.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '535', '~', null, '~', null, '18', '25', 28801.00000000, 30300.00000000, null, null, 531.00000000, 1856.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '536', '~', null, '~', null, '18', '26', 28801.00000000, 30300.00000000, null, null, 552.00000000, 1930.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '537', '~', null, '~', null, '18', '27', 28801.00000000, 30300.00000000, null, null, 573.00000000, 2004.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '538', '~', null, '~', null, '18', '28', 28801.00000000, 30300.00000000, null, null, 594.00000000, 2079.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '539', '~', null, '~', null, '18', '29', 28801.00000000, 30300.00000000, null, null, 616.00000000, 2153.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '540', '~', null, '~', null, '18', '30', 28801.00000000, 30300.00000000, null, null, 637.00000000, 2227.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '541', '~', null, '~', null, '19', '1', 30301.00000000, 31800.00000000, null, null, 22.00000000, 77.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '542', '~', null, '~', null, '19', '2', 30301.00000000, 31800.00000000, null, null, 44.00000000, 156.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '543', '~', null, '~', null, '19', '3', 30301.00000000, 31800.00000000, null, null, 66.00000000, 233.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '544', '~', null, '~', null, '19', '4', 30301.00000000, 31800.00000000, null, null, 89.00000000, 312.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '545', '~', null, '~', null, '19', '5', 30301.00000000, 31800.00000000, null, null, 112.00000000, 389.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '546', '~', null, '~', null, '19', '6', 30301.00000000, 31800.00000000, null, null, 134.00000000, 468.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '547', '~', null, '~', null, '19', '7', 30301.00000000, 31800.00000000, null, null, 156.00000000, 545.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '548', '~', null, '~', null, '19', '8', 30301.00000000, 31800.00000000, null, null, 178.00000000, 623.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '549', '~', null, '~', null, '19', '9', 30301.00000000, 31800.00000000, null, null, 200.00000000, 701.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '550', '~', null, '~', null, '19', '10', 30301.00000000, 31800.00000000, null, null, 222.00000000, 779.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '551', '~', null, '~', null, '19', '11', 30301.00000000, 31800.00000000, null, null, 245.00000000, 857.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '552', '~', null, '~', null, '19', '12', 30301.00000000, 31800.00000000, null, null, 267.00000000, 935.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '553', '~', null, '~', null, '19', '13', 30301.00000000, 31800.00000000, null, null, 290.00000000, 1012.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '554', '~', null, '~', null, '19', '14', 30301.00000000, 31800.00000000, null, null, 312.00000000, 1091.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '555', '~', null, '~', null, '19', '15', 30301.00000000, 31800.00000000, null, null, 334.00000000, 1168.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '556', '~', null, '~', null, '19', '16', 30301.00000000, 31800.00000000, null, null, 356.00000000, 1247.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '557', '~', null, '~', null, '19', '17', 30301.00000000, 31800.00000000, null, null, 378.00000000, 1324.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '558', '~', null, '~', null, '19', '18', 30301.00000000, 31800.00000000, null, null, 401.00000000, 1403.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '559', '~', null, '~', null, '19', '19', 30301.00000000, 31800.00000000, null, null, 423.00000000, 1480.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '560', '~', null, '~', null, '19', '20', 30301.00000000, 31800.00000000, null, null, 445.00000000, 1558.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '561', '~', null, '~', null, '19', '21', 30301.00000000, 31800.00000000, null, null, 468.00000000, 1636.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '562', '~', null, '~', null, '19', '22', 30301.00000000, 31800.00000000, null, null, 490.00000000, 1714.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '563', '~', null, '~', null, '19', '23', 30301.00000000, 31800.00000000, null, null, 512.00000000, 1792.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '564', '~', null, '~', null, '19', '24', 30301.00000000, 31800.00000000, null, null, 534.00000000, 1870.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '565', '~', null, '~', null, '19', '25', 30301.00000000, 31800.00000000, null, null, 557.00000000, 1948.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '566', '~', null, '~', null, '19', '26', 30301.00000000, 31800.00000000, null, null, 579.00000000, 2026.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '567', '~', null, '~', null, '19', '27', 30301.00000000, 31800.00000000, null, null, 601.00000000, 2103.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '568', '~', null, '~', null, '19', '28', 30301.00000000, 31800.00000000, null, null, 623.00000000, 2182.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '569', '~', null, '~', null, '19', '29', 30301.00000000, 31800.00000000, null, null, 645.00000000, 2259.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '570', '~', null, '~', null, '19', '30', 30301.00000000, 31800.00000000, null, null, 668.00000000, 2338.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '571', '~', null, '~', null, '20', '1', 31801.00000000, 33300.00000000, null, null, 23.00000000, 82.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '572', '~', null, '~', null, '20', '2', 31801.00000000, 33300.00000000, null, null, 46.00000000, 164.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '573', '~', null, '~', null, '20', '3', 31801.00000000, 33300.00000000, null, null, 70.00000000, 244.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '574', '~', null, '~', null, '20', '4', 31801.00000000, 33300.00000000, null, null, 93.00000000, 326.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '575', '~', null, '~', null, '20', '5', 31801.00000000, 33300.00000000, null, null, 116.00000000, 408.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '576', '~', null, '~', null, '20', '6', 31801.00000000, 33300.00000000, null, null, 140.00000000, 490.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '577', '~', null, '~', null, '20', '7', 31801.00000000, 33300.00000000, null, null, 164.00000000, 571.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '578', '~', null, '~', null, '20', '8', 31801.00000000, 33300.00000000, null, null, 187.00000000, 653.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '579', '~', null, '~', null, '20', '9', 31801.00000000, 33300.00000000, null, null, 210.00000000, 734.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '580', '~', null, '~', null, '20', '10', 31801.00000000, 33300.00000000, null, null, 233.00000000, 816.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '581', '~', null, '~', null, '20', '11', 31801.00000000, 33300.00000000, null, null, 256.00000000, 897.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '582', '~', null, '~', null, '20', '12', 31801.00000000, 33300.00000000, null, null, 280.00000000, 979.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '583', '~', null, '~', null, '20', '13', 31801.00000000, 33300.00000000, null, null, 303.00000000, 1061.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '584', '~', null, '~', null, '20', '14', 31801.00000000, 33300.00000000, null, null, 326.00000000, 1142.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '585', '~', null, '~', null, '20', '15', 31801.00000000, 33300.00000000, null, null, 349.00000000, 1224.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '586', '~', null, '~', null, '20', '16', 31801.00000000, 33300.00000000, null, null, 373.00000000, 1305.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '587', '~', null, '~', null, '20', '17', 31801.00000000, 33300.00000000, null, null, 397.00000000, 1387.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '588', '~', null, '~', null, '20', '18', 31801.00000000, 33300.00000000, null, null, 420.00000000, 1469.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '589', '~', null, '~', null, '20', '19', 31801.00000000, 33300.00000000, null, null, 443.00000000, 1550.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '590', '~', null, '~', null, '20', '20', 31801.00000000, 33300.00000000, null, null, 466.00000000, 1631.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '591', '~', null, '~', null, '20', '21', 31801.00000000, 33300.00000000, null, null, 490.00000000, 1713.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '592', '~', null, '~', null, '20', '22', 31801.00000000, 33300.00000000, null, null, 513.00000000, 1795.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '593', '~', null, '~', null, '20', '23', 31801.00000000, 33300.00000000, null, null, 536.00000000, 1877.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '594', '~', null, '~', null, '20', '24', 31801.00000000, 33300.00000000, null, null, 559.00000000, 1958.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '595', '~', null, '~', null, '20', '25', 31801.00000000, 33300.00000000, null, null, 583.00000000, 2039.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '596', '~', null, '~', null, '20', '26', 31801.00000000, 33300.00000000, null, null, 606.00000000, 2121.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '597', '~', null, '~', null, '20', '27', 31801.00000000, 33300.00000000, null, null, 629.00000000, 2203.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '598', '~', null, '~', null, '20', '28', 31801.00000000, 33300.00000000, null, null, 653.00000000, 2285.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '599', '~', null, '~', null, '20', '29', 31801.00000000, 33300.00000000, null, null, 676.00000000, 2366.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '600', '~', null, '~', null, '20', '30', 31801.00000000, 33300.00000000, null, null, 700.00000000, 2447.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '601', '~', null, '~', null, '21', '1', 33301.00000000, 34800.00000000, null, null, 24.00000000, 85.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '602', '~', null, '~', null, '21', '2', 33301.00000000, 34800.00000000, null, null, 49.00000000, 170.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '603', '~', null, '~', null, '21', '3', 33301.00000000, 34800.00000000, null, null, 73.00000000, 255.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '604', '~', null, '~', null, '21', '4', 33301.00000000, 34800.00000000, null, null, 97.00000000, 341.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '605', '~', null, '~', null, '21', '5', 33301.00000000, 34800.00000000, null, null, 122.00000000, 427.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '606', '~', null, '~', null, '21', '6', 33301.00000000, 34800.00000000, null, null, 146.00000000, 512.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '607', '~', null, '~', null, '21', '7', 33301.00000000, 34800.00000000, null, null, 170.00000000, 597.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '608', '~', null, '~', null, '21', '8', 33301.00000000, 34800.00000000, null, null, 195.00000000, 682.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '609', '~', null, '~', null, '21', '9', 33301.00000000, 34800.00000000, null, null, 219.00000000, 767.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '610', '~', null, '~', null, '21', '10', 33301.00000000, 34800.00000000, null, null, 243.00000000, 852.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '611', '~', null, '~', null, '21', '11', 33301.00000000, 34800.00000000, null, null, 268.00000000, 938.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '612', '~', null, '~', null, '21', '12', 33301.00000000, 34800.00000000, null, null, 292.00000000, 1023.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '613', '~', null, '~', null, '21', '13', 33301.00000000, 34800.00000000, null, null, 317.00000000, 1109.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '614', '~', null, '~', null, '21', '14', 33301.00000000, 34800.00000000, null, null, 341.00000000, 1194.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '615', '~', null, '~', null, '21', '15', 33301.00000000, 34800.00000000, null, null, 366.00000000, 1279.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '616', '~', null, '~', null, '21', '16', 33301.00000000, 34800.00000000, null, null, 390.00000000, 1364.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '617', '~', null, '~', null, '21', '17', 33301.00000000, 34800.00000000, null, null, 414.00000000, 1449.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '618', '~', null, '~', null, '21', '18', 33301.00000000, 34800.00000000, null, null, 439.00000000, 1535.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '619', '~', null, '~', null, '21', '19', 33301.00000000, 34800.00000000, null, null, 463.00000000, 1620.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '620', '~', null, '~', null, '21', '20', 33301.00000000, 34800.00000000, null, null, 487.00000000, 1705.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '621', '~', null, '~', null, '21', '21', 33301.00000000, 34800.00000000, null, null, 512.00000000, 1791.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '622', '~', null, '~', null, '21', '22', 33301.00000000, 34800.00000000, null, null, 536.00000000, 1876.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '623', '~', null, '~', null, '21', '23', 33301.00000000, 34800.00000000, null, null, 560.00000000, 1961.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '624', '~', null, '~', null, '21', '24', 33301.00000000, 34800.00000000, null, null, 585.00000000, 2046.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '625', '~', null, '~', null, '21', '25', 33301.00000000, 34800.00000000, null, null, 609.00000000, 2132.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '626', '~', null, '~', null, '21', '26', 33301.00000000, 34800.00000000, null, null, 633.00000000, 2217.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '627', '~', null, '~', null, '21', '27', 33301.00000000, 34800.00000000, null, null, 658.00000000, 2302.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '628', '~', null, '~', null, '21', '28', 33301.00000000, 34800.00000000, null, null, 682.00000000, 2387.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '629', '~', null, '~', null, '21', '29', 33301.00000000, 34800.00000000, null, null, 706.00000000, 2472.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '630', '~', null, '~', null, '21', '30', 33301.00000000, 34800.00000000, null, null, 731.00000000, 2558.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '631', '~', null, '~', null, '22', '1', 34801.00000000, 36300.00000000, null, null, 25.00000000, 88.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '632', '~', null, '~', null, '22', '2', 34801.00000000, 36300.00000000, null, null, 51.00000000, 178.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '633', '~', null, '~', null, '22', '3', 34801.00000000, 36300.00000000, null, null, 76.00000000, 266.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '634', '~', null, '~', null, '22', '4', 34801.00000000, 36300.00000000, null, null, 102.00000000, 356.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '635', '~', null, '~', null, '22', '5', 34801.00000000, 36300.00000000, null, null, 127.00000000, 444.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '636', '~', null, '~', null, '22', '6', 34801.00000000, 36300.00000000, null, null, 153.00000000, 534.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '637', '~', null, '~', null, '22', '7', 34801.00000000, 36300.00000000, null, null, 178.00000000, 622.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '638', '~', null, '~', null, '22', '8', 34801.00000000, 36300.00000000, null, null, 203.00000000, 712.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '639', '~', null, '~', null, '22', '9', 34801.00000000, 36300.00000000, null, null, 229.00000000, 800.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '640', '~', null, '~', null, '22', '10', 34801.00000000, 36300.00000000, null, null, 254.00000000, 890.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '641', '~', null, '~', null, '22', '11', 34801.00000000, 36300.00000000, null, null, 280.00000000, 978.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '642', '~', null, '~', null, '22', '12', 34801.00000000, 36300.00000000, null, null, 305.00000000, 1068.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '643', '~', null, '~', null, '22', '13', 34801.00000000, 36300.00000000, null, null, 330.00000000, 1156.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '644', '~', null, '~', null, '22', '14', 34801.00000000, 36300.00000000, null, null, 356.00000000, 1246.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '645', '~', null, '~', null, '22', '15', 34801.00000000, 36300.00000000, null, null, 381.00000000, 1334.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '646', '~', null, '~', null, '22', '16', 34801.00000000, 36300.00000000, null, null, 407.00000000, 1423.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '647', '~', null, '~', null, '22', '17', 34801.00000000, 36300.00000000, null, null, 432.00000000, 1512.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '648', '~', null, '~', null, '22', '18', 34801.00000000, 36300.00000000, null, null, 458.00000000, 1600.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '649', '~', null, '~', null, '22', '19', 34801.00000000, 36300.00000000, null, null, 483.00000000, 1690.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '650', '~', null, '~', null, '22', '20', 34801.00000000, 36300.00000000, null, null, 508.00000000, 1778.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '651', '~', null, '~', null, '22', '21', 34801.00000000, 36300.00000000, null, null, 534.00000000, 1868.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '652', '~', null, '~', null, '22', '22', 34801.00000000, 36300.00000000, null, null, 559.00000000, 1956.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '653', '~', null, '~', null, '22', '23', 34801.00000000, 36300.00000000, null, null, 585.00000000, 2046.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '654', '~', null, '~', null, '22', '24', 34801.00000000, 36300.00000000, null, null, 610.00000000, 2134.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '655', '~', null, '~', null, '22', '25', 34801.00000000, 36300.00000000, null, null, 636.00000000, 2224.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '656', '~', null, '~', null, '22', '26', 34801.00000000, 36300.00000000, null, null, 661.00000000, 2312.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '657', '~', null, '~', null, '22', '27', 34801.00000000, 36300.00000000, null, null, 686.00000000, 2402.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '658', '~', null, '~', null, '22', '28', 34801.00000000, 36300.00000000, null, null, 712.00000000, 2490.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '659', '~', null, '~', null, '22', '29', 34801.00000000, 36300.00000000, null, null, 737.00000000, 2579.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '660', '~', null, '~', null, '22', '30', 34801.00000000, 36300.00000000, null, null, 763.00000000, 2668.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '661', '~', null, '~', null, '23', '1', 36301.00000000, 38200.00000000, null, null, 27.00000000, 94.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '662', '~', null, '~', null, '23', '2', 36301.00000000, 38200.00000000, null, null, 53.00000000, 187.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '663', '~', null, '~', null, '23', '3', 36301.00000000, 38200.00000000, null, null, 81.00000000, 281.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '664', '~', null, '~', null, '23', '4', 36301.00000000, 38200.00000000, null, null, 107.00000000, 375.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '665', '~', null, '~', null, '23', '5', 36301.00000000, 38200.00000000, null, null, 134.00000000, 468.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '666', '~', null, '~', null, '23', '6', 36301.00000000, 38200.00000000, null, null, 160.00000000, 561.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '667', '~', null, '~', null, '23', '7', 36301.00000000, 38200.00000000, null, null, 187.00000000, 655.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '668', '~', null, '~', null, '23', '8', 36301.00000000, 38200.00000000, null, null, 214.00000000, 748.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '669', '~', null, '~', null, '23', '9', 36301.00000000, 38200.00000000, null, null, 241.00000000, 842.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '670', '~', null, '~', null, '23', '10', 36301.00000000, 38200.00000000, null, null, 267.00000000, 936.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '671', '~', null, '~', null, '23', '11', 36301.00000000, 38200.00000000, null, null, 294.00000000, 1029.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '672', '~', null, '~', null, '23', '12', 36301.00000000, 38200.00000000, null, null, 321.00000000, 1123.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '673', '~', null, '~', null, '23', '13', 36301.00000000, 38200.00000000, null, null, 348.00000000, 1217.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '674', '~', null, '~', null, '23', '14', 36301.00000000, 38200.00000000, null, null, 375.00000000, 1310.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '675', '~', null, '~', null, '23', '15', 36301.00000000, 38200.00000000, null, null, 401.00000000, 1404.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '676', '~', null, '~', null, '23', '16', 36301.00000000, 38200.00000000, null, null, 428.00000000, 1498.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '677', '~', null, '~', null, '23', '17', 36301.00000000, 38200.00000000, null, null, 454.00000000, 1592.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '678', '~', null, '~', null, '23', '18', 36301.00000000, 38200.00000000, null, null, 481.00000000, 1684.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '679', '~', null, '~', null, '23', '19', 36301.00000000, 38200.00000000, null, null, 508.00000000, 1778.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '680', '~', null, '~', null, '23', '20', 36301.00000000, 38200.00000000, null, null, 535.00000000, 1872.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '681', '~', null, '~', null, '23', '21', 36301.00000000, 38200.00000000, null, null, 561.00000000, 1965.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '682', '~', null, '~', null, '23', '22', 36301.00000000, 38200.00000000, null, null, 588.00000000, 2059.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '683', '~', null, '~', null, '23', '23', 36301.00000000, 38200.00000000, null, null, 615.00000000, 2153.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '684', '~', null, '~', null, '23', '24', 36301.00000000, 38200.00000000, null, null, 642.00000000, 2246.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '685', '~', null, '~', null, '23', '25', 36301.00000000, 38200.00000000, null, null, 669.00000000, 2340.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '686', '~', null, '~', null, '23', '26', 36301.00000000, 38200.00000000, null, null, 695.00000000, 2434.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '687', '~', null, '~', null, '23', '27', 36301.00000000, 38200.00000000, null, null, 722.00000000, 2527.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '688', '~', null, '~', null, '23', '28', 36301.00000000, 38200.00000000, null, null, 748.00000000, 2621.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '689', '~', null, '~', null, '23', '29', 36301.00000000, 38200.00000000, null, null, 776.00000000, 2714.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '690', '~', null, '~', null, '23', '30', 36301.00000000, 38200.00000000, null, null, 802.00000000, 2807.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '691', '~', null, '~', null, '24', '1', 38201.00000000, 40100.00000000, null, null, 28.00000000, 98.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '692', '~', null, '~', null, '24', '2', 38201.00000000, 40100.00000000, null, null, 56.00000000, 197.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '693', '~', null, '~', null, '24', '3', 38201.00000000, 40100.00000000, null, null, 84.00000000, 295.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '694', '~', null, '~', null, '24', '4', 38201.00000000, 40100.00000000, null, null, 113.00000000, 393.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '695', '~', null, '~', null, '24', '5', 38201.00000000, 40100.00000000, null, null, 140.00000000, 491.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '696', '~', null, '~', null, '24', '6', 38201.00000000, 40100.00000000, null, null, 168.00000000, 589.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '697', '~', null, '~', null, '24', '7', 38201.00000000, 40100.00000000, null, null, 197.00000000, 687.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '698', '~', null, '~', null, '24', '8', 38201.00000000, 40100.00000000, null, null, 224.00000000, 786.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '699', '~', null, '~', null, '24', '9', 38201.00000000, 40100.00000000, null, null, 253.00000000, 884.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '700', '~', null, '~', null, '24', '10', 38201.00000000, 40100.00000000, null, null, 281.00000000, 983.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '701', '~', null, '~', null, '24', '11', 38201.00000000, 40100.00000000, null, null, 308.00000000, 1081.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '702', '~', null, '~', null, '24', '12', 38201.00000000, 40100.00000000, null, null, 337.00000000, 1179.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '703', '~', null, '~', null, '24', '13', 38201.00000000, 40100.00000000, null, null, 365.00000000, 1278.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '704', '~', null, '~', null, '24', '14', 38201.00000000, 40100.00000000, null, null, 393.00000000, 1375.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '705', '~', null, '~', null, '24', '15', 38201.00000000, 40100.00000000, null, null, 421.00000000, 1473.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '706', '~', null, '~', null, '24', '16', 38201.00000000, 40100.00000000, null, null, 449.00000000, 1572.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '707', '~', null, '~', null, '24', '17', 38201.00000000, 40100.00000000, null, null, 477.00000000, 1670.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '708', '~', null, '~', null, '24', '18', 38201.00000000, 40100.00000000, null, null, 505.00000000, 1768.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '709', '~', null, '~', null, '24', '19', 38201.00000000, 40100.00000000, null, null, 534.00000000, 1867.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '710', '~', null, '~', null, '24', '20', 38201.00000000, 40100.00000000, null, null, 561.00000000, 1965.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '711', '~', null, '~', null, '24', '21', 38201.00000000, 40100.00000000, null, null, 589.00000000, 2063.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '712', '~', null, '~', null, '24', '22', 38201.00000000, 40100.00000000, null, null, 618.00000000, 2162.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '713', '~', null, '~', null, '24', '23', 38201.00000000, 40100.00000000, null, null, 645.00000000, 2259.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '714', '~', null, '~', null, '24', '24', 38201.00000000, 40100.00000000, null, null, 674.00000000, 2358.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '715', '~', null, '~', null, '24', '25', 38201.00000000, 40100.00000000, null, null, 702.00000000, 2456.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '716', '~', null, '~', null, '24', '26', 38201.00000000, 40100.00000000, null, null, 730.00000000, 2554.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '717', '~', null, '~', null, '24', '27', 38201.00000000, 40100.00000000, null, null, 758.00000000, 2653.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '718', '~', null, '~', null, '24', '28', 38201.00000000, 40100.00000000, null, null, 786.00000000, 2751.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '719', '~', null, '~', null, '24', '29', 38201.00000000, 40100.00000000, null, null, 815.00000000, 2849.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '720', '~', null, '~', null, '24', '30', 38201.00000000, 40100.00000000, null, null, 842.00000000, 2948.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '721', '~', null, '~', null, '25', '1', 40101.00000000, 42000.00000000, null, null, 30.00000000, 103.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '722', '~', null, '~', null, '25', '2', 40101.00000000, 42000.00000000, null, null, 59.00000000, 206.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '723', '~', null, '~', null, '25', '3', 40101.00000000, 42000.00000000, null, null, 88.00000000, 308.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '724', '~', null, '~', null, '25', '4', 40101.00000000, 42000.00000000, null, null, 117.00000000, 411.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '725', '~', null, '~', null, '25', '5', 40101.00000000, 42000.00000000, null, null, 147.00000000, 515.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '726', '~', null, '~', null, '25', '6', 40101.00000000, 42000.00000000, null, null, 177.00000000, 618.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '727', '~', null, '~', null, '25', '7', 40101.00000000, 42000.00000000, null, null, 206.00000000, 721.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '728', '~', null, '~', null, '25', '8', 40101.00000000, 42000.00000000, null, null, 235.00000000, 823.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '729', '~', null, '~', null, '25', '9', 40101.00000000, 42000.00000000, null, null, 264.00000000, 926.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '730', '~', null, '~', null, '25', '10', 40101.00000000, 42000.00000000, null, null, 294.00000000, 1029.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '731', '~', null, '~', null, '25', '11', 40101.00000000, 42000.00000000, null, null, 324.00000000, 1132.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '732', '~', null, '~', null, '25', '12', 40101.00000000, 42000.00000000, null, null, 353.00000000, 1235.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '733', '~', null, '~', null, '25', '13', 40101.00000000, 42000.00000000, null, null, 382.00000000, 1337.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '734', '~', null, '~', null, '25', '14', 40101.00000000, 42000.00000000, null, null, 411.00000000, 1440.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '735', '~', null, '~', null, '25', '15', 40101.00000000, 42000.00000000, null, null, 441.00000000, 1544.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '736', '~', null, '~', null, '25', '16', 40101.00000000, 42000.00000000, null, null, 471.00000000, 1647.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '737', '~', null, '~', null, '25', '17', 40101.00000000, 42000.00000000, null, null, 500.00000000, 1750.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '738', '~', null, '~', null, '25', '18', 40101.00000000, 42000.00000000, null, null, 529.00000000, 1852.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '739', '~', null, '~', null, '25', '19', 40101.00000000, 42000.00000000, null, null, 558.00000000, 1955.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '740', '~', null, '~', null, '25', '20', 40101.00000000, 42000.00000000, null, null, 588.00000000, 2058.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '741', '~', null, '~', null, '25', '21', 40101.00000000, 42000.00000000, null, null, 618.00000000, 2161.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '742', '~', null, '~', null, '25', '22', 40101.00000000, 42000.00000000, null, null, 647.00000000, 2264.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '743', '~', null, '~', null, '25', '23', 40101.00000000, 42000.00000000, null, null, 676.00000000, 2366.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '744', '~', null, '~', null, '25', '24', 40101.00000000, 42000.00000000, null, null, 705.00000000, 2469.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '745', '~', null, '~', null, '25', '25', 40101.00000000, 42000.00000000, null, null, 735.00000000, 2573.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '746', '~', null, '~', null, '25', '26', 40101.00000000, 42000.00000000, null, null, 765.00000000, 2676.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '747', '~', null, '~', null, '25', '27', 40101.00000000, 42000.00000000, null, null, 794.00000000, 2779.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '748', '~', null, '~', null, '25', '28', 40101.00000000, 42000.00000000, null, null, 823.00000000, 2881.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '749', '~', null, '~', null, '25', '29', 40101.00000000, 42000.00000000, null, null, 852.00000000, 2984.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '750', '~', null, '~', null, '25', '30', 40101.00000000, 42000.00000000, null, null, 882.00000000, 3087.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '751', '~', null, '~', null, '26', '1', 42001.00000000, 43900.00000000, null, null, 31.00000000, 107.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '752', '~', null, '~', null, '26', '2', 42001.00000000, 43900.00000000, null, null, 62.00000000, 215.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '753', '~', null, '~', null, '26', '3', 42001.00000000, 43900.00000000, null, null, 92.00000000, 323.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '754', '~', null, '~', null, '26', '4', 42001.00000000, 43900.00000000, null, null, 123.00000000, 430.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '755', '~', null, '~', null, '26', '5', 42001.00000000, 43900.00000000, null, null, 154.00000000, 538.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '756', '~', null, '~', null, '26', '6', 42001.00000000, 43900.00000000, null, null, 185.00000000, 645.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '757', '~', null, '~', null, '26', '7', 42001.00000000, 43900.00000000, null, null, 215.00000000, 753.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '758', '~', null, '~', null, '26', '8', 42001.00000000, 43900.00000000, null, null, 245.00000000, 860.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '759', '~', null, '~', null, '26', '9', 42001.00000000, 43900.00000000, null, null, 276.00000000, 968.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '760', '~', null, '~', null, '26', '10', 42001.00000000, 43900.00000000, null, null, 307.00000000, 1075.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '761', '~', null, '~', null, '26', '11', 42001.00000000, 43900.00000000, null, null, 338.00000000, 1183.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '762', '~', null, '~', null, '26', '12', 42001.00000000, 43900.00000000, null, null, 369.00000000, 1291.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '763', '~', null, '~', null, '26', '13', 42001.00000000, 43900.00000000, null, null, 399.00000000, 1398.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '764', '~', null, '~', null, '26', '14', 42001.00000000, 43900.00000000, null, null, 430.00000000, 1505.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '765', '~', null, '~', null, '26', '15', 42001.00000000, 43900.00000000, null, null, 461.00000000, 1614.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '766', '~', null, '~', null, '26', '16', 42001.00000000, 43900.00000000, null, null, 492.00000000, 1721.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '767', '~', null, '~', null, '26', '17', 42001.00000000, 43900.00000000, null, null, 523.00000000, 1828.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '768', '~', null, '~', null, '26', '18', 42001.00000000, 43900.00000000, null, null, 553.00000000, 1936.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '769', '~', null, '~', null, '26', '19', 42001.00000000, 43900.00000000, null, null, 584.00000000, 2044.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '770', '~', null, '~', null, '26', '20', 42001.00000000, 43900.00000000, null, null, 615.00000000, 2151.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '771', '~', null, '~', null, '26', '21', 42001.00000000, 43900.00000000, null, null, 645.00000000, 2259.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '772', '~', null, '~', null, '26', '22', 42001.00000000, 43900.00000000, null, null, 676.00000000, 2366.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '773', '~', null, '~', null, '26', '23', 42001.00000000, 43900.00000000, null, null, 706.00000000, 2474.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '774', '~', null, '~', null, '26', '24', 42001.00000000, 43900.00000000, null, null, 737.00000000, 2581.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '775', '~', null, '~', null, '26', '25', 42001.00000000, 43900.00000000, null, null, 768.00000000, 2689.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '776', '~', null, '~', null, '26', '26', 42001.00000000, 43900.00000000, null, null, 799.00000000, 2796.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '777', '~', null, '~', null, '26', '27', 42001.00000000, 43900.00000000, null, null, 830.00000000, 2904.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '778', '~', null, '~', null, '26', '28', 42001.00000000, 43900.00000000, null, null, 860.00000000, 3012.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '779', '~', null, '~', null, '26', '29', 42001.00000000, 43900.00000000, null, null, 891.00000000, 3119.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '780', '~', null, '~', null, '26', '30', 42001.00000000, 43900.00000000, null, null, 922.00000000, 3226.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '781', '~', null, '~', null, '27', '1', 43901.00000000, 45800.00000000, null, null, 32.00000000, 113.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '782', '~', null, '~', null, '27', '2', 43901.00000000, 45800.00000000, null, null, 64.00000000, 224.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '783', '~', null, '~', null, '27', '3', 43901.00000000, 45800.00000000, null, null, 96.00000000, 337.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '784', '~', null, '~', null, '27', '4', 43901.00000000, 45800.00000000, null, null, 128.00000000, 449.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '785', '~', null, '~', null, '27', '5', 43901.00000000, 45800.00000000, null, null, 160.00000000, 561.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '786', '~', null, '~', null, '27', '6', 43901.00000000, 45800.00000000, null, null, 192.00000000, 673.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '787', '~', null, '~', null, '27', '7', 43901.00000000, 45800.00000000, null, null, 224.00000000, 786.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '788', '~', null, '~', null, '27', '8', 43901.00000000, 45800.00000000, null, null, 256.00000000, 897.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '789', '~', null, '~', null, '27', '9', 43901.00000000, 45800.00000000, null, null, 288.00000000, 1010.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '790', '~', null, '~', null, '27', '10', 43901.00000000, 45800.00000000, null, null, 321.00000000, 1122.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '791', '~', null, '~', null, '27', '11', 43901.00000000, 45800.00000000, null, null, 353.00000000, 1235.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '792', '~', null, '~', null, '27', '12', 43901.00000000, 45800.00000000, null, null, 385.00000000, 1346.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '793', '~', null, '~', null, '27', '13', 43901.00000000, 45800.00000000, null, null, 417.00000000, 1459.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '794', '~', null, '~', null, '27', '14', 43901.00000000, 45800.00000000, null, null, 449.00000000, 1571.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '795', '~', null, '~', null, '27', '15', 43901.00000000, 45800.00000000, null, null, 481.00000000, 1683.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '796', '~', null, '~', null, '27', '16', 43901.00000000, 45800.00000000, null, null, 513.00000000, 1795.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '797', '~', null, '~', null, '27', '17', 43901.00000000, 45800.00000000, null, null, 545.00000000, 1908.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '798', '~', null, '~', null, '27', '18', 43901.00000000, 45800.00000000, null, null, 577.00000000, 2019.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '799', '~', null, '~', null, '27', '19', 43901.00000000, 45800.00000000, null, null, 609.00000000, 2132.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '800', '~', null, '~', null, '27', '20', 43901.00000000, 45800.00000000, null, null, 641.00000000, 2244.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '801', '~', null, '~', null, '27', '21', 43901.00000000, 45800.00000000, null, null, 673.00000000, 2356.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '802', '~', null, '~', null, '27', '22', 43901.00000000, 45800.00000000, null, null, 705.00000000, 2469.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '803', '~', null, '~', null, '27', '23', 43901.00000000, 45800.00000000, null, null, 737.00000000, 2581.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '804', '~', null, '~', null, '27', '24', 43901.00000000, 45800.00000000, null, null, 769.00000000, 2693.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '805', '~', null, '~', null, '27', '25', 43901.00000000, 45800.00000000, null, null, 801.00000000, 2805.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '806', '~', null, '~', null, '27', '26', 43901.00000000, 45800.00000000, null, null, 833.00000000, 2918.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '807', '~', null, '~', null, '27', '27', 43901.00000000, 45800.00000000, null, null, 865.00000000, 3030.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '808', '~', null, '~', null, '27', '28', 43901.00000000, 45800.00000000, null, null, 897.00000000, 3142.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '809', '~', null, '~', null, '27', '29', 43901.00000000, 45800.00000000, null, null, 930.00000000, 3254.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '810', '~', null, '~', null, '27', '30', 43901.00000000, 45800.00000000, null, null, 962.00000000, 3367.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '811', '~', null, '~', null, '28', '1', 45801.00000000, 0, null, null, 32.00000000, 113.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '812', '~', null, '~', null, '28', '2', 45801.00000000, 0, null, null, 64.00000000, 224.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '813', '~', null, '~', null, '28', '3', 45801.00000000, 0, null, null, 96.00000000, 337.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '814', '~', null, '~', null, '28', '4', 45801.00000000, 0, null, null, 128.00000000, 449.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '815', '~', null, '~', null, '28', '5', 45801.00000000, 0, null, null, 160.00000000, 561.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '816', '~', null, '~', null, '28', '6', 45801.00000000, 0, null, null, 192.00000000, 673.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '817', '~', null, '~', null, '28', '7', 45801.00000000, 0, null, null, 224.00000000, 786.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '818', '~', null, '~', null, '28', '8', 45801.00000000, 0, null, null, 256.00000000, 897.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '819', '~', null, '~', null, '28', '9', 45801.00000000, 0, null, null, 288.00000000, 1010.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '820', '~', null, '~', null, '28', '10', 45801.00000000, 0, null, null, 321.00000000, 1122.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '821', '~', null, '~', null, '28', '11', 45801.00000000, 0, null, null, 353.00000000, 1235.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '822', '~', null, '~', null, '28', '12', 45801.00000000, 0, null, null, 385.00000000, 1346.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '823', '~', null, '~', null, '28', '13', 45801.00000000, 0, null, null, 417.00000000, 1459.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '824', '~', null, '~', null, '28', '14', 45801.00000000, 0, null, null, 449.00000000, 1571.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '825', '~', null, '~', null, '28', '15', 45801.00000000, 0, null, null, 481.00000000, 1683.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '826', '~', null, '~', null, '28', '16', 45801.00000000, 0, null, null, 513.00000000, 1795.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '827', '~', null, '~', null, '28', '17', 45801.00000000, 0, null, null, 545.00000000, 1908.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '828', '~', null, '~', null, '28', '18', 45801.00000000, 0, null, null, 577.00000000, 2019.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '829', '~', null, '~', null, '28', '19', 45801.00000000, 0, null, null, 609.00000000, 2132.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '830', '~', null, '~', null, '28', '20', 45801.00000000, 0, null, null, 641.00000000, 2244.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '831', '~', null, '~', null, '28', '21', 45801.00000000, 0, null, null, 673.00000000, 2356.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '832', '~', null, '~', null, '28', '22', 45801.00000000, 0, null, null, 705.00000000, 2469.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '833', '~', null, '~', null, '28', '23', 45801.00000000, 0, null, null, 737.00000000, 2581.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '834', '~', null, '~', null, '28', '24', 45801.00000000, 0, null, null, 769.00000000, 2693.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '835', '~', null, '~', null, '28', '25', 45801.00000000, 0, null, null, 801.00000000, 2805.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '836', '~', null, '~', null, '28', '26', 45801.00000000, 0, null, null, 833.00000000, 2918.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '837', '~', null, '~', null, '28', '27', 45801.00000000, 0, null, null, 865.00000000, 3030.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '838', '~', null, '~', null, '28', '28', 45801.00000000, 0, null, null, 897.00000000, 3142.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '839', '~', null, '~', null, '28', '29', 45801.00000000, 0, null, null, 930.00000000, 3254.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL1'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '840', '~', null, '~', null, '28', '30', 45801.00000000, 0, null, null, 962.00000000, 3367.00000000, null, '2017-01-04 17:12:59', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '1', '~', null, '~', null, '1', '1', 0, 1500.00000000, null, null, 0, 90.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '2', '~', null, '~', null, '1', '2', 1501.00000000, 3000.00000000, null, null, 0, 180.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '3', '~', null, '~', null, '1', '3', 3001.00000000, 4500.00000000, null, null, 0, 270.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '4', '~', null, '~', null, '1', '4', 4501.00000000, 6000.00000000, null, null, 0, 360.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '5', '~', null, '~', null, '1', '5', 6001.00000000, 7500.00000000, null, null, 0, 450.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '6', '~', null, '~', null, '2', '6', 7501.00000000, 8700.00000000, null, null, 0, 522.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '7', '~', null, '~', null, '2', '7', 8701.00000000, 9900.00000000, null, null, 0, 594.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '8', '~', null, '~', null, '2', '8', 9901.00000000, 11100.00000000, null, null, 0, 666.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '9', '~', null, '~', null, '2', '9', 11101.00000000, 12540.00000000, null, null, 0, 752.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '10', '~', null, '~', null, '2', '10', 12541.00000000, 13500.00000000, null, null, 0, 810.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '11', '~', null, '~', null, '3', '11', 13501.00000000, 15840.00000000, null, null, 0, 950.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '12', '~', null, '~', null, '3', '12', 15841.00000000, 16500.00000000, null, null, 0, 990.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '13', '~', null, '~', null, '3', '13', 16501.00000000, 17280.00000000, null, null, 0, 1037.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '14', '~', null, '~', null, '3', '14', 17281.00000000, 17880.00000000, null, null, 0, 1073.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '15', '~', null, '~', null, '3', '15', 17881.00000000, 19047.00000000, null, null, 0, 1143.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '16', '~', null, '~', null, '3', '16', 19048.00000000, 20008.00000000, null, null, 0, 1200.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '17', '~', null, '~', null, '3', '17', 20009.00000000, 21009.00000000, null, null, 0, 1261.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '18', '~', null, '~', null, '3', '18', 21010.00000000, 21900.00000000, null, null, 0, 1314.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '19', '~', null, '~', null, '3', '19', 21901.00000000, 22800.00000000, null, null, 0, 1368.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '20', '~', null, '~', null, '3', '20', 22801.00000000, 24000.00000000, null, null, 0, 1440.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '21', '~', null, '~', null, '4', '21', 24001.00000000, 25200.00000000, null, null, 0, 1512.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '22', '~', null, '~', null, '4', '22', 25201.00000000, 26400.00000000, null, null, 0, 1584.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '23', '~', null, '~', null, '4', '23', 26401.00000000, 27600.00000000, null, null, 0, 1656.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '24', '~', null, '~', null, '4', '24', 27601.00000000, 28800.00000000, null, null, 0, 1728.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '25', '~', null, '~', null, '4', '25', 28801.00000000, 30300.00000000, null, null, 0, 1818.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '26', '~', null, '~', null, '5', '26', 30301.00000000, 31800.00000000, null, null, 0, 1908.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '27', '~', null, '~', null, '5', '27', 31801.00000000, 33300.00000000, null, null, 0, 1998.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '28', '~', null, '~', null, '5', '28', 33301.00000000, 34800.00000000, null, null, 0, 2088.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '29', '~', null, '~', null, '5', '29', 34801.00000000, 36300.00000000, null, null, 0, 2178.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '30', '~', null, '~', null, '5', '30', 36301.00000000, 38200.00000000, null, null, 0, 2292.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '31', '~', null, '~', null, '6', '31', 38201.00000000, 40100.00000000, null, null, 0, 2406.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '32', '~', null, '~', null, '6', '32', 40101.00000000, 42000.00000000, null, null, 0, 2520.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '33', '~', null, '~', null, '6', '33', 42001.00000000, 43900.00000000, null, null, 0, 2634.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '34', '~', null, '~', null, '6', '34', 43901.00000000, 45800.00000000, null, null, 0, 2748.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '35', '~', null, '~', null, '6', '35', 45801.00000000, 48200.00000000, null, null, 0, 2892.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '36', '~', null, '~', null, '7', '36', 48201.00000000, 50600.00000000, null, null, 0, 3036.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '37', '~', null, '~', null, '7', '37', 50601.00000000, 53000.00000000, null, null, 0, 3180.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '38', '~', null, '~', null, '7', '38', 53001.00000000, 55400.00000000, null, null, 0, 3324.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '39', '~', null, '~', null, '7', '39', 55401.00000000, 57800.00000000, null, null, 0, 3468.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '40', '~', null, '~', null, '7', '40', 57801.00000000, 60800.00000000, null, null, 0, 3648.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '41', '~', null, '~', null, '8', '41', 60801.00000000, 63800.00000000, null, null, 0, 3828.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '42', '~', null, '~', null, '8', '42', 63801.00000000, 66800.00000000, null, null, 0, 4008.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '43', '~', null, '~', null, '8', '43', 66801.00000000, 69800.00000000, null, null, 0, 4188.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '44', '~', null, '~', null, '8', '44', 69801.00000000, 72800.00000000, null, null, 0, 4368.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '45', '~', null, '~', null, '8', '45', 72801.00000000, 76500.00000000, null, null, 0, 4590.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '46', '~', null, '~', null, '9', '46', 76501.00000000, 80200.00000000, null, null, 0, 4812.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '47', '~', null, '~', null, '9', '47', 80201.00000000, 83900.00000000, null, null, 0, 5034.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '48', '~', null, '~', null, '9', '48', 83901.00000000, 87600.00000000, null, null, 0, 5256.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '49', '~', null, '~', null, '9', '49', 87601.00000000, 92100.00000000, null, null, 0, 5526.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '50', '~', null, '~', null, '10', '50', 92101.00000000, 96600.00000000, null, null, 0, 5796.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '51', '~', null, '~', null, '10', '51', 96601.00000000, 101100.00000000, null, null, 0, 6066.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '52', '~', null, '~', null, '10', '52', 101101.00000000, 105600.00000000, null, null, 0, 6336.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '53', '~', null, '~', null, '10', '53', 105601.00000000, 110100.00000000, null, null, 0, 6606.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '54', '~', null, '~', null, '10', '54', 110101.00000000, 115500.00000000, null, null, 0, 6930.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '55', '~', null, '~', null, '11', '55', 115501.00000000, 120900.00000000, null, null, 0, 7254.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '56', '~', null, '~', null, '11', '56', 120901.00000000, 126300.00000000, null, null, 0, 7578.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '57', '~', null, '~', null, '11', '57', 126301.00000000, 131700.00000000, null, null, 0, 7902.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '58', '~', null, '~', null, '11', '58', 131701.00000000, 137100.00000000, null, null, 0, 8226.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '59', '~', null, '~', null, '11', '59', 137101.00000000, 142500.00000000, null, null, 0, 8550.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '60', '~', null, '~', null, '11', '60', 142501.00000000, 147900.00000000, null, null, 0, 8874.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '61', '~', null, '~', null, '11', '61', 147901.00000000, 150000.00000000, null, null, 0, 9000.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

INSERT INTO twhr_rangeline (pk_rangetable, pk_rangeline, pk_group, pk_org, pk_org_v, rowno, creator, creationtime, modifier, modifiedtime, rangeclass, rangegrade, rangelower, rangeupper, rangerate, rangerevise, employeeamount, employeramount, memo, ts, dr)
select replace(pk_org, '000000000', 'RANGETBL2'), replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_rangeline), '000000000'))), pk_group, pk_org, pk_vid, '62', '~', null, '~', null, '11', '62', 150001.00000000, 0, null, null, 0, 9000.00000000, null, '2017-01-04 17:15:07', 0
from org_orgs 
where isbusinessunit='Y';

