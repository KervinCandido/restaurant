package br.com.fiap.restaurant.restaurant.infra.persistence.adapter;

import br.com.fiap.restaurant.restaurant.core.domain.MenuItem;
import br.com.fiap.restaurant.restaurant.core.domain.pagination.Page;
import br.com.fiap.restaurant.restaurant.core.domain.pagination.PagedQuery;
import br.com.fiap.restaurant.restaurant.core.exception.BusinessException;
import br.com.fiap.restaurant.restaurant.core.gateway.MenuItemGateway;
import br.com.fiap.restaurant.restaurant.infra.persistence.entity.MenuItemEntity;
import br.com.fiap.restaurant.restaurant.infra.persistence.entity.RestaurantEntity;
import br.com.fiap.restaurant.restaurant.infra.persistence.mapper.MenuItemMapper;
import br.com.fiap.restaurant.restaurant.infra.persistence.repository.MenuItemRepository;
import br.com.fiap.restaurant.restaurant.infra.persistence.repository.RestaurantRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class MenuItemGatewayAdapter implements MenuItemGateway {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    public MenuItemGatewayAdapter(MenuItemRepository menuItemRepository, RestaurantRepository restaurantRepository) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public MenuItem save(MenuItem menuItem, Long restaurantId) {
        RestaurantEntity restaurantEntity = restaurantRepository
                .findById(restaurantId)
                .orElseThrow(() -> new BusinessException("Restaurante não encontrado: " + restaurantId));

        MenuItemEntity entity = MenuItemMapper.toEntity(menuItem, restaurantEntity);
        entity = menuItemRepository.save(entity);
        return MenuItemMapper.toDomain(entity);
    }

    @Override
    public Optional<MenuItem> findById(Long id) {
        return menuItemRepository.findById(id)
                .map(MenuItemMapper::toDomain);
    }

    @Override
    public List<MenuItem> findByRestaurantId(Long restaurantId) {
        return menuItemRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(MenuItemMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        menuItemRepository.deleteById(id);
    }

    @Override
    public boolean existsByNameAndRestaurantId(String name, Long restaurantId) {
        return menuItemRepository.existsByNameAndRestaurantId(name.trim(), restaurantId);
    }

    @Override
    public Optional<Long> findRestaurantIdByItemId(Long itemId) {
        return menuItemRepository.findById(itemId)
                .map(entity -> entity.getRestaurant().getId());
    }

    @Override
    public Page<MenuItem> findByRestaurant(PagedQuery<Long> query) {
        var pageable = PageRequest.of(query.pageNumber(), query.pageSize());
        var pagedResult = menuItemRepository.findByRestaurantId(query.filter(), pageable);

        return new Page<> (
                pagedResult.getNumber(),
                pagedResult.getSize(),
                pagedResult.getTotalElements(),
                pagedResult.getContent().stream().map(MenuItemMapper::toDomain).toList()
        );
    }
}
