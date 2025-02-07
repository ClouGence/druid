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

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.*;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

import java.util.List;

import static com.alibaba.druid.sql.parser.Token.*;

public class DB2StatementParser extends SQLStatementParser {
    public DB2StatementParser(String sql) {
        super(new DB2ExprParser(sql));
    }

    public DB2StatementParser(String sql, SQLParserFeature... features) {
        super(new DB2ExprParser(sql, features));
    }

    public DB2StatementParser(Lexer lexer) {
        super(new DB2ExprParser(lexer));
    }

    public DB2SelectParser createSQLSelectParser() {
        return new DB2SelectParser(this.exprParser, selectListCache);
    }

    public boolean parseStatementListDialect(List<SQLStatement> statementList) {
        if (lexer.token() == Token.VALUES) {
            lexer.nextToken();
            DB2ValuesStatement stmt = new DB2ValuesStatement();
            stmt.setExpr(this.exprParser.expr());
            statementList.add(stmt);
            return true;
        }

        return false;
    }

    @Override
    public SQLStatement parseCreateSchema() {
        accept(Token.CREATE);
        accept(Token.SCHEMA);

        DB2CreateSchemaStatement stmt = new DB2CreateSchemaStatement();

        stmt.setSchemaName(this.exprParser.name());

        while (lexer.token() != SEMI && !lexer.isEOF()) {
            if (lexer.token() == Token.CREATE) {
                Lexer.SavePoint mark = lexer.markOut();
                lexer.nextToken();
                if (lexer.token() == Token.TABLE) {
                    lexer.reset(mark);
                    stmt.getCreateStatements().add(this.parseCreateTable());
                    continue;
                } else if (lexer.token() == VIEW) {
                    lexer.reset(mark);
                    stmt.getCreateStatements().add(this.parseCreateView());
                    continue;
                } else if (lexer.token() == INDEX) {
                    lexer.reset(mark);
                    stmt.getCreateStatements().add(this.parseCreateIndex());
                    continue;
                } else if (lexer.token() == SEQUENCE) {
                    lexer.reset(mark);
                    stmt.getCreateStatements().add(this.parseCreateSequence());
                    continue;
                } else if (lexer.token() == TRIGGER) {
                    lexer.reset(mark);
                    stmt.getCreateStatements().add(this.parseCreateTrigger());
                    continue;
                }
            }

            throw new ParserException("syntax error. " + lexer.info());
        }

        return stmt;
    }

    @Override
    protected SQLDropStatement parseDropSchema(boolean physical) {
        DB2DropSchemaStatement stmt = new DB2DropSchemaStatement();

        if (lexer.token() == Token.SCHEMA) {
            lexer.nextToken();
        }

        if (lexer.token() == Token.IF) {
            lexer.nextToken();
            accept(Token.EXISTS);
            stmt.setIfExists(true);
        }

        SQLName name = this.exprParser.name();
        stmt.setSchemaName(name);

        if (lexer.token() == Token.CASCADE) {
            lexer.nextToken();
            stmt.setCascade(true);
        } else {
            stmt.setCascade(false);
        }
        if (lexer.token() == Token.RESTRICT) {
            lexer.nextToken();
            stmt.setRestrict(true);
        }

        return stmt;
    }

    public SQLCreateTableParser getSQLCreateTableParser() {
        return new DB2CreateTableParser(this.exprParser);
    }

