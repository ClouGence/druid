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
package com.alibaba.druid.sql.dialect.db2.ast.stmt;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.statement.SQLAlterStatement;
import com.alibaba.druid.sql.dialect.db2.ast.DB2Statement;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2ASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class DB2RenameTableStatement extends SQLStatementImpl implements DB2Statement, SQLAlterStatement {
    private SQLName name;
    private SQLName to;

    public DB2RenameTableStatement() {
        this.dbType = DbType.db2;
    }

    public DB2RenameTableStatement(DbType dbType) {
        this.dbType = dbType;
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public SQLName getTo() {
        return to;
    }

    public void setTo(SQLName to) {
        this.to = to;
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v instanceof DB2ASTVisitor) {
            this.accept0((DB2ASTVisitor) v);
        } else {
            throw new UnsupportedOperationException(this.getClass().getName());
        }
    }

    @Override
    public void accept0(DB2ASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, to);
        }
        visitor.endVisit(this);
    }
}
