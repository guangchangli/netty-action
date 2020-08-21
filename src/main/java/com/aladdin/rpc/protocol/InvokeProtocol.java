package com.aladdin.rpc.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lgc
 */
@Data
public class InvokeProtocol implements Serializable {

    private String className;
    private String methodName;
    private Class<?>[] params;
    private Object[] values;
}
