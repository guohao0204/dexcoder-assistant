package com.dexcoder.jdbc.build;

import com.dexcoder.jdbc.BoundSql;
import com.dexcoder.jdbc.NameHandler;
import com.dexcoder.jdbc.exceptions.JdbcAssistantException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by liyd on 2015-12-7.
 */
public class InsertBuilder extends AbstractFieldBuilder {

    private static final String COMMAND_OPEN = "INSERT INTO ";

    public void addField(String fieldName, String sqlOperator, String fieldOperator, AutoFieldType type, Object value) {
        AutoField autoField = buildAutoField(fieldName, sqlOperator, fieldOperator, type, value);
        this.autoFields.put(fieldName, autoField);
    }

    public void addCondition(String fieldName, String sqlOperator, String fieldOperator, AutoFieldType type, Object value) {
        throw new JdbcAssistantException("InsertBuilder不支持设置条件");
    }

    public BoundSql build(Class<?> clazz, Object entity, boolean isIgnoreNull, NameHandler nameHandler) {
        super.mergeEntityFields(entity, AutoFieldType.INSERT, nameHandler);
        StringBuilder sql = new StringBuilder(COMMAND_OPEN);
        StringBuilder args = new StringBuilder("(");
        List<Object> params = new ArrayList<Object>();
        String tableName = nameHandler.getTableName(clazz, getFields());
        sql.append(tableName).append(" (");

        for (Map.Entry<String, AutoField> entry : getFields().entrySet()) {
            String columnName = nameHandler.getColumnName(entry.getKey());
            sql.append(columnName).append(",");
            AutoField autoField = entry.getValue();
            if (autoField.getType() == AutoFieldType.PK_VALUE_NAME) {
                args.append(autoField.getValue());
            } else {
                args.append("?");
            }
            args.append(",");
            params.add(autoField.getValue());
        }
        sql.deleteCharAt(sql.length() - 1);
        args.deleteCharAt(args.length() - 1);
        sql.append(")").append(" VALUES ").append(args.append(")"));
        return new CriteriaBoundSql(sql.toString(), params);
    }
}
