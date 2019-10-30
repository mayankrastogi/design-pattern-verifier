import com.mayankrastogi.cs474.hw2.annotations.Iterator;

@Iterator(Integer.class)
class NextItemWithParameters {

    @Iterator.CurrentItem
    Integer current() { return 0; }

    @Iterator.IsDone
    boolean isDone() { return true; }

    @Iterator.NextItem
    Integer next(int param) { return 0; }
}
