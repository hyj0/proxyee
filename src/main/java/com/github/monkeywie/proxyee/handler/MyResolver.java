package com.github.monkeywie.proxyee.handler;

import io.netty.resolver.AbstractAddressResolver;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class MyResolver extends AbstractAddressResolver<InetSocketAddress> {
    public MyResolver(EventExecutor executor) {
        super(executor);
    }

    @Override
    protected boolean doIsResolved(InetSocketAddress inetSocketAddress) {
        System.out.println((new Exception()).getStackTrace()[0].getMethodName() + " " + inetSocketAddress + " " + " start");

        return false;
    }

    @Override
    protected void doResolve(InetSocketAddress inetSocketAddress, Promise<InetSocketAddress> promise) throws Exception {
        System.out.println((new Exception()).getStackTrace()[0].getMethodName() + " " + inetSocketAddress + " " + " start");
        String ip = DNSResolver.getAddressByName(inetSocketAddress.getHostString());
        if (ip == null) {
            promise.setFailure(new Throwable("getAddressByName err"));
            return;
        }
        InetSocketAddress retSockAddr = new InetSocketAddress(ip, inetSocketAddress.getPort());
        promise.setSuccess(retSockAddr);
        return;
    }

    @Override
    protected void doResolveAll(InetSocketAddress inetSocketAddress, Promise<List<InetSocketAddress>> promise) throws Exception {
        System.out.println((new Exception()).getStackTrace()[0].getMethodName() + " " + inetSocketAddress + " " + " start");
        String ip = DNSResolver.getAddressByName(inetSocketAddress.getHostString());
        if (ip == null) {
            promise.setFailure(new Throwable("getAddressByName err"));
            return;
        }
        InetSocketAddress retSockAddr = new InetSocketAddress(ip, inetSocketAddress.getPort());
        List<InetSocketAddress> addressList = new ArrayList<>();
        addressList.add(retSockAddr);
        promise.setSuccess(addressList);
        return;
    }

}
