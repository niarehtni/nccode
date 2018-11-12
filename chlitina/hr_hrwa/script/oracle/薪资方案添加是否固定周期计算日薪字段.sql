--添加是否固定周期计算日薪字段
ALTER TABLE wa_waclass  ADD  isdaysalary char(1) NULL;
alter table wa_waclass add isferry char(1) null;