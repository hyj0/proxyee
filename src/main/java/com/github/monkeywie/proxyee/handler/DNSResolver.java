package com.github.monkeywie.proxyee.handler;

import com.kendamasoft.dns.DnsConnectionAuto;
import com.kendamasoft.dns.protocol.Message;
import com.kendamasoft.dns.protocol.MessageBuilder;
import com.kendamasoft.dns.protocol.RecordType;
import com.kendamasoft.dns.protocol.ResourceRecord;
import com.kendamasoft.dns.records.ARecord;

import java.io.IOException;
import java.util.HashMap;

public class DNSResolver {
    public static HashMap<String, String> host2IpMap = new HashMap<>();

    public static String getAddressByName(String host) {
        synchronized (host2IpMap) {
            if (host2IpMap.containsKey(host)) {
                return host2IpMap.get(host);
            }
        }

        if (isRealIP(host)) {
            synchronized (host2IpMap) {
                host2IpMap.put(host, host);
            }
            return host;
        }

        Message request = new MessageBuilder()
                .setName(host)
                .setType(RecordType.A)
                .build();

        Message response = null;
        try {
            response = new DnsConnectionAuto().doRequest(request);
            for (ResourceRecord record : response.getAllRecords()) {
                if (record.getRecordType() == RecordType.A) {
                    ARecord aRecord = (ARecord) record.getContent();
                    String ip = aRecord.toString().split(" ")[1];
                    synchronized (host2IpMap) {
                        host2IpMap.put(host, ip);
                    }
                    return ip;
                }
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean isRealIP(String host) {
        String[] sp = host.split("\\.");
        int nCount = 0;
        if (sp.length != 4) {
            return false;
        }

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < sp[i].length(); j++) {
                char c = sp[i].charAt(j);
                if (c < '0' || c > '9') {
                    return false;
                }
            }
            int n = Integer.parseInt(sp[i]);
            if (n > 255) {
                return false;
            }
        }
        return true;

    }
}
