<?xml version="1.0" encoding='UTF-8'?>
<Application TagName="Application" caption="店员调动" i18nName="w_ta-001289" langDir="node_ta-res" controllerClazz="nc.bs.hrsms.hi.employ.AppShopTransferAppController" defaultWindowId="ShopTransfer" id="AppShopTransfer" sourcePackage="hi/src/public/">
    <PageMetas>
        <PageMeta caption="店员调动" id="ShopTransfer" isCanFreeDesign="true">
        </PageMeta>
        <PageMeta caption="店员调动卡片窗口" id="ShopTransferCard" isCanFreeDesign="true">
        </PageMeta>
    </PageMetas>
    <Connectors>
        <Connector pluginId="ReSearch" plugoutId="closewindow" source="main" sourceWindow="ShopTransferCard" target="main" targetWindow="ShopTransfer">
            <Maps>
                <Map>
                    <outValue>c_1</outValue>
                    <inValue>closewindow</inValue>
                </Map>
            </Maps>
        </Connector>
    </Connectors>
</Application>