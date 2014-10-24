package com.jcwhatever.bukkit.generic.converters;

/**
 * Implements IValueConverter and extracts values from Wrapper<?>.
 * 
 * @author JC The Pants
 *
 * @param <T>
 * @param <F>
 */
public abstract class ValueConverter<T, F> {

    private ConversionValueContainer _currentContainer;

	public final T convert(Object value) {

        if (value instanceof ConversionValueContainer) {
            _currentContainer = (ConversionValueContainer)value;

            if (_currentContainer.hasConverter(this))
                return null;

            _currentContainer.addConverter(this);
            value = _currentContainer.getValue();
        }
        else {
            _currentContainer = new ConversionValueContainer(this, value);
        }

        T result = onConvert(value);

        _currentContainer = null;

        return result;
	}

	public final F unconvert(Object value) {

        if (value instanceof ConversionValueContainer) {
            _currentContainer = (ConversionValueContainer)value;

            if (_currentContainer.hasConverter(this))
                return null;

            _currentContainer.addConverter(this);
            value = _currentContainer.getValue();
        }
        else {
            _currentContainer = new ConversionValueContainer(this, value);
        }

        F result = onUnconvert(value);

        _currentContainer = null;

        return result;
	}

    public ReversedConverter<F, T> getReverse() {
        return new ReversedConverter<F, T>(this);
    }
	
	protected abstract T onConvert(Object value);
	protected abstract F onUnconvert(Object value);

    protected final <V> V callConvert(ValueConverter<V, ?> externalConverter, Object value) {
        ConversionValueContainer container = new ConversionValueContainer(this, value, _currentContainer);
        return externalConverter.convert(container);
    }

    protected final <V> V callUnconvert(ValueConverter<?, V> externalConverter, Object value) {
        ConversionValueContainer container = new ConversionValueContainer(this, value, _currentContainer);
        return externalConverter.unconvert(container);
    }



}
