package utility;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import java.util.Collection;

/**
 * This is a class you can use to quickly remove a mapper from
 *  AbstractDTODomainMapper. Use this when the things your mapping
 *  are not going to or from the JAX-B classes/
 */
public abstract class TwoWayMapper<L,R> {
    protected abstract L mapToDTOImpl(R rightObject, Object... otherMappedObjects);
    protected abstract R mapToDomainImpl(L leftObject, Object... otherMappedObjects);

    public L mapToDTO(R object, Object... otherMappedObjects) {
        if (object == null){
            return null;
        }

        return mapToDTOImpl(object, otherMappedObjects);
    }

    public Collection<L> mapToDTO(Collection<? extends R> collection, final Object... otherMappedObjects) {
        return FluentIterable.from(collection).transform(new Function<R, L>() {
            public L apply(R input) {
                return mapToDTOImpl(input, otherMappedObjects);
            }
        }).toList();
    }

    public R mapToDomain(L object, Object... otherMappedObjects) {
        if(object == null){
            return null;
        }
        return mapToDomainImpl(object, otherMappedObjects);
    }

    public Collection<R> mapToDomain(Collection<? extends L> collection, final Object... otherMappedObjects) {
        return FluentIterable.from(collection).transform(new Function<L, R>() {
            public R apply(L input) {
                return mapToDomain(input, otherMappedObjects);
            }
        }).toList();
    }
}
