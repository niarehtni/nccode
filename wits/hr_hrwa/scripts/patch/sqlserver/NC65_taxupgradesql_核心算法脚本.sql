
/*==============================================================*/
/* Table: wa_additiona_deduction                                */
/*==============================================================*/
-- 如果之前没有执行过脚本 前面两行可能报错，忽略
--alter table WA_PERIOD drop column TAXYEAR, TAXPERIOD;

-- 薪资期间表增加纳税年度，纳税期间
ALTER TABLE [WA_PERIOD]
	ADD [TAXYEAR] nchar(4) NULL, 
	[TAXPERIOD] nchar(2)  NULL;

-- 初始化薪资期间单据模板
-- select * from pub_billtemplet_b where itemkey in ('taxyear','taxperiod') and pk_billtemplet = '1001Z71000000000SKJZ';
delete from  pub_billtemplet_b where itemkey in ('taxyear','taxperiod') and pk_billtemplet = '1001Z71000000000SKJZ';
INSERT INTO pub_billtemplet_b(cardflag, datatype, defaultshowname, defaultvalue, dr, editflag, editformula, foreground, hyperlinkflag, idcolname, inputlength, itemkey, itemtype, leafflag, listflag, listhyperlinkflag, listshowflag, listshoworder, loadformula, lockflag, metadatapath, metadataproperty, metadatarelation, newlineflag, nullflag, options, pk_billtemplet, pk_billtemplet_b, pk_corp, pos, reftype, resid, resid_tabname, reviseflag, showflag, showorder, table_code, table_name, totalflag, ts, userdefflag, userdefine1, userdefine2, userdefine3, usereditflag, userflag, userreviseflag, usershowflag, validateformula, width)
  VALUES(1, -1, NULL, NULL, 0, 1, NULL, -1, N'N', NULL, -1, N'taxperiod', 0, N'N', 0, N'N', N'N', 13, NULL, 0, N'taxperiod', N'hrwa.PeriodVO.taxperiod', NULL, N'N', 1, NULL, N'1001Z71000000000SKJZ', N'0001AA10000000000D5E', N'@@@@', 1, NULL, NULL, NULL, N'N', 1, 13, N'md_period', N'md_period', 0, '2018-11-28 10:51:11', N'N', NULL, NULL, NULL, 1, 1, N'N', 1, NULL, 100);
INSERT INTO pub_billtemplet_b(cardflag, datatype, defaultshowname, defaultvalue, dr, editflag, editformula, foreground, hyperlinkflag, idcolname, inputlength, itemkey, itemtype, leafflag, listflag, listhyperlinkflag, listshowflag, listshoworder, loadformula, lockflag, metadatapath, metadataproperty, metadatarelation, newlineflag, nullflag, options, pk_billtemplet, pk_billtemplet_b, pk_corp, pos, reftype, resid, resid_tabname, reviseflag, showflag, showorder, table_code, table_name, totalflag, ts, userdefflag, userdefine1, userdefine2, userdefine3, usereditflag, userflag, userreviseflag, usershowflag, validateformula, width)
  VALUES(1, -1, NULL, NULL, 0, 1, NULL, -1, N'N', NULL, -1, N'taxyear', 0, N'N', 0, N'N', N'N', 12, NULL, 0, N'taxyear', N'hrwa.PeriodVO.taxyear', NULL, N'N', 1, NULL, N'1001Z71000000000SKJZ', N'0001AA10000000000D5F', N'@@@@', 1, NULL, NULL, NULL, N'N', 1, 12, N'md_period', N'md_period', 0, '2018-11-28 10:51:11', N'N', NULL, NULL, NULL, 1, 1, N'N', 1, NULL, 100);
