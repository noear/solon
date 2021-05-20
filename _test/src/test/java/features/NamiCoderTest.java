package features;

import model.UserModel;
import org.junit.Test;
import org.noear.nami.coder.fastjson.FastjsonDecoder;
import org.noear.nami.coder.fastjson.FastjsonEncoder;
import org.noear.nami.coder.hession.HessianDecoder;
import org.noear.nami.coder.hession.HessianEncoder;
import org.noear.nami.coder.jackson.JacksonDecoder;
import org.noear.nami.coder.jackson.JacksonEncoder;
import org.noear.nami.coder.protostuff.ProtostuffDeoder;
import org.noear.nami.coder.protostuff.ProtostuffEncoder;
import org.noear.nami.coder.snack3.SnackDecoder;
import org.noear.nami.coder.snack3.SnackEncoder;
import org.noear.nami.common.Result;
import org.noear.snack.ONode;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author noear 2021/5/20 created
 */
public class NamiCoderTest {
    String json_err = "{\"@type\":\"java.lang.IllegalArgumentException\",\"localizedMessage\":\"coin_type,amount,tran_num\",\"message\":\"coin_type,amount,tran_num\",\"stackTrace\":[{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.srww.uapi.validation.ValidatorFailureHandlerSrwwImp\",\"fileName\":\"ValidatorFailureHandlerSrwwImp.java\",\"lineNumber\":51,\"methodName\":\"onFailure\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.solon.extend.validation.ValidatorManager\",\"fileName\":\"ValidatorManager.java\",\"lineNumber\":200,\"methodName\":\"failureDo\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.solon.extend.validation.ValidatorManager\",\"fileName\":\"ValidatorManager.java\",\"lineNumber\":186,\"methodName\":\"validateDo\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.solon.extend.validation.ValidatorManager\",\"fileName\":\"ValidatorManager.java\",\"lineNumber\":160,\"methodName\":\"validate\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.solon.extend.validation.ValidatorManager\",\"fileName\":\"ValidatorManager.java\",\"lineNumber\":146,\"methodName\":\"handle\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.solon.extend.validation.ContextValidateInterceptor\",\"fileName\":\"ContextValidateInterceptor.java\",\"lineNumber\":16,\"methodName\":\"handle\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.solon.core.handle.Action\",\"fileName\":\"Action.java\",\"lineNumber\":184,\"methodName\":\"lambda$invoke0$0\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.solon.core.handle.Action\",\"fileName\":\"Action.java\",\"lineNumber\":239,\"methodName\":\"handleDo\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.solon.core.handle.Action\",\"fileName\":\"Action.java\",\"lineNumber\":182,\"methodName\":\"invoke0\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.solon.core.handle.Action\",\"fileName\":\"Action.java\",\"lineNumber\":161,\"methodName\":\"invoke\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.solon.core.handle.Action\",\"fileName\":\"Action.java\",\"lineNumber\":140,\"methodName\":\"handle\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.solon.core.route.RouterHandler\",\"fileName\":\"RouterHandler.java\",\"lineNumber\":68,\"methodName\":\"handleOne\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.solon.core.route.RouterHandler\",\"fileName\":\"RouterHandler.java\",\"lineNumber\":42,\"methodName\":\"handle\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.solon.SolonApp\",\"fileName\":\"SolonApp.java\",\"lineNumber\":501,\"methodName\":\"doFilter\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.solon.core.handle.FilterChainNode\",\"fileName\":\"FilterChainNode.java\",\"lineNumber\":23,\"methodName\":\"doFilter\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.solon.SolonApp\",\"fileName\":\"SolonApp.java\",\"lineNumber\":490,\"methodName\":\"doHandle\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.solon.SolonApp\",\"fileName\":\"SolonApp.java\",\"lineNumber\":512,\"methodName\":\"tryHandle\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.noear.solon.extend.servlet.SolonServletHandler\",\"fileName\":\"SolonServletHandler.java\",\"lineNumber\":30,\"methodName\":\"service\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"javax.servlet.http.HttpServlet\",\"fileName\":\"HttpServlet.java\",\"lineNumber\":750,\"methodName\":\"service\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.servlet.ServletHolder\",\"fileName\":\"ServletHolder.java\",\"lineNumber\":791,\"methodName\":\"handle\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.servlet.ServletHandler\",\"fileName\":\"ServletHandler.java\",\"lineNumber\":550,\"methodName\":\"doHandle\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.server.handler.ScopedHandler\",\"fileName\":\"ScopedHandler.java\",\"lineNumber\":233,\"methodName\":\"nextHandle\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.server.session.SessionHandler\",\"fileName\":\"SessionHandler.java\",\"lineNumber\":1624,\"methodName\":\"doHandle\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.server.handler.ScopedHandler\",\"fileName\":\"ScopedHandler.java\",\"lineNumber\":233,\"methodName\":\"nextHandle\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.server.handler.ContextHandler\",\"fileName\":\"ContextHandler.java\",\"lineNumber\":1435,\"methodName\":\"doHandle\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.server.handler.ScopedHandler\",\"fileName\":\"ScopedHandler.java\",\"lineNumber\":188,\"methodName\":\"nextScope\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.servlet.ServletHandler\",\"fileName\":\"ServletHandler.java\",\"lineNumber\":501,\"methodName\":\"doScope\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.server.session.SessionHandler\",\"fileName\":\"SessionHandler.java\",\"lineNumber\":1594,\"methodName\":\"doScope\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.server.handler.ScopedHandler\",\"fileName\":\"ScopedHandler.java\",\"lineNumber\":186,\"methodName\":\"nextScope\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.server.handler.ContextHandler\",\"fileName\":\"ContextHandler.java\",\"lineNumber\":1350,\"methodName\":\"doScope\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.server.handler.ScopedHandler\",\"fileName\":\"ScopedHandler.java\",\"lineNumber\":141,\"methodName\":\"handle\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.server.handler.HandlerWrapper\",\"fileName\":\"HandlerWrapper.java\",\"lineNumber\":127,\"methodName\":\"handle\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.server.Server\",\"fileName\":\"Server.java\",\"lineNumber\":516,\"methodName\":\"handle\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.server.HttpChannel\",\"fileName\":\"HttpChannel.java\",\"lineNumber\":388,\"methodName\":\"lambda$handle$1\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.server.HttpChannel\",\"fileName\":\"HttpChannel.java\",\"lineNumber\":633,\"methodName\":\"dispatch\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.server.HttpChannel\",\"fileName\":\"HttpChannel.java\",\"lineNumber\":380,\"methodName\":\"handle\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.server.HttpConnection\",\"fileName\":\"HttpConnection.java\",\"lineNumber\":279,\"methodName\":\"onFillable\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.io.AbstractConnection$ReadCallback\",\"fileName\":\"AbstractConnection.java\",\"lineNumber\":311,\"methodName\":\"succeeded\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.io.FillInterest\",\"fileName\":\"FillInterest.java\",\"lineNumber\":105,\"methodName\":\"fillable\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.io.ChannelEndPoint$1\",\"fileName\":\"ChannelEndPoint.java\",\"lineNumber\":104,\"methodName\":\"run\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.util.thread.QueuedThreadPool\",\"fileName\":\"QueuedThreadPool.java\",\"lineNumber\":882,\"methodName\":\"runJob\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"classLoaderName\":\"app\",\"className\":\"org.eclipse.jetty.util.thread.QueuedThreadPool$Runner\",\"fileName\":\"QueuedThreadPool.java\",\"lineNumber\":1036,\"methodName\":\"run\",\"nativeMethod\":false},{\"@type\":\"java.lang.StackTraceElement\",\"className\":\"java.lang.Thread\",\"fileName\":\"Thread.java\",\"lineNumber\":832,\"methodName\":\"run\",\"moduleName\":\"java.base\",\"moduleVersion\":\"14.0.2\",\"nativeMethod\":false}]}";
    String json_usr = "{\"id\":1,\"name\":\"noear\",\"sex\":1}";
    String json_usr_ary = "[{\"@type\":\"model.UserModel\",\"id\":1,\"name\":\"noear\",\"sex\":1}]";

