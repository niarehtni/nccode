--导入时,需要先删除重复数据
--delete from wa_data where PK_WA_CLASS = (select PK_WA_CLASS from WA_WACLASS where code = 'TWMONTH' and dr = 0 and PK_ORG = (select PK_ORG from org_orgs where code = 'WNC' and dr= 0)) and pk_org = (select PK_ORG from org_orgs where code = 'WNC' and dr= 0)  and cyear = '2018' and CPERIOD = '07'
delete FROM wa_imp_fieldmapping WHERE imptype = 0;
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+1, 0,5,'f_338', '2026', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+2, 0,6,'f_342', '2030', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+3, 0,7,'f_340', '2028', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+4, 0,8,'f_339', '2027', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+5, 0,9,'f_419', '2031', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+6, 0,10,'f_341', '2029', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+7, 0,11,'f_411', '4147', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+8, 0,12,'f_358', '4145', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+9, 0,13,'f_64', '4009', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+10, 0,14,'f_65', '4010', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+11, 0,15,'f_190', '7205', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+12, 0,16,'f_192', '7207', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+13, 0,17,'f_193', '7208', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+14, 0,18,'f_191', '7206', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+15, 0,19,'f_4', 'f_4', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+16, 0,20,'f_277', '0009', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+17, 0,21,'f_3', 'f_3', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+18, 0,22,'f_280', '4133', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+19, 0,23,'f_46', '3101', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+20, 0,24,'f_48', '3103', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+21, 0,25,'f_47', '3102', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+22, 0,26,'f_281', '3111', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+23, 0,27,'f_50', '3105', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+24, 0,28,'f_45', '2016', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+25, 0,29,'f_44', '2015', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+26, 0,30,'f_49', '3104', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+27, 0,31,'f_282', '3112', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+28, 0,32,'f_283', '3113', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+29, 0,33,'f_57', '4002', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+30, 0,34,'f_58', '4003', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+31, 0,35,'f_59', '4004', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+32, 0,36,'f_456', '4186', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+33, 0,37,'f_68', '4101', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+34, 0,38,'f_307', '4142', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+35, 0,39,'f_69', '4102', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+36, 0,40,'f_303', '4138', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+37, 0,41,'f_285', '4017', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+38, 0,42,'f_286', '4018', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+39, 0,43,'f_71', '4104', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+40, 0,44,'f_289', '4020', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+41, 0,45,'f_32', '1006', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+42, 0,46,'f_27', '1001', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+43, 0,47,'f_33', '1007', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+44, 0,48,'f_28', '1002', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+45, 0,49,'f_30', '1004', 0, '2020-01-15 17:40:01');
INSERT INTO WA_IMP_FIELDMAPPING (ID, IMPTYPE, COLINDEX, ITEMKEY, CODE, DR, TS) VALUES ((select max(id) FROM wa_imp_fieldmapping)+46, 0,50,'f_29', '1003', 0, '2020-01-15 17:40:01');
