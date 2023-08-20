package io.github.greetapp;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import io.helidon.config.Config;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

public class TokenUtils {

    private static final Logger LOGGER = Logger.getLogger(TokenUtils.class.getName());

    private String appClientId;
    private Map<String, String> kidToPem;

    public TokenUtils(Config config) {
        this.appClientId = readProperty(config, "cognito.userpool.clientid");

        this.kidToPem = Map.of(
                readProperty(config, "cognito.userpool.publickey1.kid"),
                readProperty(config, "cognito.userpool.publickey1.pem"),
                readProperty(config, "cognito.userpool.publickey2.kid"),
                readProperty(config, "cognito.userpool.publickey2.pem"));
    }

    private String readProperty(Config config, String key) {
        return config.get(key).asString()
                .orElseThrow(() -> new AppException("Property not found in configuration: %s".formatted(key)));
    }

    public Optional<UserInfo> getVerifiedUserInfo(String accessToken) {
        String[] accessTokenParts = accessToken.split("\\.");
        String headerBase64 = accessTokenParts[0];
        String payloadBase64 = accessTokenParts[1];
        String signature = accessTokenParts[2];

        JsonObject header = base64UrlDecodeToJSON(headerBase64);
        JsonObject payload = base64UrlDecodeToJSON(payloadBase64);

        String alg = header.getString("alg");
        String kid = header.getString("kid");

        String clientId = payload.getString("client_id");
        long exp = payload.getInt("exp");

        boolean sigValid = isSignatureValid(headerBase64, payloadBase64, signature, clientId, alg, exp, kid);

        if (sigValid) {
            String username = payload.getString("username");
            JsonArray groupsArr = payload.getJsonArray("cognito:groups");

            List<String> groups = new ArrayList<>();
            if (groupsArr != null) {
                for (int i = 0; i < groupsArr.size(); i++) {
                    groups.add(groupsArr.getString(i));
                }
            }

            return Optional.of(new UserInfo(username, groups));
        } else {
            return Optional.empty();
        }
    }

    private static byte[] base64UrlDecode(String str) {
        Base64.Decoder dec = Base64.getUrlDecoder();
        return dec.decode(str);
    }

    private static JsonObject base64UrlDecodeToJSON(String str) {
        byte[] decoded = base64UrlDecode(str);
        return Json.createReader(new ByteArrayInputStream(decoded)).readObject();
    }

    private boolean isSignatureValid(String headerBase64, String payloadBase64, String signature,
            String clientId, String alg, long exp, String kid) {
        if (!appClientId.equals(clientId)) {
            return false;
        }

        if (!"RS256".equals(alg)) {
            return false;
        }

        if ((exp * 1000) < new Date().getTime()) {
            return false;
        }

        if (!this.kidToPem.containsKey(kid)) {
            throw new AppException("No public key was found for the informed kid in the JWT token.");
        }

        String publicKeyPEM = this.kidToPem.get(kid);

        publicKeyPEM = publicKeyPEM
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PUBLIC KEY-----", "");

        try {
            byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            Signature signatureVerify = Signature.getInstance("SHA256withRSA");
            signatureVerify.initVerify(publicKey);
            String document = headerBase64 + "." + payloadBase64;
            signatureVerify.update(document.getBytes(StandardCharsets.UTF_8));

            byte[] signatureDecoded = Base64.getUrlDecoder().decode(signature);
            boolean sigVerified = signatureVerify.verify(signatureDecoded);
            return sigVerified;
        } catch (NullPointerException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException
                | SignatureException e) {
            LOGGER.warning(e.getMessage());
            throw new AppException("Could not verify Access Token");
        }
    }

}
