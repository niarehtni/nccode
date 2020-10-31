package nc.search.skd.crawler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import nc.bs.framework.common.NCLocator;
import nc.bs.framework.component.ContextAwareComponent;
import nc.bs.framework.component.ServiceComponent;
import nc.bs.framework.core.ComponentContext;
import nc.bs.framework.core.Container;
import nc.bs.framework.core.LifeCycle;
import nc.bs.framework.core.LifeCycleListener;
import nc.bs.framework.core.Server;
import nc.bs.framework.execute.ThreadFactoryManager;
import nc.search.sdk.common.AnteLogger;
import nc.search.sdk.crawler.CrawlerException;
import nc.search.sdk.crawler.ICrawlerService;
import nc.search.sdk.crawler.ISearchHandler;

public class SearchMessageHandler implements ISearchHandler, ServiceComponent, 
		ContextAwareComponent<Container>, LifeCycleListener {

	private SearchMessageDaemon daemon;
	
	private ComponentContext<Container> ctx;

	private volatile boolean started = false;

	public SearchMessageHandler(SearchMessageQ searchQ, int interval, int workThread) {
		AnteLogger.debug("The handler construct success!!");
		daemon = new SearchMessageDaemon(searchQ, interval, workThread);
	}

	public boolean isStarted() {
			return started;
	}

	public void start() throws Exception {
		AnteLogger.debug("The handler start success!!");
		if (!isStarted()) {
			started = true;
			getServer().addLifecycleListener(this);
		}
	}

	public void stop() throws Exception {
		started = false;
		getServer().removeLifecycleListener(this);
	}

	class SearchMessageDaemon implements Runnable {

		final SearchMessageQ searchQ;

		int interval;

		private volatile Thread t;

		private volatile boolean requireStop;

		private final ExecutorService executorManager;
		
		private ExecutorService threadPool;
		
		private int workThreadNum;

		public SearchMessageDaemon(SearchMessageQ searchQ, int interval, int workThreadNum) {
			this.searchQ = searchQ;
			this.interval = interval;
			this.executorManager = Executors.newFixedThreadPool(1,
					ThreadFactoryManager.newThreadFactory());
			this.workThreadNum = workThreadNum;
		}

		public boolean isRequireStop() {
			return requireStop;
		}

		public void start() {
			try {
				threadPool = ThreadPoolFactory.getInstance(workThreadNum);
				requireStop = false;
				executorManager.submit(this);
			} catch (Exception exp) {
			}
		}

		public void stop() {
			threadPool.shutdown();
			requireStop = true;
			executorManager.shutdown();
			if (t != null) {
				t.interrupt();
			}
		}

		public void run() {
			t = Thread.currentThread();

			do {
				String group = null;
				try {
					group = searchQ.consume();
					AnteLogger.error("handle success!! the group is " + group);
					searchQ.remove(group);
					threadPool.submit( new IndexUpdateRunnable(group) );
					
					try {
						Thread.sleep(interval);
					} catch (InterruptedException e1) {
						if (requireStop) {
							break;
						}
					}

				} catch (InterruptedException e) {
					if (!requireStop) {
						Thread.interrupted();
					}
				} catch (Throwable thr) {
				}
			} while (!requireStop);
			t = null;
		}
		

		public boolean isStarted() {
			return t != null && t.isAlive();
		}
	}
	
	class UpdateThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true){
				try{
					update();
					Thread.sleep(60*1000);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		
		public void update(){
			ICrawlerService crawler = NCLocator.getInstance().lookup(ICrawlerService.class);
			try {
				AnteLogger.error("###crawlAll started!");
				crawler.crawlGroup("bd_material");
				crawler.crawlGroup("bd_material_v");
				crawler.crawlGroup("bd_supplier");
				crawler.crawlGroup("bd_customer");
				crawler.crawlGroup("bd_cust_supplier");
				crawler.crawlGroup("bd_psndoc");
				AnteLogger.error("###crawlAll finished!");
			} catch (CrawlerException e) {
				e.printStackTrace();
			}
		}
		
	}
	


	public void afterStart(LifeCycle lc) {
		daemon.start();
		Thread updateThread = new Thread(new UpdateThread());
		updateThread.setName("auto-update-group");
		updateThread.start();
	}

	public void beforeStop(LifeCycle lc) {
		daemon.stop();
	}
	
	private Server getServer() {
		return ctx.getContainer().getServer();
	}
	
	private Container getContainer() {
		return (Container) ctx.getContainer();
	}

	@Override
	public void setComponentContext(ComponentContext<Container> context) {
		this.ctx = context;
	}
}

class IndexUpdateRunnable implements Runnable {
	String group;
	
	public IndexUpdateRunnable(String group) {
		this.group = group;
	}

	@Override
	public void run() {
		ICrawlerService searcher = NCLocator.getInstance().lookup(ICrawlerService.class);
		try {
			searcher.crawlGroup(group);
		} catch (CrawlerException e) {
			// TODO Auto-generated catch block
			AnteLogger.error(e);
		}
	}
}
