<?xml version="1.0" encoding='UTF-8'?>
<PageMeta i18nName="a_pub-000160" langDir="node_pub-res" caption="批量调班详细界面" controllerClazz="nc.bs.hrsms.ta.sss.calendar.ctrl.BatchChangeShift" id="BatchChangeShift" sourcePackage="src/public/" windowType="win">
    <Processor>nc.uap.lfw.core.event.AppRequestProcessor</Processor>
    <Widgets>
        <Widget canFreeDesign="true" id="main" refId="main">
        </Widget>
    </Widgets>
    <Attributes>
        <Attribute>
            <Key>$QueryTemplate</Key>
            <Value>
            </Value>
            <Desc>
            </Desc>
        </Attribute>
    </Attributes>
    <Events>
        <Event async="true" jsEventClaszz="nc.uap.lfw.core.event.conf.PageListener" methodName="sysWindowClosed" name="onClosed" onserver="true">
            <SubmitRule cardSubmit="false" panelSubmit="false" tabSubmit="false">
            </SubmitRule>
            <Params>
                <Param>
                    <Name>event</Name>
                    <Value>
                    </Value>
                    <Desc>                        <![CDATA[nc.uap.lfw.core.event.PageEvent]]>
                    </Desc>
                </Param>
            </Params>
            <Action>
            </Action>
        </Event>
    </Events>
    <Connectors>
        <Connector id="632b0f97-a8ab-4cbf-9583-c359f261fbee" pluginId="psnList" plugoutId="plugOutPsnList" source="psnList" target="main">
        </Connector>
        <Connector id="f9507acd-8fc4-4ae8-9698-953a77c13f74" pluginId="deptList" plugoutId="plugoutDeptList" source="deptList" target="main">
        </Connector>
    </Connectors>
</PageMeta>
