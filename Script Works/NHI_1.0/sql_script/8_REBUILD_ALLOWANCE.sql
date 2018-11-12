DELETE FROM twhr_allowance;

DECLARE @pkgroup NCHAR(20);
DECLARE @count INT;
DECLARE @CurrentDate NCHAR(19);

--INIT group
SELECT @pkgroup=pk_group FROM dbo.org_group WHERE dr=0;

--INIT REFs
SET @count = 1
SELECT @CurrentDate = FORMAT(GETDATE(), 'yyyy-MM-dd HH:mm:ss')
				   
			   
INSERT INTO twhr_allowance (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, id, code, allowancetype, allowanceamount, startdate, enddate, creator, creationtime, modifier, modifiedtime, pk_org, pk_org_v, pk_group, ts, dr) VALUES (null, '年滿70歲以上中低收入老人', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, N'0000TW1100'+FORMAT(@count, '0000')+'ORGREF', 'HA01', 2, 100.00000000, @CurrentDate, null, '~', null, '~', null, 'GLOBLE00000000000000', '~', @pkgroup, @CurrentDate, 0);
SET @count = @count + 1
INSERT INTO twhr_allowance (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, id, code, allowancetype, allowanceamount, startdate, enddate, creator, creationtime, modifier, modifiedtime, pk_org, pk_org_v, pk_group, ts, dr) VALUES (null, '極重度、重度身心障礙', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, N'0000TW1100'+FORMAT(@count, '0000')+'ORGREF', 'HA02', 2, 100.00000000, @CurrentDate, null, '~', null, '~', null, 'GLOBLE00000000000000', '~', @pkgroup, @CurrentDate, 0);
SET @count = @count + 1
INSERT INTO twhr_allowance (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, id, code, allowancetype, allowanceamount, startdate, enddate, creator, creationtime, modifier, modifiedtime, pk_org, pk_org_v, pk_group, ts, dr) VALUES (null, '中度身心障礙', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, N'0000TW1100'+FORMAT(@count, '0000')+'ORGREF', 'HA03', 2, 50.00000000, @CurrentDate, null, '~', null, '~', null, 'GLOBLE00000000000000', '~', @pkgroup, @CurrentDate, 0);
SET @count = @count + 1
INSERT INTO twhr_allowance (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, id, code, allowancetype, allowanceamount, startdate, enddate, creator, creationtime, modifier, modifiedtime, pk_org, pk_org_v, pk_group, ts, dr) VALUES (null, '輕度身心障礙', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, N'0000TW1100'+FORMAT(@count, '0000')+'ORGREF', 'HA04', 2, 25.00000000, @CurrentDate, null, '~', null, '~', null, 'GLOBLE00000000000000', '~', @pkgroup, @CurrentDate, 0);
SET @count = @count + 1
INSERT INTO twhr_allowance (def1, name, name2, name3, name4, name5, name6, def2, def3, def4, def5, def6, def7, def8, def9, def10, def11, def12, def13, def14, def15, def16, def17, def18, def19, def20, id, code, allowancetype, allowanceamount, startdate, enddate, creator, creationtime, modifier, modifiedtime, pk_org, pk_org_v, pk_group, ts, dr) VALUES (null, '中低收入戶減免50%', '中低收入戶減免50%', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, N'0000TW1100'+FORMAT(@count, '0000')+'ORGREF', 'HA05', 2, 50.00000000, @CurrentDate, null, '~', null, '~', null, 'GLOBLE00000000000000', '~', @pkgroup, @CurrentDate, 0);


