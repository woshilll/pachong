package com.yang;

import cn.hutool.http.HttpUtil;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.List;

/**
 * desc
 *
 * @author stmj
 * @version 1.0.0
 * @date 2021/9/23 16:15
 */
public class Test {
    private static final Logger log = LoggerFactory.getLogger(Test.class);
    public static void main(String[] args) throws Exception{
        PayRequest payRequest = new PayRequest();
        log.info("asadasd {}", JsonUtil.toJson(payRequest));
    }
}
