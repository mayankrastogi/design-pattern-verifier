import com.mayankrastogi.cs474.hw2.annotations.Iterator;

@Iterator(Integer.class)
class IteratorWithTwoCurrentItemMethod {

    @Iterator.CurrentItem
    Integer current() { return 0; }

    @Iterator.CurrentItem
    Integer current2() { return 0; }

    @Iterator.IsDone
    boolean isDone() { return true; }

    @Iterator.NextItem
    Integer next() { return 0; }
}
