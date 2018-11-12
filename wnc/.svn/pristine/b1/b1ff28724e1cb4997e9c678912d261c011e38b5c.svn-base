--select * from wa_classitem;
--select * from v$version;
--select * from wa_data;

DECLARE
  num NUMBER;
BEGIN
  SELECT COUNT(1) INTO num from cols where table_name = upper('wa_classitem') and column_name = upper('trano');
  IF num <= 0 THEN
      execute immediate 'alter table wa_classitem add (trano varchar(50) NULL)';
  END IF;
	num:=0;

SELECT COUNT(1) INTO num from cols where table_name = upper('wa_classitem') and column_name = upper('debitentry');
  IF num <= 0 THEN
      execute immediate 'alter table wa_classitem add (debitentry varchar(20) NULL)';
  END IF;
	num:=0;

SELECT COUNT(1) INTO num from cols where table_name = upper('wa_classitem') and column_name = upper('debitentry_b');
  IF num <= 0 THEN
      execute immediate 'alter table wa_classitem add (debitentry_b varchar(20) NULL)';
  END IF;
	num:=0;

SELECT COUNT(1) INTO num from cols where table_name = upper('wa_classitem') and column_name = upper('creditsubject');
  IF num <= 0 THEN
      execute immediate 'alter table wa_classitem add (creditsubject varchar(20)  NULL)';
  END IF;
	num:=0;

SELECT COUNT(1) INTO num from cols where table_name = upper('wa_classitem') and column_name = upper('credisubject_b');
  IF num <= 0 THEN
      execute immediate 'alter table wa_classitem add (credisubject_b varchar(20) NULL)';
  END IF;
	num:=0;
END;

