package com.essaid.hapi.ts1;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "terminology")
public class TSProperties {

  private boolean enable_terminology;
  private int server_conformance_cache_milliseconds;
  private int terminology_conformance_cache_milliseconds;

  private boolean enable_auth;
  // https://docs.spring.io/spring-security/reference/servlet/authorization/expression-based.html
  private String enabled_access;
  private String disabled_access;

  private String write_user;
  private String write_password;
  private String write_role;

  private String read_user;
  private String read_password;
  private String read_role;


  //	private String auth_realm;
  private List<String> allowed_ips;

  private int lucene_max_clause;

//	public List<String> getCodesystems() {
//		return codesystems;
//	}
//
//	public void setCodesystems(List<String> codesystems) {
//		this.codesystems = codesystems;
//	}

  public boolean isEnable_terminology() {
    return enable_terminology;
  }

  public void setEnable_terminology(boolean enable_terminology) {
    this.enable_terminology = enable_terminology;
  }

  public int getServer_conformance_cache_milliseconds() {
    return server_conformance_cache_milliseconds;
  }

  public void setServer_conformance_cache_milliseconds(int server_conformance_cache_milliseconds) {
    this.server_conformance_cache_milliseconds = server_conformance_cache_milliseconds;
  }

  public int getTerminology_conformance_cache_milliseconds() {
    return terminology_conformance_cache_milliseconds;
  }

  public void setTerminology_conformance_cache_milliseconds(
      int terminology_conformance_cache_milliseconds) {
    this.terminology_conformance_cache_milliseconds = terminology_conformance_cache_milliseconds;
  }

  public String getWrite_password() {
    return write_password;
  }

  public void setWrite_password(String write_password) {
    this.write_password = write_password;
  }

  public String getRead_password() {
    return read_password;
  }

  public void setRead_password(String read_password) {
    this.read_password = read_password;
  }

  public String getWrite_user() {
    return write_user;
  }

  public void setWrite_user(String write_user) {
    this.write_user = write_user;
  }

  public String getRead_user() {
    return read_user;
  }

  public void setRead_user(String read_user) {
    this.read_user = read_user;
  }

//	public String getAuth_realm() {
//		return auth_realm;
//	}
//
//	public void setAuth_realm(String auth_realm) {
//		this.auth_realm = auth_realm;
//	}

  public List<String> getAllowed_ips() {
    return allowed_ips;
  }

  public void setAllowed_ips(List<String> allowed_ips) {
    this.allowed_ips = allowed_ips;
  }

  public boolean isEnable_auth() {
    return enable_auth;
  }

  public void setEnable_auth(boolean enable_auth) {
    this.enable_auth = enable_auth;
  }

  public String getWrite_role() {
    return write_role;
  }

  public void setWrite_role(String write_role) {
    this.write_role = write_role;
  }


  public String getRead_role() {
    return read_role;
  }

  public void setRead_role(String read_role) {
    this.read_role = read_role;
  }

  public String getEnabled_access() {
    return enabled_access;
  }

  public void setEnabled_access(String enabled_access) {
    this.enabled_access = enabled_access;
  }

  public String getDisabled_access() {
    return disabled_access;
  }

  public void setDisabled_access(String disabled_access) {
    this.disabled_access = disabled_access;
  }

  public int getLucene_max_clause() {
    return lucene_max_clause;
  }

  public void setLucene_max_clause(int lucene_max_clause) {
    this.lucene_max_clause = lucene_max_clause;
  }
}
