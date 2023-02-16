package proxy;

import org.apache.commons.net.util.SubnetUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class IPCheckApnic implements IPCheck0 {
    private String ipFile;
    HashMap<String, Set<String>> mask2IpMap;

    public IPCheckApnic(String ipFile) throws IOException {
        this.ipFile = ipFile;
        mask2IpMap = new HashMap<>();

        parse();
    }

    private int parse() throws IOException {
        FileInputStream fileInputStream = new FileInputStream(ipFile);
        byte[] bytes = fileInputStream.readAllBytes();
        String str = new String(bytes);
        String[] strings = str.split("\n");
        for (var s :
                strings) {
            //223.248.0.0/14
            String[] sp = s.split("/");
            if (sp.length != 2) {
                System.err.println("err host/mask " + s);
                continue;
            }
            SubnetUtils subnetUtils = new SubnetUtils(s);
            SubnetUtils.SubnetInfo subnetInfo = subnetUtils.getInfo();
            String ip = subnetInfo.getAddress();
            String netmask = subnetInfo.getNetmask();
            System.out.println(String.format("%s  %s", s, ip, netmask));

            if (mask2IpMap.containsKey(netmask)) {
                Set<String> setIp = mask2IpMap.get(netmask);
                setIp.add(ip);
            } else {
                Set<String> setIp = new HashSet<>();
                setIp.add(ip);
                mask2IpMap.put(netmask, setIp);
            }
        }
        return 0;
    }

    public boolean isCNIP(String host) {
        for (var s :
                mask2IpMap.entrySet()) {
            String netMask = s.getKey();
            Set<String> ipSet = s.getValue();
            SubnetUtils subnetUtils = new SubnetUtils(host, netMask);
            String ip = subnetUtils.getInfo().getAddress();

            boolean isOk = ipSet.contains(ip);
            if (isOk) {
                return true;
            } else {
                continue;
            }
        }
        return false;
    }

    public static void main(String[] args) throws IOException {
        IPCheckApnic ipCheck = new IPCheckApnic("/tmp/cn_ip.txt");
        System.out.println(ipCheck.isCNIP("39.156.66.10")); //baidu.com
        System.out.println(ipCheck.isCNIP("10.9.0.164"));
        System.out.println(ipCheck.isCNIP("127.0.0.1"));
        System.out.println(ipCheck.isCNIP("172.217.163.46"));//google.com
        System.out.println(ipCheck.isCNIP("13.107.21.200")); //bing.com
    }
}
