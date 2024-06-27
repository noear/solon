package org.noear.solon;

import org.noear.solon.annotation.Import;
import org.noear.solon.core.*;
import org.noear.solon.core.convert.ConverterManager;
import org.noear.solon.core.event.EventListener;
import org.noear.solon.core.event.*;
import org.noear.solon.core.exception.StatusException;
import org.noear.solon.core.handle.*;
import org.noear.solon.core.route.RouterWrapper;
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.core.util.ConsumerEx;
import org.noear.solon.core.util.LogUtil;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


/**
 * 应用管理中心
 *
 * <pre><code>
 * @SolonMain
 * public class DemoApp{
 *     public static void main(String[] args){
 *         Solon.start(DemoApp.class, args);
 *     }
 * }
 * </code></pre>
 *
 * @author noear
 * @since 1.0
 */
public class SolonApp extends RouterWrapper {
    private final SolonProps _cfg; //属性配置
    private final AppContext _context;//容器上下文
    private final ConverterManager _converterManager; //转换管理器

    private final Class<?> _source; //应用加载源
    private final URL _sourceLocation;
    private final long _startupTime;

    protected boolean stopped = false;

    /**
     * 应用上下文
     */
    @Override
    public AppContext context() {
        return _context;
    }

    /**
     * 转换管理器
     * */
    public ConverterManager converterManager() {
        return _converterManager;
    }

    /**
     * 工厂管理器
     * */
    public FactoryManager factoryManager(){
        return FactoryManager.getGlobal();
    }

    /**
     * 应用属性（或配置）
     */
    public SolonProps cfg() {
        return _cfg;
    }

    protected SolonApp(Class<?> source, NvMap args) throws Exception {
        //添加启动类检测
        if (source == null) {
            throw new IllegalArgumentException("The startup class parameter('source') cannot be null");
        }
        _startupTime = System.currentTimeMillis();
        _source = source;
        _sourceLocation = source.getProtectionDomain().getCodeSource().getLocation();
        _converterManager = new ConverterManager();


        //添加启动类包名检测
        if (source.getPackage() == null || Utils.isEmpty(source.getPackage().getName())) {
            throw new IllegalStateException("The startup class is missing package: " + source.getName());
        }

        //初始化配置
        _cfg = new SolonProps(this, args);
        _context = new AppContext(new AppClassLoader(AppClassLoader.global()), _cfg);

        //初始化路由
        initRouter(this::doFilter);

        _handler = routerHandler();
    }


    /**
     * 启动
     */
    protected void start(ConsumerEx<SolonApp> initialize) throws Throwable {
        //1.0.打印构造时的告警
        if(_cfg.warns.size() > 0) {
            for (String warn : _cfg.warns) {
                LogUtil.global().warn(warn);
            }
        }

        //2.0.内部初始化等待（尝试ping等待）
        initAwait();

        //2.1.内部初始化（如配置等，顺序不能乱）
        init(initialize);

        //3.运行应用（运行插件、扫描Bean等）
        run();
    }

    /**
     * 初始化等待
     */
    private void initAwait() throws Throwable {
        String addr = cfg().get("solon.start.ping");

        if (Utils.isNotEmpty(addr)) {
            try {
                while (true) {
                    if (Utils.ping(addr)) {
                        LogUtil.global().info("App: Start ping succeed: " + addr);
                        Thread.sleep(1000); //成功也再等1s
                        break;
                    } else {
                        LogUtil.global().warn("App: Start ping failure: " + addr);
                        Thread.sleep(2000);
                    }
                }
            } catch (Throwable e) {
                throw new IllegalStateException(e);
            }
        }
    }

