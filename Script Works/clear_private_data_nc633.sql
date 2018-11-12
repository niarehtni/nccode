update org_orgs set code = replace(code, 'WNC', 'YTW');
update org_orgs set name=code, name2=code;
update org_orgs_v set code = replace(code, 'WNC', 'YTW');
update org_orgs_v set name=code, name2=code;

update org_hrorg set code = replace(code, 'WNC', 'YTW');
update org_hrorg set name=code, name2=code;
update ORG_HRORG_V set code = replace(code, 'WNC', 'YTW');
update ORG_HRORG_V set name=code, name2=code;

update org_adminorg set code = replace(code, 'WNC', 'YTW');
update org_adminorg set name=code, name2=code;
update ORG_ADMINORG_V set code = replace(code, 'WNC', 'YTW');
update ORG_ADMINORG_V set name=code, name2=code;

update ORG_CORP set code = replace(code, 'WNC', 'YTW');
update ORG_CORP set name=code, name2=code;
update ORG_CORP_V set code = replace(code, 'WNC', 'YTW');
update ORG_CORP_V set name=code, name2=code;

update org_dept set code = replace(code, 'WNC', 'YTW');
update org_dept set name=code, name2=code;
update org_dept_v set code = replace(code, 'WNC', 'YTW');
update org_dept_v set name=code, name2=code;

update bd_psndoc set id = substr(pk_psndoc, -10, 10);
update hi_psndoc_glbdef2 set glbdef3 = (select id from bd_psndoc where pk_psndoc = hi_psndoc_glbdef2.pk_psndoc) where glbdef2 = '本人';
update hi_psndoc_glbdef2 set glbdef3 = substr(pk_psndoc_sub, -10, 10) where glbdef2 <> '本人';
update hi_psndoc_glbdef17 set glbdef3 = (select id from bd_psndoc where pk_psndoc = hi_psndoc_glbdef17.pk_psndoc) where glbdef2 = '本人';
update hi_psndoc_glbdef17 set glbdef3 = substr(pk_psndoc_sub, -10, 10) where glbdef2 <> '本人';
update hi_psndoc_glbdef17 set glbdef14 = replace(replace(glbdef14, '南山', 'XX'), '中國', '某某');

