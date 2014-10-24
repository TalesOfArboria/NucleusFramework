package com.jcwhatever.bukkit.generic.collections;

import com.jcwhatever.bukkit.generic.collections.Definitions.Definition;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import java.util.ArrayList;

/**
 * A list of object definitions.
 *
 * @param <T> The term type.
 * @param <D> The definition type.
 */
public class Definitions<T, D> extends ArrayList<Definition> {

	private static final long serialVersionUID = -3078314350437433749L;

    /**
     * Constructor.
     */
    public Definitions() {
        super();
    }

    /**
     * Constructor.
     *
     * @param size  The initial capacity.
     */
    public Definitions(int size) {
        super(size);
    }

    /**
     * Constructor.
     *
     * @param definitions Another Definitions object to initialize from.
     */
	public Definitions(Definitions<T, D> definitions) {
		super(definitions);
	}

    /**
     * Set a term and definition.
     *
     * @param term        The term to define.
     * @param definition  The definition of the term.
     * @return  Returns self.
     */
	public Definitions<T, D> set(T term, D definition) {
		this.add(new Definition<T, D>(term, definition));
		return this;
	}


    /**
     * Represents a simple definition, where one object is used to define another.
     *
     */
    public static class Definition<T, D> {

        protected final T _term;
        protected final D _definition;

        /**
         * Constructor.
         *
         * @param term        The term to describe.
         * @param definition  The definition of the term.
         */
        public Definition(T term, D definition) {
            PreCon.notNull(term);
            PreCon.notNull(definition);

            _term = term;
            _definition = definition;
        }

        /**
         * Get the term being described.
         */
        public T getTerm() {
            return _term;
        }

        /**
         * Get the definition of the term.
         * @return
         */
        public D getDefinition() {
            return _definition;
        }

        @Override
        public int hashCode() {
            return _term.hashCode();
        }

        @Override
        public boolean equals(Object o) {

            return o instanceof Definition &&
                    ((Definition) o)._term.equals(_term) &&
                    ((Definition) o)._definition.equals(_definition);
        }
    }
	
}
