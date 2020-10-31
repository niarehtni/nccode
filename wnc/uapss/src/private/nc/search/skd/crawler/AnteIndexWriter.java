package nc.search.skd.crawler;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
//import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import nc.itf.metadata.search.IField;
import nc.search.ante.core.AnteIndexConfig;
import nc.search.ante.core.DirectoryFactory;
import nc.search.craw.tool.DateUtil;
import nc.search.craw.tool.IndexFileUtil;
import nc.search.index.analyzer.TokenizerChain;
import nc.search.index.analyzer.extend.MMSegExtendTokenizerFactory;
import nc.search.metadata.config.AnalyzerSchema;
import nc.search.metadata.config.AnteException;
import nc.search.metadata.config.AnteIndexAnalyzer;
import nc.search.metadata.strategy.IndexStrategy;
import nc.search.metadata.strategy.TokenStreamFactory;
import nc.search.sdk.common.AnteLogger;
import nc.search.sdk.common.FieldType;
import nc.search.sdk.common.IAnteParaConst;
import nc.search.sdk.common.PinYinType;
import nc.search.sdk.crawler.AbstractIndexData;
import nc.search.sdk.crawler.IDocStatus;
import nc.search.sdk.crawler.IndexFieldData;
import nc.search.sdk.crawler.IndexMetadata;
import nc.vo.jcom.io.FileUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFTime;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
//import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexDeletionPolicy;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.NativeFSLockFactory;
import org.apache.lucene.store.NoLockFactory;
import org.apache.lucene.store.SimpleFSLockFactory;
import org.apache.lucene.store.SingleInstanceLockFactory;
//import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.BytesRef;

import com.yonyou.ante.AnteSearchException;
import com.yonyou.ante.cluster.routing.operation.hash.djb.DjbHashFunction;
import com.yonyou.ante.common.lucene.HashedBytesRef;
import com.yonyou.ante.common.lucene.uid.Versions;
import com.yonyou.ante.index.core.ShardCore;
import com.yonyou.ante.index.core.ShardCore.Flush;
import com.yonyou.ante.index.core.ShardCore.Origin;
import com.yonyou.ante.index.core.ShardCore.RobinSearcher;
import com.yonyou.ante.index.shard.ShardId;
import com.yonyou.ante.index.translog.Translog;
import com.yonyou.ante.index.translog.fs.FsTranslog;
import com.yonyou.ante.index.translog.fs.FsTranslogFile;

/**
 * @author guohuia 2012-04-13
 * @author liumiaob
 * @add method getIndexWriter()
 * 
 */
public class AnteIndexWriter {

	public final static String LOCK_TYPE_SIMPLE = "simple";
	public final static String LOCK_TYPE_NATIVE = "native";
	public final static String LOCK_TYPE_SINGLE = "single";
	public final static String LOCK_TYPE_NONE = "none";

	// flag indicating if a dirty operation has occurred since the last refresh
	private volatile boolean dirty = false;

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public boolean isDirty() {
		return dirty;
	}

	private volatile boolean flushNeeded = false;
	private volatile boolean isStart = false;
	private final AtomicLong translogIdGenerator = new AtomicLong();

	public static final int DEFAULT_PRECISION_STEP = 4;

	public static final String COMMIT_TIME_MSEC_KEY = "commitTimeMSec";

	public static final BigDecimal BIGDECIMAL_STORE_FACTOR = new BigDecimal("100");

	private IndexWriter writer;

	private Translog translog;

	public Translog getTranslog() {
		return translog;
	}

	public void setTranslog(Translog translog) {
		this.translog = translog;
	}

	private DirectoryFactory directoryFactory;

	private Directory directory = null;

	@SuppressWarnings("unused")
	private String indexName;

	private String indexPath;

	private ShardId shardId;

	private String translogPath;

	private static AnalyzerSchema indexSchema = AnalyzerSchema.getInstance();

	private IndexWriterConfig indexWriterConfig;

	private final AnteIndexConfig indexConfig;
	private final Analyzer defaultAnalyzer;
	private IndexDeletionPolicy indexDeletionPolicy;

	private final ConcurrentMap<HashedBytesRef, VersionValue> versionMap;

	private final Object[] dirtyLocks = new Object[500]; // we multiply it to
															// have enough...;

	private ShardCore parentShardCore;

	public AnteIndexWriter(ShardId shardId, String path, DirectoryFactory factory, boolean create, AnteIndexConfig indexConfig,
			IndexDeletionPolicy indexDelPolicy, String translogPath, ShardCore parentShardCore) {
		super();
		this.parentShardCore = parentShardCore;
		this.shardId = shardId;
		this.indexPath = path;
		this.directoryFactory = factory;
		this.indexConfig = indexConfig;
		this.indexDeletionPolicy = indexDelPolicy;
		defaultAnalyzer = new WhitespaceAnalyzer(this.indexConfig.luceneVersion);
		this.indexWriterConfig = getIndexWriterConfig(create);
		this.translogPath = translogPath;
		for (int i = 0; i < dirtyLocks.length; i++) {
			dirtyLocks[i] = new Object();
		}
		versionMap = new ConcurrentHashMap<HashedBytesRef, AnteIndexWriter.VersionValue>();
		initTranslog();
	}