    @Test
    public void test_snack3() {
        //err
        Result err_rst = new Result(200, json_err.getBytes(StandardCharsets.UTF_8));
        try {
            SnackDecoder.instance.decode(err_rst, UserModel.class);
            assert false;
        } catch (Throwable e) {
            assert e instanceof IllegalArgumentException;
            System.out.println("test_snack3::ok");
        }

        //bean
        Result usr_rst = new Result(200, json_usr.getBytes(StandardCharsets.UTF_8));
        Object usr_obj = SnackDecoder.instance.decode(usr_rst, UserModel.class);

        assert usr_obj instanceof UserModel;
        assert ((UserModel) usr_obj).id == 1;


        //bean list
        Result usr_rst_ary = new Result(200, json_usr_ary.getBytes(StandardCharsets.UTF_8));
        Object usr_obj_ary = SnackDecoder.instance.decode(usr_rst_ary, List.class);

        assert usr_obj_ary instanceof List;
        assert ((List<?>) usr_obj_ary).size()==1;


        //null
        usr_rst = new Result(200, SnackEncoder.instance.encode(null));
        usr_obj = SnackDecoder.instance.decode(usr_rst, UserModel.class);

        assert usr_obj == null;
    }

    @Test
    public void test_fastjson() {
        //err
        Result err_rst = new Result(200, json_err.getBytes(StandardCharsets.UTF_8));
        try {
            FastjsonDecoder.instance.decode(err_rst, UserModel.class);
            assert false;
        } catch (Throwable e) {
            assert e instanceof IllegalArgumentException;
            System.out.println("test_fastjson::ok");
        }

        //bean
        Result usr_rst = new Result(200, json_usr.getBytes(StandardCharsets.UTF_8));
        Object usr_obj = FastjsonDecoder.instance.decode(usr_rst, UserModel.class);

        assert usr_obj instanceof UserModel;
        assert ((UserModel) usr_obj).id == 1;

        //bean list
        Result usr_rst_ary = new Result(200, json_usr_ary.getBytes(StandardCharsets.UTF_8));
        Object usr_obj_ary = FastjsonDecoder.instance.decode(usr_rst_ary, List.class);

        assert usr_obj_ary instanceof List;
        assert ((List<?>) usr_obj_ary).size()==1;


        //null
        usr_rst = new Result(200, FastjsonEncoder.instance.encode(null));
        usr_obj = FastjsonDecoder.instance.decode(usr_rst, UserModel.class);

        assert usr_obj == null;
    }

