package com.aladdin.rpc.provider;

import com.aladdin.rpc.api.RpcHelloService;

/**
 * @author lgc
 */
public class RpcHelloServiceImpl implements RpcHelloService {
    @Override
    public String hello(String name) {
        return "Hello "+name+"!";
    }
}