    /**
     * 初始化（不能合在构建函数里）
     */
    private void init(ConsumerEx<SolonApp> initialize) throws Throwable {
        List<ClassLoader> loaderList;

        //1.尝试加载扩展文件夹
        String filterStr = cfg().extendFilter();
        if (Utils.isEmpty(filterStr)) {
            //不需要过滤
            loaderList = ExtendLoader.load(cfg().extend(), false);
        } else {
            //增加过滤
            String[] filterS = filterStr.split(",");
            loaderList = ExtendLoader.load(cfg().extend(), false, (path) -> {
                for (String f : filterS) {
                    if (path.contains(f)) {
                        return true;
                    }
                }

                return false;
            });
        }


        //2.尝试扫描插件
        cfg().plugsScan(loaderList);


        //3.运行自定义初始化
        if (initialize != null) {
            initialize.accept(this);
        }
    }


    /**
     * 运行应用
     */
    private void run() throws Throwable {

        //event::0.x.推送App init end事件
        EventBus.publish(new AppInitEndEvent(this));

        List<PluginEntity> plugs = cfg().plugs();
        //1.0.尝式初始化插件 //一般插件不需要
        for (int i = 0, len = plugs.size(); i < len; i++) {
            if (Solon.cfg().isDebugMode()) {
                LogUtil.global().info("App: plugin init: " + plugs.get(i).getClassName());
            }
            plugs.get(i).init(context());
        }

        //event::1.0.x推送Plugin init end事件
        EventBus.publish(new AppPluginInitEndEvent(this));

        LogUtil.global().info("App: Plugin starting");

        //1.1.尝试启动插件（顺序不能乱） //不能用forEach，以免当中有插进来
        for (int i = 0, len = plugs.size(); i < len; i++) {
            plugs.get(i).start(context());
        }

        //1.2.检查配置是否完全适配
        cfg().complete();

        //event::1.3.推送Plugin load end事件
        EventBus.publish(new AppPluginLoadEndEvent(this));


        LogUtil.global().info("App: Bean scanning");

        //2.1.通过注解导入bean（一般是些配置器）
        beanImportTry();

        //2.2.通过源扫描bean
        if (source() != null) {
            context().beanScan(source());
        }

        //event::2.x.推送Bean load end事件
        EventBus.publish(new AppBeanLoadEndEvent(this));

        //3.加载渲染关系
        Map<String, String> map = cfg().getMap("solon.view.mapping.");
        map.forEach((k, v) -> {
            RenderManager.mapping("." + k, v);
        });

        //3.1.尝试设置 context-path
        if (Utils.isNotEmpty(Solon.cfg().serverContextPath())) {
            Solon.app().filterIfAbsent(-99, new ContextPathFilter());
        }

        //3.2.标识上下文加载完成
        context().start();

        //event::4.x.推送App load end事件
        EventBus.publish(new AppLoadEndEvent(this));
    }

    //通过注解，导入bean
    protected void beanImportTry() {
        if (_source == null) {
            return;
        }

        for (Annotation a1 : _source.getAnnotations()) {
            if (a1 instanceof Import) {
                context().beanImport((Import) a1);
            } else {
                context().beanImport(a1.annotationType().getAnnotation(Import.class));
            }
        }
    }


    //////////////////////////////////

    private final Map<Integer, Signal> signals = new LinkedHashMap<>();


    /**
     * 添加信号
     */
    public void signalAdd(Signal instance) {
        signals.putIfAbsent(instance.port(), instance);
    }

    /**
     * 获取信号
     *
     * @param port 端口
     */
    public Signal signalGet(int port) {
        return signals.get(port);
    }

    /**
     * 获取信号记录
     */
    public Collection<Signal> signals() {
        return Collections.unmodifiableCollection(signals.values());
    }


    //////////////////////////////////


    /**
     * 共享变量（一般用于插件之间）
     */
    private final Set<BiConsumer<String, Object>> _onSharedAdd_event = new HashSet<>();
    private final Map<String, Object> _shared = new HashMap<>();
    private Map<String, Object> _shared_unmod;

    /**
     * 类加载器
     */
    public ClassLoader classLoader() {
        return context().getClassLoader();
    }

