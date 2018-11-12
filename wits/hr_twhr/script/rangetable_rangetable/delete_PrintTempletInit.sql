DELETE FROM pub_systemplate_base where pk_systemplate = N'0001ZZ10000000001GYA';
delete from pub_print_datasource where ctemplateid = N'0001ZZ10000000001GRC';
delete from pub_print_cell where ctemplateid = N'0001ZZ10000000001GRC';
delete from pub_print_line where ctemplateid = N'0001ZZ10000000001GRC';
delete from pub_print_variable where ctemplateid = N'0001ZZ10000000001GRC';
delete from pub_print_template where ctemplateid = N'0001ZZ10000000001GRC';
DELETE FROM aam_appasset WHERE pk_asset = N'0001ZZ10000000001GY9';
