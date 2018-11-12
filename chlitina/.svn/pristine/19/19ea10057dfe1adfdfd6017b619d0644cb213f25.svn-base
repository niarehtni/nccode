DELETE FROM bd_defdoc WHERE pk_defdoclist = (select pk_defdoclist from bd_defdoclist where code = 'TWHR000');
INSERT INTO bd_defdoc (code, creationtime, creator, dataoriginflag, datatype, dr, enablestate, innercode, memo, mnecode, modifiedtime, modifier, name, name2, name3, name4, name5, name6, pid, pk_defdoc, pk_defdoclist, pk_group, pk_org, shortname, shortname2, shortname3, shortname4, shortname5, shortname6) 
SELECT infoset_code, null, 'NC_USER0000000000000', 0, 1, 0, 2, null, null, null, null, null, infoset_name, null, null, null, null, null, '~', replace(pk_infoset, '00000', 'TWDEF'), 'TWHRA210000000DEFSET', (select pk_group from org_group where rownum=1), (select pk_group from org_group where rownum=1), null, null, null, null, null, null
FROM hr_infoset WHERE infoset_code like 'hi_psndoc_glbdef%' and dr=0;

INSERT INTO bd_defdoc (code, creationtime, creator, dataoriginflag, datatype, dr, enablestate, innercode, memo, mnecode, modifiedtime, modifier, name, name2, name3, name4, name5, name6, pid, pk_defdoc, pk_defdoclist, pk_group, pk_org, shortname, shortname2, shortname3, shortname4, shortname5, shortname6) 
SELECT replace(meta_data, 'hrhi.', ''), null, 'NC_USER0000000000000', 0, 1, 0, 2, null, null, null, null, null, item_name, null, null, null, null, null, '~', replace(pk_infoset_item, '000', 'TWD'), 'TWHRA210000000DEFSET', (select pk_group from org_group where rownum=1), (select pk_group from org_group where rownum=1), null, null, null, null, null, null
FROM hr_infoset_item WHERE pk_infoset_item = 'TWHRA01000INFOSET004';