    /**
     * 添加共享对象
     */
    public void sharedAdd(String key, Object obj) {
        _shared.put(key, obj);
        _onSharedAdd_event.forEach(fun -> {
            fun.accept(key, obj);
        });
    }

    /**
     * 获取共享对象（异步获取）
     */
    public <T> void sharedGet(String key, Consumer<T> event) {
        Object tmp = _shared.get(key);
        if (tmp != null) {
            event.accept((T) tmp);
        } else {
            onSharedAdd((k, v) -> {
                if (k.equals(key)) {
                    event.accept((T) v);
                }
            });
        }
    }

    /**
     * 共享对象添加事件
     */
    public void onSharedAdd(BiConsumer<String, Object> event) {
        _onSharedAdd_event.add(event);
    }

    /**
     * 共享对象
     */
    public Map<String, Object> shared() {
        if (_shared_unmod == null) {
            _shared_unmod = Collections.unmodifiableMap(_shared);
        }

        return _shared_unmod;
    }


    /**
     * 从启动开启已运行时间
     */
    protected long elapsedTimes() {
        return System.currentTimeMillis() - _startupTime;
    }


    /**
     * 启动入口类
     */
    public Class<?> source() {
        return _source;
    }

    /**
     * 启动入口类所在位置
     */
    public URL sourceLocation(){
        return _sourceLocation;
    }

    /**
     * 插入插件
     */
    @Deprecated
    public void plug(Plugin plugin) {
        PluginEntity p = new PluginEntity(plugin);
        p.init(context());
        p.start(context());
        cfg().plugs().add(p);
    }

    /**
     * 添加插件（只有执行前添加才有效）
     *
     * @param priority 优先级（越大越优化）
     * @param plugin   插件
     * @deprecated 2.2
     */
    @Deprecated
    public void pluginAdd(int priority, Plugin plugin) {
        PluginEntity p = new PluginEntity(plugin, priority);
        cfg().plugs().add(p);
        cfg().plugsSort();
    }

    /**
     * Solon Handler
     */
    private Handler _handler = null;

    /**
     * 处理器获取
     * */
    public Handler handlerGet() {
        return _handler;
    }

    /**
     * 处理器设置
     * */
    public void handlerSet(Handler handler) {
        if (handler != null) {
            _handler = handler;
        }
    }


