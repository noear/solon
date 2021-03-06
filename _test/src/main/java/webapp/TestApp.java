package webapp;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.SolonBuilder;
import org.noear.solon.annotation.Import;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.tool.PreheatUtils;
import org.noear.solon.core.handle.MethodType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.demo6_aop.TestImport;

@Import(value = TestImport.class)
//@EnableCron4j
//@EnableQuartz
public class TestApp {

    static Logger logger = LoggerFactory.getLogger(TestApp.class);

    public static void main(String[] args) throws Exception {
        /**
         *
         * http://t5_undertow.test.noear.org
         *
         * http://t4_nettyhttp.test.noear.org
         *
         * http://t3_smarthttp.test.noear.org
         *
         * http://t2_jlhttp.test.noear.org
         *
         * http://t1_jetty.test.noear.org
         *
         * */

        //简化方式
        //SolonApp app = Solon.start(TestApp.class, args, x -> x.enableSocketD(true).enableWebSocket(true));


        //构建方式
        SolonApp app = new SolonBuilder().onError(e -> {
            e.printStackTrace();
        }).onAppInitEnd(e -> {
            System.out.println("1.初始化完成");
        }).onPluginLoadEnd(e -> {
            System.out.println("2.插件加载完成了");
        }).onBeanLoadEnd(e -> {
            System.out.println("3.Bean扫描并加载完成");
        }).onAppLoadEnd(e -> {
            System.out.println("4.应用全加载完成了");
        }).start(TestApp.class, args, x -> x.enableSocketD(true).enableWebSocket(true));

        System.out.println("testname : " + Solon.cfg().get("testname"));


        System.out.println("生在ID = " + CloudClient.id().generate());

//        app.filter((ctx, chain)->{
//            System.out.println("我是过滤器!!!path="+ctx.path());
//            chain.doFilter(ctx);
//        });

//        app.ws("/demoe/websocket",(session,message)->{
//            System.out.println(session.resourceDescriptor());
//
//            if(Solon.cfg().isDebugMode()){
//                return;
//            }
//
//            if (session.method() == XMethod.WEBSOCKET) {
//                message.setHandled(true);
//
//                session.getOpenSessions().forEach(s -> {
//                    s.send(message.toString());
//                });
//            } else {
//                System.out.println("X我收到了::" + message.toString());
//                //session.send("X我收到了::" + message.toString());
//            }
//        });

        //预热测试
        PreheatUtils.preheat("/demo1/run0/");

        logger.debug("测试");


        //socket server
        app.socket("/seb/test", (c) -> {
            String msg = c.body();
            c.output("收到了...:" + msg);
        });

        //web socket wss 监听
        app.ws("/seb/test", (c) -> {
            String msg = c.body();
            c.output("收到了...:" + msg);
        });
    }

    void test1() {
        //控制渲染的示例 //即拦截执行结果的机制
        //
        SolonApp app = Solon.start(TestApp.class, null);

        //开始之前把上下文置为已泻染
        app.before("/user/**", MethodType.HTTP, c -> c.setRendered(true));

        app.after("/user/**", MethodType.HTTP, c -> {
            //可对 c.result 进行处理 //并输出
        });
    }
}
