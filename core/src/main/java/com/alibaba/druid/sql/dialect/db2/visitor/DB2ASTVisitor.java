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
package com.alibaba.druid.sql.dialect.db2.visitor;

import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.*;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public interface DB2ASTVisitor extends SQLASTVisitor {
    default boolean visit(DB2SelectQueryBlock x) {
        return visit((SQLSelectQueryBlock) x);
    }

    default void endVisit(DB2SelectQueryBlock x) {
        endVisit((SQLSelectQueryBlock) x);
    }

    default boolean visit(DB2ValuesStatement x) {
        return true;
    }

    default void endVisit(DB2ValuesStatement x) {
    }

    default boolean visit(DB2CreateTableStatement x) {
        return visit((SQLCreateTableStatement) x);
    }

    default void endVisit(DB2CreateTableStatement x) {
        endVisit((SQLCreateTableStatement) x);
    }

    default boolean visit(DB2CreateSchemaStatement x) {
        return true;
    }

    default void endVisit(DB2CreateSchemaStatement x) {
    }

    default boolean visit(DB2DropSchemaStatement x) {
        return true;
    }

    default void endVisit(DB2DropSchemaStatement x) {
    }

    default boolean visit(DB2RenameTableStatement x) {
        return true;
    }

    default void endVisit(DB2RenameTableStatement x) {
    }

    default boolean visit(DB2AlterTableDropConstraint x) {
        return true;
    }

    default void endVisit(DB2AlterTableDropConstraint x) {
    }
}
