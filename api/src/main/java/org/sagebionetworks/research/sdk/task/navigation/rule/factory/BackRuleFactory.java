/*
 *    Copyright 2017 Sage Bionetworks
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package org.sagebionetworks.research.sdk.task.navigation.rule.factory;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.sagebionetworks.research.sdk.step.Step;
import org.sagebionetworks.research.sdk.task.navigation.rule.NavigationRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by liujoshua on 10/13/2017.
 */

public class BackRuleFactory implements NavigationRuleFactory<NavigationRule.Back>{
    private static final Logger LOGGER = LoggerFactory.getLogger(BackRuleFactory.class);

    @Nullable
    @Override
    public NavigationRule.Back create(@NonNull Step step) {
        checkNotNull(step);

        NavigationRule.Back rule = null;
        LOGGER.debug("Creating back rule for step: " + step, ", created: " + rule);
        return rule;
    }
}