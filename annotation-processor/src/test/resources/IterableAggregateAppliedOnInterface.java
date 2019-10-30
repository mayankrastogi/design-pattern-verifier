import com.mayankrastogi.cs474.hw2.annotations.IterableAggregate;
import com.mayankrastogi.cs474.hw2.annotations.Iterator;

@IterableAggregate(ValidIterator.class)
interface IterableAggregateAppliedOnInterface {

    @IterableAggregate.IteratorFactory
    ValidIterator iterator();
}

@Iterator(Integer.class)
class ValidIterator {}