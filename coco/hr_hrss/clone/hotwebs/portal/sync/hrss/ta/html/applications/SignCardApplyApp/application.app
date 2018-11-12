<?xml version="1.0" encoding='UTF-8'?>
<Application TagName="Application" caption="SignCardApplyApp" controllerClazz="nc.bs.hrss.ta.signcard.ctrl.SignCardApplyApp" defaultWindowId="SignCardList" id="SignCardApplyApp" sourcePackage="src/public/">
    <PageMetas>
        <PageMeta isCanFreeDesign="true" caption="SignCardApply" id="SignCardApply">
        </PageMeta>
        <PageMeta isCanFreeDesign="true" caption="SignCardList" id="SignCardList">
        </PageMeta>
        <PageMeta isCanFreeDesign="false" i18nName="a_ta-000011" langDir="node_ta-res" caption="联查审批单据" id="pfinfo">
        </PageMeta>
        <PageMeta isCanFreeDesign="false" i18nName="a_ta-000012" langDir="node_ta-res" caption="文档管理" id="HrssFilemanager">
        </PageMeta>
    </PageMetas>
    <Connectors>
        <Connector pluginId="ReSearch" plugoutId="closewindow" source="main" sourceWindow="SignCardApply" target="main" targetWindow="SignCardList">
            <Maps>
                <Map>
                    <outValue>c_1</outValue>
                    <inValue>closewindow</inValue>
                </Map>
            </Maps>
        </Connector>
    </Connectors>
</Application>