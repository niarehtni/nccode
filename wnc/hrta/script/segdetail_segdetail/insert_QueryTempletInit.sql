INSERT INTO pub_query_templet (ts, ID, MODEL_CODE, MODEL_NAME, NODE_CODE, PK_CORP, METACLASS, LAYER ) VALUES ('2018-04-23 00:28:27', '0001ZZ10000000015XAV', '60170segdetail', '加班分段明细', '60170segdetail', '@@@@', '59c59639-7fd1-44ad-aacd-8e4bf9fef6e3', 0 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2018-04-23 00:28:27', '-99', 0, 0, 1, 'pk_segdetail', '0001ZZ10000000015XAW', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000015XAV', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2018-04-23 00:28:27', '-99', 0, 1, 1, 'pk_segdetailconsume', '0001ZZ10000000015XAX', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000015XAV', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2018-04-23 00:28:27', '集团', 5, 2, 1, 'pk_group', '0001ZZ10000000015XAY', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=', '等于@', 0, '@@@@', '0001ZZ10000000015XAV', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, VALUE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2018-04-23 00:28:27', '组织（包含全局）(所有)', 5, 3, 1, 'pk_org', '0001ZZ10000000015XAZ', 'Y', 'N', 'Y', 'N', 'N', 'Y', 'N', 'N', 'Y', 'N', 'N', '=@', '等于@', 0, '@@@@', '0001ZZ10000000015XAV', 2, '#mainorg#', 'Y', 'Y', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2018-04-23 00:28:27', '业务单元版本', 5, 4, 1, 'pk_org_v', '0001ZZ10000000015XB0', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=', '等于@', 0, '@@@@', '0001ZZ10000000015XAV', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2018-04-23 00:28:27', '用户', 5, 5, 1, 'creator', '0001ZZ10000000015XB1', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=', '等于@', 0, '@@@@', '0001ZZ10000000015XAV', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2018-04-23 00:28:27', '-99', 8, 6, 1, 'creationtime', '0001ZZ10000000015XB2', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', 'between@=@>@>=@<@<=@', '介于@等于@大于@大于等于@小于@小于等于@', 0, '@@@@', '0001ZZ10000000015XAV', 2, 'Y', 'Y', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2018-04-23 00:28:27', '用户', 5, 7, 1, 'modifier', '0001ZZ10000000015XB3', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=', '等于@', 0, '@@@@', '0001ZZ10000000015XAV', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2018-04-23 00:28:27', '-99', 8, 8, 1, 'modifiedtime', '0001ZZ10000000015XB4', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', 'between@=@>@>=@<@<=@', '介于@等于@大于@大于等于@小于@小于等于@', 0, '@@@@', '0001ZZ10000000015XAV', 2, 'Y', 'Y', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, VALUE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2018-04-23 00:28:27', '-99', 3, 9, 0, 'maketime', '0001ZZ10000000015XB5', 'Y', 'N', 'Y', 'N', 'N', 'Y', 'N', 'N', 'Y', 'N', 'N', 'between@=@>@>=@<@<=@', '介于@等于@大于@大于等于@小于@小于等于@', 0, '@@@@', '0001ZZ10000000015XAV', 2, '#day(0)#,#day(0)#', 'Y', 'Y', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2018-04-23 00:28:27', '-99', 5, 10, 1, 'pk_parentsegdetail', '0001ZZ10000000015XB6', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=', '等于@', 0, '@@@@', '0001ZZ10000000015XAV', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2018-04-23 00:28:27', '-99', 1, 11, 1, 'nodeno', '0001ZZ10000000015XB7', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', 'between@=@>@>=@<@<=@', '介于@等于@大于@大于等于@小于@小于等于@', 0, '@@@@', '0001ZZ10000000015XAV', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2018-04-23 00:28:27', '-99', 0, 12, 1, 'nodecode', '0001ZZ10000000015XB8', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000015XAV', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2018-04-23 00:28:27', '-99', 0, 13, 1, 'nodename', '0001ZZ10000000015XB9', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@like@left like@right like@', '等于@包含@左包含@右包含@', 0, '@@@@', '0001ZZ10000000015XAV', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2018-04-23 00:28:27', '-99', 10, 14, 1, 'regdate', '0001ZZ10000000015XBA', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', 'between@=@>@>=@<@<=@', '介于@等于@大于@大于等于@小于@小于等于@', 0, '@@@@', '0001ZZ10000000015XAV', 2, 'Y', 'Y', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2018-04-23 00:28:27', '-99', 5, 15, 1, 'pk_segrule', '0001ZZ10000000015XBB', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=', '等于@', 0, '@@@@', '0001ZZ10000000015XAV', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2018-04-23 00:28:27', '-99', 5, 16, 1, 'pk_segruleterm', '0001ZZ10000000015XBC', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=', '等于@', 0, '@@@@', '0001ZZ10000000015XAV', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2018-04-23 00:28:27', 'HR人员', 5, 17, 1, 'pk_psndoc', '0001ZZ10000000015XBD', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=', '等于@', 0, '@@@@', '0001ZZ10000000015XAV', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2018-04-23 00:28:27', '-99', 5, 18, 1, 'pk_overtimereg', '0001ZZ10000000015XBE', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=', '等于@', 0, '@@@@', '0001ZZ10000000015XAV', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2018-04-23 00:28:27', '8', 2, 19, 1, 'rulehours', '0001ZZ10000000015XBF', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', 'between@=@>@>=@<@<=@', '介于@等于@大于@大于等于@小于@小于等于@', 0, '@@@@', '0001ZZ10000000015XAV', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2018-04-23 00:28:27', '8', 2, 20, 1, 'hours', '0001ZZ10000000015XBG', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', 'between@=@>@>=@<@<=@', '介于@等于@大于@大于等于@小于@小于等于@', 0, '@@@@', '0001ZZ10000000015XAV', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2018-04-23 00:28:27', '8', 2, 21, 1, 'hourlypay', '0001ZZ10000000015XBH', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', 'between@=@>@>=@<@<=@', '介于@等于@大于@大于等于@小于@小于等于@', 0, '@@@@', '0001ZZ10000000015XAV', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2018-04-23 00:28:27', '8', 2, 22, 1, 'otrate', '0001ZZ10000000015XBI', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', 'between@=@>@>=@<@<=@', '介于@等于@大于@大于等于@小于@小于等于@', 0, '@@@@', '0001ZZ10000000015XAV', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2018-04-23 00:28:27', '8', 2, 23, 1, 'consumedhours', '0001ZZ10000000015XBJ', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', 'between@=@>@>=@<@<=@', '介于@等于@大于@大于等于@小于@小于等于@', 0, '@@@@', '0001ZZ10000000015XAV', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2018-04-23 00:28:27', '8', 2, 24, 1, 'consumedamount', '0001ZZ10000000015XBK', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', 'between@=@>@>=@<@<=@', '介于@等于@大于@大于等于@小于@小于等于@', 0, '@@@@', '0001ZZ10000000015XAV', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2018-04-23 00:28:27', '8', 2, 25, 1, 'remaininghours', '0001ZZ10000000015XBL', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', 'between@=@>@>=@<@<=@', '介于@等于@大于@大于等于@小于@小于等于@', 0, '@@@@', '0001ZZ10000000015XAV', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2018-04-23 00:28:27', '8', 2, 26, 1, 'remainingamount', '0001ZZ10000000015XBM', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', 'between@=@>@>=@<@<=@', '介于@等于@大于@大于等于@小于@小于等于@', 0, '@@@@', '0001ZZ10000000015XAV', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2018-04-23 00:28:27', '-99', 4, 27, 1, 'iscanceled', '0001ZZ10000000015XBN', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@', '等于@', 0, '@@@@', '0001ZZ10000000015XAV', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2018-04-23 00:28:27', '-99', 4, 28, 1, 'iscompensation', '0001ZZ10000000015XBO', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@', '等于@', 0, '@@@@', '0001ZZ10000000015XAV', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2018-04-23 00:28:27', '-99', 4, 29, 1, 'isconsumed', '0001ZZ10000000015XBP', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@', '等于@', 0, '@@@@', '0001ZZ10000000015XAV', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_query_condition (ts, CONSULT_CODE, DATA_TYPE, DISP_SEQUENCE, DISP_TYPE, FIELD_CODE, ID, IF_AUTOCHECK, IF_DATAPOWER, IF_DEFAULT, IF_GROUP, IF_IMMOBILITY, IF_MUST, IF_ORDER, IF_SUM, IF_USED, IF_SUBINCLUDED, IF_MULTICORPREF, OPERA_CODE, OPERA_NAME, ORDER_SEQUENCE, PK_CORP, PK_TEMPLET, RETURN_TYPE, ISCONDITION, IF_SYSFUNCREFUSED, IF_ATTRREFUSED, LIMITS ) VALUES ('2018-04-23 00:28:27', '-99', 4, 30, 1, 'issettled', '0001ZZ10000000015XBQ', 'Y', 'N', 'N', 'N', 'N', 'N', 'N', 'N', 'Y', 'N', 'N', '=@', '等于@', 0, '@@@@', '0001ZZ10000000015XAV', 2, 'Y', 'N', 'N', 9999 );
INSERT INTO pub_systemplate_base (ts, nodekey, funnode, layer, moduleid, templateid, pk_systemplate, devorg, pk_country, pk_industry, tempstyle, dr ) VALUES ('2018-04-23 00:28:27', 'qt', '60170segdetail', 0, '6017', '0001ZZ10000000015XAV', '0001ZZ10000000015XBR', '00001', '~', '~', 1, 0 );