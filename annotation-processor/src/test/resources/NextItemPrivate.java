import com.mayankrastogi.cs474.hw2.annotations.Iterator;

@Iterator(Integer.class)
class NextItemPrivate {

    @Iterator.CurrentItem
    Integer current() { return 0; }

    @Iterator.IsDone
    boolean isDone() { return true; }

    @Iterator.NextItem
    private Integer next() { return 0; }
}
