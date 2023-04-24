package ru.practicum.shareit.item.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

@Repository
public class ItemDbStorage implements ItemStorage {

    private final JdbcTemplate jdbcTemplate;

    public ItemDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Item> getAllByName(String name) {
        /*SqlRowSet itemsIdRow = jdbcTemplate.queryForRowSet(
                "SELECT item_id FROM items");
        List<Item> allItems = new ArrayList<>();
        while (itemsIdRow.next()) {
            allItems.add(getById(itemsIdRow.getLong("item_id")));
        }
        List<Item> items = new ArrayList<>();
        for (Item item : allItems) {
            if (item.getName().toLowerCase().contains(name.toLowerCase()) || item.getDescription().toLowerCase().contains(name.toLowerCase())) {
                if (item.getAvailable() == true) {
                    items.add(item);
                }
            }
        }*/
        String finalName = "%" + name + "%";
       SqlRowSet itemsIdRow = jdbcTemplate.queryForRowSet(
                "SELECT item_id FROM items WHERE LOWER(item_name) LIKE (LOWER(?)) OR LOWER(description) LIKE (LOWER(?) AND available IS TRUE)", finalName, finalName);
        List<Item> items = new ArrayList<>();
        while (itemsIdRow.next()) {
            items.add(getById(itemsIdRow.getLong("item_id")));
        }
        return items;
    }

    @Override
    public Item create(Item item) {
        Map<String, Object> keys = new SimpleJdbcInsert(this.jdbcTemplate)
                .withTableName("items")
                .usingColumns("item_name", "description", "available", "ownerId")
                .usingGeneratedKeyColumns("item_id")
                .executeAndReturnKeyHolder(Map.of("item_name", item.getName(),
                        "description", item.getDescription(),
                        "available", item.getAvailable(),
                        "ownerId", item.getOwnerId()))
                .getKeys();
        item.setId((Long) keys.get("item_id"));
        return getById((Long) keys.get("item_id"));
    }

    @Override
    public Item update(Long itemId, Item item) {
        if (item.getName() != null && !item.getName().isBlank()) {
            String sqlQuery = "UPDATE items "
                    + "SET item_name = ? "
                    + "WHERE item_id = ?";
            jdbcTemplate.update(sqlQuery, item.getName(), itemId);
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            String sqlQuery = "UPDATE items "
                    + "SET description = ? "
                    + "WHERE item_id = ?";
            jdbcTemplate.update(sqlQuery, item.getDescription(),  itemId);
        }
        if (item.getAvailable() != null) {
            String sqlQuery = "UPDATE items "
                    + "SET available = ? "
                    + "WHERE item_id = ?";
            jdbcTemplate.update(sqlQuery, item.getAvailable(), itemId);
        }
        return getById(itemId);
    }

    @Override
    public Item delete(Long id) {
        Item item = getById(id);
        jdbcTemplate.execute("DELETE FROM items WHERE item_id = " + id);
        return item;
    }

    @Override
    public Item getById(Long id) {
        String sqlQuery = "SELECT * FROM items "
                + "WHERE item_id = ?";
        SqlRowSet srs = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (srs.next()) {
            return itemMap(srs);
        } else {
            throw new NotFoundException("Movie with ID = " + id + " not found");
        }
    }

    @Override
    public List<Item> getAllItemsByUserId(Long userId) {
        SqlRowSet itemsIdRow = jdbcTemplate.queryForRowSet(
                "SELECT item_id FROM items WHERE ownerId=?", userId);
        List<Item> items = new ArrayList<>();
        while (itemsIdRow.next()) {
            items.add(getById(itemsIdRow.getLong("item_id")));
        }
        return items;
    }

    @Override
    public boolean containsItem(Long id) {
        return jdbcTemplate.queryForRowSet("SELECT item_id FROM items WHERE item_id=?", id).next();
    }

    private Item itemMap(SqlRowSet srs) {
        Long id = srs.getLong("item_id");
        String name = srs.getString("item_name");
        String description = srs.getString("description");
        Boolean available = srs.getBoolean("available");
        Long ownerId = srs.getLong("ownerId");
        return Item.builder()
                .id(id)
                .name(name)
                .description(description)
                .available(available)
                .ownerId(ownerId)
                .build();
    }
}