    protected SQLAlterTableAlterColumn parseAlterColumn() {
        if (lexer.token() == Token.COLUMN) {
            lexer.nextToken();
        }

        SQLColumnDefinition column = this.exprParser.parseColumn();

        SQLAlterTableAlterColumn alterColumn = new SQLAlterTableAlterColumn();
        alterColumn.setColumn(column);

        if (column.getDataType() == null && column.getConstraints().isEmpty()) {
            if (lexer.token() == Token.SET) {
                lexer.nextToken();
                if (lexer.token() == Token.NOT) {
                    lexer.nextToken();
                    accept(Token.NULL);
                    alterColumn.setSetNotNull(true);
                } else if (lexer.token() == Token.DEFAULT) {
                    lexer.nextToken();
                    SQLExpr defaultValue = this.exprParser.expr();
                    alterColumn.setSetDefault(defaultValue);
                } else if (lexer.identifierEquals(FnvHash.Constants.DATA)) {
                    lexer.nextToken();
                    acceptIdentifier("TYPE");
                    SQLDataType dataType = this.exprParser.parseDataType();
                    alterColumn.setDataType(dataType);
                } else {
                    throw new ParserException("TODO : " + lexer.info());
                }
            } else if (lexer.token() == Token.DROP) {
                lexer.nextToken();
                if (lexer.token() == Token.NOT) {
                    lexer.nextToken();
                    accept(Token.NULL);
                    alterColumn.setDropNotNull(true);
                } else {
                    accept(Token.DEFAULT);
                    alterColumn.setDropDefault(true);
                }
            }
        }

        return alterColumn;
    }

    @Override
    public SQLDeleteStatement parseDeleteStatement() {
        SQLDeleteStatement deleteStatement = new SQLDeleteStatement(getDbType());

        if (lexer.token() == Token.DELETE) {
            lexer.nextToken();
            if (lexer.token() == (Token.FROM)) {
                lexer.nextToken();
            }

            if (lexer.token() == Token.COMMENT) {
                lexer.nextToken();
            }

            SQLName tableName = exprParser.name();

            deleteStatement.setTableName(tableName);

            if (lexer.token() == Token.FROM) {
                lexer.nextToken();
                SQLTableSource tableSource = createSQLSelectParser().parseTableSource();
                deleteStatement.setFrom(tableSource);
            }

            // try to parse alias
            deleteStatement.setAlias(tableAlias());
        }

        if (lexer.token() == (Token.WHERE)) {
            lexer.nextToken();
            SQLExpr where = this.exprParser.expr();
            deleteStatement.setWhere(where);
        }

        return deleteStatement;
    }

    @Override
    public SQLStatement parseTruncate() {
        accept(Token.TRUNCATE);
        if (!lexer.nextIf(Token.TABLE)) {
            lexer.nextIfIdentifier("TABLE");
        }
        SQLTruncateStatement stmt = new SQLTruncateStatement(getDbType());

        SQLName name = this.exprParser.name();
        stmt.addTableSource(name);

        for (;;) {
            if (lexer.token() == Token.DROP) {
                lexer.nextToken();
                acceptIdentifier("STORAGE");
                stmt.setDropStorage(true);
                continue;
            }

            if (lexer.identifierEquals("REUSE")) {
                lexer.nextToken();
                acceptIdentifier("STORAGE");
                stmt.setReuseStorage(true);
                continue;
            }

            if (lexer.identifierEquals("IGNORE")) {
                lexer.nextToken();
                accept(Token.DELETE);
                acceptIdentifier("TRIGGERS");
                stmt.setIgnoreDeleteTriggers(true);
                continue;
            }

            if (lexer.token() == Token.RESTRICT) {
                lexer.nextToken();
                accept(Token.WHEN);
                accept(Token.DELETE);
                acceptIdentifier("TRIGGERS");
                stmt.setRestrictWhenDeleteTriggers(true);
                continue;
            }

            if (lexer.token() == Token.CONTINUE) {
                lexer.nextToken();
                accept(Token.IDENTITY);
                continue;
            }

            if (lexer.token() == Token.RESTART) {
                lexer.nextToken();
                accept(Token.IDENTITY);
                stmt.setRestartIdentity(Boolean.TRUE);
                continue;
            }

            if (lexer.identifierEquals("IMMEDIATE")) {
                lexer.nextToken();
                stmt.setImmediate(true);
                continue;
            }

            break;
        }

        return stmt;
    }

