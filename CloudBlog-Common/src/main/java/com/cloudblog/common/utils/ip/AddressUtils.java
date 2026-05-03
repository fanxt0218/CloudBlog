package com.cloudblog.common.utils.ip;


import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cloudblog.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

/**
 * 获取地址类
 * 
 * @author fanxt
 */
public class AddressUtils
{
    private static final Logger log = LoggerFactory.getLogger(AddressUtils.class);

    // IP地址查询
    public static final String IP_URL = "http://whois.pconline.com.cn/ipJson.jsp";

    // 未知地址
    public static final String UNKNOWN = "XX XX";

    public static String getRealAddressByIP(String ip)
    {
        return getRealAddressByIP(ip, true);
    }

    public static String getRealAddressByIP(String ip, boolean addressEnabled)
    {
        if (IpUtils.internalIp(ip))
        {
            return "内网IP";
        }
        if (addressEnabled)
        {
            try
            {
                String rspStr = HttpUtil.get(IP_URL + "?ip=" + ip + "&json=true", Charset.forName("GBK"));
                if (StringUtils.isEmpty(rspStr))
                {
                    log.error("获取地理位置异常 {}", ip);
                    return UNKNOWN;
                }
                JSONObject obj = JSONUtil.parseObj(rspStr);
                String region = obj.getStr("pro");
                String city = obj.getStr("city");
                return String.format("%s %s", region, city);
            }
            catch (Exception e)
            {
                log.error("获取地理位置异常 {}", ip);
            }
        }
        return UNKNOWN;
    }
}
