/*
 * Copyright 2016 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.halyard.config.services.v1;

import com.netflix.spinnaker.halyard.config.errors.v1.config.IllegalConfigException;
import com.netflix.spinnaker.halyard.config.errors.v1.config.NoAccountException;
import com.netflix.spinnaker.halyard.config.model.v1.providers.Account;
import com.netflix.spinnaker.halyard.config.model.v1.providers.Provider;
import com.netflix.spinnaker.halyard.config.model.v1.providers.Providers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This service is meant to be autowired into any service or controller that needs to inspect the current halconfigs
 * deployments.
 */
@Component
public class AccountService {
  @Autowired
  ProviderService providerService;

  public Account getAccount(String deploymentName, String providerName, String accountName) {
    Provider provider = providerService.getProvider(deploymentName, providerName);

    List<Account> accounts = provider.getAccounts();

    List<Account> matchingAccounts = accounts
        .stream()
        .filter(a -> a.getName().equals(accountName))
        .collect(Collectors.toList());

    switch (matchingAccounts.size()) {
      case 0:
        throw new NoAccountException("No account in " + NamingService.account(deploymentName, providerName, accountName) + " could be found");
      case 1:
        return matchingAccounts.get(0);
      default:
        throw new IllegalConfigException("Multiple accounts in " + NamingService.account(deploymentName, providerName, accountName) + " were found");
    }
  }
}