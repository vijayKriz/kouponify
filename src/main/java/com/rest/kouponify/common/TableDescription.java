package com.rest.kouponify.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Created by vijay on 2/15/16.
 */
public class TableDescription {

    private final String name;
    private final List<String> idColumns;
    private final String fromClause;

    public TableDescription(final String name, final String fromClause, final String... idColumns) {
        Assert.notNull(name);
        Assert.notNull(idColumns);
        Assert.isTrue(idColumns.length > 0, "At least one primary key column must be provided");

        this.name = name;
        this.idColumns = Collections.unmodifiableList(Arrays.asList(idColumns));
        if (StringUtils.hasText(fromClause)) {
            this.fromClause = fromClause;
        } else {
            this.fromClause = name;
        }
    }

    public TableDescription(final String name, final String idColumn) {
        this(name, null, idColumn);
    }

    public String getName() {
        return name;
    }

    public List<String> getIdColumns() {
        return idColumns;
    }

    public String getFromClause() {
        return fromClause;
    }
}