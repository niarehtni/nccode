DELETE FROM twhr_basedoc;

INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '補充保險費率(僱主)', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 3, 'TWEP0002', 2, 1.91000000, '~', '2016-01-18 11:44:53', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '普通健保費承擔比例(僱主)', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 2, 'TWNP0003', 2, 60.00000000, '~', '2015-04-24 12:08:30', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '平均眷屬人數', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 2, 'TWNP0004', 1, 1.61000000, '~', '2016-02-01 11:17:22', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '補充保險費率(個人)', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 3, 'TWEP0001', 2, 1.91000000, '~', '2016-01-18 11:44:53', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '普通事故保險費承擔比例(個人)', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 1, 'TWLP0002', 2, 20.00000000, '~', '2015-04-24 12:08:30', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '免稅額(標準)', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 4, 'TWSP0001', 1, 88000.00000000, '~', '2017-01-04 17:24:15', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '免稅額(年滿70歲)', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 4, 'TWSP0002', 1, 127500.00000000, '~', '2015-04-24 12:08:30', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '扣除額(單身)', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 4, 'TWSP0003', 1, 90000.00000000, '~', '2015-05-07 16:13:53', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '扣除額(夫妻合併)', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 4, 'TWSP0004', 1, 180000.00000000, '~', '2015-05-07 16:13:53', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '特別扣除額(薪資所得, 每人)', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 4, 'TWSP0005', 1, 128000.00000000, '~', '2015-05-07 16:13:53', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '特別扣除額(儲蓄投資, 每戶)', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 4, 'TWSP0006', 1, 270000.00000000, '~', '2015-04-24 12:08:30', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '特別扣除額(身心障礙, 每人)', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 4, 'TWSP0007', 1, 128000.00000000, '~', '2015-05-07 16:13:53', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '代扣所得稅最小給付金額', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 4, 'TWSP0008', 1, 73000.00000000, '~', '2015-05-07 16:12:08', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '基本工資(月)', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 4, 'TWSP0009', 1, 21009.00000000, '~', '2017-07-28 13:23:07', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '常住居民天數', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 4, 'TWSP0013', 1, 183.00000000, '~', '2015-04-24 12:08:30', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '代扣所得稅最小扣除額', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 4, 'TWSP0014', 1, 2000.00000000, '~', '2015-06-04 21:23:41', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '勞保政府補助比例', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 1, 'TWLP0010', 2, 10.00000000, '~', '2015-05-07 15:39:52', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '積欠工資墊償費率', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 1, 'TWLP0013', 2, 0.02500000, '~', '2015-04-24 12:08:30', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '勞保投保薪資上限', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 1, 'TWLP0014', 1, 45800.00000000, '~', '2016-05-11 11:35:43', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '勞退金提繳薪資上限', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 1, 'TWLP0015', 1, 150000.00000000, '~', '2015-04-24 12:08:30', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '勞退金員工自願提繳率', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 1, 'TWLP0017', 2, 0E-8, '~', '2015-10-15 10:03:23', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '健保投保薪資上限', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 2, 'TWLP0016', 1, 182000.00000000, '~', '2015-05-07 15:39:52', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '普通事故保險費承擔比例(僱主)', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 1, 'TWLP0003', 2, 70.00000000, '~', '2015-04-24 12:08:30', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '就業保險費率', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 1, 'TWLP0004', 2, 1.00000000, '~', '2015-04-24 12:08:30', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '就業保險費承擔比例(個人)', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 1, 'TWLP0005', 2, 20.00000000, '~', '2015-04-24 12:08:30', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '就業保險費承擔比例(僱主)', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 1, 'TWLP0006', 2, 70.00000000, '~', '2015-04-24 12:08:30', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '職業災害保險費率(本業)', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 1, 'TWLP0007', 2, 0.19000000, '~', '2016-01-06 17:18:01', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '職業災害保險費承擔比例(個人)', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 1, 'TWLP0008', 2, 0E-8, '~', '2015-04-24 12:08:30', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '職業災害保險費承擔比例(僱主)', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 1, 'TWLP0009', 2, 100.00000000, '~', '2015-04-24 12:08:30', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '勞退金僱主提繳率', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 1, 'TWLP0011', 2, 6.00000000, '~', '2015-04-24 12:08:30', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '員工福利金提撥率', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 4, 'TWLP0012', 2, 0.50000000, '~', '2015-05-08 10:59:04', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '普通健保費率', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 2, 'TWNP0001', 2, 4.69000000, '~', '2016-01-18 11:44:14', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '普通健保費承擔比例(個人)', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 2, 'TWNP0002', 2, 30.00000000, '~', '2015-04-24 12:08:30', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '普通事故保險費率', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 1, 'TWLP0001', 2, 9.50000000, '~', '2017-01-04 14:29:50', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '外籍勞工扣稅基本工資倍數', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 4, 'TWSP0010', 1, 1.50000000, '~', '2015-04-24 12:08:30', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '外籍勞工扣稅稅率', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 4, 'TWSP0011', 2, 6.00000000, '~', '2015-04-24 12:08:30', 0
from org_orgs 
where isbusinessunit='Y';


INSERT INTO twhr_basedoc (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, pk_group, pk_org, pk_org_v, creator, creationtime, modifier, modifiedtime, id, doccategory, code, doctype, numbervalue, waitemvalue, ts, dr)
select null, '外籍勞工超額扣稅稅率', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, (select pk_group from (select pk_group from org_group order by pk_group) where rownum=1), pk_org, pk_vid, (select cuserid from (select * from sm_user where user_type = 0 order by cuserid) where rownum=1), '2015-04-24 12:08:30', '~', null, replace(pk_org, '000000000', ltrim(to_char((select count(*)+1 from twhr_basedoc), '000000000'))), 4, 'TWSP0012', 2, 18.00000000, '~', '2015-04-24 12:08:30', 0
from org_orgs 
where isbusinessunit='Y';



