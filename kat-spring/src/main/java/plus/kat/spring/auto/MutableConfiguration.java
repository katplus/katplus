/*
 * Copyright 2022 Kat+ Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package plus.kat.spring.auto;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import plus.kat.*;
import plus.kat.spare.*;

/**
 * @author kraity
 * @since 0.0.6
 */
@Configuration
public class MutableConfiguration {

    @Bean
    public Context getContext() {
        return Supplier.ins();
    }

    @Bean
    public Supplier getSupplier() {
        return Supplier.ins();
    }
}
