update wa_classitem set vformula=substr(vformula,1,length(vformula)-1) +',null)' where vformula like 'psnsubinf%';
