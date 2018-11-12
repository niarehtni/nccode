--更新模板精度
update pub_billtemplet_b
set reftype = '4,-100000000000000000000.00,100000000000000000000.00'
where metadataproperty in (
select 'hrhi.' + tbl.id + '.' + col.name from md_column col
inner join md_table tbl on col.tableid = tbl.id 
where tbl.displayname in  ('勞保勞退信息', '健保信息', '勞保投保明細', '勞健保投保匯總')
and (col.displayname like '%率%' or col.displayname like '%比例%'))

update pub_billtemplet_b
set reftype = '0,-100000000000000000000.00,100000000000000000000.00'
where metadataproperty in (
select 'hrhi.' + tbl.id + '.' + col.name from md_column col
inner join md_table tbl on col.tableid = tbl.id 
where tbl.displayname in  ('勞保勞退信息', '健保信息', '勞保投保明細', '勞健保投保匯總')
and (col.displayname like '%额%' or col.displayname like '%级距%' or col.displayname like '%薪资%' or col.displayname like '%数%' or col.displayname like '%保费'))

--同步多語資源
update pub_billtemplet_b
set pub_billtemplet_b.resid = 
    (
      select hr_infoset_item.resid
      from hr_infoset_item
      where pub_billtemplet_b.metadataproperty = hr_infoset_item.meta_data
    ),
    pub_billtemplet_b.resid_tabname =
    (
      select hr_infoset_item.respath
      from hr_infoset_item
      where pub_billtemplet_b.metadataproperty = hr_infoset_item.meta_data
    )
where pub_billtemplet_b.metadataproperty like '%hi_psndoc_glbdef%'
  and exists 
    (
      select hr_infoset_item.respath
      from hr_infoset_item
      where pub_billtemplet_b.metadataproperty = hr_infoset_item.meta_data
    );

update pub_billtemplet_b
set pub_billtemplet_b.resid =
    (
      select md_property.resid
      from md_property
      where pub_billtemplet_b.metadataproperty = 'twhr.nhicalc.' || md_property.name
        and classid = (select id from md_class where fullclassname = 'nc.vo.twhr.nhicalc.NhiCalcVO')
    ),
    pub_billtemplet_b.resid_tabname = '68861705'
where pub_billtemplet_b.metadataproperty like 'twhr.nhicalc%'
  and exists
    (
      select md_property.resid
      from md_property
      where pub_billtemplet_b.metadataproperty = 'twhr.nhicalc.' || md_property.name
        and classid = (select id from md_class where fullclassname = 'nc.vo.twhr.nhicalc.NhiCalcVO')
    );



--增加勞健保默認設定
--delete from hi_psndoc_glbdef3
--delete from hi_psndoc_glbdef4

--insert into hi_psndoc_glbdef3 (begindate, enddate, glbdef1, glbdef10, glbdef11, pk_psndoc, pk_psndoc_sub, dr, lastflag, creator, creationtime)
--select org.begindate, null, '1001ZZ10000000001J3L', 'Y', 'Y', psn.pk_psndoc, RIGHT(psn.pk_psndoc, 5) + LEFT(psn.pk_psndoc, 15), 0, 'Y', '1001ZZ100000000001JP', (CONVERT([nchar](19),getdate(),(20))) 
--from bd_psndoc psn inner join hi_psnorg org on psn.pk_psndoc = org.pk_psndoc and org.lastflag = 'Y'

--insert into hi_psndoc_glbdef4 (begindate, enddate, glbdef1, glbdef3, glbdef5, glbdef14, glbdef15, pk_psndoc, pk_psndoc_sub, dr, lastflag, creator, creationtime)
--select (case when org.begindate<='1995-03-01' then '1995-03-01' else org.begindate end), null, name, LEFT(id, 10), '1001ZZ10000000001J5K', 'Y', 'Y', psn.pk_psndoc, RIGHT(psn.pk_psndoc, 15) + LEFT(psn.pk_psndoc, 5), 0, 'Y','1001ZZ100000000001JP', (CONVERT([nchar](19),getdate(),(20)))  
--from bd_psndoc psn inner join hi_psnorg org on psn.pk_psndoc = org.pk_psndoc and org.lastflag = 'Y'
