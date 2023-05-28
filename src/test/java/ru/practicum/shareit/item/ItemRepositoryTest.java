package ru.practicum.shareit.item;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void findAllByNameTest() {
        for (int i = 0; i < 10; i++) {
            Item item = createItem(i);
            itemRepository.save(item);
        }
        PageRequest page = PageRequest.of(0, 10);
        List<Item> itemsByItem = itemRepository.findAllByName("item", page);
        assertThat(itemsByItem.size()).isEqualTo(10);
        List<Item> itemsByDescription = itemRepository.findAllByName("deSCriPtiON", page);
        assertThat(itemsByDescription.size()).isEqualTo(10);
        List<Item> itemsBy1 = itemRepository.findAllByName("1", page);
        assertThat(itemsBy1.size()).isEqualTo(1);
        List<Item> itemsByNull = itemRepository.findAllByName("null", page);
        assertThat(itemsByNull.size()).isEqualTo(0);
    }

    private Item createItem(int id) {
        Item item = new Item();
        item.setName("Name for item " + id);
        item.setDescription("Description for item " + id);
        item.setOwnerId(1L);
        item.setAvailable(true);
        return item;
    }
}