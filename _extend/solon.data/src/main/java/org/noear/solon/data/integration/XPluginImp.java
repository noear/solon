package org.noear.solon.data.integration;

import org.noear.solon.SolonApp;
import org.noear.solon.core.*;
import org.noear.solon.data.annotation.*;
import org.noear.solon.data.cache.CacheServiceEventListener;
import org.noear.solon.data.cache.CacheLib;
import org.noear.solon.data.cache.CacheService;
import org.noear.solon.data.cache.CacheServiceDefault;
import org.noear.solon.data.tran.TranExecutor;
import org.noear.solon.data.around.CacheInterceptor;
import org.noear.solon.data.around.CachePutInterceptor;
import org.noear.solon.data.around.CacheRemoveInterceptor;
import org.noear.solon.data.around.TranInterceptor;
import org.noear.solon.data.tran.TranExecutorImp;

public class XPluginImp implements Plugin {
    @Override
    public void start(SolonApp app) {
        if (app.enableTransaction()) {
            Aop.wrapAndPut(TranExecutor.class, TranExecutorImp.global);
        }

        if (app.enableCaching()) {
            CacheLib.cacheServiceAddIfAbsent("", CacheServiceDefault.instance);

            app.onEvent(BeanWrap.class, new CacheServiceEventListener());
        }

        Aop.context().beanOnloaded(() -> {
            if (Aop.has(CacheService.class) == false) {
                Aop.wrapAndPut(CacheService.class, CacheServiceDefault.instance);
            }
        });

        Aop.context().beanAroundAdd(CachePut.class, new CachePutInterceptor(), 110);
        Aop.context().beanAroundAdd(CacheRemove.class, new CacheRemoveInterceptor(), 110);
        Aop.context().beanAroundAdd(Cache.class, new CacheInterceptor(), 111);
        Aop.context().beanAroundAdd(Tran.class, new TranInterceptor(), 120);
    }
}
