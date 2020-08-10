/*
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

package ir.moke.jca.app;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

public interface JsonUtils {

    Jsonb jsonb = JsonbBuilder.create();

    static String toJson(Object object) {
        return jsonb.toJson(object);
    }

    static <T> T fromJson(String json, Class<T> tClass) {
        return jsonb.fromJson(json, tClass);
    }
}
