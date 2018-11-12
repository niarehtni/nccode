/*     */ package nc.uap.portal.user.impl;
/*     */ 
/*     */ import java.net.URLEncoder;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.SimpleTimeZone;
/*     */ import java.util.TimeZone;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import javax.servlet.http.Cookie;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpSession;
/*     */ import nc.bs.framework.common.InvocationInfoProxy;
/*     */ import nc.uap.cpb.org.exception.CpbBusinessException;
/*     */ import nc.uap.cpb.org.itf.ICpSysinitQry;
/*     */ import nc.uap.cpb.org.vos.CpUserVO;
/*     */ import nc.uap.lfw.core.AppInteractionUtil;
/*     */ import nc.uap.lfw.core.LfwRuntimeEnvironment;
/*     */ import nc.uap.lfw.core.WebContext;
/*     */ import nc.uap.lfw.core.WebSession;
/*     */ import nc.uap.lfw.core.cache.LfwCacheManager;
/*     */ import nc.uap.lfw.core.comp.text.ComboBoxComp;
/*     */ import nc.uap.lfw.core.comp.text.TextComp;
/*     */ import nc.uap.lfw.core.ctx.AppLifeCycleContext;
/*     */ import nc.uap.lfw.core.ctx.ApplicationContext;
/*     */ import nc.uap.lfw.core.ctx.ViewContext;
/*     */ import nc.uap.lfw.core.ctx.WindowContext;
/*     */ import nc.uap.lfw.core.exception.LfwInteractionException;
/*     */ import nc.uap.lfw.core.exception.LfwRuntimeException;
/*     */ import nc.uap.lfw.core.page.LfwView;
/*     */ import nc.uap.lfw.core.page.ViewComponents;
/*     */ import nc.uap.lfw.core.util.HttpUtil;
/*     */ import nc.uap.lfw.login.vo.LfwSessionBean;
/*     */ import nc.uap.lfw.util.LfwUserShareUtil;
/*     */ import nc.uap.portal.cache.PortalCacheManager;
/*     */ import nc.uap.portal.deploy.vo.PtSessionBean;
/*     */ import nc.uap.portal.exception.BreakPortalLoginException;
/*     */ import nc.uap.portal.exception.PortalServerRuntimeException;
/*     */ import nc.uap.portal.exception.PortalServiceException;
/*     */ import nc.uap.portal.log.PortalLogger;
/*     */ import nc.uap.portal.login.itf.ILoginHandler;
/*     */ import nc.uap.portal.login.itf.ILoginSsoService;
/*     */ import nc.uap.portal.login.itf.IMaskerHandler;
/*     */ import nc.uap.portal.login.itf.LoginInterruptedException;
/*     */ import nc.uap.portal.login.vo.AuthenticationUserVO;
/*     */ import nc.uap.portal.om.Page;
/*     */ import nc.uap.portal.plugins.PluginManager;
/*     */ import nc.uap.portal.service.PortalServiceUtil;
/*     */ import nc.uap.portal.service.itf.IPtPageQryService;
/*     */ import nc.uap.portal.user.entity.IOrgVO;
/*     */ import nc.uap.portal.user.entity.IUserVO;
/*     */ import nc.uap.portal.user.itf.IUserLoginPlugin;
/*     */ import nc.uap.portal.util.PortalPageDataWrap;
/*     */ import nc.uap.portal.util.ToolKit;
/*     */ import nc.uap.portal.vo.PtPageVO;
/*     */ import nc.vo.bd.format.FormatDocVO;
/*     */ import nc.vo.ml.AbstractNCLangRes;
/*     */ import nc.vo.ml.NCLangRes4VoTransl;
/*     */ import nc.vo.pub.BusinessException;
/*     */ import nc.vo.pub.lang.UFBoolean;
/*     */ import org.apache.commons.lang.StringUtils;
/*     */ import uap.lfw.core.locator.AdapterServiceLocator;
/*     */ import uap.lfw.core.ml.LfwResBundle;
/*     */ import uap.lfw.portal.model.BrotherPair;
/*     */ import uap.lfw.portal.user.ForceLoginTools;
/*     */ import uap.lfw.portal.user.itf.IUserBill;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PortalLoginHandler
/*     */   implements ILoginHandler<PtSessionBean>, IMaskerHandler<PtSessionBean>
/*     */ {
/*     */   private static final String INFO = "INFO";
/*     */   private static final String ERROR = "ERROR";
/*     */   private static final String LEVEL2 = "level";
/*     */   private static final String CHALLLID2 = "challlid";
/*     */   private static final String DESC = "DESC";
/*     */   private static final String CODE = "CODE";
/*     */   private static final String AFTER = "after";
/*     */   private static final String CA_USER_ID = "p_userId";
/*     */   private static final String SIGNDATA = "p_signdata";
/*     */   private static final String MAXWIN = "p_maxwin";
/*     */   private static final String LANGUAGE = "p_language";
/*     */   protected static final String LOGINDATE = "logindate";
/*     */   protected static final String FORCE = "force";
/*     */   private static final String BEFORE = "before";
/*     */   public static final String KEY = "ufida&UAP!102";
/*  97 */   private List<IUserLoginPlugin> plugins = null;
/*  98 */   IUserBill ub = null;
/*     */   
/*     */   public PortalLoginHandler() {}
/*     */   
/*     */   public PtSessionBean doAuthenticate(AuthenticationUserVO userInfo) throws LoginInterruptedException {
/*     */     try {
/* 104 */       Map<String, String> extMap = (Map)userInfo.getExtInfo();
/*     */       
/*     */ 
/*     */ 
/* 108 */       loginPluginExecutor(userInfo, "before");
/*     */       
/* 110 */       String userid = userInfo.getUserID();
				extMap.put("needverifypasswd", "N");
/* 111 */       Map rsl = getUserBill().userVerify(userid, userInfo.getPassword(), extMap);
/*     */       
/*     */ 
/* 114 */       String rslCode = (String)rsl.get("CODE");
/* 115 */       String rslMsg = (String)rsl.get("DESC");
/* 116 */       String level = (String)rsl.get("level");
/*     */       
/*     */ 
/*     */ 
/* 120 */       if ("0".equals(rslCode))
/*     */       {
/*     */ 
/*     */ 
/* 124 */         if (AppLifeCycleContext.current() != null) {
/* 125 */           if (rslMsg != null) {
/* 126 */             if ("ERROR".equals(level))
/*     */             {
/* 128 */               LfwRuntimeEnvironment.getWebContext().getRequest().setAttribute("isResetUserPwdFlag", "Y");
/* 129 */               ensureChangePasswd(userid, rslMsg);
/*     */               
/* 131 */               throw new LoginInterruptedException(rslMsg);
/*     */             }
/* 133 */             if ("INFO".equals(level)) {
/* 134 */               AppInteractionUtil.showMessageDialogWithRePost(rslMsg);
/*     */             }
/*     */           }
/*     */         }
/* 138 */         else if ((rslMsg != null) && 
/* 139 */           ("ERROR".equals(level))) {
/* 140 */           throw new LoginInterruptedException(rslMsg);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 150 */       if ("1".equals(rslCode)) {
/* 151 */         getUserBill().doLoginErrorLog(userInfo, rslMsg);
/* 152 */         WebContext webContext = LfwRuntimeEnvironment.getWebContext();
/* 153 */         HttpSession session = null;
/* 154 */         String groupId; if (webContext != null) {
/* 155 */           HttpServletRequest httpServRequest = webContext.getRequest();
/* 156 */           if (httpServRequest != null) {
/* 157 */             session = httpServRequest.getSession();
/* 158 */             if (session != null) {
/* 159 */               session.setAttribute("fatalexcp:" + userid, "Y");
/*     */             }
/*     */           }
/*     */         } else {
/* 163 */           String userId = InvocationInfoProxy.getInstance().getUserId();
/* 164 */           groupId = InvocationInfoProxy.getInstance().getGroupId();
/*     */         }
/* 166 */         throw new LoginInterruptedException(rslMsg);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 172 */       if ("2".equals(rslCode))
/*     */       {
/* 174 */         WebContext webContext = LfwRuntimeEnvironment.getWebContext();
/* 175 */         HttpSession session = null;
/* 176 */         String challlid = UUID.randomUUID().toString();
/* 177 */         if (webContext != null) {
/* 178 */           HttpServletRequest httpServRequest = webContext.getRequest();
/* 179 */           if (httpServRequest != null) {
/* 180 */             session = httpServRequest.getSession();
/* 181 */             if (session != null) {
/* 182 */               session.setAttribute("challlid", challlid);
/*     */             }
/*     */           }
/*     */         }
/* 186 */         AppLifeCycleContext.current().getWindowContext().addExecScript("calogin('" + challlid + "','" + userid + "')");
/*     */         
/* 188 */         return null;
/*     */       }
/*     */       
/* 191 */       String langCode = "simpchn";
/* 192 */       if (extMap.get("p_language") != null) {
/* 193 */         langCode = (String)extMap.get("p_language");
/*     */       } else {
/* 195 */         langCode = LfwRuntimeEnvironment.getLangCode();
/*     */       }
/* 197 */       IUserVO ptUser = (IUserVO)rsl.get("USER");
/*     */       
/* 199 */       if (!ForceLoginTools.canMultiLogin()) {
/* 200 */         String datasource = InvocationInfoProxy.getInstance().getUserDataSource();
/* 201 */         BrotherPair<String, String> ret = ForceLoginTools.getOriSessionId(ptUser.getUserid(), datasource);
/* 202 */         if (ret != null) {
/* 203 */           if (AppLifeCycleContext.current() != null) {
/* 204 */             boolean forceLoginFlag = AppInteractionUtil.showConfirmDialog(NCLangRes4VoTransl.getNCLangRes().getStrByID("pserver", "exception-0001"), NCLangRes4VoTransl.getNCLangRes().getStrByID("pserver", "PortalLoginHandler-0000"));
/* 205 */             if (forceLoginFlag) {
/* 206 */               ForceLoginTools.logout(ret);
/*     */             } else {
/* 208 */               return null;
/*     */             }
/*     */           }
/* 211 */           else if ("Y".equals(extMap.get("forcelogin"))) {
/* 212 */             ForceLoginTools.logout(ret);
/*     */           } else {
/* 214 */             throw new SecurityException(NCLangRes4VoTransl.getNCLangRes().getStrByID("pserver", "PortalLoginHandler-0000"));
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 220 */       ptUser.setLangcode(langCode);
/* 221 */       PtSessionBean sbean = createSessionBean(ptUser);
/* 222 */       String tzOffset = (String)extMap.get("p_tz");
/* 223 */       if (tzOffset != null) {
/* 224 */         int rawOffset = Integer.parseInt(tzOffset) * 60 * -1 * 1000;
/* 225 */         TimeZone tz = new SimpleTimeZone(rawOffset, "GMT " + rawOffset / 60 / 60 / 1000);
/* 226 */         sbean.setTimeZone(tz);
/*     */       }
/*     */       
/* 229 */       sbean.setSecurityToken(null);
/* 230 */       sbean.fireLocalEnvironment();
/*     */       
/* 232 */       return sbean;
/*     */     } catch (Exception e) {
/* 234 */       if ((e instanceof LfwInteractionException)) {
/* 235 */         throw ((LfwInteractionException)e);
/*     */       }
/* 237 */       if ((e instanceof LoginInterruptedException))
/* 238 */         throw ((LoginInterruptedException)e);
/* 239 */       if ((e instanceof SecurityException)) {
/* 240 */         throw ((SecurityException)e);
/*     */       }
/* 242 */       PortalLogger.error("Login Error:" + e.getMessage(), e);
/* 243 */       throw new LoginInterruptedException(e.getMessage());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void ensureChangePasswd(String userid, String rslMsg)
/*     */   {
/* 252 */     WebContext webContext = LfwRuntimeEnvironment.getWebContext();
/* 253 */     HttpSession session = null;
/* 254 */     String challlid = UUID.randomUUID().toString();
/* 255 */     if (webContext != null) {
/* 256 */       HttpServletRequest httpServRequest = webContext.getRequest();
/* 257 */       if (httpServRequest != null) {
/* 258 */         session = httpServRequest.getSession();
/* 259 */         if (session != null) {
/* 260 */           session.setAttribute("USER_SESSION_ID", userid);
/*     */         }
/*     */       }
/*     */     }
/* 264 */     StringBuffer urlBuf = new StringBuffer();
/* 265 */     urlBuf.append("/portal/app/mockapp/passwordmng?model=nc.uap.portal.mng.pwdmng.PasswordManagerModel");
/* 266 */     urlBuf.append("&otherPageUniqueId=" + LfwRuntimeEnvironment.getWebContext().getWebSession().getWebSessionId());
/* 267 */     AppLifeCycleContext.current().getApplicationContext().popOuterWindow(urlBuf.toString(), rslMsg, "480", "280", "TYPE_DIALOG");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void afterLogin(LfwSessionBean userVO)
/*     */   {
/* 276 */     HttpServletRequest request = LfwRuntimeEnvironment.getWebContext().getRequest();
/*     */     try {
/* 278 */       LfwRuntimeEnvironment.setLfwSessionBean(userVO);
/* 279 */       LfwRuntimeEnvironment.setClientIP(HttpUtil.getIp());
/* 280 */       LfwRuntimeEnvironment.setDatasource(userVO.getDatasource());
/* 281 */       if (!"annoyuser".equals(userVO.getUser_code()))
/* 282 */         changeSessionIdentifier(request);
/* 283 */       request.getSession().setAttribute("LOGIN_SESSION_BEAN", userVO);
/* 284 */       initUser(userVO);
/* 285 */       regOnlineUser(userVO, request);
/* 286 */       ILoginSsoService<PtSessionBean> ssoService = getLoginSsoService();
/* 287 */       ssoService.addSsoSign((PtSessionBean)userVO, getSysType());
/* 288 */       UFBoolean loginResult = UFBoolean.TRUE;
/*     */       
/*     */ 
/*     */ 
/* 292 */       loginPluginExecutor(userVO, "after");
/* 293 */       getUserBill().doLoginLog(userVO, loginResult, LfwResBundle.getInstance().getStrByID("pserver", "PortalLoginHandler-000023"));
/*     */     }
/*     */     catch (BusinessException e) {
/* 296 */       PortalLogger.error(e.getMessage(), e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void initUser(LfwSessionBean sbean)
/*     */     throws PortalServiceException
/*     */   {
/* 307 */     IPtPageQryService qry = PortalServiceUtil.getPageQryService();
/* 308 */     PtSessionBean sb = (PtSessionBean)sbean;
/* 309 */     IUserVO user = sb.getUser();
/*     */     
/* 311 */     String origPkorg = user.getPk_org();
/* 312 */     CpUserVO uservo = (CpUserVO)user.getUser();
/*     */     
/*     */ 
/*     */ 
/* 316 */     if ((LfwUserShareUtil.isNeedShareUser) && (StringUtils.isNotEmpty(sbean.getPk_unit())) && (StringUtils.isNotEmpty(user.getPk_group())) && (!sbean.getPk_unit().equals(user.getPk_group()))) {
/* 317 */       uservo.setPk_org(sbean.getPk_unit());
/*     */     }
/* 319 */     PtPageVO[] pageVOs = qry.getPagesByUser(user);
/* 320 */     uservo.setPk_org(origPkorg);
/*     */     
/* 322 */     if ((pageVOs == null) || (pageVOs.length == 0))
/* 323 */       throw new PortalServerRuntimeException(LfwResBundle.getInstance().getStrByID("pserver", "PortalLoginHandler-000024"));
/* 324 */     pageVOs = PortalPageDataWrap.filterPagesByUserType(pageVOs, sb.getUser_type());
/* 325 */     if ((pageVOs == null) || (pageVOs.length == 0))
/* 326 */       throw new PortalServerRuntimeException(LfwResBundle.getInstance().getStrByID("pserver", "PortalLoginHandler-000025"));
/* 327 */     List<Page> pageList = PortalPageDataWrap.praseUserPages(pageVOs);
/* 328 */     if (pageList.isEmpty())
/* 329 */       throw new PortalServerRuntimeException(LfwResBundle.getInstance().getStrByID("pserver", "PortalLoginHandler-000026"));
/* 330 */     Map<String, Page> pagesCache = PortalPageDataWrap.praseUserPages((Page[])pageList.toArray(new Page[0]));
/* 331 */     PortalCacheManager.getUserPageCache().clear();
/* 332 */     PortalCacheManager.getUserPageCache().putAll(pagesCache);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private List<IUserLoginPlugin> getLoginPlugins()
/*     */   {
/* 341 */     if (this.plugins == null)
/* 342 */       this.plugins = PluginManager.newIns().getExtInstances("loginplugin", IUserLoginPlugin.class);
/* 343 */     return this.plugins;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private PtSessionBean createSessionBean(IUserVO user)
/*     */   {
/* 356 */     IOrgVO org = getUserBill().getOrg(user.getPk_group());
/* 357 */     String groupName; 
			  String groupNo; 
			  if (org != null) {
/* 358 */       groupNo = org.getCode();
/* 359 */       groupName = org.getName();
/*     */     } else {
/* 361 */       groupNo = "0000";
/* 362 */       groupName = "0000";
/*     */     }
/*     */     
/* 365 */     PtSessionBean sbean = new PtSessionBean();
/*     */     
/* 367 */     sbean.setDatasource(LfwRuntimeEnvironment.getDatasource());
/* 368 */     sbean.setUnitNo(groupNo);
/* 369 */     sbean.setUnitName(groupName);
/* 370 */     sbean.setUserType(user.getUsertype());
/* 371 */     sbean.setUser(user);
/* 372 */     sbean.setTimespan(System.currentTimeMillis());
/* 373 */     String themeId = LfwRuntimeEnvironment.getThemeId();
/* 374 */     sbean.setThemeId(themeId);
/* 375 */     return sbean;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public AuthenticationUserVO getAuthenticateVO()
/*     */     throws LoginInterruptedException
/*     */   {
/* 383 */     AuthenticationUserVO userVO = new AuthenticationUserVO();
/* 384 */     Map<String, String> extMap = new HashMap();
/* 385 */     LfwView widget = getCurrentWidget();
/* 386 */     TextComp userIdComp = (TextComp)widget.getViewComponents().getComponent("userid");
/* 387 */     TextComp randomImageComp = (TextComp)widget.getViewComponents().getComponent("randimg");
/*     */     
/* 389 */     ICpSysinitQry cpSysinitQry = PortalServiceUtil.getCpSysinitQry();
/* 390 */     boolean enabledRandomImage = false;
/*     */     try {
/* 392 */       String showRanImg = cpSysinitQry.getSysinitValueByCodeAndPkorg("randomimg", null);
/* 393 */       enabledRandomImage = UFBoolean.valueOf(showRanImg).booleanValue();
/*     */     } catch (CpbBusinessException e) {
/* 395 */       PortalLogger.error(e.getMessage(), e);
/*     */     }
/* 397 */     String userId = null;
/*     */     
/* 399 */     AppLifeCycleContext pctx = AppLifeCycleContext.current();
/* 400 */     HttpSession session = LfwRuntimeEnvironment.getWebContext().getRequest().getSession();
/* 401 */     String signdata = pctx.getParameter("p_signdata");
/* 402 */     String sn = pctx.getParameter("p_sn");
/* 403 */     String tz = pctx.getParameter("p_tz");
/* 404 */     if (userIdComp != null) {
/* 405 */       if (enabledRandomImage) {
/* 406 */         String rand = null;
/* 407 */         if (session != null) {
/* 408 */           rand = (String)session.getAttribute("rand");
/*     */         }
/* 410 */         String ricv = randomImageComp.getValue();
/* 411 */         if (!StringUtils.equals(rand, ricv)) {
/* 412 */           throw new LoginInterruptedException(LfwResBundle.getInstance().getStrByID("pserver", "PortalLoginHandler-000006"));
/*     */         }
/*     */       }
/* 415 */       userId = userIdComp.getValue();
/* 416 */       if ((userId == null) || (userId.equals(""))) {
/* 417 */         throw new LoginInterruptedException(LfwResBundle.getInstance().getStrByID("pserver", "PortalLoginHandler-000007"));
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 422 */     TextComp passComp = (TextComp)widget.getViewComponents().getComponent("password");
/* 423 */     String passValue = null;
/* 424 */     if (passComp != null) {
/* 425 */       passValue = passComp.getValue();
/* 426 */       if (passValue == null) {
/* 427 */         passValue = "";
/*     */       }
/*     */     }
/*     */     
/* 431 */     ComboBoxComp multiLanguageCombo = (ComboBoxComp)widget.getViewComponents().getComponent("multiLanguageCombo");
/* 432 */     String language = multiLanguageCombo.getValue();
/*     */     
/* 434 */     userVO.setUserID(userId);
/* 435 */     userVO.setPassword(passValue);
/* 436 */     extMap.put("p_language", language);
/* 437 */     extMap.put("p_maxwin", "N");
/* 438 */     extMap.put("p_signdata", signdata);
/* 439 */     extMap.put("p_sn", sn);
/*     */     
/* 441 */     extMap.put("p_tz", tz);
/* 442 */     String challlid = (String)session.getAttribute("challlid");
/* 443 */     extMap.put("challlid", challlid);
/* 444 */     userVO.setExtInfo(extMap);
/* 445 */     return userVO;
/*     */   }
/*     */   
/*     */ 
/*     */   public Cookie[] getCookies(AuthenticationUserVO userVO)
/*     */   {
/* 451 */     List<Cookie> list = new ArrayList();
/* 452 */     String userId = userVO.getUserID();
/* 453 */     Map<String, String> extMap = (Map)userVO.getExtInfo();
/* 454 */     String sysId = "" + LfwRuntimeEnvironment.getSysId();
/* 455 */     String themeId = LfwRuntimeEnvironment.getLfwSessionBean().getThemeId();
/* 456 */     String language = (String)extMap.get("p_language");
/* 457 */     String maxwin = (String)extMap.get("p_maxwin");
/* 458 */     String useridEncode = null;
/* 459 */     String cookiePath = LfwRuntimeEnvironment.getRootPath();
/*     */     try {
/* 461 */       useridEncode = URLEncoder.encode(userId, "UTF-8");
/*     */     } catch (Exception e) {
/* 463 */       PortalLogger.warn(e.getMessage());
/*     */     }
/*     */     
/*     */ 
/* 467 */     Cookie tc = new Cookie("TH_K" + sysId, themeId);
/* 468 */     tc.setPath("/");
/* 469 */     tc.setMaxAge(604800000);
/* 470 */     list.add(tc);
/*     */     
/*     */ 
/* 473 */     Cookie lc = new Cookie("LA_K" + sysId, language);
/* 474 */     lc.setPath("/");
/* 475 */     lc.setMaxAge(604800000);
/* 476 */     list.add(lc);
/*     */     
/* 478 */     Cookie uc = new Cookie("p_userId", useridEncode);
/* 479 */     uc.setPath(cookiePath);
/* 480 */     uc.setMaxAge(604800000);
/* 481 */     list.add(uc);
/*     */     
/* 483 */     Cookie mc = new Cookie("isMaxWindow", maxwin);
/* 484 */     mc.setPath(cookiePath);
/* 485 */     mc.setMaxAge(604800000);
/* 486 */     list.add(mc);
/*     */     
/* 488 */     Cookie p_auth = new Cookie("p_logoutflag", null);
/* 489 */     p_auth.setMaxAge(604800000);
/* 490 */     p_auth.setPath(cookiePath);
/* 491 */     list.add(p_auth);
/*     */     
/* 493 */     return (Cookie[])list.toArray(new Cookie[0]);
/*     */   }
/*     */   
/*     */   public ILoginSsoService<PtSessionBean> getLoginSsoService()
/*     */   {
/* 498 */     return new PortalSSOServiceImpl();
/*     */   }
/*     */   
/*     */   public String getSysType()
/*     */   {
/* 503 */     return "pt";
/*     */   }
/*     */   
/*     */   public LfwView getCurrentWidget() {
/* 507 */     return AppLifeCycleContext.current().getViewContext().getView();
/*     */   }
/*     */   
/*     */   public FormatDocVO getMaskerInfo(PtSessionBean loginBean)
/*     */   {
/* 512 */     return getUserBill().getMaskerInfo(loginBean);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void regOnlineUser(LfwSessionBean sb, HttpServletRequest request)
/*     */     throws BusinessException
/*     */   {
/* 522 */     String clientIP = HttpUtil.getIp();
/* 523 */     String sessionid = request.getSession().getId();
/* 524 */     getUserBill().regOnlineUser(sb, sessionid, clientIP);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void loginPluginExecutor(Object userInfo, String cmd)
/*     */   {
/* 537 */     if (ToolKit.notNull(getLoginPlugins())) {
/* 538 */       for (IUserLoginPlugin ex : getLoginPlugins()) {
/* 539 */         boolean isBefore = "before".equals(cmd);
/*     */         try {
/* 541 */           if (isBefore) {
/* 542 */             ex.beforeLogin((AuthenticationUserVO)userInfo);
/*     */           } else
/* 544 */             ex.afterLogin((PtSessionBean)userInfo);
/*     */         } catch (BreakPortalLoginException e) {
/* 546 */           PortalLogger.error(e.getMessage(), e);
/* 547 */           if (isBefore) {
/* 548 */             getUserBill().doLoginErrorLog((AuthenticationUserVO)userInfo, e.getHint());
/*     */           } else
/* 550 */             getUserBill().doLoginLog((LfwSessionBean)userInfo, UFBoolean.FALSE, e.getHint());
/* 551 */           throw new LfwRuntimeException(e.getHint());
/*     */         } catch (Throwable a) {
/* 553 */           PortalLogger.error(LfwResBundle.getInstance().getStrByID("pserver", "PortalLoginHandler-000027") + a.getMessage(), a);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private IUserBill getUserBill() {
/* 560 */     if (this.ub == null) {
/* 561 */       this.ub = ((IUserBill)AdapterServiceLocator.newIns().get(IUserBill.class));
/*     */     }
/* 563 */     return this.ub;
/*     */   }
/*     */   
/*     */   private HttpSession changeSessionIdentifier(HttpServletRequest request)
/*     */   {
/* 568 */     HttpSession oldSession = request.getSession();
/*     */     
/*     */ 
/* 571 */     Map<String, Object> temp = new ConcurrentHashMap();
/* 572 */     Map<String, Object> oldSessionCache = new ConcurrentHashMap();
/* 573 */     oldSessionCache.putAll(LfwCacheManager.getSessionCache());
/* 574 */     Enumeration<String> e = oldSession.getAttributeNames();
/* 575 */     while ((e != null) && (e.hasMoreElements())) {
/* 576 */       String name = (String)e.nextElement();
/* 577 */       Object value = oldSession.getAttribute(name);
/* 578 */       temp.put(name, value);
/*     */     }
/* 580 */     oldSession.setAttribute("SESSION_SELF_DESTORY", Boolean.TRUE);
/*     */     
/* 582 */     oldSession.invalidate();
/* 583 */     HttpSession newSession = request.getSession(true);
/*     */     
/*     */ 
/* 586 */     for (Map.Entry<String, Object> stringObjectEntry : temp.entrySet())
/*     */     {
/* 588 */       newSession.setAttribute((String)stringObjectEntry.getKey(), stringObjectEntry.getValue());
/*     */     }
/* 590 */     Map<String, Object> newSessionCache = LfwCacheManager.getSessionCache();
/* 591 */     newSessionCache.putAll(oldSessionCache);
/* 592 */     return newSession;
/*     */   }
/*     */ }

/* Location:           D:\NCHOME\nc65\modules\websm\lib\pubwebsm_pserverLevel-1.jar
 * Qualified Name:     nc.uap.portal.user.impl.PortalLoginHandler
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.0.1
 */