	@Deprecated
	public AnteIndexWriter(String indexName, String path, DirectoryFactory factory, boolean create, AnteIndexConfig indexConfig,
			IndexDeletionPolicy indexDelPolicy) {
		super();
		this.indexName = indexName;
		this.indexPath = path;
		this.directoryFactory = factory;
		this.indexConfig = indexConfig;
		this.indexDeletionPolicy = indexDelPolicy;
		defaultAnalyzer = new WhitespaceAnalyzer(this.indexConfig.luceneVersion);
		this.indexWriterConfig = getIndexWriterConfig(create);
		versionMap = new ConcurrentHashMap<HashedBytesRef, AnteIndexWriter.VersionValue>();
		init();
	}

	private IndexWriterConfig getIndexWriterConfig(boolean create) {
		IndexWriterConfig config = indexConfig.toIndexWriterConfig(indexSchema, defaultAnalyzer);
		IndexWriterConfig.OpenMode openMode = create ? IndexWriterConfig.OpenMode.CREATE
				: IndexWriterConfig.OpenMode.CREATE_OR_APPEND;
		config.setOpenMode(openMode);

		config.setIndexDeletionPolicy(indexDeletionPolicy);

		return config;
	}

	public static Directory getDirectory(String path, DirectoryFactory directoryFactory, AnteIndexConfig indexConfig)
			throws IOException {
		Directory d = directoryFactory.open(path);
		String rawLockType = indexConfig.lockType;
		final String lockType = rawLockType.toLowerCase(Locale.ENGLISH).trim();
		if (LOCK_TYPE_SIMPLE.equals(lockType)) {
			d.setLockFactory(new SimpleFSLockFactory(path));
		} else if (LOCK_TYPE_NATIVE.equals(lockType)) {
			d.setLockFactory(new NativeFSLockFactory(path));
		} else if (LOCK_TYPE_SINGLE.equals(lockType)) {
			if (!(d.getLockFactory() instanceof SingleInstanceLockFactory))
				d.setLockFactory(new SingleInstanceLockFactory());
		} else if (LOCK_TYPE_NONE.equals(lockType)) {
			AnteLogger.error("CONFIGURATION WARNING: locks are disabled on " + path);
			d.setLockFactory(NoLockFactory.getNoLockFactory());
		} else {
			throw new AnteException(AnteException.ErrorCode.SERVER_ERROR, "Unrecognized lockType: " + rawLockType);
		}
		return d;
	}

	private void init() {
		try {
			IndexFileUtil.removeLockIfUseless(indexPath);
			directory = getDirectory(indexPath, directoryFactory, indexConfig);
			writer = new IndexWriter(directory, indexWriterConfig);
			AnteLogger.debug("ante core index Path:" + indexPath);
		} catch (LockObtainFailedException e) {
			AnteLogger.error("index locked already.", e);
		} catch (Exception e) {// 删除重建
			AnteLogger.error("init shard " + shardId + " io error! ", e);
			try {
				File indexDir = new File(indexPath);
				FileUtil.cleanDirectory(indexDir.getParentFile());
				writer = new IndexWriter(directory, getIndexWriterConfig(true));
			} catch (Exception e2) {
				AnteLogger.error("reopen index writer error!", e2);
			}
		}
	}

	/**
	 * 建立translog对象
	 */
	private void initTranslog() {
		if (translog == null) {
			AnteLogger.debug("begin create translog ! the translog dir is " + translogPath);
			File dir = new File(translogPath);
			translog = new FsTranslog(shardId, FsTranslogFile.Type.SIMPLE.name(), dir);
			translogIdGenerator.set(System.currentTimeMillis());
			translog.newTranslog(translogIdGenerator.get());
			AnteLogger.debug("create translog success !");
		} else {
			AnteLogger.error("translog already exist ! the translog dir is " + translogPath);
		}
	}

	@Deprecated
	public void reOpen() {
		this.indexWriterConfig = getIndexWriterConfig(false);
		init();
	}

	/**
	 * 建立并启动lucene的indexWriter
	 */
	public void start() {
		if (!isStart) {
			isStart = true;
			init();
			flushNeeded = true;
			AnteLogger.debug(shardId + "ante index writer start:" + writer);
			flush(new Flush());
		}
	}

	public void clear() {
		try {
			this.writer.deleteUnusedFiles();
		} catch (IOException e) {
			AnteLogger.error(e.getMessage(), e);
		}
	}

	public void close() {

		try {
			AnteLogger.debug("Closing writer :" + shardId);
			// writer.close();
			if (writer != null) {
				writer.rollback();
				writer.close(false);// 防止关闭后资源依然被占用的情况
				writer = null;
			}
			isStart = false;
		} catch (Exception e) {
			AnteLogger.error("close index writer " + shardId + " error !", e);
		} finally {
			try {
				if (IndexWriter.isLocked(directory)) {
					IndexWriter.unlock(directory);
				}
			} catch (Exception ee) {
				AnteLogger.error("unlock directory error !", ee);
			}
		}

		try {
			directoryFactory.release(directory);
			directory.close();
		} catch (Exception e) {
			AnteLogger.error("close directory error!", e);
		}
		try {
			translog.closeWithDelete();
		} catch (Exception e) {
			AnteLogger.error("close translog error", e);
		}
	}