    @Override
    public SQLStatement parseRename() {
        if (!lexer.nextIf(Token.TABLE)) {
            lexer.nextIfIdentifier("TABLE");
            lexer.nextToken();
            lexer.nextToken();
            return parseRenameTable();
        } else {
            throw new ParserException("TODO " + lexer.info());
        }
    }

    public SQLStatement parseRenameTable() {
        DB2RenameTableStatement stmt = new DB2RenameTableStatement();
        stmt.setName(this.exprParser.name());
        accept(Token.TO);
        stmt.setTo(this.exprParser.name());
        return stmt;
    }

    public void parseAlterDrop(SQLAlterTableStatement stmt) {
        lexer.nextToken();

        boolean ifExists = false;

        if (lexer.token() == Token.CONSTRAINT) {
            lexer.nextToken();
            SQLAlterTableDropConstraint item = new SQLAlterTableDropConstraint();
            item.setConstraintName(this.exprParser.name());
            if (lexer.token() == RESTRICT) {
                lexer.nextToken();
                item.setRestrict(true);
            } else if (lexer.token() == CASCADE) {
                lexer.nextToken();
                item.setCascade(true);
            }
            stmt.addItem(item);
        } else if (lexer.token() == Token.COLUMN || lexer.identifierEquals(FnvHash.Constants.COLUMNS)) {
            lexer.nextToken();
            SQLAlterTableDropColumnItem item = new SQLAlterTableDropColumnItem();
            parseAlterDropRest(stmt, item);
        } else if (lexer.token() == Token.LITERAL_ALIAS) {
            SQLAlterTableDropColumnItem item = new SQLAlterTableDropColumnItem();
            this.exprParser.names(item.getColumns());

            if (lexer.token() == Token.CASCADE) {
                item.setCascade(true);
                lexer.nextToken();
            }

            stmt.addItem(item);
        } else if (lexer.token() == Token.PARTITION) {
            {
                SQLAlterTableDropPartition dropPartition = parseAlterTableDropPartition(ifExists);
                stmt.addItem(dropPartition);
            }

            while (lexer.token() == COMMA) {
                lexer.nextToken();
                Lexer.SavePoint mark = lexer.mark();
                if (lexer.token() == Token.PARTITION) {
                    SQLAlterTableDropPartition dropPartition = parseAlterTableDropPartition(ifExists);
                    stmt.addItem(dropPartition);
                } else {
                    lexer.reset(mark);
                }
            }

        } else if (lexer.token() == Token.INDEX) {
            lexer.nextToken();
            SQLName indexName = this.exprParser.name();
            SQLAlterTableDropIndex item = new SQLAlterTableDropIndex();
            item.setIndexName(indexName);
            stmt.addItem(item);
        } else if (lexer.token() == Token.PRIMARY) {
            lexer.nextToken();
            accept(Token.KEY);
            SQLAlterTableDropPrimaryKey item = new SQLAlterTableDropPrimaryKey();
            stmt.addItem(item);
        } else if (lexer.token() == UNIQUE) {
            lexer.nextToken();
            SQLName uniqueName = this.exprParser.name();
            DB2AlterTableDropConstraint item = new DB2AlterTableDropConstraint();
            item.setConstraintName(uniqueName);
            item.setConstraintType(ConstraintType.Unique);
            stmt.addItem(item);
        } else if (lexer.token() == FOREIGN) {
            lexer.nextToken();
            accept(Token.KEY);
            SQLName foreignName = this.exprParser.name();
            DB2AlterTableDropConstraint item = new DB2AlterTableDropConstraint();
            item.setConstraintName(foreignName);
            item.setConstraintType(ConstraintType.ForeignKey);
            stmt.addItem(item);
        } else if (lexer.token() == Token.IDENTIFIER) {
            SQLAlterTableDropColumnItem item = new SQLAlterTableDropColumnItem();

            SQLName name = this.exprParser.name();
            item.addColumn(name);
            stmt.addItem(item);

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
            }

            if (lexer.token() == Token.DROP) {
                parseAlterDrop(stmt);
            }
        } else {
            throw new ParserException("TODO " + lexer.info());
        }
    }
}
