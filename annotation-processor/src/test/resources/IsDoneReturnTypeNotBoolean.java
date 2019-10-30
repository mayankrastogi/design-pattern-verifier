import com.mayankrastogi.cs474.hw2.annotations.Iterator;

@Iterator(Integer.class)
class IsDoneReturnTypeNotBoolean {

    @Iterator.CurrentItem
    Integer current() { return 0; }

    @Iterator.IsDone
    int isDone() { return 0; }

    @Iterator.NextItem
    Integer next() { return 0; }
}