	public void commit() {
		try {
			writer.commit();
		} catch (CorruptIndexException e) {
			AnteLogger.error(e.getMessage(), e);
		} catch (IOException e) {
			AnteLogger.error(e.getMessage(), e);
		}
	}

	// @Deprecated
	// private void delete(String field, String value) {
	// Term deleteTerm = new Term(field, value);
	// try {
	// this.writer.deleteDocuments(deleteTerm);
	// } catch (CorruptIndexException e) {
	// AnteLogger.error("index corrupt exception:" + e.getMessage(), e);
	// } catch (IOException e) {
	// AnteLogger.error("inde IOException" + e.getMessage(), e);
	// }
	// }

	@Deprecated
	public void deleteByQuery(Query query) {
		try {
			this.writer.deleteDocuments(query);
		} catch (CorruptIndexException e) {
			AnteLogger.error("index corrupt exception:" + e.getMessage(), e);
		} catch (IOException e) {
			AnteLogger.error("inde IOException" + e.getMessage(), e);
		}
	}

	// @Deprecated
	// private void delete(String field, List<String> values) {
	// if (values == null)
	// return;
	// for (String strValue : values) {
	// delete(field, strValue);
	// }
	// }

	public void deleteAll() {
		try {
			this.writer.deleteAll();
		} catch (IOException e) {
			AnteLogger.error(e.getMessage(), e);
		}
	}

	public void index(AbstractIndexData data, Origin origin) throws Exception {
		if (data == null)
			return;
		int docStatus = data.getDocStatus();
		switch (docStatus) {
		case IDocStatus.DELETED:// 作废掉
			deleteDocument(data, origin);
			break;
		case IDocStatus.ADD:
		case IDocStatus.UPDATED:
		default:
			addDocument(data, origin);
			break;
		}
	}

	public void delete(AbstractIndexData data, Origin origin) throws Exception {
		IndexMetadata metadata = data.getMetadata();
		try {
			String anteId = getAnteUidString(data, metadata);
			Term anteIdTerm = new Term(IAnteParaConst.ANTE_DOC_UID, anteId);
			innerDelete(anteIdTerm, writer, data, origin);
			dirty = true;
			flushNeeded = true;
		} catch (Exception e) {
			AnteLogger.error("index writer delete doc error! ", e);
			throw e;
		}
	}

	private void innerDelete(Term anteIdTerm, IndexWriter iw, AbstractIndexData data, Origin origin) throws Exception {
		synchronized (dirtyLock(anteIdTerm)) {
			HashedBytesRef versionKey = versionKey(anteIdTerm);
			final long currentVersion;
			VersionValue versionValue = versionMap.get(versionKey);
			if (versionValue == null) {// 如果缓存中没有取到，则去searcher中取version
				currentVersion = loadCurrentVersionFromIndex(anteIdTerm);
			} else {
				if (versionValue.delete()) {
					currentVersion = Versions.NOT_FOUND; // deleted, and GC
				} else {
					currentVersion = versionValue.version();
				}
			}

			long updatedVersion;
			long expectedVersion = Versions.MATCH_ANY;
			IndexFieldData tmp = data.get(IAnteParaConst.ANTE_DOC_VERSION);
			if (tmp != null) {
				expectedVersion = (Long) tmp.getValue();
			}

			if (Origin.PRIMARY.equals(origin)) {// 从用户请求来
				// TODO 这里要判断下冲突情况，暂时先不判断了。
				if (currentVersion != Versions.NOT_FOUND) {
					updatedVersion = currentVersion + 1;
				} else {
					updatedVersion = 1;
				}
			} else {// 从translog中来（可能还有别的地方来。。。？？需要研究）
				if (expectedVersion == Versions.MATCH_ANY) {// 必须有值
					throw new AnteSearchException("expectedVersion mast have value!");
				}
				if (currentVersion >= expectedVersion) {// 当前的版本比预期的还新，证明是旧数据，直接忽略
					return;
				}
				updatedVersion = expectedVersion;
			}

			IndexFieldData v = new IndexFieldData(IAnteParaConst.ANTE_DOC_VERSION);
			v.setValue(updatedVersion, 1.0f);
			data.put(IAnteParaConst.ANTE_DOC_VERSION, v);

			if (currentVersion == Versions.NOT_FOUND) {
				// doc does not exists and no prior deletes
				Translog.Location translogLocation = translog.add(new Translog.Delete(anteIdTerm, updatedVersion, data));
				versionMap.put(versionKey, new VersionValue(updatedVersion, true, System.currentTimeMillis(), translogLocation));
			} else if (versionValue != null && versionValue.delete()) {
				// a "delete on delete", in this case, we still increment the
				// version, log it, and return that version
				Translog.Location translogLocation = translog.add(new Translog.Delete(anteIdTerm, updatedVersion, data));
				versionMap.put(versionKey, new VersionValue(updatedVersion, true, System.currentTimeMillis(), translogLocation));
			} else {
				writer.deleteDocuments(anteIdTerm);
				Translog.Location translogLocation = translog.add(new Translog.Delete(anteIdTerm, updatedVersion, data));
				versionMap.put(versionKey, new VersionValue(updatedVersion, true, System.currentTimeMillis(), translogLocation));
			}
		}// end synchronized
	}

