import com.mayankrastogi.cs474.hw2.annotations.Iterator;

@Iterator(Integer.class)
class CurrentItemReturnTypeDifferentFromIteratorAnnotationValue {

    @Iterator.CurrentItem
    Float current() { return 0f; }

    @Iterator.IsDone
    boolean isDone() { return true; }

    @Iterator.NextItem
    Integer next() { return 0; }
}
