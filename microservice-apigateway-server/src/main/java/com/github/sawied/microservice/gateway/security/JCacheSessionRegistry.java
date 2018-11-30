package com.github.sawied.microservice.gateway.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.cache.Cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.util.Assert;

public class JCacheSessionRegistry implements SessionRegistry, ApplicationListener<SessionDestroyedEvent> {

	protected final Logger logger = LoggerFactory.getLogger(JCacheSessionRegistry.class);
	
	
	@SuppressWarnings("rawtypes")
	private final Cache<Object,Set> principals;
	
	private final Cache<String, SessionInformation> sessionIds;
	

	@SuppressWarnings("rawtypes")
	public JCacheSessionRegistry(Cache<Object, Set> principals,
			Cache<String, SessionInformation> sessionIds
			) {
		super();
		this.principals = principals;
		this.sessionIds=sessionIds;
	}

	@Override
	public void onApplicationEvent(SessionDestroyedEvent event) {
		String sessionId = event.getId();
		removeSessionInformation(sessionId);
	}

	@Override
	public List<Object> getAllPrincipals() {
		List<Object> objects=new ArrayList<Object>();
		principals.forEach(entry -> objects.add(entry.getKey()));
		return objects;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SessionInformation> getAllSessions(Object principal, boolean includeExpiredSessions) {
		final Set<String> sessionsUsedByPrincipal = principals.get(principal);

		if (sessionsUsedByPrincipal == null) {
			return Collections.emptyList();
		}

		List<SessionInformation> list = new ArrayList<>(
				sessionsUsedByPrincipal.size());

		for (String sessionId : sessionsUsedByPrincipal) {
			SessionInformation sessionInformation = getSessionInformation(sessionId);

			if (sessionInformation == null) {
				continue;
			}

			if (includeExpiredSessions || !sessionInformation.isExpired()) {
				list.add(sessionInformation);
			}
		}

		return list;
	}

	@Override
	public SessionInformation getSessionInformation(String sessionId) {
		Assert.hasText(sessionId, "SessionId required as per interface contract");
		return sessionIds.get(sessionId);
	}

	@Override
	public void refreshLastRequest(String sessionId) {
		Assert.hasText(sessionId, "SessionId required as per interface contract");

		SessionInformation info = getSessionInformation(sessionId);

		if (info != null) {
			info.refreshLastRequest();
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void registerNewSession(String sessionId, Object principal) {
		Assert.hasText(sessionId, "SessionId required as per interface contract");
		Assert.notNull(principal, "Principal required as per interface contract");

		if (logger.isDebugEnabled()) {
			logger.debug("Registering session " + sessionId + ", for principal "
					+ principal);
		}

		if (getSessionInformation(sessionId) != null) {
			removeSessionInformation(sessionId);
		}

		sessionIds.put(sessionId,
				new SessionInformation(principal, sessionId, new Date()));

		Set<String> sessionsUsedByPrincipal = principals.get(principal);

		if (sessionsUsedByPrincipal == null) {
			sessionsUsedByPrincipal = new CopyOnWriteArraySet<>();
			
			Set<String> prevSessionsUsedByPrincipal = 
			principals.getAndPut(principal, sessionsUsedByPrincipal);
			
			if (prevSessionsUsedByPrincipal != null) {
				sessionsUsedByPrincipal = prevSessionsUsedByPrincipal;
			}
		}

		sessionsUsedByPrincipal.add(sessionId);

		if (logger.isTraceEnabled()) {
			logger.trace("Sessions used by '" + principal + "' : "
					+ sessionsUsedByPrincipal);
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void removeSessionInformation(String sessionId) {

		Assert.hasText(sessionId, "SessionId required as per interface contract");

		SessionInformation info = getSessionInformation(sessionId);

		if (info == null) {
			return;
		}

		if (logger.isTraceEnabled()) {
			logger.debug("Removing session " + sessionId
					+ " from set of registered sessions");
		}

		sessionIds.remove(sessionId);

		Set<String> sessionsUsedByPrincipal = principals.get(info.getPrincipal());

		if (sessionsUsedByPrincipal == null) {
			return;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Removing session " + sessionId
					+ " from principal's set of registered sessions");
		}

		sessionsUsedByPrincipal.remove(sessionId);

		if (sessionsUsedByPrincipal.isEmpty()) {
			// No need to keep object in principals Map anymore
			if (logger.isDebugEnabled()) {
				logger.debug("Removing principal " + info.getPrincipal()
						+ " from registry");
			}
			principals.remove(info.getPrincipal());
		}

		if (logger.isTraceEnabled()) {
			logger.trace("Sessions used by '" + info.getPrincipal() + "' : "
					+ sessionsUsedByPrincipal);
		}
		
	}

	

}