	private void addDocument(AbstractIndexData data, Origin origin) throws Exception {
		IndexMetadata metadata = data.getMetadata();
		AnteIndexAnalyzer analyzer = getAnteIndexAnalyzer(metadata.getSrcFields());
		// TODO 查找扩展词库，这个地方应该性能有问题，稍后考虑修改下
		String ds = (String) data.getFieldValue(IAnteParaConst.DATASOURCE);
		updateExtendDic(analyzer, ds);
		try {
			String anteId = getAnteUidString(data, metadata);
			Term anteIdTerm = new Term(IAnteParaConst.ANTE_DOC_UID, anteId);
			innerAddDocument(anteIdTerm, writer, analyzer, data, origin);

			dirty = true;
			flushNeeded = true;
		} catch (CorruptIndexException e) {
			AnteLogger.error("index writer CorruptIndexException:" + e.getMessage(), e);
			throw e;
		} catch (IOException e) {
			AnteLogger.error("index writer IOException:" + e.getMessage(), e);
			throw e;
		}
	}

	private void innerAddDocument(Term anteIdTerm, IndexWriter iw, AnteIndexAnalyzer analyzer, AbstractIndexData data,
			Origin origin) throws Exception {
		synchronized (dirtyLock(anteIdTerm)) {
			HashedBytesRef versionKey = versionKey(anteIdTerm);
			final long currentVersion;
			VersionValue versionValue = versionMap.get(versionKey);
			if (versionValue == null) {// 如果缓存中没有取到，则去searcher中取version
				currentVersion = loadCurrentVersionFromIndex(anteIdTerm);
			} else {
				if (versionValue.delete()) {
					currentVersion = Versions.NOT_FOUND; // deleted, and GC
				} else {
					currentVersion = versionValue.version();
				}
			}

			long updatedVersion;
			long expectedVersion = Versions.MATCH_ANY;
			IndexFieldData tmp = data.get(IAnteParaConst.ANTE_DOC_VERSION);
			if (tmp != null) {
				expectedVersion = (Long) tmp.getValue();
			}

			if (Origin.PRIMARY.equals(origin)) {// 从用户请求来
				// TODO 这里要判断下冲突情况，暂时先不判断了。
				if (currentVersion != Versions.NOT_FOUND) {
					updatedVersion = currentVersion + 1;
				} else {
					updatedVersion = 1;
				}
			} else {// 从translog中来（可能还有别的地方来。。。？？需要研究）
				if (expectedVersion == Versions.MATCH_ANY) {// 必须有值
					throw new AnteSearchException("expectedVersion mast have value!");
				}
				if (currentVersion >= expectedVersion) {// 当前的版本比预期的还新，证明是旧数据，直接忽略
					return;
				}
				updatedVersion = expectedVersion;
			}

			IndexFieldData v = new IndexFieldData(IAnteParaConst.ANTE_DOC_VERSION);
			v.setValue(updatedVersion, 1.0f);
			data.put(IAnteParaConst.ANTE_DOC_VERSION, v);
			IndexMetadata metadata = data.getMetadata();
			Document doc = getDocument(data, metadata, analyzer);

			if (currentVersion == Versions.NOT_FOUND) {
				// 写索引文件
				iw.addDocument(doc, analyzer);
			} else {
				iw.updateDocument(anteIdTerm, doc);
			}
			// 写日志文件
			Translog.Location translogLocation = translog.add(new Translog.Index(anteIdTerm, updatedVersion, data));
			// 将版本记录缓存
			versionMap.put(versionKey, new VersionValue(updatedVersion, false, System.currentTimeMillis(), translogLocation));
		}// end synchronized
	}

	private void deleteDocument(AbstractIndexData data, Origin origin) {
		String idField = data.getMetadata().getIDFieldName();
		String pk = data.getFieldValue(idField).toString();
		try {
			writer.deleteDocuments(new Term(idField, pk));
		} catch (CorruptIndexException e) {
			AnteLogger.error(e.getMessage(), e);
		} catch (IOException e) {
			AnteLogger.error(e.getMessage(), e);
		}
	}

	public boolean flush(Flush flush) throws AnteSearchException {
		if (flush.type() == Flush.Type.NEW_WRITER) {
			// TODO
		} else if (flush.type() == Flush.Type.COMMIT_TRANSLOG) {
			if (flushNeeded || flush.force()) {
				flushNeeded = false;
				try {
					long translogId = translogIdGenerator.incrementAndGet();
					translog.newTransientTranslog(translogId);
					Map<String, String> commitMap = new HashMap<String, String>();
					commitMap.put(Translog.TRANSLOG_ID_KEY, Long.toString(translogId));
					writer.setCommitData(commitMap);

					writer.commit();
					// 刷新缓存表
					refreshVersioningTable(System.currentTimeMillis());
					// we need to move transient to current only after we
					// refresh
					// so items added to current will still be around for
					// realtime get
					// when tans overrides it
					translog.makeTransientCurrent();
					return true;
				} catch (OutOfMemoryError e) {
					translog.revertTransient();
					throw new AnteSearchException("shard " + shardId, e);
				} catch (IllegalStateException e) {
					if (e.getMessage().contains("OutOfMemoryError")) {
					}
					throw new AnteSearchException("shard " + shardId, e);
				} catch (Throwable e) {
					translog.revertTransient();
					throw new AnteSearchException("shard " + shardId, e);
				}
			}
		} else if (flush.type() == Flush.Type.COMMIT) {
			// TODO
		} else {
			throw new AnteSearchException("flush type [" + flush.type() + "] not supported");
		}

		return false;
	}

