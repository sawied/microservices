package com.github.sawied.microservice.oauth2.jpa;

public interface AccountLogRepositoryCutom {

	Long countLogByNameAndTimestamp(String username, String timestamp);

}
