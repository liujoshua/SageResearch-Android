/*
 * BSD 3-Clause License
 *
 * Copyright 2018  Sage Bionetworks. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1.  Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2.  Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * 3.  Neither the name of the copyright holder(s) nor the names of any contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission. No license is granted to the trademarks of
 * the copyright holders even if such marks are included in this software.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sagebionetworks.research.domain.inject;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.sagebionetworks.research.domain.RuntimeTypeAdapterFactory;
import org.sagebionetworks.research.domain.inject.GsonModule.ClassKey;
import org.sagebionetworks.research.domain.step.ActiveUIStepBase;
import org.sagebionetworks.research.domain.step.FormUIStepBase;
import org.sagebionetworks.research.domain.step.SectionStepBase;
import org.sagebionetworks.research.domain.step.Step;
import org.sagebionetworks.research.domain.step.StepBase;
import org.sagebionetworks.research.domain.step.UIStepBase;
import org.sagebionetworks.research.domain.step.ui.ActiveUIStep;
import org.sagebionetworks.research.domain.step.ui.ConcreteUIAction;
import org.sagebionetworks.research.domain.step.ui.FormUIStep;
import org.sagebionetworks.research.domain.step.ui.UIAction;
import org.sagebionetworks.research.domain.step.ui.UIStep;

import java.util.Map;
import java.util.Map.Entry;

import dagger.MapKey;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import dagger.multibindings.IntoSet;

import static org.sagebionetworks.research.domain.inject.GsonModule.createPassthroughDeserializer;

@Module(includes = {GsonModule.class})
public class StepModule {
    /**
     * Annotation marker for registering a Step subclass for polymorphic deserialization.
     */
    @MapKey
    public @interface StepClassKey {
        Class<? extends Step> value();
    }

    // region ActiveUIStepBase
    /**
     * @return json type key for ActiveUIStep.class
     */
    @Provides
    @IntoMap
    @StepClassKey(ActiveUIStep.class)
    static String provideActiveUIStep() {
        return ActiveUIStepBase.TYPE_KEY;
    }

    /**
     * @return The json Deserializer for an active step.
     */
    @Provides
    @IntoMap
    @ClassKey(FormUIStep.class)
    static JsonDeserializer provideFormUIStepDeserializer() {
        return createPassthroughDeserializer(FormUIStepBase.class);
    }

    /**
     * @return The json Deserializer for an active step.
     */
    @Provides
    @IntoMap
    @ClassKey(ActiveUIStep.class)
    static JsonDeserializer provideActiveUIStepDeserializer() {
        return createPassthroughDeserializer(ActiveUIStepBase.class);
    }
    // endregion

    //region SectionStepBase
    /**
     * @return json type key for SectionStepBase.class
     */
    @Provides
    @IntoMap
    @StepClassKey(SectionStepBase.class)
    static String provideSectionStep() {
        return SectionStepBase.TYPE_KEY;
    }
    // endregion

    // region UIStepBase
    /**
     * @return json type key for UIStepBase.class
     */
    @Provides
    @IntoMap
    @StepClassKey(UIStep.class)
    static String provideUIStepMap() {
        return UIStepBase.TYPE_KEY;
    }
    // endregion

    // region FormUIStepBase
    /**
     * @return json type key for FormUIStepBase.class
     */
    @Provides
    @IntoMap
    @StepClassKey(FormUIStepBase.class)
    static String provideFormStep() {
        return FormUIStepBase.TYPE_KEY;
    }
    // endregion

    /**
     * @return GSON runtime type adapter factory for polymorphic deserialization of Step classes
     */
    @IntoSet
    @Provides
    static RuntimeTypeAdapterFactory provideType(Map<Class<? extends Step>, String> stepClasses) {
        RuntimeTypeAdapterFactory<Step> stepAdapterFactory = RuntimeTypeAdapterFactory.of(Step.class, Step.KEY_TYPE);

        for (Entry<Class<? extends Step>, String> stepClassEntry : stepClasses.entrySet()) {
            stepAdapterFactory.registerSubtype(stepClassEntry.getKey(), stepClassEntry.getValue());
        }
        return stepAdapterFactory.registerDefaultType(StepBase.class);
    }

    @Provides
    @IntoMap
    @ClassKey(UIAction.class)
    static JsonDeserializer provideUIActionDeseriazlier() {
        return createPassthroughDeserializer(ConcreteUIAction.class);
    }

    @Provides
    @IntoMap
    @ClassKey(UIStep.class)
    static JsonDeserializer provideUIStepDeserizlier() {
        return createPassthroughDeserializer(UIStepBase.class);
    }


    // region JSON Parsing
    /**
     * Returns the string corresponding to the given key in the given json object, or throws a JsonParseExecption if
     * no such String exists.
     *
     * @param json
     *         The json to get the string field from.
     * @param key
     *         The field to get as a string from the given json.
     * @return The string corresponding to the given key in the given json object.
     * @throws JsonParseException
     *         if there is no string corresponding to the given key in the json object.
     */
    public static String getStringFieldNonNull(JsonObject json, String key) throws JsonParseException {
        JsonElement element = json.get(key);
        if (element != null) {
            String result = element.getAsString();
            if (result != null) {
                return result;
            }
        }

        throw new JsonParseException("NonNull field " + key + "of object " + json.toString() + " couldn't be parsed");
    }

    /**
     * Returns the string from the given field or null if the given field has been ommited from the JSON
     *
     * @param json
     *         the json object to get the field from
     * @param key
     *         the name of the field to get from the json object
     * @return The string that corresponds to key or null if no such String exists
     */
    public static String getStringFieldNullable(JsonObject json, String key) {
        JsonElement element = json.get(key);
        if (element != null) {
            return element.getAsString();
        }

        return null;
    }
    // endregion
}
