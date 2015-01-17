package com.jcwhatever.nucleus.collections.java;

import static org.junit.Assert.assertEquals;

import java.util.List;

/**
 * Test a {@link List} implementation.
 *
 * <p>Also runs {@link ListIteratorTest} and {@link CollectionTest}
 * which runs {@link IterableTest}.</p>
 *
 * <p>Not a JUnit test case but throws errors via JUnit. Intended
 * to be instantiated with a test and the {@link #run} method invoked.</p>
 */
public class ListTest<E> implements Runnable {

    final List<E> _list;
    final E _value1;
    final E _value2;
    final E _value3;

    /**
     * Constructor.
     *
     * @param list    The list to test.
     * @param value1  A value to use for testing.
     * @param value2  A value to use for testing.
     * @param value3  A value to use for testing.
     */
    public ListTest(List<E> list, E value1, E value2, E value3) {
        this._list = list;
        this._value1 = value1;
        this._value2 = value2;
        this._value3 = value3;
    }

    @Override
    public void run() {
        CollectionTest<E> test = new CollectionTest<>(_list, _value1, _value2, _value3);
        test.run();

        _list.clear();

        try {
            _list.add(_value1);
            _list.add(_value2);
            _list.add(_value3);

            // test get
            try {
                assertEquals(_value1, _list.get(0));
                assertEquals(_value2, _list.get(1));
                assertEquals(_value3, _list.get(2));

                try {
                    _list.get(3);
                    throw new AssertionError("IndexOutOfBoundsException expected.");
                } catch (IndexOutOfBoundsException ignore) {
                }

            }catch (UnsupportedOperationException ignore) {}

            // test set
            try {
                assertEquals(_value1, _list.set(0, _value2));
                assertEquals(_value2, _list.set(1, _value3));
                assertEquals(_value3, _list.set(2, _value1));

                try {
                    _list.set(3, _value1);
                    throw new AssertionError("IndexOutOfBoundsException expected.");
                }
                catch (IndexOutOfBoundsException ignore) {}

            }
            catch (UnsupportedOperationException ignore) {}


            // test add
            try {

                _list.clear();
                _list.add(_value1);

                _list.add(0, _value2);

                assertEquals(_value2, _list.get(0));
                assertEquals(_value1, _list.get(1));

                try {
                    _list.add(3, _value1);
                    throw new AssertionError("IndexOutOfBoundsException expected.");
                }
                catch (IndexOutOfBoundsException ignore) {}

            }
            catch (UnsupportedOperationException ignore) {}

            // test remove
            try {
                _list.clear();
                _list.add(_value1);

                assertEquals(_value1, _list.remove(0));

                try {
                    assertEquals(_value1, _list.remove(1));
                    throw new AssertionError("IndexOutOfBoundsException expected.");
                }
                catch (IndexOutOfBoundsException ignore) {}
            }
            catch (UnsupportedOperationException ignore) {}


            try {
                _list.clear();
                _list.add(_value1);
                _list.add(_value2);
                _list.add(_value3);

                assertEquals(0, _list.indexOf(_value1));
                assertEquals(1, _list.indexOf(_value2));
                assertEquals(2, _list.indexOf(_value3));
            }
            catch(UnsupportedOperationException ignore) {
                throw new AssertionError("UnsupportedOperationException NOT expected.");
            }


            try {
                _list.clear();
                _list.add(_value1);
                _list.add(_value1);
                _list.add(_value2);
                _list.add(_value2);
                _list.add(_value3);
                _list.add(_value3);

                assertEquals(1, _list.lastIndexOf(_value1));
                assertEquals(3, _list.lastIndexOf(_value2));
                assertEquals(5, _list.lastIndexOf(_value3));
            }
            catch(UnsupportedOperationException ignore) {
                throw new AssertionError("UnsupportedOperationException NOT expected.");
            }


            _list.clear();
            _list.add(_value1);
            _list.add(_value1);
            _list.add(_value2);
            _list.add(_value2);
            _list.add(_value3);
            _list.add(_value3);

            List<E> subList = _list.subList(1, 4);

            assertEquals(3, subList.size());
            assertEquals(_value1, subList.get(0));
            assertEquals(_value2, subList.get(1));
            assertEquals(_value2, subList.get(2));
            assertEquals(2, subList.lastIndexOf(_value2));

            assertEquals(true, subList.contains(_value1));
            assertEquals(true, subList.contains(_value2));
            assertEquals(false, subList.contains(_value3));

            assertEquals(_value1, subList.remove(0));
            assertEquals(2, subList.size());
            assertEquals(5, _list.size());

            ListIteratorTest<E> listIteratorTest = new ListIteratorTest<E>(
                    _list.listIterator(), _list, _value2, _value3);
            listIteratorTest.run();

        }
        catch (UnsupportedOperationException ignore) {

            System.out.println("Cannot test because elements cannot be added.");
        }
    }

}