	private void refreshVersioningTable(long time) {
		// we need to refresh in order to clear older version values
		// refresh(new Refresh(true).force(true));
		for (Map.Entry<HashedBytesRef, VersionValue> entry : versionMap.entrySet()) {
			HashedBytesRef uid = entry.getKey();
			synchronized (dirtyLock(uid.bytes)) { // can we do it without this
													// lock on each value? maybe
													// batch to a set and get
													// the lock once per set?
				VersionValue versionValue = versionMap.get(uid);
				if (versionValue == null) {
					continue;
				}
				if (time - versionValue.time() <= 0) {
					continue; // its a newer value, from after/during we
								// refreshed, don't clear it
				}
				if (versionValue.delete()) {
					if (time - versionValue.time() > 60000) {// 大于60秒再删
						versionMap.remove(uid);
					}
				} else {
					versionMap.remove(uid);
				}
			}
		}
	}

	private long loadCurrentVersionFromIndex(Term uid) throws IOException {
		RobinSearcher rs = parentShardCore.getSearcher();
		try {
			return Versions.loadVersion(rs.reader(), uid);
		} finally {
			rs.release();
		}
	}

	private Object dirtyLock(Term uid) {
		return dirtyLock(uid.bytes());
	}

	private Object dirtyLock(BytesRef uid) {
		int hash = DjbHashFunction.DJB_HASH(uid.bytes, uid.offset, uid.length);
		// abs returns Integer.MIN_VALUE, so we need to protect against it...
		if (hash == Integer.MIN_VALUE) {
			hash = 0;
		}
		return dirtyLocks[Math.abs(hash) % dirtyLocks.length];
	}

	private HashedBytesRef versionKey(Term uid) {
		return new HashedBytesRef(uid.bytes());
	}

	@SuppressWarnings({ "null", "unchecked" })
	private Document getDocument(AbstractIndexData data, IndexMetadata metadata, AnteIndexAnalyzer analyzer) throws IOException,
			BusinessException {
		Document doc = new Document();
		String[] sourceFieldNames = metadata.getSrcFieldNames();
		Collection<String> valueFieldNames = data.getFieldNames();
		valueFieldNames.addAll(Arrays.asList(sourceFieldNames));

		Collection<String> fieldNames = valueFieldNames;

		for (String fieldName : fieldNames) {

			IField metaField = metadata.getSrcField(fieldName);
			Object fieldValue = data.getValues(fieldName);

			if (IAnteParaConst.ANTE_DOC_VERSION.equals(fieldName)) {
				IndexableField field = createUidIndexField(fieldName, fieldValue);
				if (field != null)
					doc.add(field);
				continue;
			}

			if (fieldValue == null && (metaField != null && metaField.getCompoundType() == null))
				continue;

			String strategy = null;
			String pinYinType = PinYinType.Nonsupport.getCode();
			int ftypeCode = FieldType.STRING;

			if (metaField != null) {
				strategy = metaField.getStrategy();
				pinYinType = metaField.getPinYinType();
				ftypeCode = FieldType.getTypeCode(metaField.getFieldType());
			}
			// 附加字段的metadata自动加入了源的元数据里
			// else {
			// // 附件字段不从metadata中获得索引策略，在这里手动指定
			// if (fieldName.equalsIgnoreCase(IAnteParaConst.ANTE_ATTACH +
			// IAnteParaConst.UNDERLINE + IAnteParaConst.FILE_NAME)) {
			// // 文件名 采用不分词
			// strategy = "text_MM_CN";
			// } else if (fieldName.equalsIgnoreCase(IAnteParaConst.ANTE_ATTACH
			// + IAnteParaConst.UNDERLINE
			// + IAnteParaConst.FILE_CONTENTS)) {
			// // 文件内容采用mmseg4j
			// strategy = "text_MM_CN";
			// }
			// }

			IndexStrategy indexStrategy = indexSchema.getIndexStrategy(strategy);

			if (isCompoundField(metaField)) {
				List<IField> compFields = metadata.getCompoundTypeFields(metaField.getCompoundType());
				for (IField cf : compFields) {
					String compFieldName = getCompoundFieldName(metaField.getName(), cf.getName());
					Object objValue = data.getFieldValue(compFieldName);
					IndexableField compIndexField = getCompoundField(cf, compFieldName, objValue);
					doc.add(compIndexField);
				}
			} else {
				Collection<Object> value = null;
				if (fieldValue instanceof Collection) {
					value = (Collection<Object>) fieldValue;
				} else {
					value = new ArrayList<Object>();
					value.add(fieldValue);
				}
				if (value != null) {
					// int changeName = 0;
					for (Object eachValue : value) {
						if (eachValue == null) {
							continue;
						}
						// changeName++;
						// String diffName = fieldName + changeName;
						if (!isNumericField(ftypeCode)) {
							if (pinYinType != null && !pinYinType.equals(PinYinType.Nonsupport.getCode())) {
								Reader tokenReader = getTokenReader(eachValue);
								TokenStream ts = analyzer.getTokenStream(fieldName, tokenReader);
								// TokenStream ts =
								// analyzer.tokenStream(diffName, tokenReader);
								TokenStream pyts = TokenStreamFactory.getPYTokenStream(pinYinType, ts);
								Field pyField = createIndexField(indexStrategy, fieldName, eachValue);
								pyField.setTokenStream(pyts);
								doc.add(pyField);
							} else {
								Field field = createIndexField(indexStrategy, fieldName, eachValue);
								Reader fieldReader = getTokenReader(eachValue);
								// TokenStream tokenStream =
								// analyzer.tokenStream(diffName, fieldReader);
								TokenStream tokenStream = analyzer.getTokenStream(fieldName, fieldReader);
								field.setTokenStream(tokenStream);
								doc.add(field);
							}
						} else {
							IndexableField field = createNumericIndexField(indexStrategy, ftypeCode, fieldName, eachValue);
							if (field != null)
								doc.add(field);
						}
					}
				}
			}
		}

		// 根据用户的数据id生成索引里的唯一id——ANTE_UID
		Field field = createUidIndexField(data, metadata);
		doc.add(field);
		// AnteLogger.debug("添加Doc:" + doc);
		return doc;
	}

