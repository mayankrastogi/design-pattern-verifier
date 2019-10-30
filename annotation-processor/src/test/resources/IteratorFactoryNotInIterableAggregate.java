import com.mayankrastogi.cs474.hw2.annotations.IterableAggregate;
import com.mayankrastogi.cs474.hw2.annotations.Iterator;

//@IterableAggregate(ValidIterator.class)
class IteratorFactoryNotInIterableAggregate {

    @IterableAggregate.IteratorFactory
    ValidIterator iterator() { return null; }
}

@Iterator(Integer.class)
class ValidIterator {}