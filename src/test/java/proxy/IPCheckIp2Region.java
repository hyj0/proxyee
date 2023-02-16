package proxy;

import org.lionsoul.ip2region.xdb.Searcher;

import java.io.IOException;

public class IPCheckIp2Region implements IPCheck0 {
    Searcher searcher = null;

    public IPCheckIp2Region(String xdbPath) throws IOException {
        searcher = Searcher.newWithFileOnly(xdbPath);
    }

    @Override
    public boolean isCNIP(String ip) {
        try {
            String region = searcher.search(ip);
            //System.out.println(region);
            if (region.contains("中国") || region.contains("内网")) {
                return true;
            }
        } catch (Exception e) {
            System.err.println("isCNIP err " + ip);
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static void main(String[] args) throws IOException {
        IPCheck0 ipCheck = new IPCheckIp2Region("/data/projector/IdeaProjects/ip2region/data/ip2region.xdb");
        System.out.println(ipCheck.isCNIP("39.156.66.10")); //baidu.com
        System.out.println(ipCheck.isCNIP("10.9.0.164"));
        System.out.println(ipCheck.isCNIP("127.0.0.1"));
        System.out.println(ipCheck.isCNIP("172.217.163.46"));//google.com
        System.out.println(ipCheck.isCNIP("13.107.21.200")); //bing.com
    }
}
