/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.sql.dialect.db2.parser;

import com.alibaba.druid.sql.parser.Keywords;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;

import java.util.HashMap;
import java.util.Map;

import static com.alibaba.druid.sql.parser.DialectFeature.ParserFeature.ParseAssignItemSkip;

public class DB2Lexer extends Lexer {
    @Override
    protected Keywords loadKeywords() {
        Map<String, Token> map = new HashMap<String, Token>();

        map.putAll(Keywords.DEFAULT_KEYWORDS.getKeywords());

        map.put("FETCH", Token.FETCH);
        map.put("FIRST", Token.FIRST);
        map.put("ONLY", Token.ONLY);
        map.put("OPTIMIZE", Token.OPTIMIZE);
        map.put("OF", Token.OF);
        map.put("CONCAT", Token.CONCAT);
        map.put("CONTINUE", Token.CONTINUE);
        map.put("IDENTITY", Token.IDENTITY);
        map.put("MERGE", Token.MERGE);
        map.put("USING", Token.USING);
        map.put("MATCHED", Token.MATCHED);
        map.put("IF", Token.IF);
        map.put("EXISTS", Token.EXISTS);
        map.put("RESTRICT", Token.RESTRICT);
        map.put("CASCADE", Token.CASCADE);
        map.put("UNIQUE", Token.UNIQUE);
        map.put("FOREIGN", Token.FOREIGN);

        return new Keywords(map);
    }

    public DB2Lexer(String input) {
        super(input);
    }

    public DB2Lexer(String input, SQLParserFeature... features) {
        super(input);
        for (SQLParserFeature feature : features) {
            config(feature, true);
        }
    }

    @Override
    protected void initDialectFeature() {
        super.initDialectFeature();
        this.dialectFeature.configFeature(ParseAssignItemSkip);
    }
}
