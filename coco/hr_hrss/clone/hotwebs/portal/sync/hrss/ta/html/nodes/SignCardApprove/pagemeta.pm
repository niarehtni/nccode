<?xml version="1.0" encoding='UTF-8'?>
<PageMeta  i18nName="a_pub-000186" langDir="node_pub-res" caption="签卡审批详细界面" controllerClazz="nc.bs.hrss.pub.pf.ctrl.PFApproveWin" id="SignCardApprove" sourcePackage="src/public/" windowType="win">
    <Processor>nc.uap.lfw.core.event.AppRequestProcessor</Processor>
    <Widgets>
        <Widget canFreeDesign="true" id="main" refId="main">
        </Widget>
        <Widget canFreeDesign="false" id="pubview_exetask" refId="../pubview_exetask">
        </Widget>
    </Widgets>
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
        <Connector id="connExetask" pluginId="plugin_exetask" plugoutId="plugout_exetask" source="pubview_exetask" target="main">
        	<Maps>
            </Maps>
        </Connector>
    </Connectors>
</PageMeta>
