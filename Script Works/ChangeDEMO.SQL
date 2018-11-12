--6886 -> 68J6
UPDATE  dbo.dap_dapsystem
SET     moduleid = CASE WHEN moduleid IS NULL THEN NULL ELSE REPLACE(moduleid, '6886', '68J6') END
WHERE   moduleid = '6886';

UPDATE  dbo.sm_funcregister
SET     own_module = CASE WHEN own_module IS NULL THEN NULL ELSE REPLACE(own_module, '6886', '68J6') END ,
        funcode = CASE WHEN funcode IS NULL THEN NULL ELSE REPLACE(funcode, '6886', '68J6') END
WHERE   own_module = '6886';

UPDATE  dbo.pub_systemplate
SET     moduleid = CASE WHEN moduleid IS NULL THEN NULL ELSE REPLACE(moduleid, '6886', '68J6') END ,
        funnode = CASE WHEN funnode IS NULL THEN NULL ELSE REPLACE(funnode, '6886', '68J6') END
WHERE   moduleid = '6886';

UPDATE  dbo.pub_query_templet
SET     model_code = CASE WHEN model_code IS NULL THEN NULL ELSE REPLACE(model_code, '6886', '68J6') END ,
        node_code = CASE WHEN node_code IS NULL THEN NULL ELSE REPLACE(node_code, '6886', '68J6') END
WHERE   model_code LIKE '6886%';

UPDATE  dbo.pub_billtemplet
SET     nodecode = CASE WHEN nodecode IS NULL THEN NULL ELSE REPLACE(nodecode, '6886', '68J6') END ,
        pk_billtypecode = CASE WHEN pk_billtypecode IS NULL THEN NULL ELSE REPLACE(pk_billtypecode, '6886', '68J6') END
WHERE   nodecode LIKE '6886%';

UPDATE  dbo.pub_print_template
SET     vnodecode = CASE WHEN vnodecode IS NULL THEN NULL ELSE REPLACE(vnodecode, '6886', '68J6') END ,
        vtemplatecode = CASE WHEN vtemplatecode IS NULL THEN NULL ELSE REPLACE(vtemplatecode, '6886', '68J6') END
WHERE   vnodecode LIKE '6886%';

UPDATE  dbo.sm_resp_func
SET     busi_pk = CASE WHEN busi_pk IS NULL THEN NULL ELSE REPLACE(busi_pk, '6886', '68J6') END
WHERE   busi_pk LIKE '6886%';

UPDATE  dbo.pub_sysinittemp
SET     domainflag = CASE WHEN domainflag IS NULL THEN NULL ELSE REPLACE(domainflag, '6886', '68J6') END
WHERE   domainflag = '6886';

UPDATE  sm_menuitemreg
SET     funcode = CASE WHEN funcode IS NULL THEN NULL ELSE REPLACE(funcode, '6886', '68J6') END
WHERE   funcode LIKE '6886%';

UPDATE  dbo.aam_appasset
SET     pk_module = CASE WHEN pk_module IS NULL THEN NULL ELSE REPLACE(pk_module, '6886', '68J6') END
WHERE   pk_module = '6886';

UPDATE  dbo.sm_createcorp
SET     funccode = CASE WHEN funccode IS NULL THEN NULL ELSE REPLACE(funccode, '6886', '68J6') END
WHERE   funccode = '6886';

UPDATE  dbo.sm_product_version
SET     productcode = CASE WHEN productcode IS NULL THEN NULL ELSE REPLACE(productcode, '6886', '68J6') END
WHERE   productcode LIKE '6886%';
