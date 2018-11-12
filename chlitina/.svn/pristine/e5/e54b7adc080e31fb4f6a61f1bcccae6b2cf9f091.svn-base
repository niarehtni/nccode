

--HR_INFOSET_ITEM表的删除语句
--delete  HR_INFOSET_ITEM WHERE PK_INFOSET_ITEM ='1001ZZ1000000000L9FT';
--HR_INFOSET_ITEMnull



Insert into HR_INFOSET_ITEM
(accessor_classname,calculation,class_id,creationtime,creator,custom_attr,data_type,data_type_id,data_type_style,default_value,description,dr,dynamic_flag,enum_id,fixed_length,help,hided,id,is_active,is_authen,item_code,item_formula,item_formula_sql,item_name,item_name2,item_name3,item_name4,item_name5,item_name6,max_length,max_value,meta_data,min_value,modifiedtime,modifier,not_serialize,nullable,pk_group,pk_infoset,pk_infoset_item,pk_main_item,pk_org,precise,read_only,ref_leaf_flag,ref_model_name,resid,respath,showorder,sub_formula,sub_formula_sql,ts,unique_flag,visibility)
Values(null,null,null,'2018-09-21 17:48:19','NC_USER0000000000000','Y',4,null,null,null,null,0,null,null,null,null,'N',null,null,null,'ismonsalary',null,null,'是否月薪',null,null,null,null,null,1,null,null,null,null,null,null,'Y','~','GLOBLE00000000000000','1001ZZ1000000000L9FT','~','GLOBLE00000000000000',null,'N','N',null,'hi_psnjob_99149','6007psn',56,null,null,'2018-09-21 17:48:21','N',null);

--修正劳退开始结束日期类型
UPDATE HR_INFOSET_ITEM SET DATA_TYPE = 20, MAX_LENGTH = 10 WHERE PK_INFOSET = '1001ZZ10000000001PQV' AND ITEM_CODE = 'glbdef14';
UPDATE HR_INFOSET_ITEM SET DATA_TYPE = 20, MAX_LENGTH = 10 WHERE PK_INFOSET = '1001ZZ10000000001PQV' AND ITEM_CODE = 'glbdef15';
UPDATE MD_PROPERTY SET ATTRLENGTH = 10, DATATYPE = 'BS000010000100001039' where classid = (select id from md_class where defaulttablename = 'hi_psndoc_glbdef1') and name in ('glbdef14', 'glbdef15');

--家庭信息增加字段
INSERT INTO hr_infoset_item (accessor_classname, calculation, class_id, creationtime, creator, custom_attr, data_type, data_type_id, data_type_style, default_value, description, dr, dynamic_flag, enum_id, fixed_length, help, hided, id, is_active, is_authen, item_code, item_formula, item_formula_sql, item_name, item_name2, item_name3, item_name4, item_name5, item_name6, max_length, max_value, meta_data, min_value, modifiedtime, modifier, not_serialize, nullable, pk_group, pk_infoset, pk_infoset_item, pk_main_item, pk_org, precise, read_only, ref_leaf_flag, ref_model_name, resid, respath, showorder, sub_formula, sub_formula_sql, ts, unique_flag, visibility) VALUES (null, null, null, '2017-07-18 17:42:32', null, 'Y', 4, null, null, null, null, 0, null, null, null, null, 'N', null, null, null, 'isnhifeed', null, null, '是否所得税抚养人', '是否所得稅撫養人', null, null, null, null, 1, null, null, null, '2018-04-12 09:15:59', null, null, 'Y', (select pk_group from org_group where rownum=1), '1002Z710000000006ZLI', '1001ZZ10000000000NX7', '~', 'GLOBLE00000000000000', null, 'N', 'N', null, 'hi_psndoc_family_51991', '6007psn', 28, null, null, '2018-04-12 09:15:59', 'N', null);
INSERT INTO hr_infoset_item (accessor_classname, calculation, class_id, creationtime, creator, custom_attr, data_type, data_type_id, data_type_style, default_value, description, dr, dynamic_flag, enum_id, fixed_length, help, hided, id, is_active, is_authen, item_code, item_formula, item_formula_sql, item_name, item_name2, item_name3, item_name4, item_name5, item_name6, max_length, max_value, meta_data, min_value, modifiedtime, modifier, not_serialize, nullable, pk_group, pk_infoset, pk_infoset_item, pk_main_item, pk_org, precise, read_only, ref_leaf_flag, ref_model_name, resid, respath, showorder, sub_formula, sub_formula_sql, ts, unique_flag, visibility) VALUES (null, null, null, '2017-09-13 15:40:32', null, 'Y', 0, null, null, null, null, 0, null, null, null, null, 'N', null, null, null, 'idnumber', null, null, '身份证号码', '身份證號碼', null, null, null, null, 128, null, null, null, '2018-04-12 09:15:59', null, null, 'Y', (select pk_group from org_group where rownum=1), '1002Z710000000006ZLI', '1001A110000000001NJY', '~', 'GLOBLE00000000000000', null, 'N', 'N', null, 'hi_psndoc_family_32110', '6007psn', 29, null, null, '2018-04-12 09:15:59', 'N', null);
