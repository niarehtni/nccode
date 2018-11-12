INSERT INTO sm_paramregister
        ( dr ,
          paramname ,
          paramvalue ,
          parentid ,
          pk_param 
        )
VALUES  ( 0 , 
          N'PluginBeanConfigFilePath' ,
          N'/nc/ui/hi/employee/uif2_ext_config.xml' , 
          (SELECT cfunid FROM sm_funcregister WHERE funcode = '60070employee' AND own_module = '6007'), 
          N'1001JF100000PLUGIN01' 
        );