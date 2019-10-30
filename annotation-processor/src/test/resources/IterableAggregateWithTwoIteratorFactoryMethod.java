import com.mayankrastogi.cs474.hw2.annotations.IterableAggregate;
import com.mayankrastogi.cs474.hw2.annotations.Iterator;

@IterableAggregate(ValidIterator.class)
class IterableAggregateWithTwoIteratorFactoryMethod {

    @IterableAggregate.IteratorFactory
    ValidIterator iterator() { return null; }

    @IterableAggregate.IteratorFactory
    ValidIterator iterator2() { return null; }
}

@Iterator(Integer.class)
class ValidIterator {}