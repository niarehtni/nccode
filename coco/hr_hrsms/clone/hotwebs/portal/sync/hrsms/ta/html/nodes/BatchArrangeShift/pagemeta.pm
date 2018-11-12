<?xml version="1.0" encoding='UTF-8'?>
<PageMeta i18nName="a_pub-000169" langDir="node_pub-res" caption="批量排班详细界面" controllerClazz="nc.bs.hrsms.ta.sss.calendar.ctrl.BatchArrangeShiftWin"  id="BatchArrangeShift" sourcePackage="ta/src/public/" windowType="win">
    <Processor>nc.uap.lfw.core.event.AppRequestProcessor</Processor>
    <Widgets>
        <Widget canFreeDesign="true" id="main" refId="main">
        </Widget>
    </Widgets>
    <Attributes>
        <Attribute>
            <Key>$MODIFY_TS</Key>
            <Value>1355188453254</Value>
            <Desc>
            </Desc>
        </Attribute>
    </Attributes>
    <PlugoutDescs>
    </PlugoutDescs>
    <PluginDescs>
    </PluginDescs>
    <Events>
        <Event async="false" jsEventClaszz="nc.uap.lfw.core.event.conf.PageListener" methodName="sysWindowClosed" name="onClosed" onserver="true">
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
        <Event async="false" controllerClazz="nc.uap.lfw.core.app.LfwAppDefaultController" jsEventClaszz="nc.uap.lfw.core.event.conf.PageListener" methodName="onPageClosed" name="onClosed" onserver="true">
            <Action>
            </Action>
        </Event>
    </Events>
</PageMeta>
