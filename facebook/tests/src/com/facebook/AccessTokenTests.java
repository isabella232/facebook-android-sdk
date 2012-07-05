/**
 * Copyright 2012 Facebook
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.facebook;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.test.AndroidTestCase;

public final class AccessTokenTests extends AndroidTestCase {

    public void testEmptyToken() {
        List<String> permissions = list();
		AccessToken token = AccessToken.createEmptyToken(permissions);
		assertSamePermissions(permissions, token);
		assertEquals(token.getToken(), "");
		assertTrue(token.isInvalid());
		assertTrue(token.getExpires().before(new Date()));
	}
	
	public void testEmptyTokenWithPermissions() {
	    List<String> permissions = list("stream_publish");
        AccessToken token = AccessToken.createEmptyToken(permissions);
        assertSamePermissions(permissions, token);
        assertEquals(token.getToken(), "");
        assertTrue(token.isInvalid());
        assertTrue(token.getExpires().before(new Date()));
	}

    public void testFromDialog() {
        List<String> permissions = list("stream_publish", "go_outside_and_play");
        String token = "AnImaginaryTokenValue";

        Bundle bundle = new Bundle();
        bundle.putString("access_token", token);
        bundle.putString("expires_in", "60");

        AccessToken accessToken = AccessToken.createFromDialog(permissions, bundle);
        assertSamePermissions(permissions, accessToken);
        assertEquals(accessToken.getToken(), token);
        assertTrue(!accessToken.isInvalid());
    }

    public void testFromSSO() {
        List<String> permissions = list("stream_publish", "go_outside_and_play");
        String token = "AnImaginaryTokenValue";

        Intent intent = new Intent();
        intent.putExtra("access_token", token);
        intent.putExtra("expires_in", "60");
        intent.putExtra("extra_extra", "Something unrelated");

        AccessToken accessToken = AccessToken.createFromSSO(permissions, intent);
        assertSamePermissions(permissions, accessToken);
        assertEquals(accessToken.getToken(), token);
        assertTrue(!accessToken.isInvalid());
    }

	public void testFromCache() {
	    ArrayList<String> permissions = list("stream_publish", "go_outside_and_play");
	    String token = "AnImaginaryTokenValue";
	    Date later = nowPlusSeconds(60);
	    Date earlier = nowPlusSeconds(-60);

	    Bundle bundle = new Bundle();
	    bundle.putString(TokenCache.TOKEN_KEY, token);
	    Utility.putBundleDate(bundle, TokenCache.EXPIRATION_DATE_KEY, later);
	    bundle.putBoolean(TokenCache.IS_SSO_KEY, true);
	    Utility.putBundleDate(bundle, TokenCache.LAST_REFRESH_DATE_KEY, earlier);
	    bundle.putStringArrayList(TokenCache.PERMISSIONS_KEY, permissions);

	    AccessToken accessToken = AccessToken.createFromCache(bundle);
        assertSamePermissions(permissions, accessToken);
        assertEquals(accessToken.getToken(), token);
        assertTrue(!accessToken.isInvalid());
	}

	private ArrayList<String> list(String...ss) {
	    ArrayList<String> result = new ArrayList<String>();
	    
	    for (String s : ss) {
	        result.add(s);
	    }
	    
	    return result;
	}

	private static Date nowPlusSeconds(long offset) {
	    return new Date(new Date().getTime() + (offset * 1000L));
	}
	
	private static void assertSamePermissions(List<String> expected, AccessToken actual) {
	    if (expected == null) {
	        assertEquals(actual.getPermissions(), null);
	    } else {
	        for (String p : expected) {
	            assertTrue(actual.getPermissions().contains(p));
	        }
	        for (String p : actual.getPermissions()) {
	            assertTrue(expected.contains(p));
	        }
	    }
	}
}