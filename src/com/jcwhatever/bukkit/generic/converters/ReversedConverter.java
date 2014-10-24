package com.jcwhatever.bukkit.generic.converters;

/**
 * A wrapper for a {@code ValueConverter} instance that reverses the
 * convert and unconvert methods.
 *
 * @param <F>  The parent {@code ValueConverter}'s unconvert return value type
 * @param <T>  The parent {@code ValueConverter}'s convert return value type
 */
public class ReversedConverter<F, T> extends ValueConverter<F, T> {

    private ValueConverter<T, F> _parentConverter;

    /**
     * Constructor.
     *
     * @param parentConverter  The parent converter to wrap.
     */
    ReversedConverter(ValueConverter<T, F> parentConverter) {
        _parentConverter = parentConverter;
    }

    /**
     * Convert using the parent converters unconvert method.
     */
    @Override
    protected F onConvert(Object value) {
        return callUnconvert(_parentConverter, value);
    }

    /**
     * Unconvert using the parent converters convert method.
     */
    @Override
    protected T onUnconvert(Object value) {
        return callConvert(_parentConverter, value);
    }

    /**
     * Get the wrapped parent value converter.
     */
    public ValueConverter<T, F> getParent() {
        return _parentConverter;
    }

}
