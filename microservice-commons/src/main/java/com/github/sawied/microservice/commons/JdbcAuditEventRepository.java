package com.github.sawied.microservice.commons;

import java.time.Instant;
import java.util.List;

import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;

public class JdbcAuditEventRepository implements AuditEventRepository {

	@Override
	public void add(AuditEvent event) {
		
	}

	@Override
	public List<AuditEvent> find(String principal, Instant after, String type) {
		
		return null;
	}

}
