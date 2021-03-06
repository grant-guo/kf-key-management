/*
 * Copyright 2018 Ontario Institute for Cancer Research
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.kidsfirst.keys.get;

import io.kidsfirst.keys.core.LambdaRequestHandler;
import io.kidsfirst.keys.core.dao.SecretDao;
import io.kidsfirst.keys.core.model.Secret;
import io.kidsfirst.keys.core.utils.KMSUtils;
import org.json.simple.JSONObject;

import java.util.List;

public class GetSecret extends LambdaRequestHandler{


  @Override
  public String processEvent(JSONObject event, String userId) throws IllegalArgumentException {

    String service = getService(event);
    if (service == null) {
        throw new IllegalArgumentException("Required Field [type] missing in query string.");
    }

    List<Secret> allSecrets = SecretDao.getSecret(service, userId);


    if (!allSecrets.isEmpty()) {
    Secret secret = allSecrets.get(0);
      // TODO: decrypt secretValue
      String secretValue = secret.getSecret();
      return KMSUtils.decrypt(secretValue);

    } else {
      // If no data available, empty response
      return "";

    }
  }

  public String getService(JSONObject event) {
    // 'type' is a query param, passed in the event object:
    //  event.queryStringParameters.service

    JSONObject queryParam = (JSONObject) event.get("queryStringParameters");
    String service = (String) queryParam.getOrDefault("service", null);

    return service;
  }

}