	private Reader getTokenReader(Object fieldValue) {
		if (fieldValue == null) {
			return new StringReader("");
		}
		Reader tokenReader = null;
		if (fieldValue instanceof Reader) {
			tokenReader = (Reader) fieldValue;
		} else {
			tokenReader = new StringReader(fieldValue.toString());
		}
		return tokenReader;
	}

	private String getCompoundFieldName(String sourceCpFieldName, String compFieldName) {
		return IAnteParaConst.ANTE_PREFIX + sourceCpFieldName + IAnteParaConst.UNDERLINE + compFieldName;
	}

	private boolean isCompoundField(IField f) {
		if (f != null && f.getFieldType() != null && f.getFieldType().equals(FieldType.COMPOUND_TYPE.getName())
				&& f.getCompoundType() != null) {
			return true;
		}
		return false;
	}

	private IndexableField getCompoundField(IField compoundField, String compFieldName, Object compFieldValue)
			throws BusinessException {
		IndexStrategy compStrategy = indexSchema.getIndexStrategy(compoundField.getStrategy());
		int compFieldType = FieldType.getTypeCode(compoundField.getCompoundType());
		IndexableField compIndexField = null;
		if (isNumericField(compFieldType)) {
			compIndexField = createNumericIndexField(compStrategy, compFieldType, compFieldName, compFieldValue);
		} else {
			compIndexField = createCompoundField(compFieldName, compFieldValue);
		}
		return compIndexField;
	}

	private Field createUidIndexField(AbstractIndexData data, IndexMetadata metadata) {

		String fieldName = IAnteParaConst.ANTE_DOC_UID;
		String fieldValue = getAnteUidString(data, metadata);

		Field field = new TextField(fieldName, fieldValue, Field.Store.YES);
		return field;
	}

	private String getAnteUidString(AbstractIndexData data, IndexMetadata metadata) {

		String idField = metadata.getIDFieldName();
		Object fieldValue = data.getFieldValue(idField);
		String source = metadata.getSource();
		String sourceGroup = metadata.getSourceGroup();
		StringBuilder sb = new StringBuilder();
		sb.append(sourceGroup).append("#").append(source).append("#").append(fieldValue);

		return sb.toString();
	}

	private Field createIndexField(IndexStrategy indexStrategy, String fieldName, Object fieldValue) {
		if (fieldValue == null || fieldName == null)
			return null;
		Field field = null;
		if (fieldValue instanceof String) {
			Field.Store stored = null;
			if (fieldName.startsWith(IAnteParaConst.ANTE_CHILD) || fieldName.startsWith(IAnteParaConst.ANTE_ATTACH))
				stored = Field.Store.NO;
			else
				stored = indexStrategy.isStore() ? Field.Store.YES : Field.Store.NO;
			// Field.Index index = Field.Index.toIndex(indexStrategy.isIndex(),
			// indexStrategy.isAnalyzed(), indexStrategy.isStore());
			// // field = new Field(fieldName, fieldValue.toString(), store,
			// index);
			// org.apache.lucene.document.FieldType fieldType = new
			// org.apache.lucene.document.FieldType();
			// fieldType.setStored(false);
			// fieldType.setIndexed(true);
			field = new TextField(fieldName, fieldValue.toString(), stored);
		} else if (fieldValue instanceof Reader) {
			field = new TextField(fieldName, (Reader) fieldValue);
		} else {
			field = new TextField(fieldName, new StringReader(fieldValue.toString()));
		}
		return field;
	}

