/*
 * Copyright contributors to the Galasa project
 */
package dev.galasa.githubissue.internal.properties;

import dev.galasa.framework.spi.ConfigurationPropertyStoreException;
import dev.galasa.framework.spi.cps.CpsProperties;
import dev.galasa.githubissue.GitHubIssueManagerException;

public class GitHubIssueInstanceRepository extends CpsProperties {
	
	public static String get() throws GitHubIssueManagerException {
		try {
			return getStringNulled(GitHubIssuePropertiesSingleton.cps(), "instance", "repository");
		} catch (ConfigurationPropertyStoreException e) {
			throw new GitHubIssueManagerException();
		}
	}
	
	

}
