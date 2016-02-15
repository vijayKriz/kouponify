package com.rest.kouponify.common;

import static org.apache.commons.lang3.StringUtils.repeat;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class SqlGenerator {

    public static final String WHERE = " WHERE ";
    public static final String AND = " AND ";
    public static final String OR = " OR ";
    public static final String SELECT = "SELECT ";
    public static final String FROM = "FROM ";
    public static final String DELETE = "DELETE ";
    public static final String COMMA = ", ";
    public static final String PARAM = " = ?";
    private final String allColumnsClause;

    public SqlGenerator(final String allColumnsClause) {
        this.allColumnsClause = allColumnsClause;
    }

    public SqlGenerator() {
        this("*");
    }

    public String count(final TableDescription table) {
        return SELECT + "COUNT(*) " + FROM + table.getFromClause();
    }
    
    public String sum(final TableDescription table, final String columnName) {
        return SELECT + "SUM(" + columnName +") " + FROM + table.getFromClause();
    }

    public String deleteById(final TableDescription table) {
        return DELETE + FROM + table.getName() + whereByIdClause(table);
    }

    private String whereByIdClause(final TableDescription table) {
        final StringBuilder whereClause = new StringBuilder(WHERE);
        for (final Iterator<String> idColIterator = table.getIdColumns().iterator(); idColIterator.hasNext(); ) {
            whereClause.append(idColIterator.next()).append(PARAM);
            if (idColIterator.hasNext()) {
                whereClause.append(AND);
            }
        }
        return whereClause.toString();
    }

    private String whereClause(final List<String> whereColumns) {
        final StringBuilder whereClause = new StringBuilder(WHERE);
        for (final Iterator<String> idColIterator = whereColumns.iterator(); idColIterator.hasNext();) {
            whereClause.append(idColIterator.next()).append(PARAM);
            if (idColIterator.hasNext()) {
                whereClause.append(AND);
            }
        }
        return whereClause.toString();
    }

    private String whereByIdsClause(final TableDescription table, final int idsCount) {
        final List<String> idColumnNames = table.getIdColumns();
        if (idColumnNames.size() > 1) {
            return whereByIdsWithMultipleIdColumns(idsCount, idColumnNames);
        }
        return whereByIdsWithSingleIdColumn(idsCount, idColumnNames.get(0));
    }

    private String whereByIdsWithMultipleIdColumns(final int idsCount, final List<String> idColumnNames) {
        final int idColumnsCount = idColumnNames.size();
        final StringBuilder whereClause = new StringBuilder(WHERE);
        final int totalParams = idsCount * idColumnsCount;
        for (int idColumnIdx = 0; idColumnIdx < totalParams; idColumnIdx += idColumnsCount) {
            if (idColumnIdx > 0) {
                whereClause.append(OR);
            }
            whereClause.append("(");
            for (int i = 0; i < idColumnsCount; ++i) {
                if (i > 0) {
                    whereClause.append(AND);
                }
                whereClause.append(idColumnNames.get(i)).append(" = ?");
            }
            whereClause.append(")");
        }
        return whereClause.toString();
    }

    private String whereByIdsWithSingleIdColumn(final int idsCount, final String idColumn) {
        final StringBuilder whereClause = new StringBuilder(WHERE);
        return whereClause.
                append(idColumn).
                append(" IN (").
                append(repeat("?", COMMA, idsCount)).
                append(")").
                toString();
    }

    public String getPartialInClause(final int idsCount, final String idColumn) {
        final StringBuilder whereClause = new StringBuilder();
        return whereClause.append(idColumn).append(" IN (").append(repeat("?", COMMA, idsCount)).append(")").toString();
    }


    private String whereByCustomQuery(final String query) {
        final StringBuilder whereClause = new StringBuilder(WHERE);
        return whereClause.append(query).toString();
    }

    public String selectAll(final TableDescription table) {
        return SELECT + allColumnsClause + " " + FROM + table.getFromClause();
    }

    public String selectAll(final TableDescription table, final Pageable page) {
        return selectAll(table, page.getSort()) + limitClause(page);
    }

    public String selectAll(final TableDescription table, final Sort sort) {
        return selectAll(table) + sortingClauseIfRequired(sort);
    }

    protected String limitClause(final Pageable page) {
        final int offset = page.getPageNumber() * page.getPageSize();
        return " LIMIT " + offset + COMMA + page.getPageSize();
    }

    public String selectById(final TableDescription table) {
        return selectAll(table) + whereByIdClause(table);
    }

    public String selectByIds(final TableDescription table, final int idsCount) {
        switch (idsCount) {
            case 0:
                return selectAll(table);
            case 1:
                return selectById(table);
            default:
                return selectAll(table) + whereByIdsClause(table, idsCount);
        }
    }

    public String selectByCustomColumnIds(final TableDescription table, final int idsCount, final String columnName) {
        return selectAll(table) + whereByIdsWithSingleIdColumn(idsCount, columnName);
    }

    public String selectByCustomQuery(final TableDescription table, final String query){
        return selectAll(table) + whereByCustomQuery(query);
    }

    public String selectByCustomQuery(final TableDescription table, final List<String> whereColumns) {
        return selectAll(table) + whereClause(whereColumns);
    }

    public String selectByCustomQuery(final TableDescription table, final String query, final Pageable page){
        return selectAll(table) + whereByCustomQuery(query) + sortingClauseIfRequired(page.getSort()) + limitClause(page);
    }

    protected String sortingClauseIfRequired(final Sort sort) {
        if (sort == null) {
            return "";
        }
        final StringBuilder orderByClause = new StringBuilder();
        orderByClause.append(" ORDER BY ");
        for(final Iterator<Sort.Order> iterator = sort.iterator(); iterator.hasNext();) {
            final Sort.Order order = iterator.next();
            orderByClause.
            append(order.getProperty()).
            append(" ").
            append(order.getDirection().toString());
            if (iterator.hasNext()) {
                orderByClause.append(COMMA);
            }
        }
        return orderByClause.toString();
    }

    public String updateById(final TableDescription table, final List<String> columns) {
        final StringBuilder updateQuery = new StringBuilder("UPDATE " + table.getName() + " SET ");
        for (final Iterator<String> iterator = columns.iterator(); iterator.hasNext();) {
            final String column = iterator.next();
            updateQuery.append(column).append(" = ?");
            if (iterator.hasNext()) {
                updateQuery.append(COMMA);
            }
        }
        updateQuery.append(whereByIdClause(table));
        return updateQuery.toString();
    }

    public String update(final String table, final List<String> columns, final List<String> whereColumns) {
        final StringBuilder updateQuery = new StringBuilder("UPDATE " + table + " SET ");
        for (final Iterator<String> iterator = columns.iterator(); iterator.hasNext();) {
            final String column = iterator.next();
            updateQuery.append(column).append(" = ?");
            if (iterator.hasNext()) {
                updateQuery.append(COMMA);
            }
        }
        updateQuery.append(whereClause(whereColumns));
        return updateQuery.toString();
    }

    public String create(final TableDescription table, final Map<String, Object> columns) {
        final StringBuilder createQuery = new StringBuilder("INSERT INTO " + table.getName() + " (");
        appendColumnNames(createQuery, columns.keySet());
        createQuery.append(")").append(" VALUES (");
        createQuery.append(repeat("?", COMMA, columns.size()));
        return createQuery.append(")").toString();
    }

    private void appendColumnNames(final StringBuilder createQuery, final Set<String> columnNames) {
        for(final Iterator<String> iterator = columnNames.iterator(); iterator.hasNext();) {
            final String column = iterator.next();
            createQuery.append(column);
            if (iterator.hasNext()) {
                createQuery.append(COMMA);
            }
        }
    }


    public String deleteAll(final TableDescription table) {
        return DELETE + FROM + table.getName();
    }

    public String countById(final TableDescription table) {
        return count(table) + whereByIdClause(table);
    }

    public String countByCustomQuery(final TableDescription table, final List<String> columns) {
        return count(table) + whereClause(columns);
    }

    public String countByCustomQuery(final TableDescription table, final String query) {
        return count(table) + whereByCustomQuery(query);
    }

    public String sumByCustomQuery(final TableDescription table, final String column, final List<String> whereColumns) {
        return sum(table, column) + whereClause(whereColumns);
    }

    public String getAllColumnsClause() {
        return allColumnsClause;
    }

    public String selectByCustomQuery(final TableDescription table, final List<String> columns, final Pageable page) {
        return selectAll(table) + whereClause(columns) + sortingClauseIfRequired(page.getSort())
                + limitClause(page);
    }
}
