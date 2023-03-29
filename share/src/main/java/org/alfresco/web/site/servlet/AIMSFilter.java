/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.web.site.servlet;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.site.servlet.config.AIMSConfig;
import org.alfresco.web.site.servlet.config.CustomAuthorizationRequestResolver;
import org.alfresco.web.site.servlet.config.SecurityUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.surf.FrameworkUtil;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.surf.exception.RequestContextException;
import org.springframework.extensions.surf.exception.UserFactoryException;
import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.extensions.surf.support.AlfrescoUserFactory;
import org.springframework.extensions.surf.support.ServletRequestContextFactory;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.connector.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.ClientAuthorizationRequiredException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.DefaultRefreshTokenTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2RefreshTokenGrantRequest;
import org.springframework.security.oauth2.client.oidc.authentication.OidcIdTokenDecoderFactory;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoderFactory;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.util.ThrowableAnalyzer;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Collection;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Objects;
import static org.alfresco.web.site.servlet.config.SecurityUtils.toMultiMap;
import static org.alfresco.web.site.servlet.config.SecurityUtils.isAuthorizationResponse;
import static org.alfresco.web.site.servlet.config.SecurityUtils.convert;

public class AIMSFilter implements Filter
{
    private static final Log logger = LogFactory.getLog(AIMSFilter.class);

    private ApplicationContext context;
    private ConnectorService connectorService;
    private SlingshotLoginController loginController;

    private boolean enabled = false;

    public static final String ALFRESCO_ENDPOINT_ID = "alfresco";
    public static final String ALFRESCO_API_ENDPOINT_ID = "alfresco-api";

    public static final String SHARE_PAGE = "/share/page";
    public static final String SHARE_AIMS_LOGOUT = "/share/page/aims/logout";

    public static final String DEFAULT_AUTHORIZATION_REQUEST_BASE_URI = "/oauth2/authorization";
    private ClientRegistrationRepository clientRegistrationRepository;
    private OAuth2AuthorizedClientService oauth2ClientService;
    private final RedirectStrategy authorizationRedirectStrategy;
    private OAuth2AuthorizationRequestResolver authorizationRequestResolver;
    private RequestCache requestCache;
    private AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository;
    private final AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private final OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient = new DefaultAuthorizationCodeTokenResponseClient();
    private final DefaultRefreshTokenTokenResponseClient refreshTokenResponseClient = new DefaultRefreshTokenTokenResponseClient();
    private ThrowableAnalyzer throwableAnalyzer;
    private final JwtDecoderFactory<ClientRegistration> jwtDecoderFactory = new OidcIdTokenDecoderFactory();
    private final GrantedAuthoritiesMapper authoritiesMapper = (authorities) -> {
        return authorities;
    };
    private final OAuth2UserService<OidcUserRequest, OidcUser> userService = new OidcUserService();
    private String clientId;

    public AIMSFilter() {
        this.authorizationRedirectStrategy = new DefaultRedirectStrategy();
    }
    /**
     * Initialize the filter
     *
     * @param filterConfig
     * @throws ServletException
     */
    public void init(FilterConfig filterConfig) throws ServletException
    {
        // Info
        if (logger.isInfoEnabled())
        {
            logger.info("Initializing the AIMS filter.");
        }

        this.context = WebApplicationContextUtils.getRequiredWebApplicationContext(filterConfig.getServletContext());

        AIMSConfig config = (AIMSConfig) this.context.getBean("aims.config");
        this.enabled = config.isEnabled();
        if(this.enabled) {
            this.clientId=System.getProperty("aims.resource");
            // OIDC Specific Setup
            clientRegistrationRepository = context.getBean(ClientRegistrationRepository.class);
            oauth2ClientService = context.getBean(OAuth2AuthorizedClientService.class);
            this.requestCache = new HttpSessionRequestCache();
            this.authorizationRequestResolver = new CustomAuthorizationRequestResolver(clientRegistrationRepository, DEFAULT_AUTHORIZATION_REQUEST_BASE_URI);
            this.authorizationRequestRepository = new HttpSessionOAuth2AuthorizationRequestRepository();
            this.throwableAnalyzer = new SecurityUtils.DefaultThrowableAnalyzer();
        }
        this.connectorService = (ConnectorService) context.getBean("connector.service");
        this.loginController = (SlingshotLoginController) context.getBean("loginController");

        // Info
        if (logger.isInfoEnabled())
        {
            logger.info("AIMS filter initialized.");
        }
    }

