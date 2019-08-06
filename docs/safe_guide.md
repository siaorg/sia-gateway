# 安全认证使用文档

### 安全认证流程

下图是网关基本安全认证流程图，主要涉及API网关认证服务器、API网关和客户端及业务资源。业务资源方需要事先绑定网关安全认证服务组件，并通过路由安全认证模块设置安全口令，才能使安全认证在路由资源生效。

![](/docs/static_files/Frm.png)

### 路由资源管理安全码设置

路由资源Owner需要在网关设置一个安全口令及安全口令的有效时间，然后在【组件管理】->【安全认证】中绑定路由与安全认证服务即可实现路由的安全认证服务。

### 安全认证客户端代码示例


```java
/**
 * 封装OAuth Server端认证需要的参数
 */
public class ClientParams {

    public static final String CLIENT_ID = "gantrygw-book-test2"; // 应用id CLIENT_ID

    public static final String CLIENT_SECRET = "123456"; // 应用secret CLIENT_SECRET

    public static final String OAUTH_SERVER_TOKEN_URL = "http://localhost:8090/oauth/token"; // ACCESS_TOKEN换取地址

    public static final String OAUTH_SERVER_REDIRECT_URI = "http://notes.coding.me"; // 回调地址

    public static final String OAUTH_SERVICE_API = "http://localhost:8080/book2/checked-out"; // 测试开放数据api

}
```

```java

public class OauthClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(OauthClient.class);

    public static void main(String[] args) throws Exception {

          getResource(makeTokenRequestWithAuthCode().getAccessToken());

    }

    /**
     *
     * @param
     * @return
     * @throws OAuthProblemException
     * @throws OAuthSystemException
     */
    private static OAuthAccessTokenResponse makeTokenRequestWithAuthCode() throws OAuthProblemException, OAuthSystemException {

        OAuthClientRequest request = OAuthClientRequest
                .tokenLocation(ClientParams.OAUTH_SERVER_TOKEN_URL)
                .setClientId(ClientParams.CLIENT_ID)
                .setClientSecret(ClientParams.CLIENT_SECRET)
                .setGrantType(GrantType.CLIENT_CREDENTIALS)
                .buildBodyMessage();

        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());

        OAuthAccessTokenResponse oauthResponse = oAuthClient.accessToken(request);
        return oauthResponse;
    }

    private static void getResource(String token) throws OAuthSystemException, OAuthProblemException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(ClientParams.OAUTH_SERVICE_API);
        post.addHeader("Authorization",token);
        String responseContent = null; // 响应内容
        CloseableHttpResponse response = null;
        try {
            response = client.execute(post);
            System.out.println(JSONHelper.toString(response));
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                responseContent = EntityUtils.toString(entity, "UTF-8");
            }

            if (response != null)
                response.close();
            if (client != null)
                client.close();

            LOGGER.info("responseContent:" + responseContent);

        } catch(ClientProtocolException e) {
            LOGGER.info(e.getMessage());
        } catch(IOException e) {
            LOGGER.info(e.getMessage());
        }
    }

}

```

### Demo工程代码实例

sia-gateway-admin工程测试用例：OauthClient.java