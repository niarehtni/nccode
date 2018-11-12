update pub_billtemplet_b set reftype = '健保身份(自定義檔案),code=N' where table_code = (select infoset_code from hr_infoset where infoset_name = '健保信息') and metadatapath='glbdef5';
update pub_billtemplet_b set reftype = '勞保身份(自定義檔案),code=N' where table_code = (select infoset_code from hr_infoset where infoset_name = '勞保勞退信息') and metadatapath='glbdef1';
update pub_billtemplet_b set reftype = '勞保身份(自定義檔案),code=N' where table_code = (select infoset_code from hr_infoset where infoset_name = '勞保投保明細') and metadatapath='glbdef2';