	@SuppressWarnings("deprecation")
	private IndexableField createCompoundField(String fieldName, Object fieldValue) {
		if (fieldName == null || fieldValue == null)
			return null;
		Field field = null;
		if (fieldValue instanceof String) {
			Field.Store store = Field.Store.NO;
			Field.Index index = Field.Index.toIndex(true, true, false);
			field = new Field(fieldName, fieldValue.toString(), store, index);
		} else if (fieldValue instanceof Reader) {
			field = new Field(fieldName, (Reader) fieldValue);
		}
		return field;
	}

	// private Field createCopyField(String fieldName, Object fieldValue) {
	// if (fieldName == null || fieldValue == null)
	// return null;
	// Field field = null;
	// if (fieldValue instanceof String) {
	// Field.Store store = Field.Store.NO;
	// Field.Index index = Field.Index.toIndex(true, true, false);
	// field = new Field(fieldName, fieldValue.toString(), store, index);
	// } else if (fieldValue instanceof Reader) {
	// field = new Field(fieldName, (Reader) fieldValue);
	// }
	// return field;
	// }

	private IndexableField createUidIndexField(String fieldName, Object externalVal) {
		long longValue = Long.parseLong(externalVal.toString());
		Field f = new NumericDocValuesField(fieldName, longValue);
		return f;
	}

	// 数值型在通过term检索和queryparser转换时存在问题，转换为String类型进行构建
	private IndexableField createNumericIndexField(IndexStrategy indexStrategy, int type, String fieldName, Object externalVal)
			throws BusinessException {
		if (fieldName == null || externalVal == null)
			return null;
		boolean indexed = false;
		boolean stored = false;
		if (fieldName.startsWith(IAnteParaConst.ANTE_CHILD)) {
			indexed = true;
			stored = false;
		} else {
			indexed = indexStrategy.isIndex();
			stored = indexStrategy.isStore();
		}
		if (!indexed && !stored) {
			return null;
		}

		Field f = null;
		switch (type) {

		case FieldType.SHORT:
		case FieldType.INTEGER: {
			int intValue = Integer.parseInt(externalVal.toString());
			// f = new IntField(fieldName, intValue, stored ? Field.Store.YES :
			// Field.Store.NO);
			f = new StringField(fieldName, String.valueOf(intValue), stored ? Field.Store.YES : Field.Store.NO);
		}
			break;

		case FieldType.FLOAT: {
			float floatValue = Float.parseFloat(externalVal.toString());
			// f = new FloatField(fieldName, floatValue, stored ?
			// Field.Store.YES : Field.Store.NO);
			f = new StringField(fieldName, String.valueOf(floatValue), stored ? Field.Store.YES : Field.Store.NO);
		}
			break;

		case FieldType.LONG: {
			long longValue = Long.parseLong(externalVal.toString());
			// f = new LongField(fieldName, longValue, stored ? Field.Store.YES
			// : Field.Store.NO);
			f = new StringField(fieldName, String.valueOf(longValue), stored ? Field.Store.YES : Field.Store.NO);
		}
			break;

		case FieldType.DOUBLE:
		case FieldType.UFDOUBLE: {
			double doubleValue = Double.parseDouble(externalVal.toString());
			// f = new DoubleField(fieldName, doubleValue, stored ?
			// Field.Store.YES : Field.Store.NO);
			f = new StringField(fieldName, String.valueOf(doubleValue), stored ? Field.Store.YES : Field.Store.NO);
		}
			break;

		case FieldType.DATE: {
			Date formateDate = null;
			try {
				formateDate = new SimpleDateFormat("yyyy-MM-dd").parse((String) externalVal);
			} catch (Exception e) {
				try {
					formateDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse((String) externalVal);
				} catch (ParseException e1) {
					AnteLogger.error("only support date style like yyyy-MM-dd or yyyy-MM-dd HH:mm:ss");
					throw new BusinessException("only support date style like yyyy-MM-dd or yyyy-MM-dd HH:mm:ss", e1);
				}
			}
			long longValue = new UFDate(formateDate).toDate().getTime();
			f = new LongField(fieldName, longValue, stored ? Field.Store.YES : Field.Store.NO);
		}
			break;

		case FieldType.UFDATE: {
			long longValue = (new UFDate((String) externalVal)).getMillis();
			f = new LongField(fieldName, longValue, stored ? Field.Store.YES : Field.Store.NO);
		}
			break;

		case FieldType.UFDATETIME: {
			long longValue = (new UFDateTime((String) externalVal)).getMillis();
			f = new LongField(fieldName, longValue, stored ? Field.Store.YES : Field.Store.NO);
		}
			break;

		case FieldType.UFTIME: {
			long longValue = (new UFTime((String) externalVal)).getMillis();
			f = new LongField(fieldName, longValue, stored ? Field.Store.YES : Field.Store.NO);
		}
			break;

		case FieldType.BIGDECIMAL:
			try {
				BigDecimal decimalValue = (BigDecimal) externalVal;
				long indexValue = decimalValue.multiply(BIGDECIMAL_STORE_FACTOR).longValue();
				// f = new LongField(fieldName, indexValue, stored ?
				// Field.Store.YES : Field.Store.NO);
				f = new StringField(fieldName, String.valueOf(indexValue), stored ? Field.Store.YES : Field.Store.NO);
			} catch (Exception e) {
				BigDecimal decimalValue = new BigDecimal(externalVal.toString());
				long indexValue = decimalValue.multiply(BIGDECIMAL_STORE_FACTOR).longValue();
				f = new LongField(fieldName, indexValue, stored ? Field.Store.YES : Field.Store.NO);
			}

			break;

		default:
			return null;
		}

		// f.setOmitNorms(indexStrategy.omitNorms());
		// f.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
		return f;
	}

