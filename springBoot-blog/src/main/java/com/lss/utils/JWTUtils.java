package com.lss.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * token工具类
 *
 * @author lss
 * @create 2022年03月04日 10:59
 */
public class JWTUtils {

    /**
     * 用于签名的私钥
     */
    private static final String PRIVATE_KEY = "LiShuShengBLOG";
    /**
     * 签发者
     */
    private static final String ISS = "LSS";

    /**
     * 生成token
     */
    public static String createToken(String id, String username) {
        //获取加密后的密钥
        SecretKey secretKey = generalKey();
        //Jwt头
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "HS256");
        Map<String, Object> claims = new HashMap<>();
        //自定义有效载荷部分
        claims.put("id", id);
        claims.put("username", username);
        return Jwts.builder()
                //发证人
                .setIssuer(ISS)
                //Jwt头
                .setHeader(header)
                //有效载荷
                .setClaims(claims)
                //设定签发时间
                .setIssuedAt(new Date())
                //使用HS256算法签名，PRIVATE_KEY为签名密钥
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /**
     * 生成加密后的秘钥 secretKey
     */
    private static SecretKey generalKey() {
        byte[] encodedKey = Base64.getDecoder().decode(JWTUtils.PRIVATE_KEY);
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    }

    //解析token
    public static Claims parseToken(String token) {
        SecretKey secretKey = generalKey();
        return Jwts.parser().setSigningKey(secretKey) //设置签名密钥为yyh
                .parseClaimsJws(token).getBody();
    }

}
