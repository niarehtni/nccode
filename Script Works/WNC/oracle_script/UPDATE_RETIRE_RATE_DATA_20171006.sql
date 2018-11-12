update hr_infoset_item 
set item_name = '劳退自提比例(%)', item_name2='勞退自提比例(%)'
where pk_infoset=(select pk_infoset from hr_infoset where infoset_name = '勞保勞退信息') and item_code = 'glbdef8';

update md_property set displayname='劳退自提比例(%)'  where id = (
select id from hr_infoset_item where pk_infoset=(select pk_infoset from hr_infoset where infoset_name = '勞保勞退信息') and item_code = 'glbdef8');

update twhr_basedoc set name='員工最高勞退自提比例', numbervalue=6 where code = 'TWLP0017' ;

update pub_billtemplet_b set defaultshowname = '勞退自提比例(%)' where defaultshowname = '勞退自提金額' and itemkey = 'glbdef8'; 
update pub_billtemplet_b set defaultshowname = '劳退自提比例(%)' where defaultshowname = '劳退自提金额' and itemkey = 'glbdef8';

update hr_infoset_item set nullable='N' where pk_infoset in (select pk_infoset from hr_infoset where infoset_code like 'hi_psndoc_glbdef%') and item_code = 'pk_psndoc_sub';