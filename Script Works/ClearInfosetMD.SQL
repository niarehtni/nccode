delete from md_class where id =  'b5d2e4ce-57cc-42ea-b681-92362480aa4f';
delete from md_property where classid = 'b5d2e4ce-57cc-42ea-b681-92362480aa4f';
delete from hr_infoset_item where pk_infoset not in (select pk_infoset from hr_infoset);