    /**
     * @param sreq Servlet Request
     * @param sres Servlet Response
     * @param chain Filter Chain
     * @throws IOException
     * @throws ServletException
     */
    public void doFilter(ServletRequest sreq, ServletResponse sres,
                         FilterChain chain) throws IOException, ServletException
    {
        HttpServletRequest request = (HttpServletRequest) sreq;
        HttpServletResponse response = (HttpServletResponse) sres;
        HttpSession session = request.getSession(false);
        boolean isAuthenticated = false;
        /**
         * check if authentication is done.
         */
        if (null != session &&
            this.enabled) {
            SecurityContext attribute = (SecurityContext) session.getAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
            if(null != attribute) {
                isAuthenticated = attribute.getAuthentication().isAuthenticated();

                /**
                 * Check if token existing token is valid or expired.
                 */
                if(isAuthenticated) {
                    try {
                        refreshToken(attribute, session);
                    } catch(Exception e) {
                        logger.error("Resulted in Error while doing refresh token " + e.getMessage());
                        session.invalidate();
                        isAuthenticated=false;
                    }
                }
            }
        }

        if (!isAuthenticated &&
            this.enabled &&
            (request.getRequestURI().contains(SHARE_PAGE)
                || request.getRequestURI().contains(SHARE_AIMS_LOGOUT)))
        {
            /**
             // Match the request that came from Idp (redirect uri)
             */
            if (this.matchesAuthorizationResponse(request)) {
                this.processAuthorizationResponse(request, response, session);
            } else {
                try {
                    this.requestCache.saveRequest(request, response);
                    OAuth2AuthorizationRequest authorizationRequest = this.authorizationRequestResolver
                        .resolve(request, this.clientId);
                    if (authorizationRequest != null) {
                        this.sendRedirectForAuthorization(request, response, authorizationRequest);
                        return;
                    }
                } catch (Exception var11) {
                    this.unsuccessfulRedirectForAuthorization(response);
                    return;
                }

                try {
                    chain.doFilter(request,response);
                } catch (IOException var9) {
                    throw var9;
                } catch (Exception var10) {
                    Throwable[] causeChain = this.throwableAnalyzer.determineCauseChain(var10);
                    ClientAuthorizationRequiredException authzEx = (ClientAuthorizationRequiredException) this.throwableAnalyzer.getFirstThrowableOfType(ClientAuthorizationRequiredException.class, causeChain);
                    if (authzEx != null) {
                        try {
                            OAuth2AuthorizationRequest authorizationRequest = this.authorizationRequestResolver.resolve(request, authzEx.getClientRegistrationId());
                            if (authorizationRequest == null) {
                                throw authzEx;
                            }

                            this.sendRedirectForAuthorization(request, response, authorizationRequest);
                            this.requestCache.saveRequest(request, response);
                        } catch (Exception var8) {
                            this.unsuccessfulRedirectForAuthorization(response);
                        }

                    } else if (var10 instanceof ServletException) {
                        throw (ServletException) var10;
                    } else if (var10 instanceof RuntimeException) {
                        throw (RuntimeException) var10;
                    } else {
                        throw new RuntimeException(var10);
                    }
                }
            }
        }
        else
        {
            chain.doFilter(sreq, sres);
        }
    }

