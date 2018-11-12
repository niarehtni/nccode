<?xml version="1.0" encoding='UTF-8'?>
<Application TagName="Application" caption="店员出差申请" controllerClazz="nc.bs.hrsms.ta.sss.away.ctrl.ShopAwayRegApp" defaultWindowId="ShopAwayRegList" id="ShopAwayRegApp" sourcePackage="src/public/">
    <PageMetas>
       <PageMeta isCanFreeDesign="true" i18nName="" langDir="" caption="出差登记列表界面" id="ShopAwayRegList">
        </PageMeta>
       <PageMeta isCanFreeDesign="true" i18nName="" langDir="" caption="出差登记详细界面" id="ShopAwayRegCard">
        </PageMeta>
       <PageMeta isCanFreeDesign="false" i18nName="" langDir="" caption="批量出差登记界面" id="ShopAwayRegBatchCard">
        </PageMeta>
        <PageMeta isCanFreeDesign="false" i18nName="" langDir="" caption="单据冲突" id="BillMutexInfo">
        </PageMeta>
    </PageMetas>
    <Connectors>
        <Connector pluginId="ReSearch" plugoutId="closewindow" source="main" sourceWindow="ShopAwayRegCard" target="main" targetWindow="ShopAwayRegList">
            <Maps>
                <Map>
                    <outValue>c_1</outValue>
                    <inValue>closewindow</inValue>
                </Map>
            </Maps>
        </Connector>
        <Connector pluginId="Batch_ReSearch" plugoutId="closewindow" source="main" sourceWindow="ShopAwayRegBatchCard" target="main" targetWindow="ShopAwayRegList">
            <Maps>
                <Map>
                    <outValue>c_1</outValue>
                    <inValue>closewindow</inValue>
                </Map>
            </Maps>
        </Connector>
    </Connectors>
</Application>