    /**
     * 应用请求处理入口(异常时，自动500处理)
     */
    public void tryHandle(Context x) {
        try {
            //设置当前线程上下文
            ContextUtil.currentSet(x);

            if (stopped) {
                x.status(503);
            } else {
                chainManager().doFilter(x);

                //todo: 改由 HttpException 处理（不会到这里来了）
//                if (x.getHandled() == false) { //@since: 1.9
//                    if (x.status() <= 200 && x.mainHandler() == null) {//@since: 1.10
//                        int statusPreview = x.statusPreview(); //支持405  //@since: 2.5
//                        if (statusPreview > 0) {
//                            x.status(statusPreview);
//                        } else {
//                            x.status(404);
//                        }
//                    }
//                    //x.setHandled(true);  //todo: 不能加，对websocket有影响
//                }
            }

            //40x,50x...
            doStatus(x);
        } catch (Throwable ex) {
            ex = Utils.throwableUnwrap(ex);

            //如果未处理，尝试处理
            if(ex instanceof StatusException){
                x.status(((StatusException) ex).getCode());
            }else {
                //推送异常事件 //todo: Action -> Gateway? -> RouterHandler -> Filter -> SolonApp!
                LogUtil.global().warn("SolonApp tryHandle failed!", ex);

                if (x.getHandled() == false) {
                    if (x.status() < 400) {
                        x.status(500);
                    }
                    //x.setHandled(true); 不再需要
                }
            }

            //如果未渲染，尝试渲染
            if (x.getRendered() == false) {
                //40x,50x...
                try {
                    if (doStatus(x) == false) {
                        if (Solon.cfg().isDebugMode()) {
                            x.output(ex);
                        }
                    }
                } catch (RuntimeException e) {
                    throw e;
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        } finally {
            //移除当前线程上下文
            ContextUtil.currentRemove();
        }
    }

    protected void doFilter(Context x, FilterChain chain) throws Throwable {
        _handler.handle(x);
    }

    protected boolean doStatus(Context x) throws Throwable {
        if (x.status() >= 400 && _statusHandlers.size() > 0) {
            Handler h = _statusHandlers.get(x.status());
            if (h != null) {
                x.status(200);
                x.setHandled(true);
                h.handle(x);
                return true;
            }
        }

        return false;
    }

    /**
     * 订阅事件
     */
    public <T> SolonApp onEvent(Class<T> type, EventListener<T> handler) {
        EventBus.subscribe(type, handler);
        return this;
    }

    /**
     * 订阅事件
     */
    public <T> SolonApp onEvent(Class<T> type, int index, EventListener<T> handler) {
        EventBus.subscribe(type, index, handler);
        return this;
    }

    private Map<Integer, Handler> _statusHandlers = new HashMap<>();

    /**
     * 订阅异常状态
     */
    public SolonApp onStatus(Integer code, Handler handler) {
        _statusHandlers.put(code, handler);
        return this;
    }


    /**
     * 锁住线程（如果有需要，建议在启动程序的最后调用）
     */
    public void block() throws InterruptedException {
        Thread.currentThread().join();
    }


    private boolean _enableHttp = true; //与函数同名，_开头

    /**
     * 是否已启用 Http 信号接入
     */
    public boolean enableHttp() {
        return _enableHttp && NativeDetector.isNotAotRuntime();
    }

    /**
     * 启用 Http 信号接入
     */
    public SolonApp enableHttp(boolean enable) {
        _enableHttp = enable;
        return this;
    }

    private boolean _enableWebSocket = false;

    public boolean enableWebSocket() {
        return _enableWebSocket && NativeDetector.isNotAotRuntime();
    }

    /**
     * 启用 WebSocket 信号接入
     *
     * @param enable 是否启用
     */
    public SolonApp enableWebSocket(boolean enable) {
        _enableWebSocket = enable;
        return this;
    }


    private boolean _enableSocketD = false;

    /**
     * 是否已启用 SocketD 信号接入
     */
    public boolean enableSocketD() {
        return _enableSocketD && NativeDetector.isNotAotRuntime();
    }

    /**
     * 启用 SocketD 信号接入
     *
     * @param enable 是否启用
     */
    public SolonApp enableSocketD(boolean enable) {
        _enableSocketD = enable;
        return this;
    }


    private boolean _enableTransaction = true;

    /**
     * 是否已启用事务
     */
    public boolean enableTransaction() {
        return _enableTransaction;
    }

    /**
     * 启用事务
     *
     * @param enable 是否启用
     */
    public SolonApp enableTransaction(boolean enable) {
        _enableTransaction = enable;
        return this;
    }

    private boolean _enableCaching = true;

    /**
     * 是否已启用缓存
     */
    public boolean enableCaching() {
        return _enableCaching;
    }

    /**
     * 启用缓存
     *
     * @param enable 是否启用
     */
    public SolonApp enableCaching(boolean enable) {
        _enableCaching = enable;
        return this;
    }

    private boolean _enableStaticfiles = true;

    /**
     * 是否已启用静态文件服务
     */
    public boolean enableStaticfiles() {
        return _enableStaticfiles;
    }

    /**
     * 启用静态文件服务
     *
     * @param enable 是否启用
     */
    public SolonApp enableStaticfiles(boolean enable) {
        _enableStaticfiles = enable;
        return this;
    }


    private boolean _enableSessionState = true;

    /**
     * 是否已启用会话状态
     */
    public boolean enableSessionState() {
        return _enableSessionState;
    }

    /**
     * 启用会话状态
     *
     * @param enable 是否启用
     */
    public SolonApp enableSessionState(boolean enable) {
        _enableSessionState = enable;
        return this;
    }
}
