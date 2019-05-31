-- 申报明细档新增列
ALTER TABLE hrwa_incometax
	ADD (biztype varchar(20) DEFAULT '~');
ALTER TABLE hrwa_incometax
	ADD (feetype varchar(20) DEFAULT '~');
ALTER TABLE hrwa_incometax
	ADD (projectcode varchar(20) DEFAULT '~');