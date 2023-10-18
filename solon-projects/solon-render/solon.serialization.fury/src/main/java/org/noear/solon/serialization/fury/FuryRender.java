package org.noear.solon.serialization.fury;

import io.fury.Fury;
import io.fury.ThreadSafeFury;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.SerializationConfig;

import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

//不要要入参，方便后面多视图混用
//
public class FuryRender implements Render {
    static ThreadSafeFury fury = Fury.builder().requireClassRegistration(false).buildThreadSafeFury();

    @Override
    public String renderAndReturn(Object data, Context ctx) throws Throwable {
        byte[] bytes = null;
        if (data instanceof ModelAndView) {
            bytes = serializeDo(new LinkedHashMap((Map) data));
        } else {
            bytes = serializeDo(data);
        }

        return Base64.getEncoder().encodeToString(bytes);
    }

    @Override
    public void render(Object obj, Context ctx) throws Throwable {
        if (SerializationConfig.isOutputMeta()) {
            ctx.headerAdd("solon.serialization", "FuryRender");
        }

        ctx.contentType("application/fury");

        if (obj instanceof ModelAndView) {
            ctx.output(serializeDo(new LinkedHashMap((Map) obj)));
        } else {
            ctx.output(serializeDo(obj));
        }
    }

    private byte[] serializeDo(Object obj) throws Throwable {
        return fury.serialize(obj);
    }
}