<?xml version="1.0" encoding='UTF-8'?>
<PageMeta caption="店员离职卡片窗口" componentId="annoyuicomponnet" controllerClazz="nc.bs.hrsms.hi.employ.ShopDimission.ShopDimissionCardWin" id="ShopDimissionCard" i18nName="a_hi-0000024" langDir="node_hi-res" windowType="win" sourcePackage="hi/src/public/">
    <Processor>nc.uap.lfw.core.event.AppRequestProcessor</Processor>
    <Widgets>
        <Widget id="main" refId="main" canFreeDesign="true">
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
</PageMeta>