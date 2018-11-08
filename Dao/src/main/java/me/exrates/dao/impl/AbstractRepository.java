package me.exrates.dao.impl;

import lombok.SneakyThrows;
import me.exrates.dao.resources.annotations.Column;
import me.exrates.dao.resources.annotations.Table;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractRepository<T> {

    protected final JdbcTemplate jdbcTemplate;

    private final Class<T> entity;
    private final String tableName;

    public AbstractRepository(DataSource dataSource, Class<T> entity){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.entity = entity;
        tableName = (entity.getAnnotation(Table.class)).name();
    }

    public void save(T enity) {

        Map<String, Object> entityParamMap = null;
        try {
            entityParamMap = getEntityParamMap(enity);
        } catch (IllegalAccessException e) {
            e.printStackTrace(); //todo
        }

        String names = "(" + entityParamMap.entrySet().stream().map(e -> e.getKey()).collect(Collectors.joining(",")) + ") ";
        String values = "(" + entityParamMap.entrySet().stream().map(e-> getStringValue(e)).collect(Collectors.joining(",")) + ")";

        jdbcTemplate.execute("INSERT INTO " + tableName + names + "VALUES" + values);
    }

    private String getStringValue(Map.Entry<String, Object> e) {
        Object value = e.getValue();

        if(value instanceof String){
            return "'" + value + "'";
        }

        if(value instanceof Enum) {
            return String.valueOf(((Enum)value).ordinal());
        }

        return value.toString();
    }

    /*
    ** This method will properly works only for entities without super class and primary types!
     */
    @SneakyThrows
    public Optional<T> findByParameters(T entity) {
        if(entity.getClass().getSuperclass() != Object.class) throw new RuntimeException("Not Allowed for entity with superclass != Object");

        Map<String, Object> params = getEntityParamMap(entity);

        StringBuilder query = new StringBuilder("SELECT * FROM " + tableName + " WHERE ");
        for (Map.Entry<String, Object> e: params.entrySet()) {
            query.append(e.getKey() + "=? AND ");
        }
        query.replace(query.lastIndexOf("AND"), query.length(), "");

        try {
            return (Optional<T>) Optional.of(jdbcTemplate.queryForObject(query.toString(), params.values().toArray(), new BeanPropertyRowMapper(this.entity)));
        } catch (EmptyResultDataAccessException e){
            return Optional.ofNullable(null);
        }
    }

    private Map<String, Object> getEntityParamMap(T entity) throws IllegalAccessException {
        Map<String, Object> params = new LinkedHashMap<>();
        for(Field field : entity.getClass().getDeclaredFields()){
            field.setAccessible(true);
            if(field.get(entity) == null) continue;
            params.put(field.getAnnotation(Column.class).name(), field.get(entity));
        }
        return params;
    }

    public RowMapper rowMapper(Class aClass, String[] fields) {
        return (resultSet, i) -> {
            try {
                Object o = aClass.newInstance();
                for (String field : fields) {
                    Field declaredField = o.getClass().getDeclaredField(field);
                    declaredField.setAccessible(true);
                    declaredField.set(field, resultSet.getObject(field, declaredField.getClass()));
                }
                return o;
            } catch (Exception e){
                e.printStackTrace();
                return null;}
        };
    }
}
