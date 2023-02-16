package proxy;

import com.github.monkeywie.proxyee.intercept.HttpProxyIntercept;
import com.github.monkeywie.proxyee.intercept.HttpProxyInterceptInitializer;
import com.github.monkeywie.proxyee.intercept.HttpProxyInterceptPipeline;
import com.github.monkeywie.proxyee.proxy.ProxyConfig;
import com.github.monkeywie.proxyee.proxy.ProxyType;
import com.github.monkeywie.proxyee.server.HttpProxyServer;
import io.netty.channel.Channel;
import com.github.monkeywie.proxyee.handler.DNSResolver;

import java.io.IOException;

class MyHttpProxyIntercept extends HttpProxyIntercept {
    IPCheck0 ipCheck;

    public MyHttpProxyIntercept(IPCheck0 ipCheck) {
        this.ipCheck = ipCheck;
    }


    @Override
    public void beforeConnect(Channel clientChannel, HttpProxyInterceptPipeline pipeline) throws Exception {
        String host = pipeline.getRequestProto().getHost();
        System.out.println((new Exception()).getStackTrace()[0].getMethodName() + " " + host + " " + " start");

        String ip = DNSResolver.getAddressByName(host);
        if (host.contains("google")) {

        } else {
//            pipeline.getRequestProto().setIp(ip);
        }
        if (ip == null || ipCheck.isCNIP(ip)) {
            System.out.println((new Exception()).getStackTrace()[0].getMethodName() + " " + host + " " + ip + " cn_ip");
            ProxyConfig proxyConfig = pipeline.getProxyConfig();
            super.beforeConnect(clientChannel, pipeline);
        } else {
            System.out.println((new Exception()).getStackTrace()[0].getMethodName() + " " + host + " " + ip + " no cn_ip");
            ProxyConfig proxyConfig = new ProxyConfig(ProxyType.SOCKS5, "127.0.0.1", 1080);
            pipeline.setProxyConfig(proxyConfig);
            pipeline.beforeConnect(clientChannel);
        }
    }
}

public class HttpProxy {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("usage:exec ip.txt");
            return;
        }
        /*
        wget -c http://ftp.apnic.net/stats/apnic/delegated-apnic-latest
        cat delegated-apnic-latest | awk -F '|' '/CN/&&/ipv4/ {print $4 "/" 32-log($5)/log(2)}' > /tmp/cn_ip.txt
         */
        String ipFile = args[args.length - 1];
        IPCheck0 ipCheck = new IPCheckIp2Region(ipFile);

        HttpProxyServer httpProxyServer = new HttpProxyServer();
        httpProxyServer.proxyInterceptInitializer(new HttpProxyInterceptInitializer() {
            @Override
            public void init(HttpProxyInterceptPipeline pipeline) {
                pipeline.addLast(new MyHttpProxyIntercept(ipCheck));
            }
        });
        httpProxyServer.start(8080);
    }
}