	private boolean isNumericField(int fieldType) {
		switch (fieldType) {
		case FieldType.SHORT:
		case FieldType.INTEGER:
		case FieldType.FLOAT:
		case FieldType.LONG:
		case FieldType.DOUBLE:
		case FieldType.UFDOUBLE:
		case FieldType.DATE:
		case FieldType.UFDATE:
		case FieldType.UFDATETIME:
		case FieldType.UFTIME:
		case FieldType.BIGDECIMAL:
			return true;
		default:
			return false;
		}
	}

	private AnteIndexAnalyzer getAnteIndexAnalyzer(List<IField> fields) {
		AnteIndexAnalyzer analyzer = new AnteIndexAnalyzer(indexSchema, fields);
		return analyzer;
	}

	/**
	 * 更新中文扩展分词词库 TODO 每次都调用是不是不太好？
	 * 
	 * @param analyzer
	 * @param dataSource
	 */
	private void updateExtendDic(AnteIndexAnalyzer analyzer, String dataSource) {
		Collection<Analyzer> analyzersCol = analyzer.getAnalyzers().values();
		Iterator<Analyzer> it = analyzersCol.iterator();
		while (it.hasNext()) {
			Analyzer an = it.next();
			if (an instanceof TokenizerChain) {
				TokenizerFactory tokenFactory = ((TokenizerChain) an).getTokenizerFactory();
				if (tokenFactory instanceof MMSegExtendTokenizerFactory) {
					MMSegExtendTokenizerFactory extendFactory = ((MMSegExtendTokenizerFactory) tokenFactory);
					extendFactory.updateExtendDict(dataSource);
				}
			}
		}
	}

	// add by liumiaob
	public IndexWriter getIndexWriter() {
		return this.writer;
	}

	public boolean isOpen() {
		if (writer == null)
			return false;
		Directory testDir = null;
		try {
			testDir = writer.getDirectory();
		} catch (AlreadyClosedException e) {
			return false;
		} catch (Exception e) {
			return false;
		}
		if (testDir != null)
			return true;
		return false;
	}

	public void optimize() {
		try {
			writer.deleteUnusedFiles();
			writer.forceMerge(10, true);
		} catch (CorruptIndexException e) {
			AnteLogger.error(e.getMessage(), e);
		} catch (IOException e) {
			AnteLogger.error(e.getMessage(), e);
		}
	}

	public void forceMergeDeletes() throws IOException {
		writer.forceMergeDeletes();
	}

	public void forceMerge(int maxNumSegments) throws IOException {
		writer.forceMerge(maxNumSegments);
	}

	static class VersionValue {
		private final long version;
		private final boolean delete;
		private final long time;
		private final Translog.Location translogLocation;

		VersionValue(long version, boolean delete, long time, Translog.Location translogLocation) {
			this.version = version;
			this.delete = delete;
			this.time = time;
			this.translogLocation = translogLocation;
		}

		public long time() {
			return this.time;
		}

		public long version() {
			return version;
		}

		public boolean delete() {
			return delete;
		}

		public Translog.Location translogLocation() {
			return this.translogLocation;
		}
	}

	// /**
	// * 这个是为了进行测试而写的方法，暂时查看分词效果
	// * 本方法在运行环境调用时，会影响索引，长生问题。
	// * 不清楚为什么在finally 中的 ts.reset 方法不好使
	// * @param con
	// * @param ts
	// */
	// private void forDebug( String fileName, String str , TokenStream ts ){
	// /*
	// StringBuilder sb = new StringBuilder();
	// sb.append( fileName ).append( ":" )
	// .append( str ).append( ":" );
	// try {
	// ts.reset();
	// while ( ts.incrementToken() ) {
	// CharTermAttribute ta = ts.getAttribute(CharTermAttribute.class);
	// sb.append( "[" ).append( ta.toString() ).append( "]" );
	// }
	// AnteLogger.debug("分词：word segmentation：" + sb.toString());
	// } catch (Exception e) {
	// AnteLogger.error("分词：word segmentation error:" + str,e);
	// } finally {
	// try {
	// ts.reset();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// */
	// }
	// private String gettokens(TokenStream ts) throws IOException {
	// List<AttributeSource> tokens = new ArrayList<AttributeSource>();
	// ts.reset();
	// while (ts.incrementToken()) {
	// tokens.add(ts.cloneAttributes());
	// }
	// StringBuffer sb = new StringBuffer("");
	// if (tokens != null) {
	// for (AttributeSource aSource : tokens) {
	//
	// sb.append(" [ " +
	// aSource.addAttribute(CharTermAttribute.class).toString() + " ] ");
	// }
	// }
	// return sb.toString();
	// }
}