    /**
     *
     * @param request HTTP Servlet Request
     * @param response HTTP Servlet Response
     * @param session HTTP Session
     * @param authenticationResult OAuth2LoginAuthenticationToken
     */
    private void onSuccess(HttpServletRequest request, HttpServletResponse response, HttpSession session,
                           OAuth2LoginAuthenticationToken authenticationResult)
    {
        // Info
        if (logger.isInfoEnabled())
        {
            logger.info("Completing the AIMS authentication.");
        }

        String username = authenticationResult.getPrincipal().getAttribute("preferred_username");
        String accessToken = authenticationResult.getAccessToken().getTokenValue();
        synchronized (this)
        {
            try
            {
                // Init request context for further use on getting user
                this.initRequestContext(request, response);

                // Get the alfTicket from repo, using the JWT token from Idp
                String alfTicket = this.getAlfTicket(session, username, accessToken);
                if (alfTicket != null)
                {
                    // Ensure User ID is in session so the web-framework knows we have logged in
                    session.setAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID, username);
                    session.setAttribute(UserFactory.SESSION_ATTRIBUTE_EXTERNAL_AUTH_AIMS, true);

                    // Set the alfTicket into connector's session for further use on repo calls (will be set on the RemoteClient)
                    Connector connector = this.connectorService.getConnector(ALFRESCO_ENDPOINT_ID, username, session);
                    connector.getConnectorSession().setParameter(AlfrescoAuthenticator.CS_PARAM_ALF_TICKET, alfTicket);

                    // Set credential username for further use on repo
                    // if there is no pass, as in our case, there will be a "X-Alfresco-Remote-User" header set using this value
                    CredentialVault vault = FrameworkUtil.getCredentialVault(session, username);
                    Credentials credentials = vault.newCredentials(AlfrescoUserFactory.ALFRESCO_ENDPOINT_ID);
                    credentials.setProperty(Credentials.CREDENTIAL_USERNAME, username);
                    vault.store(credentials);

                    // Inform the Slingshot login controller of a successful login attempt as further processing may be required ?
                    this.loginController.beforeSuccess(request, response);

                    // Initialise the user metadata object used by some web scripts
                    this.initUser(request);

                }
                else
                {
                    logger.error("Could not get an alfTicket from Repository.");
                }
            }
            catch (Exception e)
            {
                throw new AlfrescoRuntimeException("Failed to complete AIMS authentication process.", e);
            }
        }
    }

    /**
     * Initialise the request context and request attributes for further use by some web scripts
     * that require authentication
     *
     * @param request
     * @throws RequestContextException
     */
    private void initRequestContext(HttpServletRequest request, HttpServletResponse response) throws RequestContextException
    {
        RequestContext context = ThreadLocalRequestContext.getRequestContext();
        if (context == null)
        {
            ServletRequestContextFactory factory =
                (ServletRequestContextFactory) this.context.getBean("webframework.factory.requestcontext.servlet");
            context = factory.newInstance(new ServletWebRequest(request));
            request.setAttribute(RequestContext.ATTR_REQUEST_CONTEXT, context);
        }

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request, response));
        ServletUtil.setRequest(request);
    }

    /**
     * Initialise the user meta data object and set it into the session and request context (_alf_USER_OBJECT)
     * The user meta data object is used by web scripts that require authentication
     *
     * This is present in the filter for avoiding Basic Authentication prompt for those web scripts,
     * when user access them and is logged out (see https://issues.alfresco.com/jira/browse/APPS-117)
     *
     * @param request
     * @throws UserFactoryException
     */
    private void initUser(HttpServletRequest request) throws UserFactoryException
    {
        RequestContext context = ThreadLocalRequestContext.getRequestContext();
        if (context != null && context.getUser() == null)
        {
            String userEndpointId = (String) context.getAttribute(RequestContext.USER_ENDPOINT);
            UserFactory userFactory = context.getServiceRegistry().getUserFactory();
            User user = userFactory.initialiseUser(context, request, userEndpointId);
            context.setUser(user);
        }
    }

    /**
     * Get an alfTicket using the JWT token from Identity Service
     *
     * @param session HTTP Session
     * @param username username
     * @param accessToken access token
     * @return The alfTicket
     * @throws ConnectorServiceException
     */
    private String getAlfTicket(HttpSession session, String username, String accessToken) throws ConnectorServiceException
    {
        // Info
        if (logger.isInfoEnabled())
        {
            logger.info("Retrieving the Alfresco Ticket from Repository.");
        }

        String alfTicket = null;
        Connector connector = this.connectorService.getConnector(ALFRESCO_API_ENDPOINT_ID, username, session);
        ConnectorContext c = new ConnectorContext(HttpMethod.GET, null, Collections.singletonMap("Authorization", "Bearer " + accessToken));
        c.setContentType("application/json");
        Response r = connector.call("/-default-/public/authentication/versions/1/tickets/-me-?noCache=" + UUID.randomUUID().toString(), c);

        if (Status.STATUS_OK != r.getStatus().getCode())
        {
            if (logger.isErrorEnabled())
            {
                logger.error("Failed to retrieve Alfresco Ticket from Repository.");
            }
        }
        else
        {
            // Parse the alfTicket
            JSONObject json = new JSONObject(r.getText());
            try
            {
                alfTicket = json.getJSONObject("entry").getString("id");
            }
            catch (JSONException e)
            {
                if (logger.isErrorEnabled())
                {
                    logger.error("Failed to parse Alfresco Ticket from Repository response.");
                }
            }
        }

        return alfTicket;
    }

    private boolean matchesAuthorizationResponse(HttpServletRequest request) {
        MultiValueMap<String, String> params = toMultiMap(request.getParameterMap());
        if (!isAuthorizationResponse(params)) {
            return false;
        } else {
            OAuth2AuthorizationRequest authorizationRequest = this.authorizationRequestRepository
                .loadAuthorizationRequest(request);
            if (authorizationRequest == null) {
                return false;
            } else {
                UriComponents requestUri = UriComponentsBuilder.fromUriString(UrlUtils.buildFullRequestUrl(request)).build();
                UriComponents redirectUri = UriComponentsBuilder.fromUriString(authorizationRequest.getRedirectUri()).build();
                Set<Map.Entry<String, List<String>>> requestUriParameters = new LinkedHashSet(requestUri.getQueryParams().entrySet());
                Set<Map.Entry<String, List<String>>> redirectUriParameters = new LinkedHashSet(redirectUri.getQueryParams().entrySet());
                requestUriParameters.retainAll(redirectUriParameters);
                return Objects.equals(requestUri.getScheme(), redirectUri.getScheme()) &&
                    Objects.equals(requestUri.getUserInfo(), redirectUri.getUserInfo()) &&
                    Objects.equals(requestUri.getHost(), redirectUri.getHost()) &&
                    Objects.equals(requestUri.getPort(), redirectUri.getPort()) &&
                    Objects.equals(requestUri.getPath(), redirectUri.getPath()) &&
                    Objects.equals(requestUriParameters.toString(), redirectUriParameters.toString());
            }
        }
    }

    private synchronized void processAuthorizationResponse(HttpServletRequest request, HttpServletResponse response, HttpSession session)
        throws IOException {
        /**
         * Construct Authorization Request & Response
         */
        OAuth2AuthorizationRequest authorizationRequest = this.authorizationRequestRepository
            .removeAuthorizationRequest(request, response);
        MultiValueMap<String, String> params = toMultiMap(request.getParameterMap());
        String redirectUri = UrlUtils.buildFullRequestUrl(request);
        OAuth2AuthorizationResponse authorizationResponse = convert(params, redirectUri);

        ClientRegistration clientRegistration = this.clientRegistrationRepository.findByRegistrationId(this.clientId);

        /**
         * Prepare Authentication Request to get Authentication Result
         */
        OAuth2LoginAuthenticationToken authenticationRequest = new OAuth2LoginAuthenticationToken(clientRegistration,
            new OAuth2AuthorizationExchange(authorizationRequest,authorizationResponse));
        authenticationRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
        OAuth2LoginAuthenticationToken authenticationResult;
        try {
            authenticationResult = (OAuth2LoginAuthenticationToken)this.authenticate(authenticationRequest);
        } catch (OAuth2AuthorizationException var16) {
            OAuth2Error error = var16.getError();
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(authorizationRequest
                .getRedirectUri()).queryParam("error", new Object[]{error.getErrorCode()});
            if (!StringUtils.isEmpty(error.getDescription())) {
                uriBuilder.queryParam("error_description", new Object[]{error.getDescription()});
            }
            if (!StringUtils.isEmpty(error.getUri())) {
                uriBuilder.queryParam("error_uri", new Object[]{error.getUri()});
            }
            this.redirectStrategy.sendRedirect(request, response, uriBuilder.build().encode().toString());
            return;
        }

        /**
         * Add Authentication Result in Security Context and save the User
         */
        SecurityContextHolder.clearContext();
        SecurityContextHolder.getContext().setAuthentication(authenticationResult);
        Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
        String principalName = currentAuthentication != null ? currentAuthentication.getPrincipal().toString() : "anonymousUser";

        OAuth2AuthorizedClient authorizedClient = new OAuth2AuthorizedClient(authenticationResult.getClientRegistration(),
            principalName, authenticationResult.getAccessToken(), authenticationResult.getRefreshToken());
        this.oauth2ClientService.saveAuthorizedClient(authorizedClient, currentAuthentication);

        /**
         * Save the Security Context in Session
         */
        String redirectUrl = authorizationRequest.getRedirectUri();

        /**
         * Retrieve the Cached Page Request before authentication and now after Authentication redirect to the Page.
         */
        SavedRequest savedRequest = this.requestCache.getRequest(request, response);

        /**
         * Retrieve the Cached Page Request before authentication and now after Authentication redirect to the Page.
         */
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
            SecurityContextHolder.getContext());

        if (SecurityContextHolder.getContext() != null && !AuthenticationUtil.isAuthenticated(request))
        {
            this.onSuccess(request, response, session, authenticationResult);
        }

        if (savedRequest != null) {
            redirectUrl = savedRequest.getRedirectUrl();
            this.requestCache.removeRequest(request, response);
        }
        this.redirectStrategy.sendRedirect(request, response, redirectUrl);
    }

    /**
     * Performs the Authentication based on Authentication Request
     */
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuth2LoginAuthenticationToken authorizationCodeAuthentication = (OAuth2LoginAuthenticationToken)authentication;
        if (!authorizationCodeAuthentication.getAuthorizationExchange().getAuthorizationRequest().getScopes().contains("openid")) {
            return null;
        } else {
            OAuth2AuthorizationRequest authorizationRequest = authorizationCodeAuthentication.getAuthorizationExchange().getAuthorizationRequest();
            OAuth2AuthorizationResponse authorizationResponse = authorizationCodeAuthentication.getAuthorizationExchange().getAuthorizationResponse();
            if (authorizationResponse.statusError()) {
                throw new OAuth2AuthenticationException(authorizationResponse.getError(), authorizationResponse.getError().toString());
            } else if (!authorizationResponse.getState().equals(authorizationRequest.getState())) {
                OAuth2Error oauth2Error = new OAuth2Error("invalid_state_parameter");
                throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
            } else {
                OAuth2AccessTokenResponse accessTokenResponse;
                try {
                    accessTokenResponse = this.accessTokenResponseClient.getTokenResponse(new OAuth2AuthorizationCodeGrantRequest(authorizationCodeAuthentication.getClientRegistration(), authorizationCodeAuthentication.getAuthorizationExchange()));
                } catch (OAuth2AuthorizationException var14) {
                    OAuth2Error oauth2Error = var14.getError();
                    throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
                }

                ClientRegistration clientRegistration = authorizationCodeAuthentication.getClientRegistration();
                Map<String, Object> additionalParameters = accessTokenResponse.getAdditionalParameters();
                if (!additionalParameters.containsKey("id_token")) {
                    OAuth2Error invalidIdTokenError = new OAuth2Error("invalid_id_token", "Missing (required) ID Token in Token Response for Client Registration: " + clientRegistration.getRegistrationId(), (String)null);
                    throw new OAuth2AuthenticationException(invalidIdTokenError, invalidIdTokenError.toString());
                } else {
                    OidcIdToken idToken = this.createOidcToken(clientRegistration, accessTokenResponse);
                    String requestNonce = authorizationRequest.getAttribute("nonce");
                    if (requestNonce != null) {
                        String nonceHash;
                        OAuth2Error oauth2Error;
                        try {
                            nonceHash = createHash(requestNonce);
                        } catch (NoSuchAlgorithmException var13) {
                            oauth2Error = new OAuth2Error("invalid_nonce");
                            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
                        }

                        String nonceHashClaim = idToken.getNonce();
                        if (nonceHashClaim == null || !nonceHashClaim.equals(nonceHash)) {
                            oauth2Error = new OAuth2Error("invalid_nonce");
                            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
                        }
                    }

                    OidcUser oidcUser = this.userService.loadUser(new OidcUserRequest(clientRegistration, accessTokenResponse.getAccessToken(), idToken, additionalParameters));
                    Collection<? extends GrantedAuthority> mappedAuthorities = this.authoritiesMapper.mapAuthorities(oidcUser.getAuthorities());
                    OAuth2LoginAuthenticationToken authenticationResult = new OAuth2LoginAuthenticationToken(authorizationCodeAuthentication.getClientRegistration(), authorizationCodeAuthentication.getAuthorizationExchange(), oidcUser, mappedAuthorities, accessTokenResponse.getAccessToken(), accessTokenResponse.getRefreshToken());
                    authenticationResult.setDetails(authorizationCodeAuthentication.getDetails());
                    return authenticationResult;
                }
            }
        }
    }

    private OidcIdToken createOidcToken(ClientRegistration clientRegistration, OAuth2AccessTokenResponse accessTokenResponse) {
        JwtDecoder jwtDecoder = this.jwtDecoderFactory.createDecoder(clientRegistration);

        Jwt jwt;
        try {
            jwt = jwtDecoder.decode((String)accessTokenResponse.getAdditionalParameters().get("id_token"));
        } catch (JwtException var7) {
            OAuth2Error invalidIdTokenError = new OAuth2Error("invalid_id_token", var7.getMessage(), (String)null);
            throw new OAuth2AuthenticationException(invalidIdTokenError, invalidIdTokenError.toString(), var7);
        }

        OidcIdToken idToken = new OidcIdToken(jwt.getTokenValue(), jwt.getIssuedAt(), jwt.getExpiresAt(), jwt.getClaims());
        return idToken;
    }

    static String createHash(String nonce) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(nonce.getBytes(StandardCharsets.US_ASCII));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
    }

    private void sendRedirectForAuthorization(HttpServletRequest request, HttpServletResponse response, OAuth2AuthorizationRequest authorizationRequest) throws IOException {
        if (AuthorizationGrantType.AUTHORIZATION_CODE.equals(authorizationRequest.getGrantType())) {
            this.authorizationRequestRepository.saveAuthorizationRequest(authorizationRequest, request, response);
        }
        this.authorizationRedirectStrategy.sendRedirect(request, response, authorizationRequest.getAuthorizationRequestUri());
    }

    private void unsuccessfulRedirectForAuthorization(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }

    private synchronized void refreshToken(SecurityContext attribute, HttpSession session) {
        OAuth2LoginAuthenticationToken oAuth2LoginAuthenticationToken = (OAuth2LoginAuthenticationToken)attribute.getAuthentication();
        /**
         * do something to get new access token
         */
        ClientRegistration clientRegistration = oAuth2LoginAuthenticationToken.getClientRegistration();
        /**
         * Call Auth server token endpoint to refresh token.
         */
        OAuth2RefreshTokenGrantRequest refreshTokenGrantRequest = new OAuth2RefreshTokenGrantRequest(
            clientRegistration, oAuth2LoginAuthenticationToken.getAccessToken(), oAuth2LoginAuthenticationToken.getRefreshToken());
        OAuth2AccessTokenResponse accessTokenResponse = this.refreshTokenResponseClient
            .getTokenResponse(refreshTokenGrantRequest);
        /**
         * Convert id_token to OidcToken.
         */
        OidcIdToken idToken = createOidcToken(clientRegistration, accessTokenResponse);
        /**
         * Since I have already implemented a custom OidcUserService, reuse existing
         * code to get new user.
         */
        OidcUser oidcUser = this.userService.loadUser(new OidcUserRequest(clientRegistration,
            accessTokenResponse.getAccessToken(), idToken, accessTokenResponse.getAdditionalParameters()));

        /**
         * Create new authentication(OAuth2LoginAuthenticationToken).
         */
        Collection<? extends GrantedAuthority> mappedAuthorities = this.authoritiesMapper.mapAuthorities(oidcUser.getAuthorities());
        OAuth2LoginAuthenticationToken authenticationResult = new OAuth2LoginAuthenticationToken(clientRegistration, oAuth2LoginAuthenticationToken.getAuthorizationExchange(),
            oidcUser, mappedAuthorities, accessTokenResponse.getAccessToken(), accessTokenResponse.getRefreshToken());
        authenticationResult.setDetails(oAuth2LoginAuthenticationToken.getDetails());
        /**
         * Update access_token and refresh_token by saving new authorized client.
         */
        OAuth2AuthorizedClient updatedAuthorizedClient = new OAuth2AuthorizedClient(clientRegistration,
            oAuth2LoginAuthenticationToken.getName(), accessTokenResponse.getAccessToken(),
            accessTokenResponse.getRefreshToken());
        this.oauth2ClientService.saveAuthorizedClient(updatedAuthorizedClient, authenticationResult);
        /**
         * Set new authentication in SecurityContextHolder.
         */
        attribute.setAuthentication(authenticationResult);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
            attribute);
    }
}
