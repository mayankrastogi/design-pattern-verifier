import com.mayankrastogi.cs474.hw2.annotations.Iterator;

@Iterator(Integer.class)
interface IteratorAppliedOnInterface {

    @Iterator.CurrentItem
    Integer current();

    @Iterator.IsDone
    boolean isDone();

    @Iterator.NextItem
    Integer next();
}
