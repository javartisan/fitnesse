// Copyright (C) 2003-2009 by Object Mentor, Inc. All rights reserved.
// Released under the terms of the CPL Common Public License version 1.0.
package fitnesse;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import fitnesse.authentication.Authenticator;
import fitnesse.components.Logger;
import fitnesse.html.template.PageFactory;
import fitnesse.responders.ResponderFactory;
import fitnesse.testsystems.TestSystemFactory;
import fitnesse.testsystems.TestSystemListener;
import fitnesse.wiki.RecentChanges;
import fitnesse.wiki.SystemVariableSource;
import fitnesse.wiki.UrlPathVariableSource;
import fitnesse.wiki.WikiPage;
import fitnesse.wiki.WikiPageFactory;
import fitnesse.wiki.fs.VersionsController;
import fitnesse.wikitext.parser.VariableSource;

public class FitNesseContext {
  private static final String WIKI_PROTOCOL_PROPERTY = "wiki.protocol";
  public static final String SSL_PARAMETER_CLASS_PROPERTY = "wiki.protocol.ssl.parameter.class";
  public static final String SSL_CLIENT_AUTH_PROPERTY = "wiki.protocol.ssl.client.auth";
  public static final String recentChangesDateFormat = "kk:mm:ss EEE, MMM dd, yyyy";
  public static final String rfcCompliantDateFormat = "EEE, d MMM yyyy HH:mm:ss Z";
  public static final String testResultsDirectoryName = "testResults";

  public final FitNesseVersion version;
  public final FitNesse fitNesse;

  public final TestSystemFactory testSystemFactory;
  public final TestSystemListener testSystemListener;

  public final int port;
  private final WikiPageFactory wikiPageFactory;
  public final String rootPath;
  private final String rootDirectoryName;
  public final String contextRoot;
  public final ResponderFactory responderFactory;
  public final PageFactory pageFactory;

  public final SystemVariableSource variableSource;
  public final VersionsController versionsController;
  public final RecentChanges recentChanges;
  public final Logger logger;
  public final Authenticator authenticator;
  public final boolean useHTTPS;
  public String sslParameterClassName;
  public final boolean sslClientAuth;
  private final Properties properties;

  protected FitNesseContext(FitNesseVersion version, WikiPageFactory wikiPageFactory, String rootPath,
                            String rootDirectoryName, String contextRoot, VersionsController versionsController,
                            RecentChanges recentChanges, int port,
                            Authenticator authenticator, Logger logger,
                            TestSystemFactory testSystemFactory, TestSystemListener testSystemListener,
                            Properties properties) {
    super();
    this.version = version;
    this.wikiPageFactory = wikiPageFactory;
    this.rootPath = rootPath;
    this.rootDirectoryName = rootDirectoryName;
    this.contextRoot = contextRoot;
    this.versionsController = versionsController;
    this.recentChanges = recentChanges;
    this.port = port;
    this.authenticator = authenticator;
    this.logger = logger;
    this.testSystemFactory = testSystemFactory;
    this.testSystemListener = testSystemListener;
    this.properties = properties;
    responderFactory = new ResponderFactory(getRootPagePath());
    variableSource = new SystemVariableSource(properties);
    fitNesse = new FitNesse(this);
    pageFactory = new PageFactory(this);
    String protocol = variableSource.getProperty(WIKI_PROTOCOL_PROPERTY);
    this.useHTTPS = (protocol == null ?  false : (protocol.equalsIgnoreCase("https")));
    String clientAuth = variableSource.getProperty(SSL_CLIENT_AUTH_PROPERTY);
    this.sslClientAuth = (clientAuth == null) ? false : (clientAuth.equalsIgnoreCase("required"));
    this.sslParameterClassName = variableSource.getProperty(SSL_PARAMETER_CLASS_PROPERTY);
  }

  public WikiPage getRootPage() {
    return getRootPage(variableSource);
  }

  public WikiPage getRootPage(Map<String, String> customProperties) {
    return getRootPage(new UrlPathVariableSource(variableSource, customProperties));
  }

  private WikiPage getRootPage(VariableSource variableSource) {
    return wikiPageFactory.makePage(new File(rootPath, rootDirectoryName), rootDirectoryName, null, variableSource);

  }
  public File getTestHistoryDirectory() {
    String testHistoryPath = getProperty("test.history.path");
    if (testHistoryPath == null) {
      testHistoryPath = String.format("%s/files/%s", getRootPagePath(), testResultsDirectoryName);
    }
    return new File(testHistoryPath);
  }

  public String getTestProgressPath() {
    return String.format("%s/files/testProgress/", getRootPagePath());
  }

  public String getRootPagePath() {
    return String.format("%s%s%s", rootPath, File.separator, rootDirectoryName);
  }

  public Properties getProperties() {
    return properties;
  }

  public String getProperty(String name) {
    return variableSource.getProperty(name);
  }
}
