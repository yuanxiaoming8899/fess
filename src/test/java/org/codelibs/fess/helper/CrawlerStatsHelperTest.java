/*
 * Copyright 2012-2022 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.unit.UnitFessTestCase;

public class CrawlerStatsHelperTest extends UnitFessTestCase {

    private static final Logger logger = LogManager.getLogger(CrawlerStatsHelperTest.class);

    private CrawlerStatsHelper crawlerStatsHelper;

    private ThreadLocal<String> localLogMsg = new ThreadLocal<>();

    @Override
    public void setUp() throws Exception {
        super.setUp();
        crawlerStatsHelper = new CrawlerStatsHelper() {
            @Override
            protected void log(final StringBuilder buf) {
                localLogMsg.set(buf.toString());
            }
        };
        crawlerStatsHelper.init();
    }

    public void test_beginDone() {
        String key = "test";
        crawlerStatsHelper.begin(key);
        crawlerStatsHelper.done(key);
        logger.info(localLogMsg.get());
        String[] values = localLogMsg.get().split("\t");
        assertEquals(3, values.length);
        assertEquals("url:test", values[0]);
        assertTrue(values[1].startsWith("time:"));
        assertTrue(values[2].startsWith("done:"));

        localLogMsg.remove();
        crawlerStatsHelper.done(key);
        assertNull(localLogMsg.get());
    }

    public void test_beginDoneWithRecord1() {
        String key = "test";
        crawlerStatsHelper.begin(key);
        crawlerStatsHelper.record(key, "aaa");
        crawlerStatsHelper.done(key);
        logger.info(localLogMsg.get());
        String[] values = localLogMsg.get().split("\t");
        assertEquals(4, values.length);
        assertEquals("url:test", values[0]);
        assertTrue(values[1].startsWith("time:"));
        assertTrue(values[2].startsWith("done:"));
        assertTrue(values[3].startsWith("aaa:"));

        localLogMsg.remove();
        crawlerStatsHelper.done(key);
        assertNull(localLogMsg.get());
    }

    public void test_beginDoneWithRecord2() {
        String key = "test";
        crawlerStatsHelper.begin(key);
        crawlerStatsHelper.record(key, "aaa");
        crawlerStatsHelper.record(key, "bbb");
        crawlerStatsHelper.done(key);
        logger.info(localLogMsg.get());
        String[] values = localLogMsg.get().split("\t");
        assertEquals(5, values.length);
        assertEquals("url:test", values[0]);
        assertTrue(values[1].startsWith("time:"));
        assertTrue(values[2].startsWith("done:"));
        assertTrue(values[3].startsWith("aaa:"));
        assertTrue(values[4].startsWith("bbb:"));

        localLogMsg.remove();
        crawlerStatsHelper.done(key);
        assertNull(localLogMsg.get());
    }
}
