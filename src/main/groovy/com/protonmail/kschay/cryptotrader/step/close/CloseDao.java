package com.protonmail.kschay.cryptotrader.step.close;

import com.protonmail.kschay.cryptotrader.domain.close.Close;
import com.protonmail.kschay.cryptotrader.domain.close.CloseRepository;
import com.protonmail.kschay.cryptotrader.domain.symbol.Symbol;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public class CloseDao implements CloseRepository {

    private static final String INSERT_CLOSE =
            "insert into close " +
            "(symbol, price, hour, date) " +
            "values (:symbol, :price, :hour, :date) ";

    public static final String CLOSE_EXISTS =
            "select count(date) from close " +
            "where date(date) = date(:date) " +
            "and symbol like :symbol ";

    private static final String UPDATE_CLOSE =
            "update close set symbol = :symbol, price = :price, hour = :hour, date = :date, updateTime = current_timestamp " +
            "where date(date) = date(:date) " +
            "and symbol like :symbol " +
            "order by insertTime desc " +
            "limit 1 ";

    private static final String GET_CLOSE =
            "select close.id, text symbol, price, hour, date from close " +
            "inner join symbol on close.symbol like symbol.id " +
            "where symbol like :symbol " +
            "order by date desc " +
            "limit 1 ";

    private static final String GET_PRICE =
            "select price " +
            "from close " +
            "where symbol like :symbol " +
            "order by date desc " +
            "limit 1 ";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public CloseDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public boolean insert(Close close) {
        if(closeExists(close.getSymbol(), close.getDate())) {
            return update(close);
        }
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("symbol", close.getSymbol().toString());
        mapSqlParameterSource.addValue("price", close.getPrice());
        mapSqlParameterSource.addValue("hour", close.getHour());
        mapSqlParameterSource.addValue("date", close.getDate());
        int updatedRows = namedParameterJdbcTemplate.update(INSERT_CLOSE, mapSqlParameterSource);
        return updatedRows > 0;
    }

    @Override
    public boolean closeExists(Symbol symbol, Timestamp date) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("symbol", symbol);
        mapSqlParameterSource.addValue("date", date);
        Integer found = namedParameterJdbcTemplate.queryForObject(CLOSE_EXISTS, mapSqlParameterSource, Integer.class);
        return found != null && found > 0;
    }

    @Override
    public boolean update(Close close) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("symbol", close.getSymbol().toString());
        mapSqlParameterSource.addValue("price", close.getPrice());
        mapSqlParameterSource.addValue("hour", close.getHour());
        mapSqlParameterSource.addValue("date", close.getDate());
        int updatedRows = namedParameterJdbcTemplate.update(UPDATE_CLOSE, mapSqlParameterSource);
        return updatedRows > 0;
    }

    @Override
    public Close getClose(Symbol symbol) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("symbol", symbol);
        return namedParameterJdbcTemplate.queryForObject(GET_CLOSE, mapSqlParameterSource, new BeanPropertyRowMapper<>(Close.class));
    }

    @Override
    public Double getClosePrice(Symbol symbol) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("symbol", symbol);
        return namedParameterJdbcTemplate.queryForObject(GET_PRICE, mapSqlParameterSource, Double.class);
    }
}
