package com.aladdin.rpc.api;

/**
 * @author lgc
 */
public interface RpcService {
    /**
     * 加
     */
    int add(int a, int b);

    /**
     * 减
     */
    int sub(int a, int b);

    /**
     * 乘
     */
    int mult(int a, int b);

    /**
     * 除
     */
    int div(int a, int b);
}
