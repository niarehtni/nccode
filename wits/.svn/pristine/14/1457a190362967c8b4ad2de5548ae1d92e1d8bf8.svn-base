package nc.bs.twhr.rangetable.plugin.bpplugin;

import nc.impl.pubapp.pattern.rule.plugin.IPluginPoint;

/**
 * 标准单据的扩展插入点
 * 
 */
public enum RangetablePluginPoint implements IPluginPoint {
  /**
   * 审批
   */
  APPROVE,
  /**
   * 删除
   */
  DELETE,
  /**
   * 新增
   */
  INSERT,

  /**
   * 送审
   */
  SEND_APPROVE,

  /**
   * 取消审核
   */
  UNAPPROVE,

  /**
   * 收回
   */
  UNSEND_APPROVE,

  /**
   * 更新
   */
  UPDATE,
  
  /**
   * 脚本删除
   */
  SCRIPT_DELETE,
  /**
   * 脚本新增
   */
  SCRIPT_INSERT,
  /**
   * 脚本更新
   */
  SCRIPT_UPDATE;

  @Override
  public String getComponent() {
    return "rangetable";
  }

  @Override
  public String getModule() {
    return "twhr";
  }

  @Override
  public String getPoint() {
    return this.getClass().getName() + "." + this.name();
  }

}
