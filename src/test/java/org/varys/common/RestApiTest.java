/*
 * This file is part of Varys.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Varys.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.varys.common;

import org.junit.Test;
import retrofit2.Call;

import static org.junit.Assert.assertEquals;

public class RestApiTest {

    @Test
    public void getDomainName() {
        final RestApi restApi = new RestApi() {
            @Override
            public String getLabel() {
                return "test rest api";
            }

            @Override
            public String getBaseUrl() {
                return "http://example.com/test";
            }

            @Override
            public boolean isCompatible() {
                return false;
            }

            @Override
            public Call buildStatusCall() {
                return null;
            }

            @Override
            public boolean isOnline() {
                return false;
            }
        };

        assertEquals("example.com", restApi.getDomainName());
    }
}