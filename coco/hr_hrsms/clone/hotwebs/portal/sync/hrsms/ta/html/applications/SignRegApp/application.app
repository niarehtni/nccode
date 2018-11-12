<?xml version="1.0" encoding='UTF-8'?>
<Application TagName="Application" caption="店员签卡登记" controllerClazz="nc.bs.hrsms.ta.SignReg.ctrl.SignRegAppController" defaultWindowId="SignRegList" id="SignRegApp" sourcePackage="ta/src/public/">
    <PageMetas>
        <PageMeta caption="店员签卡登记列表" id="SignRegList" isCanFreeDesign="false">
        </PageMeta>
        <PageMeta caption="新增签卡登记" id="SignRegCard" isCanFreeDesign="false">
        </PageMeta>
        <PageMeta caption="批量新增签卡登记" id="BatchSignReg" isCanFreeDesign="false">
        </PageMeta>
    </PageMetas>
    <Connectors>
        <Connector pluginId="ReSearch" plugoutId="closewindow" source="main" sourceWindow="SignRegCard" target="main" targetWindow="SignRegList">
            <Maps>
                <Map>
                    <outValue>c_1</outValue>
                    <inValue>closewindow</inValue>
                </Map>
            </Maps>
        </Connector>
        <Connector pluginId="ReSearch" plugoutId="closewindow" source="main" sourceWindow="BatchSignReg" target="main" targetWindow="SignRegList">
            <Maps>
                <Map>
                    <outValue>c_1</outValue>
                    <inValue>closewindow</inValue>
                </Map>
            </Maps>
        </Connector>
        <Connector pluginId="inid_soci" plugoutId="scty_outid" source="main" sourceWindow="BatchSignReg" target="main" targetWindow="SignRegList">
        </Connector>
      
    </Connectors>
</Application>