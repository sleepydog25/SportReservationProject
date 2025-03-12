package com.example.demo.Util;

import com.example.demo.modal.dto.ReservationDto;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import javax.crypto.SecretKey;
import java.util.Date;

public class JwtTokenUtil {

    private static final String SECRET_KEY_BASE64 = "gwx3dHiS/LhVWZf6tjruztKncK4zm+PIHxDUllENDSk="; // 使用你生成的 Base64 編碼密鑰

    public static String generateToken(ReservationDto reservation) throws JOSEException {
        SecretKey secretKey = KeyUtil.decodeKeyFromBase64(SECRET_KEY_BASE64);

        // 建立 JWT 令牌
        JWSSigner signer = new MACSigner(secretKey.getEncoded());
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject("reservation")
                .claim("id", reservation.getId())
                .claim("email", reservation.getEmail())
                .claim("field", reservation.getField())
                .claim("date", reservation.getDate())
                .claim("time", reservation.getTime())
                .claim("equipment", reservation.isEquipment())
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + 3600000)) // 1 hour expiration
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
        signedJWT.sign(signer);

        return signedJWT.serialize();
    }
}