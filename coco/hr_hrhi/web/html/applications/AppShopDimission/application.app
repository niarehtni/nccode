<?xml version="1.0" encoding='UTF-8'?>
<Application TagName="Application" caption="店员离职" i18nName="w_ta-001289" langDir="node_ta-res" controllerClazz="nc.bs.hrsms.hi.employ.AppShopDimissionAppController" defaultWindowId="ShopDimission" id="AppShopDimission" sourcePackage="hi/src/public/">
    <PageMetas>
        <PageMeta caption="店员离职" id="ShopDimission" isCanFreeDesign="true">
        </PageMeta>
        <PageMeta caption="店员离职卡片窗口" id="ShopDimissionCard" isCanFreeDesign="true">
        </PageMeta>
    </PageMetas>
    <Connectors>
        <Connector pluginId="ReSearch" plugoutId="closewindow" source="main" sourceWindow="ShopDimissionCard" target="main" targetWindow="ShopDimission">
            <Maps>
                <Map>
                    <outValue>c_1</outValue>
                    <inValue>closewindow</inValue>
                </Map>
            </Maps>
        </Connector>
    </Connectors>  
</Application>