    @Test
    public void test_jackjson() {
        //err
        IllegalArgumentException err = ONode.deserialize(json_err);
        Result err_rst = new Result(200, JacksonEncoder.instance.encode(err));
        try {
            JacksonDecoder.instance.decode(err_rst, UserModel.class);
            assert false;
        } catch (Throwable e) {
            assert e instanceof RuntimeException;
            System.out.println("test_jackjson::ok");
        }

        //bean
        Result usr_rst = new Result(200, json_usr.getBytes(StandardCharsets.UTF_8));
        Object usr_obj = JacksonDecoder.instance.decode(usr_rst, UserModel.class);

        assert usr_obj instanceof UserModel;
        assert ((UserModel) usr_obj).id == 1;

        //bean list //不支持
//        List<UserModel> usr_ary = ONode.deserialize(json_usr_ary);
//        Result usr_rst_ary = new Result(200, JacksonEncoder.instance.encode(usr_ary));
//        Object usr_obj_ary = JacksonDecoder.instance.decode(usr_rst_ary, List.class);
//
//        assert usr_obj_ary instanceof List;
//        assert ((List<?>) usr_obj_ary).size()==1;


        //null
        usr_rst = new Result(200, JacksonEncoder.instance.encode(null));
        usr_obj = JacksonDecoder.instance.decode(usr_rst, UserModel.class);

        assert usr_obj == null;
    }

    @Test
    public void test_hessian() {
        //err
        IllegalArgumentException err = ONode.deserialize(json_err);
        Result err_rst = new Result(200, HessianEncoder.instance.encode(err));
        try {
            HessianDecoder.instance.decode(err_rst, UserModel.class);
            assert false;
        } catch (Throwable e) {
            assert e instanceof IllegalArgumentException;
            System.out.println("test_hessian::ok");
        }


        //bean
        UserModel usr = ONode.deserialize(json_usr, UserModel.class);

        Result usr_rst = new Result(200, HessianEncoder.instance.encode(usr));
        Object usr_obj = HessianDecoder.instance.decode(usr_rst, UserModel.class);

        assert usr_obj instanceof UserModel;
        assert ((UserModel) usr_obj).id == 1;

        //bean list
        List<UserModel> usr_ary = ONode.deserialize(json_usr_ary);
        Result usr_rst_ary = new Result(200, HessianEncoder.instance.encode(usr_ary));
        Object usr_obj_ary = HessianDecoder.instance.decode(usr_rst_ary, List.class);

        assert usr_obj_ary instanceof List;
        assert ((List<?>) usr_obj_ary).size()==1;


        //null
        usr_rst = new Result(200, HessianEncoder.instance.encode(null));
        usr_obj = HessianDecoder.instance.decode(usr_rst, UserModel.class);

        assert usr_obj == null;
    }

    @Test
    public void test_protostuff() {
        //err
        IllegalArgumentException err = ONode.deserialize(json_err);
        Result err_rst = new Result(200, ProtostuffEncoder.instance.encode(err));
        try {
            ProtostuffDeoder.instance.decode(err_rst, UserModel.class);
            assert false;
        } catch (Throwable e) {
            assert e instanceof IllegalArgumentException;
            System.out.println("test_protostuff::ok");
        }

        //bean
        UserModel usr = ONode.deserialize(json_usr, UserModel.class);

        Result usr_rst = new Result(200, ProtostuffEncoder.instance.encode(usr));
        Object usr_obj = ProtostuffDeoder.instance.decode(usr_rst, UserModel.class);

        assert usr_obj instanceof UserModel;
        assert ((UserModel) usr_obj).id == 1;

        //bean list
        List<UserModel> usr_ary = ONode.deserialize(json_usr_ary);
        Result usr_rst_ary = new Result(200, ProtostuffEncoder.instance.encode(usr_ary));
        Object usr_obj_ary = ProtostuffDeoder.instance.decode(usr_rst_ary, List.class);

        assert usr_obj_ary instanceof List;
        assert ((List<?>) usr_obj_ary).size()==1;


        //null
        usr_rst = new Result(200, ProtostuffEncoder.instance.encode(null));
        usr_obj = ProtostuffDeoder.instance.decode(usr_rst, UserModel.class);

        assert usr_obj == null;
    }
}
