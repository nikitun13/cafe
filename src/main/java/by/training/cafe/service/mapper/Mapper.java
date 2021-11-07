package by.training.cafe.service.mapper;

/**
 * The class {@code Mapper} is an interface
 * that Maps {@code DAOs} to {@code DTOs} and vice versa.
 *
 * @param <E> type of the {@code Entity}.
 * @param <T> type of the {@code Dto}.
 * @author Nikita Romanov
 */
public interface Mapper<E, T> {

    /**
     * Maps {@code Dto} to {@code Entity}.
     *
     * @param dto to be mapped to {@code Entity}.
     * @return mapped {@code Entity}.
     */
    E mapDtoToEntity(T dto);

    /**
     * Maps {@code Entity} to {@code Dto}.
     *
     * @param entity to be mapped to {@code Dto}.
     * @return mapped {@code Dto}..
     */
    T mapEntityToDto(E entity);
}
