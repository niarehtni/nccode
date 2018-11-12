INSERT INTO dbo.sm_paramregister
        ( dr ,
          paramname ,
          paramvalue ,
          parentid ,
          pk_param 
        )
VALUES  ( 0 , -- dr - smallint
          N'PluginBeanConfigFilePath' , -- paramname - nvarchar(150)
          N'/nc/ui/hi/employee/uif2_ext_config.xml' , -- paramvalue - nvarchar(150)
          (SELECT cfunid FROM dbo.sm_funcregister WHERE funcode = '60070employee' AND own_module = '6007'), -- parentid - nvarchar(20)
          N'1001JF100000PLUGIN01'  -- pk_param - nchar(20)
        )